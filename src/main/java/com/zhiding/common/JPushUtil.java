package com.zhiding.common;

import java.util.HashMap;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;

public class JPushUtil {
	
	/**
	 * 向所有用户推送消息
	 * @param appKey
	 * @param masterSecret
	 * @param is_quiet
	 * @param content
	 * @return
	 */
	public static boolean sendMessageAll(String appKey,String masterSecret,boolean is_quiet,String content){
		
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, 3);
		PushResult pushResult = null;
		try {
			if(is_quiet){	
				pushResult = jpushClient.sendMessageAll(content);
			}else{
				pushResult = jpushClient.sendNotificationAll(content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pushResult.isResultOK();
	}
	
	/**
	 * 推送消息至安卓手机
	 * @param appKey
	 * @param masterSecret
	 * @param is_quiet
	 * @param title
	 * @param content
	 * @param reg_ids
	 * @return
	 */
	public static boolean sendMessageToAnd(String appKey,String masterSecret,boolean is_quiet,String title,String content,String...reg_ids){
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, 3);
		PushResult pushResult = null;
		
		try {
			if(is_quiet){
				pushResult = jpushClient.sendAndroidMessageWithRegistrationID(title, content, reg_ids);
			}else{
				pushResult = jpushClient.sendAndroidNotificationWithRegistrationID(title, content, new HashMap<String,String>(), reg_ids);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pushResult.isResultOK();
	}
	
	/**
	 * 向IOS推送消息
	 * @param appKey
	 * @param masterSecret
	 * @param is_quiet
	 * @param title
	 * @param content
	 * @param reg_ids
	 * @return
	 */
	public static boolean sendMessageToIos(String appKey,String masterSecret,boolean is_quiet,String title,String content,String...reg_ids) {
		JPushClient jpushClient = new JPushClient(masterSecret, appKey, 3);
		PushResult pushResult = null;
		
		try {
			if(is_quiet){
				pushResult = jpushClient.sendIosMessageWithRegistrationID(title, content, reg_ids);
			}else{
				pushResult = jpushClient.sendIosNotificationWithRegistrationID(content, new HashMap<String,String>(), reg_ids);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pushResult.isResultOK();
	}
}
