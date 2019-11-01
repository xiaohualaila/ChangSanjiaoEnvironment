package com.aier.environment.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aier.environment.R;
import com.aier.environment.adapter.LogAdapter;
import com.aier.environment.utils.SingleSocket;
import com.aier.environment.view.ARVideoView;
import com.gyf.barlibrary.ImmersionBar;

import org.ar.common.enums.ARNetQuality;
import org.ar.common.enums.ARVideoCommon;
import org.ar.common.utils.AR_AudioManager;
import org.ar.meet_kit.ARMeetEngine;
import org.ar.meet_kit.ARMeetEvent;
import org.ar.meet_kit.ARMeetKit;
import org.ar.meet_kit.ARMeetOption;
import org.ar.meet_kit.ARMeetType;
import org.ar.meet_kit.ARMeetZoomMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.VideoRenderer;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MeetingActivity extends AppCompatActivity implements View.OnClickListener {

    View space;
    RelativeLayout rlVideo, rl_log_layout;
    ImageButton ibCamera, ibLog, ibtn_close_log;
    Button ibVideo, ibAudio, ibHangUp;
    RecyclerView rvLog;
    TextView tvRoomId;
    LinearLayout ll_bottom_layout;
    protected ImmersionBar mImmersionBar;
    private LogAdapter logAdapter;
    private ARVideoView mVideoView;
    private ARMeetKit mMeetKit;
    private String meetId = "";
    private String userId = (int) ((Math.random() * 9 + 1) * 100000) + "";
    //    private String userId="654321";
    AR_AudioManager arAudioManager;
    private String nickname = "";

    private Socket mSocket;
    private boolean isCallToOther;
    private boolean mIsSingle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        this.setContentView(this.getLayoutId());
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
        this.initView();
        Intent intent = getIntent();
        isCallToOther = intent.getBooleanExtra("isCallToOther", false);
        mIsSingle = intent.getBooleanExtra("mIsSingle", false);
        socket();
    }


    private void socket() {
        UserInfo myInfo = JMessageClient.getMyInfo();
        nickname = myInfo.getNickname();
        String url = "http://192.168.0.68:3002/chat?userid=" + myInfo.getUserName() + "&type=mobile";
        mSocket = SingleSocket.getInstance().getSocket(url);
        //注册  事件
        mSocket.on("message", messageData);
    }


    private Emitter.Listener messageData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //{"type":"quit","data":{"userid":"0002"}}
                    JSONObject data = (JSONObject) args[0];
                    if (data.optString("type").equals("quit")) {
                        if (isCallToOther && mIsSingle) {
                            if (mMeetKit != null) {
                                mMeetKit.clean();
                            }
                            finishAnimActivity();
                        }
                    }

                    Log.i("xxx", "message " + data.toString());
                }
            });
        }
    };

    public int getLayoutId() {
        return R.layout.activity_meeting;
    }

    public void initView() {

        space = findViewById(R.id.view_space);
        mImmersionBar.titleBar(space).init();
        tvRoomId = findViewById(R.id.tv_room_id);
        rvLog = findViewById(R.id.rv_log);
        rlVideo = findViewById(R.id.rl_video);
        ibCamera = findViewById(R.id.btn_camare);
        rl_log_layout = findViewById(R.id.rl_log_layout);
        ibtn_close_log = findViewById(R.id.ibtn_close_log);
        ll_bottom_layout = findViewById(R.id.ll_bottom_layout);
        ibVideo = findViewById(R.id.ib_video);
        ibAudio = findViewById(R.id.ib_audio);
        ibLog = findViewById(R.id.btn_log);
        ibHangUp = findViewById(R.id.ib_leave);
        ibCamera.setOnClickListener(this);
        ibVideo.setOnClickListener(this);
        ibAudio.setOnClickListener(this);
        ibLog.setOnClickListener(this);
        ibHangUp.setOnClickListener(this);
        ibtn_close_log.setOnClickListener(this);
        logAdapter = new LogAdapter();
        rvLog.setLayoutManager(new LinearLayoutManager(this));
        logAdapter.bindToRecyclerView(rvLog);
        meetId = getIntent().getStringExtra("meet_id");
        nickname = getIntent().getStringExtra("nickname");
        tvRoomId.setText("房间ID：" + meetId);
        mVideoView = new ARVideoView(rlVideo, ARMeetEngine.Inst().Egl(), this);
        mVideoView.setVideoViewLayout(false, Gravity.CENTER, LinearLayout.HORIZONTAL);
        //获取配置类
        ARMeetOption option = ARMeetEngine.Inst().getARMeetOption();
        //设置默认为前置摄像头
        option.setDefaultFrontCamera(true);
        //设置视频分辨率
        option.setVideoProfile(ARVideoCommon.ARVideoProfile.ARVideoProfile360x640);
        //设置会议类型
        option.setMeetType(ARMeetType.Normal);
        option.setMediaType(ARVideoCommon.ARMediaType.Video);
        mMeetKit = new ARMeetKit(arMeetEvent);
        mMeetKit.setFrontCameraMirrorEnable(true);
        mMeetKit.setNetworkStatus(true);
        VideoRenderer localVideoRender = mVideoView.openLocalVideoRender();
        mMeetKit.setLocalVideoCapturer(localVideoRender.GetRenderPointer());
        mMeetKit.joinRTCByToken("", meetId, userId, getUserInfo());
        arAudioManager = AR_AudioManager.create(this, new Runnable() {
            @Override
            public void run() {

            }
        });
        arAudioManager.init();


    }

    public String getUserInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("MaxJoiner", 6);
            jsonObject.put("userId", userId);
            jsonObject.put("nickName", nickname);
            jsonObject.put("headUrl", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camare:
                mMeetKit.switchCamera();//翻转摄像头
                if (ibCamera.isSelected()) {
                    ibCamera.setSelected(false);
                } else {
                    ibCamera.setSelected(true);
                }
                logAdapter.addData("方法：翻转摄像头");
                break;
            case R.id.ib_audio:
                if (ibAudio.isSelected()) {
                    ibAudio.setSelected(false);
                    mMeetKit.setLocalAudioEnable(true);//允许本地音频传输
                } else {
                    ibAudio.setSelected(true);
                    mMeetKit.setLocalAudioEnable(false);//禁止本地音频传输
                }
                logAdapter.addData("方法：" + (ibAudio.isSelected() ? "本地音频传输关闭" : "本地音频传输开启"));
                break;
            case R.id.ib_video:
                if (ibVideo.isSelected()) {
                    mMeetKit.setLocalVideoEnable(true);//允许本地视频传输
                    ibVideo.setSelected(false);
                } else {
                    ibVideo.setSelected(true);
                    mMeetKit.setLocalVideoEnable(false);//禁止本地视频传输
                }
                logAdapter.addData("方法：" + (ibVideo.isSelected() ? "本地视频传输关闭" : "本地视频传输开启"));
                break;
            case R.id.btn_log:
                rl_log_layout.setVisibility(View.VISIBLE);
                break;
            case R.id.ibtn_close_log:
                rl_log_layout.setVisibility(View.GONE);
                break;
            case R.id.ib_leave:
                try {
                    JSONObject object = new JSONObject();
                    object.put("room", meetId);
                    mSocket.emit("leave", object.toString());// 挂掉电话
                    mSocket.off("message", messageData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (mMeetKit != null) {
                    mMeetKit.clean();
                }
                finishAnimActivity();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMeetKit != null) {
                mMeetKit.clean();
            }
            finishAnimActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    ARMeetEvent arMeetEvent = new ARMeetEvent() {
        @Override
        public void onRTCJoinMeetOK(final String roomId) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCJoinMeetOK 加入房间成功 ID：" + roomId);
                }
            });
        }

        @Override
        public void onRTCJoinMeetFailed(final String roomId, final int code, final String reason) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCJoinMeetFailed 加入房间失败 ID：" + roomId + "code:" + code + "reason:" + reason);
                    if (code == 701) {
                        Toast.makeText(MeetingActivity.this, "会议人数已满", Toast.LENGTH_SHORT).show();
                        finishAnimActivity();
                    }
                }
            });
        }

        @Override
        public void onRTCLeaveMeet(final int code) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCLeaveMeet 离开房间 code：" + code);
                }
            });
        }

        @Override
        public void onRTCOpenRemoteVideoRender(final String peerId, final String publishId, final String userId, String userData) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCOpenRemoteVideoRender 远程视频流接入即将渲染显示 publishId：" + publishId + "\n peerId:" + peerId + "user:" + userId);

                    final VideoRenderer render = mVideoView.openRemoteVideoRender(publishId);
                    if (null != render) {
                        mMeetKit.setRemoteVideoRender(publishId, render.GetRenderPointer());
                    }
                }
            });
        }

        @Override
        public void onRTCCloseRemoteVideoRender(final String peerId, final String publishId, final String userId) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCCloseRemoteVideoRender 远程视频流关闭 publishId：" + publishId + "\n peerId:" + peerId + "user:" + userId);

                    if (mMeetKit != null && mVideoView != null) {
                        mVideoView.removeRemoteRender(publishId);
                        mMeetKit.setRemoteVideoRender(publishId, 0);
                    }

                }
            });
        }

        @Override
        public void onRTCOpenScreenRender(final String peerId, final String publishId, final String userId, String userData) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCOpenScreenRender 屏幕共享流接入即将渲染显示 publishId：" + publishId + "\n peerId:" + peerId + "user:" + userId);
                    final VideoRenderer render = mVideoView.openRemoteVideoRender("ScreenShare");
                    if (null != render) {
                        mMeetKit.setRemoteVideoRender(publishId, render.GetRenderPointer());
                    }
                }
            });
        }

        @Override
        public void onRTCCloseScreenRender(final String peerId, final String publishId, final String userId) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCCloseScreenRender 屏幕共享流关闭 publishId：" + publishId + "\n peerId:" + peerId + "user:" + userId);
                    if (mMeetKit != null && mVideoView != null) {
                        mVideoView.removeRemoteRender("ScreenShare");
                        mMeetKit.setRemoteVideoRender(publishId, 0);
                    }
                }
            });
        }


        @Override
        public void onRTCOpenRemoteAudioTrack(final String peerId, final String userId, String userData) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRtcOpenRemoteAudioTrack 远程音频流接入 peerId:" + peerId + "user:" + userId);
                }
            });
        }

        @Override
        public void onRTCCloseRemoteAudioTrack(final String peerId, final String userId) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRtcOpenRemoteAudioTrack 远程音频流离开 peerId:" + peerId + "user:" + userId);
                }
            });
        }

        @Override
        public void onRTCLocalAudioPcmData(String peerId, byte[] data, int nLen, int nSampleHz, int nChannel) {

        }

        @Override
        public void onRTCRemoteAudioPcmData(String peerId, byte[] data, int nLen, int nSampleHz, int nChannel) {

        }

        @Override
        public void onRTCRemoteAVStatus(final String peerId, final boolean bAudio, final boolean bVideo) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCRemoteAVStatus 远程用户音视频状态  peerId:" + peerId + "bAudio:" + bAudio + "bVideo:" + bVideo);
                }
            });
        }

        @Override
        public void onRTCLocalAVStatus(final boolean bAudio, final boolean bVideo) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCLocalAVStatus 本地音视频状态 bAudio:" + bAudio + "bVideo:" + bVideo);
                }
            });
        }

        @Override
        public void onRTCRemoteAudioActive(final String peerId, String userId, final int nLevel, int nTime) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }


        @Override
        public void onRTCLocalAudioActive(final int nLevel, int nTime) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onRTCRemoteNetworkStatus(final String publishId, String userId, final int nNetSpeed, final int nPacketLost, final ARNetQuality netQuality) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mVideoView != null) {
                        mVideoView.updateRemoteNetStatus(publishId, String.valueOf(nNetSpeed / 1024 * 8));
                    }

                }
            });
        }

        @Override
        public void onRTCLocalNetworkStatus(final int nNetSpeed, final int nPacketLost, final ARNetQuality netQuality) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mVideoView != null) {
                        mVideoView.updateLocalNetStatus(String.valueOf(nNetSpeed / 1024 * 8));
                    }
                }
            });
        }

        @Override
        public void onRTCConnectionLost() {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCConnectionLost 网络丢失....");
                }
            });
        }

        @Override
        public void onRTCUserMessage(String userId, final String userName, String headUrl, final String message) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCUserMessage 收到消息 " + "\n姓名：" + userName + "\n消息：" + message);
                }
            });
        }

        @Override
        public void onRTCShareEnable(final boolean success) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCShareEnable 分享通道开启关闭结果" + success);
                }
            });
        }

        @Override
        public void onRTCShareOpen(final int type, final String shareInfo, String userId, String userData) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCShareOpen 分享通道开启 shareInfo:" + shareInfo + "==========" + type);
                }
            });
        }

        @Override
        public void onRTCShareClose() {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCShareClose 分享通道关闭");
                }
            });
        }

        @Override
        public void onRTCHosterOnline(final String peerId, final String userId, String userData) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCHosterOnline 主持人上线 peerId:" + peerId);
                }
            });
        }

        @Override
        public void onRTCHosterOffline(final String peerId) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRTCHosterOnline 主持人下线 peerId:" + peerId);
                }
            });
        }

        @Override
        public void onRTCTalkOnlyOn(String peerId, String userId, String userData) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onRTCTalkOnlyOff(String peerId) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onRtcUserCome(final String peerId, String publishId, String userId, String userData) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRtcUserCome 有人进入 peerId:" + peerId);
                }
            });
        }

        @Override
        public void onRtcUserOut(final String peerId, String publishId, String userId) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    logAdapter.addData("回调：onRtcUserOut 有人退出 peerId:" + peerId);
                }
            });
        }

        @Override
        public void onRTCZoomPageInfo(ARMeetZoomMode zoomMode, int allPages, int curPage, int allRender, int screenIndex, int num) {
            MeetingActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.removeLocalVideoRender();
            mVideoView.removeAllRemoteRender();
        }
        if (arAudioManager != null) {
            arAudioManager.close();
            arAudioManager = null;
        }
        if (mImmersionBar != null)
            mImmersionBar.destroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        ll_bottom_layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVideoView.setBottomHeight(ll_bottom_layout.getMeasuredHeight());
            }
        }, 100);
    }

    public void finishAnimActivity() {
        finish();
    }
}
