package com.aier.environment;

import android.content.Context;
import com.activeandroid.ActiveAndroid;
import com.aier.environment.database.UserEntry;
import com.aier.environment.entity.NotificationClickEventReceiver;
import com.aier.environment.pickerimage.utils.StorageUtil;
import com.aier.environment.utils.SharePreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;


public class JGApplication extends  com.activeandroid.app.Application {
    public static Context context;
    public static final String CONV_TITLE = "conv_title";
    public static final int IMAGE_MESSAGE = 1;
    public static final int TAKE_PHOTO_MESSAGE = 2;
    public static final int TAKE_LOCATION = 3;
    public static final int FILE_MESSAGE = 4;
    public static final int TACK_VIDEO = 5;
    public static final int TACK_VOICE = 6;
    public static final int BUSINESS_CARD = 7;
    public static final int REQUEST_CODE_SEND_FILE = 26;

    public static final int REQUEST_CODE_FRIEND_LIST = 17;
    public static final int RESULT_CODE_AT_ALL = 32;
    public static final int RESULT_CODE_SEND_LOCATION = 25;
    public static final int RESULT_CODE_SEND_FILE = 27;
    public static final int RESULT_CODE_CHAT_DETAIL = 15;
    public static final int RESULT_CODE_AT_MEMBER = 31;

    public static final String DRAFT = "draft";
    public static final String CONV_TYPE = "conversationType"; //value使用 ConversationType
    public static final String ROOM_ID = "roomId";
    public static final String GROUP_ID = "groupId";
    public static final String POSITION = "position";
    public static final String MsgIDs = "msgIDs";
    public static final String MSG_JSON = "msg_json";
    public static final String MSG_LIST_JSON = "msg_list_json";
    public static final String NAME = "name";
    public static final String ATALL = "atall";
    public static final String SEARCH_AT_MEMBER_NAME = "search_at_member_name";
    public static final String SEARCH_AT_MEMBER_USERNAME = "search_at_member_username";
    public static final String SEARCH_AT_APPKEY = "search_at_appkey";

    public static final String MEMBERS_COUNT = "membersCount";

    public static String PICTURE_DIR = "sdcard/JChatDemo/pictures/";
    private static final String JCHAT_CONFIGS = "JChat_configs";
    public static String FILE_DIR = "sdcard/JChatDemo/recvFiles/";
    public static String VIDEO_DIR = "sdcarVIDEOd/JChatDemo/sendFiles/";
    public static String THUMP_PICTURE_DIR;
    public static final String TARGET_ID = "targetId";
    public static final String ATUSER = "atuser";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static int maxImgCount;               //允许选择图片最大数
    public static final String GROUP_NAME = "groupName";
    public static String groupAvatarPath;

    public static long registerOrLogin = 1;
    public static final int RESULT_CODE_ALL_MEMBER = 22;
    public static final int REQUEST_CODE_FRIEND_INFO = 16;





    public static Map<Long, Boolean> isAtMe = new HashMap<>();
    public static Map<Long, Boolean> isAtall = new HashMap<>();
    public static List<Message> forwardMsg = new ArrayList<>();
   // public static LocationService locationService;

    public static List<GroupInfo> mGroupInfoList = new ArrayList<>();
    public static List<UserInfo> mFriendInfoList = new ArrayList<>();
    public static List<UserInfo> mSearchGroup = new ArrayList<>();
    public static List<UserInfo> mSearchAtMember = new ArrayList<>();
    public static List<Message> ids = new ArrayList<>();
    public static List<UserInfo> alreadyRead = new ArrayList<>();
    public static List<UserInfo> unRead = new ArrayList<>();
    public static List<String> forAddFriend = new ArrayList<>();
    public static List<String> forAddIntoGroup = new ArrayList<>();
    public static Conversation delConversation;
    public static ArrayList<String> selectedUser;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        THUMP_PICTURE_DIR = context.getFilesDir().getAbsolutePath() + "/JChatDemo";
        StorageUtil.init(context, null);
        //初始化ActivityAndroid
        JMessageClient.init(getApplicationContext(), true);
        JMessageClient.setDebugMode(true);
        SharePreferenceManager.init(getApplicationContext(), JCHAT_CONFIGS);
        //设置Notification的模式
      //  JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_SOUND | JMessageClient.FLAG_NOTIFY_WITH_LED | JMessageClient.FLAG_NOTIFY_WITH_VIBRATE);
        //注册Notification点击的接收器
      //  new NotificationClickEventReceiver(getApplicationContext());

    }



    public static UserEntry getUserEntry() {
        return UserEntry.getUser(JMessageClient.getMyInfo().getUserName(), JMessageClient.getMyInfo().getAppKey());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();//清理
    }
}
