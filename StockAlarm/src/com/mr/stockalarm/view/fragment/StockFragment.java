package com.mr.stockalarm.view.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mr.stockalarm.AppManager;
import com.mr.stockalarm.R;
import com.mr.stockalarm.domain.Market;
import com.mr.stockalarm.domain.Record;
import com.mr.stockalarm.domain.Stock;
import com.mr.stockalarm.util.FormatUtil;
import com.mr.stockalarm.util.HttpUtil;
import com.mr.stockalarm.util.SqliteUtil;
import com.mr.stockalarm.view.StockManagerActivity;

public class StockFragment extends DialogFragment {
	
	AppManager appManager = AppManager.getAppManger();
	SqliteUtil sqliteUtil = appManager.getSqliteUtil();
	
	StockManagerActivity stockManagerActivity;
	
	public StockFragment(StockManagerActivity stockManagerActivity) {
		super();
		this.stockManagerActivity = stockManagerActivity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, android.R.style.Theme_Black);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.stock_fragment, container, false);
		Button edit_button = (Button)view.findViewById(R.id.sm_edit_button);
		Button cancel_button = (Button)view.findViewById(R.id.sm_cancel_button);
		
		
		final EditText code_edit = (EditText)view.findViewById(R.id.sm_code_edit);
		final TextView name_text = (TextView)view.findViewById(R.id.sm_name_text);
		final TextView market_text = (TextView)view.findViewById(R.id.sm_market_text);
		final TextView price_text = (TextView)view.findViewById(R.id.sm_price_text);
		final TextView updown_text = (TextView)view.findViewById(R.id.sm_updown_text);
		
		edit_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String code = code_edit.getText().toString();
				if (code != null && !"".equals(code.trim())) {
					List<Stock> stocks = appManager.getStocks();
					for (Stock stock : stocks) {
						if (stock.symbol.equals(code)) {
							Toast.makeText(getActivity(), getString(R.string.repeat_code), Toast.LENGTH_SHORT).show();
							code_edit.requestFocus();
							return;
						}
					}
					List<Record> records = HttpUtil.getInstance().getStocks_163(code);
					if (records != null && !records.isEmpty()) {
						Stock stock = new Stock(records.get(0));
						sqliteUtil.insertStock(appManager.getDb(), stock);
						stocks.add(stock);
						appManager.setStocks(stocks);
						sqliteUtil.insertRecords(appManager.getDb(), records);
						name_text.setText(stock.name);
						market_text.setText(Market.getMarket(stock.type));
						price_text.setText(FormatUtil.formatDoubleToString(stock.price));
						updown_text.setText(FormatUtil.formatPercentToString(stock.percent) 
								+ "   " +  FormatUtil.formatDoubleToString(stock.updown));
						return;
					}
				} 
				Toast.makeText(getActivity(), getString(R.string.invalid_code), Toast.LENGTH_SHORT).show();
				code_edit.requestFocus();
			}
		});
		
		cancel_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		return view;
	}
}
