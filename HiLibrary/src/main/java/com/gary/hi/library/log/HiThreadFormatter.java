package com.gary.hi.library.log;

/**
 * @author lzm
 * @date 2020/8/24
 */
public class HiThreadFormatter implements HiLogFormatter<Thread> {
    @Override
    public String format(Thread data) {
        return "Thread:" + data.getName();
    }
}
