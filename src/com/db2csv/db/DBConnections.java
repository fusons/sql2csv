package com.db2csv.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnections {
	public static final String DRIVER = "";
	public static final String URL = "";
	public static final String USERNAME = "";
	public static final String PASSWORD = "";
	
	private String driver;
	private String url;
	private String username;
	private String password;
	
	public DBConnections(String driver, String url, String username, String password) {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public Connection getConn() throws Exception {
		Connection conn = null;
		try {
			Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
        return conn;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
		
}
