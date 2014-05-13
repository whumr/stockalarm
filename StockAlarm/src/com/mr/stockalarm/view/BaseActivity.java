package com.mr.stockalarm.view;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

import com.actionbarsherlock.app.SherlockActivity;
import com.mr.stockalarm.AppManager;

public class BaseActivity extends SherlockActivity {

	protected AppManager appManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appManager = AppManager.getAppManger();
		appManager.addActivity(this);
	}
	
	@Override
	protected void onDestroy() {
		appManager.removeActivity(this);
		super.onDestroy();
	}
	
	protected void hideInputKeyboard(IBinder binder) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(binder, 0);
	}
}
