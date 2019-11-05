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
import com.aier.environment.model.WeatherBean;
import com.aier.environment.view.MainView;

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

    public void setWeatherBean(WeatherBean weatherBean ) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WeatherBean.ResultBean resultBean =  weatherBean.getResult();
                firstFragment.tv_city.setText(resultBean.getCity()+"市");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
                Date date = new Date(System.currentTimeMillis());
                firstFragment.tv_date.setText(simpleDateFormat.format(date));
                firstFragment.tv_wendu.setText(resultBean.getWendu()+"℃");
               List<WeatherBean.ResultBean.ForecastBean>  forecastBeans = resultBean.getForecast();
                WeatherBean.ResultBean.ForecastBean forecastBean;
                SimpleDateFormat format1 = new SimpleDateFormat("MM/dd");
                String data;
                for(int i=0;i<forecastBeans.size();i++){
                    forecastBean = forecastBeans.get(i);
                    data = forecastBean.getDate();
                    if(i==0){
                        firstFragment.tv_feng.setText(forecastBean.getFengxiang()+forecastBean.getFengli());
                        firstFragment.tv_weather.setText(forecastBean.getType());
                        firstFragment.tv_date_1.setText(format1.format(new Date(System.currentTimeMillis())));
                        firstFragment.tv_today_1.setText("今天");
                    }else if(i==1){
                        firstFragment.tv_date_2.setText(data.substring(0,2));
                        firstFragment.tv_today_2.setText(data.substring(2,data.length()));
                    }else if(i==2){
                        firstFragment.tv_date_3.setText(data.substring(0,2));
                        firstFragment.tv_today_3.setText(data.substring(2,data.length()));
                    }else if(i==3){
                        firstFragment.tv_date_4.setText(data.substring(0,2));
                        firstFragment.tv_today_4.setText(data.substring(2,data.length()));
                    }else if(i==4){
                        firstFragment.tv_date_5.setText(data.substring(0,2));
                        data = forecastBean.getDate();
                        firstFragment.tv_today_5.setText(data.substring(2,data.length()));
                    }
                }

            }
        });

    }
}