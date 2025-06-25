package cn.elevendev.io.strategy;

public enum StrategyType {
    
    /**
     * 本地文件操作策略
     */
    NATIVE,
    
    /**
     * 使用 Shizuku 服务的文件操作策略
     */
    SHIZUKU,
    
    /**
     * 利用系统漏洞的文件操作策略
     */
    //LOOPHOLE;
}
