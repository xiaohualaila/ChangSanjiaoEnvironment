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
import com.aier.environment.JGApplication;
import com.aier.environment.R;
import com.aier.environment.location.service.LocationService;
import com.aier.environment.model.WeatherBean;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private Context mContext;
    public TextView tv_city,tv_date,tv_wendu,tv_feng,tv_weather,
            tv_date_1,tv_today_1,tv_date_2,tv_today_2,tv_date_3,tv_today_3,tv_date_4,tv_today_4,tv_date_5,tv_today_5;

    private LocationService locationService;
    private BDLocation mLocation;
    public String city;
    private boolean isFirst = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        locationService = JGApplication.locationService;
        locationService.registerListener(mListener);//是否应该在onStart中注册
        locationService.start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                mLocation= location;
                city = location.getCity();
                if(isFirst==true&&!TextUtils.isEmpty(city)){
                    isFirst = false;
                    getWeatherData();
                }
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }

    };


    private void getWeatherData() {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();

        try {
            object.put("method", "ENVIRONMENTAPI_GETCITYWEARTHER");
            city = city.substring(0,city.length()-1);
            Log.i("sss",city);
            object.put("city",city );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("sss",object.toString());

        RequestBody body = RequestBody.create(json, object.toString());
        Request request = new Request.Builder()//创建Request 对象。
                .url("http://192.168.0.68:3001/environmentalapi")
                .post(body)
                .build();
        Call call = client.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                Log.i("sss",e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String string = null;
                        try {
                            string = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.i("sss",string);
                        Gson gson = new Gson();
                        WeatherBean weatherBean =  gson.fromJson(string, WeatherBean.class);
                        WeatherBean.ResultBean resultBean =  weatherBean.getResult();
                        if(weatherBean.isSuccess()){
                            tv_city.setText(resultBean.getCity()+"市");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
                            Date date = new Date(System.currentTimeMillis());
                            tv_date.setText(simpleDateFormat.format(date));
                            tv_wendu.setText(resultBean.getWendu()+"℃");
                            List<WeatherBean.ResultBean.ForecastBean> forecastBeans = resultBean.getForecast();
                            WeatherBean.ResultBean.ForecastBean forecastBean;
                            SimpleDateFormat format1 = new SimpleDateFormat("MM/dd");
                            String data;
                            for(int i=0;i<forecastBeans.size();i++){
                                forecastBean = forecastBeans.get(i);
                                data = forecastBean.getDate();
                                if(i==0){
                                    tv_feng.setText(forecastBean.getFengxiang()+forecastBean.getFengli());
                                    tv_weather.setText(forecastBean.getType());
                                    tv_date_1.setText(format1.format(new Date(System.currentTimeMillis())));
                                    tv_today_1.setText("今天");
                                }else if(i==1){
                                    tv_date_2.setText(data.substring(0,2));
                                    tv_today_2.setText(data.substring(2,data.length()));
                                }else if(i==2){
                                    tv_date_3.setText(data.substring(0,2));
                                    tv_today_3.setText(data.substring(2,data.length()));
                                }else if(i==3){
                                    tv_date_4.setText(data.substring(0,2));
                                    tv_today_4.setText(data.substring(2,data.length()));
                                }else if(i==4){
                                    tv_date_5.setText(data.substring(0,2));
                                    data = forecastBean.getDate();
                                    tv_today_5.setText(data.substring(2,data.length()));
                                }
                            }
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        isFirst = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(mListener); // 注销掉监听
        locationService.stop();
    }
}
