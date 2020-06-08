package com.rexense.imoco.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.CTSL;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.EProduct;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.utility.Logger;

/**
 * Creator: xieshaobing
 * creat time: 2020-05-14 15:29
 * Description: 场景管理器
 */
public class SceneManager {
    private Context mContext;

    // 构造
    public SceneManager(Context context) {
        this.mContext = context;
    }

    // 获取测试数据
    public List<EScene.sceneListItemEntry> getTestData() {
        List<EScene.sceneListItemEntry> list = new ArrayList<EScene.sceneListItemEntry>();
        for(int i = 1; i <= 10; i++) {
            EScene.sceneListItemEntry entry = new EScene.sceneListItemEntry();
            entry.name = "一键执行场景" + i;
            list.add(entry);
        }
        return list;
    }

    // 获取场景列表
    public void getSceneList(String groupId, int pageNo, int pageSize,
                            Handler commitFailureHandler,
                            Handler responseErrorHandler,
                            Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler is not null!");
            return;
        }

        // 设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETSCENELIST;
        requestParameterEntry.version = "1.0.5";
        requestParameterEntry.addParameter("groupId", groupId);
        requestParameterEntry.addParameter("pageNo", pageNo < 1 ? 1 : pageNo);
        requestParameterEntry.addParameter("pageSize", pageSize <= 0  || pageSize > 50 ? 50 : pageSize);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETSCENELIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 生成场景模板列表
    public List<EScene.sceneModelEntry> genSceneModelList() {
        List<EScene.sceneModelEntry> list = new ArrayList<EScene.sceneModelEntry>();
        list.add(new EScene.sceneModelEntry(CScene.SMC_NONE, R.string.scenemodel_recommend, R.drawable.background_null));
        list.add(new EScene.sceneModelEntry(CScene.SMC_NIGHT_RISE_ON, R.string.scenemodel_night_rise_on, R.drawable.scen_background1));
        list.add(new EScene.sceneModelEntry(CScene.SMC_UNMANNED_OFF, R.string.scenemodel_unmanned_off, R.drawable.scen_background2));
        list.add(new EScene.sceneModelEntry(CScene.SMC_ALARM_ON, R.string.scenemodel_alarm_on, R.drawable.scen_background3));
        list.add(new EScene.sceneModelEntry(CScene.SMC_REMOTE_CONTROL_ON, R.string.scenemodel_remote_control_on, R.drawable.scen_background4));
        list.add(new EScene.sceneModelEntry(CScene.SMC_OPEN_DOOR_ON, R.string.scenemodel_open_door_on, R.drawable.scen_background5));
        list.add(new EScene.sceneModelEntry(CScene.SMC_BELL_PLAY, R.string.scenemodel_bell_play, R.drawable.scen_background6));
        list.add(new EScene.sceneModelEntry(CScene.SMC_ALARM_PLAY, R.string.scenemodel_alarm_play, R.drawable.scen_background7));
        list.add(new EScene.sceneModelEntry(CScene.SMC_PIR_DEPLOY_ALARM, R.string.scenemodel_pir_deploy_alarm, R.drawable.scen_background8));
        list.add(new EScene.sceneModelEntry(CScene.SMC_DOOR_DEPLOY_ALARM, R.string.scenemodel_door_deploy_alarm, R.drawable.scen_background9));
        list.add(new EScene.sceneModelEntry(CScene.SMC_NONE, R.string.scenemodel_one_key, R.drawable.background_null));
        list.add(new EScene.sceneModelEntry(CScene.SMC_GO_HOME_PATTERN, R.string.scenemodel_go_home_pattern, R.drawable.scen_background10));
        list.add(new EScene.sceneModelEntry(CScene.SMC_LEAVE_HOME_PATTERN, R.string.scenemodel_leave_home_pattern, R.drawable.scen_background11));
        list.add(new EScene.sceneModelEntry(CScene.SMC_SLEEP_PATTERN, R.string.scenemodel_sleep_pattern, R.drawable.scen_background12));
        list.add(new EScene.sceneModelEntry(CScene.SMC_GETUP_PATTERN, R.string.scenemodel_getup_pattern, R.drawable.scen_background13));
        return list;
    }

