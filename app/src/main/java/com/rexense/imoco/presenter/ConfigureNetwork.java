package com.rexense.imoco.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.rexense.imoco.contract.CBLE;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.model.EConfigureNetwork;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.utility.AES;
import com.rexense.imoco.utility.BLEService;
import com.rexense.imoco.utility.Logger;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-09 15:29
 * Description: 配网
 */
public class ConfigureNetwork {
    private Context mContext;
    private byte[] buffer;
    private short dataLength;

    // 构造
    public ConfigureNetwork(Context context){
        this.mContext = context;
        this.buffer = new byte[Constant.CONFIGNETWORK_BUFFER_MAXSIZE];
        this.dataLength = 0;
    }

    // 发送配网数据到网关蓝牙
    public static boolean sendDataToBLE(BLEService bleService, String data, String key, int cmd) {
        if(data == null || data.length() == 0) {
            return false;
        }

        // 数据加密(加密内容为在原始内容基础上前置一个原始内容长度标识字节)
        byte[] content = new byte[data.getBytes().length + 1];
        content[0] = (byte) (data.getBytes().length & 0xFF);
        System.arraycopy(data.getBytes(), 0, content, 1, data.getBytes().length & 0xFF);
        byte[] content_encrypt = AES.CBC128ZeroPaddingEncrypt(content, key, Constant.CONFIGNETWORK_AES_CBC_IV);

        // 生成数据帧
        byte[] value = new EConfigureNetwork.dataFrameEntry().genFrame(content_encrypt, cmd);
        return bleService.writeCharacteristic(CBLE.READ_WRITE_SERVICE_UUID, CBLE.READ_WRITE_CHARACTERISTIC_UUID, value);
    }

    // 解析网关蓝牙应答数据
    public void parseBLEResponseData(byte[] frame, String key, Handler handler) {
        if(frame == null || frame.length == 0 || key == null || key.length() == 0){
            return;
        }

        // 将数据追加到缓存
        this.addDataToBuffer(frame);

        while (this.dataLength >= Constant.CONFIGNETWORK_FRAME_MINSIZE){
            // 构造数据帧
            EConfigureNetwork.dataFrameEntry frameEntry = new EConfigureNetwork.dataFrameEntry(this.buffer, this.dataLength);

            // 帧不合法则丢掉前面一个字节继续处理
            if((frameEntry.header > 0 && frameEntry.header != Constant.CONFIGNETWORK_FRAME_HEADER) ||
                    frameEntry.length == 0 ||
                    frameEntry.length - frameEntry.dataLength != 3 ||
                    (frameEntry.footer > 0 && frameEntry.footer != Constant.CONFIGNETWORK_FRAME_FOOTER)) {
                this.removeFrontDataFromBuffer((short) 1);
            }

            // 帧数据没收全则退出下一次再处理
            if(-1 == frameEntry.header ||
                    -1 == frameEntry.length ||
                    -1 == frameEntry.cmd ||
                    -1 == frameEntry.ack ||
                    -1 == frameEntry.dataLength ||
                    -1 == frameEntry.footer) {
                break;
            }

            // 处理合法帧
            boolean processIsSuccess = true;
            EConfigureNetwork.parseResultEntry resultEntry = null;
            if(frameEntry.ack == Constant.CONFIGNETWORK_FRAME_NONACK){
                // 数据解密
                byte[] contentBytes = AES.CBC128ZeroPaddingDecrypt(frameEntry.data, key, Constant.CONFIGNETWORK_AES_CBC_IV);
                int dataLength = contentBytes[0] & 0xFF;
                if(dataLength >= contentBytes.length){
                    dataLength = contentBytes.length - 1;
                }
                byte[] dataBytes = new byte[dataLength];
                System.arraycopy(contentBytes, 1, dataBytes, 0, dataBytes.length);
                String content = new String(dataBytes);
                // 单独处理token(直接采用字节值16进制字符)
                if(frameEntry.cmd == Constant.CONFIGNETWORK_CMD_RECEIVETOKEN){
                    StringBuilder sb = new StringBuilder();
                    for(byte b: dataBytes){
                        sb.append(String.format("%02X", b));
                    }
                    content = sb.toString();
                }
                if(content == null) {
                    processIsSuccess = false;
                } else {
                    resultEntry = new EConfigureNetwork.parseResultEntry(frameEntry.cmd, frameEntry.ack, content);
                }
            } else {
                resultEntry = new EConfigureNetwork.parseResultEntry(frameEntry.cmd, frameEntry.ack, "");
            }
            if(handler != null && processIsSuccess) {
                Message msg = new Message();
                msg.what = Constant.MSG_PARSE_CONFIGNETWORKFRAME;
                msg.obj = resultEntry;
                handler.sendMessage(msg);
            }

            // 移除处理过的数据
            this.removeFrontDataFromBuffer((short)(frameEntry.length + 2 + 1 + 1));
        }

    }

