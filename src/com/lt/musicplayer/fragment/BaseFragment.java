package com.lt.musicplayer.fragment;

import com.lt.musicplayer.db.AlbumDao;
import com.lt.musicplayer.model.Album;
import com.lt.musicplayer.service.PlaySongService;
import com.lt.musicplayer.service.ScanSongService;
import com.lt.musicplayer.utils.MusicUtils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

public abstract class BaseFragment extends Fragment{

	protected PlaySongService mPlayService;
	protected ServiceConnection playConn=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mPlayService=null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			PlaySongService.MyBinder binder=(PlaySongService.MyBinder)service;
			mPlayService=binder.getService();
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter filter = new IntentFilter(
				ScanSongService.ACTION_SCAN_FINISH);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				mReceiver, filter);
		Intent intent=new Intent(getActivity(), PlaySongService.class);
		getActivity().startService(intent);
		getActivity().bindService(intent, playConn, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * 接收扫描完成后发送的广播
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			updateList();
			AlbumDao dao=new AlbumDao(getActivity());
			try {
				for(Album album:dao.findAllData()){
					MusicUtils.getAlbumBitmap(getActivity(), album.getId(), album.getAlbumId(), true, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
		getActivity().unbindService(playConn);
	}
	
	/**
	 * 列表更新
	 */
	protected abstract void updateList();

	@Override
	public void startActivity(Intent intent) {
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        super.startActivity(intent);
	}
	
}
