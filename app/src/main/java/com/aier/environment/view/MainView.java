package com.aier.environment.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.aier.environment.R;
import com.aier.environment.utils.SharePreferenceManager;
import com.baidu.location.BDLocation;


/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class MainView extends RelativeLayout {

    private Button[] mBtnList;
    private int[] mBtnListID;
    private ScrollControlViewPager mViewContainer;
    private TextView mAllContactNumber;
    private LinearLayout ll_bottom_latLng;
    private TextView tv_lat,tv_long;
    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initModule() {
        mBtnListID = new int[] {
                R.id.actionbar_msg_btn, R.id.actionbar_chatroom_btn,
                R.id.actionbar_contact_btn, R.id.actionbar_me_btn};
        mBtnList = new Button[mBtnListID.length];
        for (int i = 0; i < mBtnListID.length; i++) {
            mBtnList[i] =  findViewById(mBtnListID[i]);
        }
        mViewContainer =  findViewById(R.id.viewpager);
        mViewContainer.setOffscreenPageLimit(2);
        mBtnList[0].setTextColor(getResources().getColor(R.color.menu_item_back_pres_color));
        mBtnList[0].setSelected(true);
        mAllContactNumber = findViewById(R.id.all_contact_number);
        if (SharePreferenceManager.getCachedNewFriendNum() > 0) {
            mAllContactNumber.setVisibility(VISIBLE);
            mAllContactNumber.setText(String.valueOf(SharePreferenceManager.getCachedNewFriendNum()));
        } else {
            mAllContactNumber.setVisibility(GONE);
        }
        ll_bottom_latLng = findViewById(R.id.ll_bottom_latLng);
        tv_lat  = findViewById(R.id.tv_lat);
        tv_long  = findViewById(R.id.tv_long);
    }

    public void setOnClickListener(OnClickListener onclickListener) {
        for (int i = 0; i < mBtnListID.length; i++) {
            mBtnList[i].setOnClickListener(onclickListener);
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mViewContainer.addOnPageChangeListener(onPageChangeListener);
    }

    public void setViewPagerAdapter(FragmentPagerAdapter adapter) {
        mViewContainer.setAdapter(adapter);
    }

    public void setCurrentItem(int index, boolean scroll) {
        mViewContainer.setCurrentItem(index, scroll);
    }

    public void setButtonColor(int index) {
        for (int i = 0; i < mBtnListID.length; i++) {
            if (index == i) {
                mBtnList[i].setSelected(true);
                mBtnList[i].setTextColor(getResources().getColor(R.color.menu_item_back_pres_color));
                if(i==0){
                   Drawable drawable = getResources().getDrawable(R.drawable.first);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mBtnList[i].setCompoundDrawables(null,drawable,null,null);
                }else if(i==1){
                    Drawable drawable = getResources().getDrawable(R.drawable.map);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mBtnList[i].setCompoundDrawables(null,drawable,null,null);
                }else if(i==2){
                    Drawable drawable = getResources().getDrawable(R.drawable.contacts);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mBtnList[i].setCompoundDrawables(null,drawable,null,null);
                }else if(i==3){
                    Drawable drawable = getResources().getDrawable(R.drawable.me);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mBtnList[i].setCompoundDrawables(null,drawable,null,null);
                }
            } else {
                mBtnList[i].setSelected(false);
                mBtnList[i].setTextColor(getResources().getColor(R.color.action_bar_txt_color));
                if(i==0){
                    Drawable drawable = getResources().getDrawable(R.drawable.first_);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mBtnList[i].setCompoundDrawables(null,drawable,null,null);
                }else if(i==1){
                    Drawable drawable = getResources().getDrawable(R.drawable.map_);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mBtnList[i].setCompoundDrawables(null,drawable,null,null);
                }else if(i==2){
                    Drawable drawable = getResources().getDrawable(R.drawable.contacts_);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mBtnList[i].setCompoundDrawables(null,drawable,null,null);
                }else if(i==3){
                    Drawable drawable = getResources().getDrawable(R.drawable.me_);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    mBtnList[i].setCompoundDrawables(null,drawable,null,null);

                }
            }
        }
    }


    public void setll_latlngVisibility(boolean flag) {
        if(flag){
            ll_bottom_latLng.setVisibility(VISIBLE);
        }else
            ll_bottom_latLng.setVisibility(GONE);
    }


    public void setLocation(BDLocation mLocation) {
        if(mLocation!=null){
            tv_lat.setText("北纬"+mLocation.getLatitude()+"°");
            tv_long.setText("东经"+mLocation.getLongitude()+"°");
        }

    }
}
