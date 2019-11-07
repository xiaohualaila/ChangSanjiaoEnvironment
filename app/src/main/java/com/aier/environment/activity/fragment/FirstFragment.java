package com.aier.environment.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aier.environment.JGApplication;
import com.aier.environment.R;
import com.aier.environment.location.service.LocationService;
import com.aier.environment.model.WeatherBean;
import com.aier.environment.utils.ToastUtil;
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
    public TextView tv_city, tv_date, tv_wendu, tv_feng, tv_weather,
            tv_date_1, tv_today_1, tv_date_2, tv_today_2, tv_date_3, tv_today_3, tv_date_4, tv_today_4,
            tv_date_5, tv_today_5, tv_wendu_1, tv_wendu_2, tv_wendu_3, tv_wendu_4, tv_wendu_5, tv_weather_1,
            tv_weather_2, tv_weather_3, tv_weather_4, tv_weather_5;

    private ImageView iv_weather_1, iv_weather_2, iv_weather_3, iv_weather_4, iv_weather_5, iv_air;
    private LocationService locationService;
    private BDLocation mLocation;
    public String city;
    private boolean isFirst = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        tv_city = view.findViewById(R.id.tv_city);
        tv_date = view.findViewById(R.id.tv_date);
        tv_wendu = view.findViewById(R.id.tv_wendu);
        tv_feng = view.findViewById(R.id.tv_feng);
        tv_weather = view.findViewById(R.id.tv_weather);
        tv_date_1 = view.findViewById(R.id.tv_date_1);
        tv_today_1 = view.findViewById(R.id.tv_today_1);
        tv_date_2 = view.findViewById(R.id.tv_date_2);
        tv_today_2 = view.findViewById(R.id.tv_today_2);
        tv_date_3 = view.findViewById(R.id.tv_date_3);
        tv_today_3 = view.findViewById(R.id.tv_today_3);
        tv_date_4 = view.findViewById(R.id.tv_date_4);
        tv_today_4 = view.findViewById(R.id.tv_today_4);
        tv_date_5 = view.findViewById(R.id.tv_date_5);
        tv_today_5 = view.findViewById(R.id.tv_today_5);
        tv_wendu_1 = view.findViewById(R.id.tv_wendu_1);
        tv_wendu_2 = view.findViewById(R.id.tv_wendu_2);
        tv_wendu_3 = view.findViewById(R.id.tv_wendu_3);
        tv_wendu_4 = view.findViewById(R.id.tv_wendu_4);
        tv_wendu_5 = view.findViewById(R.id.tv_wendu_5);
        tv_weather_1 = view.findViewById(R.id.tv_weather_1);
        tv_weather_2 = view.findViewById(R.id.tv_weather_2);
        tv_weather_3 = view.findViewById(R.id.tv_weather_3);
        tv_weather_4 = view.findViewById(R.id.tv_weather_4);
        tv_weather_5 = view.findViewById(R.id.tv_weather_5);
        iv_weather_1 = view.findViewById(R.id.iv_weather_1);
        iv_weather_2 = view.findViewById(R.id.iv_weather_2);
        iv_weather_3 = view.findViewById(R.id.iv_weather_3);
        iv_weather_4 = view.findViewById(R.id.iv_weather_4);
        iv_weather_5 = view.findViewById(R.id.iv_weather_5);
        iv_air = view.findViewById(R.id.iv_air);
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
                mLocation = location;
                city = location.getCity();
                if (isFirst == true && !TextUtils.isEmpty(city)) {
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
            city = city.substring(0, city.length() - 1);
            //   Log.i("sss",city);
            object.put("city", city);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Log.i("sss",object.toString());

        RequestBody body = RequestBody.create(json, object.toString());
        Request request = new Request.Builder()//创建Request 对象。
                .url("http://121.41.52.56:3002/environmentalapi")
                .post(body)
                .build();
        Call call = client.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                //  Log.i("sss",e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.shortToast(getActivity(),"网络异常！");
                    }
                });
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
                        // Log.i("sss",string);
                        Gson gson = new Gson();
                        WeatherBean weatherBean = gson.fromJson(string, WeatherBean.class);
                        WeatherBean.ResultBean resultBean = weatherBean.getResult();
                        if (weatherBean.isSuccess()) {
                            tv_city.setText(resultBean.getCity() + "市");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");// HH:mm:ss
                            Date date = new Date(System.currentTimeMillis());
                            tv_date.setText(simpleDateFormat.format(date));
                            tv_wendu.setText(resultBean.getWendu() + "℃");
                            List<WeatherBean.ResultBean.ForecastBean> forecastBeans = resultBean.getForecast();
                            WeatherBean.ResultBean.ForecastBean forecastBean;
                            SimpleDateFormat format1 = new SimpleDateFormat("MM/dd");
                            String data;
                            String type;
                            String wendu;
                            for (int i = 0; i < forecastBeans.size(); i++) {
                                forecastBean = forecastBeans.get(i);
                                data = forecastBean.getDate();
                                type = forecastBean.getType();
                                wendu = forecastBean.getLow().substring(2, forecastBean.getLow().length())
                                        + "~" + forecastBean.getHigh().substring(2, forecastBean.getHigh().length());
                                if (i == 0) {
                                    String fengli = forecastBean.getFengli();
                                    fengli = fengli.substring(9, fengli.length());
                                    int index = fengli.indexOf("级");
                                    fengli = fengli.substring(0, index + 1);
                                    tv_feng.setText(forecastBean.getFengxiang() + fengli);
                                    tv_weather.setText(forecastBean.getType());
                                    tv_date_1.setText(data.substring(0, 2));
                                    tv_today_1.setText("今天");

                                    tv_wendu_1.setText(wendu);
                                    setWeatherPic(type, iv_air);
                                    setWeatherPic(type, iv_weather_1);
                                    tv_weather_1.setText(forecastBean.getType());
                                } else if (i == 1) {
                                    tv_date_2.setText(data.substring(0, 2));
                                    tv_today_2.setText(data.substring(2, data.length()));
                                    tv_wendu_2.setText(wendu);
                                    setWeatherPic(type, iv_weather_2);
                                    tv_weather_2.setText(forecastBean.getType());
                                } else if (i == 2) {
                                    tv_date_3.setText(data.substring(0, 2));
                                    tv_today_3.setText(data.substring(2, data.length()));
                                    tv_wendu_3.setText(wendu);
                                    setWeatherPic(type, iv_weather_3);
                                    tv_weather_3.setText(forecastBean.getType());
                                } else if (i == 3) {
                                    tv_date_4.setText(data.substring(0, 2));
                                    tv_today_4.setText(data.substring(2, data.length()));
                                    tv_wendu_4.setText(wendu);
                                    setWeatherPic(type, iv_weather_4);
                                    tv_weather_4.setText(forecastBean.getType());
                                } else if (i == 4) {
                                    tv_date_5.setText(data.substring(0, 2));
                                    data = forecastBean.getDate();
                                    tv_today_5.setText(data.substring(2, data.length()));
                                    tv_wendu_5.setText(wendu);
                                    setWeatherPic(type, iv_weather_5);
                                    tv_weather_5.setText(forecastBean.getType());
                                }
                            }
                        }
                    }
                });
            }
        });

    }

    private void setWeatherPic(String type, ImageView imageView) {
        // Log.i("sss","type   "+type);
        if (type.equals("晴")) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather_qing));
        } else if (type.equals("阴")) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather_yin));
        } else if (type.equals("小雨")) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather_xiaoyu));
        } else if (type.equals("中雨")) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather_zhongyu));
        } else if (type.equals("大雨")) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather_dayu));
        } else if (type.equals("小雪")) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather_xiaoxue));
        } else if (type.equals("大雪")) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather_daxue));
        } else if (type.equals("雷雨")) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather_leiyu));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.weather_duoyun));//多云
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        locationService = JGApplication.locationService;
        locationService.registerListener(mListener);//是否应该在onStart中注册
        locationService.start();
        isFirst = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        locationService.unregisterListener(mListener); // 注销掉监听
        locationService.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
