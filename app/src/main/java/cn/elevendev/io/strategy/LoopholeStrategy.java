package cn.elevendev.io.strategy;

import android.app.Activity;
import java.util.List;

public class LoopholeStrategy implements Strategy {

    private Strategy strategy;
    
    public LoopholeStrategy() {
        strategy = new FileStrategy();
    }
    
    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容的字符串表示
     */
    @Override
    public String readFile(String filePath) {
        return strategy.readFile(insertZeroWidth(filePath));
    }

    /**
     * 读取文件内容，返回字节数组
     *
     * @param filePath 文件路径
     * @return 字节数组
     */
    @Override
    public byte[] readFileAsBytes(String filePath) {
        return strategy.readFileAsBytes(insertZeroWidth(filePath));
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
        return strategy.writeFile(insertZeroWidth(filePath), content);
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
        return strategy.writeFile(insertZeroWidth(filePath), data);
    }

    /**
     * 删除文件或文件夹
     *
     * @param filePath 文件或文件夹的路径
     * @return 是否删除成功
     */
    @Override
    public boolean delete(String filePath) {
        return strategy.delete(insertZeroWidth(filePath));
    }

    /**
     * 判断文件或文件夹是否存在
     *
     * @param filePath 文件或文件夹的路径
     * @return 是否存在
     */
    @Override
    public boolean exists(String filePath) {
        return strategy.exists(insertZeroWidth(filePath));
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
        return strategy.copy(insertZeroWidth(sourcePath), insertZeroWidth(destPath));
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
        return strategy.move(insertZeroWidth(sourcePath), insertZeroWidth(destPath));
    }

    /**
     * 获取目录下的所有内容（文件和文件夹）
     *
     * @param dirPath 目录的路径
     * @return 目录下所有内容的名称列表
     */
    @Override
    public List<String> getList(String dirPath) {
        return strategy.getList(insertZeroWidth(dirPath));
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
        return strategy.getList(insertZeroWidth(dirPath), listDirectories);
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录的路径
     * @return 是否创建成功
     */
    @Override
    public boolean createDirectory(String dirPath) {
        return strategy.createDirectory(insertZeroWidth(dirPath));
    }

    /**
     * 检查是否拥有文件或文件夹的权限
     *
     * @param activity 当前Activity
     * @return 是否拥有权限
     */
    @Override
    public boolean isStoragePermissionGranted(Activity activity) {
        return strategy.isStoragePermissionGranted(activity);
    }
    
    /**
     * 检查是否拥有文件或文件夹的权限
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
     * 获取文件或文件夹的权限
     *
     * @param activity 当前Activity
     */
    @Override
    public void requestStoragePermission(Activity activity) {
        strategy.requestStoragePermission(activity);
    }
    
    /**
     * 获取文件或文件夹的权限
     *
     * @param activity 当前Activity
     * @param dirPath 目录路径
     */
    @Override
    public void requestStoragePermission(Activity activity, String dirPath) {
        requestStoragePermission(activity);
    }
    
    /**
     * 插入零宽空格字符（\u200B）
     * 
     * @param path 原始路径
     * @return 处理后的路径
     */
    private String insertZeroWidth(String path) {
        if (path.contains("/Android/data/")) {
            int index = path.indexOf("/Android/data/");
            if (index != -1) {
                return path.substring(0, index) + '\u200B' + path.substring(index);
            }
        }

        if (path.contains("/Android/obb/")) {
            int index = path.indexOf("/Android/obb/");
            if (index != -1) {
                return path.substring(0, index) + '\u200B' + path.substring(index);
            }
        }

        return path;
    }
}
