package com.xiezhu.jzj.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.EWiFi;
import com.xiezhu.jzj.presenter.AptWiFiList;
import com.xiezhu.jzj.utility.WiFiHelper;

/**
 * Creator: xieshaobing
 * creat time: 2020-04-14 15:29
 * Description: 选择WiFi
 */
public class ChoiceWiFiActivity extends Activity {
    private List<EWiFi.WiFiEntry> mWiFiList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_wifi);

        TextView title = (TextView) findViewById(R.id.includeTitleLblTitle);
        title.setText(R.string.wifi_title);

        // 回退处理
        ImageView imgBack = (ImageView) findViewById(R.id.includeTitleImgBack);
        imgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 获取WiFi
        WiFiHelper wiFiHelper = new WiFiHelper(this);
        mWiFiList = wiFiHelper.getSSIDList();
        if (mWiFiList != null && mWiFiList.size() > 0) {
            ListView wifiList = (ListView) findViewById(R.id.choiceWiFiLstWiFi);
            wifiList.setAdapter(new AptWiFiList(this, mWiFiList));
            wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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