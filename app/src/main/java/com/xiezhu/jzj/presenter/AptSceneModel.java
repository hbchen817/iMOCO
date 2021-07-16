package com.xiezhu.jzj.presenter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vise.log.ViseLog;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CScene;
import com.xiezhu.jzj.model.EScene;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 09:29
 * Description: 场景模板列表适配器
 */
public class AptSceneModel extends BaseAdapter {
    private class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView description;
    }

    private Context mContext;
    private List<EScene.sceneModelEntry> mModelList;

    // 构造
    public AptSceneModel(Context context) {
        super();
        mContext = context;
        mModelList = new ArrayList<EScene.sceneModelEntry>();
    }

    // 设置数据
    public void setData(List<EScene.sceneModelEntry> modelList) {
        mModelList = modelList;
    }

    // 清除数据
    public void clearData() {
        mModelList.clear();
    }

    // 返回列表条目数量
    @Override
    public int getCount() {
        return mModelList == null ? 0 : mModelList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0 > mModelList.size() ? null : mModelList.get(arg0);
    }

    // 获取网格条目视图
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        if (mModelList == null || mModelList.size() <= position) {
            return inflater.inflate(R.layout.custom_null, null, true);
        }
        if (mModelList.get(position).code == CScene.SMC_NONE) {
            convertView = inflater.inflate(R.layout.list_scenemodel_title, null, true);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.sceneModelLblName);
            convertView.setTag(viewHolder);
            if (mModelList == null || mModelList.size() <= position) {
                return inflater.inflate(R.layout.custom_null, null, true);
            }
            viewHolder.name.setText(mModelList.get(position).name);
        } else {
            convertView = inflater.inflate(R.layout.list_scenemodel, null, true);
            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.sceneModelLblName);
            viewHolder.description = convertView.findViewById(R.id.sceneModelLblDescription);
            viewHolder.icon = convertView.findViewById(R.id.sceneModelImgIcon);
            convertView.setTag(viewHolder);
            if (mModelList == null || mModelList.size() <= position) {
                return inflater.inflate(R.layout.custom_null, null, true);
            }
            viewHolder.name.setText(mModelList.get(position).name);
            viewHolder.icon.setImageResource(mModelList.get(position).icon);
            viewHolder.description.setText(new SceneManager(mContext).getSceneModelDescription(mModelList.get(position).code));
        }
        return convertView;
    }
}