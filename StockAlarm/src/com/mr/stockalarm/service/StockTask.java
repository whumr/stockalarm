package com.mr.stockalarm.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mr.stockalarm.AppManager;
import com.mr.stockalarm.R;
import com.mr.stockalarm.config.Config;
import com.mr.stockalarm.domain.Alarm;
import com.mr.stockalarm.domain.Notify;
import com.mr.stockalarm.domain.Record;
import com.mr.stockalarm.domain.Stock;
import com.mr.stockalarm.util.FormatUtil;
import com.mr.stockalarm.util.HttpUtil;
import com.mr.stockalarm.util.SqliteUtil;

public class StockTask extends TimerTask {
	
	private AppManager appManager = AppManager.getAppManger();
	private SqliteUtil sqliteUtil = appManager.getSqliteUtil();
	private SQLiteDatabase db = appManager.getDb();

	private AlarmService alarmService;
	
	private String alarm_title, alarm_content_up, alarm_content_down;
	
	public StockTask(AlarmService alarmService) {
		super();
		this.alarmService = alarmService;
		alarm_title = alarmService.getString(R.string.alarm_title);
		alarm_content_up = alarmService.getString(R.string.alarm_content_up);
		alarm_content_down = alarmService.getString(R.string.alarm_content_down);
	}

	@Override
	public void run() {
		List<Stock> stocks = appManager.getStocks();
		if (stocks != null && !stocks.isEmpty())
			if (checkTime() && checkNet(alarmService))
				search(stocks);
	}
	
	private void search(List<Stock> stocks) {
		HashMap<String, Stock> stock_map = new HashMap<String, Stock>();
		HashMap<String, Alarm> alarm_map = new HashMap<String, Alarm>();
		StringBuilder buffer = new StringBuilder();
		for (Stock stock : stocks) {
			buffer.append(stock.symbol).append(",");
			stock_map.put(stock.symbol, stock);
		}
		for (Alarm alarm : appManager.getAlarms()) {
			alarm_map.put(alarm.code, alarm);
		}
		if (buffer.length() > 1) {
			List<Record> records = HttpUtil.getInstance().getStocks_163(buffer.substring(0, buffer.length() - 1));
			if (records != null && !records.isEmpty()) {
				for (Record record : records) {
					Stock stock = stock_map.get(record.code);
					if (stock != null) {
						stock.price = record.price;
						stock.updown = record.updown;
						stock.percent = record.percent;
						stock.update = record.update;
					}
					Alarm alarm = alarm_map.get(record.symbol);
					if (alarm != null)
						createNotify(alarm, record);
				}
				appManager.setStocks(stocks);
				//插入records
				sqliteUtil.insertRecords(db, records);
			}
		}
	}
	
	private boolean checkTime() {
		Calendar cl = Calendar.getInstance();
		int day = cl.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.SUNDAY || day == Calendar.SATURDAY)
			return false;
		int hour = cl.get(Calendar.HOUR_OF_DAY);
		int minute = cl.get(Calendar.MINUTE);
		int second = cl.get(Calendar.SECOND);
		int time = hour * 10000 + minute * 100 + second;
		if ((time > 93000 && time < 113100) || (time > 130000 && time < 150100))
			return true;
		return false;
	}
	
	private boolean checkNet(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) 
			return false;
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable())
			return false;
		return true;
	}
	
	private void createNotify(Alarm alarm, Record record) {
		Long last_notified = appManager.getAlarm_map().get(alarm.code);
		if (last_notified != null && (System.currentTimeMillis() - last_notified) < Config.ALARM_IDLE)
			return;
		Notify notify = new Notify();
		if (alarm.percent > 0) {
			if (Math.abs(record.percent) > alarm.percent) {
				notify.title = alarm_title.replaceFirst("#", record.name);
				String content = record.percent > 0 ? alarm_content_up : alarm_content_down;
				notify.content = content.replaceFirst("#", record.name)
						.replaceFirst("#", FormatUtil.formatPercentToString(Math.abs(record.percent)))
						.replaceFirst("#", FormatUtil.formatDoubleToString(Math.abs(record.updown)))
						.replaceFirst("#", FormatUtil.formatDoubleToString(record.price));
				appManager.getNotifies().add(notify);
				return;
			}
		}
		if (alarm.money > 0) {
			if (Math.abs(record.price) > alarm.money) {
				notify.title = alarm_title.replaceFirst("#", record.name);
				String content = record.price > 0 ? alarm_content_up : alarm_content_down;
				notify.content = content.replaceFirst("#", record.name)
						.replaceFirst("#", FormatUtil.formatPercentToString(Math.abs(record.percent)))
						.replaceFirst("#", FormatUtil.formatDoubleToString(Math.abs(record.updown)))
						.replaceFirst("#", FormatUtil.formatDoubleToString(record.price));
				appManager.getNotifies().add(notify);
				return;
			}
		}
	}
}