package cn.elevendev.io.strategy;

import android.app.Activity;
import cn.elevendev.io.IOUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.elevendev.io.utils.PermissionUtil;

public class FileStrategy implements Strategy {

    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容的字符串表示
     */
    @Override
    public String readFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取文件内容，返回字节数组
     *
     * @param filePath 文件路径
     * @return 字节数组
     */
    @Override
    public byte[] readFileAsBytes(String filePath) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
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
        File parentDir = new File(filePath).getParentFile();
        
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                return false;
            }
        }
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
        File parentDir = new File(filePath).getParentFile();
        
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                return false;
            }
        }
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文件或文件夹
     *
     * @param filePath 文件或文件夹的路径
     * @return 是否删除成功
     */
    @Override
    public boolean delete(String filePath) {
        File file = new File(filePath);
        if (file.isFile()) {
            return file.delete();
        }
        try {
            deleteDirectory(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断文件或文件夹是否存在
     *
     * @param filePath 文件或文件夹的路径
     * @return 是否存在
     */
    @Override
    public boolean exists(String filePath) {
        return new File(filePath).exists();
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
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        try {
            if (sourceFile.isFile()) {
                copyOrMoveFile(sourceFile, destFile, false);
            } else {
                copyOrMoveDirectory(sourceFile, destFile, false);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        try {
            if (sourceFile.isFile()) {
                copyOrMoveFile(sourceFile, destFile, true);
            } else {
                copyOrMoveDirectory(sourceFile, destFile, true);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取目录下的所有内容（文件和文件夹）
     *
     * @param dirPath 目录的路径
     * @return 目录下所有内容的名称列表
     */
    @Override
    public List<String> getList(String dirPath) {
        List<String> list = new ArrayList<>();
        File file = new File(dirPath);
        if (file.exists() && file.isDirectory()) {
            for (File name : file.listFiles()) {
                list.add(name.getAbsolutePath());
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
        List<String> list = new ArrayList<>();
        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (listDirectories) {
                    if (file.isDirectory()) {
                        list.add(file.getAbsolutePath());
                    }
                } else {
                    if (file.isFile()) {
                        list.add(file.getAbsolutePath());
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
        if (!exists(dirPath)) {
            return new File(dirPath).mkdirs();
        }
        return true;
    }

    /**
     * 检查是否拥有存储权限
     *
     * @param activity 当前Activity
     * @return 是否拥有权限
     */
    @Override
    public boolean isStoragePermissionGranted(Activity activity) {
        return PermissionUtil.isStoragePermissionGranted(activity);
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
        PermissionUtil.requestStoragePermission(activity);
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
     * 删除文件夹
     *
     * @param dir
     * @throws IOException
     */
    private void deleteDirectory(File dir) throws IOException {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        
        if (!dir.delete()) {
            throw new IOException("无法删除文件或目录: " + dir.getAbsolutePath());
        }
    }
    
    /**
     * 复制或移动文件夹
     *
     * @param sourceDir
     * @param targetDir
     * @param move
     * @throws IOException
     */
    private void copyOrMoveDirectory(File sourceDir, File targetDir, boolean move) throws IOException {
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new IOException("无法创建目标目录: " + targetDir.getAbsolutePath());
        }

        File[] files = sourceDir.listFiles();
        if (files == null) {
            throw new IOException("无法读取源目录内容: " + sourceDir.getAbsolutePath());
        }

        for (File sourceFile : files) {
            File targetFile = new File(targetDir, sourceFile.getName());

            if (sourceFile.isDirectory()) {
                copyOrMoveDirectory(sourceFile, targetFile, move);
            } else {
                copyOrMoveFile(sourceFile, targetFile, move);
            }
        }

        if (move && !sourceDir.delete()) {
            throw new IOException("无法删除源目录: " + sourceDir.getAbsolutePath());
        }
    }
    
    /**
     * 复制或移动文件
     *
     * @param sourceDir
     * @param targetDir
     * @param move
     * @throws IOException
     */
    private void copyOrMoveFile(File sourceFile, File targetFile, boolean move) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(sourceFile));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(targetFile))) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        if (move && !sourceFile.delete()) {
            throw new IOException("无法删除源文件: " + sourceFile.getAbsolutePath());
        }
    }

}
