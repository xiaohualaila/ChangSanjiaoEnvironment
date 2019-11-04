package com.aier.environment.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.aier.environment.R;
import com.aier.environment.controller.MeController;



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
