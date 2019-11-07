package com.aier.environment.controller;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.activeandroid.ActiveAndroid;
import com.aier.environment.JGApplication;
import com.aier.environment.R;
import com.aier.environment.activity.GroupActivity;
import com.aier.environment.activity.SearchContactsActivity;
import com.aier.environment.adapter.StickyListAdapter;
import com.aier.environment.database.FriendEntry;
import com.aier.environment.database.UserEntry;
import com.aier.environment.model.FriendBean;
import com.aier.environment.utils.pinyin.HanziToPinyin;
import com.aier.environment.utils.pinyin.PinyinComparator;
import com.aier.environment.utils.sidebar.SideBar;
import com.aier.environment.view.ContactsView;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cn.jpush.im.android.api.JMessageClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by ${chenyn} on 2017/2/20.
 */

public class ContactsController implements View.OnClickListener, SideBar.OnTouchingLetterChangedListener {
    private ContactsView mContactsView;
    private Activity mContext;
    private List<FriendEntry> mList = new ArrayList<>();
    private StickyListAdapter mAdapter;
    private TextView mAllContactNumber;
    private List<FriendEntry> forDelete = new ArrayList<>();
    List<FriendBean.ResultBean.UsersBean> mListUsersBean =new ArrayList<>();
    private String appkey;

    public ContactsController(ContactsView mContactsView, FragmentActivity context) {
        this.mContactsView = mContactsView;
        this.mContext = context;
        mAllContactNumber = mContext.findViewById(R.id.all_contact_number);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.ib_goToAddFriend://标题栏加号添加好友
                // intent.setClass(mContext, SearchForAddFriendActivity.class);
                //  mContext.startActivity(intent);
                break;
            case R.id.verify_ll://验证消息
                Log.i("sss", "验证消息+++");
//                intent.setClass(mContext, VerificationMessageActivity.class);
//                mContext.startActivity(intent);
//                mContactsView.dismissNewFriends();
//                mAllContactNumber.setVisibility(View.GONE);
                break;
            case R.id.group_ll://群组
                Log.i("sss", "群组+++");
                intent.setClass(mContext, GroupActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.search_title://查找
                intent.setClass(mContext, SearchContactsActivity.class);
                mContext.startActivity(intent);
                break;
            default:
                break;
        }
    }


    public void initContacts() {
        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                JMessageClient.getMyInfo().getAppKey());
        appkey = JMessageClient.getMyInfo().getAppKey();
        mContactsView.showLoadingHeader();
//        ContactManager.getFriendList(new GetUserInfoListCallback() {
//            @Override
//            public void gotResult(int responseCode, String responseMessage, List<UserInfo> userInfoList) {
//                mContactsView.dismissLoadingHeader();
//                if (responseCode == 0) {
//                    if (userInfoList != null && userInfoList.size() != 0) {
//                        mContactsView.dismissLine();
//                        ActiveAndroid.beginTransaction();
//                        try {
//                            for (UserInfo userInfo : userInfoList) {
//                                String displayName = userInfo.getDisplayName();
//                                String letter;
//                                if (!TextUtils.isEmpty(displayName.trim())) {
//                                    ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(displayName);
//                                    StringBuilder sb = new StringBuilder();
//                                    if (tokens != null && tokens.size() > 0) {
//                                        for (HanziToPinyin.Token token : tokens) {
//                                            if (token.type == HanziToPinyin.Token.PINYIN) {
//                                                sb.append(token.target);
//                                            } else {
//                                                sb.append(token.source);
//                                            }
//                                        }
//                                    }
//                                    String sortString = sb.toString().substring(0, 1).toUpperCase();
//                                    if (sortString.matches("[A-Z]")) {
//                                        letter = sortString.toUpperCase();
//                                    } else {
//                                        letter = "#";
//                                    }
//                                } else {
//                                    letter = "#";
//                                }
//                                //避免重复请求时导致数据重复A
//                                FriendEntry friend = FriendEntry.getFriend(user,
//                                        userInfo.getUserName(), userInfo.getAppKey());
//                                if (null == friend) {
//                                    if (TextUtils.isEmpty(userInfo.getAvatar())) {
//                                        friend = new FriendEntry(userInfo.getUserID(), userInfo.getUserName(), userInfo.getNotename(), userInfo.getNickname(), userInfo.getAppKey(),
//                                                null, displayName, letter, user);
//                                    } else {
//                                        friend = new FriendEntry(userInfo.getUserID(), userInfo.getUserName(), userInfo.getNotename(), userInfo.getNickname(), userInfo.getAppKey(),
//                                                userInfo.getAvatarFile().getAbsolutePath(), displayName, letter, user);
//                                    }
//                                    friend.save();
//                                    mList.add(friend);
//                                }
//                                forDelete.add(friend);
//                            }
//                            ActiveAndroid.setTransactionSuccessful();
//                        } finally {
//                            ActiveAndroid.endTransaction();
//                        }
//                    } else {
//                        mContactsView.showLine();
//                    }
//                    //其他端删除好友后,登陆时把数据库中的也删掉
//                    List<FriendEntry> friends = JGApplication.getUserEntry().getFriends();
//                    friends.removeAll(forDelete);
//                    for (FriendEntry del : friends) {
//                        del.delete();
//                        mList.remove(del);
//                    }
//                    Collections.sort(mList, new PinyinComparator());//按拼音排序
//                    mAdapter = new StickyListAdapter(mContext, mList, false);
//                    mContactsView.setAdapter(mAdapter);
//                }
//            }
//        });
        // TODO: 2019/10/29 替换网络请求

