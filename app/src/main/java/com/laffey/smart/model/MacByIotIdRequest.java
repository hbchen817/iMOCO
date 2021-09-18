package com.laffey.smart.model;

public class MacByIotIdRequest {
    private String apiVer;
    private Param params;

    public String getApiVer() {
        return apiVer;
    }

    public void setApiVer(String apiVer) {
        this.apiVer = apiVer;
    }

    public Param getParams() {
        return params;
    }

    public void setParams(Param params) {
        this.params = params;
    }

    public MacByIotIdRequest(String apiVer, Param params) {
        this.apiVer = apiVer;
        this.params = params;
    }

    public static class Param {
        private String paltform;
        private String iotId;

        public Param(String paltform, String iotId) {
            this.paltform = paltform;
            this.iotId = iotId;
        }

        public String getPaltform() {
            return paltform;
        }

        public void setPaltform(String paltform) {
            this.paltform = paltform;
        }

        public String getIotId() {
            return iotId;
        }

        public void setIotId(String iotId) {
            this.iotId = iotId;
        }
    }
}
