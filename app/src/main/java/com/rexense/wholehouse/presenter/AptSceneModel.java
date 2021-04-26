package com.rexense.wholehouse.presenter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CScene;
import com.rexense.wholehouse.model.EScene;

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
		this.mContext = context;
		this.mModelList = new ArrayList<EScene.sceneModelEntry>();
	}

	// 设置数据
	public void setData(List<EScene.sceneModelEntry> modelList) {
		this.mModelList = modelList;
	}

	// 清除数据
	public void clearData() {
		this.mModelList.clear();
	}

	// 返回列表条目数量
	@Override
	public int getCount() {
		return this.mModelList == null ? 0 : this.mModelList.size();
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0 > this.mModelList.size() ? null : this.mModelList.get(arg0);
	}

	// 获取网格条目视图
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = new ViewHolder();
		LayoutInflater inflater = LayoutInflater.from(this.mContext);
		if(this.mModelList.get(position).code == CScene.SMC_NONE) {
			convertView = inflater.inflate(R.layout.list_scenemodel_title, null, true);
			viewHolder.name = (TextView) convertView.findViewById(R.id.sceneModelLblName);
			viewHolder.name.setText(this.mModelList.get(position).name);
		} else {
			convertView = inflater.inflate(R.layout.list_scenemodel, null, true);
			viewHolder.name = (TextView) convertView.findViewById(R.id.sceneModelLblName);
			viewHolder.description = (TextView) convertView.findViewById(R.id.sceneModelLblDescription);
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.sceneModelImgIcon);
			convertView.setTag(viewHolder);
			viewHolder.name.setText(this.mModelList.get(position).name);
			viewHolder.icon.setImageResource(this.mModelList.get(position).icon);
			viewHolder.description.setText(new SceneManager(this.mContext).getSceneModelDescription(this.mModelList.get(position).code));
		}
		return convertView;
	}
}