    // 发送粘包信息(测试用）
    public static boolean sendPacketSplicingInfoTest(BLEService bleService, String key) {
        // 模拟SSID
        byte[] content1 = {4, 'S', 'S', 'I', 'D'};
        byte[] value1 = new EConfigureNetwork.dataFrameEntry().genFrame(AES.CBC128ZeroPaddingEncrypt(content1, key, Constant.CONFIGNETWORK_AES_CBC_IV), Constant.CONFIGNETWORK_CMD_SENDSSID);
        // 模拟Password
        byte[] content2 = {3, 'P', 'W', 'D'};
        byte[] value2 = new EConfigureNetwork.dataFrameEntry().genFrame(AES.CBC128ZeroPaddingEncrypt(content2, key, Constant.CONFIGNETWORK_AES_CBC_IV), Constant.CONFIGNETWORK_CMD_SENDPASSWORD);
        // 模拟Token
        byte[] content3 = {5, 'T', 'o', 'k', 'e', 'n'};
        byte[] value3 = new EConfigureNetwork.dataFrameEntry().genFrame(AES.CBC128ZeroPaddingEncrypt(content3, key, Constant.CONFIGNETWORK_AES_CBC_IV), Constant.CONFIGNETWORK_CMD_RECEIVETOKEN);
        // 模拟应答帧
        byte[] value4 = {0x05, 0x55, 0x03, 0x01, 0x00, 0x00, 0x33, 0x05, 0x55, 0x03, 0x02, 0x00, 0x00, 0x33, 0x05, 0x55, 0x03, 0x05, 0x00, 0x00, 0x33};
        //发送
        byte[] value = new byte[value1.length + value2.length + value3.length + value4.length];
        System.arraycopy(value1, 0, value, 0, value1.length);
        System.arraycopy(value2, 0, value, value1.length, value2.length);
        System.arraycopy(value3, 0, value, value1.length + value2.length, value3.length);
        System.arraycopy(value4, 0, value, value1.length + value2.length + value3.length, value4.length);
        boolean r = bleService.writeCharacteristic(CBLE.READ_WRITE_SERVICE_UUID, CBLE.READ_WRITE_CHARACTERISTIC_UUID, value);

        return r;
    }

