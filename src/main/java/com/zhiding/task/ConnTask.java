package com.zhiding.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.webbuilder.utils.DbUtil;

public class ConnTask implements Job{

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println("开始连接MySql" + new Date());
		
		Connection conn = null;
		ResultSet order_rs = null;
		
		try{
			conn = DbUtil.getConnection();
			conn.setAutoCommit(false);
			
			order_rs = DbUtil.getResultSet(conn, "select * from wb_key where key_name like '01'");
			while(order_rs.next()){
				int i = 1;
			}
		}catch(Exception e){
			DbUtil.closeConnection(conn,true);
			e.printStackTrace();
		}finally{
			DbUtil.closeResultSet(order_rs);
			DbUtil.closeConnection(conn, false);
		}
		System.out.println("结束连接MySql" + new Date());
	}
	
}
