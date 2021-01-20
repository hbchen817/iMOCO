package com.laffey.smart.demoTest;

import com.laffey.smart.contract.Constant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionEntry {
    private List<Object> entries = new ArrayList<>();

    public List<Object> getEntries(){
        return entries;
    }

    public static class SetSwitch {
        private String uri = "action/automation/setSwitch";
        private Map<String, Object> params = new HashMap<>();

        public void setRuleId(String id) {
            params.put("automationRuleId", id);
        }

        public void setSwitchStatus(int status) {
            params.put("switchStatus", status);
        }
    }

    public static class Trigger {
        private String uri = "action/scene/trigger";
        private Map<String, String> params = new HashMap<>();

        public void setSceneId(String id) {
            this.params.put("sceneId", id);
        }

        public String getSceneId() {
            return this.params.get("sceneId");
        }
    }

    public static class InvokeService {
        private String uri = "action/device/invokeService";
        private Map<String, Object> params = new HashMap<>();

        public void setIotId(String id) {
            this.params.put("iotId", id);
        }

        public void setServiceName(String name) {
            this.params.put("serviceName", name);
        }

        public void setServiceArgs(Object args) {
            this.params.put("serviceArgs", args);
        }

        public String getIotId() {
            return (String) this.params.get("iotId");
        }

        public String getServiceName() {
            return (String) this.params.get("serviceName");
        }

        public Object getServiceArgs() {
            return this.params.get("serviceArgs");
        }
    }

    public static class Property {
        private String uri = "action/device/setProperty";
        private Map<String, Object> params = new HashMap<>();

        public void setIotId(String id) {
            this.params.put("iotId", id);
        }

        public void setPropertyName(String propertyName) {
            this.params.put("propertyName", propertyName);
        }

        public String getPropertyName() {
            return (String) this.params.get("propertyName");
        }

        public void setPropertyValue(Object propertyValue) {
            this.params.put("propertyValue", propertyValue);
        }

        public Object getPropertyValue() {
            return this.params.get("propertyValue");
        }
    }

    public static class SendMsg {
        private String uri = Constant.SCENE_ACTION_SEND;
        private Map<String, Object> params = new HashMap<>();

        public void setMessage(String msg) {
            this.params.put("msgTag", "IlopBusiness_CustomMsg");

            Map<String, String> m = new HashMap<>();
            m.put("message",msg);

            this.params.put("customData", m);
        }

        public String getMessage(){
            Map<String, String> m = (Map<String, String>) params.get("customData");
            return m.get("message");
        }
    }
}
