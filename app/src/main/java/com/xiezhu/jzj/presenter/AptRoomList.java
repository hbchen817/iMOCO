package com.xiezhu.jzj.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.EHomeSpace;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 09:29
 * Description: 房间列表适配器
 */
public class AptRoomList extends BaseAdapter {
    private class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView status;
    }

    private Context mContext;
    private List<EHomeSpace.roomEntry> mRoomList;

    // 构造
    public AptRoomList(Context context) {
        super();
        mContext = context;
        mRoomList = new ArrayList<EHomeSpace.roomEntry>();
    }

    // 设置数据
    public void setData(List<EHomeSpace.roomEntry> roomList) {
        mRoomList = roomList;
    }

    // 清除数据
    public void clearData() {
        mRoomList.clear();
    }

    // 返回列表条目数量
    @Override
    public int getCount() {
        return mRoomList == null ? 0 : mRoomList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0 > mRoomList.size() ? null : mRoomList.get(arg0);
    }

    // 获取列表条目视图
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        if (mRoomList == null || mRoomList.size() <= position) {
            return LayoutInflater.from(mContext).inflate(R.layout.custom_null, null, true);
        }

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_room, null, true);
            viewHolder.icon = convertView.findViewById(R.id.roomListImgIcon);
            viewHolder.name = convertView.findViewById(R.id.roomListLblName);
            viewHolder.status = convertView.findViewById(R.id.roomListLblStatus);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.icon.setImageResource(ImageProvider.genRoomIcon(mContext, mRoomList.get(position).name));
        viewHolder.name.setText(mRoomList.get(position).name);
        viewHolder.status.setText(String.format(this.mContext.getString(R.string.roomlist_description), mRoomList.get(position).deviceCnt));
        return convertView;
    }
}