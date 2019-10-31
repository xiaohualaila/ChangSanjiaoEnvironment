package com.aier.environment.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.aier.environment.R;
import com.aier.environment.utils.SingleSocket;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Random;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ReceivePhoneActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private Button btn_receive, btn_cancel,btn_cancel_;
    private LinearLayout ll_toOther_people;
    private boolean isCallToOther;
    private boolean mIsSingle;
    private String mGroupId;
    private String mTargetId;

    private Socket mSocket;
    private String roomId;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_receive_phone);
        Intent intent = getIntent();
        isCallToOther = intent.getBooleanExtra("isCallToOther",false);
        mIsSingle = intent.getBooleanExtra("mIsSingle",false);
//        mGroupId = intent.getStringExtra("mGroupId");
        mTargetId = intent.getStringExtra("mTargetId");
        roomId = intent.getStringExtra("meet_id");
        ll_toOther_people = findViewById(R.id.ll_toOther_people);
        btn_receive = findViewById(R.id.btn_receive);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel_ = findViewById(R.id.btn_cancel_);
        btn_receive.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_cancel_.setOnClickListener(this);
        socket();
        Log.i("aaa","onCreate+++++++++++");
    }

    private void socket() {
        if(isCallToOther){//打给别人的情况
            Random r = new Random();
            roomId = String.valueOf(r.nextInt(1000));
            btn_cancel_.setVisibility(View.VISIBLE);
        }else {
            ll_toOther_people.setVisibility(View.VISIBLE);
        }
        UserInfo myInfo = JMessageClient.getMyInfo();
        userName=myInfo.getUserName();
        Log.i("aaa","房间号随机数"+roomId);
        Log.i("aaa",userName + "");
        String url = "http://192.168.0.68:3002/chat?userid="+userName+"&type=mobile";
        mSocket = SingleSocket.getInstance().getSocket(url);
        //注册  事件

        mSocket.on("message", messageData);
        if(isCallToOther){
       //     if(mIsSingle){//单人聊天
                try {
                    JSONObject object = new JSONObject();
                    object.put("userid", mTargetId);//mTargetId 对方id

                    object.put("data", "");
                    object.put("room", roomId);
                    object.put("type", "is-join");
                    Log.i("aaa",object.toString());
                    mSocket.emit("call-user", object.toString());// 单个用户呼叫
                    Log.i("aaa","单个用户呼叫 call-user 用户id "+mTargetId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        //    }
//            else {//多人聊天
//                try {
//                    JSONObject obj1 = new JSONObject();
//                    obj1.put("room", "111");
//                    obj1.put("data", mGroupId);
//                    Log.i("aaa",obj1.toString());
//                    mSocket.emit("call", obj1.toString());//用户群呼
//                    Log.i("aaa","发送群聊信息 call 群组id"+mGroupId);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
        }

    }


    private Emitter.Listener messageData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    if(data.optString("type").equals("joined")){
                      //  {"type":"joined","data":{"userid":"0002"}}
                        Intent intent = new Intent(ReceivePhoneActivity.this, MeetingActivity.class);
                        intent.putExtra("meet_id", roomId);
                        startActivity(intent);
                        finish();
                    }else {
                        finish();
                    }
                    Log.i("aaa","message "+data.toString());
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_receive:
                String[]  perm = {Permission.RECORD_AUDIO,Permission.CAMERA,Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE};

                if (AndPermission.hasPermissions(ReceivePhoneActivity.this,perm)){
                    toMeetingActivity();
                }else {
                    AndPermission.with(ReceivePhoneActivity.this).runtime().permission(Permission.RECORD_AUDIO,Permission.CAMERA,Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE).onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            Toast.makeText(ReceivePhoneActivity.this, "请打开音视频,文件读写权限", Toast.LENGTH_SHORT).show();
                        }
                    }).onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            try {
                                JSONObject object = new JSONObject();
                                object.put("userid", userName);
                                object.put("room", roomId);
                                object.put("from_type", "mobile");
                                mSocket.emit("join", object.toString());// 接听电话
                                toMeetingActivity();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
            case R.id.btn_cancel:
            case R.id.btn_cancel_:

                try {
                    JSONObject object = new JSONObject();
                    object.put("userid", userName);
                    object.put("room", "");
                    object.put("from_type", "mobile");
                    mSocket.emit("join", object.toString());// 拒绝电话
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
                break;
        }

    }

    private void toMeetingActivity() {
        Intent intent = new Intent(ReceivePhoneActivity.this, MeetingActivity.class);
        intent.putExtra("meet_id", roomId);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("message", messageData);
    }
}
