package com.gary.hi.library.log;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 日志可视化 RecyclerView所展示的日志实体
 */
public class HiLogMo {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
    public long timeMills;
    public int level;
    public String tag;
    public String log;

    public HiLogMo(long timeMills, int level, String tag, String log) {
        this.timeMills = timeMills;
        this.level = level;
        this.tag = tag;
        this.log = log;
    }

    public String getFlattenedLog() {
        return getFlattened() + log;
    }

    public String getFlattened() {
        return format() + "|" + HiLogType.getTypeString(level) + "|" + tag + "|:";
    }

    public String format() {
        return DATE_FORMAT.format(timeMills);
    }
}
