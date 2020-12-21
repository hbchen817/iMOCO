package com.xiezhu.jzj.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.xiezhu.jzj.R;
import com.xiezhu.jzj.presenter.AptMessageRecord;
import com.xiezhu.jzj.presenter.CloudDataParser;
import com.xiezhu.jzj.presenter.TSLHelper;
import com.xiezhu.jzj.contract.Constant;
import com.xiezhu.jzj.model.ETSL;
import com.xiezhu.jzj.utility.SrlUtils;
import com.xiezhu.jzj.utility.Utility;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import androidx.annotation.NonNull;

/**
 * Creator: xieshaobing
 * creat time: 2020-05-04 15:06
 * Description: 消息记录界面
 */
public class MessageRecordActivity extends BaseActivity {
    private String mIODId;
    private String mProductKey;
    private List<ETSL.messageRecordContentEntry> mContents;
    private String mContentId;
    private int mContentType;
    private TSLHelper mTSLHelper;
    private int mPageSize = 30;
    private ListView mListView;
    private AptMessageRecord mAPTMessageRecord;
    private long mMinTimeStamp = 0;
    private TextView mTitle;
    private ImageView mDropDown;
    SmartRefreshLayout mSrlFragmentMe;

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mMinTimeStamp=0;
            startQuery();
        }
    };

    private OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            mMinTimeStamp--;
            nextQuery();
        }
    };

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETTSLPROPERTYTIMELINEDATA:
                    // 处理获取属性时间线数据处理
                    ETSL.propertyTimelineListEntry propertyTimelineList = CloudDataParser.processPropertyTimelineData((String)msg.obj);
                    if(propertyTimelineList != null && propertyTimelineList.items != null && propertyTimelineList.items.size() > 0) {
                        if (mMinTimeStamp==0){
                            mAPTMessageRecord.clearData();
                        }
                        List<ETSL.messageRecordEntry> messageRecordEntries = mTSLHelper.processPropertyMessageRecord(mProductKey, propertyTimelineList.items);
                        if(messageRecordEntries != null && messageRecordEntries.size() > 0) {
                            mAPTMessageRecord.addData(messageRecordEntries);
                        }
                        mMinTimeStamp = propertyTimelineList.minTimeStamp;
                    } else {
                        Toast.makeText(MessageRecordActivity.this, getString(R.string.messagerecord_loadend), Toast.LENGTH_LONG).show();
                    }
                    SrlUtils.finishRefresh(mSrlFragmentMe,true);
                    SrlUtils.finishLoadMore(mSrlFragmentMe,true);
                    break;
                case Constant.MSG_CALLBACK_GETTSLEVENTTIMELINEDATA:
                    // 处理获取事件时间线数据处理
                    ETSL.eventTimelineListEntry eventTimelineList = CloudDataParser.processEventTimelineData((String)msg.obj);
                    if(eventTimelineList != null && eventTimelineList.items != null && eventTimelineList.items.size() > 0) {
                        if (mMinTimeStamp==0){
                            mAPTMessageRecord.clearData();
                        }
                        List<ETSL.messageRecordEntry> messageRecordEntries = mTSLHelper.processEventMessageRecord(mProductKey, eventTimelineList.items);
                        if(messageRecordEntries != null && messageRecordEntries.size() > 0) {
                            mAPTMessageRecord.addData(messageRecordEntries);
                        }
                        mMinTimeStamp = eventTimelineList.minTimeStamp;
                    } else {
                        Toast.makeText(MessageRecordActivity.this, getString(R.string.messagerecord_loadend), Toast.LENGTH_LONG).show();
                    }
                    SrlUtils.finishRefresh(mSrlFragmentMe,true);
                    SrlUtils.finishLoadMore(mSrlFragmentMe,true);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagerecord);
        mSrlFragmentMe = (SmartRefreshLayout) findViewById(R.id.srl_fragment_me);
        mSrlFragmentMe.setOnRefreshListener(onRefreshListener);
        mSrlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);

        this.mIODId = getIntent().getStringExtra("iotId");
        this.mProductKey = getIntent().getStringExtra("productKey");

        this.mTitle = (TextView)findViewById(R.id.messageRecordLblTitle);
        this.mDropDown = (ImageView)findViewById(R.id.messageRecordImgDropdown);
        this.mDropDown.setVisibility(View.GONE);

        // 回退处理
        ImageView imgBack = (ImageView)findViewById(R.id.messageRecordImgBack);
        imgBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 消息记录列表处理
        this.mAPTMessageRecord = new AptMessageRecord(this);
        this.mListView = (ListView)findViewById(R.id.messageRecordLstMessageRecord);
        this.mListView.setAdapter(this.mAPTMessageRecord);

        this.mTSLHelper = new TSLHelper(this);
        // 获取设备消息记录内容
        this.mContents = this.mTSLHelper.getMessageRecordContent(this.mProductKey);
        if(this.mContents != null && this.mContents.size() > 0) {
            // 查询第一个内容
            this.mContentId = this.mContents.get(0).id;
            this.mContentType = this.mContents.get(0).type;
            this.mTitle.setText(this.mContents.get(0).name);
            this.startQuery();
            // 如果有多个内容则显示下拉选择
            if(this.mContents.size() > 1) {
                this.mDropDown.setVisibility(View.VISIBLE);
                this.mDropDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 选择消息记录内容
                        Intent in = new Intent(MessageRecordActivity.this, ChoiceContentActivity.class);
                        in.putExtra("productKey", mProductKey);
                        startActivityForResult(in, Constant.REQUESTCODE_CALLCHOICECONTENTACTIVITY);
                    }
                });
            }
        }
        initStatusBar();
    }

    // 嵌入式状态栏
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= 23) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constant.REQUESTCODE_CALLCHOICECONTENTACTIVITY && resultCode == Constant.RESULTCODE_CALLCHOICECONTENTACTIVITY) {
            this.mContentId = data.getStringExtra("id");
            this.mTitle.setText(data.getStringExtra("name"));
            this.mContentType = data.getIntExtra("type", Constant.CONTENTTYPE_PROPERTY);
            this.startQuery();
        }
    }

    // 开始查询
    private void startQuery() {
        mAPTMessageRecord.clearData();
        if(this.mContentType == Constant.CONTENTTYPE_PROPERTY) {
            this.mTSLHelper.getPropertyTimelineData(this.mIODId, this.mContentId, 0, Utility.getCurrentTimeStamp(), this.mPageSize, "desc",
                    mCommitFailureHandler, mResponseErrorHandler, this.mAPIDataHandler);
        } else {
            this.mTSLHelper.getEventTimelineData(this.mIODId, this.mContentId, this.mContentType,0, Utility.getCurrentTimeStamp(), this.mPageSize, "desc",
                    mCommitFailureHandler, mResponseErrorHandler, this.mAPIDataHandler);
        }
    }

    // 下一页查询
    private void nextQuery() {
        if(this.mContentType == Constant.CONTENTTYPE_PROPERTY) {
            this.mTSLHelper.getPropertyTimelineData(this.mIODId, this.mContentId, 0, mMinTimeStamp, this.mPageSize, "desc",
                    mCommitFailureHandler, mResponseErrorHandler, this.mAPIDataHandler);
        } else {
            this.mTSLHelper.getEventTimelineData(this.mIODId, this.mContentId, this.mContentType,0, mMinTimeStamp, this.mPageSize, "desc",
                    mCommitFailureHandler, mResponseErrorHandler, this.mAPIDataHandler);
        }
    }
}