package com.xiezhu.jzj.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.CScene;
import com.xiezhu.jzj.model.EScene;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: xieshaobing
 * creat time: 2020-06-10 09:29
 * Description: 场景参数列表适配器
 */
public class AptSceneParameter extends BaseAdapter {
    private class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView state;
        private TextView action;
        private CheckBox select;
        private TextView noHas;
    }

    private Context mContext;
    private List<EScene.parameterEntry> mParameterList;

    // 构造
    public AptSceneParameter(Context context) {
        super();
        mContext = context;
        mParameterList = new ArrayList<EScene.parameterEntry>();
    }

    // 设置数据
    public void setData(List<EScene.parameterEntry> parameterEntry) {
        mParameterList = parameterEntry;
    }

    // 清除数据
    public void clearData() {
        mParameterList.clear();
    }

    // 返回列表条目数量
    @Override
    public int getCount() {
        return mParameterList == null ? 0 : mParameterList.size();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public Object getItem(int arg0) {
        return arg0 > mParameterList.size() ? null : mParameterList.get(arg0);
    }

    // 获取列表条目视图
    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (mParameterList == null || mParameterList.size() <= position) {
            return inflater.inflate(R.layout.custom_null, null, true);
        }

        // 触发设备标题、条件村里、响应设备标题处理
        if (mParameterList.get(position).type == CScene.SPT_TRIGGER_TITLE || mParameterList.get(position).type == CScene.SPT_CONDITION_TITLE ||
                mParameterList.get(position).type == CScene.SPT_RESPONSE_TITLE) {
            convertView = inflater.inflate(R.layout.list_sceneparametertitle, null, true);
            viewHolder.name = convertView.findViewById(R.id.sceneParameterTitleLblName);
            convertView.setTag(viewHolder);
            viewHolder.name.setText(mParameterList.get(position).typeName);
            return convertView;
        }

        // 触发设备处理
        if (mParameterList.get(position).type == CScene.SPT_TRIGGER) {
            convertView = inflater.inflate(R.layout.list_trigger, null, true);
            viewHolder.icon = convertView.findViewById(R.id.triggerListImgIcon);
            viewHolder.name = convertView.findViewById(R.id.triggerListLblName);
            viewHolder.state = convertView.findViewById(R.id.triggerListLblState);
            viewHolder.select = convertView.findViewById(R.id.triggerlistChkSelect);
            viewHolder.select.setTag(position);
            viewHolder.noHas = convertView.findViewById(R.id.triggerlistLblNohas);
            convertView.setTag(viewHolder);
            if (mParameterList.get(position).triggerEntry.image != null && mParameterList.get(position).triggerEntry.image.length() > 0)
                Glide.with(mContext).load(mParameterList.get(position).triggerEntry.image).into(viewHolder.icon);
            else
                viewHolder.icon.setBackgroundResource(ImageProvider.genProductIcon(mParameterList.get(position).triggerEntry.productKey));
            viewHolder.name.setText(mParameterList.get(position).triggerEntry.name);
            if (mParameterList.get(position).triggerEntry.state != null) {
                viewHolder.state.setText(mParameterList.get(position).triggerEntry.state.value);
            }
            if (mParameterList.get(position).triggerEntry.iotId.equals("")) {
                // 无设备处理
                viewHolder.select.setVisibility(View.GONE);
                viewHolder.noHas.setVisibility(View.VISIBLE);
            } else {
                // 具体设备处理
                if (mParameterList.get(position).triggerEntry.isSelected) {
                    viewHolder.select.setChecked(true);
                }
                viewHolder.select.setVisibility(View.VISIBLE);
                viewHolder.noHas.setVisibility(View.GONE);
                viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        final int index = (Integer) buttonView.getTag();
                        mParameterList.get(index).triggerEntry.isSelected = isChecked;
                    }
                });
            }
            return convertView;
        }

        // 时间条件处理
        if (mParameterList.get(position).type == CScene.SPT_CONDITION_TIME) {
            convertView = inflater.inflate(R.layout.list_trigger, null, true);
            viewHolder.icon = convertView.findViewById(R.id.triggerListImgIcon);
            viewHolder.name = convertView.findViewById(R.id.triggerListLblName);
            viewHolder.state = convertView.findViewById(R.id.triggerListLblState);
            viewHolder.select = convertView.findViewById(R.id.triggerlistChkSelect);
            viewHolder.select.setTag(position);
            viewHolder.noHas = convertView.findViewById(R.id.triggerlistLblNohas);
            convertView.setTag(viewHolder);
            viewHolder.icon.setBackgroundResource(R.drawable.time_range);
            viewHolder.name.setText(mParameterList.get(position).conditionTimeEntry.getTimeRangeString());
            viewHolder.state.setText(mParameterList.get(position).conditionTimeEntry.getWeekRepeatString(mContext));
            viewHolder.select.setVisibility(View.VISIBLE);
            viewHolder.noHas.setVisibility(View.GONE);
            if (mParameterList.get(position).conditionTimeEntry.isSelected) {
                viewHolder.select.setChecked(true);
            }
            viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final int index = (Integer) buttonView.getTag();
                    mParameterList.get(index).conditionTimeEntry.isSelected = isChecked;
                }
            });

            return convertView;
        }

        // 状态条件处理
        if (mParameterList.get(position).type == CScene.SPT_CONDITION_STATE) {
            convertView = inflater.inflate(R.layout.list_trigger, null, true);
            viewHolder.icon = convertView.findViewById(R.id.triggerListImgIcon);
            viewHolder.name = convertView.findViewById(R.id.triggerListLblName);
            viewHolder.state = convertView.findViewById(R.id.triggerListLblState);
            viewHolder.select = convertView.findViewById(R.id.triggerlistChkSelect);
            viewHolder.select.setTag(position);
            viewHolder.noHas = convertView.findViewById(R.id.triggerlistLblNohas);
            convertView.setTag(viewHolder);
            String image = mParameterList.get(position).conditionStateEntry.image;
            if (image != null && image.length() > 0)
                Glide.with(mContext).load(image).into(viewHolder.icon);
            else
                viewHolder.icon.setBackgroundResource(ImageProvider.genProductIcon(mParameterList.get(position).conditionStateEntry.productKey));
            viewHolder.name.setText(mParameterList.get(position).conditionStateEntry.name);
            if (mParameterList.get(position).conditionStateEntry.state != null) {
                viewHolder.state.setText(mParameterList.get(position).conditionStateEntry.state.value);
            }
            if (mParameterList.get(position).conditionStateEntry.iotId.equals("")) {
                // 无设备处理
                viewHolder.select.setVisibility(View.GONE);
                viewHolder.noHas.setVisibility(View.VISIBLE);
            } else {
                // 具体设备处理
                if (mParameterList.get(position).conditionStateEntry.isSelected) {
                    viewHolder.select.setChecked(true);
                }
                viewHolder.select.setVisibility(View.VISIBLE);
                viewHolder.noHas.setVisibility(View.GONE);
                viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        final int index = (Integer) buttonView.getTag();
                        mParameterList.get(index).conditionStateEntry.isSelected = isChecked;
                    }
                });
            }
            return convertView;
        }

        // 响应设备处理
        if (mParameterList.get(position).type == CScene.SPT_RESPONSE) {
            convertView = inflater.inflate(R.layout.list_response, null, true);
            viewHolder.icon = convertView.findViewById(R.id.responseListImgIcon);
            viewHolder.name = convertView.findViewById(R.id.responseListLblName);
            viewHolder.action = convertView.findViewById(R.id.responseListLblAction);
            viewHolder.select = convertView.findViewById(R.id.responselistChkSelect);
            viewHolder.select.setTag(position);
            viewHolder.noHas = convertView.findViewById(R.id.responselistLblNohas);
            convertView.setTag(viewHolder);
            String image = mParameterList.get(position).responseEntry.image;
            if (image != null && image.length() > 0)
                Glide.with(mContext).load(image).into(viewHolder.icon);
            else
                viewHolder.icon.setBackgroundResource(ImageProvider.genProductIcon(mParameterList.get(position).responseEntry.productKey));
            viewHolder.name.setText(mParameterList.get(position).responseEntry.name);
            // 处理属性状态
            if (mParameterList.get(position).responseEntry.state != null) {
                viewHolder.action.setText(mParameterList.get(position).responseEntry.state.value);
            }
            // 处理服务
            if (mParameterList.get(position).responseEntry.service != null) {
                // 目前只处理单参数
                if (mParameterList.get(position).responseEntry.service.args != null &&
                        mParameterList.get(position).responseEntry.service.args.size() > 0) {
                    viewHolder.action.setText(mParameterList.get(position).responseEntry.service.args.get(0).value);
                }
            }
            if (mParameterList.get(position).responseEntry.iotId.equals("")) {
                // 无设备处理
                viewHolder.select.setVisibility(View.GONE);
                viewHolder.noHas.setVisibility(View.VISIBLE);
            } else {
                // 具体设备处理
                if (mParameterList.get(position).responseEntry.isSelected) {
                    viewHolder.select.setChecked(true);
                }
                viewHolder.select.setVisibility(View.VISIBLE);
                viewHolder.noHas.setVisibility(View.GONE);
                viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        final int index = (Integer) buttonView.getTag();
                        mParameterList.get(index).responseEntry.isSelected = isChecked;
                    }
                });
            }
            return convertView;
        }

        return convertView;
    }
}