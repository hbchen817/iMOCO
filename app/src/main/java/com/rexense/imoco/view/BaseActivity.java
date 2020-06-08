package com.rexense.imoco.view;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import java.util.Map;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.utility.Logger;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 基础界面
 */
public class BaseActivity extends Activity {
    // 提交失败处理器
    protected Handler mCommitFailureHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            if(Constant.MSG_CALLBACK_APICOMMITFAIL == msg.what) {
                EAPIChannel.commitFailEntry commitFailEntry = (EAPIChannel.commitFailEntry)msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%]失败:", commitFailEntry.path));
                if(commitFailEntry.parameters != null && commitFailEntry.parameters.size() > 0)
                {
                    for(Map.Entry<String, Object> entry : commitFailEntry.parameters.entrySet()){
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
                Logger.e(sb.toString());
                String exceptionInfo = commitFailEntry.exception != null ? commitFailEntry.exception.getMessage() : "";
                Toast.makeText(BaseActivity.this, String.format(getString(R.string.api_commitfailure), commitFailEntry.path, exceptionInfo), Toast.LENGTH_LONG).show();
            }
            return false;
        }
    });

    // 响应错误处理器
    protected Handler mResponseErrorHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            if(Constant.MSG_CALLBACK_APIRESPONSEERROR == msg.what) {
                EAPIChannel.responseErrorEntry responseErrorEntry = (EAPIChannel.responseErrorEntry)msg.obj;
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("提交接口[%s]成功, 但是响应发生错误:", responseErrorEntry.path));
                if(responseErrorEntry.parameters != null && responseErrorEntry.parameters.size() > 0)
                {
                    for(Map.Entry<String, Object> entry : responseErrorEntry.parameters.entrySet()){
                        sb.append(String.format("\r\n    %s : %s", entry.getKey(), entry.getValue().toString()));
                    }
                }
                sb.append(String.format("\r\n    exception message: %s", responseErrorEntry.message));
                sb.append(String.format("\r\n    exception local message: %s", responseErrorEntry.localizedMsg));
                Logger.e(sb.toString());
                Toast.makeText(BaseActivity.this, String.format(getString(R.string.api_responseerror), responseErrorEntry.path, responseErrorEntry.localizedMsg), Toast.LENGTH_LONG).show();
            }
            return false;
        }
    });
}