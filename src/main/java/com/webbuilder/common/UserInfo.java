package com.webbuilder.common;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.webbuilder.utils.LogUtil;

public class UserInfo implements HttpSessionBindingListener,
		java.io.Serializable {
	private static final long serialVersionUID = 4490661288665900556L;
	public String ip;
	public String userId;
	public String userName;

	public void valueBound(HttpSessionBindingEvent event) {
		if (canLog())
			LogUtil.log(userName, ip, 1, "login");
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		if (canLog())
			LogUtil.log(userName, ip, 1, "logout");
		Session.sessionList.remove(userId);
	}

	private boolean canLog() {
		try {
			return Var.getBool("webbuilder.session.loginLog");
		} catch (Throwable e) {
			return false;
		}
	}
}
