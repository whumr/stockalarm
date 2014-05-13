package com.mr.stockalarm.config;

public class Config {

	public static long QUIT_IDLE = 2000;
	
	public static class NetWork {
		public static String STOCK_163_URL = "http://api.money.126.net/data/feed/", STOCK_163_CHARSET = "UTF-8";
	}
	
	public static class DB {
		public static int DB_VERSION = 2;
		public static String DB_NAME = "stock_db";
	}
}
