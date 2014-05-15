package com.mr.stockalarm.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {
	
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	public static DecimalFormat DOUBLE_FORMAT = new DecimalFormat(".##");
	
	public static String intToDate(int seconds) {
		return DATE_FORMAT.format(new Date(seconds * 1000L));
	}

	public static double formatDouble(double percent) {
		return Double.parseDouble(DOUBLE_FORMAT.format(percent * 100));
	}
	
	public static double stringToDouble(String str) {
		return formatDouble(Double.parseDouble(str));
	}
	
	public static String dateToTime(String date) {
		try {
			return TIME_FORMAT.format(DATE_FORMAT.parse(date));
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static int timeStringToInt(String time) {
		try {
			return (int)(DATE_FORMAT.parse(time).getTime() / 1000);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static String doubleArrayToString(double[] array) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < array.length; i++) 
			buffer.append(array[i]).append(",");
		return buffer.length() > 1 ? buffer.substring(0, buffer.length() - 1) : buffer.toString();
	}

	public static String intArrayToString(int[] array) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < array.length; i++) 
			buffer.append(array[i]).append(",");
		return buffer.length() > 1 ? buffer.substring(0, buffer.length() - 1) : buffer.toString();
	}
}