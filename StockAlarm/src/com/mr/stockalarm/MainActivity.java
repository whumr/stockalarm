package com.mr.stockalarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
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

import com.mr.stockalarm.common.BaseActivity;
import com.mr.stockalarm.config.Config;
import com.mr.stockalarm.domain.Record;
import com.mr.stockalarm.domain.Stock;
import com.mr.stockalarm.service.AlarmService;
import com.mr.stockalarm.util.HttpUtil;
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
	
	Timer timer;
	Handler handler;
	
	MainMenu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("MainActivity onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		codeText = (EditText)findViewById(R.id.codeText);
		contentText = (TextView)findViewById(R.id.contentText);
		searchButton = (Button)findViewById(R.id.searchButton);
		stockList = (ListView)findViewById(R.id.stockList);
		
		adapter = new ArrayAdapter<Stock>(this, R.layout.stock_list, R.id.listText, stocks);
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
	
	private void refresh() {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < stocks.size(); i++) 
			buffer.append(stocks.get(i).symbol).append(",");
		if (buffer.length() > 1)
			search(buffer.substring(0, buffer.length() - 1));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("MainActivity onResume...");
		adapter.notifyDataSetChanged();
	}
	
	private void search(String codes) {
		List<Record> list = HttpUtil.getInstance().getStocks_163(codes);
		if (list != null && !list.isEmpty()) {
			List<Stock> stocks = new ArrayList<Stock>();
			Stock stock = new Stock(list.get(0));
			contentText.setText(stock.toString());
			boolean exist = false;
			for (Record record : list) {
				for (int i = 0; i < adapter.getCount(); i++) {
					stock = adapter.getItem(i);
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
		super.finish();
	}
	
	@Override
	protected void onDestroy() {
		System.out.println("MainActivity onDestroy");
		super.onDestroy();
	}
}
