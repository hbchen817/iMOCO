package com.rexense.imoco.view;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.rexense.imoco.R;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.HomeSpaceManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.presenter.UserCenter;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.EHomeSpace;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.utility.Dialog;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-21 17:14
 * Description: 子设备更多界面
 */
public class MoreSubdeviceActivity extends BaseActivity {
    private String mIOTId, mName, mProductKey, mRoomName, mBindTime;
    private int mSetType = 0;
    private RelativeLayout mWheelPickerLayout;
    private WheelPicker mWheelPicker;
    private TextView mLblTitle, mWheelPickerValue, mLblNewNickName, mLblRoomName;
    private HomeSpaceManager mHomeSpaceManager;
    private UserCenter mUserCenter;
    private EHomeSpace.roomListEntry mRoomListEntry;
    private String mNewNickName, mNewRoomId, mNewRoomName;

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETHOMEROOMLIST:
                    // 处理获取家房间列表回调
                    mRoomListEntry = CloudDataParser.processHomeRoomList((String)msg.obj);
                    break;
                case Constant.MSG_CALLBACK_SETDEVICENICKNAME:
                    // 处理设置设备昵称回调
                    mLblNewNickName.setText(mNewNickName);
                    mLblTitle.setText(mNewNickName);
                    // 更新设备缓存备注名称
                    DeviceBuffer.updateDeviceNickName(mIOTId, mNewNickName);
                    break;
                case Constant.MSG_CALLBACK_UPDATEDEVICEROOM:
                    // 处理更新设备所属房间回调
                    mLblRoomName.setText(mNewRoomName);
                    // 更新设备缓存房间数据
                    DeviceBuffer.updateDeviceRoom(mIOTId, mNewRoomId, mNewRoomName);
                    RefreshData.refreshRoomListData();
                    break;
                case Constant.MSG_CALLBACK_GETTHINGBASEINFO:
                    // 处理获取物的基本信息回调
                    ETSL.thingBaseInforEntry thingBaseInforEntry = CloudDataParser.processThingBaseInformation((String)msg.obj);
                    TextView version = (TextView)findViewById(R.id.moreSubdeviceLblVersion);
                    version.setText(thingBaseInforEntry.firmwareVersion);
                    break;
                case Constant.MSG_CALLBACK_UNBINDEVICE:
                    // 处理设备解除绑定回调(用于Detail界面直接退出)
                    setResult(Constant.RESULTCODE_CALLMOREACTIVITYUNBIND, null);
                    // 删除缓存中的数据
                    DeviceBuffer.deleteDevice(mIOTId);
                    Dialog.confirm(MoreSubdeviceActivity.this, R.string.dialog_title, getString(R.string.dialog_unbind_ok), R.drawable.dialog_prompt, R.string.dialog_ok, true);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    // 设置滑轮选择器(type取值,1房间列表)
    private void setWheelPicker(int type, String initValue) {
        mSetType = type;
        mWheelPickerValue.setText(initValue + "");
        TextView ok = (TextView)findViewById(R.id.oneItemWheelPickerLblOk);
        ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
                if(mSetType == 1) {
                    // 设置设备所属房间
                    mHomeSpaceManager.updateRoomDevice(SystemParameter.getInstance().getHomeId(), mNewRoomId, mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                }
            }
        });
        TextView cancel = (TextView)findViewById(R.id.oneItemWheelPickerLblCancel);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWheelPickerLayout.setVisibility(View.GONE);
            }
        });

        // 生成选择项数量
        int count = 0;
        if(type == 1) {
            count = mRoomListEntry == null || mRoomListEntry.data == null ? 0 : mRoomListEntry.data.size();
        }

        // 生成选择项内容
        if(count > 0) {
            List<String> data = new ArrayList<String>();
            int initIndex = 0;
            if(type == 1) {
                int n = 0;
                for(EHomeSpace.roomEntry room : mRoomListEntry.data) {
                    data.add(room.name);
                    if(room.name.equals(initValue)) {
                        initIndex = n;
                    }
                    n++;
                }
            }
            mWheelPicker.setOnItemSelectedListener(new WheelPicker.OnItemSelectedListener() {
                @Override
                public void onItemSelected(WheelPicker picker, Object data, int position) {
                    mWheelPickerValue.setText(data.toString());
                    if(mSetType == 1) {
                        mNewRoomId = mRoomListEntry.data.get(position).roomId;
                        mNewRoomName = data.toString();
                    }
                }
            });
            // 加载两次数据是为了正确初始选中位置
            for(int i = 0; i < 2; i++) {
                mWheelPicker.setData(data);
                mWheelPicker.setSelectedItemPosition(initIndex);
            }
            mWheelPicker.invalidate();
            this.mWheelPickerLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_subdevice);

        // 获取参数
        Intent intent = getIntent();
        this.mIOTId = intent.getStringExtra("iotId");
        this.mName = intent.getStringExtra("name");
        this.mProductKey = intent.getStringExtra("productKey");

        this.mLblTitle = (TextView)findViewById(R.id.includeTitleLblTitle);
        this.mLblTitle.setText(this.mName);

        // 分享设备不允许修改房间，故不显示
        if(intent.getIntExtra("owned", 0) == 0){
            RelativeLayout rlRoom = (RelativeLayout)findViewById(R.id.moreSubdeviceRLRoom);
            rlRoom.setVisibility(View.GONE);
        }

        // 获取房间与绑定时间
        EDevice.deviceEntry deviceEntry = DeviceBuffer.getDeviceInformation(this.mIOTId);
        if(deviceEntry != null) {
            this.mRoomName = deviceEntry.roomName;
            this.mBindTime = deviceEntry.bindTime;
        }

        // 回退处理
        ImageView imgBack = (ImageView)findViewById(R.id.includeTitleImgBack);
        imgBack.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.mWheelPickerLayout = (RelativeLayout)findViewById(R.id.oneItemWheelPickerRLPicker);
        this.mWheelPickerLayout.setVisibility(View.GONE);
        this.mWheelPickerValue = (TextView)findViewById(R.id.oneItemWheelPickerLblValue);
        this.mWheelPicker = (WheelPicker) findViewById(R.id.oneItemWheelPickerWPPicker);
        this.mLblRoomName = (TextView)findViewById(R.id.moreSubdeviceLblRoom);
        this.mLblRoomName.setText(this.mRoomName);
        TextView bindTime = (TextView)findViewById(R.id.moreSubdeviceLblBindTime);
        bindTime.setText(this.mBindTime);

        // 录入备注名称事件处理
        this.mLblNewNickName = (TextView)findViewById(R.id.moreSubdeviceLblName);
        this.mLblNewNickName.setText(mName);
        ImageView inputNickName = (ImageView) findViewById(R.id.moreSubdeviceImgName);
        inputNickName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mNickName = new EditText(MoreSubdeviceActivity.this);
                mNickName.setText(mLblNewNickName.getText().toString());
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(MoreSubdeviceActivity.this);
                mDialogBuilder.setTitle(R.string.moredevice_namehint);
                mDialogBuilder.setIcon(R.drawable.dialog_prompt);
                mDialogBuilder.setView(mNickName);
                mDialogBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNewNickName = mNickName.getText().toString();
                        // 设置设备昵称
                        mUserCenter.setDeviceNickName(mIOTId, mNewNickName, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    }
                });
                mDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                mDialogBuilder.setCancelable(true);
                AlertDialog dialog = mDialogBuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });

        // 选择所属房间处理
        OnClickListener selectRoomListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                setWheelPicker(1, mLblRoomName.getText().toString());
            }
        };
        ImageView selectRoom = (ImageView) findViewById(R.id.moreSubdeviceImgRoom);
        selectRoom.setOnClickListener(selectRoomListener);

        // 解除绑定处理
        OnClickListener unBindListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MoreSubdeviceActivity.this);
                builder.setIcon(R.drawable.dialog_quest);
                builder.setTitle(R.string.dialog_title);
                builder.setMessage(R.string.dialog_unbind);
                builder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 设备解除绑定
                        mUserCenter.unbindDevice(mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                    }
                });
                builder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                builder.create().show();
            }
        };
        TextView lblUnbind = (TextView)findViewById(R.id.moreSubdeviceLblUnbind);
        ImageView imgUnbind = (ImageView)findViewById(R.id.moreSubdeviceImgUnbind);
        lblUnbind.setOnClickListener(unBindListener);
        imgUnbind.setOnClickListener(unBindListener);

        // 消息记录处理
        OnClickListener messageRecordListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreSubdeviceActivity.this, MessageRecordActivity.class);
                intent.putExtra("iotId", mIOTId);
                intent.putExtra("productKey", mProductKey);
                startActivity(intent);
            }
        };
        ImageView imgMessageRecord = (ImageView)findViewById(R.id.moreSubdeviceImgMsg);
        imgMessageRecord.setOnClickListener(messageRecordListener);

        // 获取设备基本信息
        new TSLHelper(this).getBaseInformation(this.mIOTId, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        // 获取家房间列表
        this.mHomeSpaceManager = new HomeSpaceManager(this);
        this.mHomeSpaceManager.getHomeRoomList(SystemParameter.getInstance().getHomeId(), 1, 50, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);

        this.mUserCenter = new UserCenter(this);
    }
}