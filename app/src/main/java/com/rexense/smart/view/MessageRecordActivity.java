package com.rexense.smart.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import com.rexense.smart.R;
import com.rexense.smart.databinding.ActivityMessagerecordBinding;
import com.rexense.smart.presenter.AptMessageRecord;
import com.rexense.smart.presenter.CloudDataParser;
import com.rexense.smart.presenter.TSLHelper;
import com.rexense.smart.contract.Constant;
import com.rexense.smart.model.ETSL;
import com.rexense.smart.utility.SrlUtils;
import com.rexense.smart.utility.ToastUtils;
import com.rexense.smart.utility.Utility;
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
    private final int PAGE_SIZE = 30;
    private AptMessageRecord mAPTMessageRecord;
    private long mMinTimeStamp = 0;

    private ActivityMessagerecordBinding mViewBinding;

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
        public boolean handleMessage(Message msg) {
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
                        ToastUtils.showLongToast(MessageRecordActivity.this, R.string.messagerecord_loadend);
                    }
                    SrlUtils.finishRefresh(mViewBinding.srlFragmentMe, true);
                    SrlUtils.finishLoadMore(mViewBinding.srlFragmentMe, true);
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
                        ToastUtils.showLongToast(MessageRecordActivity.this, R.string.messagerecord_loadend);
                    }
                    SrlUtils.finishRefresh(mViewBinding.srlFragmentMe, true);
                    SrlUtils.finishLoadMore(mViewBinding.srlFragmentMe, true);
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
        mViewBinding = ActivityMessagerecordBinding.inflate(getLayoutInflater());
        setContentView(mViewBinding.getRoot());

        mViewBinding.srlFragmentMe.setOnRefreshListener(onRefreshListener);
        mViewBinding.srlFragmentMe.setOnLoadMoreListener(onLoadMoreListener);

        mIODId = getIntent().getStringExtra("iotId");
        mProductKey = getIntent().getStringExtra("productKey");

        mViewBinding.messageRecordImgDropdown.setVisibility(View.GONE);

        // 回退处理
        ImageView imgBack = (ImageView) findViewById(R.id.messageRecordImgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 消息记录列表处理
        mAPTMessageRecord = new AptMessageRecord(this);
        mViewBinding.messageRecordLstMessageRecord.setAdapter(mAPTMessageRecord);

        mTSLHelper = new TSLHelper(this);
        // 获取设备消息记录内容
        mContents = mTSLHelper.getMessageRecordContent(mProductKey);
        if (mContents != null && mContents.size() > 0) {
            // 查询第一个内容
            mContentId = mContents.get(0).id;
            mContentType = mContents.get(0).type;
            mViewBinding.messageRecordLblTitle.setText(mContents.get(0).name);
            startQuery();
            // 如果有多个内容则显示下拉选择
            if (mContents.size() > 1) {
                mViewBinding.messageRecordImgDropdown.setVisibility(View.VISIBLE);
                mViewBinding.messageRecordImgDropdown.setOnClickListener(new View.OnClickListener() {
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
            mViewBinding.messageRecordLblTitle.setText(data.getStringExtra("name"));
            mContentType = data.getIntExtra("type", Constant.CONTENTTYPE_PROPERTY);
            startQuery();
        }
    }

    // 开始查询
    private void startQuery() {
        if (mContents != null && mContents.size() > 0) {
            mAPTMessageRecord.clearData();
            if (mContentType == Constant.CONTENTTYPE_PROPERTY) {
                mTSLHelper.getPropertyTimelineData(mIODId, mContentId, 0, Utility.getCurrentTimeStamp(), PAGE_SIZE, "desc",
                        mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            } else {
                mTSLHelper.getEventTimelineData(mIODId, mContentId, mContentType, 0, Utility.getCurrentTimeStamp(), PAGE_SIZE, "desc",
                        mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
        } else {
            SrlUtils.finishRefresh(mViewBinding.srlFragmentMe, true);
            SrlUtils.finishLoadMore(mViewBinding.srlFragmentMe, true);
        }
    }

    // 下一页查询
    private void nextQuery() {
        if (mContents != null && mContents.size() > 0) {
            if (mContentType == Constant.CONTENTTYPE_PROPERTY) {
                mTSLHelper.getPropertyTimelineData(mIODId, mContentId, 0, mMinTimeStamp, PAGE_SIZE, "desc",
                        mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            } else {
                this.mTSLHelper.getEventTimelineData(mIODId, mContentId, mContentType, 0, mMinTimeStamp, PAGE_SIZE, "desc",
                        mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
            }
        } else {
            SrlUtils.finishRefresh(mViewBinding.srlFragmentMe, true);
            SrlUtils.finishLoadMore(mViewBinding.srlFragmentMe, true);
        }
    }
}