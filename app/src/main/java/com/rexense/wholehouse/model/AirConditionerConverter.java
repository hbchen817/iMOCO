package com.rexense.wholehouse.model;

public class AirConditionerConverter {

    public static class AirConditioner {
        private String endPoint;
        private String nickname;
        private String workMode;
        private String windSpeed;
        private String powerSwitch;
        private String currentTemperature;
        private String targetTemperature;

        public AirConditioner() {
        }

        public String getTargetTemperature() {
            return targetTemperature;
        }

        public void setTargetTemperature(String targetTemperature) {
            this.targetTemperature = targetTemperature;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getWorkMode() {
            return workMode;
        }

        public void setWorkMode(String workMode) {
            this.workMode = workMode;
        }

        public String getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(String windSpeed) {
            this.windSpeed = windSpeed;
        }

        public String getPowerSwitch() {
            return powerSwitch;
        }

        public void setPowerSwitch(String powerSwitch) {
            this.powerSwitch = powerSwitch;
        }

        public String getCurrentTemperature() {
            return currentTemperature;
        }

        public void setCurrentTemperature(String currentTemperature) {
            this.currentTemperature = currentTemperature;
        }
    }
}
