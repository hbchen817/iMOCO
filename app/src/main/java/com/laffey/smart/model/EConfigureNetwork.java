package com.laffey.smart.model;

import com.laffey.smart.contract.Constant;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 配网实体
 */
public class EConfigureNetwork {
    // 数据帧实体
    public static class dataFrameEntry {
        public short header;
        public short length;
        public byte cmd;
        public short ack;
        public short dataLength;
        public byte[] data;
        public byte footer;

        // 构造
        public dataFrameEntry() {
        }

        // 构造(用于解析接收帧)
        public dataFrameEntry(byte[] frame, short length){
            // header处理
            if(length >= 2) {
                this.header = (short) ((short) frame[1] * 256 + (short)frame[0]);
            } else {
                this.header = -1;
                return;
            }

            // length处理
            if(length >= 3) {
                this.length = (short) (frame[2] & 0xFF);
            } else {
                this.length = -1;
                return;
            }

            // cmd处理
            if(length >= 4) {
                this.cmd = frame[3];
            } else {
                this.cmd = -1;
                return;
            }

            // ack处理
            if(length >= 5) {
                this.ack = (short)(frame[4] & 0xFF);
            } else {
                this.ack = -1;
                return;
            }

            // dataLength处理
            if(length >= 6) {
                this.dataLength = (short)(frame[5] & 0xFF);
            } else {
                this.dataLength = -1;
                return;
            }

            // data处理
            if(this.dataLength > 0) {
                if(length >= (6 + this.dataLength)) {
                    this.data = new byte[this.dataLength];
                    System.arraycopy(frame, 6, this.data, 0, this.dataLength);
                } else {
                    this.dataLength = -1;
                    return;
                }

                // footer处理
                if(length >= (6 + this.dataLength + 1)) {
                    this.footer = frame[6 + this.dataLength];
                } else {
                    this.footer = -1;
                }
            } else {
                // 如果是应答帧则没有Data直接处理footer
                if(length >= 7){
                    this.footer = frame[6];
                } else {
                    this.footer = -1;
                }
            }
        }

        // 生成数据帧(用于组装发送帧)
        public byte[] genFrame(byte[] data, int cmd){
            if(data == null || data.length == 0){
                return null;
            }

            byte[] frame = new byte[2 + 1 + 1 + 1 + 1 + data.length + 1];
            int index = 0;
            frame[index++] = (byte) (Constant.CONFIGNETWORK_FRAME_HEADER & 0xFF);
            frame[index++] = (byte) ((Constant.CONFIGNETWORK_FRAME_HEADER >> 8) & 0xFF);
            frame[index++] = (byte) (1 + 1 + 1 + data.length);
            frame[index++] = (byte) cmd;
            frame[index++] = (byte) Constant.CONFIGNETWORK_FRAME_NONACK;
            frame[index++] = (byte) (data.length);
            System.arraycopy(data, 0, frame, index, data.length);
            frame[index + data.length] = (byte) Constant.CONFIGNETWORK_FRAME_FOOTER;
            return  frame;
        }
    }

    // 解析结果实体
    public static class parseResultEntry {
        public short cmd;
        public short ack;
        public String content;

        // 构造
        public parseResultEntry(short cmd, short ack, String content){
            this.cmd = cmd;
            this.ack = ack;
            this.content = content;
        }
    }

    // 绑定设备参数实体
    public static class bindDeviceParameterEntry{
        public String homeId;
        public String productKey;
        public String deviceName;
        public String token;

        // 构造
        public bindDeviceParameterEntry(){
        }
    }
}

