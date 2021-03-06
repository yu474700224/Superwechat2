/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.chatuidemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.ucai.chatuidemo.I;
import cn.ucai.chatuidemo.SuperWeChatApplication;
import cn.ucai.chatuidemo.bean.Result;
import cn.ucai.chatuidemo.bean.UserAvatar;
import cn.ucai.chatuidemo.domain.User;

import com.easemob.EMCallBack;

import cn.ucai.applib.controller.HXSDKHelper;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import cn.ucai.chatuidemo.Constant;
import cn.ucai.chatuidemo.DemoHXSDKHelper;

import com.easemob.chatuidemo.R;

import cn.ucai.chatuidemo.db.UserDao;
import cn.ucai.chatuidemo.task.DownContactListTask;
import cn.ucai.chatuidemo.task.DownGroupListTask;
import cn.ucai.chatuidemo.utils.CommonUtils;
import cn.ucai.chatuidemo.utils.OkHttpUtils2;
import cn.ucai.chatuidemo.utils.Utils;

/**
 * 登陆页面
     *
     */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    public static final int REQUEST_CODE_SETNICK = 1;
    private EditText usernameEditText;
    private EditText passwordEditText;

    private boolean progressShow;
    private boolean autoLogin = false;

    private String currentUsername;
    private String currentPassword;

    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 如果用户名密码都有，直接进入主页面
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

            return;
        }
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);

        // 如果用户名改变，清空密码
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditText.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (SuperWeChatApplication.getInstance().getUserName() != null) {
            usernameEditText.setText(SuperWeChatApplication.getInstance().getUserName());
        }
    }

    /**
     * 登录
     *
     * @param view
     */
    public void login(View view) {
        if (!CommonUtils.isNetWorkConnected(this)) {
            Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        currentUsername = usernameEditText.getText().toString().trim();
        currentPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(this, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(this, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        progressShow = true;
        pd = new ProgressDialog(LoginActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progressShow = false;
            }
        });
        pd.setMessage(getString(R.string.Is_landing));
        pd.show();

        final long start = System.currentTimeMillis();
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                if (!progressShow) {
                    return;
                }
               LoginAppServer();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                if (!progressShow) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void LoginAppServer() {
        OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
        utils2.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME,currentUsername)
                .addParam(I.User.PASSWORD,currentPassword)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if (s!=null) {
                            Result result = Utils.getResultFromJson(s, UserAvatar.class);
                            UserAvatar user = (UserAvatar) result.getRetData();
                            SuperWeChatApplication.currentUserNick = user.getMUserNick();
                            SavaUserToHXDB(user);
                            HXLoginSuccess(user);
                        }else{
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "登录失败",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        pd.dismiss();
                        DemoHXSDKHelper.getInstance().logout(true, null);
                        Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void SavaUserToHXDB(UserAvatar user) {
        if (user != null) {
            UserDao dao = new UserDao(LoginActivity.this);
            dao.saveUserToHXDB(user);
        }
    }

    private void HXLoginSuccess(UserAvatar user) {
        // 登陆成功，保存用户名密码
        SuperWeChatApplication.getInstance().setUserName(currentUsername);
        SuperWeChatApplication.getInstance().setPassword(currentPassword);
        SuperWeChatApplication.getInstance().setUser(user);
        SuperWeChatApplication.currentUserNick = user.getMUserNick();
        new DownContactListTask(currentUsername,LoginActivity.this).execute();
        new DownGroupListTask(currentUsername,LoginActivity.this).execute();
        try {
            // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
            // ** manually load all local groups and
            EMGroupManager.getInstance().loadAllGroups();
            EMChatManager.getInstance().loadAllConversations();
            // 处理好友和群组
            initializeContacts();
        } catch (Exception e) {
            e.printStackTrace();
            // 取好友或者群聊失败，不让进入主页面
            runOnUiThread(new Runnable() {
                public void run() {
                    pd.dismiss();
                    DemoHXSDKHelper.getInstance().logout(true, null);
                    Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
        boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                SuperWeChatApplication.currentUserNick.trim());
        if (!updatenick) {
            Log.e("LoginActivity", "update current user nick fail");
        }
        if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
            pd.dismiss();
        }
        // 进入主页面
        Intent intent = new Intent(LoginActivity.this,
                MainActivity.class);
        startActivity(intent);

        finish();
    }

    private void initializeContacts() {
        Map<String, User> userlist = new HashMap<String, User>();
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
//        User groupUser = new User();
//        String strGroup = getResources().getString(R.string.group_chat);
//        groupUser.setUsername(Constant.GROUP_USERNAME);
//        groupUser.setNick(strGroup);
//        groupUser.setHeader("");
//        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
//        User robotUser = new User();
//        String strRobot = getResources().getString(R.string.robot_chat);
//        robotUser.setUsername(Constant.CHAT_ROBOT);
//        robotUser.setNick(strRobot);
//        robotUser.setHeader("");
//        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(LoginActivity.this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);
    }

    /**
     * 注册
     *
     * @param view
     */
    public void register(View view) {
        startActivityForResult(new Intent(this, RegisterActivity.class), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }
}
