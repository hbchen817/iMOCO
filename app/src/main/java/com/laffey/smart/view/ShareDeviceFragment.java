package com.laffey.smart.view;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.laffey.smart.R;
import com.laffey.smart.event.ShareDeviceEvent;
import com.laffey.smart.model.EDevice;
import com.laffey.smart.model.ItemShareDevice;
import com.laffey.smart.model.Visitable;
import com.laffey.smart.presenter.DeviceBuffer;
import com.laffey.smart.utility.ToastUtils;
import com.laffey.smart.viewholder.CommonAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;

/**
 * @author fyy
 * @date 2018/7/17
 */
public class ShareDeviceFragment extends BaseFragment {

    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.msg_nodata_view)
    LinearLayout mDevNodataView;

    private CommonAdapter adapter;
    private List<Visitable> models = new ArrayList<Visitable>();
    private Intent intent;
    private int type;

    @Subscribe
    public void shareDeviceEvent(ShareDeviceEvent shareDeviceEvent) {
        if (type == 0) {
            if (shareDeviceEvent.getName().equals("select")) {
                changeStatus(2);
            } else if (shareDeviceEvent.getName().equals("cancel")) {
                changeStatus(1);
            } else if (shareDeviceEvent.getName().equals("confirm")) {
                getSelectedIds();
                if (selectedIdList.isEmpty()) {
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.share_device_selected_is_empty));
                } else if (selectedIdList.size() > 20) {
                    ToastUtils.showToastCentrally(mActivity, getString(R.string.share_device_num_error));
                } else {
                    intent = new Intent(mActivity, DeviceQrcodeActivity.class);
                    intent.putStringArrayListExtra("iotIdList", selectedIdList);
                    startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    protected int setLayout() {
        EventBus.getDefault().register(this);
        return R.layout.fragment_msg_center;
    }

    @Override
    protected void init() {
        type = getArguments().getInt("type");

        adapter = new CommonAdapter(models, mActivity);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        recycleView.setLayoutManager(layoutManager);
        recycleView.setAdapter(adapter);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.root_view) {
                    int p = (int) view.getTag();
                    ItemShareDevice itemShareDevice = (ItemShareDevice) models.get(p);
                    if (itemShareDevice.getStatus() == 1) {
                        selectedIdList.clear();
                        selectedIdList.add(itemShareDevice.getId());
                        intent = new Intent(mActivity, DeviceQrcodeActivity.class);
                        intent.putStringArrayListExtra("iotIdList", selectedIdList);
                        startActivity(intent);
                    } else if (itemShareDevice.getStatus() == 2) {
                        itemShareDevice.setStatus(3);
                    } else if (itemShareDevice.getStatus() == 3) {
                        itemShareDevice.setStatus(2);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
//        for (int i=0;i<5;i++){
//            ItemShareDevice itemMsgCenter = new ItemShareDevice();
//            itemMsgCenter.setId(""+i);
//            itemMsgCenter.setDeviceName("设备名称"+i);
//            itemMsgCenter.setStatus(1);
//            models.add(itemMsgCenter);
//        }
//        adapter.notifyDataSetChanged();
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
            itemShareDevice.setImage(mapValue.image);
            if (type == 0 && mapValue.owned == 1) {
                itemShareDevice.setStatus(1);
                models.add(itemShareDevice);
            }
            if (type == 1 && mapValue.owned == 0) {
                itemShareDevice.setStatus(0);
                models.add(itemShareDevice);
            }
        }
        adapter.notifyDataSetChanged();
        if (models.isEmpty()) {
            recycleView.setVisibility(View.GONE);
            mDevNodataView.setVisibility(View.VISIBLE);
        } else {
            recycleView.setVisibility(View.VISIBLE);
            mDevNodataView.setVisibility(View.GONE);
        }
    }

    private void changeStatus(int status) {
        int length = models.size();
        for (int i = 0; i < length; i++) {
            ItemShareDevice itemShareDevice = (ItemShareDevice) models.get(i);
            itemShareDevice.setStatus(status);
        }
        adapter.notifyDataSetChanged();
    }

    private ArrayList<String> selectedIdList = new ArrayList<>();

    private void getSelectedIds() {
        selectedIdList.clear();
        int length = models.size();
        for (int i = 0; i < length; i++) {
            ItemShareDevice itemShareDevice = (ItemShareDevice) models.get(i);
            if (itemShareDevice.getStatus() == 3) {
                selectedIdList.add(itemShareDevice.getId());
            }
        }
    }

}
