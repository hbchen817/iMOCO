package com.gary.hi.library.log;

import java.util.Stack;

/**
 * @author lzm
 * @date 2020/8/25
 */
public class HiStackTraceUtil {

    /**
     * 获取裁剪的真实的堆栈信息
     *
     * @param stackTrace
     * @param ignorePackage
     * @param maxDepth
     * @return
     */
    public static StackTraceElement[] getCroppedRealStackTrace(StackTraceElement[] stackTrace, String ignorePackage, int maxDepth) {
        return cropStackTrace(getRealStackTrace(stackTrace, ignorePackage), maxDepth);
    }

    /**
     * 获取真实堆栈信息 排除掉HILog包下堆栈信息
     *
     * @param stackTrace
     * @param ignorePackage
     * @return
     */
    private static StackTraceElement[] getRealStackTrace(StackTraceElement[] stackTrace, String ignorePackage) {
        int ignoreLength = 0;
        int allDepth = stackTrace.length;
        String className;
        for (int i = 0; i < allDepth; i++) {
            className = stackTrace[i].getClassName();
            if (ignorePackage != null && className.startsWith(ignorePackage)) {
                ignoreLength = i + 1;
            }
        }
        int realDepth = allDepth - ignoreLength;
        StackTraceElement[] realStack = new StackTraceElement[realDepth];
        System.arraycopy(stackTrace, ignoreLength, realStack, 0, realDepth);
        return realStack;
    }

    /**
     * 裁剪堆栈信息
     *
     * @param callStack
     * @param maxDepth
     * @return
     */
    private static StackTraceElement[] cropStackTrace(StackTraceElement[] callStack, int maxDepth) {
        int realDepth = callStack.length;
        if (maxDepth > 0) {
            realDepth = Math.min(realDepth, maxDepth);
        }
        StackTraceElement[] realStack = new StackTraceElement[realDepth];
        System.arraycopy(callStack, 0, realStack, 0, realDepth);
        return realStack;
    }
}
