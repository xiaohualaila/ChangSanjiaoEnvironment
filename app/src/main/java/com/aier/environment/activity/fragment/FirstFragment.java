package com.aier.environment.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        getWeatherData();
    }

    private void getWeatherData() {

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();

        try {
            object.put("method", "ENVIRONMENTAPI_GETCITYWEARTHER");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.i("sss",object.toString());

        RequestBody body = RequestBody.create(json, object.toString());
        Request request = new Request.Builder()//创建Request 对象。
                .url("http://121.41.52.56:3001/environmentalapi")
                .post(body)
                .build();
        Call call = client.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_first,container,false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }





}
