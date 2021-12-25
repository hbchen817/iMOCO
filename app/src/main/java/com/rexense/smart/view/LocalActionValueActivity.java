package com.rexense.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rexense.smart.R;
import com.rexense.smart.contract.CTSL;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.databinding.ActivityEditPropertyValueBinding;
import com.rexense.smart.model.EAction;
import com.rexense.smart.presenter.DeviceBuffer;
import com.rexense.smart.presenter.SceneManager;
import com.rexense.smart.utility.AppUtils;
import com.rexense.smart.utility.QMUITipDialogUtil;
import com.rexense.smart.model.ERetrofit;
import com.rexense.smart.utility.RetrofitUtil;
import com.rexense.smart.utility.ToastUtils;
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

public class LocalActionValueActivity extends BaseActivity {
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
    private final List<String> mEventValueList = new ArrayList<>();

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
    private String mKeyName;
    private String mEndId;
    private JSONObject mCommand;
    private String mCommandType;

    private EAction mEAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityEditPropertyValueBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        EventBus.getDefault().register(this);

        mCompareTypes = new String[]{"=="};

        mViewBinding.compareTypeWv.setEntries(getString(R.string.equal_to));
        mViewBinding.compareTypeWv.setCurrentIndex(0);
        mIconfont = Typeface.createFromAsset(getAssets(), Constant.ICON_FONT_TTF);
        initStatusBar();
        initRecyclerView();
        initData();
    }

    private void initData() {
        mSceneManager = new SceneManager(this);

        //requestMacByIot("chengxunfei", mDevIotId);
        if (CTSL.TEST_PK_ONEWAYWINDOWCURTAINS.equals(mProductKey) ||
                CTSL.TEST_PK_TWOWAYWINDOWCURTAINS.equals(mProductKey)) {
            // 单路、双路窗帘面板
            mViewBinding.valueRv.setVisibility(View.VISIBLE);

            String value = mCommand.getString("Operate");
            PropertyValue p1 = new PropertyValue(getString(R.string.stop), "2");
            PropertyValue p2 = new PropertyValue(getString(R.string.open), "0");
            PropertyValue p3 = new PropertyValue(getString(R.string.close), "1");

            if ("2".equals(value)) {
                p1.setChecked(true);
            } else if ("0".equals(value)) {
                p2.setChecked(true);
            } else if ("1".equals(value)) {
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
            String value = mCommand.getString("State");
            PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.open), "1");
            PropertyValue p3 = new PropertyValue(getString(R.string.reverse), "2");
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
        } else if (CTSL.PK_SIX_TWO_SCENE_SWITCH.equals(mProductKey)) {
            // 六键四开二场景开关
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mCommand.getString("State");
            PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.open), "1");
            PropertyValue p3 = new PropertyValue(getString(R.string.reverse), "2");
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
        } else if (CTSL.PK_OUTLET.equals(mProductKey)) {
            // 二三极插座
            mViewBinding.valueRv.setVisibility(View.VISIBLE);
            String value = mCommand.getString("State");
            PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
            PropertyValue p2 = new PropertyValue(getString(R.string.open), "1");
            if ("0".equals(value)) {
                p1.setChecked(true);
            } else if ("1".equals(value)) {
                p2.setChecked(true);
            }
            mList.add(p1);
            mList.add(p2);
        } else if (CTSL.PK_AIRCOMDITION_TWO.equals(mProductKey) ||
                CTSL.PK_AIRCOMDITION_FOUR.equals(mProductKey)) {
            // 空调二管制
            if (getString(R.string.power_switch).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "10");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("10".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.work_mode).equals(mIdentifierName)) {
                // 工作模式
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.heating), "4");
                PropertyValue p2 = new PropertyValue(getString(R.string.refrigeration), "3");
                PropertyValue p3 = new PropertyValue(getString(R.string.air_supply), "7");
                if ("4".equals(value)) {
                    p1.setChecked(true);
                } else if ("3".equals(value)) {
                    p2.setChecked(true);
                } else if ("7".equals(value)) {
                    p3.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
            } else if (getString(R.string.target_temperature).equals(mIdentifierName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 目标温度
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String value = mCommand.getString("Temperature");
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareTypeWv.setVisibility(View.GONE);
                mViewBinding.nameTv.setText(R.string.equal_to);
                mViewBinding.unitTv.setText(R.string.centigrade);

                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            } else if (getString(R.string.fan_speed).equals(mIdentifierName)) {
                // 风速
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.fan_speed_low), "1");
                PropertyValue p2 = new PropertyValue(getString(R.string.fan_speed_mid), "2");
                PropertyValue p3 = new PropertyValue(getString(R.string.fan_speed_high), "3");
                PropertyValue p4 = new PropertyValue(getString(R.string.auto), "5");
                if ("1".equals(value)) {
                    p1.setChecked(true);
                } else if ("2".equals(value)) {
                    p2.setChecked(true);
                } else if ("3".equals(value)) {
                    p3.setChecked(true);
                } else if ("5".equals(value)) {
                    p4.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
                mList.add(p4);
            }
        } else if (CTSL.PK_FAU.equals(mProductKey)) {
            // 新风
            if (getString(R.string.power_switch).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "4");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("4".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.fan_speed).equals(mIdentifierName)) {
                // 风速
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.fan_speed_low), "1");
                PropertyValue p2 = new PropertyValue(getString(R.string.fan_speed_mid), "2");
                PropertyValue p3 = new PropertyValue(getString(R.string.fan_speed_high), "3");
                PropertyValue p4 = new PropertyValue(getString(R.string.auto), "5");
                if ("1".equals(value)) {
                    p1.setChecked(true);
                } else if ("2".equals(value)) {
                    p2.setChecked(true);
                } else if ("3".equals(value)) {
                    p3.setChecked(true);
                } else if ("5".equals(value)) {
                    p4.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
                mList.add(p4);
            }
        } else if (CTSL.PK_FLOORHEATING001.equals(mProductKey)) {
            // 地暖
            if (getString(R.string.power_switch).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "10");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("10".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.target_temperature).equals(mIdentifierName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 当前温度
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String value = mCommand.getString("Temperature");
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }

                mViewBinding.compareTypeWv.setVisibility(View.GONE);
                mViewBinding.nameTv.setText(R.string.equal_to);
                mViewBinding.unitTv.setText(R.string.centigrade);

                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            }
        } else if (CTSL.PK_LIGHT.equals(mProductKey)) {
            // 调光调色面板
            if (getString(R.string.lightness).equals(mIdentifierName)) {
                // 亮度
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                mEventValueList.clear();
                for (int i = 0; i <= 100; ) {
                    mEventValueList.add(String.valueOf(i));
                    i = i + 25;
                }
                String value = mCommand.getString("Level");
                if (value == null || value.length() == 0) {
                    value = "0";
                }
                mViewBinding.compareTypeWv.setVisibility(View.GONE);
                mViewBinding.nameTv.setText(R.string.equal_to);
                mViewBinding.unitTv.setText("K");

                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value));
            } else if (getString(R.string.color_temperature).equals(mIdentifierName)) {
                // 色温
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                mEventValueList.clear();
                for (int i = 2700; i <= 6500; ) {
                    mEventValueList.add(String.valueOf(i));
                    i = i + 25;
                }
                String value = mCommand.getString("Temperature");
                if (value == null || value.length() == 0) {
                    value = "2700";
                }
                mViewBinding.compareTypeWv.setVisibility(View.GONE);
                mViewBinding.nameTv.setText(R.string.equal_to);
                mViewBinding.unitTv.setText("%");

                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - 2700);
            }
        } else if (CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(mProductKey)) {
            // 调光面板
            mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
            mEventValueList.clear();
            for (int i = 0; i <= 100; ) {
                mEventValueList.add(String.valueOf(i));
                i = i + 25;
            }
            String value = mCommand.getString("Level");
            if (value == null || value.length() == 0) {
                value = "0";
            }
            mViewBinding.compareTypeWv.setVisibility(View.GONE);
            mViewBinding.nameTv.setText(R.string.equal_to);
            mViewBinding.unitTv.setText("K");

            mViewBinding.compareValueWv.setEntries(mEventValueList);
            mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value));
        } else if (CTSL.PK_MULTI_THREE_IN_ONE.equals(mProductKey)) {
            // 三合一温控器
            initMulti3To1Data();
        } else if (CTSL.PK_MULTI_AC_AND_FH.equals(mProductKey)) {
            // 空调+地暖二合一温控器
            initMultiACAndFHData();
        } else if (CTSL.PK_MULTI_AC_AND_FA.equals(mProductKey)) {
            // 空调+新风二合一温控器
            initMultiACAndFAData();
        } else if (CTSL.PK_MULTI_FH_AND_FA.equals(mProductKey)) {
            // 地暖+新风二合一温控器
            initMultiFHAndFAData();
        }
    }

    // 地暖+新风二合一温控器
    private void initMultiFHAndFAData() {
        if ("1".equals(mEndId)) {
            // 地暖
            if (getString(R.string.power_switch_floorheat).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "10");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("10".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.target_temperature_floorheat).equals(mIdentifierName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 目标温度
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String value = mCommand.getString("Temperature");
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            }
        } else if ("2".equals(mEndId)) {
            // 新风
            if (getString(R.string.power_switch_freshair).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "4");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("4".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.fan_speed_freshair).equals(mIdentifierName)) {
                // 风速
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.fan_speed_low), "1");
                PropertyValue p2 = new PropertyValue(getString(R.string.fan_speed_mid), "2");
                PropertyValue p3 = new PropertyValue(getString(R.string.fan_speed_high), "3");
                PropertyValue p4 = new PropertyValue(getString(R.string.auto), "5");
                if ("1".equals(value)) {
                    p1.setChecked(true);
                } else if ("2".equals(value)) {
                    p2.setChecked(true);
                } else if ("3".equals(value)) {
                    p3.setChecked(true);
                } else if ("5".equals(value)) {
                    p4.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
                mList.add(p4);
            }
        }
    }

    // 空调+新风二合一温控器
    private void initMultiACAndFAData() {
        if ("1".equals(mEndId)) {
            // 空调
            if (getString(R.string.power_switch_airconditioner).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "10");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("10".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.work_mode_airconditioner).equals(mIdentifierName)) {
                // 工作模式
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.heating), "4");
                PropertyValue p2 = new PropertyValue(getString(R.string.refrigeration), "3");
                PropertyValue p3 = new PropertyValue(getString(R.string.air_supply), "7");
                if ("4".equals(value)) {
                    p1.setChecked(true);
                } else if ("3".equals(value)) {
                    p2.setChecked(true);
                } else if ("7".equals(value)) {
                    p3.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
            } else if (getString(R.string.target_temperature_airconditioner).equals(mIdentifierName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 目标温度
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String value = mCommand.getString("Temperature");
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareTypeWv.setVisibility(View.GONE);
                mViewBinding.nameTv.setText(R.string.equal_to);
                mViewBinding.unitTv.setText(R.string.centigrade);

                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            } else if (getString(R.string.fan_speed_airconditioner).equals(mIdentifierName)) {
                // 风速
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.fan_speed_low), "1");
                PropertyValue p2 = new PropertyValue(getString(R.string.fan_speed_mid), "2");
                PropertyValue p3 = new PropertyValue(getString(R.string.fan_speed_high), "3");
                PropertyValue p4 = new PropertyValue(getString(R.string.auto), "5");
                if ("1".equals(value)) {
                    p1.setChecked(true);
                } else if ("2".equals(value)) {
                    p2.setChecked(true);
                } else if ("3".equals(value)) {
                    p3.setChecked(true);
                } else if ("5".equals(value)) {
                    p4.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
                mList.add(p4);
            }
        } else if ("2".equals(mEndId)) {
            // 新风
            if (getString(R.string.power_switch_freshair).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "4");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("4".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.fan_speed_freshair).equals(mIdentifierName)) {
                // 风速
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.fan_speed_low), "1");
                PropertyValue p2 = new PropertyValue(getString(R.string.fan_speed_mid), "2");
                PropertyValue p3 = new PropertyValue(getString(R.string.fan_speed_high), "3");
                PropertyValue p4 = new PropertyValue(getString(R.string.auto), "5");
                if ("1".equals(value)) {
                    p1.setChecked(true);
                } else if ("2".equals(value)) {
                    p2.setChecked(true);
                } else if ("3".equals(value)) {
                    p3.setChecked(true);
                } else if ("5".equals(value)) {
                    p4.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
                mList.add(p4);
            }
        }
    }

    // 空调+地暖二合一温控器
    private void initMultiACAndFHData() {
        if ("1".equals(mEndId)) {
            // 空调
            if (getString(R.string.power_switch_airconditioner).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "10");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("10".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.work_mode_airconditioner).equals(mIdentifierName)) {
                // 工作模式
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.heating), "4");
                PropertyValue p2 = new PropertyValue(getString(R.string.refrigeration), "3");
                PropertyValue p3 = new PropertyValue(getString(R.string.air_supply), "7");
                if ("4".equals(value)) {
                    p1.setChecked(true);
                } else if ("3".equals(value)) {
                    p2.setChecked(true);
                } else if ("7".equals(value)) {
                    p3.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
            } else if (getString(R.string.target_temperature_airconditioner).equals(mIdentifierName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 目标温度
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String value = mCommand.getString("Temperature");
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareTypeWv.setVisibility(View.GONE);
                mViewBinding.nameTv.setText(R.string.equal_to);
                mViewBinding.unitTv.setText(R.string.centigrade);

                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            } else if (getString(R.string.fan_speed_airconditioner).equals(mIdentifierName)) {
                // 风速
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.fan_speed_low), "1");
                PropertyValue p2 = new PropertyValue(getString(R.string.fan_speed_mid), "2");
                PropertyValue p3 = new PropertyValue(getString(R.string.fan_speed_high), "3");
                PropertyValue p4 = new PropertyValue(getString(R.string.auto), "5");
                if ("1".equals(value)) {
                    p1.setChecked(true);
                } else if ("2".equals(value)) {
                    p2.setChecked(true);
                } else if ("3".equals(value)) {
                    p3.setChecked(true);
                } else if ("5".equals(value)) {
                    p4.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
                mList.add(p4);
            }
        } else if ("2".equals(mEndId)) {
            // 地暖
            if (getString(R.string.power_switch_floorheat).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "10");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("10".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.target_temperature_floorheat).equals(mIdentifierName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 当前温度
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String value = mCommand.getString("Temperature");
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            }
        }
    }

    // 三合一温控器
    private void initMulti3To1Data() {
        if ("1".equals(mEndId)) {
            // 空调
            if (getString(R.string.power_switch_airconditioner).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "10");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("10".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.work_mode_airconditioner).equals(mIdentifierName)) {
                // 工作模式
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.heating), "4");
                PropertyValue p2 = new PropertyValue(getString(R.string.refrigeration), "3");
                PropertyValue p3 = new PropertyValue(getString(R.string.air_supply), "7");
                if ("4".equals(value)) {
                    p1.setChecked(true);
                } else if ("3".equals(value)) {
                    p2.setChecked(true);
                } else if ("7".equals(value)) {
                    p3.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
            } else if (getString(R.string.target_temperature_airconditioner).equals(mIdentifierName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 目标温度
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String value = mCommand.getString("Temperature");
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareTypeWv.setVisibility(View.GONE);
                mViewBinding.nameTv.setText(R.string.equal_to);
                mViewBinding.unitTv.setText(R.string.centigrade);

                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            } else if (getString(R.string.fan_speed_airconditioner).equals(mIdentifierName)) {
                // 风速
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.fan_speed_low), "1");
                PropertyValue p2 = new PropertyValue(getString(R.string.fan_speed_mid), "2");
                PropertyValue p3 = new PropertyValue(getString(R.string.fan_speed_high), "3");
                PropertyValue p4 = new PropertyValue(getString(R.string.auto), "5");
                if ("1".equals(value)) {
                    p1.setChecked(true);
                } else if ("2".equals(value)) {
                    p2.setChecked(true);
                } else if ("3".equals(value)) {
                    p3.setChecked(true);
                } else if ("5".equals(value)) {
                    p4.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
                mList.add(p4);
            }
        } else if ("2".equals(mEndId)) {
            // 地暖
            if (getString(R.string.power_switch_floorheat).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("WorkMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "10");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("10".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.target_temperature_floorheat).equals(mIdentifierName)) {
                mViewBinding.eventLayout.setVisibility(View.VISIBLE);// 滚动选择
                // 目标温度
                mEventValueList.clear();
                for (int i = MIN_TEMP; i <= MAX_TEMP; i++) {
                    mEventValueList.add(String.valueOf(i));
                }
                String value = mCommand.getString("Temperature");
                if (value == null || value.length() == 0) {
                    value = 9 + MIN_TEMP + "";
                }
                mViewBinding.compareValueWv.setEntries(mEventValueList);
                mViewBinding.compareValueWv.setCurrentIndex(Integer.parseInt(value) - MIN_TEMP);
            }
        } else if ("3".equals(mEndId)) {
            // 新风
            if (getString(R.string.power_switch_freshair).equals(mIdentifierName)) {
                // 电源开关
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.close), "0");
                PropertyValue p2 = new PropertyValue(getString(R.string.open), "4");
                if ("0".equals(value)) {
                    p1.setChecked(true);
                } else if ("4".equals(value)) {
                    p2.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
            } else if (getString(R.string.fan_speed_freshair).equals(mIdentifierName)) {
                // 风速
                mViewBinding.valueRv.setVisibility(View.VISIBLE);
                String value = mCommand.getString("FanMode");
                PropertyValue p1 = new PropertyValue(getString(R.string.fan_speed_low), "1");
                PropertyValue p2 = new PropertyValue(getString(R.string.fan_speed_mid), "2");
                PropertyValue p3 = new PropertyValue(getString(R.string.fan_speed_high), "3");
                PropertyValue p4 = new PropertyValue(getString(R.string.auto), "5");
                if ("1".equals(value)) {
                    p1.setChecked(true);
                } else if ("2".equals(value)) {
                    p2.setChecked(true);
                } else if ("3".equals(value)) {
                    p3.setChecked(true);
                } else if ("5".equals(value)) {
                    p4.setChecked(true);
                }
                mList.add(p1);
                mList.add(p2);
                mList.add(p3);
                mList.add(p4);
            }
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

        if (CTSL.PK_ONEWAYSWITCH.equals(mProductKey) ||
                CTSL.PK_TWOWAYSWITCH.equals(mProductKey) ||
                CTSL.PK_THREE_KEY_SWITCH.equals(mProductKey) ||
                CTSL.PK_FOURWAYSWITCH_2.equals(mProductKey) ||
                CTSL.PK_SIX_TWO_SCENE_SWITCH.equals(mProductKey)) {
            mCommand.put("State", value.getValue());
        } else if (CTSL.TEST_PK_ONEWAYWINDOWCURTAINS.equals(mProductKey) ||
                CTSL.TEST_PK_TWOWAYWINDOWCURTAINS.equals(mProductKey)) {
            mCommand.put("Operate", value.getValue());
        } else if (CTSL.PK_OUTLET.equals(mProductKey)) {
            mCommand.put("State", value.getValue());
        } else if (CTSL.PK_AIRCOMDITION_TWO.equals(mProductKey) ||
                CTSL.PK_AIRCOMDITION_FOUR.equals(mProductKey) ||
                CTSL.PK_VRV_AC.equals(mProductKey) ||
                CTSL.PK_MULTI_THREE_IN_ONE.equals(mProductKey) ||
                CTSL.PK_MULTI_AC_AND_FH.equals(mProductKey) ||
                CTSL.PK_MULTI_AC_AND_FA.equals(mProductKey) ||
                CTSL.PK_MULTI_FH_AND_FA.equals(mProductKey) ||
                CTSL.PK_FLOORHEATING001.equals(mProductKey) ||
                CTSL.PK_FAU.equals(mProductKey)) {
            if (mCommand.containsKey("WorkMode")) {
                // 电源开关、工作模式
                mCommand.put("WorkMode", value.getValue());
            } else if (mCommand.containsKey("Temperature")) {
                // 目标温度
                mCommand.put("Temperature", mEventValueList.get(mViewBinding.compareValueWv.getCurrentIndex()));
            } else if (mCommand.containsKey("FanMode")) {
                // 电源开关（新风）、风速
                mCommand.put("FanMode", value.getValue());
            }
        } else if (CTSL.PK_LIGHT.equals(mProductKey) || CTSL.PK_ONE_WAY_DIMMABLE_LIGHT.equals(mProductKey)) {
            if (mCommand.containsKey("Level")) {
                // 亮度
                mCommand.put("Level", mEventValueList.get(mViewBinding.compareValueWv.getCurrentIndex()));
            } else if (mCommand.containsKey("Temperature")) {
                // 色温
                mCommand.put("Temperature", mEventValueList.get(mViewBinding.compareValueWv.getCurrentIndex()));
            }
        }
        String target = DeviceBuffer.getCacheInfo("LocalSceneTag");
        mEAction.setTarget(target);
        mEAction.getAction().getParameters().setCommand(mCommand);
        // ViseLog.d(GsonUtil.toJson(mEAction));
        EventBus.getDefault().postSticky(mEAction);

        if ("LocalSceneActivity".equals(target)) {
            Intent intent = new Intent(this, LocalSceneActivity.class);
            startActivity(intent);
        } else if ("SwitchLocalSceneActivity".equals(target)) {
            Intent intent = new Intent(this, SwitchLocalSceneActivity.class);
            startActivity(intent);
        } else if ("TimerLocalSceneActivity".equals(target)) {
            Intent intent = new Intent(this, TimerLocalSceneActivity.class);
            startActivity(intent);
        }
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

                RelativeLayout rootLayout = holder.getView(R.id.root_layout);
                rootLayout.setBackground(null);
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
                .queryMacByIotId(token, AppUtils.getPesudoUniqueID(),
                        ERetrofit.convertToBody(obj.toJSONString()))
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
                                QMUITipDialogUtil.showFailDialog(LocalActionValueActivity.this, R.string.pls_try_again_later);
                            }
                        } else {
                            RetrofitUtil.showErrorMsg(LocalActionValueActivity.this, jsonObject);
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
        if (obj instanceof EAction) {
            mEAction = (EAction) obj;
            // ViseLog.d(GsonUtil.toJson(mEAction));
            if ("LocalActionValueActivity".equals(mEAction.getTarget())) {
                mDevIotId = mEAction.getIotId();
                mIdentifierName = mEAction.getKeyNickName();
                mProductKey = DeviceBuffer.getDeviceInformation(mDevIotId).productKey;
                mDevMac = mEAction.getAction().getParameters().getDeviceId();
                mCommand = mEAction.getAction().getParameters().getCommand();
                mCommandType = mEAction.getAction().getParameters().getCommandType();
                mEndId = mEAction.getAction().getParameters().getEndpointId();
            }
        }
    }
}