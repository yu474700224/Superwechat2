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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.R;
import com.easemob.exceptions.EaseMobException;

import java.io.File;

import cn.ucai.chatuidemo.I;
import cn.ucai.chatuidemo.SuperWeChatApplication;
import cn.ucai.chatuidemo.adapter.GroupAdapter;
import cn.ucai.chatuidemo.bean.GroupAvatar;
import cn.ucai.chatuidemo.bean.Result;
import cn.ucai.chatuidemo.listener.OnSetAvatarListener;
import cn.ucai.chatuidemo.utils.OkHttpUtils2;
import cn.ucai.chatuidemo.utils.Utils;

public class NewGroupActivity extends BaseActivity {
    private static String TAG = NewGroupActivity.class.getSimpleName();
    private EditText groupNameEditText;
    private ProgressDialog progressDialog;
    private EditText introductionEditText;
    private CheckBox checkBox;
    private CheckBox memberCheckbox;
    private LinearLayout openInviteContainer;

    private ImageView avatar;
    private OnSetAvatarListener mOnSetAvatarListener;
    private String mAvatarName;
    private static int CREATE_GROUP = 100;
    String st2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
        introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
        checkBox = (CheckBox) findViewById(R.id.cb_public);
        memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
        openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);

        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openInviteContainer.setVisibility(View.INVISIBLE);
                } else {
                    openInviteContainer.setVisibility(View.VISIBLE);
                }
            }
        });

        avatar = (ImageView) findViewById(R.id.iv_register_avatar);
        findViewById(R.id.layout_NewGroup_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSetAvatarListener = new OnSetAvatarListener(NewGroupActivity.this, R.id.layout_New_group, getAvatarName(), I.AVATAR_TYPE_GROUP_PATH);
            }


        });
    }

    private String getAvatarName() {
        mAvatarName = String.valueOf(SystemClock.currentThreadTimeMillis());
        return mAvatarName;
    }

    /**
     * @param v
     */
    public void save(View v) {
        String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
        String name = groupNameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Intent intent = new Intent(this, AlertDialog.class);
            intent.putExtra("msg", str6);
            startActivity(intent);
        } else {
            // 进通讯录选人
            startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), CREATE_GROUP);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mOnSetAvatarListener.setAvatar(requestCode, data, avatar);
        Log.e(TAG, "CREATE_GROUP:" + CREATE_GROUP);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == OnSetAvatarListener.REQUEST_CROP_PHOTO) {

        }
        if (requestCode == CREATE_GROUP) {
            createNewGroup(data);
        }
    }


    private void createNewGroup(final Intent data) {
        st2 = getResources().getString(R.string.Failed_to_create_groups);
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        //新建群组
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(st1);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 调用sdk创建群组方法
                String groupName = groupNameEditText.getText().toString().trim();
                String desc = introductionEditText.getText().toString();
                String[] members = data.getStringArrayExtra("newmembers");
                EMGroup group;
                try {
                    if (checkBox.isChecked()) {
                        //创建公开群，此种方式创建的群，可以自由加入
                        //创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
                        group = EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true, 200);
                    } else {
                        //创建不公开群
                        group = EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(), 200);
                    }
                    Log.e(TAG, "HXID=" + group.getGroupId());
                    createAppGroup(group.getGroupId(), groupName, desc, members);

                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void createAppGroup(final String groupId, String groupName, String desc, final String[] members) {
        File file = new File(OnSetAvatarListener.getAvatarPath(NewGroupActivity.this, I.AVATAR_TYPE_GROUP_PATH),
                mAvatarName + I.AVATAR_SUFFIX_JPG);
        Log.e(TAG, "filePath:" + file.getAbsolutePath());
        String userName = SuperWeChatApplication.getInstance().getUserName();
        boolean isPublic = memberCheckbox.isChecked();
        boolean invites = !isPublic;
        final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
        utils2.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID, groupId)
                .addParam(I.Group.NAME, groupName)
                .addParam(I.Group.DESCRIPTION, desc)
                .addParam(I.Group.OWNER, userName)
                .addParam(I.Group.IS_PUBLIC, String.valueOf(isPublic))
                .addParam(I.Group.ALLOW_INVITES, String.valueOf(invites))
                .targetClass(String.class)
                .addFile(file)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s:" + s);
                        final Result result = Utils.getResultFromJson(s, GroupAvatar.class);
                        Log.e(TAG, "result:" + result);
                        GroupAvatar groupAvatar = (GroupAvatar) result.getRetData();
                        if (result != null && result.isRetMsg()) {
                            if (members != null && members.length > 0) {
                                addGroupMember(groupId, members);
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "erroe:" + error);
                        Toast.makeText(NewGroupActivity.this, st2 + error, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void addGroupMember(String groupId, String[] members) {
        String memeberArr ="";
        for (String m : members) {
            memeberArr += m + ",";
        }
        Log.e(TAG, memeberArr.toString());
        memeberArr = memeberArr.substring(0, memeberArr.length() - 1);
        final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
        utils2.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
                .addParam(I.Member.GROUP_HX_ID, groupId)
                .addParam(I.Member.USER_NAME,memeberArr)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s:" + s);
                        final Result result = Utils.getResultFromJson(s, GroupAvatar.class);
                        Log.e(TAG, "result:" + result);
                        GroupAvatar groupAvatar = (GroupAvatar) result.getRetData();
                        if (result != null && result.isRetMsg()) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    progressDialog.dismiss();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                        } else {
                            Toast.makeText(NewGroupActivity.this, st2 , Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "erroe:" + error);
                        Toast.makeText(NewGroupActivity.this, st2 + error, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }


    public void back(View view) {
        finish();
    }
}
