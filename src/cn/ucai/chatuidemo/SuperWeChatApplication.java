/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.chatuidemo;

import android.app.Application;
import android.content.Context;

import com.easemob.EMCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.chatuidemo.bean.GroupAvatar;
import cn.ucai.chatuidemo.bean.MemberUserAvatar;
import cn.ucai.chatuidemo.bean.UserAvatar;

public class SuperWeChatApplication extends Application {

	public static Context applicationContext;
	private static SuperWeChatApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";
	
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();

	@Override
	public void onCreate() {
		super.onCreate();
        applicationContext = this;
        instance = this;

        /**
         * this function will initialize the HuanXin SDK
         * 
         * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
         * 
         * 环信初始化SDK帮助函数
         * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
         * 
         * for example:
         * 例子：
         * 
         * public class DemoHXSDKHelper extends HXSDKHelper
         * 
         * HXHelper = new DemoHXSDKHelper();
         * if(HXHelper.onInit(context)){
         *     // do HuanXin related work
         * }
         */
        hxSDKHelper.onInit(applicationContext);
	}

	public static SuperWeChatApplication getInstance() {
		return instance;
	}
 

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param username
	 */
	public void setUserName(String username) {
	    hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
	    hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final boolean isGCM,final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
	    hxSDKHelper.logout(isGCM,emCallBack);
	}

	private UserAvatar user;

	List<UserAvatar> userAvatarList = new ArrayList<UserAvatar>() {
	};

	Map<String, UserAvatar> userAvatarMap = new HashMap<String, UserAvatar>();

	List<GroupAvatar> groupAvatarList = new ArrayList<GroupAvatar>();

	private Map<String, Map<String, MemberUserAvatar>> MemberAvatarMap = new HashMap<String, Map<String, MemberUserAvatar>>();

	public List<GroupAvatar> getGroupAvatarList() {
		return groupAvatarList;
	}

	public void setGroupAvatarList(List<GroupAvatar> groupAvatarList) {
		this.groupAvatarList = groupAvatarList;
	}

	public UserAvatar getUser() {
		return user;
	}

	public void setUser(UserAvatar user) {
		this.user = user;
	}

	public List<UserAvatar> getUserAvatarList() {
		return userAvatarList;
	}

	public void setUserAvatarList(List<UserAvatar> userAvatarList) {
		this.userAvatarList = userAvatarList;
	}

	public Map<String, UserAvatar> getUserAvatarMap() {
		return userAvatarMap;
	}

	public void setUserAvatarMap(Map<String, UserAvatar> userAvatarMap) {
		this.userAvatarMap = userAvatarMap;
	}


	public Map<String, Map<String, MemberUserAvatar>> getMemberAvatarMap() {
		return MemberAvatarMap;
	}

	public void setMemberAvatarMap(Map<String, Map<String, MemberUserAvatar>> memberAvatarMap) {
		MemberAvatarMap = memberAvatarMap;
	}

}
