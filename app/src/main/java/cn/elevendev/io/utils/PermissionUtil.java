package cn.elevendev.io.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import cn.elevendev.io.R;
import com.airbnb.lottie.LottieAnimationView;

public class PermissionUtil {

    public static final int REQUEST_WRITE_STORAGE = 10011;
    public static final int REQUEST_CODE_ALL_FILES_PERMISSION = 10012;
    
    /**
     * 判断是否拥有存储权限
     *
     * @param activity
     * @return
     */
    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * 获取存储权限
     *
     * @param activity
     */
    public static void requestStoragePermission(Activity activity) {
        if (isStoragePermissionGranted(activity)) {
            return;
        }
        showDialog(activity);
    }


    /**
     * 获取存储权限
     * 适用于 Android 11 以下版本
     *
     * @param activity
     */
    private static void requestLegacyPermission(Activity activity) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(activity, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_WRITE_STORAGE);
        } else {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        }
    }


    /**
     * 获取所有文件访问权限
     * 适用于 Android 11 以上版本
     *
     * @param activity
     */
    private static void requestAllFilesPermission(Activity activity) {
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivityForResult(intent, REQUEST_CODE_ALL_FILES_PERMISSION);
        }
    }

    private static void showDialog(Activity activity) {
        final AppCompatDialog dialog = new AppCompatDialog(activity, R.style.DialogTransBgDark);
        final CardView closeView, confirmView;
        View view = View.inflate(activity, R.layout.dialog_storage_permission, null);
        closeView = view.findViewById(R.id.close_view);
        confirmView = view.findViewById(R.id.confirm_view);
        
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.animation_view);
        lottieAnimationView.playAnimation();
        
        ViewGroup.LayoutParams lottieViewParams = lottieAnimationView.getLayoutParams();
        lottieViewParams.height = (int) (getScreenWidth(activity) / 2.5f);
        lottieAnimationView.setLayoutParams(lottieViewParams);
        
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getScreenWidth(activity) / 7 * 5;
        dialog.getWindow().setAttributes(params);
        
        closeView.setOnClickListener(v -> dialog.dismiss());
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestAllFilesPermission(activity);
                } else {
                    requestLegacyPermission(activity);
                }
            }
        });
    }

    /**
     * 获取屏幕的宽度（像素）
     *
     * @param context 上下文
     * @return 屏幕宽度（像素）
     */
    private static int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null && wm.getDefaultDisplay() != null) {
            wm.getDefaultDisplay().getMetrics(metrics);
            return metrics.widthPixels;
        }
        return 0;
    }
}
