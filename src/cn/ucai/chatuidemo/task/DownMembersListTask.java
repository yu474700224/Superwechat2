package cn.ucai.chatuidemo.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.chatuidemo.I;
import cn.ucai.chatuidemo.SuperWeChatApplication;
import cn.ucai.chatuidemo.bean.MemberUserAvatar;
import cn.ucai.chatuidemo.bean.Result;
import cn.ucai.chatuidemo.bean.UserAvatar;
import cn.ucai.chatuidemo.utils.OkHttpUtils2;
import cn.ucai.chatuidemo.utils.Utils;

/**
 * Created by Administrator on 2016/7/21.
 */
public class DownMembersListTask {
    private static String TAG = DownMembersListTask.class.getSimpleName();
    private String hxid;
    private Context context;

    public DownMembersListTask(String hxid, Context context) {
        this.hxid = hxid;
        this.context = context;
    }

    public void execute(){
        final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
        utils2.setRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID)
                .addParam(I.Member.GROUP_HX_ID, hxid)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s:" + s);
                        Result result = Utils.getListResultFromJson(s, MemberUserAvatar.class);
                        Log.e(TAG, "result:" + result);
                        List<MemberUserAvatar> list = (List<MemberUserAvatar>) result.getRetData();
                        Log.e(TAG, "list:" + list.toString());
                        if (list != null && list.size() > 0) {
                            Log.e(TAG, "list.size():" + list.size());
                             Map<String, Map<String, MemberUserAvatar>> memberAvatarMap =
                                    SuperWeChatApplication.getInstance().getMemberAvatarMap();
                            if (!memberAvatarMap.containsKey(hxid)) {
                                memberAvatarMap.put(hxid, new HashMap<String, MemberUserAvatar>());
                            }
                            Map<String, MemberUserAvatar> MemberMap = memberAvatarMap.get(hxid);
                            for (MemberUserAvatar u : list) {
                                Log.e(TAG, u.toString());
                                MemberMap.put(u.getMUserName(), u);
                            }
                            context.sendStickyBroadcast(new Intent("update_member_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

}
