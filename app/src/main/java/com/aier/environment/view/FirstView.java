package com.aier.environment.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.aier.environment.R;
import com.aier.environment.utils.DialogCreator;
import com.aier.environment.utils.ToastUtil;
import com.aier.environment.utils.photochoose.SelectableRoundedImageView;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.IntegerCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;


/**
 * Created by ${chenyn} on 2017/2/21.
 */

public class FirstView extends ConstraintLayout {
    private Context mContext;

    private int mWidth;
    private int mHeight;


    public FirstView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

    }


    public void initModule(float density, int width) {


        mWidth = width;
        mHeight = (int) (190 * density);





    }

    public void setListener(OnClickListener onClickListener) {



    }




}
