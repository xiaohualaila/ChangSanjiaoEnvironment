package com.aier.environment.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aier.environment.JGApplication;
import com.aier.environment.R;
import com.aier.environment.activity.ReceivePhoneActivity;
import com.aier.environment.location.service.LocationService;
import com.aier.environment.model.MyMarkerBean;
import com.aier.environment.model.UserOnlineBean;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
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
    Map<String, List<LatLng>> maps = new HashMap<>();
    private String my_name;
    public UserOnlineBean userOnlineBean;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserInfo myInfo = JMessageClient.getMyInfo();
        my_name = myInfo.getUserName();
    }

    private void initMap() {
        //获取地图控件引用
        mBaiduMap = mMapView.getMap();
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
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }
        //配置定位SDK参数
        initLocation();
        getAllUserPostion();

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
//                MyLocationData locData = new MyLocationData.Builder()
//                        .accuracy(location.getRadius())
//                        // 此处设置开发者获取到的方向信息，顺时针0-360
//                        .direction(100).latitude(location.getLatitude())
//                        .longitude(location.getLongitude()).build();
//                // 设置定位数据
//                mBaiduMap.setMyLocationData(locData);
                // 当不需要定位图层时关闭定位图层
                //mBaiduMap.setMyLocationEnabled(false);

                if (isFirstLoc) {
                //    showLation(location);
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

    List<MyMarkerBean> markInfoList = new ArrayList<>();
    LatLng mLatLng;

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
                  //  Log.i("sss", e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String string = null;
                    try {
                        string = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    JSONObject jsonObj = null;

                    try {
                        jsonObj = new JSONObject(string);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    List<LatLng> latLngs;
                    MyMarkerBean myMarkerBean;
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
                        //    Log.i("sss", key + "______________");
                            myMarkerBean = new MyMarkerBean();
                            myMarkerBean.name = key;
                            for (int i = 0; i < array.length(); i++) {
                                obj = array.optJSONObject(i);

                              //  Log.i("sss", obj.optString("latitude") + "  " + obj.optString("longitude"));
                                mLatLng = new LatLng(Double.parseDouble(obj.optString("latitude")),
                                        Double.parseDouble(obj.optString("longitude")));
                                //将points集合中的点绘制轨迹线条图层，显示在地图上
                                latLngs.add(mLatLng);
                                if (i == 0) {

                                    myMarkerBean.latitude = Double.parseDouble(obj.optString("latitude"));
                                    myMarkerBean.longitude = Double.parseDouble(obj.optString("longitude"));
                                    markInfoList.add(myMarkerBean);
                                }
                            }
                            maps.put(key, latLngs);
                        }
                    }
                    addMapMarks();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //设置用户在线状态
    private void setUserOnline(MyMarkerBean myMarkerBean) {
        if (userOnlineBean != null) {//设置是否在线
            List<UserOnlineBean.DataBean> all = userOnlineBean.getData();
            if (all != null && all.size() > 0) {
                for (int z = 0; z < all.size(); z++) {
                    //  Log.i("bbb","============="+myMarkerBean.name + " "+all.get(z).getUsername());
                    if (myMarkerBean.name.equals(all.get(z).getUsername())) {
                        myMarkerBean.isOnline = true;
                        return;
                    } else {
                        myMarkerBean.isOnline = false;
                    }
                }
            }
        }
    }

    //设置用户在线状态
    public void setUserOnline2(UserOnlineBean bean) {
        userOnlineBean = bean;
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

    private void addMapMarks() {
        mBaiduMap.clear();//先清除一下图层
        LatLng latLng = null;
        Marker marker = null;
        OverlayOptions options;

        MyMarkerBean markerBeanData;
        for (int i = 0; i < markInfoList.size(); i++) {
            markerBeanData = markInfoList.get(i);
            setUserOnline(markerBeanData);
            //经纬度对象
            latLng = new LatLng(markerBeanData.latitude, markerBeanData.longitude);//需要创建一个经纬对象，通过该对象就可以定位到处于地图上的某个具体点
            BitmapDescriptor markerIcon;
            if (markerBeanData.isOnline) {
                markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_online);
            } else {
                markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_offonline);
            }

            //图标
            options = new MarkerOptions().position(latLng).icon(markerIcon).zIndex(9);
            marker = (Marker) mBaiduMap.addOverlay(options);//将覆盖物添加到地图上
            Bundle bundle = new Bundle();
            bundle.putSerializable("marker", markerBeanData);
            marker.setExtraInfo(bundle);
        }
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);//通过这个经纬度对象，地图就可以定位到该点
        mBaiduMap.animateMapStatus(msu);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {


        Bundle bundle = marker.getExtraInfo();
        MyMarkerBean markerBean = (MyMarkerBean) bundle.getSerializable("marker");
     //   Log.i("sss", markerBean.name);
        if (markerBean.name.equals(my_name)) { //如果是他自己隐藏通话按钮
            View view1 = View.inflate(getActivity(), R.layout.marker_click_not_tonghua_window, null);
            TextView tv_guiji = view1.findViewById(R.id.tv_guiji);
            ImageView iv_x = view1.findViewById(R.id.iv_x);
            TextView tv_marker_name = view1.findViewById(R.id.tv_marker_name);
            tv_marker_name.setText(markerBean.name);

            if (!markerBean.isShowGuiji) {
                tv_guiji.setText("显示轨迹");
            } else {
                tv_guiji.setText("隐藏轨迹");
            }
         //   Log.i("sss", markerBean.name + "  my_name" + my_name);
            iv_x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBaiduMap.hideInfoWindow();
                }
            });
            tv_guiji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBaiduMap.hideInfoWindow();
                    //将marker移动到地图中间
                    MapStatus mMapStatus = new MapStatus.Builder().target(new LatLng(markerBean.latitude, markerBean.longitude)).zoom(18).build();
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    mBaiduMap.animateMapStatus(mMapStatusUpdate);
                    if (!markerBean.isShowGuiji) {
                        markerBean.isShowGuiji = true;
                        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(maps.get(markerBean.name));
                        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                        tv_guiji.setText("隐藏轨迹");
                        // mPolyline.setZIndex(3);
                    } else {
                        markerBean.isShowGuiji = false;
                        addMapMarks1();
                    }
                }
            });
            final InfoWindow mInfoWindow = new InfoWindow(view1, marker.getPosition(), -47);
            mBaiduMap.showInfoWindow(mInfoWindow);
        }else {
            View view = View.inflate(getActivity(), R.layout.marker_click_window, null);
            TextView tv_tonghua = view.findViewById(R.id.tv_tonghua);
            TextView tv_guiji = view.findViewById(R.id.tv_guiji);
            ImageView iv_x = view.findViewById(R.id.iv_x);
            TextView tv_marker_name = view.findViewById(R.id.tv_marker_name);
            tv_marker_name.setText(markerBean.name);

            if (!markerBean.isShowGuiji) {
                tv_guiji.setText("显示轨迹");
            } else {
                tv_guiji.setText("隐藏轨迹");
            }
            tv_tonghua.setVisibility(View.VISIBLE);
            tv_tonghua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBaiduMap.hideInfoWindow();
                    Intent intent = new Intent(getActivity(), ReceivePhoneActivity.class);
                    intent.putExtra("isCallToOther", true);
                    intent.putExtra("mIsSingle", true);
                    intent.putExtra("mTargetId", markerBean.name);
                    intent.putExtra("mGroupId", "");
                    startActivity(intent);
                }
            });
            iv_x.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBaiduMap.hideInfoWindow();
                }
            });
            tv_guiji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBaiduMap.hideInfoWindow();
                    //将marker移动到地图中间
                    MapStatus mMapStatus = new MapStatus.Builder().target(new LatLng(markerBean.latitude, markerBean.longitude)).zoom(18).build();
                    MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                    mBaiduMap.animateMapStatus(mMapStatusUpdate);
                    if (!markerBean.isShowGuiji) {
                        markerBean.isShowGuiji = true;
                        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(maps.get(markerBean.name));
                        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                        tv_guiji.setText("隐藏轨迹");
                        // mPolyline.setZIndex(3);
                    } else {
                        markerBean.isShowGuiji = false;
                        addMapMarks1();
                    }
                }
            });
            final InfoWindow mInfoWindow = new InfoWindow(view, marker.getPosition(), -47);
            mBaiduMap.showInfoWindow(mInfoWindow);
