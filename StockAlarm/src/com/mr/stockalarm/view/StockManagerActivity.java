package com.mr.stockalarm.view;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mr.stockalarm.R;
import com.mr.stockalarm.common.BaseFragmentActivity;
import com.mr.stockalarm.domain.Stock;

public class StockManagerActivity extends BaseFragmentActivity {
	
	ListView stockList;
	ListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_manager);
		stockList = (ListView)findViewById(R.id.stockManageList);
		adapter = new StockManagerListAdapter(stocks);
		stockList.setAdapter(adapter);
	}
	
	class StockManagerListAdapter extends BaseAdapter {
		
		private List<Stock> stocks;
		private LayoutInflater mInflater;
		
		public StockManagerListAdapter(List<Stock> data) {
			this.stocks = data;
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			return stocks.size();
		}
		
		@Override
		public Object getItem(int position) {
			return stocks.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = mInflater.inflate(R.layout.stock_manager_list, parent, false);
			TextView codeText = (TextView)convertView.findViewById(R.id.sm_code_text);
			TextView nameText = (TextView)convertView.findViewById(R.id.sm_name_text);
			Button deleteButton = (Button)convertView.findViewById(R.id.sm_delete_button);
			
			final Stock stock = stocks.get(position);
			codeText.setText(stock.symbol);
			nameText.setText(stock.name);
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sqliteUtil.deleteStock(db, stock.code, true);
					stocks.remove(stock);
					notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}
}
