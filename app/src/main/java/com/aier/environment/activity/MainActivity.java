package com.aier.environment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import com.aier.environment.R;
import com.aier.environment.controller.MainController;
import com.aier.environment.utils.SingleSocket;
import com.aier.environment.view.MainView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import org.json.JSONObject;
import java.util.List;
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
        socket();
    }

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


    }



    private Emitter.Listener messageData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    if(data.optString("type").equals("is-join")) {
                       JSONObject obj = data.optJSONObject("data");
                        String roomId = obj.optString("room");
                        if(!TextUtils.isEmpty(roomId)){
                            Intent intent = new Intent(MainActivity.this, ReceivePhoneActivity.class);
                            intent.putExtra("isCallToOther", false);
                            intent.putExtra("meet_id", roomId);

                            startActivity(intent);
                        }

                    }
                    Log.i("aaa","message "+data.toString());
//{"type":"is-join","data":{"from":"0002","room":"51289945","from_type":"pc"}}
                    Log.i("aaa","+++++++++++++++++++++");
                }
            });
        }
    };

    @Override
    protected void onResume() {
        JCoreInterface.onResume(this);
        mMainController.sortConvList();
        mSocket.on("message", messageData);
        super.onResume();
    }

    @Override
    protected void onPause() {
        JCoreInterface.onPause(this);
        mSocket.off("message", messageData);
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off();
        SingleSocket.getInstance().disConnect();
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
