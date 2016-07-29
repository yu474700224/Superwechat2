package cn.ucai.fuliCenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.fuliCenter.I;
import cn.ucai.fuliCenter.FuliCenterApplication;
import cn.ucai.fuliCenter.bean.Result;
import cn.ucai.fuliCenter.bean.UserAvatar;
import cn.ucai.fuliCenter.utils.OkHttpUtils2;
import cn.ucai.fuliCenter.utils.Utils;

/**
 * Created by Administrator on 2016/7/21.
 */
public class DownContactListTask {
    private static String TAG = DownContactListTask.class.getSimpleName();
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
                        Log.e(TAG, "list:" + list.toString());
                        if (list != null && list.size() > 0) {
                            Log.e(TAG, "list.size():" + list.size());
                            FuliCenterApplication.getInstance().setUserAvatarList(list);
                            context.sendStickyBroadcast(new Intent("update_contact_list"));
                            final Map<String, UserAvatar> userAvatarMap = FuliCenterApplication.getInstance().getUserAvatarMap();
                            for (UserAvatar u : list) {
                                userAvatarMap.put(u.getMUserName(), u);
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

}
