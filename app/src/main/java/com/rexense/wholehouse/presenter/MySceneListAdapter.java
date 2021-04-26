package com.rexense.wholehouse.presenter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rexense.wholehouse.R;
import com.rexense.wholehouse.contract.CScene;
import com.rexense.wholehouse.model.EScene;

import java.util.ArrayList;
import java.util.List;

public class MySceneListAdapter extends RecyclerView.Adapter {
    private List<EScene.sceneListItemEntry> mList;
    private List<Boolean> mDeleteList;
    private Context mContext;
    private MySceneListCallback mCallback;
    private Handler mCommitFailureHandler, mResponseErrorHandler, mProcessDataHandler;

    public MySceneListAdapter(Context context, List<EScene.sceneListItemEntry> list, MySceneListCallback callback, Handler commitFailureHandler, Handler responseErrorHandler, Handler processDataHandler) {
        mContext = context;
        mDeleteList = new ArrayList<>();
        mList = list;
        mCallback = callback;
        this.mCommitFailureHandler = commitFailureHandler;
        this.mResponseErrorHandler = responseErrorHandler;
        this.mProcessDataHandler = processDataHandler;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_scene, null, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mDeleteList.size() != mList.size()) {
            mDeleteList.clear();
            for (int i = 0; i < getItemCount(); i++) {
                mDeleteList.add(false);
            }
        }
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            EScene.sceneListItemEntry entry = mList.get(position);
            viewHolder.sceneListLblName.setText(entry.name);
            viewHolder.sceneListLblType.setText(entry.catalogId.equals(CScene.TYPE_MANUAL)
                    ? this.mContext.getString(R.string.scenetype_manual) : this.mContext.getString(R.string.scenetype_automatic));
            viewHolder.sceneListLblDelete.setVisibility(mDeleteList.get(position) ? View.VISIBLE : View.GONE);
            viewHolder.sceneListRelItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyDataSetChangedCustom();
                    mCallback.onItemClick(position);
                }
            });
            viewHolder.sceneListRelItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    boolean f = mDeleteList.get(position);
                    mDeleteList.set(position, !f);
                    notifyDataSetChanged();
                    return false;
                }
            });
            viewHolder.sceneListLblDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            new SceneManager(mContext).deleteScene(entry.id, mCommitFailureHandler, mResponseErrorHandler, mProcessDataHandler);
                        }
                    });
                    alert.show();
                }
            });
        }
    }

    public void notifyDataSetChangedCustom() {
        mDeleteList.clear();
        for (int i = 0; i < getItemCount(); i++) {
            mDeleteList.add(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mList == null) return 0;
        return mList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout sceneListRelItem;
        TextView sceneListLblName;
        TextView sceneListLblType;
        TextView sceneListLblDelete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            sceneListRelItem = (RelativeLayout) itemView.findViewById(R.id.sceneListRelItem);
            sceneListLblName = (TextView) itemView.findViewById(R.id.sceneListLblName);
            sceneListLblType = (TextView) itemView.findViewById(R.id.sceneListLblType);
            sceneListLblDelete = (TextView) itemView.findViewById(R.id.sceneListLblDelete);
        }
    }

    public interface MySceneListCallback {
        void onItemClick(int pos);
    }
}
