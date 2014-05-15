package com.mr.stockalarm.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.mr.stockalarm.config.Config.DB;
import com.mr.stockalarm.domain.Alarm;
import com.mr.stockalarm.domain.Record;
import com.mr.stockalarm.domain.Stock;

public class SqliteUtil extends SQLiteOpenHelper {
	
	public SqliteUtil(Context context) {
		super(context, DB.DB_NAME, null, DB.DB_VERSION);
	}

	public SqliteUtil(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("create db");  
        //execSQL用于执行SQL语句  
		for (String sql : SQL.CREATE_TABLE_SQL)
			db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1 && newVersion == DB.DB_VERSION) {
			db.execSQL("create table alarms (code varchar(10) primary key, percent real, money real)");
		}
	}
	
	public List<Stock> getStocks(SQLiteDatabase db) {
		List<Stock> list = new ArrayList<Stock>();
		Cursor cursor = db.rawQuery(SQL.GET_ALL_STOCK_INFO, null);
		while (cursor.moveToNext()) {
			Stock stock = new Stock();
			stock.code = cursor.getString(0);
			stock.symbol = cursor.getString(1);
			stock.name = cursor.getString(2);
			stock.type = cursor.getString(3);
			stock.update = FormatUtil.intToDate(cursor.getInt(4));
			
			stock.price = cursor.getDouble(5);
			stock.updown = cursor.getDouble(6);
			stock.percent = cursor.getDouble(7);
			list.add(stock);
		}
		cursor.close();
		return list;
	}
	
	public void insertStocks(SQLiteDatabase db, List<Stock> stocks) {
		if (stocks != null && !stocks.isEmpty()) {
			StringBuilder buffer = new StringBuilder();
			for (Stock stock : stocks) 
				buffer.append("'").append(stock.code).append("',");
			if (buffer.length() > 0) {
				Cursor cursor = db.rawQuery(SQL.GET_STOCK_BY_CODE, new String[]{buffer.substring(0, buffer.length() - 1)});
				while (cursor.moveToNext()) {
					String code = cursor.getString(0);
					for (Iterator<Stock> iterator = stocks.iterator(); iterator.hasNext();) {
						Stock stock = iterator.next();
						if (code.equals(stock.code))
							iterator.remove();
					}
				}
				cursor.close();
			}
			for (Stock stock : stocks) 
				db.execSQL(SQL.INSERT_STOCK, new String[]{stock.code, stock.name, stock.type, stock.symbol});
		}
	}
	
	public void insertRecords(SQLiteDatabase db, List<Record> records) {
		if (records != null && !records.isEmpty()) {
			for (Record record : records) {
				Cursor cursor = db.rawQuery(SQL.GET_RECORD_BY_CODE_UPDATED, new String[]{record.code, FormatUtil.timeStringToInt(record.update) + ""});
				if (!cursor.moveToNext()) {
					db.execSQL(SQL.INSERT_RECORD, new Object[]{
						record.code, FormatUtil.timeStringToInt(record.update), FormatUtil.timeStringToInt(record.time), record.turnover,
						record.volume, FormatUtil.doubleArrayToString(record.ask), FormatUtil.doubleArrayToString(record.bid), 
						FormatUtil.intArrayToString(record.askvol), FormatUtil.intArrayToString(record.bidvol),
						record.yestclose, record.open, record.price, record.high, record.low, record.updown, record.percent});
				}
				cursor.close();
			}
		}
	}
	
	public void deleteStock(SQLiteDatabase db, String code, boolean deleteRecords) {
		if (code != null && !"".equals(code.trim())) {
			db.execSQL(SQL.DELETE_STOCK, new String[] {code});
			if (deleteRecords)
				db.execSQL(SQL.DELETE_RECORDS, new String[] {code});
		}
	}
	
	public void insertOrUpdateAlarm(SQLiteDatabase db, Alarm alarm) {
		Cursor cursor = db.rawQuery(SQL.GET_ALARM_BY_CODE, new String[]{alarm.code});
		if (cursor.moveToNext()) 
			db.execSQL(SQL.UPDATE_ALARM, new Object[]{alarm.percent, alarm.money, alarm.code});
		else
			db.execSQL(SQL.INSERT_ALARM, new Object[]{alarm.code, alarm.percent, alarm.money});
	}
	
	public List<Alarm> getAlarms(SQLiteDatabase db) {
		List<Alarm> list = new ArrayList<Alarm>();
		Cursor cursor = db.rawQuery(SQL.GET_ALL_ALARM_INFO, null);
		while (cursor.moveToNext()) {
			Alarm alarm = new Alarm();
			alarm.code = cursor.getString(0);
			alarm.stock_name = cursor.getString(1);
			alarm.stock_symbol = cursor.getString(2);
			alarm.percent = cursor.getDouble(3);
			alarm.money = cursor.getDouble(4);
			list.add(alarm);
		}
		cursor.close();
		return list;
	}
	
	public void deleteAlarm(SQLiteDatabase db, String code) {
		if (code != null && !"".equals(code.trim())) 
			db.execSQL(SQL.DELETE_ALARMS, new String[] {code});
	}
	
	private static class SQL {
		/** get **/
		static String GET_ALL_STOCK_INFO = "select s.code, s.symbol, s.name, s.type, r.updated, r.price, r.updown, r.percent " +
				"from stock s, (select r1.* from records r1, (select code, max(time) time from records group by code) r2 " +
				"where r1.code = r2.code and r1.time = r2.time) r " +
				"where s.code = r.code";
		static String GET_STOCK_BY_CODE = "select code from stock where code in (?)";
		static String GET_RECORD_BY_CODE_UPDATED = "select code from records where code = ? and updated = ? limit 1";
		static String GET_ALARM_BY_CODE = "select code from alarms where code = ?";
		static String GET_ALL_ALARM_INFO = "select a.code, s.name, s.symbol, a.percent, a.money from alarms a, stock s where a.code = s.code";
		
		/** insert **/
		static String INSERT_STOCK = "insert into stock values (?, ?, ?, ?)";
		static String INSERT_RECORD = "insert into records values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		static String INSERT_ALARM = "insert into alarms values (?, ?, ?)";
		
		/** delete **/
		static String DELETE_STOCK = "delete from stock where code = ?";
		static String DELETE_RECORDS = "delete from records where code = ?";
		static String DELETE_ALARMS = "delete from alarms where code = ?";
		
		/** update **/
		static String UPDATE_ALARM = "update alarms set percent = ?, money = ? where code = ?";
		
		/** create table **/
		static String[] CREATE_TABLE_SQL = {
			"create table stock (code varchar(10) primary key, name varchar(20), type varchar(20), symbol varchar(10))",
			"create table records (code varchar(10), updated int, time int, turnover int, volumn int," +
					"ask varchar(50), bid varchar(50), askvol varchar(50), bidvod varchar(50)," +
					"yestclose real, open real, price real, high real, low real, updown real, percent real)",
			"create unique index records_uni_code_updated on records(code, updated)",
			"create table alarms (code varchar(10) primary key, percent real, money real)"
		};
	}
	
}
