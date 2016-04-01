package com.zhiding.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.h2.Driver;
import org.h2.tools.Server;
import org.h2.util.StringUtils;

public class H2DbStarter  implements ServletContextListener {
	private Connection conn;
	private Server server;
	
	public H2DbStarter() {
	}

	public void contextInitialized(ServletContextEvent servletcontextevent) {
		try {
			ServletContext servletcontext = servletcontextevent
					.getServletContext();
			String isOpen = getParameter(servletcontext, "h2db.open", "true");
			if("false".equalsIgnoreCase(isOpen)) return;
			
			Driver.load();
			String s = getParameter(servletcontext, "db.url", "jdbc:h2:~/test");
			if(s.indexOf("/WEB-INF")>-1) {
				String dbPath = servletcontextevent.getServletContext().getRealPath("/WEB-INF");
				dbPath = dbPath.replaceAll("\\\\", "/");
				s = s.replaceAll("/WEB-INF", dbPath);
				System.out.println("dbPath: "+ dbPath+"\t"+s);
			}
			String s1 = getParameter(servletcontext, "db.user", "sa");
			String s2 = getParameter(servletcontext, "db.password", "sa");
			String s3 = getParameter(servletcontext, "db.tcpServer", null);
			if (s3 != null) {
				String as[] = StringUtils.arraySplit(s3, ' ', true);
				server = Server.createTcpServer(as);
				server.start();
			}
			conn = DriverManager.getConnection(s, s1, s2);
			servletcontext.setAttribute("connection", conn);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static String getParameter(ServletContext servletcontext, String s,
			String s1) {
		String s2 = servletcontext.getInitParameter(s);
		return s2 != null ? s2 : s1;
	}

	public Connection getConnection() {
		return conn;
	}

	public void contextDestroyed(ServletContextEvent servletcontextevent) {
		try {
			Statement statement = conn.createStatement();
			statement.execute("SHUTDOWN");
			statement.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		try {
			conn.close();
		} catch (Exception exception1) {
			exception1.printStackTrace();
		}
		if (server != null) {
			server.stop();
			server = null;
		}
	}

}
