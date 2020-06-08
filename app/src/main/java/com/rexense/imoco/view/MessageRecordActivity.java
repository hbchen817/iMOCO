package com.rexense.imoco.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import com.rexense.imoco.R;
import com.rexense.imoco.presenter.AptMessageRecord;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.TSLHelper;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.ETSL;
import com.rexense.imoco.utility.Utility;

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
    private int mTotalItemCount;
    private long mMinTimeStamp = 0;
    private TextView mTitle;
    private ImageView mDropDown;

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_GETTSLPROPERTYTIMELINEDATA:
                    // 处理获取属性时间线数据处理
                    ETSL.propertyTimelineListEntry propertyTimelineList = CloudDataParser.processPropertyTimelineData((String)msg.obj);
                    if(propertyTimelineList != null && propertyTimelineList.items != null && propertyTimelineList.items.size() > 0) {
                        List<ETSL.messageRecordEntry> messageRecordEntries = mTSLHelper.processPropertyMessageRecord(mProductKey, propertyTimelineList.items);
                        if(messageRecordEntries != null && messageRecordEntries.size() > 0) {
                            mAPTMessageRecord.addData(messageRecordEntries);
                        }
                        mMinTimeStamp = propertyTimelineList.minTimeStamp;
                    } else {
                        Toast.makeText(MessageRecordActivity.this, getString(R.string.messagerecord_loadend), Toast.LENGTH_LONG).show();
                    }
                    break;
                case Constant.MSG_CALLBACK_GETTSLEVENTTIMELINEDATA:
                    // 处理获取事件时间线数据处理
                    ETSL.eventTimelineListEntry eventTimelineList = CloudDataParser.processEventTimelineData((String)msg.obj);
                    if(eventTimelineList != null && eventTimelineList.items != null && eventTimelineList.items.size() > 0) {
                        List<ETSL.messageRecordEntry> messageRecordEntries = mTSLHelper.processEventMessageRecord(mProductKey, eventTimelineList.items);
                        if(messageRecordEntries != null && messageRecordEntries.size() > 0) {
                            mAPTMessageRecord.addData(messageRecordEntries);
                        }
                        mMinTimeStamp = eventTimelineList.minTimeStamp;
                    } else {
                        Toast.makeText(MessageRecordActivity.this, getString(R.string.messagerecord_loadend), Toast.LENGTH_LONG).show();
                    }
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
        // 消息记录列表滚动事件处理
        this.mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(mContentId != null && mContentId.length() > 0) {
                    int lastVisibleIndex = mListView.getLastVisiblePosition();
                    if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastVisibleIndex == mTotalItemCount - 1 && mMinTimeStamp > 0) {
                        // 加载下一页数据
                        if(mContentType == Constant.CONTENTTYPE_PROPERTY) {
                            mTSLHelper.getPropertyTimelineData(mIODId, mContentId, 0, mMinTimeStamp - 1, mPageSize, "desc",
                                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            mTSLHelper.getEventTimelineData(mIODId, mContentId, mContentType, 0, mMinTimeStamp - 1, mPageSize, "desc",
                                    mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mContentId != null && mContentId.length() > 0) {
                    mTotalItemCount = totalItemCount;
                }
            }
        });

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
        this.mAPTMessageRecord.clearData();
        if(this.mContentType == Constant.CONTENTTYPE_PROPERTY) {
            this.mTSLHelper.getPropertyTimelineData(this.mIODId, this.mContentId, 0, Utility.getCurrentTimeStamp(), this.mPageSize, "desc",
                    mCommitFailureHandler, mResponseErrorHandler, this.mAPIDataHandler);
        } else {
            this.mTSLHelper.getEventTimelineData(this.mIODId, this.mContentId, this.mContentType,0, Utility.getCurrentTimeStamp(), this.mPageSize, "desc",
                    mCommitFailureHandler, mResponseErrorHandler, this.mAPIDataHandler);
        }
    }
}