        OkHttpClient client2 = new OkHttpClient();//创建OkHttpClient对象。
        MediaType json2 = MediaType.parse("application/json; charset=utf-8");
        JSONObject object2 = new JSONObject();
        JSONObject obj12 = new JSONObject();

        try {
            object2.put("method", "ENVIRONMENTAPI_IMUSERSLIST");
            obj12.put("start", 0);
            obj12.put("count", 100);
            object2.put("params", obj12);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //  Log.i("sss",object.toString());

        RequestBody body2 = RequestBody.create(json2, object2.toString());
        Request request2 = new Request.Builder()//创建Request 对象。
                .url("http://121.41.52.56:3001/environmentalapi")
                .post(body2)
                .build();
        Call call2 = client2.newCall(request2);
        //请求加入调度
        call2.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {//回调的方法执行在子线程。
                    String str = null;
                    try {
                        str = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Gson gson = new Gson();
                    FriendBean bean = gson.fromJson(str, FriendBean.class);
                    Log.i("sss", str);
                    if (bean.isSuccess()) {
                        List<FriendBean.ResultBean.UsersBean> list = bean.getResult().getUsers();
                        mListUsersBean.clear();
                        mListUsersBean.addAll(list);
//                        if (list != null && list.size() != 0) {
//                            ActiveAndroid.beginTransaction();
//                            try {
//                                for (int i = 0; i < list.size(); i++) {
//                                    FriendBean.ResultBean.UsersBean usersBean = list.get(i);
//                                    String displayName = usersBean.getUsername();
//                                    String letter;
//                                    if (!TextUtils.isEmpty(displayName.trim())) {
//                                        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(displayName);
//                                        StringBuilder sb = new StringBuilder();
//                                        if (tokens != null && tokens.size() > 0) {
//                                            for (HanziToPinyin.Token token : tokens) {
//                                                if (token.type == HanziToPinyin.Token.PINYIN) {
//                                                    sb.append(token.target);
//                                                } else {
//                                                    sb.append(token.source);
//                                                }
//                                            }
//                                        }
//                                        String sortString = sb.toString().substring(0, 1).toUpperCase();
//                                        if (sortString.matches("[A-Z]")) {
//                                            letter = sortString.toUpperCase();
//                                        } else {
//                                            letter = "#";
//                                        }
//                                    } else {
//                                        letter = "#";
//                                    }
//                                    //避免重复请求时导致数据重复A
//                                    FriendEntry friend = FriendEntry.getFriend(user, usersBean.getUsername(), appkey);
//                                    if (null == friend) {
//                                        friend = new FriendEntry(10201101L, usersBean.getUsername(), usersBean.getNickname(),
//                                                usersBean.getNickname(), appkey, null, displayName, letter, user);
//                                        friend.save();
//                                        mList.add(friend);
//                                    }
//                                    forDelete.add(friend);
//                                }
//                                ActiveAndroid.setTransactionSuccessful();
//                            } finally {
//                                ActiveAndroid.endTransaction();
//                            }
//
//
//                        }
//                        //其他端删除好友后,登陆时把数据库中的也删掉
//                        List<FriendEntry> friends = JGApplication.getUserEntry().getFriends();
//                        friends.removeAll(forDelete);
//                        for (FriendEntry del : friends) {
//                            del.delete();
//                            mList.remove(del);
//                        }
//                        Collections.sort(mList, new PinyinComparator());//按拼音排序
//                        mAdapter = new StickyListAdapter(mContext, mList, false);
//                        mContactsView.setAdapter(mAdapter);
                    }
                }
            }
        });


        ///////////////////////////////////

        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        JSONObject object = new JSONObject();
        JSONObject obj1 = new JSONObject();

        try {
            object.put("method", "ENVIRONMENTAPI_IMADMINLIST");
            obj1.put("start", 0);
            obj1.put("count", 100);
            object.put("params", obj1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //  Log.i("sss",object.toString());

        RequestBody body = RequestBody.create(json, object.toString());
        Request request = new Request.Builder()//创建Request 对象。
                .url("http://121.41.52.56:3001/environmentalapi")
                .post(body)
                .build();
        Call call = client.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mContactsView.dismissLoadingHeader();
                        if (response.isSuccessful()) {//回调的方法执行在子线程。
                            String str = null;
                            try {
                                str = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Gson gson = new Gson();
                            FriendBean bean = gson.fromJson(str, FriendBean.class);
                      //      Log.i("zzz", str);
                            if (bean.isSuccess()) {
                                List<FriendBean.ResultBean.UsersBean> list = bean.getResult().getUsers();
                                list.addAll(mListUsersBean);
                                if (list != null && list.size() != 0) {
                                    mContactsView.dismissLine();
                                    ActiveAndroid.beginTransaction();

                                    try {
                                        for (int i = 0; i < list.size(); i++) {

                                            FriendBean.ResultBean.UsersBean usersBean = list.get(i);
                                            String displayName = usersBean.getUsername();
                                            String letter;
                                            if (!TextUtils.isEmpty(displayName.trim())) {
                                                ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(displayName);
                                                StringBuilder sb = new StringBuilder();
                                                if (tokens != null && tokens.size() > 0) {
                                                    for (HanziToPinyin.Token token : tokens) {
                                                        if (token.type == HanziToPinyin.Token.PINYIN) {
                                                            sb.append(token.target);
                                                        } else {
                                                            sb.append(token.source);
                                                        }
                                                    }
                                                }
                                                String sortString = sb.toString().substring(0, 1).toUpperCase();
                                                if (sortString.matches("[A-Z]")) {
                                                    letter = sortString.toUpperCase();
                                                } else {
                                                    letter = "#";
                                                }
                                            } else {
                                                letter = "#";
                                            }
                                            //避免重复请求时导致数据重复A
                                            FriendEntry friend = FriendEntry.getFriend(user, usersBean.getUsername(), appkey);
                                            if (null == friend) {
//                                    if (TextUtils.isEmpty(usersBean.getAvatar())) {
//                                        friend = new FriendEntry(userInfo.getUserID(), userInfo.getUserName(), userInfo.getNotename(),
//                                        userInfo.getNickname(), userInfo.getAppKey(),
//                                        null, displayName, letter, user);
                                                friend = new FriendEntry(10201101L, usersBean.getUsername(), usersBean.getNickname(),
                                                        usersBean.getNickname(), appkey,
                                                        null, displayName, letter, user);
//                                    } else {
//                                        friend = new FriendEntry(usersBean.getUsername(), usersBean.getUsername(),
//                                        usersBean.getUsername(), usersBean.getNickname(), userInfo.getAppKey(),
//                                                usersBean.getAvatarFile().getAbsolutePath(), displayName, letter, user);
//                                    }
                                                friend.save();
                                                mList.add(friend);
                                            }
                                            forDelete.add(friend);
                                        }
                                        ActiveAndroid.setTransactionSuccessful();
                                    } finally {
                                        ActiveAndroid.endTransaction();
                                    }


                                } else {
                                    mContactsView.showLine();
                                }
                                //其他端删除好友后,登陆时把数据库中的也删掉
                                List<FriendEntry> friends = JGApplication.getUserEntry().getFriends();
                                friends.removeAll(forDelete);
                                for (FriendEntry del : friends) {
                                    del.delete();
                                    mList.remove(del);
                                }
                                Collections.sort(mList, new PinyinComparator());//按拼音排序
                                mAdapter = new StickyListAdapter(mContext, mList, false);
                                mContactsView.setAdapter(mAdapter);
                            }
                        }
                    }
                });

            }
        });
    }


    @Override
    public void onTouchingLetterChanged(String s) {
        //该字母首次出现的位置
        if (null != mAdapter) {
            int position = mAdapter.getSectionForLetter(s);
            if (position != -1 && position < mAdapter.getCount()) {
                mContactsView.setSelection(position);
            }
        }
    }

    public void refresh(FriendEntry entry) {
        mList.add(entry);
        if (null == mAdapter) {
            mAdapter = new StickyListAdapter(mContext, mList, false);
        } else {
            Collections.sort(mList, new PinyinComparator());
        }
        mAdapter.notifyDataSetChanged();
    }

    public void refreshContact() {
        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                JMessageClient.getMyInfo().getAppKey());
        mList = user.getFriends();
        Collections.sort(mList, new PinyinComparator());
        mAdapter = new StickyListAdapter(mContext, mList, false);
        mContactsView.setAdapter(mAdapter);
    }

}
