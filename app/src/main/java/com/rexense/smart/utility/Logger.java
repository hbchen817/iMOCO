package com.rexense.smart.utility;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 日志记录器
 */
public class Logger {
    // 定义日志标签
    private static final String LOG_TAG = "tag_com.rexense.imoco";
    private static final int mMaxSize = 3072;
    private static int mLogLevel = 1;
    private static int mInvokingLevel = 3;

    // 设置日志等级
    // 参数logLevel取值如下：
    // 0: 不输出信息
    // 2: 输出详细信息
    // 3: 输出跟踪信息
    // 4: 输出一般信息
    // 5: 输出警告信息
    // 6: 输出错误信息
    // 7: 输出断言信息
    // 说明：除0等级日志水以外，设置的日志等级是输出的最低等级，高于这个等级的日志将一并输出
    public static void setLogLevel(int logLevel) {
        mLogLevel = logLevel;
    }

    // 设置栈调用等级
    public static void setInvokingLevel(int invokingLevel) {
        mInvokingLevel = invokingLevel;
    }

    // 记录详细信息
    public static void v(String msg) {
        if (Log.VERBOSE >= mLogLevel && mLogLevel > 0) {
            String message = initMessage(new Throwable(), msg);
            if (message.length() <= mMaxSize) {
                Log.v(LOG_TAG, message);
            } else {
                Iterator it = splitMessage(message).iterator();
                while (it.hasNext()) {
                    Log.v(LOG_TAG, it.next().toString());
                }
            }
        }
    }

    // 记录跟踪信息
    public static void d(String msg) {
        if (Log.DEBUG >= mLogLevel && mLogLevel > 0) {
            String message = initMessage(new Throwable(), msg);
            if (message.length() <= mMaxSize) {
                Log.d(LOG_TAG, message);
            } else {
                Iterator it = splitMessage(message).iterator();
                while (it.hasNext()) {
                    Log.d(LOG_TAG, it.next().toString());
                }
            }
        }
    }

    // 记录一般信息
    public static void i(String msg) {
        if (Log.INFO >= mLogLevel && mLogLevel > 0) {
            String message = initMessage(new Throwable(), msg);
            if (message.length() <= mMaxSize) {
                Log.i(LOG_TAG, message);
            } else {
                Iterator it = splitMessage(message).iterator();
                while (it.hasNext()) {
                    Log.i(LOG_TAG, it.next().toString());
                }
            }
        }
    }

    // 记录警告信息
    public static void w(String msg) {
        if (Log.WARN >= mLogLevel && mLogLevel > 0) {
            String message = initMessage(new Throwable(), msg);
            if (message.length() <= mMaxSize) {
                Log.w(LOG_TAG, message);
            } else {
                Iterator it = splitMessage(message).iterator();
                while (it.hasNext()) {
                    Log.w(LOG_TAG, it.next().toString());
                }
            }
        }
    }

    // 记录错误信息
    public static void e(String msg) {
        if (Log.ERROR >= mLogLevel && mLogLevel > 0) {
            String message = initMessage(new Throwable(), msg);
            if (message.length() <= mMaxSize) {
                Log.e(LOG_TAG, message);
            } else {
                Iterator it = splitMessage(message).iterator();
                while (it.hasNext()) {
                    Log.e(LOG_TAG, it.next().toString());
                }
            }
        }
    }

    // 记录测试断言信息
    public static void a(String msg) {
        if (Log.ASSERT >= mLogLevel && mLogLevel > 0) {
            String message = initMessage(new Throwable(), msg);
            if (message.length() <= mMaxSize) {
                Log.wtf(LOG_TAG, message);
            } else {
                Iterator it = splitMessage(message).iterator();
                while (it.hasNext()) {
                    Log.wtf(LOG_TAG, it.next().toString());
                }
            }
        }
    }

    // 初始化消息
    private static String initMessage(Throwable throwable, String message) {
        if (mInvokingLevel == 0) {
            return "\r\n \r\n" + message;
        }
        StackTraceElement[] elements = throwable.getStackTrace();
        int maxInvokingLevel = mInvokingLevel;
        if (mInvokingLevel > elements.length - 1) {
            maxInvokingLevel = elements.length - 1;
        }
        String invoking = "";
        for (int i = 1; i <= maxInvokingLevel; i++) {
            if (invoking.length() > 0) {
                invoking = invoking + " > ";
            }
            invoking = invoking + "[F(" + elements[i].getFileName() + "):L(" + elements[i].getLineNumber() + "):M(" + elements[i].getMethodName() + ")]";
        }
        return "\r\n \r\n" + invoking + ":\r\n" + message;
    }

    // 分割消息
    private static List splitMessage(String message) {
        List r = new ArrayList();
        if (message.length() <= mMaxSize) {
            r.add(message);
        } else {
            int index = 0;
            int maxIndex = message.length() - 1;
            while (index < maxIndex) {
                if ((maxIndex - index) >= mMaxSize) {
                    if (index == 0) {
                        r.add(message.substring(index, index + mMaxSize));
                    } else {
                        r.add("\r\n \r\n...unfinished information：\r\n    " + message.substring(index, index + mMaxSize));
                    }
                    index = index + mMaxSize;
                } else {
                    r.add("\r\n \r\n...unfinished information：\r\n    " + message.substring(index, maxIndex + 1));
                    break;
                }
            }
        }
        return r;
    }
}
