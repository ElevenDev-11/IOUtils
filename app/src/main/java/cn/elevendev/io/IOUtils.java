package cn.elevendev.io;

import static cn.elevendev.io.utils.PermissionUtil.REQUEST_WRITE_STORAGE;
import static cn.elevendev.io.utils.PermissionUtil.REQUEST_CODE_ALL_FILES_PERMISSION;
import static cn.elevendev.io.strategy.DocumentStrategy.REQUEST_CODE_DOCUMENT;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import cn.elevendev.io.strategy.Strategy;
import cn.elevendev.io.strategy.StrategyFactory;
import cn.elevendev.io.strategy.StrategyType;
import cn.elevendev.io.utils.PermissionUtil;
import java.util.List;

public class IOUtils implements Strategy {
    
    private Strategy strategy;
    private static PermissionCallback permissionCallback;
    
    public IOUtils(Activity activity, StrategyType strategyType) {
        this.strategy = StrategyFactory.createStrategy(activity, strategyType);
    }
    
    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容的字符串表示
     */
    @Override
    public String readFile(String filePath) {
        return strategy.readFile(filePath);
    }

    /**
     * 读取文件内容，返回字节数组
     *
     * @param filePath 文件路径
     * @return 字节数组
     */
    @Override
    public byte[] readFileAsBytes(String filePath) {
        return strategy.readFileAsBytes(filePath);
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
        return strategy.writeFile(filePath, content);
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
        return strategy.writeFile(filePath, data);
    }

    /**
     * 删除文件或文件夹
     *
     * @param filePath 文件或文件夹的路径
     * @return 是否删除成功
     */
    @Override
    public boolean delete(String filePath) {
        return strategy.delete(filePath);
    }

    /**
     * 判断文件或文件夹是否存在
     *
     * @param filePath 文件或文件夹的路径
     * @return 是否存在
     */
    @Override
    public boolean exists(String filePath) {
        return strategy.exists(filePath);
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
        return strategy.copy(sourcePath, destPath);
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
        return strategy.move(sourcePath, destPath);
    }

    /**
     * 获取目录下的所有内容（文件和文件夹）
     *
     * @param dirPath 目录的路径
     * @return 目录下所有内容的名称列表
     */
    @Override
    public List<String> getList(String dirPath) {
        return strategy.getList(dirPath);
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
        return strategy.getList(dirPath, listDirectories);
    }

    /**
     * 创建目录
     *
     * @param dirPath 目录的路径
     * @return 是否创建成功
     */
    @Override
    public boolean createDirectory(String dirPath) {
        return strategy.createDirectory(dirPath);
    }

    /**
     * 检查是否拥有存储权限
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
        return strategy.isStoragePermissionGranted(activity, dirPath);
    }

    /**
     * 获取存储权限
     *
     * @param activity 当前Activity
     */
    @Override
    public void requestStoragePermission(Activity activity) {
        strategy.requestStoragePermission(activity);
    }
    
    /**
     * 获取存储权限
     *
     * @param activity 当前Activity
     * @param callback 权限回调
     */
    public void requestStoragePermission(Activity activity, PermissionCallback callback) {
        IOUtils.permissionCallback = callback;
        this.requestStoragePermission(activity);
    }

    /**
     * 获取文件或文件夹的权限
     *
     * @param activity 当前Activity
     * @param dirPath 目录路径
     */
    @Override
    public void requestStoragePermission(Activity activity, String dirPath) {
        strategy.requestStoragePermission(activity, dirPath);
    }
    
    /**
     * 获取文件或文件夹的权限
     *
     * @param activity 当前Activity
     * @param dirPath 目录路径
     * @param callback 权限回调
     */
    public void requestStoragePermission(Activity activity, String dirPath, PermissionCallback callback) {
        IOUtils.permissionCallback = callback;
        this.requestStoragePermission(activity, dirPath);
    }
    
    /**
     * 处理权限结果回调
     *
     * @param requestCode  请求码
     * @param grantResults 权限结果
     */
    public static void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissionCallback != null) permissionCallback.onPermissionGranted();
            } else {
                if (permissionCallback != null) permissionCallback.onPermissionDenied();
            }
            permissionCallback = null;
        }
    }
    
    /**
     * 权限回调结果
     *
     * @param activity    当前 Activity
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        回传的 Intent 数据，处理文档权限时获取 Uri
     */
    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ALL_FILES_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    if (permissionCallback != null) permissionCallback.onPermissionGranted();
                } else {
                    if (permissionCallback != null) permissionCallback.onPermissionDenied();
                }
                permissionCallback = null;
            }
        }
        
        if(requestCode == REQUEST_CODE_DOCUMENT && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if(uri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    activity.getContentResolver().takePersistableUriPermission(uri, data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));
                }
            }
        }
    }

    public interface PermissionCallback {
        /** 权限已授予 */
        void onPermissionGranted();

        /** 权限被拒绝 */
        void onPermissionDenied();
    }
}
