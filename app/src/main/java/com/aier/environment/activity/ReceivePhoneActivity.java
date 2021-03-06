package com.aier.environment.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aier.environment.R;
import com.aier.environment.utils.SingleSocket;
import com.aier.environment.utils.ToastUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ReceivePhoneActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private Button btn_receive, btn_cancel, btn_cancel_;
    private LinearLayout ll_toOther_people;
    private boolean isCallToOther;
    private boolean mIsSingle;
    private Long mGroupId;
    private String mTargetId;

    private Socket mSocket;
    private String roomId;
    private String userName, nickname;
    private GroupInfo mGroupInfo;
    private List<UserInfo> mMemberInfoList = new ArrayList<UserInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_receive_phone);
        Intent intent = getIntent();
        isCallToOther = intent.getBooleanExtra("isCallToOther", false);
        mIsSingle = intent.getBooleanExtra("mIsSingle", false);
        mGroupId = intent.getLongExtra("mGroupId", 0);
        mTargetId = intent.getStringExtra("mTargetId");//要呼叫人的id
        roomId = intent.getStringExtra("meet_id");
        ll_toOther_people = findViewById(R.id.ll_toOther_people);
        btn_receive = findViewById(R.id.btn_receive);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel_ = findViewById(R.id.btn_cancel_);
        btn_receive.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_cancel_.setOnClickListener(this);
        socket();
        Log.i("aaa", "onCreate+++++++++++");
    }

    private void socket() {
        if (isCallToOther) {//打给别人的情况
            Random r = new Random();
            roomId = String.valueOf(r.nextInt(1000));
            btn_cancel_.setVisibility(View.VISIBLE);
        } else {
            ll_toOther_people.setVisibility(View.VISIBLE);
        }
        UserInfo myInfo = JMessageClient.getMyInfo();
        userName = myInfo.getUserName();
        Log.i("ddd", "userName " + userName + "房间号随机数" + roomId);
        String url = "https://www.airer.com?userid=" + userName + "&type=mobile";
        mSocket = SingleSocket.getInstance().getSocket(url);
        //注册  事件

        mSocket.on("message", messageData);
        if (isCallToOther) {
            if (mIsSingle) {//单人聊天
                try {
                    JSONObject object = new JSONObject();
                    object.put("userid", mTargetId);//mTargetId 对方id

                    object.put("data", "");
                    object.put("room", roomId);
                    object.put("type", "is-join");
                    Log.i("ddd", object.toString());
                    mSocket.emit("call-user", object.toString());// 单个用户呼叫
                    Log.i("ddd", "单个用户呼叫 call-user 用户id " + mTargetId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {//多人聊天

                try {
                    Conversation conv = JMessageClient.getGroupConversation(mGroupId);
                    mGroupInfo = (GroupInfo) conv.getTargetInfo();
                    mMemberInfoList = mGroupInfo.getGroupMembers();
                    UserInfo userInfo;
                    for (int i = 0; i < mMemberInfoList.size(); i++) {
                        userInfo = mMemberInfoList.get(i);
                        if (userInfo.getUserName() != userName) {
                            JSONObject object = new JSONObject();
                            object.put("userid", userInfo.getUserName());//mTargetId 对方id
                            object.put("data", "");
                            object.put("room", roomId);
                            object.put("type", "is-join");
                            Log.i("ddd", object.toString());
                            mSocket.emit("call-user", object.toString());// 单个用户呼叫
                            Log.i("ddd", "单个用户呼叫 call-user 用户id " + mTargetId);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private Emitter.Listener messageData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    if (data.optString("type").equals("joined")) {
                        //  {"type":"joined","data":{"userid":"0002"}}
                        Log.i("ddd", "message " + data.toString());
                        Intent intent = new Intent(ReceivePhoneActivity.this, MeetingActivity.class);
                        intent.putExtra("user_id", userName);
                        intent.putExtra("meet_id", roomId);
                        intent.putExtra("isCallToOther", isCallToOther);
                        intent.putExtra("mIsSingle", mIsSingle);
                        startActivity(intent);
                        finish();
                    } else if (data.optString("type").equals("join_reject")) {
                        ToastUtil.shortToast(ReceivePhoneActivity.this, "用户拒接视频通话");
                        finish();
                        Log.i("ddd", "message " + data.toString());
                    } else if (data.optString("type").equals("position")) {

                    } else if (data.optString("type").equals("online-user")) {

                    } else {
                        Log.i("ddd", "message " + data.toString());
                        //finish();
                    }
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_receive:
                toMeetingActivity();
//                String[] perm = {Permission.RECORD_AUDIO, Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE};
//
//                if (AndPermission.hasPermissions(ReceivePhoneActivity.this, perm)) {
//                        toMeetingActivity();
//                } else {
//                    AndPermission.with(ReceivePhoneActivity.this).runtime().permission(Permission.RECORD_AUDIO, Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE).onDenied(new Action<List<String>>() {
//                        @Override
//                        public void onAction(List<String> data) {
//                            Toast.makeText(ReceivePhoneActivity.this, "请打开音视频,文件读写权限", Toast.LENGTH_SHORT).show();
//                        }
//                    }).onGranted(new Action<List<String>>() {
//                        @Override
//                        public void onAction(List<String> data) {
//                            toMeetingActivity();
//                        }
//                    }).start();
//                }
                break;
            case R.id.btn_cancel:
            case R.id.btn_cancel_:
                try {
                    JSONObject object = new JSONObject();
                    object.put("userid", userName);
                    object.put("room", "");
                    object.put("from_type", "mobile");
                    mSocket.emit("join", object.toString());// 拒绝电话
                    Log.i("ddd", "拒绝电话 发送 " + object.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
                break;
        }
    }

    private void toMeetingActivity() {
        try {
            JSONObject object = new JSONObject();
            object.put("userid", userName);
            object.put("room", roomId);
            object.put("from_type", "mobile");
            mSocket.emit("join", object.toString());// 接听电话
            Log.i("ddd", "接听电话 发送 " + object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.off("message", messageData);
    }
}
