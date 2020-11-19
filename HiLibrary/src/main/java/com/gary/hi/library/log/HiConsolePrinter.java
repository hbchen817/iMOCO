package com.gary.hi.library.log;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import static com.gary.hi.library.log.HiLogConfig.MAX_LEN;

/**
 * @author lzm
 * @date 2020/8/24
 * 控制台打印器
 */
public class HiConsolePrinter implements HiLogPrinter {
    @Override
    public void print(@NotNull HiLogConfig config, int level, String tag, @NotNull String printString) {
        int length = printString.length();
        int countOfSub = length / MAX_LEN;
        if (countOfSub > 0) {
            int index = 0;
            for (int i = 0; i < countOfSub; i++) {
                Log.println(level, tag, printString.substring(index, index + MAX_LEN));
                index += MAX_LEN;
            }
            if (index != length) {
                Log.println(level, tag, printString.substring(index));
            }
        } else {
            Log.println(level, tag, printString);
        }
    }
}
