package com.rexense.imoco.view;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.event.RefreshRoomDevice;
import com.rexense.imoco.event.RefreshRoomName;
import com.rexense.imoco.model.EDevice;
import com.rexense.imoco.model.ItemAddRoomDevice;
import com.rexense.imoco.model.ItemShareDevice;
import com.rexense.imoco.model.Visitable;
import com.rexense.imoco.presenter.DeviceBuffer;
import com.rexense.imoco.presenter.HomeSpaceManager;
import com.rexense.imoco.presenter.SystemParameter;
import com.rexense.imoco.utility.ToastUtils;
import com.rexense.imoco.viewholder.CommonAdapter;

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

public class AddRoomDeviceActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_toolbar_right)
    TextView tvToolbarRight;
    @BindView(R.id.room_name_tv)
    TextView roomNameTv;
    @BindView(R.id.recycle_view1)
    RecyclerView recycleView1;
    @BindView(R.id.recycle_view2)
    RecyclerView recycleView2;
    @BindView(R.id.device_this_room_tv)
    TextView deviceThisRoomTv;
    @BindView(R.id.device_other_room_tv)
    TextView deviceOtherRoomTv;

    private CommonAdapter adapter1;
    private List<Visitable> models1 = new ArrayList<Visitable>();
    private LinearLayoutManager layoutManager1;

    private CommonAdapter adapter2;
    private List<Visitable> models2 = new ArrayList<Visitable>();
    private LinearLayoutManager layoutManager2;
    private String roomId;
    private String roomName;
    private String roomNameNew;

    private ArrayList<String> iotIdList = new ArrayList<>();
    private HomeSpaceManager homeSpaceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room_device);
        ButterKnife.bind(this);
        tvToolbarRight.setText(getString(R.string.nick_name_save));

        roomId = getIntent().getStringExtra("roomId");
        roomName = getIntent().getStringExtra("roomName");
        tvToolbarTitle.setText(roomName);
        roomNameTv.setText(roomName);
        homeSpaceManager = new HomeSpaceManager(mActivity);

        adapter1 = new CommonAdapter(models1, mActivity);
        layoutManager1 = new LinearLayoutManager(mActivity);
        recycleView1.setLayoutManager(layoutManager1);
        recycleView1.setAdapter(adapter1);
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
                    deviceThisRoomTv.setVisibility(models1.isEmpty()?View.GONE:View.VISIBLE);
                    deviceOtherRoomTv.setVisibility(models2.isEmpty()?View.GONE:View.VISIBLE);
                }
            }
        });

        adapter2 = new CommonAdapter(models2, mActivity);
        layoutManager2 = new LinearLayoutManager(mActivity);
        recycleView2.setLayoutManager(layoutManager2);
        recycleView2.setAdapter(adapter2);
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
                    deviceThisRoomTv.setVisibility(models1.isEmpty()?View.GONE:View.VISIBLE);
                    deviceOtherRoomTv.setVisibility(models2.isEmpty()?View.GONE:View.VISIBLE);
                }
            }
        });

        getData();
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
        deviceThisRoomTv.setVisibility(models1.isEmpty()?View.GONE:View.VISIBLE);
        deviceOtherRoomTv.setVisibility(models2.isEmpty()?View.GONE:View.VISIBLE);
    }

    @OnClick({R.id.tv_toolbar_right, R.id.room_name_view})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_toolbar_right:
                iotIdList.clear();
                for (int i = 0; i < models1.size(); i++) {
                    ItemAddRoomDevice itemAddRoomDevice = (ItemAddRoomDevice) models1.get(i);
                    iotIdList.add(itemAddRoomDevice.getId());
                }
                homeSpaceManager.updateRoomDevices(SystemParameter.getInstance().getHomeId(), roomId, iotIdList,
                        this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);
                break;
            case R.id.room_name_view:
                showAddDialog();
                break;
        }
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
                if (!nameStr.equals("")){
                    dialog.dismiss();
                    roomNameNew = nameStr;
                    homeSpaceManager.updateRoomInfo(SystemParameter.getInstance().getHomeId(),roomId,roomNameNew,mCommitFailureHandler, mResponseErrorHandler,mAPIDataHandler);
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

    private Handler mAPIDataHandler = new Handler(new Handler.Callback() {
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
                    tvToolbarTitle.setText(roomName);
                    roomNameTv.setText(roomName);
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.room_name_update_success));
                    EventBus.getDefault().post(new RefreshRoomName(roomName));
                    break;
                default:
                    break;
            }
            return false;
        }
    });
}
