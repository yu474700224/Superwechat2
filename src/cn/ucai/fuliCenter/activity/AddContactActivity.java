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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.ucai.fuliCenter.I;
import cn.ucai.fuliCenter.FuliCenterApplication;
import cn.ucai.applib.controller.HXSDKHelper;

import com.easemob.chat.EMContactManager;

import cn.ucai.fuliCenter.DemoHXSDKHelper;
import cn.ucai.fuliCenter.bean.Result;
import cn.ucai.fuliCenter.bean.UserAvatar;
import cn.ucai.fuliCenter.utils.OkHttpUtils2;
import cn.ucai.fuliCenter.utils.UserUtils;
import cn.ucai.fuliCenter.utils.Utils;

import com.easemob.chatuidemo.R;

public class AddContactActivity extends BaseActivity {
    private EditText editText;
    private LinearLayout searchedUserLayout;
    private TextView nameText, mTextView;
    private Button searchBtn;
    private ImageView avatar;
    private InputMethodManager inputMethodManager;
    private String toAddUsername;
    private ProgressDialog progressDialog;
    private TextView tvNoting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        mTextView = (TextView) findViewById(R.id.add_list_friends);

        editText = (EditText) findViewById(R.id.edit_note);
        String strAdd = getResources().getString(R.string.add_friend);
        mTextView.setText(strAdd);
        String strUserName = getResources().getString(R.string.user_name);
        editText.setHint(strUserName);
        searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
        nameText = (TextView) findViewById(R.id.name);
        searchBtn = (Button) findViewById(R.id.search);
        avatar = (ImageView) findViewById(R.id.avatar);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        tvNoting = (TextView) findViewById(R.id.noThing);
    }


    /**
     * 查找contact
     * @param v
     */
    public void searchContact(View v) {
        final String name = editText.getText().toString();
        String saveText = searchBtn.getText().toString();

        if (getString(R.string.button_search).equals(saveText)) {
            toAddUsername = name;
            if (TextUtils.isEmpty(name)) {
                String st = getResources().getString(R.string.Please_enter_a_username);
                startActivity(new Intent(this, AlertDialog.class).putExtra("msg", st));
                return;
            }
            if (FuliCenterApplication.getInstance().getUserName().equals(toAddUsername)) {
                String str = getString(R.string.not_add_myself);
                startActivity(new Intent(this, AlertDialog.class).putExtra("msg", str));
                return;
            }
            UserAvatar userAvatar = FuliCenterApplication.getInstance().getUserAvatarMap().get(toAddUsername);
            if (userAvatar != null) {
                startActivity(new Intent(AddContactActivity.this, UserProfileActivity.class).putExtra("username", toAddUsername));
                return;
            }
            final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
            utils2.setRequestUrl(I.SERVER_ROOT)
                    .addParam(I.KEY_REQUEST, I.REQUEST_FIND_USER)
                    .addParam(I.User.USER_NAME, toAddUsername)
                    .targetClass(String.class)
                    .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            if (s != null) {
                                Result result = Utils.getResultFromJson(s, UserAvatar.class);
                                UserAvatar user = (UserAvatar) result.getRetData();
                                // TODO 从服务器获取此contact,如果不存在提示不存在此用户
                                searchedUserLayout.setVisibility(View.VISIBLE);
                                nameText.setText(toAddUsername);
                                tvNoting.setVisibility(View.GONE);
                                UserUtils.setAppUserAvatar(AddContactActivity.this,toAddUsername,avatar);
                            } else {
                                searchedUserLayout.setVisibility(View.VISIBLE);
                                tvNoting.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            searchedUserLayout.setVisibility(View.GONE);
                            tvNoting.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    /**
     *  添加contact
     * @param view
     */
    public void addContact(View view) {

        if (((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().containsKey(nameText.getText().toString())) {
            //提示已在好友列表中，无需添加
            if (EMContactManager.getInstance().getBlackListUsernames().contains(nameText.getText().toString())) {
                startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可"));
                return;
            }
            String strin = getString(R.string.This_user_is_already_your_friend);
            startActivity(new Intent(this, AlertDialog.class).putExtra("msg", strin));
            return;
        }

        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo写死了个reason，实际应该让用户手动填入
                    String s = getResources().getString(R.string.Add_a_friend);
                    EMContactManager.getInstance().addContact(toAddUsername, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View v) {
        finish();
    }
}
