package com.lqr.wechat.ui.presenter;

import com.ipeercloud.com.controler.GsSocketManager;
import com.lqr.wechat.R;
import com.lqr.wechat.app.AppConst;
import com.lqr.wechat.app.MyApp;
import com.lqr.wechat.db.DBManager;
import com.lqr.wechat.manager.BroadcastManager;
import com.lqr.wechat.model.cache.UserCache;
import com.lqr.wechat.ui.base.BaseActivity;
import com.lqr.wechat.ui.base.BasePresenter;
import com.lqr.wechat.ui.view.IMainAtView;
import com.lqr.wechat.util.LogUtils;
import com.lqr.wechat.util.UIUtils;

import io.rong.imlib.RongIMClient;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainAtPresenter extends BasePresenter<IMainAtView> {

    public MainAtPresenter(BaseActivity context) {
        super(context);
        connect(UserCache.getToken());
        //同步所有用户信息
        DBManager.getInstance().getAllUserInfo();
        initConnect("http://sz.goonas.com",8190,"13662664466","1");


    }


    public void initConnect(String serverIp, int port, String userName, String pwd){

        Observable.just("")
                .observeOn(Schedulers.io())
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String aVoid) {

                        return GsSocketManager.getInstance().gsGproxyInit(serverIp,port,userName,pwd);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s->{if(s){LogUtils.d("-----连接成功--->");}else{LogUtils.d("-----连接失败--->");}}
                        ,e-> LogUtils.sf(e.getLocalizedMessage()));



    }



    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {

        if (UIUtils.getContext().getApplicationInfo().packageName.equals(MyApp.getCurProcessName(UIUtils.getContext()))) {

            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIMClient.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                    LogUtils.e("--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtils.e("--onSuccess---" + userid);
                    BroadcastManager.getInstance(mContext).sendBroadcast(AppConst.UPDATE_CONVERSATIONS);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    LogUtils.e("--onError" + errorCode);
                    UIUtils.showToast(UIUtils.getString(R.string.disconnect_server));
                }
            });
        }
    }
}
