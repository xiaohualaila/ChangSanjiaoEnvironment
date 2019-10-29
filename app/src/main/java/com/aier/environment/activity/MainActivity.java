package com.aier.environment.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.aier.environment.R;
import com.aier.environment.controller.MainController;
import com.aier.environment.view.MainView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import cn.jiguang.api.JCoreInterface;

public class MainActivity extends AppCompatActivity {
    private MainController mMainController;
    private MainView mMainView;
    private List<String> permissions;
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
      //  socket();
    }
    Socket socket;
    private void socket() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("192.168.1.101", 30000);
                    // socket.setSoTimeout(10000);//设置10秒超时
                    Log.i("Android", "与服务器建立连接：" + socket);
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line = br.readLine();
                    Log.i("Android", "与服务器建立连接：" + line);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = line;
                    handler.sendMessage(msg);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }


   private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                Log.i("ssss","这是来自服务器的数据:"+msg.obj.toString());
                Intent intent = new Intent(MainActivity.this,ReceivePhoneActivity.class);
                intent.putExtra("VIDEO_HOMES","111");
                startActivity(intent);
            }
        }
    };


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
}
