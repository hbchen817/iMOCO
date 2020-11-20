package com.xiezhu.jzj.sdk;

import android.os.Handler;
import android.os.Message;
import java.util.HashMap;
import java.util.Map;

import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.EAPIChannel;
import com.xiezhu.jzj.presenter.MocoApplication;
import com.xiezhu.jzj.utility.Logger;
import com.xiezhu.jzj.widget.DialogUtils;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: SDK API通道
 */
public class APIChannel {
    // 构造
    public APIChannel() {
    }

    // 提交
    public void commit(EAPIChannel.requestParameterEntry entry,
                       Handler commitFailureHandler,
                       Handler responseErrorHandler,
                       Handler processDataHandler) {
        if(entry.path == null || entry.path.length() == 0){
            Logger.e("The parameter[path] of APIChannel is not null!");
            return;
        }
        DialogUtils.showLoadingDialog(MocoApplication.sContext);

        Handler mCommitFailureHandler = commitFailureHandler;
        Handler mResponseErrorHandler = responseErrorHandler;
        Handler mProcessDataHandler = processDataHandler;
        int mCallbackMessageType = entry.callbackMessageType;

        // 构造请求参数
        IoTRequestBuilder requestBuilder = new IoTRequestBuilder();
        if(entry.scheme == null){
            requestBuilder.setScheme(Scheme.HTTPS);
        }else{
            requestBuilder.setScheme(entry.scheme);
        }
        if(entry.version == null || entry.version.length() == 0)
        {
            requestBuilder.setApiVersion("1.0.0");
        }else{
            requestBuilder.setApiVersion(entry.version);
        }
        if(entry.authType == null){
            requestBuilder.setAuthType("iotAuth");
        }else{
            requestBuilder.setAuthType(entry.authType);
        }
        requestBuilder.setPath(entry.path);
        if(entry.parameters != null && entry.parameters.size() > 0){
            requestBuilder.setParams(entry.parameters);
        }
        IoTRequest request = requestBuilder.build();

        // 获取Client实例并发送请求
        this.printfRequestInfo(request, "Started to call the method[send] of API channel, the request information", 1);
        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest request, Exception e) {
                DialogUtils.dismissLoadingDialog();
                Logger.e("Failed to submit the interface of API channel!\r\n");
                printfRequestInfo(request, "API channel failed callback returns request information", 3);
                Logger.e("The reason for the failure is:\r\n    Exception: " + e.toString());
                if(mCommitFailureHandler != null)
                {
                    EAPIChannel.commitFailEntry commitFailEntry = new EAPIChannel.commitFailEntry(e);
                    commitFailEntry.path = request.getPath();
                    commitFailEntry.version = request.getAPIVersion();
                    commitFailEntry.authType = request.getAuthType();
                    commitFailEntry.scheme = request.getScheme();
                    commitFailEntry.parameters = new HashMap<>();
                    if(request.getParams() != null && request.getParams().size() > 0){
                        for(Map.Entry<String, Object> entry : request.getParams().entrySet()){
                            commitFailEntry.parameters.put(entry.getKey(), entry.getValue());
                        }
                    }
                    Message msg = new Message();
                    msg.what = Constant.MSG_CALLBACK_APICOMMITFAIL;
                    msg.arg1 = mCallbackMessageType;
                    msg.obj = commitFailEntry;
                    mCommitFailureHandler.sendMessage(msg);
                }
            }

            @Override
            public void onResponse(IoTRequest request, IoTResponse response) {
                Logger.i("Successfully submitted the interface of API channel and got response.");
                DialogUtils.dismissLoadingDialog();
                // 返回失败数据处理
                if(Constant.API_CODE_SUCCESS != response.getCode()){
                    printfRequestInfo(request, "API channel response callback returns request information", 2);
                    String  warnInfo = "Server response returned failed data!\r\n    code: " + Integer.toString(response.getCode());
                    warnInfo = warnInfo + "\r\n    message: " + response.getMessage();
                    warnInfo = warnInfo + "\r\n    localizedMsg: " + response.getLocalizedMsg() ;
                    Logger.w(warnInfo);
                    if(mResponseErrorHandler != null){
                        EAPIChannel.responseErrorEntry responseErrorEntry = new EAPIChannel.responseErrorEntry();
                        responseErrorEntry.path = request.getPath();
                        responseErrorEntry.version = request.getAPIVersion();
                        responseErrorEntry.authType = request.getAuthType();
                        responseErrorEntry.scheme = request.getScheme();
                        responseErrorEntry.parameters = new HashMap<>();
                        responseErrorEntry.code = response.getCode();
                        responseErrorEntry.message = response.getMessage();
                        responseErrorEntry.localizedMsg = response.getLocalizedMsg();
                        if(request.getParams() != null && request.getParams().size() > 0){
                            for(Map.Entry<String, Object> entry : request.getParams().entrySet()){
                                responseErrorEntry.parameters.put(entry.getKey(), entry.getValue());
                            }
                        }
                        Message msg = new Message();
                        msg.what = Constant.MSG_CALLBACK_APIRESPONSEERROR;
                        msg.arg1 = mCallbackMessageType;
                        msg.obj = responseErrorEntry;
                        mResponseErrorHandler.sendMessage(msg);
                    }
                    return;
                }

                // 返回正确数据处理
                printfRequestInfo(request, "API channel response callback returns request information", 1);
                String data = response.getData().toString();
                Logger.i("The ALi Cloud Server returned correct data\r\n    data: " + data);
                if(mProcessDataHandler != null){
                    Message msg = new Message();
                    msg.what = mCallbackMessageType;
                    msg.obj = data;
                    mProcessDataHandler.sendMessage(msg);
                }
            }
        });
    }

    // 输出请求信息
    private void printfRequestInfo(IoTRequest request, String type, int displayType){
        String info = type + ":\r\n";
        info = info + "    host: " + request.getHost() + "\r\n";
        info = info + "    scheme: " + request.getScheme().toString() + "\r\n";
        info = info + "    path: " + request.getPath() + "\r\n";
        info = info + "    version: " + request.getAPIVersion() + "\r\n";
        info = info + "    authType: " + request.getAuthType() + "\r\n";
        info = info + "    Params:\r\n";
        Integer i = 1;
        for (Map.Entry<String, Object> entry: request.getParams().entrySet()) {
            info = info + "        " + i.toString() + ": " + entry.getKey() + " / " + entry.getValue().toString() + "\r\n";
            i++;
        }

        if(displayType == 1){
            Logger.i(info);
        }
        else if(displayType == 2){
            Logger.w(info);
        }
        else{
            Logger.e(info);
        }

    }
}

