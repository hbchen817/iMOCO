package com.rexense.imoco.view;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.sdk.android.openaccount.widget.ProgressDialog;
import com.aliyun.iot.ilop.page.scan.ScanActivity;
import com.rexense.imoco.R;
import com.rexense.imoco.utility.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author imjackzhao@gmail.com
 * @date 2018/5/15
 */
public class IndexActivity extends AppCompatActivity {

    @BindView(R.id.fl_main_container)
    FrameLayout mFlMainContainer;

    @BindView(R.id.rb_tab_one)
    RadioButton mRbTabOne;

    @BindView(R.id.rg_tab_container)
    RadioGroup mRgTabContainer;

    /**
     * 当前显示的碎片
     */
    private Fragment mCurrentFragment;

    FragmentManager mFragmentManager;

    private IndexFragment1 indexFragment1;
    private IndexFragment2 indexFragment2;
    private IndexFragment3 indexFragment3;

    private String[] tagArr = {"IndexFragment1","IndexFragment2","IndexFragment3"};
    /**
     * 第一次按返回键的时间, 默认为0
     */
    private long mFirstPressTime = 0;

    /**
     * 存放需要请求的运行时权限的集合
     */
    private List<String> mPermissionList;


    public static IndexActivity mainActivity;

    public static void start(Context context) {
        ToastUtils.showToastCentrally(context,context.getString(R.string.login_success));
        Intent starter = new Intent(context, IndexActivity.class);
        context.startActivity(starter);
    }

    /**
     * RadioGroup的RadioButton选中监听器
     */
    private RadioGroup.OnCheckedChangeListener mRbOnCheckedChangeListener
            = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_tab_one:
                    TAG = 0;
                    // 切换至碎片一
                    switchFragment(indexFragment1);
                    break;
                case R.id.rb_tab_two:
                    TAG = 1;
                    // 切换至碎片二
                    switchFragment(indexFragment2);
                    break;
                case R.id.rb_tab_three:
                    TAG = 2;
                    // 切换至碎片三
                    switchFragment(indexFragment3);
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // 设置布局文件
        setContentView(R.layout.activity_index);
        // 绑定ButterKnife
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState!=null){
            indexFragment1 = (IndexFragment1) mFragmentManager.findFragmentByTag("IndexFragment1");
            indexFragment2 = (IndexFragment2) mFragmentManager.findFragmentByTag("IndexFragment2");
            indexFragment3 = (IndexFragment3) mFragmentManager.findFragmentByTag("IndexFragment3");

            if (indexFragment1==null){
                indexFragment1 = new IndexFragment1();
            }else {
                mFragmentManager.beginTransaction().hide(indexFragment1);
            }
            if (indexFragment2==null){
                indexFragment2 = new IndexFragment2();
            }else {
                mFragmentManager.beginTransaction().hide(indexFragment2);
            }
            if (indexFragment3==null){
                indexFragment3 = new IndexFragment3();
            }else {
                mFragmentManager.beginTransaction().hide(indexFragment3);
            }
            Fragment[] fragmentsArr = {indexFragment1,indexFragment2,indexFragment3};

            initListener();
            int tag = savedInstanceState.getInt("TAG");
            mCurrentFragment = fragmentsArr[tag];
        }else {
            init();
        }
    }

    private void init() {
        mainActivity = this;
        // 没有需要处理的运行时权限, 继续执行
        initView();
        initListener();
    }

    private void initView() {
        // 实例化几个碎片
        indexFragment1 = new IndexFragment1();
        indexFragment2 = new IndexFragment2();
        indexFragment3 = new IndexFragment3();

        // 默认显示OneFragment
        mFragmentManager.beginTransaction().add(R.id.fl_main_container, indexFragment1, indexFragment1.TAG).commit();

        // 将mOneFragment赋值给当前显示的碎片
        mCurrentFragment = indexFragment1;
    }

    private void initListener() {
        // 给RadioGroup设置RadioButton选中监听器
        mRgTabContainer.setOnCheckedChangeListener(mRbOnCheckedChangeListener);
    }

    /**
     * 点击RadioButton时切换其对应的Fragment
     */
    private void switchFragment(Fragment fragment) {
        // 如果当前显示的Fragment不是切换的Fragment
        if (mCurrentFragment != fragment) {
            // 判断切换的Fragment是否已经添加过
            if (fragment.isAdded()) {
                // 如果已经添加过, 就把当前的Fragment隐藏, 把切换的Fragment显示出来
                mFragmentManager.beginTransaction().hide(mCurrentFragment).show(fragment).commit();
            } else {
                // 如果没有添加过, 就把当前的Fragment隐藏, 把切换的Fragment添加上
                mFragmentManager.beginTransaction().hide(mCurrentFragment).add(R.id.fl_main_container, fragment,tagArr[TAG]).commit();
            }

            // 把切换后的Fragment赋值给mCurrentFragment
            mCurrentFragment = fragment;
        }
    }
    private int TAG;
    /*
     * 保存TAB选中状态
     * */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //如果用以下这种做法则不保存状态，再次进来的话会显示默认tab
        //总是执行这句代码来调用父类去保存视图层的状态
        //保存tab选中的状态;
        super.onSaveInstanceState(outState);
        outState.putInt("TAG",TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 连按两次返回键退出程序
     */
    @Override
    public void onBackPressed() {
//        Intent intent= new Intent(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
        // 第二次按返回键的时间, 为当前系统时间
        long secondPressTime = System.currentTimeMillis();

        // 设置一个为时2秒的间隔时间
        long intervalTime = 2000;

        if (secondPressTime - mFirstPressTime <= intervalTime) {
            // 如果两次点按返回键的间隔时间小于2秒, 直接退出程序
            finish();
            System.exit(0);
        } else {
            // 如果两次点按返回键的间隔时间不小于2秒, 弹吐司提示用户
            ToastUtils.showToastCentrally(this, getString(R.string.press_again_to_exit));

            // 将第一次点按返回键的时间置为系统的当前时间
            mFirstPressTime = System.currentTimeMillis();
        }
    }

    public void back(View view){
        finish();
    }

    /**
     * 防止快速点击
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private long lastClickTime= System.currentTimeMillis();
    private  boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 400) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

}