    // AES加解密(测试用）
    public static void aesTest() {
        String pk = "a13I8rPymR7";
        String ssid = "Rex_Meeting";
        byte[] ssid_bytes = new byte[1 + ssid.length()];
        ssid_bytes[0] = (byte) ssid.length();
        System.arraycopy(ssid.getBytes(), 0, ssid_bytes, 1, ssid.length());
        String pwd = "rexense820123456";
        byte[] pwd_bytes = new byte[1 + pwd.length()];
        pwd_bytes[0] = (byte) pwd.length();
        System.arraycopy(pwd.getBytes(), 0, pwd_bytes, 1, pwd.length());

        Logger.i(String.format("ssid: %s", ssid));
        byte[] encrypt = AES.CBC128ZeroPaddingEncrypt(ssid_bytes, pk, Constant.CONFIGNETWORK_AES_CBC_IV);
        StringBuilder sb = new StringBuilder();
        for(byte b : encrypt){
            sb.append(String.format("%02X ", b));
        }
        Logger.i(String.format("ssid_encrypt: %s", sb.toString()));
        byte[] decrypt = AES.CBC128ZeroPaddingDecrypt(AES.CBC128ZeroPaddingEncrypt(encrypt, pk, Constant.CONFIGNETWORK_AES_CBC_IV), pk, Constant.CONFIGNETWORK_AES_CBC_IV);
        byte[] content = new byte[decrypt[0]];
        System.arraycopy(decrypt, 1, content, 0, decrypt[0]);
        Logger.i(String.format("ssid_decrypt: %s", new String(content)));

        Logger.i(String.format("password: %s", pwd));
        encrypt = AES.CBC128ZeroPaddingEncrypt(pwd_bytes, pk, Constant.CONFIGNETWORK_AES_CBC_IV);
        sb = new StringBuilder();
        for(byte b : encrypt){
            sb.append(String.format("%02X ", b));
        }
        Logger.i(String.format("password_encrypt: %s", sb.toString()));
        decrypt = AES.CBC128ZeroPaddingDecrypt(AES.CBC128ZeroPaddingEncrypt(encrypt, pk, Constant.CONFIGNETWORK_AES_CBC_IV), pk, Constant.CONFIGNETWORK_AES_CBC_IV);
        content = new byte[decrypt[0]];
        System.arraycopy(decrypt, 1, content, 0, decrypt[0]);
        Logger.i(String.format("password_decrypt: %s", new String(content)));
    }

    // 绑定设备(用于网关)
    public void bindDevice(EConfigureNetwork.bindDeviceParameterEntry parameter,
                           Handler commitFailureHandler,
                           Handler responseErrorHandler,
                           Handler processDataHandler){
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_BINDDEVICE;
        requestParameterEntry.version = "1.0.8";
        requestParameterEntry.addParameter("homeId", parameter.homeId);
        requestParameterEntry.addParameter("productKey", parameter.productKey);
        requestParameterEntry.addParameter("deviceName", parameter.deviceName);
        requestParameterEntry.addParameter("token", parameter.token);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_BINDEVICE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 允许子设备入网
    public void permitJoinSubDevice(String gatewayId, String subDeviceProductKey, int duration,
                           Handler commitFailureHandler,
                           Handler responseErrorHandler,
                           Handler processDataHandler){
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_PERMITJOINSUBDEVICE;
        requestParameterEntry.version = "1.0.2";
        requestParameterEntry.addParameter("iotId", gatewayId);
        requestParameterEntry.addParameter("productKey", subDeviceProductKey);
        requestParameterEntry.addParameter("time", duration);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_PERMITJOINSUBDEVICE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 绑定子设备
    public void bindSubDevice(String homeId, String productKey, String subDeviceName,
                              Handler commitFailureHandler,
                              Handler responseErrorHandler,
                              Handler processDataHandler){
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_BINDSUBDEVICE;
        requestParameterEntry.version = "1.0.8";
        requestParameterEntry.addParameter("homeId", homeId);
        requestParameterEntry.addParameter("productKey", productKey);
        requestParameterEntry.addParameter("deviceName", subDeviceName);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_BINDSUBDEVICE;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 将接收到的数据加入缓存
    private void addDataToBuffer(byte[] frame) {
        short addLength = (short)frame.length;
        if((this.dataLength + addLength) > Constant.CONFIGNETWORK_BUFFER_MAXSIZE) {
            addLength = (short)(Constant.CONFIGNETWORK_BUFFER_MAXSIZE - this.dataLength);
            if(addLength <= 0) {
                return;
            }
        }
        System.arraycopy(frame, 0, this.buffer, this.dataLength, addLength);
        this.dataLength = (short)(this.dataLength + addLength);
    }

    // 移除缓存前面中的数据
    private void removeFrontDataFromBuffer(short length) {
        short newDataLength = (short)(this.dataLength - length);
        if(newDataLength <= 0){
            this.dataLength = 0;
        } else {
            System.arraycopy(this.buffer, length, this.buffer, 0, newDataLength);
            this.dataLength = newDataLength;
        }
    }
}
