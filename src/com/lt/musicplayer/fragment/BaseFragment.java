package com.lt.musicplayer.fragment;

import com.lt.musicplayer.service.ScanSongService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

public abstract class BaseFragment extends Fragment{

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(
				ScanSongService.ACTION_SCAN_FINISH);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mReceiver, filter);
	}
	
	/**
	 * 接收扫描完成后发送的广播
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			updateList();
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
	}
	
	/**
	 * 列表更新
	 */
	protected abstract void updateList();
	
}
