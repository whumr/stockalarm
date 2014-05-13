package com.mr.stockalarm.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

	public static int getInt(JSONObject json, String key) {
		try {
			return json.getInt(key);
		} catch (JSONException e) {
			return 0;
		}
	}
	
	public static String getString(JSONObject json, String key) {
		try {
			return json.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}

	public static double getDouble(JSONObject json, String key) {
		try {
			return json.getDouble(key);
		} catch (JSONException e) {
			return 0;
		}
	}

	public static long getLong(JSONObject json, String key) {
		try {
			return json.getLong(key);
		} catch (JSONException e) {
			return 0;
		}
	}
}
