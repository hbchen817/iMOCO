package com.rexense.imoco.model;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-07 15:29
 * Description: BLE实体
 */
public class EBLE {
    // 设备实体
    public static class DeviceEntry {
        private String mName;
        private String mAddress;

        public DeviceEntry(String name, String address) {
            this.mName = name;
            this.mAddress = address;
        }

        public String getName() {
            return this.mName;
        }

        public String getAddress() {
            return this.mAddress;
        }
    }

    // 特性实体
    public static class CharacteristicEntry {
        private String mServiceUUID;
        private String mCharacteristicUUID;
        private byte[] mValue;

        public CharacteristicEntry(String serviceUUID, String characteristicUUID, byte[] value) {
            this.mServiceUUID = serviceUUID;
            this.mCharacteristicUUID = characteristicUUID;
            this.mValue = value;
        }

        public String getServiceUUID() {
            return this.mServiceUUID;
        }

        public String getCharacteristicUUID() {
            return this.mCharacteristicUUID;
        }

        public byte[] getValue() {
            return this.mValue;
        }
    }
}