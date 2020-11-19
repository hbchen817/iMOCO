package com.gary.hi.library.log;

public interface HiLogFormatter<T> {
    String format(T data);
}
