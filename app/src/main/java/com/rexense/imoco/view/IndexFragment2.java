package com.rexense.imoco.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rexense.imoco.R;
import com.rexense.imoco.contract.CScene;
import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EScene;
import com.rexense.imoco.presenter.AptSceneModel;
import com.rexense.imoco.presenter.CloudDataParser;
import com.rexense.imoco.presenter.PluginHelper;
import com.rexense.imoco.presenter.SceneManager;
import com.rexense.imoco.presenter.SystemParameter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author fyy
 * @date 2018/7/17
 */
public class IndexFragment2 extends BaseFragment {
    private ImageView mImgAdd;
    private TextView mLblScene, mLblMy;
    private SceneManager mSceneManager = null;
    private List<EScene.sceneModelEntry> mModelList = null;
    private List<EScene.sceneListItemEntry> mSceneList = null;
    private ListView mListSceneModel, mListMy;
    private final int mScenePageSize = 50;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_index2;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 获取碎片所依附的活动的上下文环境
        mActivity = getActivity();
        View view = inflater.inflate(setLayout(), container, false);
        this.mImgAdd = (ImageView)view.findViewById(R.id.sceneImgAdd);
        this.mLblScene = (TextView)view.findViewById(R.id.sceneLblScene);
        this.mLblMy = (TextView)view.findViewById(R.id.sceneLblMy);
        this.mListSceneModel = (ListView)view.findViewById(R.id.sceneLstSceneModel);
        this.mListMy = (ListView)view.findViewById(R.id.sceneLstMy);
        initView();
        return view;
    }

    @Override
    protected void init() {

    }
    private void initView() {
        this.mSceneManager = new SceneManager(getActivity());
        this.mModelList = this.mSceneManager.genSceneModelList();
        AptSceneModel aptSceneModel = new AptSceneModel(getActivity());
        aptSceneModel.setData(this.mModelList);
        this.mListSceneModel.setAdapter(aptSceneModel);


        // 添加点击处理
        this.mImgAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                PluginHelper.createScene(getActivity(), "CA");
            }
        });

        // 场景点击处理
        this.mLblScene.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mLblScene.setBackgroundResource(R.drawable.shape_frame_txt);
                mLblScene.setTextColor(Color.parseColor("#FFFFFF"));
                mLblMy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mLblMy.setTextColor(Color.parseColor("#464645"));
                mListSceneModel.setVisibility(View.VISIBLE);
                mListMy.setVisibility(View.GONE);
            }
        });

        // 我的点击处理
        this.mLblMy.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mLblScene.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mLblScene.setTextColor(Color.parseColor("#464645"));
                mLblMy.setBackgroundResource(R.drawable.shape_frame_txt);
                mLblMy.setTextColor(Color.parseColor("#FFFFFF"));
                mListSceneModel.setVisibility(View.GONE);
                mListMy.setVisibility(View.VISIBLE);
            }
        });

        // 场景模板点击处理
        this.mListSceneModel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mModelList.get(position).code == CScene.SMC_NONE){
                    return;
                }

                Intent intent = new Intent(getActivity(), SceneMaintainActivity.class);
                intent.putExtra("operateType", 1);
                intent.putExtra("sceneModelCode", mModelList.get(position).code);
                intent.putExtra("sceneModelName", getString(mModelList.get(position).name));
                intent.putExtra("sceneModelIcon", mModelList.get(position).icon);
                startActivity(intent);
            }
        });
    }

    // 设备列表点击监听器
    private AdapterView.OnItemClickListener deviceListOnItemClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    };

    // 开始获取场景列表
    private void startGetSceneList() {
        if(this.mSceneList == null) {
            this.mSceneList = new ArrayList<EScene.sceneListItemEntry>();
        } else {
            this.mSceneList.clear();
        }
        this.mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, 1, this.mScenePageSize, this.mCommitFailureHandler, this.mResponseErrorHandler, this.mAPIDataHandler);
    }

    // API数据处理器
    private Handler mAPIDataHandler = new Handler(new Handler.Callback(){
        @Override
        public boolean handleMessage(Message msg){
            switch (msg.what) {
                case Constant.MSG_CALLBACK_QUERYSCENELIST:
                    // 处理获取场景列表数据
                    EScene.sceneListEntry sceneList = CloudDataParser.processSceneList((String)msg.obj);
                    if(sceneList != null && sceneList.scenes != null) {
                        for(EScene.sceneListItemEntry item : sceneList.scenes) {
                            mSceneList.add(item);
                        }
                        if(sceneList.scenes.size() >= sceneList.pageSize) {
                            // 数据没有获取完则获取下一页数据
                            mSceneManager.querySceneList(SystemParameter.getInstance().getHomeId(), CScene.TYPE_MANUAL, sceneList.pageNo + 1, mScenePageSize, mCommitFailureHandler, mResponseErrorHandler, mAPIDataHandler);
                        } else {
                            // 数据获取完则设置场景列表数据
                            // ToDo
                        }
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });


}
