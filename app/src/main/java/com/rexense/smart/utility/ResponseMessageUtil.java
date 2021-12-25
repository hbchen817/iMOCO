package com.rexense.smart.utility;

public class ResponseMessageUtil {

    public static String replaceMessage(String msg) {
        switch (msg.trim()) {
            case "scene rule not enable":
                return "当前场景已停用";
            default:
                return msg;
        }
    }

}
