package cn.elevendev.io.strategy;

import android.app.Activity;
import cn.elevendev.io.IOUtils;
import java.util.List;

public interface Strategy {
    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容的字符串表示
     */
    String readFile(String filePath);

    /**
     * 读取文件内容，返回字节数组
     *
     * @param filePath 文件路径
     * @return
     */
    byte[] readFileAsBytes(String filePath);

    /**
     * 写入字符内容到文件
     *
     * @param filePath 文件路径
     * @param content 要写入的内容
     * @return 
     */
    boolean writeFile(String filePath, String content);

    /**
     * 写入字节数组到文件
     *
     * @param filePath 文件路径
     * @param data 要写入的字节数组
     * @return 
     */
    boolean writeFile(String filePath, byte[] data);

    /**
     * 删除文件或文件夹
     *
     * @param filePath 文件或文件夹的路径
     * @return 
     */
    boolean delete(String filePath);

    /**
     * 判断文件或文件夹是否存在
     *
     * @param filePath 文件或文件夹的路径
     * @return 
     */
    boolean exists(String filePath);

    /**
     * 复制文件或文件夹
     *
     * @param sourcePath 源文件或文件夹的路径
     * @param destPath 目标文件或文件夹的路径
     * @return 
     */
    boolean copy(String sourcePath, String destPath);

    /**
     * 移动文件或文件夹
     *
     * @param sourcePath 源文件或文件夹的路径
     * @param destPath 目标文件或文件夹的路径
     * @return 
     */
    boolean move(String sourcePath, String destPath);

    /**
     * 获取目录下的所有内容（文件和文件夹）
     *
     * @param dirPath 目录的路径
     * @return 目录下所有内容的名称列表
     */
    List<String> getList(String dirPath);

    /**
     * 获取目录下的文件或文件夹列表
     *
     * @param dirPath 目录的路径
     * @param listFiles 如果为 true 列出文件夹，如果为 false 列出文件
     * @return 目录下的文件或文件夹名称列表
     */
    List<String> getList(String dirPath, boolean listDirectorys);

    /**
     * 创建目录
     *
     * @param dirPath 目录的路径
     * @return
     */
    boolean createDirectory(String dirPath);

    /**
     * 检查是否拥有文件或文件夹的权限
     *
     * @param activity
     * @return 
     */
    boolean isStoragePermissionGranted(Activity activity);

    /**
     * 检查是否拥有文件或文件夹的权限
     *
     * @param activity
     * @param dirPath 
     * @return 
     */
    boolean isStoragePermissionGranted(Activity activity, String dirPath);
    
    /**
     * 获取文件或文件夹的权限
     *
     * @param activity
     */
    void requestStoragePermission(Activity activity);
    
    /**
     * 获取文件或文件夹的权限
     *
     * @param activity
     * @param dirPath 
     */
    void requestStoragePermission(Activity activity, String dirPath);
}
