package com.rexense.imoco.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import java.util.List;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.databinding.ActivityChoiceWifiBinding;
import com.rexense.imoco.model.EWiFi;
import com.rexense.imoco.presenter.AptWiFiList;
import com.rexense.imoco.utility.WiFiHelper;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 选择WiFi
 */
public class ChoiceWiFiActivity extends Activity {
    private ActivityChoiceWifiBinding mViewBinding;

    private List<EWiFi.WiFiEntry> mWiFiList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBinding = ActivityChoiceWifiBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.includeToolbar.includeTitleLblTitle.setText(R.string.wifi_title);

        // 回退处理
        mViewBinding.includeToolbar.includeTitleImgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 获取WiFi
        WiFiHelper wiFiHelper = new WiFiHelper(this);
        mWiFiList = wiFiHelper.getSSIDList();
        if (mWiFiList != null && mWiFiList.size() > 0) {
            mViewBinding.choiceWiFiLstWiFi.setAdapter(new AptWiFiList(this, mWiFiList));
            mViewBinding.choiceWiFiLstWiFi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 返回所选WiFi
                    Intent intent = new Intent();
                    intent.putExtra("ssid", mWiFiList.get(position).ssid);
                    setResult(Constant.RESULTCODE_CALLCHOICEWIFIACTIVITY, intent);
                    finish();
                }
            });
        }
    }
}