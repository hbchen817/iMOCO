package com.rexense.smart.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

    public static String toJson(Object o) {
        return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(o);
    }

    public static Gson create() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }
}
