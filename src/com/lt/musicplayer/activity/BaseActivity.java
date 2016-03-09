package com.lt.musicplayer.activity;

import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.R;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.db.AlbumDao;
import com.lt.musicplayer.db.LastSongDao;
import com.lt.musicplayer.model.Album;
import com.lt.musicplayer.model.LastSong;
import com.lt.musicplayer.service.PlaySongService;
import com.lt.musicplayer.utils.MusicUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * activity基类
 * 
 * @author taoliu date: 15/11/3
 */
public abstract class BaseActivity extends AppCompatActivity {

	public static List<Activity> mActivityList = new ArrayList<Activity>();
	protected Context mContext;
	// protected static WindowManager mWindowManager = null;
	// /**
	// * 悬浮窗是否显示
	// */
	// protected static Boolean isShow = false;
	// /**
	// * 用于判断是否在切换界面需要隐藏悬浮窗
	// */
	// public static Boolean isInside=false;
	//
	// protected static View mView = null;
	protected ImageView mMusicImage;
	protected TextView mMusicName;
	protected TextView mMusicArtist;
	protected ImageView mMusicMore;
	protected ImageView mMusicPlay;
	protected ImageView mMusicNext;
	protected ProgressBar mMusicProgress;
	/**
	 * 播放服务
	 */
	protected PlaySongService mPlayService;
	protected ServiceConnection playConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mPlayService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			PlaySongService.MyBinder binder = (PlaySongService.MyBinder) service;
			mPlayService = binder.getService();
			if (!PlaySongService.isShow) {
				if (mPlayService != null) {
					mPlayService.showPopupWindow();
				} else {
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		mActivityList.add(this);
		mContext = this;
		
		
		initData();
		initListener();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// 启动和绑定服务
				Intent intent = new Intent(this, PlaySongService.class);
				startService(intent);
				bindService(intent, playConn, Context.BIND_AUTO_CREATE);

				// mWindowManager = (WindowManager) getApplicationContext()
				// .getSystemService(Context.WINDOW_SERVICE);
				// mView = LayoutInflater.from(getApplicationContext()).inflate(
				// R.layout.window_music_play, null);

				IntentFilter filter = new IntentFilter(MessageConstant.UPDATE_PROGRESS);
				IntentFilter filterPlay = new IntentFilter(MessageConstant.ACTION_PLAY);
				IntentFilter filterPause = new IntentFilter(
						MessageConstant.ACTION_PAUSE);
				IntentFilter filterNext = new IntentFilter(
						MessageConstant.ACTION_NEXT_PLAY);
				IntentFilter filterPre = new IntentFilter(
						MessageConstant.ACTION_PRE_PLAY);
				IntentFilter filterStop = new IntentFilter(MessageConstant.ACTION_STOP);
				IntentFilter filterKeep = new IntentFilter(
						MessageConstant.ACTION_KEEP_PLAY);
				LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
						filter);
				LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
						filterPlay);
				LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
						filterPause);
				LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
						filterNext);
				LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
						filterPre);
				LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
						filterStop);
				LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
						filterKeep);
	}
	
	/**
	 * 用于更新悬浮窗
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case MessageConstant.UPDATE_PROGRESS:
				int currentPosition = intent.getIntExtra(
						MessageConstant.CURRENT_POSITION, 0);
				// setView();
				updateProgress(currentPosition);
				break;
			case MessageConstant.ACTION_PLAY:
				mMusicPlay.setImageResource(R.drawable.statusbar_close);
				setView();
				break;
			case MessageConstant.ACTION_PAUSE:
				mMusicPlay.setImageResource(R.drawable.statusbar_btn_play);
				break;
			case MessageConstant.ACTION_NEXT_PLAY:
				setView();
				break;
			case MessageConstant.ACTION_PRE_PLAY:
				setView();
				break;
			case MessageConstant.ACTION_STOP:
				mMusicPlay.setImageResource(R.drawable.statusbar_btn_play);
				break;
			case MessageConstant.ACTION_KEEP_PLAY:
				mMusicPlay.setImageResource(R.drawable.statusbar_close);
				break;
			default:
				break;
			}
		}
	};

	public void closeActivity() {
		finish();
	}

	@Override
	public void onBackPressed() {
		closeActivity();
	}

	@Override
	public void startActivity(Intent intent) {
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		super.startActivity(intent);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(playConn);
		mActivityList.remove(this);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
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

	// /**
	// * 显示悬浮窗
	// * 最下面播放界面使用的悬浮窗形式显示的
	// */
	// protected void showPopupWindow() {
	// setView();
	// WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	// params.type = WindowManager.LayoutParams.TYPE_PHONE;
	// params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
	// params.format = PixelFormat.TRANSLUCENT;
	// params.alpha = 0.9f;
	// //获取屏幕的大小
	// Point outSize=new Point();
	// mWindowManager.getDefaultDisplay().getSize(outSize);
	// //位置设为屏幕下方
	// params.x=0;
	// params.y=outSize.y/2;
	// params.width = WindowManager.LayoutParams.MATCH_PARENT;
	// params.height = UnitConverterUtil.dp2px(getApplicationContext(), 65);
	// mWindowManager.addView(mView, params);
	// isShow=true;
	// }
	// /**
	// * 隐藏悬浮窗
	// */
	// protected void hidePopupWindow(){
	// if(PlaySongService.isShow){
	// PlaySongService.mWindowManager.removeView(mView);
	// PlaySongService.isShow=false;
	// }
	// }

	@Override
	protected void onPause() {
		super.onPause();
		// if(PlaySongService.isInside){
		// PlaySongService.isInside=false;
		// }else{
		// mPlayService.hidePopupWindow();
		// }

	}

	/**
	 * 设置最下方播放条目的view
	 */
	protected void setView() {

		LastSongDao lastSongDao = new LastSongDao(this);
		List<LastSong> song;
		try {
			song = lastSongDao.findAllData();
			if (song != null && song.size() > 0) {
				mMusicArtist.setText(song.get(0).getArtist());
				mMusicName.setText(song.get(0).getTitle());
				mMusicImage.setImageBitmap(MusicUtils.getAlbumBitmap(mContext,
						song.get(0).getId(), song.get(0).getAlbumId(), true,
						true));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//
		if (mPlayService.isPause != null && mPlayService.isPause) {
			mMusicPlay.setImageResource(R.drawable.statusbar_btn_play);
		} else {
			mMusicPlay.setImageResource(R.drawable.statusbar_close);
		}

	}

	private void updateProgress(int position) {
		mMusicProgress.setProgress(position);
	}

	protected void setListener() {
		mMusicPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPlayService.isPause) {
					mPlayService.keepPlay();
				} else {
					mPlayService.pauseMusic();
				}
			}
		});

		mMusicNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPlayService.playNext();
			}
		});
	}

	protected void initAlbumPicture() {
		AlbumDao dao = new AlbumDao(mContext);
		try {
			for (Album album : dao.findAllData()) {
				MusicUtils.getAlbumBitmap(mContext, album.getId(),
						album.getAlbumId(), true, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
}
