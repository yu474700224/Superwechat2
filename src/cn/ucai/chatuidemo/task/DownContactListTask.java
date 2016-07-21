package cn.ucai.chatuidemo.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import cn.ucai.chatuidemo.I;
import cn.ucai.chatuidemo.SuperWeChatApplication;
import cn.ucai.chatuidemo.bean.Result;
import cn.ucai.chatuidemo.bean.UserAvatar;
import cn.ucai.chatuidemo.utils.OkHttpUtils2;
import cn.ucai.chatuidemo.utils.Utils;

/**
 * Created by Administrator on 2016/7/21.
 */
public class DownContactListTask {
    private String username;
    private Context context;

    public DownContactListTask(String username, Context context) {
        this.username = username;
        this.context = context;
    }

    public void execute(){
        final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
        utils2.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Result result = Utils.getListResultFromJson(s, UserAvatar.class);
                        List<UserAvatar> list = (List<UserAvatar>) result.getRetData();
                        Log.e("qqq", "list:" + list.toString());
                        if (list != null && list.size() > 0) {
                            Log.e("qqq", "list.size():" + list.size());
                            SuperWeChatApplication.getInstance().setUserAvatarList(list);
                            context.sendStickyBroadcast(new Intent("update_contact_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

}
