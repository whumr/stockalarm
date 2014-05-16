package com.mr.stockalarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.mr.stockalarm.domain.Alarm;
import com.mr.stockalarm.domain.Notify;
import com.mr.stockalarm.domain.Stock;
import com.mr.stockalarm.util.SqliteUtil;

public class AppManager extends Application {

	private static AppManager appManager;
	
	private List<Activity> activityList = new ArrayList<Activity>();
	private List<Stock> stocks;
	private List<Alarm> alarms;
	private SqliteUtil sqliteUtil;
	private SQLiteDatabase db;
	private List<Notify> notifies = new Vector<Notify>();
	private Map<String, Long> alarm_map = new HashMap<String, Long>();
	
	public static AppManager getAppManger() {
		if (appManager == null)
			appManager = new AppManager();
		return appManager;
	}
	
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
	
	public void removeActivity(Activity activity) {
		activityList.remove(activity);
	}
	
	public void quit() {
		for (Activity activity : activityList) {
			if (activity != null && !activity.isFinishing())
				activity.finish();
		}
		System.exit(0);
	}

	public List<Stock> getStocks() {
		return stocks;
	}

	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}

	public List<Alarm> getAlarms() {
		return alarms;
	}

	public void setAlarms(List<Alarm> alarms) {
		this.alarms = alarms;
	}

	public SqliteUtil getSqliteUtil() {
		return sqliteUtil;
	}

	public void setSqliteUtil(SqliteUtil sqliteUtil) {
		this.sqliteUtil = sqliteUtil;
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	public List<Notify> getNotifies() {
		return notifies;
	}

	public void setNotifies(List<Notify> notifies) {
		this.notifies = notifies;
	}

	public Map<String, Long> getAlarm_map() {
		return alarm_map;
	}

	public void setAlarm_map(Map<String, Long> alarm_map) {
		this.alarm_map = alarm_map;
	}
}
