package com.aier.environment.controller;


import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.aier.environment.R;
import com.aier.environment.activity.MainActivity;
import com.aier.environment.activity.fragment.ConversationListFragment;
import com.aier.environment.activity.fragment.FirstFragment;
import com.aier.environment.activity.fragment.MapFragment;
import com.aier.environment.activity.fragment.MeFragment;
import com.aier.environment.adapter.ViewPagerAdapter;
import com.aier.environment.activity.fragment.ChatRoomFragment;
import com.aier.environment.activity.fragment.ContactsFragment;
import com.aier.environment.model.UserOnlineBean;
import com.aier.environment.model.WeatherBean;
import com.aier.environment.view.MainView;
import com.baidu.location.BDLocation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class MainController implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private MainView mMainView;
    private MainActivity mContext;
    private FirstFragment firstFragment;
    private MapFragment mapFragment;
    private ConversationListFragment mConvListFragment;
    private ChatRoomFragment mChatRoomFragment;//改成地图
    private ContactsFragment mContactsFragment;
    private MeFragment mMeFragment;
    private BDLocation mLocation;


    public MainController(MainView mMainView, MainActivity context) {
        this.mMainView = mMainView;
        this.mContext = context;
        setViewPager();
    }

    private void setViewPager() {
        final List<Fragment> fragments = new ArrayList<>();
        // init Fragment
        firstFragment= new FirstFragment();
        mapFragment = new MapFragment();
        mConvListFragment = new ConversationListFragment();
        mChatRoomFragment = new ChatRoomFragment();
        mContactsFragment = new ContactsFragment();
        mMeFragment = new MeFragment();
        fragments.add(firstFragment);
        fragments.add(mapFragment);
       // fragments.add(mConvListFragment);//会话
       // fragments.add(mChatRoomFragment);//聊天室

        fragments.add(mContactsFragment);//通信录
        fragments.add(mMeFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mContext.getSupportFragmentManger(),
                fragments);
        mMainView.setViewPagerAdapter(viewPagerAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_msg_btn:
                mMainView.setCurrentItem(0, false);
                mMainView.setll_latlngVisibility(false);
                break;
            case R.id.actionbar_chatroom_btn:
                mMainView.setCurrentItem(1, false);
                mMainView.setll_latlngVisibility(true);
                mMainView.setLocation(mLocation);
                break;
            case R.id.actionbar_contact_btn:
                mMainView.setCurrentItem(2, false);
                mMainView.setll_latlngVisibility(false);
                break;
            case R.id.actionbar_me_btn:
                mMainView.setCurrentItem(3, false);
                mMainView.setll_latlngVisibility(false);
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mMainView.setButtonColor(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public void setUserOnline(UserOnlineBean userOnlineBean) {
        mapFragment.setUserOnline2(userOnlineBean);

    }

    public void setLocation(BDLocation location) {
        mLocation = location;
    }
}