package com.rexense.imoco.model;

import java.util.HashMap;
import java.util.Map;

import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: API通道实体
 */
public class EAPIChannel {
    // 请求参数实体
    public static class requestParameterEntry {
        public String path;
        public String version;
        public String authType;
        public Scheme scheme;
        public Map<String, Object> parameters;
        public int callbackMessageType;

        // 构造
        public requestParameterEntry(){
            this.path = null;
            this.version = null;
            this.authType = null;
            this.scheme = null;
            this.parameters = null;
        }

        // 追加参数
        public void addParameter(String name, Object object){
            if(this.parameters == null){
                this.parameters = new HashMap<>();
            }
            if(!this.parameters.containsKey(name)) {
                this.parameters.put(name, object);
            }
        }
    }

    // 提交失败实体
    public static class commitFailEntry {
        public String path;
        public String version;
        public String authType;
        public Scheme scheme;
        public Map<String, Object> parameters;
        public Exception exception;

        public commitFailEntry(Exception exception){
            this.path = null;
            this.version = null;
            this.authType = null;
            this.scheme = null;
            this.parameters = null;
            this.exception = exception;
        }
    }

    // 响应错误实体
    public static class responseErrorEntry {
        public String path;
        public String version;
        public String authType;
        public Scheme scheme;
        public Map<String, Object> parameters;
        public int code;
        public String message;
        public String localizedMsg;

        public responseErrorEntry(){
            this.path = null;
            this.version = null;
            this.authType = null;
            this.scheme = null;
            this.parameters = null;
            this.message = null;
            this.localizedMsg = null;
        }
    }
}