    // 获取指定场景模板的触发器设备
    public List<EScene.triggerEntry> getTrigger(int sceneModelCode, List<EProduct.configListEntry> productList){
        List<EScene.triggerEntry> list = new ArrayList<EScene.triggerEntry>();
        switch (sceneModelCode){
            case CScene.SMC_NIGHT_RISE_ON:
                // 起夜开灯场景处理
                // 1.获取PIR
                Map<String, EDevice.deviceEntry> pir = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_PIRSENSOR);
                if(pir == null || pir.size() == 0){
                    EScene.triggerEntry triggerEntry = new EScene.triggerEntry();
                    triggerEntry.productKey = CTSL.PK_PIRSENSOR;
                    for(EProduct.configListEntry product : productList){
                        if(product.productKey.equalsIgnoreCase(CTSL.PK_PIRSENSOR)){
                            triggerEntry.name = product.name;
                            break;
                        }
                    }
                    triggerEntry.state = CodeMapper.getPropertyTriggerState(this.mContext, CTSL.PK_PIRSENSOR, CScene.SMC_NIGHT_RISE_ON);
                    list.add(triggerEntry);
                } else {
                    for(EDevice.deviceEntry entry : pir.values()){
                        EScene.triggerEntry triggerEntry = new EScene.triggerEntry();
                        triggerEntry.productKey = CTSL.PK_PIRSENSOR;
                        triggerEntry.iotId = entry.iotId;
                        triggerEntry.name = entry.nickName;
                        triggerEntry.state = CodeMapper.getPropertyTriggerState(this.mContext, CTSL.PK_PIRSENSOR, CScene.SMC_NIGHT_RISE_ON);
                        list.add(triggerEntry);
                    }
                }
                // 2.获取门磁
                Map<String, EDevice.deviceEntry> door = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_DOORSENSOR);
                if(door == null || door.size() == 0){
                    EScene.triggerEntry triggerEntry = new EScene.triggerEntry();
                    triggerEntry.productKey = CTSL.PK_DOORSENSOR;
                    for(EProduct.configListEntry product : productList){
                        if(product.productKey.equalsIgnoreCase(CTSL.PK_DOORSENSOR)){
                            triggerEntry.name = product.name;
                            break;
                        }
                    }
                    triggerEntry.state = CodeMapper.getPropertyTriggerState(this.mContext, CTSL.PK_DOORSENSOR, CScene.SMC_NIGHT_RISE_ON);
                    list.add(triggerEntry);
                } else {
                    for(EDevice.deviceEntry entry : door.values()){
                        EScene.triggerEntry triggerEntry = new EScene.triggerEntry();
                        triggerEntry.productKey = CTSL.PK_DOORSENSOR;
                        triggerEntry.iotId = entry.iotId;
                        triggerEntry.name = entry.nickName;
                        triggerEntry.state = CodeMapper.getPropertyTriggerState(this.mContext, CTSL.PK_DOORSENSOR, CScene.SMC_NIGHT_RISE_ON);
                        list.add(triggerEntry);
                    }
                }
                break;
            default:
                break;
        }

        return list;
    }

    // 获取指定场景模板的响应设备
    public List<EScene.responseEntry> getResponse(int sceneModelCode){
        List<EScene.responseEntry> list = new ArrayList<EScene.responseEntry>();
        switch (sceneModelCode){
            case CScene.SMC_NIGHT_RISE_ON:
                // 起夜开灯场景处理
                // 1.获取一键单火开关
                Map<String, EDevice.deviceEntry> oneSwitch = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_ONEWAYSWITCH);
                if(oneSwitch == null || oneSwitch.size() == 0){
                    EScene.responseEntry responseEntry = new EScene.responseEntry();
                    responseEntry.productKey = CTSL.PK_ONEWAYSWITCH;
                    list.add(responseEntry);
                } else {
                    for(EDevice.deviceEntry entry : oneSwitch.values()){
                        EScene.responseEntry responseEntry = new EScene.responseEntry();
                        responseEntry.productKey = CTSL.PK_ONEWAYSWITCH;
                        responseEntry.iotId = entry.iotId;
                        responseEntry.name = entry.nickName;
                        list.add(responseEntry);
                    }
                }
                // 2.获取两键单火开关
                Map<String, EDevice.deviceEntry> twoSwitch = DeviceBuffer.getSameTypeDeviceInformation(CTSL.PK_TWOWAYSWITCH);
                if(twoSwitch == null || twoSwitch.size() == 0){
                    EScene.responseEntry responseEntry = new EScene.responseEntry();
                    responseEntry.productKey = CTSL.PK_TWOWAYSWITCH;
                    list.add(responseEntry);
                } else {
                    for(EDevice.deviceEntry entry : twoSwitch.values()){
                        EScene.responseEntry responseEntry = new EScene.responseEntry();
                        responseEntry.productKey = CTSL.PK_TWOWAYSWITCH;
                        responseEntry.iotId = entry.iotId;
                        responseEntry.name = entry.nickName;
                        list.add(responseEntry);
                    }
                }
                break;
            default:
                break;
        }

        return list;
    }
}