//            ViewGroup.LayoutParams params1 = new MapViewLayoutParams.Builder()
//                    .layoutMode(MapViewLayoutParams.ELayoutMode.mapMode)
//                    .position(new LatLng(markerBean.latitude,markerBean.longitude))
//                    .width(MapViewLayoutParams.WRAP_CONTENT)
//                    .height(MapViewLayoutParams.WRAP_CONTENT)
//                    .yOffset(-32)
//                    .build();
//
//            mMapView.addView(view,params1);
        }

        return true;

    }

    private void addMapMarks1() {
        mBaiduMap.clear();//先清除一下图层
        LatLng latLng = null;
        Marker marker = null;
        OverlayOptions options;

        MyMarkerBean markerBeanData;
        for (int i = 0; i < markInfoList.size(); i++) {
            markerBeanData = markInfoList.get(i);
            setUserOnline(markerBeanData);
            //经纬度对象
            latLng = new LatLng(markerBeanData.latitude, markerBeanData.longitude);//需要创建一个经纬对象，通过该对象就可以定位到处于地图上的某个具体点

            BitmapDescriptor markerIcon;
            if (markerBeanData.isOnline) {
                markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_online);
            } else {
                markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_offonline);
            }
            //图标
            options = new MarkerOptions().position(latLng).icon(markerIcon).zIndex(9);
            marker = (Marker) mBaiduMap.addOverlay(options);//将覆盖物添加到地图上
            Bundle bundle = new Bundle();
            bundle.putSerializable("marker", markerBeanData);
            marker.setExtraInfo(bundle);
        }
        // MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);//通过这个经纬度对象，地图就可以定位到该点
        //  mBaiduMap.animateMapStatus(msu);
    }

}
