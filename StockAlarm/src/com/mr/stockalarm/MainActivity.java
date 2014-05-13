package com.mr.stockalarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.mr.stockalarm.config.Config;
import com.mr.stockalarm.domain.Record;
import com.mr.stockalarm.domain.Stock;
import com.mr.stockalarm.service.AlarmService;
import com.mr.stockalarm.util.HttpUtil;
import com.mr.stockalarm.util.SqliteUtil;
import com.mr.stockalarm.view.BaseActivity;
import com.mr.stockalarm.view.menu.MainMenu;

public class MainActivity extends BaseActivity {

	static int Error = -1;
	static int Search = 1;
	static int Succeed = 2;
	
	long exitTime; 
	
	EditText codeText;
	TextView contentText;
	Button searchButton;
	ListView stockList;
	ArrayAdapter<Stock> adapter;
	
	List<Stock> data;
	
	SqliteUtil sqliteUtil;
	SQLiteDatabase db;
	
	Timer timer;
	Handler handler;
	
	MainMenu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("MainActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		((TextView)findViewById(R.id.helloText)).setText("test");
		codeText = (EditText)findViewById(R.id.codeText);
		contentText = (TextView)findViewById(R.id.contentText);
		searchButton = (Button)findViewById(R.id.searchButton);
		stockList = (ListView)findViewById(R.id.stockList);
		
		sqliteUtil = new SqliteUtil(this);
		db = sqliteUtil.getReadableDatabase();
		db = sqliteUtil.getWritableDatabase();
		data = sqliteUtil.getStocks(db);
		
		adapter = new ArrayAdapter<Stock>(this, R.layout.stock_list, R.id.listText, data);
		stockList.setAdapter(adapter);
		
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				search();
				refresh();
			}
		});
		
		codeText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				search();
				return true;
			}
		});
		
		timer = new Timer();
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == Error)
					showToast();
				else if (msg.what == Search)
					refresh();
			}
		};
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (checkTime()) {
					Message msg = new Message();
					msg.what = Search;
					handler.sendMessage(msg);
				}
			}
		}, 3000, 10000);
		
		Intent intent = new Intent(this, AlarmService.class);
		startService(intent);
		
		menu = new MainMenu(this);
	}
	
	@SuppressWarnings("deprecation")
	private void refresh() {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < data.size(); i++) 
			buffer.append(data.get(i).symbol).append(",");
		if (buffer.length() > 1)
			search(buffer.substring(0, buffer.length() - 1));
		System.out.println(buffer.toString());
		// 创建一个NotificationManager的引用
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 定义Notification的各种属性
		Notification notification = new Notification(R.drawable.ic_launcher, buffer.toString(), System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		// 0毫秒后开始振动，振动100毫秒后停止，再过200毫秒后再次振动300毫秒
		long[] vibrate = { 0, 100, 200, 300 };
		notification.vibrate = vibrate;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "title", "text", contentIntent);
		// 把Notification传递给 NotificationManager
		mNotificationManager.notify(0, notification);
	}
	
	private void search(String codes) {
		List<Record> list = HttpUtil.getInstance().getStocks_163(codes);
		if (list != null && !list.isEmpty()) {
			List<Stock> stocks = new ArrayList<Stock>();
			Stock stock = new Stock(list.get(0));
			contentText.setText(stock.toString());
			boolean exist = false;
			for (Record record : list) {
				for (int i = 0; i < data.size(); i++) {
					stock = data.get(i);
					if (stock.code.equals(record.code)) {
						adapter.remove(stock);
						adapter.insert(new Stock(record), i);
						exist = true;
						break;
					}
				}
				if (!exist) {
					stock = new Stock(record);
					adapter.add(stock);
					stocks.add(stock);
				}
			}
			//插入records
			sqliteUtil.insertRecords(db, list);
			sqliteUtil.insertStocks(db, stocks); 
		} else {
			Message msg = new Message();
			msg.what = Error;
			handler.sendMessage(msg);
		}
	}
	
	private void search() {
		hideInputKeyboard(codeText.getWindowToken());
		search(codeText.getText().toString());
	}
	
	private boolean checkTime() {
		Calendar cl = Calendar.getInstance();
		int hour = cl.get(Calendar.HOUR_OF_DAY);
		int minute = cl.get(Calendar.MINUTE);
		int second = cl.get(Calendar.SECOND);
		int time = hour * 10000 + minute * 100 + second;
		if ((time > 93000 && time < 113100) || (time > 130000 && time < 150100))
			return true;
		return false;
	}
	
	private void showToast() {
		Toast toast = Toast.makeText(MainActivity.this, getString(R.string.search_error_null), Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK :
				boolean quit = true;
				if (event.getRepeatCount() == 0 && (System.currentTimeMillis() - exitTime) > Config.QUIT_IDLE) {
					Toast.makeText(this, getString(R.string.alert_msg_quit), Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
					quit = false;
				}
				if (quit)
					quit();
				break;
			case KeyEvent.KEYCODE_MENU :
				menu.toggle();
		}
		return true;
	}
	
	public void quit() {
		appManager.quit();
	}
	
	@Override
	public void finish() {
		System.out.println("MainActivity finish");
		Intent intent = new Intent(this, AlarmService.class);
		stopService(intent);
		db.close();
		sqliteUtil.close();
		super.finish();
	}
	
	@Override
	protected void onDestroy() {
		System.out.println("MainActivity onDestroy");
		super.onDestroy();
	}
}
