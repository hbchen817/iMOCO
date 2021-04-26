package com.rexense.wholehouse.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;

import com.alibaba.sdk.android.openaccount.ui.widget.LinearLayoutTemplate;
import com.rexense.wholehouse.R;

public class NextStepButton extends LinearLayoutTemplate {
    protected Button button = (Button)this.findViewById("next");

    public NextStepButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typeArray = context.obtainStyledAttributes(attrs, new int[]{16843087});
        String text = typeArray.getString(0);
        if (!TextUtils.isEmpty(text)) {
            this.button.setText(text);
        }

        typeArray.recycle();
        this.useCustomAttrs(context, attrs);
    }

    protected String getLayoutName() {
        return "ali_sdk_openaccount_next_step";
    }

    protected void doUseCustomAttrs(Context context, TypedArray typedArray) {
        if (!this.isInEditMode()) {
            this.button.setBackgroundResource(R.drawable.ali_sdk_openaccount_bg_corners_button);
        }
    }

    public void setText(String text) {
        this.button.setText(text);
    }
}
