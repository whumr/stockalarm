package com.mr.stockalarm.domain;

import com.mr.stockalarm.util.FormatUtil;

public class Stock {

	public String name, type, symbol, code, update;
	public double price, updown, percent;
	
	public Stock() {
	}

	public Stock(String code) {
		this.code = code;
	}
	
	public Stock(Record record) {
		this.code = record.code;
		this.name = record.name;
		this.type = record.type;
		this.symbol = record.symbol;
		this.update = record.update;
		this.price = record.price;
		this.updown = record.updown;
		this.percent = record.percent;
	}
	
	@Override
	public String toString() {
		return symbol + " " + name + " " + price
			+ " " + updown + " " + FormatUtil.formatPercentToString(percent) + FormatUtil.dateToTime(update);
	}
}
