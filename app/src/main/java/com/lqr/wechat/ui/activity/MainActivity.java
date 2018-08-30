package com.lqr.wechat.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ipeercloud.com.controler.GsSocketManager;
import com.lqr.wechat.R;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.ui.adapter.CommonFragmentPagerAdapter;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BaseFragment;
import com.lqr.wechat.ui.fragment.ContactsFragment;
import com.lqr.wechat.ui.fragment.DiscoveryFragment;
import com.lqr.wechat.ui.fragment.FragmentFactory;
import com.lqr.wechat.ui.fragment.MeFragment;
import com.lqr.wechat.ui.fragment.RecentMessageFragment;
import com.lqr.wechat.ui.presenter.MainAtPresenter;
import com.lqr.wechat.ui.view.IMainAtView;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.PopupWindowUtils;
import com.lqr.wechat.util.UIUtils;
import com.lqr.wechat.widget.NToast;
import com.lqr.wechat.widget.NavigateTabBar;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import butterknife.Bind;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.lqr.wechat.app.base.BaseApp.exit;

public class MainActivity extends BaseActivity<IMainAtView, MainAtPresenter> implements  IMainAtView {

    private NavigateTabBar mNavigateTabBar;
    private String mLanguagePosition;
    private Resources mResources;
    private Configuration mConfig;
    private DisplayMetrics mDm;
    private static String mLanguageCode;
    private static String mServerCode;
    public static boolean isOpen = false;
    private String languageTag = "";

    @Bind(R.id.ibAddMenu)
    ImageButton mIbAddMenu;




    @Override
    public void init() {
        registerBR();
    }

    @Override
    public void initView() {
        isOpen = true;
        initNavigeteTabBar();

        Intent intent = getIntent();
        Bundle bundleString = intent.getBundleExtra("Language");
        if (bundleString != null) {
            languageTag = bundleString.getString("language");
        }
        setToolbarTitle(UIUtils.getString(R.string.app_name));
        mIbAddMenu.setVisibility(View.VISIBLE);
        //等待全局数据获取完毕
        showWaitingDialog(UIUtils.getString(R.string.please_wait));
    }

    @Override
    public void initListener() {
        mIbAddMenu.setOnClickListener(v -> {
            //显示或隐藏popupwindow
            View menuView = View.inflate(MainActivity.this, R.layout.menu_main, null);
            PopupWindow popupWindow = PopupWindowUtils.getPopupWindowAtLocation(menuView, getWindow().getDecorView(), Gravity.TOP | Gravity.RIGHT, UIUtils.dip2Px(5), mAppBar.getHeight() + 30);
            menuView.findViewById(R.id.tvCreateGroup).setOnClickListener(v1 -> {
                jumpToActivity(CreateGroupActivity.class);
                popupWindow.dismiss();
            });
            menuView.findViewById(R.id.tvHelpFeedback).setOnClickListener(v1 -> {
                jumpToWebViewActivity(AppConst.WeChatUrl.HELP_FEED_BACK);
                popupWindow.dismiss();
            });
            menuView.findViewById(R.id.tvAddFriend).setOnClickListener(v1 -> {
                jumpToActivity(AddFriendActivity.class);
                popupWindow.dismiss();
            });
            menuView.findViewById(R.id.tvScan).setOnClickListener(v1 -> {
                jumpToActivity(ScanActivity.class);
                popupWindow.dismiss();
            });
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBR();
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    @Override
    protected MainAtPresenter createPresenter() {
        return new MainAtPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isToolbarCanBack() {
        return false;
    }

    private void registerBR() {
        BroadcastManager.getInstance(this).register(AppConst.FETCH_COMPLETE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                hideWaitingDialog();
            }
        });
    }

    private void unRegisterBR() {
        BroadcastManager.getInstance(this).unregister(AppConst.FETCH_COMPLETE);
    }

    private void initNavigeteTabBar() {
        mNavigateTabBar = (NavigateTabBar) findViewById(R.id.home_navigate);

        String MESSAGE_PAGE = getString(R.string.message);
        mNavigateTabBar.addTab(RecentMessageFragment.class,
                new NavigateTabBar.TabParam(
                        R.mipmap.message_normal,
                        R.mipmap.message_press,
                        MESSAGE_PAGE));
        String CONTACTS_PAGE = getString(R.string.contacts);
        mNavigateTabBar.addTab(ContactsFragment.class,
                new NavigateTabBar.TabParam(
                        R.mipmap.contacts_normal,
                        R.mipmap.contacts_press,
                        CONTACTS_PAGE));
        String DISCOVERY_PAGE = getString(R.string.discovery);
        mNavigateTabBar.addTab(DiscoveryFragment.class,
                new NavigateTabBar.TabParam(
                        R.mipmap.discovery_normal,
                        R.mipmap.discovery_press,
                        DISCOVERY_PAGE));

        String MINE_PAGE = getString(R.string.me);
        mNavigateTabBar.addTab(MeFragment.class,
                new NavigateTabBar.TabParam(
                        R.mipmap.me_normal,
                        R.mipmap.me_press,
                        MINE_PAGE));

        mNavigateTabBar.setTabSelectListener(new NavigateTabBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(NavigateTabBar.ViewHolder holder) {
                if (MESSAGE_PAGE.equals(holder.tag.toString())) {
                    mNavigateTabBar.showFragment(holder);
                } else if (CONTACTS_PAGE.equals(holder.tag.toString())) {
                    mNavigateTabBar.showFragment(holder);
                }
                else if (DISCOVERY_PAGE.equals(holder.tag.toString())) {
                    //EventBus.getDefault().postSticky(new TradeSeletedEvent());
                    mNavigateTabBar.showFragment(holder);

                }
                else if (MINE_PAGE.equals(holder.tag.toString())) {
                    //EventBus.getDefault().postSticky(new TradeSeletedEvent());
                    mNavigateTabBar.showFragment(holder);

                }
            }
        });
    }

    /**
     * 点击手机返回键finish app
     */
    @Override
    public void onBackPressed() {
        exitApp();
    }

    /**
     * 退出应用
     */

    private long exitTime = 0;

    public void exitApp() {
        if (System.currentTimeMillis() - exitTime > 2000) {
          //  ToastUtil.show(getString(R.string.tv_again_click_out));
            NToast.longToast(this,getString(R.string.tv_again_click_out));
            exitTime = System.currentTimeMillis();
        } else {
           // AppManager.getInstance().finishAllActivity();
            exit();
            finish();
            System.exit(0);
        }
    }

    private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : onTouchListeners) {
            listener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }



    public interface MyOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }

}
