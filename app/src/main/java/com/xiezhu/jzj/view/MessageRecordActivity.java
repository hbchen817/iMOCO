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

import com.google.gson.Gson;
import com.vise.log.ViseLog;
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

import org.jetbrains.annotations.NotNull;

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

    private final OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(@NonNull RefreshLayout refreshLayout) {
            mMinTimeStamp = 0;
            startQuery();
        }
    };

    private final OnLoadMoreListener onLoadMoreListener = new OnLoadMoreListener() {
        @Override
        public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
            mMinTimeStamp--;
            nextQuery();
        }
    };

    // API数据处理器
    private final Handler mAPIDataHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETTSLPROPERTYTIMELINEDATA:
                    // 处理获取属性时间线数据处理
                    ETSL.propertyTimelineListEntry propertyTimelineList = CloudDataParser.processPropertyTimelineData((String) msg.obj);
                    if (propertyTimelineList != null && propertyTimelineList.items != null && propertyTimelineList.items.size() > 0) {
                        if (mMinTimeStamp == 0) {
                            mAPTMessageRecord.clearData();
                        }
                        List<ETSL.messageRecordEntry> messageRecordEntries = mTSLHelper.processPropertyMessageRecord(mProductKey, propertyTimelineList.items);
                        if (messageRecordEntries != null && messageRecordEntries.size() > 0) {
                            mAPTMessageRecord.addData(messageRecordEntries);
                        }
                        mMinTimeStamp = propertyTimelineList.minTimeStamp;
                    } else {
                        Toast.makeText(MessageRecordActivity.this, getString(R.string.messagerecord_loadend), Toast.LENGTH_LONG).show();
                    }
                    SrlUtils.finishRefresh(mSrlFragmentMe, true);
                    SrlUtils.finishLoadMore(mSrlFragmentMe, true);
                    break;
                case Constant.MSG_CALLBACK_GETTSLEVENTTIMELINEDATA:
                    // 处理获取事件时间线数据处理
                    ETSL.eventTimelineListEntry eventTimelineList = CloudDataParser.processEventTimelineData((String) msg.obj);
                    if (eventTimelineList != null && eventTimelineList.items != null && eventTimelineList.items.size() > 0) {
                        if (mMinTimeStamp == 0) {
                            mAPTMessageRecord.clearData();
                        }
                        List<ETSL.messageRecordEntry> messageRecordEntries = mTSLHelper.processEventMessageRecord(mProductKey, eventTimelineList.items);
                        if (messageRecordEntries != null && messageRecordEntries.size() > 0) {
                            mAPTMessageRecord.addData(messageRecordEntries);
                        }
                        mMinTimeStamp = eventTimelineList.minTimeStamp;
                    } else {
                        Toast.makeText(MessageRecordActivity.this, getString(R.string.messagerecord_loadend), Toast.LENGTH_LONG).show();
                    }
                    SrlUtils.finishRefresh(mSrlFragmentMe, true);
                    SrlUtils.finishLoadMore(mSrlFragmentMe, true);
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

        mIODId = getIntent().getStringExtra("iotId");
        mProductKey = getIntent().getStringExtra("productKey");

        mTitle = findViewById(R.id.messageRecordLblTitle);
        mTitle.setText(R.string.messagerecord_title);
        mDropDown = findViewById(R.id.messageRecordImgDropdown);
        mDropDown.setVisibility(View.GONE);

        // 回退处理
        ImageView imgBack = findViewById(R.id.messageRecordImgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 消息记录列表处理
        mAPTMessageRecord = new AptMessageRecord(this);
        mListView = findViewById(R.id.messageRecordLstMessageRecord);
        mListView.setAdapter(mAPTMessageRecord);

        mTSLHelper = new TSLHelper(this);
        // 获取设备消息记录内容
        mContents = mTSLHelper.getMessageRecordContent(mProductKey);
        if (mContents != null && mContents.size() > 0) {
            // 查询第一个内容
            mContentId = mContents.get(0).id;
            mContentType = mContents.get(0).type;
            mTitle.setText(mContents.get(0).name);
            startQuery();
            // 如果有多个内容则显示下拉选择
            if (mContents.size() > 1) {
                mDropDown.setVisibility(View.VISIBLE);
                mDropDown.setOnClickListener(new View.OnClickListener() {
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

        if (requestCode == Constant.REQUESTCODE_CALLCHOICECONTENTACTIVITY && resultCode == Constant.RESULTCODE_CALLCHOICECONTENTACTIVITY) {
            mContentId = data.getStringExtra("id");
            mTitle.setText(data.getStringExtra("name"));
            mContentType = data.getIntExtra("type", Constant.CONTENTTYPE_PROPERTY);
            startQuery();
        }
    }

    // 开始查询
    private void startQuery() {
        mAPTMessageRecord.clearData();
        if (mContentType == Constant.CONTENTTYPE_PROPERTY) {
            mTSLHelper.getPropertyTimelineData(mIODId, mContentId, 0, Utility.getCurrentTimeStamp(), mPageSize, "desc",
                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        } else {
            mTSLHelper.getEventTimelineData(mIODId, mContentId, mContentType, 0, Utility.getCurrentTimeStamp(), mPageSize, "desc",
                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    }

    // 下一页查询
    private void nextQuery() {
        if (mContentType == Constant.CONTENTTYPE_PROPERTY) {
            mTSLHelper.getPropertyTimelineData(mIODId, mContentId, 0, mMinTimeStamp, mPageSize, "desc",
                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        } else {
            mTSLHelper.getEventTimelineData(mIODId, mContentId, mContentType, 0, mMinTimeStamp, mPageSize, "desc",
                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
        }
    }
}