package com.rexense.imoco.view;

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

import com.rexense.imoco.R;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EWiFi;
import com.rexense.imoco.presenter.AptWiFiList;
import com.rexense.imoco.utility.WiFiHelper;

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

        TextView title = (TextView)findViewById(R.id.includeTitleLblTitle);
        title.setText(R.string.wifi_title);

        // 回退处理
        ImageView imgAdd = (ImageView)findViewById(R.id.includeTitleImgBack);
        imgAdd.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 获取WiFi
        WiFiHelper wiFiHelper = new WiFiHelper(this);
        this.mWiFiList = wiFiHelper.getSSIDList();
        if(this.mWiFiList != null && this.mWiFiList.size() > 0) {
            ListView wifiList = (ListView)findViewById(R.id.choiceWiFiLstWiFi);
            wifiList.setAdapter(new AptWiFiList(this, this.mWiFiList));
            wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
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