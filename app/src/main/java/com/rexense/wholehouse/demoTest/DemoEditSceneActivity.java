package com.rexense.wholehouse.demoTest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.model.EDevice;
import com.rexense.wholehouse.model.ItemAction;
import com.rexense.wholehouse.presenter.SceneManager;
import com.rexense.wholehouse.presenter.UserCenter;
import com.rexense.wholehouse.utility.ToastUtils;
import com.rexense.wholehouse.view.SceneSwitchDeviceListActivity;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;
import com.vise.log.ViseLog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DemoEditSceneActivity extends AppCompatActivity {
    @BindView(R.id.update_button)
    QMUIRoundButton mUpdateBtn;
    @BindView(R.id.del_button)
    QMUIRoundButton mDelBtn;
    @BindView(R.id.scene_name)
    EditText mSceneName;
    @BindView(R.id.time_select)
    TextView mTimeSelectTV;
    @BindView(R.id.time_check)
    CheckBox mTimeCheck;
    @BindView(R.id.dev_select_tv)
    TextView mDevSelectTV;
    @BindView(R.id.dev_property_tv)
    TextView mDevPropertyTV;
    @BindView(R.id.dev_value_tv)
    TextView mDevValueTV;
    @BindView(R.id.dev_check)
    CheckBox mDevCheck;

    private String mId;
    private String mCatalogId;

    private SceneManager mSceneManager;
    private UserCenter mUserCenter;

    private String mTimer;
    private boolean mTimerChecked = false;
    private boolean mDevChecked = false;
    private String mDevSelectIotId = "";

    private List<EDevice.deviceEntry> mDevEntries = new ArrayList<>();
    private List<ItemAction> mItemActions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_edit_scene);
        ButterKnife.bind(this);

        mCommitFailureHandler = new CommitFailureHandler(this);
        mResponseErrorHandler = new ResponseErrorHandler(this);

        mId = getIntent().getStringExtra("id");
        mCatalogId = getIntent().getStringExtra("catalogId");

        mSceneManager = new SceneManager(this);
        mSceneManager.querySceneDetail(mId, mCatalogId, mCommitFailureHandler, mResponseErrorHandler, mProcessDataHandler);

        mUserCenter = new UserCenter(this);

        mTimeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mTimerChecked = isChecked;
            }
        });
        mDevCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDevChecked = isChecked;
            }
        });
    }

    @OnClick({R.id.update_button, R.id.del_button,R.id.time_select,R.id.dev_select_tv,R.id.dev_property_tv,R.id.dev_value_tv})
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dev_value_tv:{
                break;
            }
            case R.id.dev_property_tv:{
                mSceneManager.getDeviceAction(mDevSelectIotId,mCommitFailureHandler, mResponseErrorHandler, mProcessDataHandler);
                break;
            }
            case R.id.dev_select_tv:{
                //mUserCenter.getDeviceList(1,100,mCommitFailureHandler, mResponseErrorHandler, mProcessDataHandler);
                /*mDevEntries.clear();
                Map<String, EDevice.deviceEntry> entryMap = DeviceBuffer.getAllDeviceInformation();
                Iterator<Map.Entry<String, EDevice.deviceEntry>> entries = entryMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, EDevice.deviceEntry> entry = entries.next();
                    mDevEntries.add(entry.getValue());
                }

                QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(DemoEditSceneActivity.this);
                for (int i=0;i<mDevEntries.size();i++){
                    builder.addItem(mDevEntries.get(i).nickName);
                }
                builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        mDevSelectIotId = mDevEntries.get(position).iotId;
                        mDevSelectTV.setText(mDevEntries.get(position).nickName);
                        mDevPropertyTV.setText("请选择action");
                        mDevValueTV.setText("请选择action");
                        dialog.dismiss();
                    }
                });
                builder.build().show();*/

                SceneSwitchDeviceListActivity.start(this);
                break;
            }
            case R.id.update_button: {
                break;
            }
            case R.id.del_button: {
                ViseLog.d("删除场景");
                mSceneManager.deleteScene(mId, mCommitFailureHandler, mResponseErrorHandler, mProcessDataHandler);
                break;
            }
            case R.id.time_select:{
                String[] timers = mTimer.split(" ");
                int hour = Integer.parseInt(timers[1]);
                int min = Integer.parseInt(timers[0]);
                TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        ViseLog.d("hourOfDay = "+hourOfDay+" , minute = "+minute);
                        String min = "";
                        if (minute<10) min = "0"+minute;
                        else min = minute+"";
                        String hour = "";
                        if (hourOfDay<10) hour = "0"+hourOfDay;
                        else hour = hourOfDay+"";

                        mTimer = min+" "+hour+" * * *";
                        mTimeSelectTV.setText(hour+":"+min);
                    }
                }, hour,min,true);
                dialog.show();
                break;
            }
        }
    }

    private CommitFailureHandler mCommitFailureHandler;
    private ResponseErrorHandler mResponseErrorHandler;

    private class CommitFailureHandler extends Handler{
        private WeakReference<Activity> mWeakRF;

        public CommitFailureHandler(Activity activity){
            mWeakRF = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mWeakRF.get() == null) return;
            ViseLog.d("CommitFailureHandler\n" + new Gson().toJson(msg));
        }
    }

    private class ResponseErrorHandler extends Handler{
        private WeakReference<Activity> mWeakRF;

        public ResponseErrorHandler(Activity activity){
            mWeakRF = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mWeakRF.get() == null) return;
            ViseLog.d("ResponseErrorHandler\n" + new Gson().toJson(msg));
        }
    }

    private Handler mProcessDataHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            ViseLog.d("processDataHandler\n" + new Gson().toJson(msg));
            switch (msg.what) {
                case Constant.MSG_CALLBACK_QUERYSCENEDETAIL: {
                    JSONObject o = JSONObject.parseObject((String) msg.obj);
                    ViseLog.d("processDataHandler MSG_CALLBACK_QUERYSCENEDETAIL\n" + o.toJSONString());
                    mSceneName.setText(o.getString("name"));
                    mSceneName.setSelection(mSceneName.getText().toString().length());

                    JSONArray jsonArray = JSONArray.parseArray(o.getString("caConditionsJson"));
                    JSONObject o1 = JSONObject.parseObject((String)jsonArray.get(0));
                    ViseLog.d("caConditionsJson = "+o1.toJSONString());

                    String uri = o1.getString("uri");
                    if ("condition/timer".equals(uri)) {
                        mTimerChecked = true;
                        mTimeCheck.setChecked(mTimerChecked);

                        mTimer = o1.getJSONObject("params").getString("cron");
                        String[] timers = mTimer.split(" ");
                        String timeHour = timers[1];
                        if (timers[1].length() == 1) timeHour = "0" + timers[1];
                        String timeMin = timers[0];
                        if (timers[0].length() == 1) timeMin = "0" + timers[1];
                        mTimeSelectTV.setText(timeHour + ":" + timeMin);
                    }

                    jsonArray = JSONArray.parseArray(o.getString("actionsJson"));
                    o1 = JSONObject.parseObject((String)jsonArray.get(0));
                    ViseLog.d("actionsJson = "+o1.toJSONString());

                    uri = o1.getString("uri");
                    if ("action/device/setProperty".equals(uri)) {
                        mDevChecked = true;
                        mDevCheck.setChecked(mDevChecked);
                        String nickName = o1.getJSONObject("params").getString("deviceNickName");
                        mDevSelectTV.setText(nickName);
                        String propertyName = o1.getJSONObject("params").getString("propertyName");
                        mDevPropertyTV.setText(propertyName);
                        String propertyValue = "";
                        if (o1.getJSONObject("params").get("propertyValue") instanceof String)
                            propertyValue = o1.getJSONObject("params").getString("propertyValue");
                        else if (o1.getJSONObject("params").get("propertyValue") instanceof Integer){
                            int i = (int) o1.getJSONObject("params").get("propertyValue");
                            propertyValue = String.valueOf(i);
                        }
                        mDevValueTV.setText(propertyValue);
                    }
                    break;
                }
                case Constant.MSG_CALLBACK_DELETESCENE:{
                    ToastUtils.showLongToast(DemoEditSceneActivity.this,"删除成功！");
                    finish();
                    break;
                }
                case Constant.MSG_CALLBACK_GETUSERDEVICTLIST:{
                    // 处理获取用户设备列表数据

                    break;
                }
                case Constant.MSG_CALLBACK_SCENE_ABILITY_TSL:{
                    mItemActions.clear();
                    JSONObject jsonObject = JSON.parseObject((String) msg.obj);
                    JSONArray simplifyAbilityDTOs = jsonObject.getJSONArray("simplifyAbilityDTOs");
                    JSONObject abilityDsl = jsonObject.getJSONObject("abilityDsl");
                    JSONArray services = abilityDsl.getJSONArray("services");
                    JSONArray properties = abilityDsl.getJSONArray("properties");
                    JSONArray events = abilityDsl.getJSONArray("events");
                    int size = simplifyAbilityDTOs.size();
                    for (int i = 0; i < size; i++) {
                        JSONObject ability = simplifyAbilityDTOs.getJSONObject(i);
                        int type = ability.getIntValue("type");//功能类型：1-属性；2-服务；3-事件
                        switch (type) {
                            case 1:
                                int propertiesSize = properties.size();
                                for (int j = 0; j < propertiesSize; j++) {
                                    JSONObject property = properties.getJSONObject(j);
                                    if (property.getString("identifier").equals(ability.getString("identifier"))) {
                                        JSONObject dataType = property.getJSONObject("dataType");
                                        String dataTypeValue = dataType.getString("type");
                                        JSONObject specs = dataType.getJSONObject("specs");
                                        switch (dataTypeValue) {
                                            case "enum":
                                            case "bool":
                                                for (Map.Entry<String, Object> map : specs.entrySet()) {
                                                    ItemAction<String> itemAction = new ItemAction<String>();
                                                    itemAction.setActionName(property.getString("name").trim());
                                                    itemAction.setIdentifier(property.getString("identifier").trim());
                                                    itemAction.setActionKey((String) map.getValue());
                                                    itemAction.setActionValue(map.getKey());
                                                    itemAction.setIotId(mDevSelectIotId);
                                                    itemAction.setDeviceName(mDevSelectTV.getText().toString());
                                                    itemAction.setProductKey(abilityDsl.getJSONObject("profile").getString("productKey").trim());
                                                    mItemActions.add(itemAction);
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            default:
                                break;
                        }
                    }

                    QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(DemoEditSceneActivity.this);
                    for (int i=0;i<mItemActions.size();i++){
                        builder.addItem(mItemActions.get(i).getActionName());
                    }
                    builder.setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                        @Override
                        public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                            dialog.dismiss();
                        }
                    }).build().show();
                    break;
                }
            }
        }
    };
}