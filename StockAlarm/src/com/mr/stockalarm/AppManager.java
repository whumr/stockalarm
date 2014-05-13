package com.mr.stockalarm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class AppManager extends Application {
	
	private List<Activity> activityList;
	
	private static AppManager appManager;
	
	private AppManager() {
		this.activityList = new ArrayList<Activity>();
	}
	
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
}
