package com.laffey.smart.viewholder;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.laffey.smart.R;
import com.laffey.smart.model.ItemHistoryMsg;

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
                case 1:
                    keyName.append("指纹钥匙");
                    break;
                case 2:
                    keyName.append("密码钥匙");
                    break;
                case 3:
                    keyName.append("卡钥匙");
                    break;
                case 4:
                    keyName.append("机械钥匙");
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
                    name.setText(keyNameStr + "被撬");
                    break;
                case "DoorUnlockedAlarm":
                    name.setText(keyNameStr + "未锁门警告");
                    break;
                case "ArmDoorOpenAlarm":
                    name.setText(keyNameStr + "暴力破门警告");
                    break;
                case "LockedAlarm":
                    name.setText(keyNameStr + "被锁定");
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
                    if (!TextUtils.isEmpty(model.getKeyID()) && model.getKeyID().equals("65535")) {
                        name.setText("删除全部钥匙");
                    } else {
                        name.setText("删除" + keyNameStr);
                    }
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
