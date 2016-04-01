package com.webbuilder.common;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.webbuilder.interact.Install;
import com.webbuilder.tool.Encrypter;
import com.webbuilder.utils.DbUtil;
import com.webbuilder.utils.EncryptHelper;
import com.webbuilder.utils.StringUtil;
import com.webbuilder.utils.WebUtil;

public class Session {
	public static final ConcurrentHashMap<String, HttpSession> sessionList = new ConcurrentHashMap<String, HttpSession>();

	public static void verify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String setRef = request.getParameter("xwlhref");
		String referer = request.getHeader("Referer"), userInfo[];
		int userCt;

		if (!StringUtil.isEmpty(setRef))
			referer = setRef;
		if (StringUtil.isEmpty(referer)
				|| referer.indexOf("main?xwl=login") != -1
				|| referer.indexOf("main?xwl=tlogin") != -1)
			referer = "main?xwl=index";
		if (Var.getBool("webbuilder.session.loginVerify"))
			checkVerifyCode(request);
		userCt = Install.getUserCount();
		userCt = 10;
		if (userCt != -1 && sessionList.size() > userCt - 1)
			if (userCt == Var.getInt("webbuilder.allowsUesr"))
				throw new Exception("The version only allows "+Var.getInt("webbuilder.allowsUesr")+" active users.");
			else
				throw new Exception("The license only allows " + userCt
						+ " active users.");
		userInfo = checkUser(request);
		createSession(request, userInfo);
		WebUtil.response(response, referer);
	}
	
	/**
	 * 单点登录验证
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static boolean casverify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//获取cas cookie
		Cookie[] cookies = request.getCookies();
        String username = "";
        String password = "";
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie c = cookies[i];
                if (c.getName().equalsIgnoreCase("encUsername")) {
                    username = c.getValue();
                } else if (c.getName().equalsIgnoreCase("encPassword")) {
                    password = c.getValue();
                }
            }
        }else{
        	return false;
        }
        if(StringUtil.isEmpty(username)||StringUtil.isEmpty(password)){
        	return false;
        }
		String[] userInfo = new String[5];
		ResultSet rs = DbUtil
				.query(
						request,
						"select USER_ID,USER_NAME,DISPLAY_NAME,PASSWORD,USE_LANG,SSO_ID from WB_USER where USER_NAME='"+username+"' and STATUS=1");
		String truePwd = "";
		if (!rs.next()){
			throw new Exception(Str.format(request, "userNotExist", username));
		}else {
			userInfo[0] = rs.getString(1);
			userInfo[1] = rs.getString(2);
			userInfo[2] = rs.getString(3);
			truePwd = rs.getString(4);
			if(StringUtil.isEmpty(rs.getString(5))){
				userInfo[3]="zh_CN";
			}else{
				userInfo[3] = rs.getString(5);
			}
			userInfo[4] = rs.getString(6);
		}
		if (!StringUtil.isEqual(Encrypter.getMD5(password), truePwd))
			//绩效系统的加密算法验证
			if (!StringUtil.isEqual(EncryptHelper.getInstance().encrypt(password), truePwd))
				throw new Exception(Str.format(request, "invalidPwd"));
		if(userInfo.length >0){
			createSession(request, userInfo);
		}else{
			return false;
		}
		return true;
	}

	public static void logout(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null)
			session.invalidate();
		//清理cas cookie
		Cookie[] cookies = request.getCookies();
		try {
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					String cookieName = cookies[i].getName();
					if (cookieName.equals("encUsername")
							|| cookieName.equals("encPassword")) {
						cookies[i].setValue(null);
						cookies[i].setPath("/");
						cookies[i].setMaxAge(0);
						response.addCookie(cookies[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void checkVerifyCode(HttpServletRequest request)
			throws Exception {
		HttpSession session = request.getSession(false);
		if (session == null)
			throw new Exception(Str.format(request, "vcExpired"));
		String vcCode = (String) session.getAttribute("sys.verifyCode");
		session.removeAttribute("sys.verifyCode");
		if (StringUtil.isEmpty(vcCode)
				|| !StringUtil.isSame(vcCode, request
						.getParameter("verifyCode"))) {
			throw new Exception(Str.format(request, "invalidVc"));
		}
	}

	private static String[] checkUser(HttpServletRequest request)
			throws Exception {
		ResultSet rs = DbUtil
				.query(
						request,
						"select USER_ID,USER_NAME,DISPLAY_NAME,PASSWORD,USE_LANG,SSO_ID from WB_USER where USER_NAME={?username?} and STATUS=1");
		String password = request.getParameter("password"), username = request
				.getParameter("username"), truePwd = "", result[] = new String[5];

		if (!rs.next()){
			throw new Exception(Str.format(request, "userNotExist", username));
		}else {
			result[0] = rs.getString(1);
			result[1] = rs.getString(2);
			result[2] = rs.getString(3);
			truePwd = rs.getString(4);
			result[3] = rs.getString(5);
			result[4] = rs.getString(6);
		}
		if (!StringUtil.isEqual(Encrypter.getMD5(password), truePwd))
			//系统的加密算法验证
			if (!StringUtil.isEqual(EncryptHelper.getInstance().encrypt(password), truePwd))
				throw new Exception(Str.format(request, "invalidPwd"));
		return result;
	}

	private static String[] getRoles(HttpServletRequest request, String userId)
			throws Exception {
		request.setAttribute("userId", userId);
		ResultSet rs = DbUtil.query(request,
				"select ROLE_ID from WB_USER_ROLE where USER_ID={?userId?}");
		ArrayList<String> list = new ArrayList<String>();
		while (rs.next()) {
			list.add(rs.getString(1));
		}
		int size = list.size();
		if (size == 0)
			return null;
		else
			return list.toArray(new String[size]);
	}

	private static void createSession(HttpServletRequest request,
			String[] userInfo) throws Exception {
		HttpSession session, prevSession;
		prevSession = sessionList.get(userInfo[0]);
		if (prevSession != null)
			prevSession.invalidate();
		int timeout = Var.getInt("webbuilder.session.sessionTimeout");
		session = request.getSession(true);
		String ip;
		UserInfo user;

		session.setAttribute("sys.logined", 1);
		if (timeout != 0)
			session.setMaxInactiveInterval(timeout);
		ip = request.getRemoteAddr();
		session.setAttribute("sys.user", userInfo[0]);
		session.setAttribute("sys.userName", userInfo[1]);
		session.setAttribute("sys.dispName", userInfo[2]);
		session.setAttribute("sys.lang", userInfo[3]);
		session.setAttribute("sys.ssoId", userInfo[4]);
		session.setAttribute("sys.userRoles", getRoles(request, userInfo[0]));
		session.setAttribute("sys.userIp", ip);
		request.setAttribute("sys.user", userInfo[0]);
		request.setAttribute("sys.userName", userInfo[1]);
		request.setAttribute("sys.dispName", userInfo[2]);
		request.setAttribute("sys.lang", userInfo[3]);
		request.setAttribute("sys.ssoId", userInfo[4]);
		request.setAttribute("sys.userRoles", session
				.getAttribute("sys.userRoles"));
		user = new UserInfo();
		user.ip = ip;
		user.userId = userInfo[0];
		user.userName = userInfo[1];
		
		sessionList.put(userInfo[0], session);
		session.setAttribute("sys.userInfo", user);
	}
}
