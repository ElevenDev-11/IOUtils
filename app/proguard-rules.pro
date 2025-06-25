# --- 保留 IOUtils 及其内部接口 PermissionCallback 的原始类名和方法名 ---
-keep class cn.elevendev.io.IOUtils { *; }
-keep class cn.elevendev.io.IOUtils$PermissionCallback { *; }

# --- 保留 StrategyType 枚举完整信息 ---
-keep class cn.elevendev.io.strategy.StrategyType { *; }

# --- 保留调试信息（可选） ---
-keepattributes SourceFile,LineNumberTable

# --- 禁止删除代码（确保所有类都保留） ---
-dontshrink