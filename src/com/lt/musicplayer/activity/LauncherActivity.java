package com.lt.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.lt.musicplayer.R;
import com.lt.musicplayer.utils.MusicUtils;

public class LauncherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_launcher);
		if(!MusicUtils.getIsLoading()){
			mHandler.sendEmptyMessage(0);
			return;
		}
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					sleep(2000);
					MusicUtils.setIsLoading(false);
					mHandler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
				}
			}

		}.start();

	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}

	};

	// @Override
	// protected void initView() {
	// setContentView(R.layout.activity_launcher);
	// }
	//
	// @Override
	// protected void initData() {
	//
	// try {
	// Thread.sleep(0);
	// Intent intent=new Intent(this, MainActivity.class);
	// // startActivity(intent);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	//
	//
	// @Override
	// protected void initListener() {
	//
	// }
	//
	// @Override
	// protected void onPostCreate(Bundle savedInstanceState) {
	// super.onPostCreate(savedInstanceState);
	// initAlbumPicture();
	// }

}
