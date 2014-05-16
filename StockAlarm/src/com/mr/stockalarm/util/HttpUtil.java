package com.mr.stockalarm.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.mr.stockalarm.config.Config.NetWork;
import com.mr.stockalarm.domain.Record;

public class HttpUtil {
	
	private static HttpUtil httpUtil;
	
	private HttpClient client;
	
	private HttpUtil() {
		client = new DefaultHttpClient();
	}
	
	public static HttpUtil getInstance() {
		if (httpUtil == null)
			httpUtil = new HttpUtil();
		return httpUtil;
	}

	public List<Record> getStocks_163(String codes) {
		if (codes == null || "".equals(codes.trim()))
			return null;
		String[] code = codes.split(",|，|\\s+");
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < code.length; i++) {
			String c = code[i].trim();
			if (c.length() == 6) {
				if (c.startsWith("0"))
					c = "1" + c;
				else if (c.startsWith("6"))
					c = "0" + c;
				buffer.append(c).append(",");
			}
		}
		if (buffer.length() > 6) {
			HttpGet getMethod = new HttpGet(NetWork.STOCK_163_URL + buffer.substring(0, buffer.length() - 1));  
			try {
				HttpResponse response = client.execute(getMethod);
				switch (response.getStatusLine().getStatusCode()) {
				case HttpStatus.SC_OK :
					return parse_163(EntityUtils.toString(response.getEntity(), NetWork.STOCK_163_CHARSET));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private List<Record> parse_163(String content) {
		List<Record> list = new ArrayList<Record>();
		try {
			JSONObject json = new JSONObject(content.substring(content.indexOf("{"), content.lastIndexOf("}") + 1));
			Iterator<?> it = json.keys();
		    while (it.hasNext()) {
		    	String code = it.next().toString();
		    	Record record = new Record(json.getJSONObject(code));
		    	list.add(record);
		    }
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}

}
