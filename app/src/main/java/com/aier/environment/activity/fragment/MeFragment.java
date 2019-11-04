package com.aier.environment.activity.fragment;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aier.environment.R;
import com.aier.environment.activity.LoginActivity;
import com.aier.environment.activity.ResetPasswordActivity;
import com.aier.environment.controller.MeController;
import com.aier.environment.utils.DialogCreator;
import com.aier.environment.utils.SharePreferenceManager;
import com.aier.environment.utils.ToastUtil;
import com.aier.environment.utils.photochoose.SelectableRoundedImageView;
import com.aier.environment.view.MeView;
import com.aier.environment.view.SlipButton;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.IntegerCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;


/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class MeFragment extends BaseFragment implements View.OnClickListener, SlipButton.OnChangedListener {
    private View mRootView;
    //public MeView mMeView;
    private MeController mMeController;
    private Context mContext;
    private Dialog mDialog;
    private Bitmap mBitmap;

    private TextView mSignatureTv;
    private TextView mNickNameTv;
    private SelectableRoundedImageView mTakePhotoBtn;
    private RelativeLayout mSet_pwd;
    public SlipButton mSet_noDisturb;
    //    private RelativeLayout mOpinion;
//    private RelativeLayout mAbout;
    private RelativeLayout mExit;
    private int mWidth;
    private int mHeight;
   // private RelativeLayout mRl_personal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_me, container, false);
        mTakePhotoBtn = v.findViewById(R.id.take_photo_iv);
        mNickNameTv = v.findViewById(R.id.nickName);
        mSignatureTv = v.findViewById(R.id.signature);
        mSet_pwd = v.findViewById(R.id.setPassword);
        mSet_noDisturb = v.findViewById(R.id.btn_noDisturb);
//        mOpinion = (RelativeLayout) v.findViewById(R.id.opinion);
//        mAbout = (RelativeLayout) v.findViewById(R.id.about);
        mExit = v.findViewById(R.id.exit);
        //     mRl_personal = (RelativeLayout) v.findViewById(R.id.rl_personal);
        mSet_noDisturb.setOnChangedListener(R.id.btn_noDisturb, this);

        mSet_pwd.setOnClickListener(this);
//        mOpinion.setOnClickListener(onClickListener);
//        mAbout.setOnClickListener(onClickListener);
        mExit.setOnClickListener(this);
       // mRl_personal.setOnClickListener(this);

        final Dialog dialog = DialogCreator.createLoadingDialog(mContext, mContext.getString(R.string.jmui_loading));
        dialog.show();
        //初始化是否全局免打扰
        JMessageClient.getNoDisturbGlobal(new IntegerCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, Integer value) {
                dialog.dismiss();
                if (responseCode == 0) {
                    mSet_noDisturb.setChecked(value == 1);
                } else {
                    ToastUtil.shortToast(mContext, responseMessage);
                }
            }
        });

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UserInfo myInfo = JMessageClient.getMyInfo();
        myInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if (i == 0) {
                    showPhoto(bitmap);
                    mMeController.setBitmap(bitmap);
                } else {
                    showPhoto(null);
                    mMeController.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rc_default_portrait));
                }
            }
        });
        showNickName(myInfo);

    }

    private void showPhoto(Bitmap avatarBitmap) {
        if (avatarBitmap != null) {
            mTakePhotoBtn.setImageBitmap(avatarBitmap);
        } else {
            mTakePhotoBtn.setImageResource(R.drawable.rc_default_portrait);
        }
    }

    public void showNickName(UserInfo myInfo) {
        if (!TextUtils.isEmpty(myInfo.getNickname().trim())) {
            mNickNameTv.setText(myInfo.getNickname());
        } else {
            mNickNameTv.setText(myInfo.getUserName());
        }
        mSignatureTv.setText(myInfo.getSignature());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setPassword:
                mContext.startActivity(new Intent(mContext, ResetPasswordActivity.class));
                break;
            //  case R.id.opinion://意见反馈
            //mContext.startActivity(new Intent(mContext.getContext(), FeedbackActivity.class));
            //    break;
            //   case R.id.about:
            //  mContext.startActivity(new Intent(mContext.getContext(), AboutJChatActivity.class));
            //      break;
            case R.id.exit:
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.jmui_cancel_btn:
                                mDialog.cancel();
                                break;
                            case R.id.jmui_commit_btn:
                                Logout();
                                cancelNotification();
                                getActivity().finish();
                                mDialog.cancel();
                                break;
                        }
                    }
                };
                mDialog = DialogCreator.createLogoutDialog(getActivity(), listener);
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                mDialog.show();
                break;
//            case R.id.rl_personal:
//                Intent intent = new Intent(mContext.getContext(), PersonalActivity.class);
//                intent.putExtra(PERSONAL_PHOTO, mBitmap);
//                mContext.startActivity(intent);
//                break;
        }
    }

    @Override
    public void onChanged(int id, boolean checkState) {
        switch (id) {
            case R.id.btn_noDisturb:
                final Dialog loadingDialog = DialogCreator.createLoadingDialog(mContext,
                        mContext.getString(R.string.jmui_loading));
                loadingDialog.show();
                JMessageClient.setNoDisturbGlobal(checkState ? 1 : 0, new BasicCallback() {
                    @Override
                    public void gotResult(int status, String desc) {
                        loadingDialog.dismiss();
                        if (status == 0) {
                        } else {
                            mSet_noDisturb.setChecked(!checkState);
                            ToastUtil.shortToast(mContext, "设置失败");
                        }
                    }
                });
                break;
        }
    }
}
