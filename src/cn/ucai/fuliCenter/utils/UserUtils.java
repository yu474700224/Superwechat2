package cn.ucai.fuliCenter.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.applib.controller.HXSDKHelper;
import cn.ucai.fuliCenter.DemoHXSDKHelper;
import cn.ucai.fuliCenter.I;
import cn.ucai.fuliCenter.SuperWeChatApplication;
import cn.ucai.fuliCenter.bean.MemberUserAvatar;
import cn.ucai.fuliCenter.bean.UserAvatar;
import cn.ucai.fuliCenter.domain.User;

import com.easemob.chatuidemo.R;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     *
     * @param username
     * @return
     */

    private static String TAG = UserUtils.class.getSimpleName();
    public static User getUserInfo(String username) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
        if (user == null) {
            user = new User(username);
        }

        if (user != null) {
            //demo没有这些数据，临时填充
            if (TextUtils.isEmpty(user.getNick()))
                user.setNick(username);
        }
        return user;
    }

    public static UserAvatar getAppUserInfo(String username) {
        UserAvatar user = SuperWeChatApplication.getInstance().getUserAvatarMap().get(username);
        if (user == null) {
            user = new UserAvatar();
        }

        return user;
    }

    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        User user = getUserInfo(username);
        if (user != null && user.getAvatar() != null) {
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }



    /**
     * 设置当前用户头像
     */
    public static void setCurrentUserAvatar(Context context, ImageView imageView) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        if (user != null && user.getAvatar() != null) {
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username, TextView textView) {
        User user = getUserInfo(username);
        if (user != null) {
            textView.setText(user.getNick());
        } else {
            textView.setText(username);
        }
    }


    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
        if (textView != null) {
            textView.setText(user.getNick());
        }
    }

    /**
     * 保存或更新某个用户
     *
     * @param newUser
     */
    public static void saveUserInfo(User newUser) {
        if (newUser == null || newUser.getUsername() == null) {
            return;
        }
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
    }

//-------------------------------------------------------------------------------------------------------------
    /**
     * 仿写：设置好友头像
     *
     * @param username
     */
    public static void setAppUserAvatar(Context context, String username, ImageView imageView) {
        String path = "";
        if (path != null && username != null) {
            path = getUserAvatarPath(username).toString();
            Log.e("qqqq", path);
            Picasso.with(context).load(path).placeholder(R.drawable.default_avatar).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

    //从服务器上获取好友头像
    private static StringBuilder getUserAvatarPath(String username) {
        StringBuilder path = new StringBuilder(I.SERVER_ROOT);
        path.append(I.QUESTION).append(I.KEY_REQUEST).append(I.EQUAL).append(I.REQUEST_DOWNLOAD_AVATAR)
                .append(I.ADD).append(I.NAME_OR_HXID).append(I.EQUAL).append(username)
                .append(I.ADD).append(I.AVATAR_TYPE).append(I.EQUAL).append(I.AVATAR_TYPE_USER_PATH);
        return path;
    }
    //------------
    /**
     * 设置当前用户昵称
     */
    public static void setAppCurrentUserNick(TextView textView) {
        final String userName = SuperWeChatApplication.getInstance().getUserName();
        final User user = UserUtils.getUserInfo(userName);
        if (user != null) {
            if (user.getNick() != null) {
                textView.setText(user.getNick());
            } else {
                textView.setText(user.getUsername());
            }
        } else {
            textView.setText(user.getUsername());
        }
    }

    /**
     * 仿写：设置查询好友昵称
     */
    public static void setContactUserNick(String username, TextView textView) {
        final UserAvatar userAvatar = SuperWeChatApplication.getInstance().getUserAvatarMap().get(username);

        if (userAvatar != null) {
            textView.setText(userAvatar.getMUserNick());
        } else {
            textView.setText(username);
        }
    }

    /**
     * 仿写：设置当前用户头像
     */
    public static void setAppCurrentUserAvatar(Context context, ImageView imageView) {
        final String userName = SuperWeChatApplication.getInstance().getUserName();
        UserUtils.setAppUserAvatar(context, userName, imageView);
    }
    /**
     * 仿写：设置用户头像
     */
    public static void setAppCurrentUserAvatar(Context context, String username,ImageView imageView) {
        UserUtils.setAppUserAvatar(context, username, imageView);
    }

    /**
     * 仿写：设置查询好友昵称
     */
    public static void setAppAddContactUserNick(UserAvatar userAvatar, TextView textView) {
        if (userAvatar != null) {
            textView.setText(userAvatar.getMUserNick());
        } else {
            textView.setText(userAvatar.getMUserName());
        }
    }

    /**
     * 设置好友关系昵称
     */
    public static void setAppUserNick(String username, TextView textView) {
        UserAvatar user = getAppUserInfo(username);
        if (user != null) {
            if (user.getMUserNick() != null) {
                textView.setText(user.getMUserNick());
            } else {
                textView.setText(username);
            }
        } else {
            textView.setText(username);
        }
    }

    /**
     * 仿写：设置群组头像
     *
     * @param hxid
     */
    public static void setGroupUserAvatar(Context context, String hxid, ImageView imageView) {
        String path = "";
        if (path != null && hxid != null) {
            path = getGroupAvatarPath(hxid).toString();
            Log.e("qqqq", path);
            Picasso.with(context).load(path).placeholder(R.drawable.group_icon).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.group_icon).into(imageView);
        }
    }

    //从服务器上获取好友头像
    private static StringBuilder getGroupAvatarPath(String hxid) {
        StringBuilder path = new StringBuilder(I.SERVER_ROOT);
        path.append(I.QUESTION).append(I.KEY_REQUEST).append(I.EQUAL).append(I.REQUEST_DOWNLOAD_AVATAR)
                .append(I.ADD).append(I.NAME_OR_HXID).append(I.EQUAL).append(hxid)
                .append(I.ADD).append(I.AVATAR_TYPE).append(I.EQUAL).append(I.AVATAR_TYPE_GROUP_PATH);
        return path;
    }

    //从全局变量中获取群组信息
    @Nullable
    private static MemberUserAvatar getAppMemberInfo(String hxId, String username) {
        MemberUserAvatar memberUserAvatar = null;
        Map<String, MemberUserAvatar> members = SuperWeChatApplication.getInstance().getMemberAvatarMap().get(hxId);
        if (members == null || members.size() < 0) {
            return null;
        }else{
            memberUserAvatar = members.get(username);
        }
        return memberUserAvatar;
    }

    public static void setAppMemberNick(String hxid, String username, TextView textView) {
        final MemberUserAvatar memberInfo = getAppMemberInfo(hxid, username);
        Log.e(TAG, "memberInfo:" + memberInfo);
        if (memberInfo != null && memberInfo.getMUserNick() != null) {
            textView.setText(memberInfo.getMUserNick());
        }else{
            textView.setText(memberInfo.getMUserName());
        }
    }
}
