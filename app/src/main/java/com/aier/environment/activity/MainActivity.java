package com.aier.environment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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
import com.aier.environment.utils.SingleSocket;
import com.aier.environment.view.MainView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.LocationContent;
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

    private LocationService locationService;
    private BDLocation mLocation;
    private String userName;
    private Handler handler = new Handler()
    {
        @Override
        //重写handleMessage方法,根据msg中what的值判断是否执行后续操作
        public void handleMessage(Message msg) {
        if(msg.what == 0x123)
        {
            //你要做的事
            if(mLocation!=null){
            //    Log.i("sss",">>>>>>>>>>>>>心跳"+"location" + mLocation.getLatitude()+" Longitude" + mLocation.getLongitude());

                try {
                    JSONObject object = new JSONObject();
                    JSONObject obj = new JSONObject();
                    object.put("username", userName);
                    obj.put(String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()));
                    object.put("position", obj);
                    Log.i("aaa",object.toString());
                    mSocket.emit("push-position",object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    };

    private void heartinterval(){
       timer = new Timer();
       task = new Task();
       timer.schedule(task, 5000,1000*60);
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
        mMainView =  findViewById(R.id.main_view);
        mMainView.initModule();
        mMainController = new MainController(mMainView, this);

        mMainView.setOnClickListener(mMainController);
        mMainView.setOnPageChangeListener(mMainController);
        UserInfo myInfo = JMessageClient.getMyInfo();
        userName=myInfo.getUserName();
        locationService = JGApplication.locationService;
        locationService.registerListener(mListener);//是否应该在onStart中注册
        locationService.start();
        socket();
        heartinterval();
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
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }

    };

    private void socket() {
        UserInfo myInfo = JMessageClient.getMyInfo();
        Log.i("aaa",myInfo.getUserName() + "");
        String url = "http://192.168.0.68:3002/chat?userid="+myInfo.getUserName()+"&type=mobile";
        mSocket = SingleSocket.getInstance().getSocket(url);
        //注册  事件
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
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
                    if(data.optString("type").equals("is-join")) {
                       JSONObject obj = data.optJSONObject("data");
                        String roomId = obj.optString("room");
                        if(!TextUtils.isEmpty(roomId)){//外面打进电话
                            Intent intent = new Intent(MainActivity.this, ReceivePhoneActivity.class);
                            intent.putExtra("isCallToOther", false);
                            intent.putExtra("meet_id", roomId);
                            startActivity(intent);
                        }

                    }
                    Log.i("aaa","message "+data.toString());

                    Log.i("aaa","+++++++++++++++++++++");
                }
            });
        }
    };

    @Override
    protected void onResume() {
        JCoreInterface.onResume(this);
        mMainController.sortConvList();
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
        mSocket.off();
        SingleSocket.getInstance().disConnect();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        handler.removeCallbacksAndMessages(null);
        locationService.unregisterListener(mListener); // 注销掉监听
        locationService.stop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    private void requestPermission() {
        String[]  perm = {Permission.RECORD_AUDIO,
                Permission.CAMERA,
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.ACCESS_COARSE_LOCATION,
                Permission.ACCESS_FINE_LOCATION,
                Permission.RECORD_AUDIO
        };

        if (AndPermission.hasPermissions(MainActivity.this,perm)){
            return;
        }else {
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
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        isConnected = true;
                    }
                    Log.i("aaa","connected success");
                }
            });
        }
    };
   //连接失败
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
                    isConnected = false;
            Log.i("aaa","diconnected");
        }
    };
    //连接错误
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("aaa","Error connecting");
                }
            });
        }
    };


}
