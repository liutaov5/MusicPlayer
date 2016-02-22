package com.lt.musicplayer.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * 
 * @author taoliu
 *	date: 15/11/3
 */
public abstract class BaseActivity extends AppCompatActivity {

	public static List<Activity> mActivityList = new ArrayList<Activity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivityList.add(this);
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
