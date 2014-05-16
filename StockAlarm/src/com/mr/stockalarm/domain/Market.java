package com.mr.stockalarm.domain;

public enum Market {

	SH("上交所"), SZ("深交所");
	
	public static String getMarket(String type) {
		if ("SH".equals(type))
			return SH.getMarket();
		else if ("SZ".equals(type))
			return SZ.getMarket();
		return null;
	}
	
	private String market;
	
	private Market(String market) {
		this.market = market;
	}

	public String getMarket() {
		return market;
	}
}
