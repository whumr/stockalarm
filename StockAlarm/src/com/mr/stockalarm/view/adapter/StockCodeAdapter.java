package com.mr.stockalarm.view.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mr.stockalarm.domain.Stock;

public class StockCodeAdapter extends BaseAdapter implements Filterable {

	private List<Stock> stocks;
	private LayoutInflater mInflater;
	private Filter filter;
	
	public StockCodeAdapter(Context context, List<Stock> list) {
		super();
		this.stocks = list;
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		View view = (convertView == null ? 
				mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false) : convertView);
		TextView text = (TextView) view;

		Stock stock = stocks.get(position);
        text.setText(stock.code + "\t" + stock.name);
        return view;
	}

	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new StockCodeFilter();
		return filter;
	}
	
	class StockCodeFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			stocks = (List<Stock>)results.values;
            if (results.count > 0)
                notifyDataSetChanged();
            else
                notifyDataSetInvalidated();
		}
	}
}