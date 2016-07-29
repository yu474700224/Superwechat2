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
package cn.ucai.fuliCenter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.ucai.fuliCenter.I;
import cn.ucai.fuliCenter.FuliCenterApplication;
import cn.ucai.fuliCenter.bean.Result;
import cn.ucai.fuliCenter.listener.OnSetAvatarListener;
import cn.ucai.fuliCenter.utils.OkHttpUtils2;
import cn.ucai.fuliCenter.utils.Utils;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.R;
import com.easemob.exceptions.EaseMobException;

import java.io.File;

/**
 * 注册页
 */
public class RegisterActivity extends BaseActivity {
    private EditText userNameEditText;
    private EditText userNickEditText;
    private EditText passwordEditText;
    private EditText confirmPwdEditText;

    private ImageView mivAvatar;
    private RelativeLayout mrlOnClickAvatar;

    OnSetAvatarListener mOnSetAvatarListener;

    String username;
    String usernick;
    String pwd;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        setListener();
    }

    private void setListener() {
        findViewById(R.id.btn_back_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(v);
            }
        });
        findViewById(R.id.layout_register_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSetAvatarListener = new OnSetAvatarListener(RegisterActivity.this,
                        R.id.layout_register,
                        userNameEditText.getText().toString().trim(),
                        I.AVATAR_TYPE_USER_PATH);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mOnSetAvatarListener.setAvatar(requestCode, data, mivAvatar);
    }

    private void initView() {
        userNameEditText = (EditText) findViewById(R.id.username);
        userNickEditText = (EditText) findViewById(R.id.usernick);
        passwordEditText = (EditText) findViewById(R.id.password);
        confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
        mivAvatar = (ImageView) findViewById(R.id.iv_register_avatar);
        mrlOnClickAvatar = (RelativeLayout) findViewById(R.id.layout_register_avatar);
    }

    /**
     * 注册
     *
     * @param view
     */
    public void register(View view) {
        username = userNameEditText.getText().toString().trim();
        usernick = userNickEditText.getText().toString().trim();
        pwd = passwordEditText.getText().toString().trim();
        String confirm_pwd = confirmPwdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return;
        } else if (!username.matches("[\\w][\\w\\d]+")) {
            Toast.makeText(this, getResources().getString(R.string.User_name_style_error), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(usernick)) {
            Toast.makeText(this, getResources().getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
            userNickEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            confirmPwdEditText.requestFocus();
            return;
        } else if (!pwd.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            pd = new ProgressDialog(this);
            pd.setMessage(getResources().getString(R.string.Is_the_registered));
            pd.show();

            RegisterForServerlt();

        }
    }

    private void RegisterForServerlt() {
        //拿到头像数据
        File file = new File(OnSetAvatarListener.getAvatarPath(RegisterActivity.this, I.AVATAR_TYPE_USER_PATH),
                username + I.AVATAR_SUFFIX_JPG);
        OkHttpUtils2<Result> utils2 = new OkHttpUtils2<Result>();
        utils2.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME, username)
                .addParam(I.User.PASSWORD, pwd)
                .addParam(I.User.NICK, usernick)
                .addFile(file)
                .targetClass(Result.class)
                .execute(new OkHttpUtils2.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (result.isRetMsg()) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    R.string.login_failure_failed+ Utils.getResourceString(RegisterActivity.this,result.getRetCode()),
                                    Toast.LENGTH_LONG).show();
                            RegisterForHX();
                        }else{
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        pd.dismiss();
                    }
                });

    }
    public void back(View view) {
        finish();
    }

    public void RegisterForHX() {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    EMChatManager.getInstance().createAccountOnServer(username, pwd);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            // 保存用户名
                            FuliCenterApplication.getInstance().setUserName(username);
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (!RegisterActivity.this.isFinishing())
                                pd.dismiss();
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NONETWORK_ERROR) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.UNAUTHORIZED) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            unRegisterAppServer();
                        }
                    });
                }
            }
        }).start();

    }

    private void unRegisterAppServer(){
        final OkHttpUtils2<Result> utils2 = new OkHttpUtils2<Result>();
        utils2.setRequestUrl(I.REQUEST_UNREGISTER)
                .addParam(I.User.USER_NAME,username)
                .targetClass(Result.class)
                .execute(new OkHttpUtils2.OnCompleteListener<Result>() {
                    @Override
                    public void onSuccess(Result result) {

                    }

                    @Override
                    public void onError(String error) {
                        Log.i("qqq", "删除server失败");
                    }
                });

    }


}
