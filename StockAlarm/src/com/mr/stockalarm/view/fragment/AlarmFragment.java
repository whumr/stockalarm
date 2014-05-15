package com.mr.stockalarm.view.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mr.stockalarm.AppManager;
import com.mr.stockalarm.R;
import com.mr.stockalarm.domain.Alarm;
import com.mr.stockalarm.util.FormatUtil;
import com.mr.stockalarm.util.SqliteUtil;

public class AlarmFragment extends DialogFragment {
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, android.R.style.Theme_Light);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.alarm_fragment, container, false);
		Button edit_button = (Button)view.findViewById(R.id.am_edit_button);
		Button cancel_button = (Button)view.findViewById(R.id.am_cancel_button);
		
		final AutoCompleteTextView code_edit = (AutoCompleteTextView)view.findViewById(R.id.am_code_edit);
		final EditText percent_edit = (EditText)view.findViewById(R.id.am_percent_edit);
		final EditText money_edit = (EditText)view.findViewById(R.id.am_money_edit);
		
		edit_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Alarm alarm = new Alarm();
				alarm.code = code_edit.getText().toString();
				try {
					alarm.percent = FormatUtil.stringToDouble(percent_edit.getText().toString());
				} catch (NumberFormatException e) {
					Toast.makeText(getActivity(), getString(R.string.invalid_percent), Toast.LENGTH_SHORT).show();
					percent_edit.requestFocus();
					return;
				}
				try {
					alarm.money = FormatUtil.stringToDouble(money_edit.getText().toString());
				} catch (NumberFormatException e) {
					Toast.makeText(getActivity(), getString(R.string.invalid_money), Toast.LENGTH_SHORT).show();
					money_edit.requestFocus();
					return;
				}
				AppManager appManager = AppManager.getAppManger();
				SqliteUtil sqliteUtil = appManager.getSqliteUtil();
				sqliteUtil.insertOrUpdateAlarm(appManager.getDb(), alarm);
				appManager.getAlarms().add(alarm);
				dismiss();
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
