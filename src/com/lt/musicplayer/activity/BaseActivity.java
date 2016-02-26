package com.lt.musicplayer.activity;

import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.service.PlaySongService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

/**
 * 
 * @author taoliu
 *	date: 15/11/3
 */
public abstract class BaseActivity extends AppCompatActivity {

	public static List<Activity> mActivityList = new ArrayList<Activity>();
	protected PlaySongService mPlayService;
	protected ServiceConnection playConn=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			PlaySongService.MyBinder binder=(PlaySongService.MyBinder)service;
			mPlayService=binder.getService();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivityList.add(this);
		Intent intent=new Intent(this, PlaySongService.class);
		startService(intent);
		bindService(intent, playConn, Context.BIND_AUTO_CREATE);
		initView();
		initData();
		initListener();
	}

	public void closeActivity() {
		finish();
	}

	@Override
	public void onBackPressed() {
		closeActivity();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(playConn);
		mActivityList.remove(this);
	}

	public static void closeApp() {
		for (Activity activity : mActivityList) {
			if (activity != null) {
				activity.finish();
			}
		}
		System.exit(0);
	}

	protected abstract void initView();

	protected abstract void initData();

	protected abstract void initListener();

}
