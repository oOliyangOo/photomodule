package com.sina.auto.components.WebCapture.api.google;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author asus
 * @param sql:预编译sql语句
 * @param ArrayList:按位传参参数
 * */
@EnableTransactionManagement 
public class SqlBean {
	private static final String dbUrl = "jdbc:mysql://172.16.11.49:3306/file_store?autoReconnect=true&failOverReadOnly=false&characterEncoding=utf8&useSSL=false";//
	private static final String username = "root";
	private static final String password = "Jcinfo@1995";
	
	/*private static String dbUrl = "jdbc:mysql://172.16.4.62:3306/data?characterEncoding=utf8&useSSL=true";
	private static String username = "root";
	private static String password = "root";*/
	@Transactional(rollbackFor=Exception.class)
	public static List selSales(String sql) {
		Connection conn=SqlBean.conn();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List list=new ArrayList();
		try {
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			list=SqlBean.convertList(rs);
			rs.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SqlBean.conn(conn);
		return list;
	}
	@Transactional(rollbackFor=Exception.class)
	public static ResultSet selSales(String sql,ArrayList list) {
    	Connection conn = getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(sql);
			
			int len=list.size();
			for(int i=0;i<len;i++) {
				if(list.get(i) instanceof String) {
					stmt.setString(i+1, (String) list.get(i));
				}else if(list.get(i) instanceof Integer) {
					stmt.setInt(i+1, (int) list.get(i));
				}else if(list.get(i) instanceof Long) {
					stmt.setLong(i+1, (long) list.get(i));
				}
			}
			rs = stmt.executeQuery();
			conn.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return rs;
	}
	@Transactional(rollbackFor=Exception.class)
	public static int updateSales(String sql) {
		Connection conn = getConnection();
		int n=0;
		try {
			PreparedStatement updateSales = conn.prepareStatement(sql);
			n = updateSales.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return n;
	}
	@Transactional(rollbackFor=Exception.class)
	public static int updateSales(String sql,ArrayList list) {
		Connection conn = getConnection();
		int n=0;
		try {
			PreparedStatement updateSales = conn.prepareStatement(sql);
			int len=list.size();
			for(int i=0;i<len;i++) {
				if(list.get(i) instanceof String) {
					updateSales.setString(i+1, (String) list.get(i));
				}else if(list.get(i) instanceof Integer) {
					updateSales.setInt(i+1, (int) list.get(i));
				}else if(list.get(i) instanceof Long) {
					updateSales.setLong(i+1, (long) list.get(i));
				}
			}
			n = updateSales.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return n;
	}
	public static String timestamp() {
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = sd.format(new Date());
		return String.valueOf(time);
	}
	public static long unixTimestamp() {
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sd.format(new Date());
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long unixTimestamp = date.getTime() / 1000;
		return unixTimestamp;
	}
	private static List convertList(ResultSet rs) throws SQLException {
		List list = new ArrayList();
		ResultSetMetaData md = rs.getMetaData();
		//Map rowData;
		int columnCount = md.getColumnCount(); 
		while (rs.next()) {
			//rowData = new HashMap(columnCount);
			Map rowData = new HashMap();
			for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(rowData);
		} 
		return list;
	}
	public static Connection conn() {
		Connection conn = getConnection();
		return conn;
	}
	public static void conn(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static Connection getConnection(){
		Connection conn=null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbUrl, username, password);
			return conn;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	 }
}
