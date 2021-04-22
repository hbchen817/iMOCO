package com.rexense.imoco.demoTest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.demoTest.ActionEntry;
import com.rexense.imoco.demoTest.CaConditionEntry;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.utility.ToastUtils;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DemoTestActivity extends AppCompatActivity {
    @BindView(R.id.one_button)
    QMUIRoundButton mOneButton;
    @BindView(R.id.two_button)
    QMUIRoundButton mTwoButton;
    @BindView(R.id.three_button)
    QMUIRoundButton mThreeButton;
    @BindView(R.id.four_button)
    QMUIRoundButton mFourButton;
    @BindView(R.id.five_button)
    QMUIRoundButton mFiveButton;
    @BindView(R.id.six_button)
    QMUIRoundButton mSixButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_test);

        ButterKnife.bind(this);
    }

    @OnClick({R.id.one_button, R.id.two_button, R.id.three_button, R.id.four_button, R.id.five_button,
            R.id.six_button})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.one_button: {
                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        CScene.TYPE_MANUAL, "每天 17：05 PowerSwitch_1 为 1", "b8ED2UkndPeYK7VO88AJ000000");

                CaConditionEntry entry = new CaConditionEntry();
                CaConditionEntry.Timer timer = new CaConditionEntry.Timer();
                timer.setCron("05 17 * * *");
                timer.setCronType("linux");
                timer.setTimezoneID("Asia/Shanghai");
                entry.getEntries().add(timer);

                ActionEntry actionEntry = new ActionEntry();
                ActionEntry.Property property = new ActionEntry.Property();
                property.setIotId("b8ED2UkndPeYK7VO88AJ000000");
                property.setPropertyName("PowerSwitch_1");
                property.setPropertyValue(1);
                actionEntry.getEntries().add(property);

                /*new SceneManager(DemoTestActivity.this).createCAAutoScene(baseInfoEntry, entry,
                        actionEntry, mCommitFailureHandler, mResponseErrorHandler, processDataHandler, true);*/
                break;
            }
            case R.id.two_button: {
                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        CScene.TYPE_MANUAL, "PowerSwitch_1 为 1 时 PowerSwitch_2 为 1", "b8ED2UkndPeYK7VO88AJ000000");

                CaConditionEntry caConditionEntry = new CaConditionEntry();
                CaConditionEntry.Property property = new CaConditionEntry.Property();
                property.setCompareType("==");
                property.setCompareValue(1);
                property.setDeviceName("CCCCCCFFFE93D9A1");
                property.setProductKey("a1pVHRVmHqD");
                property.setPropertyName("PowerSwitch_1");
                caConditionEntry.getEntries().add(property);

                ActionEntry actionEntry = new ActionEntry();
                ActionEntry.Property actionProperty = new ActionEntry.Property();
                actionProperty.setIotId("b8ED2UkndPeYK7VO88AJ000000");
                actionProperty.setPropertyName("PowerSwitch_2");
                actionProperty.setPropertyValue(1);
                actionEntry.getEntries().add(actionProperty);

                /*new SceneManager(DemoTestActivity.this).createCAAutoScene(baseInfoEntry, caConditionEntry,
                        actionEntry, mCommitFailureHandler, mResponseErrorHandler, processDataHandler, true);*/
                break;
            }
            case R.id.three_button: {
                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        CScene.TYPE_MANUAL, "PowerSwitch_2 为 1 时 调用-测试新建自动场景", "b8ED2UkndPeYK7VO88AJ000000");

                CaConditionEntry caConditionEntry = new CaConditionEntry();
                CaConditionEntry.Property caProperty = new CaConditionEntry.Property();
                caProperty.setPropertyName("PowerSwitch_2");
                caProperty.setProductKey("a1pVHRVmHqD");
                caProperty.setDeviceName("CCCCCCFFFE93D9A1");
                caProperty.setCompareType("==");
                caProperty.setCompareValue(1);
                caConditionEntry.getEntries().add(caProperty);

                ActionEntry actionEntry = new ActionEntry();
                ActionEntry.Trigger actionTrigger = new ActionEntry.Trigger();
                actionTrigger.setSceneId("3a00569f20e64632b0d613169935b55e");
                actionEntry.getEntries().add(actionTrigger);

                /*new SceneManager(DemoTestActivity.this).createCAAutoScene(baseInfoEntry, caConditionEntry, actionEntry,
                        mCommitFailureHandler, mResponseErrorHandler, processDataHandler, true);*/
                break;
            }
            case R.id.four_button: {
                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        CScene.TYPE_MANUAL, "在某个时间段内，PowerSwitch_1 为 1 时禁止调动PowerSwitch_2 为 1", "b8ED2UkndPeYK7VO88AJ000000");

                CaConditionEntry caConditionEntry = new CaConditionEntry();
                CaConditionEntry.Property caProperty = new CaConditionEntry.Property();
                caProperty.setCompareValue(1);
                caProperty.setCompareType("==");
                caProperty.setDeviceName("CCCCCCFFFE93D9A1");
                caProperty.setProductKey("a1pVHRVmHqD");
                caProperty.setPropertyName("PowerSwitch_1");

                CaConditionEntry.TimeRange caTimeRange = new CaConditionEntry.TimeRange();
                caTimeRange.setFormat("HH:mm");
                caTimeRange.setBeginDate("19:10");
                caTimeRange.setEndDate("19:15");
                caTimeRange.setRepeat("1,2,3,4,5");
                caConditionEntry.getEntries().add(caProperty);
                caConditionEntry.getEntries().add(caTimeRange);

                ActionEntry actionEntry = new ActionEntry();
                ActionEntry.Property actionProperty = new ActionEntry.Property();
                actionProperty.setIotId("b8ED2UkndPeYK7VO88AJ000000");
                actionProperty.setPropertyName("PowerSwitch_2");
                actionProperty.setPropertyValue(1);
                actionEntry.getEntries().add(actionProperty);

                /*new SceneManager(DemoTestActivity.this).createCAAutoScene(baseInfoEntry, caConditionEntry, actionEntry,
                        mCommitFailureHandler, mResponseErrorHandler, processDataHandler, false);*/
                break;
            }
            case R.id.five_button:{
                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        CScene.TYPE_MANUAL, "PowerSwitch_1 为 1 时，小网关门铃", "b8ED2UkndPeYK7VO88AJ000000");

                CaConditionEntry caConditionEntry = new CaConditionEntry();
                CaConditionEntry.Property caProperty = new CaConditionEntry.Property();
                caProperty.setProductKey("a1pVHRVmHqD");
                caProperty.setDeviceName("CCCCCCFFFE93D9A1");
                caProperty.setPropertyName("PowerSwitch_1");
                caProperty.setCompareType("==");
                caProperty.setCompareValue(1);
                caConditionEntry.getEntries().add(caProperty);

                JSONObject o = new JSONObject();
                o.put("InvokeVoice",0);


                ActionEntry actionEntry = new ActionEntry();
                ActionEntry.InvokeService invokeService = new ActionEntry.InvokeService();
                invokeService.setIotId("f0yTFrd3PEW8ftp9iTS0000100");
                invokeService.setServiceName("InvokeMode");
                invokeService.setServiceArgs(o);
                actionEntry.getEntries().add(invokeService);

                new SceneManager(DemoTestActivity.this).createCAScene(baseInfoEntry, true, "any", caConditionEntry.getEntries(),
                        actionEntry.getEntries(), mCommitFailureHandler, mResponseErrorHandler, processDataHandler);
                break;
            }
            case R.id.six_button:{
                EScene.sceneBaseInfoEntry baseInfoEntry = new EScene.sceneBaseInfoEntry(SystemParameter.getInstance().getHomeId(),
                        CScene.TYPE_MANUAL, "PowerSwitch_1 为 1 时，禁止PowerSwitch_2的场景", "b8ED2UkndPeYK7VO88AJ000000");

                CaConditionEntry caConditionEntry = new CaConditionEntry();
                CaConditionEntry.Property caProperty = new CaConditionEntry.Property();
                caProperty.setProductKey("a1pVHRVmHqD");
                caProperty.setDeviceName("CCCCCCFFFE93D9A1");
                caProperty.setPropertyName("PowerSwitch_1");
                caProperty.setCompareType("==");
                caProperty.setCompareValue(1);
                caConditionEntry.getEntries().add(caProperty);

                ActionEntry actionEntry = new ActionEntry();
                ActionEntry.SetSwitch setSwitch = new ActionEntry.SetSwitch();
                setSwitch.setRuleId("6d3e7ae5b6784317bb50315661f4d6dc");
                setSwitch.setSwitchStatus(0);
                actionEntry.getEntries().add(setSwitch);

                /*new SceneManager(DemoTestActivity.this).createCAAutoScene(baseInfoEntry, caConditionEntry, actionEntry,
                        mCommitFailureHandler, mResponseErrorHandler, processDataHandler, true);*/
                break;
            }
        }
    }

    private Handler mCommitFailureHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ViseLog.d("mCommitFailureHandler\n" + new Gson().toJson(msg));
        }
    };

    private Handler mResponseErrorHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ViseLog.d("mResponseErrorHandler\n" + new Gson().toJson(msg));
            JSONObject o = JSONObject.parseObject(new Gson().toJson(msg.obj));
            ToastUtils.showLongToast(DemoTestActivity.this, o.getString("localizedMsg"));
        }
    };

    private Handler processDataHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ViseLog.d("processDataHandler\n" + new Gson().toJson(msg));
            switch (msg.what){
                case 122:{
                    ToastUtils.showLongToast(DemoTestActivity.this, "新增成功！");
                    break;
                }
            }
        }
    };

}