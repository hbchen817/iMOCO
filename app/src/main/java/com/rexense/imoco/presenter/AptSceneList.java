package com.rexense.imoco.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.annotation.NonNull;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.event.RefreshData;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.utility.ToastUtils;
import com.rexense.imoco.view.SceneMaintainActivity;

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
    private AptSceneListCallback mCallback;

    // 构造
    public AptSceneList(Context context, List<EScene.sceneListItemEntry> list, Handler commitFailureHandler, Handler responseErrorHandler, Handler processDataHandler,
                        AptSceneListCallback callback) {
        super();
        this.mContext = context;
        this.mCommitFailureHandler = commitFailureHandler;
        this.mResponseErrorHandler = responseErrorHandler;
        this.mProcessDataHandler = processDataHandler;
        this.mSceneList = list;
        this.mDeleteList = new ArrayList<deleteTag>();
        this.mCallback = callback;
    }

    // 设置数据
    public void setData(List<EScene.sceneListItemEntry> sceneList) {
        // this.mSceneList = sceneList;
        if (sceneList != null) {
            //this.mSceneList.clear();
            //this.mSceneList.addAll(sceneList);
            mDeleteList.clear();

            for (int i = 0; i < sceneList.size(); i++) {
                deleteTag d = new deleteTag();
                d.isDeleted = false;
                mDeleteList.add(d);
            }
        }
        this.notifyDataSetChanged();
    }

    // 清除数据
    public void clearData() {
        this.mSceneList.clear();
        this.mDeleteList.clear();
    }

    // 设置删除
    public void setDelete(int position) {
        if (this.mDeleteList == null || this.mDeleteList.size() == 0) {
            return;
        }

        for (int i = 0; i < this.mDeleteList.size(); i++) {
            if (i == position) {
                this.mDeleteList.get(position).isDeleted = !this.mDeleteList.get(position).isDeleted;
            } else {
                this.mDeleteList.get(i).isDeleted = false;
            }
        }

        this.notifyDataSetChanged();
    }

    // 取消删除
    public void cancelDelete(int position) {
        if (this.mDeleteList == null || this.mDeleteList.size() == 0) {
            return;
        }

        this.mDeleteList.get(position).isDeleted = false;

        this.notifyDataSetChanged();
    }

    // 删除数据
    public void deleteData(String sceneId) {
        if (this.mSceneList == null || this.mSceneList.size() == 0) {
            return;
        }
        int index = 0;
        for (EScene.sceneListItemEntry scene : this.mSceneList) {
            if (scene.id.equalsIgnoreCase(sceneId)) {
                this.mSceneList.remove(index);
                this.mDeleteList.remove(index);
                break;
            }
            index++;
        }
        this.notifyDataSetChanged();
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

    // 获取列表条目视图
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        convertView = inflater.inflate(R.layout.list_scene, null, true);
        viewHolder.name = convertView.findViewById(R.id.sceneListLblName);
        viewHolder.type = convertView.findViewById(R.id.sceneListLblType);
        viewHolder.delete = convertView.findViewById(R.id.sceneListLblDelete);
        viewHolder.edit = convertView.findViewById(R.id.sceneListImgEdit);
        viewHolder.dividerBottom = convertView.findViewById(R.id.divider_bottom);
        if (position == this.mSceneList.size() - 1)
            viewHolder.dividerBottom.setVisibility(View.VISIBLE);
        else viewHolder.dividerBottom.setVisibility(View.GONE);
        viewHolder.delete.setTag(position);
        viewHolder.edit.setTag(position);
        convertView.setTag(viewHolder);

        viewHolder.name.setText(this.mSceneList.get(position).name);
        viewHolder.type.setText(this.mSceneList.get(position).catalogId.equals(CScene.TYPE_MANUAL) ? this.mContext.getString(R.string.scenetype_manual) : this.mContext.getString(R.string.scenetype_automatic));
        if (this.mDeleteList.get(position).isDeleted) {
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
                            //new SceneManager(mContext).deleteScene(mSceneList.get(index).id, mCommitFailureHandler, mResponseErrorHandler, mProcessDataHandler);
                            new SceneManager(mContext).deleteScene(mSceneList.get(index).id, null, null, new Myhandler(mContext));
                            mDeleteList.remove(position);
                            mCallback.onDelItem(mSceneList.get(index).id);
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

    public interface AptSceneListCallback {
        void onDelItem(String sceneId);
    }

    private class Myhandler extends Handler {
        private WeakReference<Context> ref;

        public Myhandler(Context context) {
            ref = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (ref.get() == null) return;
            RefreshData.refreshHomeSceneListData();
        }
    }
}