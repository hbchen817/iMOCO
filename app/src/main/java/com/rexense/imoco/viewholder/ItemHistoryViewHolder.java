package com.rexense.imoco.viewholder;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.model.ItemGateway;
import com.rexense.imoco.model.ItemHistoryMsg;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class ItemHistoryViewHolder extends BaseViewHolder<ItemHistoryMsg> {

    private final List<String> TYPE_ALARM = Arrays.asList("HijackingAlarm", "TamperAlarm", "DoorUnlockedAlarm", "ArmDoorOpenAlarm", "LockedAlarm");
    private final List<String> TYPE_OPEN = Arrays.asList("DoorOpenNotification", "RemoteUnlockNotification");
    private final List<String> TYPE_INFO = Arrays.asList("KeyDeletedNotification", "KeyAddedNotification", "LowElectricityAlarm", "ReportReset");

    public ItemHistoryViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void setUpView(ItemHistoryMsg model, int position, CommonAdapter adapter, List<Integer> payloads) {
        View root_view = getView(R.id.root_view);
        TextView name = (TextView) getView(R.id.name);
        TextView icon = (TextView) getView(R.id.icon);
        TextView time = (TextView) getView(R.id.time);

        time.setText(model.getTime());
        Typeface iconfont = Typeface.createFromAsset(root_view.getContext().getAssets(), "iconfont/jk/iconfont.ttf");
        icon.setTypeface(iconfont);
        String eventCode = model.getEvent_code();
        String keyNameStr;
        if (TextUtils.isEmpty(model.getUserName())) {
            StringBuffer keyName = new StringBuffer();
            switch (model.getLockType()) {
                case 0:
                    keyName.append("密码");
                    break;
                case 1:
                    keyName.append("射频");
                    break;
                case 2:
                    keyName.append("机械钥匙");
                    break;
                case 3:
                    keyName.append("卡片");
                    break;
                case 4:
                    keyName.append("指纹");
                    break;
                case 5:
                    keyName.append("临时密码");
                    break;
                case 6:
                    keyName.append("App应用程序");
                    break;
                case 7:
                    keyName.append("蓝牙");
                    break;
                case 8:
                    keyName.append("人脸");
                    break;
                case 9:
                    keyName.append("遥控器");
                    break;
                case 10:
                    keyName.append("指静脉");
                    break;
                default:
                    break;
            }
            if (!TextUtils.isEmpty(model.getKeyID())) {
                keyNameStr = keyName.append(model.getKeyID()).toString();
            } else {
                keyNameStr = "";
            }
        } else {
            keyNameStr = model.getUserName();
        }
        if (TYPE_ALARM.contains(eventCode)) {//报警记录
            icon.setText(R.string.icon_history_alarm);
            icon.setTextColor(0xffDD4946);
            switch (eventCode) {
                case "HijackingAlarm":
                    name.setText(keyNameStr + "被挟持");
                    break;
                case "TamperAlarm":
                    name.setText(keyNameStr + "被篡改");
                    break;
                case "DoorUnlockedAlarm":
                    name.setText(keyNameStr + "未锁门警告");
                    break;
                case "ArmDoorOpenAlarm":
                    name.setText(keyNameStr + "暴力破门警告");
                    break;
                case "LockedAlarm":
                    name.setText(keyNameStr + "卡塞告警");
                    break;
                default:
                    break;
            }
        } else if (TYPE_OPEN.contains(eventCode)) {//开门记录
            icon.setText(R.string.icon_history_open);
            icon.setTextColor(0xffE4E5E6);
            switch (eventCode) {
                case "DoorOpenNotification":
                    name.setText(keyNameStr + "开门");
                    break;
                case "RemoteUnlockNotification":
                    name.setText(keyNameStr + "远程开门");
                    break;
                default:
                    break;
            }
        } else if (TYPE_INFO.contains(eventCode)) {//信息记录
            icon.setText(R.string.icon_history_info);
            icon.setTextColor(0xff3BBC5F);
            switch (eventCode) {
                case "KeyDeletedNotification":
                    name.setText("删除" + keyNameStr);
                    break;
                case "KeyAddedNotification":
                    name.setText("增加" + keyNameStr);
                    break;
                case "LowElectricityAlarm":
                    name.setText("门锁电量低");
                    break;
                case "ReportReset":
                    name.setText("门锁重置");
                    break;
                default:
                    break;
            }
        }

        root_view.setTag(position);
        root_view.setOnClickListener(adapter.getOnClickListener());
    }
}
