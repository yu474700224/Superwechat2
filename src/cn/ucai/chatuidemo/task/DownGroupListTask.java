package cn.ucai.chatuidemo.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.chatuidemo.I;
import cn.ucai.chatuidemo.SuperWeChatApplication;
import cn.ucai.chatuidemo.bean.GroupAvatar;
import cn.ucai.chatuidemo.bean.Result;
import cn.ucai.chatuidemo.bean.UserAvatar;
import cn.ucai.chatuidemo.utils.OkHttpUtils2;
import cn.ucai.chatuidemo.utils.Utils;

/**
 * Created by Administrator on 2016/7/21.
 */
public class DownGroupListTask {
    private static String TAG = DownGroupListTask.class.getSimpleName();
    private String username;
    private Context context;

    public DownGroupListTask(String username, Context context) {
        this.username = username;
        this.context = context;
    }

    public void execute(){
        final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
        utils2.setRequestUrl(I.REQUEST_FIND_GROUP_BY_USER_NAME)
                .addParam(I.User.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Result result = Utils.getListResultFromJson(s, UserAvatar.class);
                        List<GroupAvatar> list = (List<GroupAvatar>) result.getRetData();
                        Log.e(TAG, "list:" + list.toString());
                        if (list != null && list.size() > 0) {
                            Log.e(TAG, "list.size():" + list.size());
                            SuperWeChatApplication.getInstance().setGroupAvatarList(list);
                            context.sendStickyBroadcast(new Intent("update_group_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

}
