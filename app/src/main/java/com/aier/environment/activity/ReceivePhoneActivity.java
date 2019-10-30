package com.aier.environment.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aier.environment.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;


public class ReceivePhoneActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private TextView btn_receive,btn_cancel;
    String video_house;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_receive_phone);
        video_house = getIntent().getStringExtra("VIDEO_HOMES");
        btn_receive = findViewById(R.id.btn_receive);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_receive.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

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
                            toMeetingActivity();
                        }
                    }).start();
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }

    }

    private void toMeetingActivity() {
        Intent intent = new Intent(ReceivePhoneActivity.this, MeetingActivity.class);
        intent.putExtra("meet_id", video_house);
        startActivity(intent);
        finish();
    }

}
