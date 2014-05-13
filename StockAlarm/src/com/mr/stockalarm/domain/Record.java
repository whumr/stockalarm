package com.mr.stockalarm.domain;

import org.json.JSONException;
import org.json.JSONObject;

import com.mr.stockalarm.util.JsonUtil;

public class Record {
	public String name, type, symbol, code, arrow, update, time;
	public int turnover, volume;
	public double yestclose, open, price, high, low, updown, percent;
	public int[] askvol, bidvol;
	public double[] ask, bid;
	
	public Record() {
	}

	public Record(JSONObject json) throws JSONException {
		this.name = JsonUtil.getString(json, "name");
		this.type = JsonUtil.getString(json, "type");
		this.symbol = JsonUtil.getString(json, "symbol");
		this.code = JsonUtil.getString(json, "code");
		this.arrow = JsonUtil.getString(json, "arrow");
		this.update = JsonUtil.getString(json, "update");
		this.time = JsonUtil.getString(json, "time");
		
		this.turnover = JsonUtil.getInt(json, "turnover");
		this.volume = JsonUtil.getInt(json, "volume");
		
		this.yestclose = JsonUtil.getDouble(json, "yestclose");
		this.open = JsonUtil.getDouble(json, "open");
		this.price = JsonUtil.getDouble(json, "price");
		this.high = JsonUtil.getDouble(json, "high");
		this.low = JsonUtil.getDouble(json, "low");
		this.updown = JsonUtil.getDouble(json, "updown");
		this.percent = JsonUtil.getDouble(json, "percent");
		
		this.ask = new double[]{
				JsonUtil.getDouble(json, "ask1"),
				JsonUtil.getDouble(json, "ask2"),
				JsonUtil.getDouble(json, "ask3"),
				JsonUtil.getDouble(json, "ask4"),
				JsonUtil.getDouble(json, "ask5")};
		this.bid = new double[]{
				JsonUtil.getDouble(json, "bid1"),
				JsonUtil.getDouble(json, "bid2"),
				JsonUtil.getDouble(json, "bid3"),
				JsonUtil.getDouble(json, "bid4"),
				JsonUtil.getDouble(json, "bid5")};
		this.askvol = new int[]{
				JsonUtil.getInt(json, "askvol1"),
				JsonUtil.getInt(json, "askvol2"),
				JsonUtil.getInt(json, "askvol3"),
				JsonUtil.getInt(json, "askvol4"),
				JsonUtil.getInt(json, "askvol5")};
		this.bidvol = new int[]{
				JsonUtil.getInt(json, "bidvol1"),
				JsonUtil.getInt(json, "bidvol2"),
				JsonUtil.getInt(json, "bidvol3"),
				JsonUtil.getInt(json, "bidvol4"),
				JsonUtil.getInt(json, "bidvol5")};
	}
}
