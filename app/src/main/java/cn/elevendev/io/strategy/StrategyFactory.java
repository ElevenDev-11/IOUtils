package cn.elevendev.io.strategy;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import static cn.elevendev.io.strategy.StrategyType.NATIVE;
import static cn.elevendev.io.strategy.StrategyType.SHIZUKU;
import java.io.File;
import java.util.Arrays;

public class StrategyFactory {
    
    /**
     * 根据传入的 StrategyType 创建对应的策略对象
     *
     * @param type 策略类型
     * @return 对应的策略对象
     */
    public static Strategy createStrategy(Activity activity, StrategyType strategyType) {
        switch (strategyType) {
            case NATIVE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (fsHasRegExBug()) {
                        return new LoopholeStrategy();
                    }
                    return new DocumentStrategy(activity);
                }
                return new FileStrategy();
            case SHIZUKU:
                return new ShizukuStrategy();
            default:
                throw new IllegalArgumentException("未知的策略类型");
        }
    }

    private static boolean fsHasRegExBug() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        return Arrays.equals(new File(externalStorageDirectory, "Android").list(), new File(externalStorageDirectory, "\u200bAndroid").list());
    }
}
