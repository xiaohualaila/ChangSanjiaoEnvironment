package com.aier.environment.activity.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.aier.environment.JGApplication;
import com.aier.environment.R;
import com.aier.environment.location.service.LocationService;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;


public class MapFragment extends Fragment {
    static MapView mMapView = null;
    // 定位相关
    LocationClient mLocClient;
    private BaiduMap mBaiduMap;
    private LocationService locationService;
    private LatLng latLng;
    private boolean isFirstLoc = true; // 是否首次定位
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

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
        option.setOpenGps(true); // 打开gps

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocClient.setLocOption(option);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView =  v.findViewById(R.id.map);
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
}
