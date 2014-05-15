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
import com.mr.stockalarm.domain.Alarm;
import com.mr.stockalarm.view.fragment.AlarmFragment;

public class AlarmManagerActivity extends BaseFragmentActivity {
	
	ListView alarmList;
	ListAdapter adapter;
	
	AlarmFragment alarmFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_manager);
		alarmList = (ListView)findViewById(R.id.alarmManageList);
		adapter = new AlarmManagerListAdapter(alarms);
		alarmList.setAdapter(adapter);
		
		alarmFragment = new AlarmFragment();
		Button addButton = (Button)findViewById(R.id.am_add_button);
		addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alarmFragment.show(getSupportFragmentManager(), "alarmFragment");
			}
		});
	}
	
	class AlarmManagerListAdapter extends BaseAdapter {
		
		private List<Alarm> alarms;
		private LayoutInflater mInflater;
		
		public AlarmManagerListAdapter(List<Alarm> data) {
			this.alarms = data;
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			return alarms.size();
		}
		
		@Override
		public Object getItem(int position) {
			return alarms.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = mInflater.inflate(R.layout.alarm_manager_list, parent, false);
			TextView codeText = (TextView)convertView.findViewById(R.id.am_code_text);
			TextView nameText = (TextView)convertView.findViewById(R.id.am_name_text);
			Button deleteButton = (Button)convertView.findViewById(R.id.am_delete_button);
			
			final Alarm alarm = alarms.get(position);
			codeText.setText(alarm.stock_symbol);
			nameText.setText(alarm.stock_name);
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sqliteUtil.deleteAlarm(db, alarm.code);
					alarms.remove(alarm);
					notifyDataSetChanged();
				}
			});
			return convertView;
		}
	}
}
