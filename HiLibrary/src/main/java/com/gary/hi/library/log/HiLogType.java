package com.gary.hi.library.log;

import android.util.Log;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * HiLog日志类型
 */
public class HiLogType {
    //接收类型是int
    @IntDef({V, D, I, W, E, A})
    //注解保留时期设置为源码级别
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE {

    }

    public static final int V = Log.VERBOSE;
    public static final int D = Log.DEBUG;
    public static final int I = Log.INFO;
    public static final int W = Log.WARN;
    public static final int E = Log.ERROR;
    public static final int A = Log.ASSERT;

    public static char getTypeString(int level) {
        switch (level) {
            case V:
                return 'V';
            case D:
                return 'D';
            case I:
                return 'I';
            case W:
                return 'W';
            case E:
                return 'E';
            case A:
                return 'A';
            default:
                return 'N';
        }
    }
}
