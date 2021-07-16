package com.xiezhu.jzj.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.EScene;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-05 09:29
 * Description: 场景触发器设备列表适配器
 */
public class AptSceneTrigger extends BaseAdapter {
    private class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView state;
        private CheckBox select;
        private TextView noHas;
    }

    private Context mContext;
    private List<EScene.triggerEntry> mTriggerList;

    // 构造
    public AptSceneTrigger(Context context) {
        super();
        mContext = context;
        mTriggerList = new ArrayList<EScene.triggerEntry>();
    }

    // 设置数据
    public void setData(List<EScene.triggerEntry> triggerList) {
        mTriggerList = triggerList;
    }

    // 清除数据
    public void clearData() {
        mTriggerList.clear();
    }

    // 返回列表条目数量
    @Override
    public int getCount() {
        return mTriggerList == null ? 0 : mTriggerList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0 > mTriggerList.size() ? null : mTriggerList.get(arg0);
    }

    // 获取网格条目视图
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_trigger, null, true);
            viewHolder = new ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.triggerListImgIcon);
            viewHolder.name = convertView.findViewById(R.id.triggerListLblName);
            viewHolder.state = convertView.findViewById(R.id.triggerListLblState);
            viewHolder.select = convertView.findViewById(R.id.triggerlistChkSelect);
            viewHolder.noHas = convertView.findViewById(R.id.triggerlistLblNohas);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mTriggerList == null || mTriggerList.size() <= position) {
            return LayoutInflater.from(mContext).inflate(R.layout.custom_null, null, true);
        }

        viewHolder.icon.setBackgroundResource(ImageProvider.genProductIcon(mTriggerList.get(position).productKey));
        viewHolder.name.setText(mTriggerList.get(position).name);
        if (mTriggerList.get(position).state != null) {
            viewHolder.state.setText(mTriggerList.get(position).state.value);
        }
        if (mTriggerList.get(position).iotId.equals("")) {
            // 无设备处理
            viewHolder.select.setVisibility(View.GONE);
            viewHolder.noHas.setVisibility(View.VISIBLE);
        } else {
            // 具体设备处理
            viewHolder.select.setVisibility(View.VISIBLE);
            viewHolder.noHas.setVisibility(View.GONE);
        }
        return convertView;
    }
}