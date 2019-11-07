package com.aier.environment.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.aier.environment.JGApplication;
import com.aier.environment.R;
import com.aier.environment.controller.MainController;
import com.aier.environment.location.service.LocationService;
import com.aier.environment.model.UserOnlineBean;
import com.aier.environment.utils.SharePreferenceManager;
import com.aier.environment.utils.SingleSocket;
import com.aier.environment.view.MainView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import io.socket.client.Socket;
import cn.jiguang.api.JCoreInterface;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MainController mMainController;
    private MainView mMainView;

    private Boolean isConnected = true;
    private Socket mSocket;
    private Timer timer;
    private Task task;
    LocationClient mLocClient;
    private LocationService locationService;
    private BDLocation mLocation;
    private String userName;
    public String city;
    public UserOnlineBean userOnlineBean;
    private Handler handler = new Handler() {
        @Override
        //重写handleMessage方法,根据msg中what的值判断是否执行后续操作
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                //你要做的事
                if (mLocation != null) {
                    //    Log.i("sss",">>>>>>>>>>>>>心跳"+"location" + mLocation.getLatitude()+" Longitude" + mLocation.getLongitude());

                    try {
                        JSONObject object = new JSONObject();
                        JSONObject obj = new JSONObject();
                        JSONObject obj1 = new JSONObject();
                        object.put("username", userName);
                        obj1.put("longitude", String.valueOf(mLocation.getLongitude()));
                        obj1.put("latitude", String.valueOf(mLocation.getLatitude()));
                        obj.put("position", obj1);
                        object.put("data", obj);
                        Log.i("aaa", object.toString());
                        mSocket.emit("push-position", object.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private void heartinterval() {
        timer = new Timer();
        task = new Task();
        timer.schedule(task, 2000, 1000 * 60);
    }

    public class Task extends TimerTask {
        @Override
        public void run() {
            handler.sendEmptyMessage(0x123);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        mMainView = findViewById(R.id.main_view);
        mMainView.initModule();
        mMainController = new MainController(mMainView, this);

        mMainView.setOnClickListener(mMainController);
        mMainView.setOnPageChangeListener(mMainController);
        UserInfo myInfo = JMessageClient.getMyInfo();
        userName = myInfo.getUserName();
        mLocClient = new LocationClient(this);
        locationService = JGApplication.locationService;
        locationService.registerListener(mListener);//是否应该在onStart中注册
        locationService.start();
        initLocation();
        socket();
        heartinterval();
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
        option.setIsNeedAltitude(true);/**设置海拔高度*/
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocClient.setLocOption(option);
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
                city = location.getAltitude() + "";//海拔
                mMainController.setLocation(location);
                Log.i("ccc", "city " + city);
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    private void socket() {
        UserInfo myInfo = JMessageClient.getMyInfo();
        Log.i("aaa", myInfo.getUserName() + "");
        String url = "https://www.airer.com?userid=" + myInfo.getUserName() + "&type=mobile";
        mSocket = SingleSocket.getInstance().getSocket(url);
        //注册  事件
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("message", messageData);
    }

    private Emitter.Listener messageData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //{"type":"is-join","data":{"from":"0002","room":"51289945","from_type":"pc"}}

                    //接听外面打进的电话
                    JSONObject data = (JSONObject) args[0];
                    Log.i("ddd", " main _message " + data.toString());
                    if (data.optString("type").equals("is-join")) {
                        JSONObject obj = data.optJSONObject("data");
                        String roomId = obj.optString("room");
                        if (!TextUtils.isEmpty(roomId)) {//外面打进电话
                            ActivityManager am = (ActivityManager) MainActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
                            am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
                            Intent intent = new Intent(MainActivity.this, ReceivePhoneActivity.class);
                            intent.putExtra("isCallToOther", false);
                            intent.putExtra("meet_id", roomId);
                            startActivity(intent);
                        }
                    } else if (data.optString("type").equals("online-user")) {
                        Gson gson = new Gson();
                        userOnlineBean = gson.fromJson(data.toString(), UserOnlineBean.class);
                        mMainController.setUserOnline(userOnlineBean);
                    } else if (data.optString("type").equals("position")) {

                    } else {
                        Log.i("ddd", " main _message " + data.toString());
                    }
                }
            });
        }
    };

    @Override
    protected void onResume() {
        JCoreInterface.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        JCoreInterface.onPause(this);
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        Log.i("ddd", "++++++++++++++++");
        mSocket.off();
        SingleSocket.getInstance().disConnect();
        handler.removeCallbacksAndMessages(null);
        locationService.unregisterListener(mListener); // 注销掉监听
        locationService.stop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    private void requestPermission() {
        String[] perm = {
                Permission.RECORD_AUDIO,
                Permission.CAMERA,
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.ACCESS_COARSE_LOCATION,
                Permission.ACCESS_FINE_LOCATION,
                Permission.RECORD_AUDIO,
                Permission.READ_PHONE_STATE
        };

        if (AndPermission.hasPermissions(MainActivity.this, perm)) {
            return;
        } else {
            AndPermission.with(MainActivity.this).runtime().permission(perm).onDenied(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    Toast.makeText(MainActivity.this, "请打开音视频,文件读写权限,定位权限！", Toast.LENGTH_SHORT).show();
                }
            }).onGranted(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    Toast.makeText(MainActivity.this, "获取权限成功！", Toast.LENGTH_SHORT).show();
                }
            }).start();
        }
    }

    public FragmentManager getSupportFragmentManger() {
        return getSupportFragmentManager();
    }

    //连接成功
    private Emitter.Listener onConnect = args -> runOnUiThread(() -> {
        if (!isConnected) {
            isConnected = true;
        }
        Log.i("ddd", "connected success");
    });
    //连接失败
    private Emitter.Listener onDisconnect = args -> {
        isConnected = false;
        Log.i("ddd", "diconnected");
    };
    //连接错误
    private Emitter.Listener onConnectError = args -> runOnUiThread(() -> Log.i("aaa", "Error connecting"));


}
