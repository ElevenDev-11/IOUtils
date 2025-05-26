package cn.elevendev.io.strategy;

import android.app.Activity;
import android.content.pm.PackageManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import rikka.shizuku.Shizuku;

import cn.elevendev.io.utils.PermissionUtil;

public class ShizukuStrategy implements Strategy {

    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容的字符串表示
     */
    @Override
    public String readFile(String filePath) {
        return executeCommandOutput("cat " + filePath);
    }

    /**
     * 读取文件内容，返回字节数组
     *
     * @param filePath 文件路径
     * @return 字节数组
     */
    @Override
    public byte[] readFileAsBytes(String filePath) {
        String output = executeCommandOutput("cat " + filePath);
        return output != null ? output.getBytes() : new byte[0];
    }

    /**
     * 写入字符内容到文件
     *
     * @param filePath 文件路径
     * @param content 要写入的内容
     * @return 是否写入成功
     */
    @Override
    public boolean writeFile(String filePath, String content) {
        String dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
        return executeCommand("mkdir -p " + dirPath + " && echo '" + content + "' > " + filePath);
    }

    /**
     * 写入字节数组到文件
     *
     * @param filePath 文件路径
     * @param data 要写入的字节数组
     * @return 是否写入成功
     */
    @Override
    public boolean writeFile(String filePath, byte[] data) {
        String base64Content = Base64.getEncoder().encodeToString(data);
        String dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
        String command = "mkdir -p " + dirPath + " && echo '" + base64Content + "' | base64 -d > " + filePath;

        return executeCommand(command);
    }

    /**
     * 删除文件或文件夹
     *
     * @param filePath 文件或文件夹的路径
     * @return 是否删除成功
     */
    @Override
    public boolean delete(String filePath) {
        return executeCommand("rm -r " + filePath);
    }

    /**
     * 判断文件或文件夹是否存在
     *
     * @param filePath 文件或文件夹的路径
     * @return 是否存在
     */
    @Override
    public boolean exists(String filePath) {
        String exist = executeCommandOutput("[ -e " + filePath + " ] && echo \"exists\" || echo \"not_exists\"");
        return !exist.contains("not_exists");
    }

    /**
     * 复制文件或文件夹
     *
     * @param sourcePath 源文件或文件夹的路径
     * @param destPath 目标文件或文件夹的路径
     * @return 是否复制成功
     */
    @Override
    public boolean copy(String sourcePath, String destPath) {
        if (exists(destPath)) {
            delete(destPath);
        }
        String dirPath = destPath.substring(0, destPath.lastIndexOf('/'));
        String command = "mkdir -p " + dirPath + " && cp -rT " + sourcePath + " " + destPath;

        return executeCommand(command);
    }

    /**
     * 移动文件或文件夹
     *
     * @param sourcePath 源文件或文件夹的路径
     * @param destPath 目标文件或文件夹的路径
     * @return 是否移动成功
     */
    @Override
    public boolean move(String sourcePath, String destPath) {
        if (exists(destPath)) {
            delete(destPath);
        }
        
        createDirectory(destPath.substring(0, destPath.lastIndexOf('/')));
        String command = "mv " + sourcePath + " " + destPath;

        return executeCommand(command);
    }

    /**
     * 获取目录下的所有内容（文件和文件夹）
     *
     * @param dirPath 目录的路径
     * @return 目录下所有内容的名称列表
     */
    @Override
    public List<String> getList(String dirPath) {
        String data = executeCommandOutput("ls -l " + dirPath);
        String[] lines = data.split("\n");
        List<String> list = new ArrayList<>();
        
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(" ");
            
            if (parts.length >= 5) {
                String name = parts[parts.length - 1];
                String fullPath = dirPath.replaceAll("/+$", "") + "/" + name;
                list.add(fullPath);
            }
        }
        
        return list;
    }

    /**
     * 获取目录下的文件或文件夹列表
     *
     * @param dirPath 目录的路径
     * @param listDirectories 如果为 true 列出文件夹，如果为 false 列出文件
     * @return 目录下的文件或文件夹名称列表
     */
    @Override
    public List<String> getList(String dirPath, boolean listDirectories) {
        String data = executeCommandOutput("ls -l " + dirPath);
        String[] lines = data.split("\n");
        List<String> list = new ArrayList<>();
        
        for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(" ");
            
            if (parts.length >= 5) {
                String name = parts[parts.length - 1];
                String fullPath = dirPath.replaceAll("/+$", "") + "/" + name;
                boolean type = parts[0].startsWith("d") ? true : false;
                if (listDirectories) {
                    if (type) {
                        list.add(fullPath);
                    }
                } else {
                    if (!type) {
                        list.add(fullPath);
                    }
                }
            }
        }
        
        return list;
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录的路径
     * @return 是否创建成功
     */
    @Override
    public boolean createDirectory(String dirPath) {
        return executeCommand("mkdir -p " + dirPath);
    }

    /**
     * 检查是否拥有存储权限
     *
     * @param activity 当前Activity
     * @return 是否拥有权限
     */
    @Override
    public boolean isStoragePermissionGranted(Activity activity) {
        if (!PermissionUtil.isStoragePermissionGranted(activity) || !Shizuku.pingBinder()) {
            return false;
        }
        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查是否拥有存储权限
     *
     * @param activity 当前Activity
     * @param dirPath 目录路径
     * @return 是否拥有权限
     */
    @Override
    public boolean isStoragePermissionGranted(Activity activity, String dirPath) {
        return isStoragePermissionGranted(activity);
    }

    /**
     * 获取存储权限
     *
     * @param activity 当前Activity
     */
    @Override
    public void requestStoragePermission(Activity activity) {
        if (!PermissionUtil.isStoragePermissionGranted(activity)) {
            PermissionUtil.requestStoragePermission(activity);
            return;
        }
        if (Shizuku.pingBinder()) {
            Shizuku.requestPermission(0);
        }
    }

    /**
     * 获取存储权限
     *
     * @param activity 当前Activity
     * @param dirPath 目录路径
     */
    @Override
    public void requestStoragePermission(Activity activity, String dirPath) {
        requestStoragePermission(activity);
    }
    
    /**
     * 执行命令
     *
     * @param command
     * @return
     */
    private String executeCommandOutput(String command) {
        try {
            Process process = Shizuku.newProcess(new String[]{"sh", "-c", command}, null, null);
            OutputStream os = process.getOutputStream();
            os.write((command + "\nexit\n").getBytes());
            os.flush();

            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            os.close();

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 执行命令
     * @param command
     * @return
     */
    private boolean executeCommand(String command) {
        try {
            Process process = Shizuku.newProcess(new String[]{"sh"}, null, null);
            OutputStream os = process.getOutputStream();
            os.write((command + "\nexit\n").getBytes());
            os.flush();
            os.close();

            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
