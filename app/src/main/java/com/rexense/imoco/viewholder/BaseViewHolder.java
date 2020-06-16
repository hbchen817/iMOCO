package com.rexense.imoco.viewholder;


import android.util.SparseArray;
import android.view.View;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
    private SparseArray<View> views;
    private View mItemView;
    public BaseViewHolder(View itemView) {
        super(itemView);
        views = new SparseArray<>();
        this.mItemView = itemView;
    }

    public View getView(int resID) {
        View view = views.get(resID);

        if (view == null) {
            view = mItemView.findViewById(resID);
            views.put(resID,view);
        }

        return view;
    }

//    public abstract void setUpView(T model, int position, CommonAdapter adapter);
    public abstract void setUpView(T model, int position, CommonAdapter adapter, List<Integer> payloads);
}
