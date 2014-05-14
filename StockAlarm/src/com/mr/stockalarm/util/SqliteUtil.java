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
	
	private static class SQL {
		/** get **/
		static String GET_ALL_STOCK_INFO = "select s.code, s.symbol, s.name, s.type, r.updated, r.price, r.updown, r.percent " +
				"from stock s, (select r1.* from records r1, (select code, max(time) time from records group by code) r2 " +
				"where r1.code = r2.code and r1.time = r2.time) r " +
				"where s.code = r.code";
		static String GET_STOCK_BY_CODE = "select code from stock where code in (?)";
		static String GET_RECORD_BY_CODE_UPDATED = "select code from records where code = ? and updated = ? limit 1";
		
		/** insert **/
		static String INSERT_STOCK = "insert into stock values (?, ?, ?, ?)";
		static String INSERT_RECORD = "insert into records values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
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
