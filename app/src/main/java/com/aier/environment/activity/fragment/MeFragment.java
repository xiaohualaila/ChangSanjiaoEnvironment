package com.aier.environment.activity.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aier.environment.R;
import com.aier.environment.activity.LoginActivity;
import com.aier.environment.controller.MeController;
import com.aier.environment.utils.SharePreferenceManager;
import com.aier.environment.utils.ToastUtil;
import com.aier.environment.view.MeView;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;


/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class MeFragment extends BaseFragment {
    private View mRootView;
    public MeView mMeView;
    private MeController mMeController;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        mRootView = layoutInflater.inflate(R.layout.fragment_me,
                (ViewGroup) getActivity().findViewById(R.id.main_view), false);
        mMeView = (MeView) mRootView.findViewById(R.id.me_view);
        mMeView.initModule(mDensity, mWidth);
        mMeController = new MeController(this, mWidth);
        mMeView.setListener(mMeController);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        UserInfo myInfo = JMessageClient.getMyInfo();
        myInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if (i == 0) {
                    mMeView.showPhoto(bitmap);
                    mMeController.setBitmap(bitmap);
                }else {
                    mMeView.showPhoto(null);
                    mMeController.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rc_default_portrait));
                }
            }
        });
        mMeView.showNickName(myInfo);
        super.onResume();
    }

    public void cancelNotification() {
        NotificationManager manager = (NotificationManager) this.getActivity().getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    //退出登录
    public void Logout() {
        final Intent intent = new Intent();
        UserInfo info = JMessageClient.getMyInfo();
        if (null != info) {
            SharePreferenceManager.setCachedUsername(info.getUserName());
            if (info.getAvatarFile() != null) {
                SharePreferenceManager.setCachedAvatarPath(info.getAvatarFile().getAbsolutePath());
            }
            JMessageClient.logout();
            intent.setClass(mContext, LoginActivity.class);
            startActivity(intent);
        } else {
            ToastUtil.shortToast(mContext, "退出失败");
        }
    }

}
