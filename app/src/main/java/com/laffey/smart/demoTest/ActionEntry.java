package com.laffey.smart.demoTest;

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

        public void setPropertyValue(Object propertyValue) {
            this.params.put("propertyValue", propertyValue);
        }
    }
}
