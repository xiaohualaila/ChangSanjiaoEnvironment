package com.aier.environment.activity.fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aier.environment.JGApplication;
import com.aier.environment.R;
import com.aier.environment.location.service.LocationService;
import com.aier.environment.model.GetAllPostion;
import com.aier.environment.model.UserLocation;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MapFragment extends Fragment implements BaiduMap.OnMarkerClickListener {
    static MapView mMapView = null;
    // 定位相关
    LocationClient mLocClient;
    private BaiduMap mBaiduMap;
    private LocationService locationService;
    private LatLng latLng;
    private boolean isFirstLoc = true; // 是否首次定位

    private Polyline mPolyline;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //默认显示普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //开启交通图
        //mBaiduMap.setTrafficEnabled(true);
        //开启热力图
        //mBaiduMap.setBaiduHeatMapEnabled(true);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        locationService = JGApplication.locationService;


        mLocClient = new LocationClient(getActivity());
        locationService.registerListener(mListener);//是否应该在onStart中注册
        locationService.start();
        //配置定位SDK参数
        initLocation();
        getAllUserPostion();
        mBaiduMap.setOnMarkerClickListener(this);
    }


    //配置定位SDK参数
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation
        // .getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocClient.setLocOption(option);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = v.findViewById(R.id.map);
        mBaiduMap = mMapView.getMap();
        initMap();

        return v;
    }

    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
                // 当不需要定位图层时关闭定位图层
                //mBaiduMap.setMyLocationEnabled(false);
                if (isFirstLoc) {
                    isFirstLoc = false;
                    LatLng ll = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(18.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                    if (location.getLocType() == BDLocation.TypeGpsLocation) {
                        // GPS定位结果
                        //      Toast.makeText(getActivity(), location.getAddrStr(), Toast.LENGTH_SHORT).show();
                    } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                        // 网络定位结果
                        //   Toast.makeText(getActivity(), location.getAddrStr(), Toast.LENGTH_SHORT).show();

                    } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                        // 离线定位结果
                        Toast.makeText(getActivity(), location.getAddrStr(), Toast.LENGTH_SHORT).show();

                    } else if (location.getLocType() == BDLocation.TypeServerError) {
                        Toast.makeText(getActivity(), "服务器错误，请检查", Toast.LENGTH_SHORT).show();
                    } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                        Toast.makeText(getActivity(), "网络错误，请检查", Toast.LENGTH_SHORT).show();
                    } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                        Toast.makeText(getActivity(), "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }

    };


    private void getAllUserPostion() {
        try {
            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
            MediaType json = MediaType.parse("application/json; charset=utf-8");
            JSONObject object = new JSONObject();
            object.put("method", "ENVIRONMENTAPI_GETALLPOSTION");

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
                    Log.i("zxzx", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
                    String string = null;
                    try {
                        string = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //    Log.i("sss",string);
                    Gson gson = new Gson();


                    JSONObject jsonObj = null;

                    try {
                        jsonObj = new JSONObject(string);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<LatLng> latLngs;
                    boolean success = jsonObj.optBoolean("success");
                    if (success) {
                        JSONObject objResult = jsonObj.optJSONObject("result");
                        Iterator<String> iterator = objResult.keys();
                        JSONArray array;
                        JSONObject obj;
                        while (iterator.hasNext()) {
                            latLngs = new ArrayList<>();
                            String key = iterator.next();
                            array = objResult.optJSONArray(key);
                            Log.i("sss", key+"______________");
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.optJSONObject(i);
                                Log.i("sss", obj.optString("latitude") + "  " + obj.optString("longitude"));

                                //将points集合中的点绘制轨迹线条图层，显示在地图上
                                latLngs.add(new LatLng(Double.parseDouble(obj.optString("latitude")),
                                        Double.parseDouble(obj.optString("longitude"))));
                            }
                            OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(latLngs);
                            mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                        }
                    }


//                        }
//                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocClient != null)
            mLocClient.stop();
        mMapView.onDestroy();
        locationService.unregisterListener(mListener); // 注销掉监听
        locationService.stop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }


    /**
     * @author Mikyou
     * 添加覆盖物
     */
//    private void addMapMarks() {
//        mBaidumap.clear();//先清除一下图层
//        LatLng latLng = null;
//        Marker marker = null;
//        OverlayOptions options;
//        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
//        View view = inflater.inflate(R.layout.item_layout_baidu_map_market, null);//这个是显示的覆盖物，其实是可以显示view的
//        ImageView phone = (ImageView) view.findViewById(R.id.iv_dog_baidu_map_market_phone);
//        //遍历MarkInfo的List一个MarkInfo就是一个Mark
//        for (int i = 0; i < markInfoList.size(); i++) {
//
//            //这是我从网络获取的信息，网络获取就不贴了。。。（不能贴）
//
//            Picasso.with(this).load(markInfoList.get(i).getDog_photo()).into(phone);
//            myMarks = BitmapDescriptorFactory.fromView(view);//引入自定义的覆盖物图标，将其转化成一个BitmapDescriptor对象
//            //经纬度对象
//            latLng = new LatLng(markInfoList.get(i).getLatitude(), markInfoList.get(i).getLongitude());//需要创建一个经纬对象，通过该对象就可以定位到处于地图上的某个具体点
//            //图标
//            options = new MarkerOptions().position(latLng).icon(myMarks).zIndex(9);
//            marker = (Marker) mBaidumap.addOverlay(options);//将覆盖物添加到地图上
//            Bundle bundle = new Bundle();//创建一个Bundle对象将每个mark具体信息传过去，当点击该覆盖物图标的时候就会显示该覆盖物的详细信息
//            bundle.putSerializable("mark", markInfoList.get(i));
//            marker.setExtraInfo(bundle);
//        }
//        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);//通过这个经纬度对象，地图就可以定位到该点
//        mBaidumap.animateMapStatus(msu);
//    }



    @Override
    public boolean onMarkerClick(Marker marker) {



        return true;
    }
}
