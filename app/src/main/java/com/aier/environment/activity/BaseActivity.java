package com.aier.environment.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.aier.environment.R;
import com.aier.environment.utils.FileHelper;
import com.aier.environment.utils.SharePreferenceManager;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class BaseActivity extends Activity {
    protected int mAvatarSize;
    protected int mWidth;
    protected int mHeight;
    protected float mDensity;
    protected int mDensityDpi;
    protected float mRatio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //注册sdk的event用于接收各种event事件
        JMessageClient.registerEventReceiver(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mRatio = Math.min((float) mWidth / 720, (float) mHeight / 1280);
        mAvatarSize = (int) (50 * mDensity);

    }

    public void onEventMainThread(LoginStateChangeEvent event) {
        final LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        if (myInfo != null) {
            String path;
            File avatar = myInfo.getAvatarFile();
            if (avatar != null && avatar.exists()) {
                path = avatar.getAbsolutePath();
            } else {
                path = FileHelper.getUserAvatarPath(myInfo.getUserName());
            }
            SharePreferenceManager.setCachedUsername(myInfo.getUserName());
            SharePreferenceManager.setCachedAvatarPath(path);
            JMessageClient.logout();
        }
        switch (reason) {
            case user_logout:
//                View.OnClickListener listener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        switch (v.getId()) {
//                            case R.id.jmui_cancel_btn:
//                                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                                break;
//                            case R.id.jmui_commit_btn:
//                                JMessageClient.login(SharePreferenceManager.getCachedUsername(), SharePreferenceManager.getCachedPsw(), new BasicCallback() {
//                                    @Override
//                                    public void gotResult(int responseCode, String responseMessage) {
//                                        if (responseCode == 0) {
//                                            Intent intent = new Intent(BaseActivity.this, MainActivity.class);
//                                            startActivity(intent);
//                                        }
//                                    }
//                                });
//                                break;
//                        }
//                    }
//                };

                Log.i("sss","您的账号在其他设备上登陆");
                break;
            case user_password_change:
                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void goToActivity(Context context, Class toActivity) {
        Intent intent = new Intent(context, toActivity);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        //注销消息接收
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }
}
