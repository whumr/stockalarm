package com.mr.stockalarm.view.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mr.stockalarm.MainActivity;
import com.mr.stockalarm.R;
import com.mr.stockalarm.view.StockManagerActivity;

public class MainMenu extends SlidingMenu {

	static final String ICON = "icon", TITLE = "text";
	
	public MainMenu(MainActivity activity) {
		super(activity);
		setMode(SlidingMenu.LEFT);
        // 滑动显示SlidingMenu的范围
        setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 菜单的宽度
        setBehindWidth(getResources().getDimensionPixelSize(R.dimen.menu_width));
        // 把SlidingMenu附加在Activity上
        // SlidingMenu.SLIDING_WINDOW:菜单拉开后高度是全屏的
        // SlidingMenu.SLIDING_CONTENT:菜单拉开后高度是不包含Title/ActionBar的内容区域
        attachToActivity(activity, SlidingMenu.SLIDING_WINDOW);
        // 菜单的布局文件
        setMenu(R.layout.menu_main);

        // 设置菜单内容
        ListView listView = (ListView) findViewById(R.id.menu_list);
        List<Integer> itemsIcon = new ArrayList<Integer>();
        itemsIcon.add(R.drawable.icon_stock);
        itemsIcon.add(R.drawable.icon_alarm);
        itemsIcon.add(R.drawable.icon_quit);

        List<String> itemsTitle = new ArrayList<String>();
        itemsTitle.add(activity.getString(R.string.menu_stock));
        itemsTitle.add(activity.getString(R.string.menu_alarm));
        itemsTitle.add(activity.getString(R.string.menu_quit));

        List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < itemsTitle.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(ICON, itemsIcon.get(i));
            map.put(TITLE, itemsTitle.get(i));
            items.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(activity, items,
                R.layout.sliding_menu_item, new String[] {ICON, TITLE},
                new int[] {R.id.icon, R.id.title});

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new MenuListner(activity));
	}
	
	class MenuListner implements OnItemClickListener {

		private MainActivity activity;
		
		public MenuListner(MainActivity activity) {
			this.activity = activity;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			switch (position) {
				case 0 :
					Intent intent = new Intent(activity, StockManagerActivity.class);
					activity.startActivity(intent);
					break;
				case 1 :
					break;
				case 2 :
					activity.quit();
					break;
			}
			MainMenu.this.toggle();
		}
		
	}
}