package cn.elevendev.io.strategy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import androidx.documentfile.provider.DocumentFile;
import cn.elevendev.io.IOUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.elevendev.io.utils.PermissionUtil;

public class DocumentStrategy implements Strategy {
    public static final int REQUEST_CODE_DOCUMENT = 10013;
    private static final int DIR_AD_LENGTH;
    private static final boolean IS_SDK_32, IS_SDK_34;
    private static final String DIR;
    private static final String ANDROID_DATA;
    private static Strategy strategy;
    private Activity activity;

    static {
        strategy = new FileStrategy();
        IS_SDK_32 = Build.VERSION.SDK_INT >= 32;
        IS_SDK_34 = Build.VERSION.SDK_INT >= 34;
        DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
        DIR_AD_LENGTH = (DIR.toLowerCase() + "/android/data").length();
        
        if (IS_SDK_34) {
            ANDROID_DATA = "content://com.android.externalstorage.documents/tree/primary%3AAndr%E2%80%8Boid%2Fda%E2%80%8Bta";
        } else {
            ANDROID_DATA = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata";
        }
    }
    
    public DocumentStrategy(Activity activity) {
        this.activity = activity;
    }
    
    
    /**
     * 读取文件内容
     *
     * @param filePath 文件路径
     * @return 文件内容的字符串表示
     */
    @Override
    public String readFile(String filePath) {
        if (isType(filePath)) {
            return strategy.readFile(filePath);
        }
        if (PermissionUtil.isStoragePermissionGranted(activity) && !isDataPermission(filePath)) {
            getDataPermission(activity, filePath);
            return null;
        }
        
        DocumentFile df = getFile(filePath, false);
        if (df != null) {
            try (InputStream is = activity.getContentResolver().openInputStream(df.getUri());
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if (isType(filePath)) {
            return strategy.readFileAsBytes(filePath);
        }
        if (PermissionUtil.isStoragePermissionGranted(activity) && !isDataPermission(filePath)) {
            getDataPermission(activity, filePath);
            return new byte[0];
        }

        DocumentFile df = getFile(filePath, false);
        if (df != null) {
            try (InputStream is = activity.getContentResolver().openInputStream(df.getUri());
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                int length;

                while ((length = is.read(buffer)) != -1) {
                    byteStream.write(buffer, 0, length);
                }

                return byteStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if (isType(filePath)) {
            return strategy.writeFile(filePath, content);
        }
        if (PermissionUtil.isStoragePermissionGranted(activity) && !isDataPermission(filePath)) {
            getDataPermission(activity, filePath);
            return false;
        }
        
        DocumentFile df = getFile(filePath, true);
        if (df != null) {
            try (OutputStream os = activity.getContentResolver().openOutputStream(df.getUri(), "wt");
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
                if (writer == null) {
                    return false;
                }
                writer.write(content);
                writer.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if (isType(filePath)) {
            return strategy.writeFile(filePath, data);
        }
        if (PermissionUtil.isStoragePermissionGranted(activity) && !isDataPermission(filePath)) {
            getDataPermission(activity, filePath);
            return false;
        }

        DocumentFile df = getFile(filePath, true);
        if (df != null) {
            try (OutputStream os = activity.getContentResolver().openOutputStream(df.getUri())) {
                if (os == null) {
                    return false;
                }

                os.write(data);
                os.flush();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if (isType(filePath)) {
            return strategy.delete(filePath);
        }
        if (PermissionUtil.isStoragePermissionGranted(activity) && !isDataPermission(filePath)) {
            getDataPermission(activity, filePath);
            return false;
        }
        
        DocumentFile df = getFile(filePath, false);
        if (df != null) {
            if (df.isFile()) {
                return df.delete();
            }
            return deleteDir(filePath);
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
        if (isType(filePath)) {
          return strategy.exists(filePath); 
        }
        if (PermissionUtil.isStoragePermissionGranted(activity) && !isDataPermission(filePath)) {
            getDataPermission(activity, filePath);
            return false;
        }
        
        DocumentFile df = getFile(filePath, false);
        if (df != null) {
            return df.exists();
        }
        return false;
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
        if (isType(sourcePath) && isType(destPath)) {
            return strategy.copy(sourcePath, destPath);
        }
        if (PermissionUtil.isStoragePermissionGranted(activity)) {
            if (!isDataPermission(sourcePath)) {
                getDataPermission(activity, sourcePath);
                return false;
            }
            if (!isDataPermission(destPath)) {
                getDataPermission(activity, destPath);
                return false;
            }
        }
        if (isType(sourcePath)) {
            File file = new File(sourcePath);
            if (file.isFile()) {
                return copyOrMoveFile(sourcePath, destPath, false);
            }
            return copyOrMoveDir(sourcePath, destPath, false);
        }
        
        DocumentFile df = getFile(sourcePath, false);
        if (df == null) {
            return false;
        }
        if (df.isFile()) {
            return copyOrMoveFile(sourcePath, destPath, false);
        }
        return copyOrMoveDir(sourcePath, destPath, false);
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
        if (isType(sourcePath) && isType(destPath)) {
            return strategy.copy(sourcePath, destPath);
        }
        if (PermissionUtil.isStoragePermissionGranted(activity)) {
            if (!isDataPermission(sourcePath)) {
                getDataPermission(activity, sourcePath);
                return false;
            }
            if (!isDataPermission(destPath)) {
                getDataPermission(activity, destPath);
                return false;
            }
        }
        if (isType(sourcePath)) {
            File file = new File(sourcePath);
            if (file.isFile()) {
                return copyOrMoveFile(sourcePath, destPath, true);
            }
            return copyOrMoveDir(sourcePath, destPath, true);
        }
        
        DocumentFile df = getFile(sourcePath, false);
        if (df == null) {
            return false;
        }
        if (df.isFile()) {
            return copyOrMoveFile(sourcePath, destPath, true);
        }
        return copyOrMoveDir(sourcePath, destPath, true);
    }

    /**
     * 获取目录下的所有内容（文件和文件夹）
     *
     * @param dirPath 目录的路径
     * @return 目录下所有内容的名称列表
     */
    @Override
    public List<String> getList(String dirPath) {
        if (isType(dirPath)) {
            if (strategy.exists(dirPath)) {
                return strategy.getList(dirPath);
            }
        }
        if (PermissionUtil.isStoragePermissionGranted(activity) && !isDataPermission(dirPath)) {
            getDataPermission(activity, dirPath);
            return new ArrayList<>();
        }
        
        List<String> list = new ArrayList<>();
        DocumentFile df = getFile(dirPath, false);
        if (dirPath.endsWith("/")) {
            dirPath = dirPath.substring(0, dirPath.length() - 1);
        }
        if (df != null && df.exists() && df.isDirectory()) {
            DocumentFile[] files = df.listFiles();
            if (files != null) {
                for (DocumentFile file : files) {
                    list.add(String.format("%s/%s", dirPath, file.getName()));
                }
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
        if (isType(dirPath)) {
            if (strategy.exists(dirPath)) {
                return strategy.getList(dirPath);
            }
        }
        if (PermissionUtil.isStoragePermissionGranted(activity) && !isDataPermission(dirPath)) {
            getDataPermission(activity, dirPath);
            return new ArrayList<>();
        }
        
        List<String> list = new ArrayList<>();
        DocumentFile df = getFile(dirPath, false);
        if (dirPath.endsWith("/")) {
            dirPath = dirPath.substring(0, dirPath.length() - 1);
        }
        if (df != null && df.exists() && df.isDirectory()) {
            DocumentFile[] files = df.listFiles();
            if (files != null) {
                for (DocumentFile file : files) {
                    if (listDirectories) {
                        if (file.isDirectory()) {
                            list.add(String.format("%s/%s", dirPath, file.getName()));
                        }
                    } else {
                        if (file.isFile()) {
                            list.add(String.format("%s/%s", dirPath, file.getName()));
                        }
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
        if (isType(dirPath)) {
            return strategy.createDirectory(dirPath);
        }
        if (PermissionUtil.isStoragePermissionGranted(activity) && !isDataPermission(dirPath)) {
            getDataPermission(activity, dirPath);
            return false;
        }
        
        DocumentFile df = getFile(dirPath, false);
        if (df == null) {
            if (dirPath.endsWith("/")) {
                dirPath = dirPath.substring(0, dirPath.length() -1);
            }
            dirPath += "/cache.so";
            return getFile(dirPath, true) != null ? delete(dirPath) : false;
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
        if (!PermissionUtil.isStoragePermissionGranted(activity)) {
            return false;
        }
        if (dirPath == null || dirPath.isEmpty()) {
            return true;
        } else {
            return isDataPermission(dirPath);
        }
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
        }
    }
    
    /**
     * 获取文件或文件夹的权限
     *
     * @param activity 当前Activity
     * @param dirPath 目录路径
     */
    @Override
    public void requestStoragePermission(Activity activity, String dirPath) {
        if (!PermissionUtil.isStoragePermissionGranted(activity)) {
            PermissionUtil.requestStoragePermission(activity);
            return;
        }
        
        getDataPermission(activity, dirPath);
    }
    
    /**
     * 判断是否拥有某个文件夹权限
     *
     * @param path 目标路径
     * @return
     */
    private boolean isDataPermission(String path) {
        if (!IS_SDK_32) {
            return getPathDocumentFile(activity, ANDROID_DATA) != null;
        }
        if (path.length() > DIR_AD_LENGTH) {
            path = path.substring(DIR_AD_LENGTH + 1);
        } else {
            return getPathDocumentFile(activity, ANDROID_DATA) != null;
        }
        
        String[] list = split(path, '/');
        DocumentFile df = getRootDocumentFile(activity, list[0]);
        return df != null;
    }


    /**
     * 获取 data 文件夹权限
     *
     * @param activity
     * @param uri
     */
    private void getDataPermission(Activity activity, String dirPath) {
        String uri;
        if (dirPath.length() > DIR_AD_LENGTH) {
            String subPath = dirPath.substring(DIR_AD_LENGTH + 1);
            if (subPath.contains("/")) {
                subPath = subPath.substring(0, subPath.indexOf("/"));
            }
            if (IS_SDK_32) {
                uri = ANDROID_DATA + "%2F" + subPath + "/document/primary%3AAndroid%2Fdata%2F" + subPath;
            } else {
                uri = ANDROID_DATA + "/document/primary%3AAndroid%2Fdata";
            }
        } else {
            uri = ANDROID_DATA + "/document/primary%3AAndroid%2Fdata";
        }
        if (IS_SDK_34) {
            uri = uri.replace("Android%2Fdata", "Andr%E2%80%8Boid%2Fda%E2%80%8Bta");
        }
        
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(uri));
        activity.startActivityForResult(intent, REQUEST_CODE_DOCUMENT);
    }
    
    /**
     * 删除文件夹
     *
     * @param filePath 目标路径
     * @return
     */
    private boolean deleteDir(String filePath) {
        boolean allDeleted = true;
        DocumentFile dir = getFile(filePath, false);
        for (DocumentFile file : dir.listFiles()) {
            if (file.isFile()) {
                if (!file.delete()) {
                    allDeleted = false;
                }
            } else if (file.isDirectory()) {
                String childPath = filePath;
                if (!filePath.endsWith("/")) {
                    childPath += "/";
                }
                childPath += file.getName();
                
                if (!deleteDir(childPath)) {
                    allDeleted = false;
                }
            }
        }

        if (allDeleted) {
            return dir.delete();
        }
        return false;
    }
    
    /**
     * 复制或移动文件
     *
     * @param context
     * @param sourceFilePath
     * @param destFilePath
     * @return
     */
    private boolean copyOrMoveFile(String sourceFilePath, String destFilePath, boolean isMove) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        
        try {
            if (isType(sourceFilePath)) {
                bis = new BufferedInputStream(new FileInputStream(sourceFilePath));
            } else {
                DocumentFile df = getFile(sourceFilePath, false);
                bis = new BufferedInputStream(activity.getContentResolver().openInputStream(df.getUri()));
            }
            if (isType(destFilePath)) {
                bos = new BufferedOutputStream(new FileOutputStream(destFilePath));
            } else {
                DocumentFile df2 = getFile(destFilePath, true);
                bos = new BufferedOutputStream(activity.getContentResolver().openOutputStream(df2.getUri()));
            }
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
            
            if (isMove) {
                delete(sourceFilePath);
            }
        } catch(IOException e) {
        	e.printStackTrace();
            return false;
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 复制或移动文件夹
     *
     * @param context
     * @param sourceDirPath
     * @param destDirPath
     * @return
     */
    private boolean copyOrMoveDir(String sourceDirPath, String destDirPath, boolean isMove) {
        if (isType(sourceDirPath)) {
            File files = new File(sourceDirPath);
            for (File file: files.listFiles()) {
                String sourceFilePath = file.getPath();
                String destFilePath = destDirPath + "/" + file.getName();
                if (isMove ? !move(sourceFilePath, destFilePath) : !copy(sourceFilePath, destFilePath)) {
                    return false;
                }
            }
            if (isMove) {
                files.delete();
            }
            return true;
        }
        
        DocumentFile df = getFile(sourceDirPath, false);
        for (DocumentFile file : df.listFiles()) {
            String sourceFilePath = String.format("%s/%s", sourceDirPath, file.getName());
            String destFilePath = String.format("%s/%s", destDirPath, file.getName());
            if (isMove ? !move(sourceFilePath, destFilePath) : !copy(sourceFilePath, destFilePath)) {
                return false;
            }
            if (isMove) {
                df.delete();
            }
        }
        return true;
    }
    
    /**
     * 获取文件DocumentFile
     * 
     * @param path
     * @param isNew
     * @return
     */
    private DocumentFile getFile(String path, boolean isNew) {
        if (path.length() <= DIR_AD_LENGTH) {
            return getPathDocumentFile(activity, ANDROID_DATA);
        }
        
        String[] list = split(path.substring(DIR_AD_LENGTH + 1), '/');
        DocumentFile df = getRootDocumentFile(activity, list[0]);

        if (df == null) {
            return null;
        }
        
        int startIndex = IS_SDK_32 ? 1 : 0;
        for (int i = startIndex; i < list.length; i++) {
            String name = list[i];
            DocumentFile df2 = df.findFile(name);

            if (df2 == null) {
                if (!isNew) {
                    return null;
                }

                if (i == list.length - 1) {
                    df = df.createFile("*/*", name);
                } else {
                    df = df.createDirectory(name);
                }
            } else {
                df = df2;
            }
        }

        return df;
    }

    
    /**
     * 获取根路径
     *
     * @param context
     * @param path
     * @return
     */
    private DocumentFile getRootDocumentFile(Context context, String pn) {
        String path = IS_SDK_32 ? ANDROID_DATA + "%2F" + pn : ANDROID_DATA;
        return getPathDocumentFile(context, path);
    }

    private DocumentFile getPathDocumentFile(Context context, String path) {
        Uri uri;
        for (UriPermission up : context.getContentResolver().getPersistedUriPermissions()) {
            if (up.isReadPermission()) {
                uri = up.getUri();
                if (path.equals(uri.toString())) {
                    return DocumentFile.fromTreeUri(context, uri);
                }
            }
        }
        return null;
    }

    
    /**
     * 分割字符串
     *
     * @param str
     * @param separatorChar
     * @return
     */
    private static String[] split(final String str, final char separatorChar) {
        if (str == null) {
            return null;
        }

        final int len = str.length();
        if (len == 0) {
            return new String[0];
        }

        List<String> list = new ArrayList<>();
        int i = 0, start = 0;
        boolean match = false;

        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }
                start = ++i;
            } else {
                match = true;
                i++;
            }
        }

        if (match) {
            list.add(str.substring(start, i));
        }

        return list.toArray(new String[0]);
    }
    
    private boolean isType(String path) {
        return !path.toLowerCase().startsWith(DIR.toLowerCase() + "/android/data");
    }
}
