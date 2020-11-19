package com.gary.hi.library.log;

public abstract class HiLogConfig {

    static int MAX_LEN = 512;

    static final HiStackTraceFormatter HI_STACK_TRACE_FORMATTER = new HiStackTraceFormatter();
    static final HiThreadFormatter HI_THREAD_FORMATTER = new HiThreadFormatter();

    //提供给外部 使其自定义解析方法
    public JsonParser injectJsonParser() {
        return null;
    }

    public String getGlobalTag() {
        return "HiLog";
    }

    public boolean enable() {
        return true;
    }

    //是否打印线程信息
    public boolean includeThread() {
        return false;
    }

    //堆栈信息深度
    public int stackTraceDepth() {
        return 5;
    }

    public HiLogPrinter[] printers() {
        return null;
    }

    public interface JsonParser {
        String toJson(Object o);
    }

}
