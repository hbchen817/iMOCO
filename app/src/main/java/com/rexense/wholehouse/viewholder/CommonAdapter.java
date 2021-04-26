package com.rexense.wholehouse.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rexense.wholehouse.model.Visitable;
import com.rexense.wholehouse.typefactory.TypeFactory;
import com.rexense.wholehouse.typefactory.TypeFactoryForList;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CommonAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private TypeFactory typeFactory;
    private List<Visitable> models;
    private List<Integer> payloads = new ArrayList<>();
    private Context mContext;
    private View.OnClickListener onClickListener;
    public CommonAdapter(List<Visitable> models, Context mContext) {
        this.models = models;
        this.typeFactory = new TypeFactoryForList();
        this.mContext = mContext;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(viewType,parent,false);
        return typeFactory.createViewHolder(viewType,itemView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setUpView(models.get(position),position,this,payloads);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//        if(manager instanceof GridLayoutManager) {
//            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
//            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                @Override
//                public int getSpanSize(int position) {
//                    if (getItemViewType(position)==R.layout.item_index_tuijianhead||getItemViewType(position) == R.layout.item_index_head){
//                        return gridManager.getSpanCount();
//                    }else {
//                        return 1;
//                    }
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
        if(null == models){
            return  0;
        }
        return models.size();
    }

    @Override
    public int getItemViewType(int position) {
        return models.get(position).type(typeFactory);
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public List<Integer> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<Integer> payloads) {
        this.payloads = payloads;
    }
}