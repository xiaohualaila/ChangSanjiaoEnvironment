package com.aier.environment.controller;


import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.aier.environment.R;
import com.aier.environment.activity.MainActivity;
import com.aier.environment.adapter.ViewPagerAdapter;
import com.aier.environment.activity.fragment.ChatRoomFragment;
import com.aier.environment.activity.fragment.ContactsFragment;
import com.aier.environment.view.MainView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class MainController implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private MainView mMainView;
    private MainActivity mContext;
//    private ConversationListFragment mConvListFragment;
  //  private MeFragment mMeFragment;
    private ContactsFragment mContactsFragment;
    private ChatRoomFragment mChatRoomFragment;


    public MainController(MainView mMainView, MainActivity context) {
        this.mMainView = mMainView;
        this.mContext = context;
        setViewPager();
    }

    private void setViewPager() {
        final List<Fragment> fragments = new ArrayList<>();
        // init Fragment
//        mConvListFragment = new ConversationListFragment();
        mChatRoomFragment = new ChatRoomFragment();
        mContactsFragment = new ContactsFragment();
//        mMeFragment = new MeFragment();

      //  fragments.add(mConvListFragment);
        fragments.add(mChatRoomFragment);
        fragments.add(mContactsFragment);
     //  fragments.add(mMeFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mContext.getSupportFragmentManger(),
                fragments);
        mMainView.setViewPagerAdapter(viewPagerAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_msg_btn:
                mMainView.setCurrentItem(0, false);
                break;
            case R.id.actionbar_chatroom_btn:
                mMainView.setCurrentItem(1, false);
                break;
            case R.id.actionbar_contact_btn:
                mMainView.setCurrentItem(2, false);
                break;
            case R.id.actionbar_me_btn:
                mMainView.setCurrentItem(3, false);
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

    public void sortConvList() {
       // mChatRoomFragment.sortConvList();
    }


}