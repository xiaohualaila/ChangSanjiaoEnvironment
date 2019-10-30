package com.aier.environment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.aier.environment.JGApplication;
import com.aier.environment.R;
import com.aier.environment.controller.MainController;
import com.aier.environment.database.UserEntry;
import com.aier.environment.view.MainView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import cn.jiguang.api.JCoreInterface;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity {
    private MainController mMainController;
    private MainView mMainView;

    private Socket mSocket;
    private Boolean isConnected = true;
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
        try {
            Log.i("aaa",JGApplication.getUserEntry().getId()+"");
            mSocket = IO.socket("http://192.168.0.68:3002/chat?userid="+JGApplication.getUserEntry().getId()+"&type=mobile");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
            mSocket.on(Socket.EVENT_CONNECT,onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("message", message);
            mSocket.on("call", call);
            mSocket.on("join", join);
            mSocket.on("leave", leave);
            mSocket.connect();
    }



    @Override
    protected void onPause() {
        JCoreInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        JCoreInterface.onResume(this);
        mMainController.sortConvList();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT,onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("message", message);
        mSocket.off("call", call);
        mSocket.off("join", join);
        mSocket.off("leave", leave);
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
                        Log.i("aaa","connected success");
                        isConnected = true;
                    }
                }
            });
        }
    };
   //连接失败
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
                //    Log.i(TAG, "diconnected");
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
            //       Log.e(TAG, "Error connecting");
                    Log.i("aaa","Error connecting");
                }
            });
        }
    };

    private Emitter.Listener call = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("aaa",data.toString());
                    Intent intent =new Intent(MainActivity.this,ReceivePhoneActivity.class);
                    intent.putExtra("VIDEO_HOMES","");
                }
            });
        }
    };
    private Emitter.Listener join = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("aaa",data.toString());

                }
            });
        }
    };
    private Emitter.Listener leave = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("aaa",data.toString());

                }
            });
        }
    };
    private Emitter.Listener message = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i("aaa","message "+data.toString());

                }
            });
        }
    };
}
