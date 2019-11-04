package com.aier.environment.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.activeandroid.ActiveAndroid;
import com.aier.environment.JGApplication;
import com.aier.environment.R;
import com.aier.environment.adapter.StickyListAdapter;
import com.aier.environment.controller.MeController;
import com.aier.environment.database.FriendEntry;
import com.aier.environment.model.FriendBean;
import com.aier.environment.utils.pinyin.HanziToPinyin;
import com.aier.environment.utils.pinyin.PinyinComparator;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class FirstFragment extends BaseFragment {
    private View mRootView;

    private MeController mMeController;
    private Context mContext;
    public TextView tv_city,tv_date,tv_wendu,tv_feng,tv_weather,
            tv_date_1,tv_today_1,tv_date_2,tv_today_2,tv_date_3,tv_today_3,tv_date_4,tv_today_4,tv_date_5,tv_today_5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_first,container,false);
        tv_city = view.findViewById(R.id.tv_city);
        tv_date = view.findViewById(R.id.tv_date);
        tv_wendu = view.findViewById(R.id.tv_wendu);
        tv_feng = view.findViewById(R.id.tv_feng);
        tv_weather= view.findViewById(R.id.tv_weather);
        tv_date_1= view.findViewById(R.id.tv_date_1);
        tv_today_1 = view.findViewById(R.id.tv_today_1);
        tv_date_2= view.findViewById(R.id.tv_date_2);
        tv_today_2 = view.findViewById(R.id.tv_today_2);
        tv_date_3= view.findViewById(R.id.tv_date_3);
        tv_today_3 = view.findViewById(R.id.tv_today_3);
        tv_date_4= view.findViewById(R.id.tv_date_4);
        tv_today_4 = view.findViewById(R.id.tv_today_4);
        tv_date_5= view.findViewById(R.id.tv_date_5);
        tv_today_5 = view.findViewById(R.id.tv_today_5);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }





}
