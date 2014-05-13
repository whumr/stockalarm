package com.mr.stockalarm.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.mr.stockalarm.R;

public class AlarmService extends Service {

	private MediaPlayer mediaPlayer;

	@Override
	public void onStart(Intent intent, int startId) {
		System.out.println("AlarmService onStart...");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("AlarmService onStartCommand...");
		System.out.println(mediaPlayer == null);
		// 开始播放音乐
		mediaPlayer.start();
		// 音乐播放完毕的事件处理
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mediaPlayer) {
				// 循环播放
				try {
					mediaPlayer.start();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
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
