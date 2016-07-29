package cn.ucai.fuliCenter.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ucai.fuliCenter.DemoHXSDKHelper;
import cn.ucai.fuliCenter.I;
import cn.ucai.fuliCenter.FuliCenterApplication;
import cn.ucai.fuliCenter.bean.Result;
import cn.ucai.fuliCenter.bean.UserAvatar;
import cn.ucai.fuliCenter.db.UserDao;
import cn.ucai.fuliCenter.task.DownContactListTask;
import cn.ucai.fuliCenter.task.DownGroupListTask;
import cn.ucai.fuliCenter.utils.OkHttpUtils2;
import cn.ucai.fuliCenter.utils.Utils;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.R;

/**
 * 开屏页
 */
public class SplashActivity extends BaseActivity {
    private static String TAG = SplashActivity.class.getSimpleName();
    private RelativeLayout rootLayout;
    private TextView versionText;

    private static final int sleepTime = 2000;

    @Override
    protected void onCreate(Bundle arg0) {
        setContentView(R.layout.activity_splash);
        super.onCreate(arg0);

        rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
        versionText = (TextView) findViewById(R.id.tv_version);

        versionText.setText(getVersion());
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        rootLayout.startAnimation(animation);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            public void run() {
                if (DemoHXSDKHelper.getInstance().isLogined()) {
                    // ** 免登陆情况 加载所有本地群和会话
                    //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                    //加上的话保证进了主页面会话和群组都已经load完毕
                    long start = System.currentTimeMillis();
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();

                    UserDao dao = new UserDao(SplashActivity.this);
                    String username = FuliCenterApplication.getInstance().getUserName();
                    Log.e("qqqq", "username:"+username);
                    UserAvatar userAvatar = dao.userAvatarInfo(username);
//                    Log.e("qqqq", "userAvatar:"+userAvatar.toString());
                    if (userAvatar == null) {
                        final OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
                        utils2.setRequestUrl(I.SERVER_ROOT)
                                .addParam(I.KEY_REQUEST, I.REQUEST_FIND_USER)
                                .addParam(I.User.USER_NAME, username)
                                .targetClass(String.class)
                                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        if (s != null) {
                                            Result result = Utils.getResultFromJson(s, UserAvatar.class);
                                            UserAvatar user = (UserAvatar) result.getRetData();
                                            if (user != null) {
                                                FuliCenterApplication.currentUserNick = user.getMUserNick();
                                                new DownContactListTask(user.getMUserName(), SplashActivity.this).execute();
                                                new DownGroupListTask(user.getMUserName(), SplashActivity.this).execute();
                                            }else{
                                                Log.e("qqqq", "没找到此用户");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {

                                    }
                                });
                    }else{
                        FuliCenterApplication.currentUserNick = userAvatar.getMUserNick();
                        new DownContactListTask(username, SplashActivity.this).execute();
                    }
                    long costTime = System.currentTimeMillis() - start;
                    //等待sleeptime时长
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //进入主页面
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).start();

    }

    /**
     * 获取当前应用程序的版本号
     */
    private String getVersion() {
        String st = getResources().getString(R.string.Version_number_is_wrong);
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
            String version = packinfo.versionName;
            return version;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return st;
        }
    }
}
