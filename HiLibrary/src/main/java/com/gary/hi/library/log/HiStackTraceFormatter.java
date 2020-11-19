package com.gary.hi.library.log;

/**
 * @author lzm
 * @date 2020/8/24
 * 堆栈信息格式化
 */
class HiStackTraceFormatter implements HiLogFormatter<StackTraceElement[]> {
    @Override
    public String format(StackTraceElement[] data) {
        StringBuilder stringBuilder = new StringBuilder(128);
        if (data == null || data.length == 0) {
            return null;
        } else if (data.length == 1) {
            return "\t-" + data[0].toString();
        } else {
            for (int i = 0; i < data.length; i++) {
                if (i == 0) {
                    stringBuilder.append("stacktrace: \n");
                }
                if (i != data.length - 1) {
                    stringBuilder.append(i == 0 ? "\t┌ " : "\t├ ");
                    stringBuilder.append(data[i].toString());
                    stringBuilder.append("\n");
                }else {
                    stringBuilder.append("\t└ ");
                    stringBuilder.append(data[i].toString());
                }
            }
            return stringBuilder.toString();
        }
    }
}
