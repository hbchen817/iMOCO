package com.gary.hi.library.log;

import android.util.Log;

import com.gary.hi.library.log.HiLogManager;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.TooManyListenersException;

/**
 * Tips:
 * 1. 打印堆栈信息
 * 2. File输出
 * 3. 模拟控制台
 */
public class HiLog {

    public static final String HI_LOG_PACKAGE;

    static {
        String className = HiLog.class.getName();
        HI_LOG_PACKAGE = className.substring(0, className.lastIndexOf('.') + 1);
    }

    public static void v(Object... contents) {
        log(HiLogType.V, contents);
    }

    public static void vt(String tag, Object... contents) {
        log(HiLogType.V, tag, contents);
    }

    public static void d(Object... contents) {
        log(HiLogType.D, contents);
    }

    public static void dt(String tag, Object... contents) {
        log(HiLogType.D, tag, contents);
    }

    public static void i(Object... contents) {
        log(HiLogType.I, contents);
    }

    public static void it(String tag, Object... contents) {
        log(HiLogType.I, tag, contents);
    }

    public static void w(Object... contents) {
        log(HiLogType.W, contents);
    }

    public static void wt(String tag, Object... contents) {
        log(HiLogType.W, tag, contents);
    }

    public static void e(Object... contents) {
        log(HiLogType.E, contents);
    }

    public static void et(String tag, Object... contents) {
        log(HiLogType.E, tag, contents);
    }

    public static void a(Object... contents) {
        log(HiLogType.A, contents);
    }

    public static void at(String tag, Object... contents) {
        log(HiLogType.A, tag, contents);
    }

    public static void log(@HiLogType.TYPE int type, Object... contents) {
        log(type, HiLogManager.getInstance().getHiLogConfig().getGlobalTag(), contents);

    }

    public static void log(@HiLogType.TYPE int type, @NotNull String tag, Object... contents) {
        log(HiLogManager.getInstance().getHiLogConfig(), type, tag, contents);
    }

    public static void log(HiLogConfig config, @HiLogType.TYPE int type, @NotNull String tag, Object... contents) {
        if (!config.enable()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (config.includeThread()) {
            String threadInfo = HiLogConfig.HI_THREAD_FORMATTER.format(Thread.currentThread());
            stringBuilder.append(threadInfo).append(" | ");
        }
        if (config.stackTraceDepth() > 0) {
            String stackTrace = HiLogConfig.HI_STACK_TRACE_FORMATTER.format(HiStackTraceUtil.getCroppedRealStackTrace(new Throwable().getStackTrace(), HI_LOG_PACKAGE, config.stackTraceDepth()));
            stringBuilder.append(stackTrace).append("\n");
        }
        String body = parseBody(contents, config);
        stringBuilder.append(body);
        List<HiLogPrinter> printers = config.printers() != null ? Arrays.asList(config.printers()) : HiLogManager.getInstance().getPrinters();
        if (printers == null) {
            return;
        }
        //打印log
        for (HiLogPrinter printer : printers) {
            printer.print(config, type, tag, stringBuilder.toString());
        }
    }

    private static String parseBody(@NotNull Object[] objects, @NotNull HiLogConfig config) {
        if (config.injectJsonParser() != null) {
            return config.injectJsonParser().toJson(objects);
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object o : objects) {
            stringBuilder.append(o.toString()).append(";");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
}
