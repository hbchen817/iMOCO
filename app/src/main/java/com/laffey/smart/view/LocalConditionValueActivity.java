package com.laffey.smart.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.laffey.smart.R;
import com.laffey.smart.contract.CTSL;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityEditPropertyValueBinding;
import com.laffey.smart.model.ECondition;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.SceneManager;
import com.laffey.smart.utility.QMUITipDialogUtil;
import com.laffey.smart.model.ERetrofit;
import com.laffey.smart.utility.ToastUtils;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LocalConditionValueActivity extends BaseActivity {
    private ActivityEditPropertyValueBinding mViewBinding;

    private static final String IDENTIFIER_ITEM = "identifier_item";
    private static final String IOT_ID = "iot_id";
    private static final String IDENTIFIER_NAME = "identifier_name";
    private static final String PRODUCT_KEY = "product_key";
    private static final String CONDITION_TYPE = "condition_type";
    private static final String KEY_NAME = "key_name";
    private static final String ENDPOINT_ID = "endpoint_id";

    private static final int MIN_TEMP = 16;
    private static final int MAX_TEMP = 32;

    //private IdentifierItemForCA mIdentifier;
    private EventValue mEventValue;
    private List<String> mEventValueList = new ArrayList<>();

    private SceneManager mSceneManager;
    //private CallbackHandler mHandler;

    private List<PropertyValue> mList;
    private BaseQuickAdapter<PropertyValue, BaseViewHolder> mAdapter;

    private String[] mCompareTypes;

    private boolean isUpate = true;

    private Typeface mIconfont;

    private String mDevMac;// 设备实际mac地址
    private String mDevIotId;
    private String mIdentifierName;
    private String mProductKey;
    private String mConditionType;
    private String mKeyName;
    private String mEndId;

    private ECondition mECondition;

    private boolean mIsRun = true;

    public static void start(Activity activity, LocalConditionIdentifierActivity.Identifier identifier) {
        Intent intent = new Intent(activity, LocalConditionValueActivity.class);
        intent.putExtra(IDENTIFIER_ITEM, identifier);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityEditPropertyValueBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);

        mCompareTypes = new String[]{"<", "<=", "==", ">=", ">", "!="};

        mViewBinding.compareTypeWv.setCurrentIndex(2);
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        initStatusBar();
        initRecyclerView();
        initData();
    }

    private void initData() {
        mSceneManager = new SceneManager(this);

        //requestMacByIot("chengxunfei", mDevIotId);
        if (CTSL.PK_ONE_SCENE_SWITCH.equals(mProductKey)) {
            // 一键场景开关
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String keyNickName = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_1);
            if (keyNickName == null || keyNickName.length() == 0) {
                keyNickName = getString(R.string.key_0);
            }

            String value = mECondition.getCondition().getParameters().getCompareValue();
            if ("1".equals(value)) {
                PropertyValue p = new PropertyValue(keyNickName, "1");
                p.setChecked(true);
            } else
                mList.add(new PropertyValue(keyNickName, "1"));
        } else if (CTSL.PK_TWO_SCENE_SWITCH.equals(mProductKey)) {
            // 二键场景开关
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mECondition.getCondition().getParameters().getCompareValue();
            String keyNickName1 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_1);
            if (keyNickName1 == null || keyNickName1.length() == 0) {
                keyNickName1 = getString(R.string.key_1);
            }
            String keyNickName2 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_2);
            if (keyNickName2 == null || keyNickName2.length() == 0) {
                keyNickName2 = getString(R.string.key_2);
            }
            PropertyValue p1 = new PropertyValue(keyNickName1, "1");
            PropertyValue p2 = new PropertyValue(keyNickName2, "2");
            if ("1".equals(value)) {
                p1.setChecked(true);
                mList.add(p1);
            } else if ("2".equals(value)) {
                p2.setChecked(true);
                mList.add(p2);
            } else {
                mList.add(p1);
                mList.add(p2);
            }
        } else if (CTSL.PK_THREE_SCENE_SWITCH.equals(mProductKey)) {
            // 三键场景开关
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String keyNickName1 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_1);
            if (keyNickName1 == null || keyNickName1.length() == 0) {
                keyNickName1 = getString(R.string.key_1);
            }
            String keyNickName2 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_2);
            if (keyNickName2 == null || keyNickName2.length() == 0) {
                keyNickName2 = getString(R.string.key_2);
            }
            String keyNickName3 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_3);
            if (keyNickName3 == null || keyNickName3.length() == 0) {
                keyNickName3 = getString(R.string.key_3);
            }

            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(keyNickName1, "1");
            PropertyValue p2 = new PropertyValue(keyNickName2, "2");
            PropertyValue p3 = new PropertyValue(keyNickName3, "3");
            if ("1".equals(value)) {
                p1.setChecked(true);
            } else if ("2".equals(value)) {
                p2.setChecked(true);
            } else if ("3".equals(value)) {
                p3.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
            mList.add(p3);
        } else if (CTSL.PK_FOUR_SCENE_SWITCH.equals(mProductKey)) {
            // 四键场景开关
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String keyNickName1 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_1);
            if (keyNickName1 == null || keyNickName1.length() == 0) {
                keyNickName1 = getString(R.string.key_1);
            }

            String keyNickName2 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_2);
            if (keyNickName2 == null || keyNickName2.length() == 0) {
                keyNickName2 = getString(R.string.key_2);
            }

            String keyNickName3 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_3);
            if (keyNickName3 == null || keyNickName3.length() == 0) {
                keyNickName3 = getString(R.string.key_3);
            }

            String keyNickName4 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_4);
            if (keyNickName4 == null || keyNickName4.length() == 0) {
                keyNickName4 = getString(R.string.key_4);
            }

            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(keyNickName1, "1");
            PropertyValue p2 = new PropertyValue(keyNickName2, "2");
            PropertyValue p3 = new PropertyValue(keyNickName3, "3");
            PropertyValue p4 = new PropertyValue(keyNickName4, "4");
            if ("1".equals(value)) {
                p1.setChecked(true);
            } else if ("2".equals(value)) {
                p2.setChecked(true);
            } else if ("3".equals(value)) {
                p3.setChecked(true);
            } else if ("4".equals(value)) {
                p4.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
            mList.add(p3);
            mList.add(p4);
        } else if (CTSL.PK_SIX_SCENE_SWITCH.equals(mProductKey)) {
            // 六键场景开关
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String keyNickName1 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_1);
            if (keyNickName1 == null || keyNickName1.length() == 0) {
                keyNickName1 = getString(R.string.key_1);
            }

            String keyNickName2 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_2);
            if (keyNickName2 == null || keyNickName2.length() == 0) {
                keyNickName2 = getString(R.string.key_2);
            }

            String keyNickName3 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_3);
            if (keyNickName3 == null || keyNickName3.length() == 0) {
                keyNickName3 = getString(R.string.key_3);
            }

            String keyNickName4 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_4);
            if (keyNickName4 == null || keyNickName4.length() == 0) {
                keyNickName4 = getString(R.string.key_4);
            }

            String keyNickName5 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_5);
            if (keyNickName5 == null || keyNickName5.length() == 0) {
                keyNickName5 = getString(R.string.key_5);
            }

            String keyNickName6 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_6);
            if (keyNickName6 == null || keyNickName6.length() == 0) {
                keyNickName6 = getString(R.string.key_6);
            }

            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(keyNickName1, "1");
            PropertyValue p2 = new PropertyValue(keyNickName2, "2");
            PropertyValue p3 = new PropertyValue(keyNickName3, "3");
            PropertyValue p4 = new PropertyValue(keyNickName4, "4");
            PropertyValue p5 = new PropertyValue(keyNickName5, "5");
            PropertyValue p6 = new PropertyValue(keyNickName6, "6");
            if ("1".equals(value)) {
                p1.setChecked(true);
            } else if ("2".equals(value)) {
                p2.setChecked(true);
            } else if ("3".equals(value)) {
                p3.setChecked(true);
            } else if ("4".equals(value)) {
                p4.setChecked(true);
            } else if ("5".equals(value)) {
                p5.setChecked(true);
            } else if ("6".equals(value)) {
                p6.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
            mList.add(p3);
            mList.add(p4);
            mList.add(p5);
            mList.add(p6);
        } else if (CTSL.TEST_PK_ONEWAYWINDOWCURTAINS.equals(mProductKey) ||
                CTSL.TEST_PK_TWOWAYWINDOWCURTAINS.equals(mProductKey)) {
            // 单路、双路窗帘面板
            mViewBinding.valueRv.setVisibility(View.VISIBLE);

            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(getString(R.string.stop), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.open), "1");
            PropertyValue p3 = new PropertyValue(getString(R.string.close), "2");

            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            } else if ("2".equals(value)) {
                p3.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
            mList.add(p3);
        } else if (CTSL.PK_ONEWAYSWITCH.equals(mProductKey) ||
                CTSL.PK_TWOWAYSWITCH.equals(mProductKey) ||
                CTSL.PK_THREE_KEY_SWITCH.equals(mProductKey) ||
                CTSL.PK_FOURWAYSWITCH_2.equals(mProductKey)) {
            // 一键、二键、三键、四键面板开关
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.open), "1");
            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
        } else if (CTSL.PK_SIX_TWO_SCENE_SWITCH.equals(mProductKey)) {
            // 六键四开二场景开关
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            if ("Event".equals(mConditionType)) {
                String keyNickName5 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_5);
                if (keyNickName5 == null || keyNickName5.length() == 0) {
                    keyNickName5 = getString(R.string.key_5);
                }
                String keyNickName6 = DeviceBuffer.getExtendedInfo(mDevIotId).getString(CTSL.SCENE_SWITCH_KEY_CODE_6);
                if (keyNickName6 == null || keyNickName6.length() == 0) {
                    keyNickName6 = getString(R.string.key_6);
                }

                String value = mECondition.getCondition().getParameters().getCompareValue();
                PropertyValue p1 = new PropertyValue(keyNickName5, "5");
                PropertyValue p2 = new PropertyValue(keyNickName6, "6");
                if ("5".equals(value)) {
                    p1.setChecked(true);
                } else if ("6".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if ("State".equals(mConditionType)) {
                String value = mECondition.getCondition().getParameters().getCompareValue();
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "1");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("1".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            }
        } else if (CTSL.PK_OUTLET.equals(mProductKey)) {
            // 二三极插座
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.open), "1");
            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
        } else if (CTSL.PK_AIRCOMDITION_TWO.equals(mProductKey)) {
            // 空调二管制
            if ("WorkMode".equals(mKeyName)) {
                // 开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mECondition.getCondition().getParameters().getCompareValue();
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "10");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("10".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if ("Temperature".equals(mKeyName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 当前温度
                mEventValueList.clear();
                // mCompareTypes = new String[]{"<", "<=", "==", ">=", ">", "!="};
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String symbol = mECondition.getCondition().getParameters().getCompareType();
                for (int i = 0; i < mCompareTypes.length; i++) {
                    if (symbol.equals(mCompareTypes[i])) {
                        mViewBinding.compareTypeWv.setCurrentIndex(i);
                        break;
                    }
                }
                String value = mECondition.getCondition().getParameters().getCompareValue();
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            }
        } else if (CTSL.PK_FAU.equals(mProductKey)) {
            // 新风
            if ("FanMode".equals(mKeyName)) {
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mECondition.getCondition().getParameters().getCompareValue();
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "4");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("4".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if ("Temperature".equals(mKeyName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 当前温度
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String symbol = mECondition.getCondition().getParameters().getCompareType();
                for (int i = 0; i < mCompareTypes.length; i++) {
                    if (symbol.equals(mCompareTypes[i])) {
                        mViewBinding.compareTypeWv.setCurrentIndex(i);
                        break;
                    }
                }
                String value = mECondition.getCondition().getParameters().getCompareValue();
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            }
        } else if (CTSL.PK_FLOORHEATING001.equals(mProductKey)) {
            // 地暖
            if ("WorkMode".equals(mKeyName)) {
                // 开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mECondition.getCondition().getParameters().getCompareValue();
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "10");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("10".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if ("Temperature".equals(mKeyName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 当前温度
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String symbol = mECondition.getCondition().getParameters().getCompareType();
                for (int i = 0; i < mCompareTypes.length; i++) {
                    if (symbol.equals(mCompareTypes[i])) {
                        mViewBinding.compareTypeWv.setCurrentIndex(i);
                        break;
                    }
                }
                String value = mECondition.getCondition().getParameters().getCompareValue();
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            }
        } else if (CTSL.PK_LIGHT.equals(mProductKey)) {
            // 调光调色面板
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.open), "1");
            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
        } else if (CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(mProductKey)) {
            // 调光面板
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.open), "1");
            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
        } else if (CTSL.PK_PIRSENSOR.equals(mProductKey)) {
            // 人体红外感应器
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(getString(R.string.sensorstate_motionnonhas), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.sensorstate_motionhas), "1");
            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
        } else if (CTSL.PK_GASSENSOR.equals(mProductKey)) {
            // 燃气传感器
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(getString(R.string.sensorstate_gasnonhas), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.sensorstate_gashas), "1");
            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
        } else if (CTSL.PK_TEMHUMSENSOR.equals(mProductKey)) {
            // 温湿度传感器
            if ("Temperature".equals(mKeyName)) {
                // 当前温度
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String symbol = mECondition.getCondition().getParameters().getCompareType();
                for (int i = 0; i < mCompareTypes.length; i++) {
                    if (symbol.equals(mCompareTypes[i])) {
                        mViewBinding.compareTypeWv.setCurrentIndex(i);
                        break;
                    }
                }
                String value = mECondition.getCondition().getParameters().getCompareValue();
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            } else if ("Humidity".equals(mKeyName)) {
                // 当前湿度
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                mEventValueList.clear();
                for (int i = 0; i <= 100; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String symbol = mECondition.getCondition().getParameters().getCompareType();
                for (int i = 0; i < mCompareTypes.length; i++) {
                    if (symbol.equals(mCompareTypes[i])) {
                        mViewBinding.compareTypeWv.setCurrentIndex(i);
                        break;
                    }
                }
                String value = mECondition.getCondition().getParameters().getCompareValue();
                if (value == null || value.length() == 0) {
                    value = "27";
                }
                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value));
            }
        } else if (CTSL.PK_SMOKESENSOR.equals(mProductKey)) {
            // 烟雾传感器
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(getString(R.string.sensorstate_smokenonhas), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.sensorstate_smokehas), "1");
            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
        } else if (CTSL.PK_WATERSENSOR.equals(mProductKey)) {
            // 水浸传感器
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(getString(R.string.sensorstate_waternonhas), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.sensorstate_waterhas), "1");
            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
        } else if (CTSL.PK_DOORSENSOR.equals(mProductKey)) {
            // 门磁
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mECondition.getCondition().getParameters().getCompareValue();
            PropertyValue p1 = new PropertyValue(getString(R.string.sensorstate_contactclose), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.sensorstate_contactopen), "1");
            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
        }
    }

    private void onToolbarRightClicker() {
        PropertyValue value = new PropertyValue();
        for (PropertyValue v : mList) {
            if (v.isChecked) {
                value = v;
                break;
            }
        }
        if ((value.getKey() == null || value.getKey().length() == 0) && mViewBinding.eventLayout.getVisibility() == View.GONE) {
            ToastUtils.showLongToast(this, R.string.pls_select_an_item);
            return;
        }
        if ("Event".equals(mConditionType)) {
            mECondition.getCondition().getParameters().setCompareValue(value.getValue());
        } else if ("State".equals(mConditionType)) {
            if (CTSL.PK_ONEWAYSWITCH.equals(mProductKey) ||
                    CTSL.PK_TWOWAYSWITCH.equals(mProductKey) ||
                    CTSL.PK_THREE_KEY_SWITCH.equals(mProductKey) ||
                    CTSL.PK_FOURWAYSWITCH_2.equals(mProductKey) ||
                    CTSL.PK_SIX_TWO_SCENE_SWITCH.equals(mProductKey) ||
                    CTSL.TEST_PK_ONEWAYWINDOWCURTAINS.equals(mProductKey) ||
                    CTSL.TEST_PK_TWOWAYWINDOWCURTAINS.equals(mProductKey) ||
                    CTSL.PK_LIGHT.equals(mProductKey) ||
                    CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(mProductKey) ||
                    CTSL.PK_PIRSENSOR.equals(mProductKey) ||
                    CTSL.PK_GASSENSOR.equals(mProductKey) ||
                    CTSL.PK_SMOKESENSOR.equals(mProductKey) ||
                    CTSL.PK_WATERSENSOR.equals(mProductKey) ||
                    CTSL.PK_DOORSENSOR.equals(mProductKey)) {
                mECondition.getCondition().getParameters().setCompareValue(value.getValue());
            } else if (CTSL.PK_AIRCOMDITION_TWO.equals(mProductKey) ||
                    CTSL.PK_FAU.equals(mProductKey) ||
                    CTSL.PK_TEMHUMSENSOR.equals(mProductKey)) {
                // 空调二管制、新风、温湿度传感器
                if ("WorkMode".equals(mKeyName) || "FanMode".equals(mKeyName)) {
                    // 开关
                    mECondition.getCondition().getParameters().setCompareValue(value.getValue());
                } else if ("Temperature".equals(mKeyName) || "Humidity".equals(mKeyName)) {
                    // 当前温度、当前湿度
                    mECondition.getCondition().getParameters().setCompareType(mCompareTypes[mViewBinding.compareTypeWv.getCurrentIndex()]);
                    mECondition.getCondition().getParameters().setCompareValue(mEventValueList.get(mViewBinding.compareValueWv.getCurrentIndex()));
                }
            }
        }
        mIsRun = false;
        mECondition.setTarget("LocalSceneActivity");
        EventBus.getDefault().postSticky(mECondition);

        Intent intent = new Intent(this, LocalSceneActivity.class);
        startActivity(intent);
    }

    private void initRecyclerView() {
        mList = new ArrayList<>();
        mAdapter = new BaseQuickAdapter<PropertyValue, BaseViewHolder>(R.layout.item_simple_checked, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, PropertyValue value) {
                TextView itemChecked = holder.getView(R.id.item_checked);
                itemChecked.setTypeface(mIconfont);

                int pos = mList.indexOf(value);
                holder.setText(R.id.item_title, value.getKey())
                        .setVisible(R.id.item_checked, value.isChecked())
                        .setVisible(R.id.item_divider, pos != 0);
                holder.getView(R.id.root_layout).setBackground(null);
            }
        };
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setChecked(i == position);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mViewBinding.valueRv.setLayoutManager(layoutManager);
        mViewBinding.valueRv.setAdapter(mAdapter);
    }

    private String getCompareTypeString(String compareType) {
        if ("<".equals(compareType))
            return getString(R.string.less_than);
        else if ("<=".equals(compareType))
            return getString(R.string.less_than_or_equal_to);
        else if ("==".equals(compareType))
            return getString(R.string.equal_to);
        else if (">".equals(compareType))
            return getString(R.string.greater_than);
        else if (">=".equals(compareType))
            return getString(R.string.great_than_or_equal_to);
        else if ("!=".equals(compareType))
            return getString(R.string.is_not_equal_to);
        return "";
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
        mViewBinding.includeToolbar.tvToolbarTitle.setText(mIdentifierName);
        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.nick_name_save));
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolbarRightClicker();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 根据IotId查询网关Mac
     *
     * @param token
     * @param iotId
     */
    private void requestMacByIot(String token, String iotId) {
        JSONObject obj = new JSONObject();
        obj.put("apiVer", "1.0");
        JSONObject params = new JSONObject();
        params.put("plantForm", "xxxxxx");
        params.put("iotId", iotId);
        obj.put("params", params);

        ERetrofit.getInstance().getService()
                .queryMacByIotId(token, ERetrofit.convertToBody(obj.toJSONString()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JSONObject jsonObject) {
                        ViseLog.d(jsonObject.toJSONString());
                        int code = jsonObject.getInteger("code");
                        if (code == 200) {
                            String mac = jsonObject.getString("mac");
                            mDevMac = mac;
                            if (mDevMac == null || mDevMac.length() == 0) {
                                QMUITipDialogUtil.showFailDialog(LocalConditionValueActivity.this, R.string.pls_try_again_later);
                            }
                        } else {
                            String msg = jsonObject.getString("message");
                            if (msg == null || msg.length() == 0) {
                                QMUITipDialogUtil.showFailDialog(LocalConditionValueActivity.this, R.string.pls_try_again_later);
                            } else
                                QMUITipDialogUtil.showFailDialog(LocalConditionValueActivity.this, msg);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        ViseLog.e(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private static class PropertyValue {
        private String key;
        private String value;
        private boolean isChecked = false;

        public PropertyValue() {
        }

        public PropertyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    private static class EventValue {
        private String max;
        private String min;
        private String step;
        private String unitName;
        private String name = "";
        private String identifier;
        private String type;
        private String unit;

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public String getStep() {
            return step;
        }

        public void setStep(String step) {
            this.step = step;
        }

        public String getUnitName() {
            if (unitName == null || "无".equals(unitName))
                unitName = "";
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(Object obj) {
        EventBus.getDefault().removeStickyEvent(obj);
        if (obj instanceof ECondition) {
            mECondition = (ECondition) obj;
            if ("LocalConditionValueActivity".equals(mECondition.getTarget())) {
                mDevIotId = mECondition.getIotId();
                mIdentifierName = mECondition.getKeyNickName();
                mProductKey = DeviceBuffer.getDeviceInformation(mDevIotId).productKey;
                mConditionType = mECondition.getCondition().getType();
                mKeyName = mECondition.getCondition().getParameters().getName();
                if ("Event".equals(mConditionType)) {
                    mKeyName = mECondition.getCondition().getParameters().getParameterName();
                }
                mEndId = mECondition.getCondition().getParameters().getEndpointId();
                mDevMac = DeviceBuffer.getDeviceInformation(mDevIotId).deviceName;
            }
        }
    }
}