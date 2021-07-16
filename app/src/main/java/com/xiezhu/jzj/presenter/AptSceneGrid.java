package com.xiezhu.jzj.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.model.EScene;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 09:29
 * Description: 场景网格适配器
 */
public class AptSceneGrid extends BaseAdapter {
    private class ViewHolder {
        private TextView name;
    }

    private Context mContext;
    private List<EScene.sceneListItemEntry> mSceneList;

    // 构造
    public AptSceneGrid(Context context) {
        super();
        this.mContext = context;
        this.mSceneList = new ArrayList<EScene.sceneListItemEntry>();
    }

    // 设置数据
    public void setData(List<EScene.sceneListItemEntry> sceneList) {
        this.mSceneList = sceneList;
    }

    // 清除数据
    public void clearData() {
        this.mSceneList.clear();
    }

    // 返回列表条目数量
    @Override
    public int getCount() {
        return this.mSceneList == null ? 0 : this.mSceneList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0 > this.mSceneList.size() ? null : this.mSceneList.get(arg0);
    }

    // 获取网格条目视图
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_scene, null, true);
            viewHolder.name = (TextView) convertView.findViewById(R.id.sceneLblName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mSceneList == null || position >= mSceneList.size()) {
            return LayoutInflater.from(mContext).inflate(R.layout.custom_null, null, true);
        }
        viewHolder.name.setText(mSceneList.get(position).name);
        return convertView;
    }
}