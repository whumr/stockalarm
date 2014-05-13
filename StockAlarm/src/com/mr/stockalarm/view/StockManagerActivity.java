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
import android.widget.Toast;

import com.mr.stockalarm.R;
import com.mr.stockalarm.domain.Stock;

public class StockManagerActivity extends BaseActivity {
	
	ListView stockList;
	ListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stock_manager);
		stockList = (ListView)findViewById(R.id.stockManageList);
		adapter = new StockManagerListAdapter(this, data);
		stockList.setAdapter(adapter);
	}
}

class StockManagerListAdapter extends BaseAdapter {

	private Context context;
	private List<Stock> data;
	private LayoutInflater mInflater;
	
	public StockManagerListAdapter(Context context, List<Stock> data) {
		this.context = context;
		this.data = data;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
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
		
		final Stock stock = data.get(position);
		codeText.setText(stock.symbol);
		nameText.setText(stock.name);
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, stock.toString(), Toast.LENGTH_SHORT).show();
			}
		});
		return convertView;
	}
}

