package com.laffey.smart.view;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.databinding.ActivityAddRoomDeviceBinding;
import com.laffey.smart.event.RefreshRoomDevice;
import com.laffey.smart.event.RefreshRoomName;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.ItemAddRoomDevice;
import com.laffey.smart.model.ItemShareDevice;
import com.laffey.smart.model.Visitable;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.presenter.HomeSpaceManager;
import com.laffey.smart.presenter.SystemParameter;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.viewholder.CommonAdapter;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddRoomDeviceActivity extends BaseActivity implements View.OnClickListener {
    private ActivityAddRoomDeviceBinding mViewBinding;

    private CommonAdapter adapter1;
    private List<Visitable> models1 = new ArrayList<Visitable>();

    private CommonAdapter adapter2;
    private List<Visitable> models2 = new ArrayList<Visitable>();

    private String roomId;
    private String roomName;
    private String roomNameNew;

    private ArrayList<String> iotIdList = new ArrayList<>();
    private HomeSpaceManager homeSpaceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityAddRoomDeviceBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.tvToolbarRight.setText(getString(R.string.nick_name_save));

        roomId = getIntent().getStringExtra("roomId");
        roomName = getIntent().getStringExtra("roomName");
        mViewBinding.includeToolbar.tvToolbarTitle.setText(roomName);
        mViewBinding.roomNameTv.setText(roomName);
        homeSpaceManager = new HomeSpaceManager(mActivity);

        adapter1 = new CommonAdapter(models1, mActivity);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mActivity);
        mViewBinding.recycleView1.setLayoutManager(layoutManager1);
        mViewBinding.recycleView1.setAdapter(adapter1);
        adapter1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.right_img) {
                    int p = (int) view.getTag();
                    ItemAddRoomDevice itemAddRoomDevice = (ItemAddRoomDevice) models1.get(p);
                    itemAddRoomDevice.setType(0);
                    models2.add(itemAddRoomDevice);
                    models1.remove(p);
                    adapter1.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                    mViewBinding.deviceThisRoomTv.setVisibility(models1.isEmpty() ? View.GONE : View.VISIBLE);
                    mViewBinding.deviceOtherRoomTv.setVisibility(models2.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }
        });

        adapter2 = new CommonAdapter(models2, mActivity);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mActivity);
        mViewBinding.recycleView2.setLayoutManager(layoutManager2);
        mViewBinding.recycleView2.setAdapter(adapter2);
        adapter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.right_img) {
                    int p = (int) view.getTag();
                    ItemAddRoomDevice itemAddRoomDevice = (ItemAddRoomDevice) models2.get(p);
                    itemAddRoomDevice.setType(1);
                    models1.add(itemAddRoomDevice);
                    models2.remove(p);
                    adapter1.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                    mViewBinding.deviceThisRoomTv.setVisibility(models1.isEmpty() ? View.GONE : View.VISIBLE);
                    mViewBinding.deviceOtherRoomTv.setVisibility(models2.isEmpty() ? View.GONE : View.VISIBLE);
                }
            }
        });

        getData();

        initStatusBar();
        mViewBinding.includeToolbar.tvToolbarRight.setOnClickListener(this);
        mViewBinding.roomNameView.setOnClickListener(this);
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    private void getData() {
        Map<String, EDevice.deviceEntry> deviceMap = DeviceBuffer.getAllDeviceInformation();
        for (Map.Entry<String, EDevice.deviceEntry> entry : deviceMap.entrySet()) {
            String mapKey = entry.getKey();
            EDevice.deviceEntry mapValue = entry.getValue();
            ItemShareDevice itemShareDevice = new ItemShareDevice();
            itemShareDevice.setId(mapKey);
            itemShareDevice.setDeviceName(mapValue.nickName);
            itemShareDevice.setProductKey(mapValue.productKey);
            if (mapValue.owned == 1) {
                ItemAddRoomDevice itemAddRoomDevice = new ItemAddRoomDevice();
                itemAddRoomDevice.setId(mapValue.iotId);
                itemAddRoomDevice.setDeviceName(mapValue.nickName);
                itemAddRoomDevice.setProductKey(mapValue.productKey);
                itemAddRoomDevice.setImage(mapValue.image);
                if (mapValue.roomId.equals(roomId)) {
                    itemAddRoomDevice.setType(1);
                    models1.add(itemAddRoomDevice);
                } else {
                    itemAddRoomDevice.setType(0);
                    models2.add(itemAddRoomDevice);
                }
            }
        }
        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
        mViewBinding.deviceThisRoomTv.setVisibility(models1.isEmpty() ? View.GONE : View.VISIBLE);
        mViewBinding.deviceOtherRoomTv.setVisibility(models2.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_roomname_dialog, null);
        builder.setView(view);
        builder.setCancelable(true);
        TextView titleTv = (TextView) view.findViewById(R.id.title);
        final EditText nameEt = (EditText) view.findViewById(R.id.name_et);
        nameEt.setText(roomName);
        final Dialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = getResources().getDimensionPixelOffset(R.dimen.dp_280);
        //这行要放在dialog.show()之后才有效
        dialog.getWindow().setAttributes(params);

        View confirmView = view.findViewById(R.id.confirm_btn);
        View cancelView = view.findViewById(R.id.cancel_btn);
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = nameEt.getText().toString().trim();
                if (!nameStr.equals("")) {
                    dialog.dismiss();
                    roomNameNew = nameStr;
                    homeSpaceManager.updateRoomInfo(SystemParameter.getInstance().getHomeId(), roomId, roomNameNew, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                }
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_UPDATEDEVICEROOM:
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.room_device_update_success));
                    EventBus.getDefault().post(new RefreshRoomDevice());
                    finish();
                    break;
                case Constant.MSG_CALLBACK_UPDATEROOM:
                    roomName = roomNameNew;
                    mViewBinding.includeToolbar.tvToolbarTitle.setText(roomName);
                    mViewBinding.roomNameTv.setText(roomName);
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.room_name_update_success));
                    EventBus.getDefault().post(new RefreshRoomName(roomName));
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_toolbar_right) {
            iotIdList.clear();
            for (int i = 0; i < models1.size(); i++) {
                ItemAddRoomDevice itemAddRoomDevice = (ItemAddRoomDevice) models1.get(i);
                iotIdList.add(itemAddRoomDevice.getId());
            }
            homeSpaceManager.updateRoomDevices(SystemParameter.getInstance().getHomeId(), roomId, iotIdList,
                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        } else if (id == R.id.room_name_view) {
            showAddDialog();
        }
    }
}
