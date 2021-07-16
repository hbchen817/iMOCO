package com.xiezhu.jzj.presenter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;

import com.vise.log.ViseLog;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CScene;
import com.xiezhu.jzj.model.EScene;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-13 09:29
 * Description: 场景列表适配器
 */
public class AptSceneList extends BaseAdapter {
    private class ViewHolder {
        private TextView name;
        private TextView type;
        private TextView delete;
        private ImageView edit;
        private View dividerBottom;
    }

    private class deleteTag {
        private Boolean isDeleted;
    }

    private Context mContext;
    private Handler mCommitFailureHandler, mResponseErrorHandler, mProcessDataHandler;
    private List<EScene.sceneListItemEntry> mSceneList;
    private List<deleteTag> mDeleteList;


    // 构造
    public AptSceneList(Context context, Handler commitFailureHandler, Handler responseErrorHandler, Handler processDataHandler) {
        super();
        mContext = context;
        mCommitFailureHandler = commitFailureHandler;
        mResponseErrorHandler = responseErrorHandler;
        mProcessDataHandler = processDataHandler;
        mSceneList = new ArrayList<EScene.sceneListItemEntry>();
        mDeleteList = new ArrayList<deleteTag>();
    }

    // 设置数据
    public void setData(List<EScene.sceneListItemEntry> sceneList) {
        // this.mSceneList = sceneList;
        if (sceneList != null) {
            mSceneList.clear();
            mSceneList.addAll(sceneList);
            mDeleteList.clear();

            for (int i = 0; i < sceneList.size(); i++) {
                deleteTag d = new deleteTag();
                d.isDeleted = false;
                mDeleteList.add(d);
            }
        }
        notifyDataSetChanged();
    }

    // 清除数据
    public void clearData() {
        mSceneList.clear();
        mDeleteList.clear();
    }

    // 设置删除
    public void setDelete(int position) {
        if (mDeleteList == null || mDeleteList.size() == 0) {
            return;
        }

        for (int i = 0; i < mDeleteList.size(); i++) {
            if (i == position) {
                mDeleteList.get(position).isDeleted = !mDeleteList.get(position).isDeleted;
            } else {
                mDeleteList.get(i).isDeleted = false;
            }
        }

        notifyDataSetChanged();
    }

    // 取消删除
    public void cancelDelete(int position) {
        if (mDeleteList == null || mDeleteList.size() == 0) {
            return;
        }

        mDeleteList.get(position).isDeleted = false;

        notifyDataSetChanged();
    }

    // 删除数据
    public void deleteData(String sceneId) {
        if (mSceneList == null || mSceneList.size() == 0) {
            return;
        }
        int index = 0;
        for (EScene.sceneListItemEntry scene : mSceneList) {
            if (scene.id.equalsIgnoreCase(sceneId)) {
                mSceneList.remove(index);
                mDeleteList.remove(index);
                break;
            }
            index++;
        }
        notifyDataSetChanged();
    }

    public void hideDeleteButton() {
        // 将删除隐藏掉
        for (deleteTag tag : mDeleteList) {
            tag.isDeleted = false;
        }
        notifyDataSetChanged();
    }

    // 返回列表条目数量
    @Override
    public int getCount() {
        return mSceneList == null ? 0 : mSceneList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0 > mSceneList.size() ? null : mSceneList.get(arg0);
    }

    // 获取列表条目视图
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_scene, null, true);
            viewHolder.name = convertView.findViewById(R.id.sceneListLblName);
            viewHolder.type = convertView.findViewById(R.id.sceneListLblType);
            viewHolder.delete = convertView.findViewById(R.id.sceneListLblDelete);
            viewHolder.edit = convertView.findViewById(R.id.sceneListImgEdit);
            viewHolder.dividerBottom = convertView.findViewById(R.id.divider_bottom);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mSceneList == null || position >= mSceneList.size()) {
            return LayoutInflater.from(mContext).inflate(R.layout.custom_null, null, true);
        }

        viewHolder.delete.setTag(position);
        viewHolder.edit.setTag(position);
        if (position == mSceneList.size() - 1)
            viewHolder.dividerBottom.setVisibility(View.VISIBLE);
        else viewHolder.dividerBottom.setVisibility(View.GONE);

        viewHolder.name.setText(mSceneList.get(position).name);
        viewHolder.type.setText(mSceneList.get(position).catalogId.equals(CScene.TYPE_MANUAL) ? this.mContext.getString(R.string.scenetype_manual) : this.mContext.getString(R.string.scenetype_automatic));
        if (mDeleteList.get(position).isDeleted) {
            viewHolder.delete.setVisibility(View.VISIBLE);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int index = Integer.parseInt(v.getTag().toString());

                    AlertDialog alert = new AlertDialog.Builder(mContext).create();
                    alert.setIcon(R.drawable.dialog_quest);
                    alert.setTitle(R.string.dialog_title);
                    alert.setMessage(mContext.getString(R.string.scene_delete_confirm));
                    //添加否按钮
                    alert.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    //添加是按钮
                    alert.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            new SceneManager(mContext).deleteScene(mSceneList.get(index).id, mCommitFailureHandler, mResponseErrorHandler, mProcessDataHandler);
                        }
                    });
                    alert.show();
                }
            });
        } else {
            viewHolder.delete.setVisibility(View.GONE);
        }
//		viewHolder.edit.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				final int index = Integer.parseInt(v.getTag().toString());
//
//				// 将删除隐藏掉
//				for(deleteTag tag : mDeleteList){
//					tag.isDeleted = false;
//				}
//				notifyDataSetChanged();
//
//				// 获取场景模板代码
//				int sceneModelCode = new SceneManager(mContext).getSceneModelCode(mSceneList.get(position).description);
//				if(sceneModelCode < CScene.SMC_NIGHT_RISE_ON){
//					// 非模板场景处理
//					PluginHelper.editScene(mContext, CScene.TYPE_IFTTT, mSceneList.get(index).catalogId, SystemParameter.getInstance().getHomeId(), mSceneList.get(index).id);
//					SystemParameter.getInstance().setIsRefreshSceneListData(true);
//				} else {
//					// 模板场景处理
//					Intent intent = new Intent(mContext, SceneMaintainActivity.class);
//					intent.putExtra("operateType", CScene.OPERATE_UPDATE);
//					intent.putExtra("sceneId", mSceneList.get(index).id);
//					intent.putExtra("name", mSceneList.get(index).name);
//					intent.putExtra("sceneModelCode", new SceneManager(mContext).getSceneModelCode(mSceneList.get(index).description));
//					intent.putExtra("sceneModelIcon", ImageProvider.genSceneIcon(mContext, mSceneList.get(index).description));
//					intent.putExtra("sceneNumber", mSceneList == null ? 0 : mSceneList.size());
//					mContext.startActivity(intent);
//				}
//			}
//		});


        return convertView;
    }
}