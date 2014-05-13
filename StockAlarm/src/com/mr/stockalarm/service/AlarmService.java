package com.mr.stockalarm.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;

import com.mr.stockalarm.MainActivity;
import com.mr.stockalarm.R;

public class AlarmService extends Service {

	MediaPlayer mediaPlayer;

	NotificationManager mNotificationManager;
	
	@Override
	public void onStart(Intent intent, int startId) {
		System.out.println("AlarmService onStart...");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("AlarmService onStartCommand...");
		
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		// 开始播放音乐
		mediaPlayer.start();
		// 音乐播放完毕的事件处理
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mediaPlayer) {
				// 循环播放
//				try {
//					mediaPlayer.start();
//				} catch (IllegalStateException e) {
//					e.printStackTrace();
//				}
			}
		});
		// 播放音乐时发生错误的事件处理
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
				// 释放资源
				try {
					mediaPlayer.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		});
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void notificate(String title, String content) {
		Builder builder = new Builder(this);
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		Notification notification = 
		builder.setContentIntent(contentIntent)
		//设置状态栏里面的图标（小图标） 　　　　　　　　　　　　　　　　　　　　
		.setSmallIcon(R.drawable.ic_launcher)
		//下拉下拉列表里面的图标（大图标） 　　　　　　　
		.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
		//设置状态栏的显示的信息
		.setTicker(content) 
		//设置时间发生时间
		.setWhen(System.currentTimeMillis())
        //设置可以清除
		.setAutoCancel(true)
		//设置下拉列表里的标题
		.setContentTitle(title)
		// 设置上下文内容
		.setContentText(content).build();
		// 定义Notification的各种属性
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		// 0毫秒后开始振动，振动100毫秒后停止，再过200毫秒后再次振动300毫秒
		long[] vibrate = { 0, 100, 200, 300 };
		notification.vibrate = vibrate;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		mNotificationManager.notify(0, notification);
	}
	
	@Override
	public void onCreate() {
		System.out.println("AlarmService onCreate");
		// 初始化音乐资源
		try {
			// 创建MediaPlayer对象
			mediaPlayer = new MediaPlayer();
			// 将音乐保存在res/raw/xingshu.mp3,R.java中自动生成{public static final int
			// xingshu=0x7f040000;}
			mediaPlayer = MediaPlayer.create(AlarmService.this, R.raw.alarm);
			// 在MediaPlayer取得播放资源与stop()之后要准备PlayBack的状态前一定要使用MediaPlayer.prepeare()
//			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		System.out.println("AlarmService onDestroy");
		// 服务停止时停止播放音乐并释放资源
		mediaPlayer.stop();
		mediaPlayer.release();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
