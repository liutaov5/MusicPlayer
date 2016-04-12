package com.lt.musicplayer.service;

import java.io.IOException;
import java.util.List;

import com.lt.musicplayer.R;
import com.lt.musicplayer.activity.MusicPlayerActivity;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.customview.CircleDrawable;
import com.lt.musicplayer.db.LastSongDao;
import com.lt.musicplayer.db.SongDao;
import com.lt.musicplayer.manager.SongManager;
import com.lt.musicplayer.model.LastSong;
import com.lt.musicplayer.model.Song;
import com.lt.musicplayer.receiver.MediaButtonReceiver;
import com.lt.musicplayer.utils.MusicUtils;
import com.lt.musicplayer.utils.ToastUtils;
import com.lt.musicplayer.utils.UnitConverterUtils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

public class PlaySongService extends Service {

	private MediaPlayer mediaPlay = null;
	private IBinder mBinder = new MyBinder();
	public static Boolean isPause = true;
	private static Boolean isFirstPlay = false;
	private static Boolean isFirstStart = true;
	private static Boolean isKeepPlay = false;
	private Boolean isCreateNotification = true;
	private AudioManager mAudioManager;

	private NotificationManager mNotificationManager = null;
	private RemoteViews mRemoteView = null;
	private Notification mNotification;

	@Override
	public void onCreate() {
		super.onCreate();
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// 为什么这个广播没用？
		IntentFilter filter = new IntentFilter(
				MessageConstant.ACTION_NOTIFICATION);
		// 使用LocalBroadcastManager注册接收不到广播，使用LocalBroadcastManager注册的广播，
		//您在发送广播的时候务必使用LocalBroadcastManager.sendBroadcast(intent);否则您接收不到广播.
		// LocalBroadcastManager.getInstance(this).registerReceiver(mReciver,
		// filter);
		// IntentFilter intentFilter = new IntentFilter();
		// intentFilter.addAction(MessageConstant.ACTION_NOTIFICATION);
		registerReceiver(mReciver, filter);
		// initButtonReceiver();
		ComponentName componentName=new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
		mAudioManager.registerMediaButtonEventReceiver(componentName);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// 更新底部进度条
			case 0:
				if (mediaPlay == null) {
					return;
				}

				int currentPosition = mediaPlay.getCurrentPosition();
				int time = mediaPlay.getDuration();
				LastSongDao lastSongDao = new LastSongDao(PlaySongService.this);
				List<LastSong> song;
				try {
					song = lastSongDao.findAllData();
					if (song != null && song.size() > 0) {
						song.get(0).setProgress(currentPosition);
					}
					lastSongDao.createOrUpdateDatas(song);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				Intent intent = new Intent(MessageConstant.UPDATE_PROGRESS);
				intent.putExtra(MessageConstant.CURRENT_POSITION,
						currentPosition * 1000 / time);
				LocalBroadcastManager.getInstance(PlaySongService.this)
						.sendBroadcast(intent);
				break;

			default:
				break;
			}
		}
	};

	public void stopPlayService(){
		
		mNotificationManager.cancelAll();
		stopSelf();
	}
	
	private BroadcastReceiver mReciver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MessageConstant.ACTION_NOTIFICATION)) {

				switch (intent.getStringExtra(MessageConstant.NOTIFICATION_TAG)) {
				case MessageConstant.NOTIFICATION_PLAY:
					if (isPause) {
						keepPlay();
					} else {
						pauseMusic();
					}
					break;
				case MessageConstant.NOTIFICATION_NEXT:
					playNext();
					break;
				case MessageConstant.NOTIFICATION_PRE:
					playPre();
					break;
				case MessageConstant.NOTIFICATION_CLOSE:
					mNotificationManager.cancel(1);
					isCreateNotification = true;
					break;

				default:
					break;
				}
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		mAudioManager.abandonAudioFocus(audioFocusChangeListener);
		ComponentName componentName=new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
		mAudioManager.unregisterMediaButtonEventReceiver(componentName);
		unregisterReceiver(mReciver);
		stopPlay();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class MyBinder extends Binder {
		public PlaySongService getService() {
			return PlaySongService.this;
		}
	}

	/**
	 * 播放音乐
	 * 
	 * @param path
	 *            音乐路径
	 */
	public void playMusic(String path) {
		if (mediaPlay == null) {
			mediaPlay = new MediaPlayer();
			setListener();
		}
		mediaPlay.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlay.reset();
		try {
			mediaPlay.setDataSource(path);
			mediaPlay.prepare();

			// 和其他程序处理好焦点
			int result = mAudioManager.requestAudioFocus(
					audioFocusChangeListener, AudioManager.STREAM_MUSIC,
					AudioManager.AUDIOFOCUS_GAIN);
			if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED != result) {
				Toast.makeText(this, "获取焦点失败", Toast.LENGTH_SHORT).show();
				return;
			}

			if (!isFirstPlay && isKeepPlay && !isFirstStart) {
				isKeepPlay = false;
				// 继续上次播放位置播放
				LastSongDao lastSongDao = new LastSongDao(this);
				List<LastSong> song;
				try {
					song = lastSongDao.findAllData();
					if (song != null && song.size() > 0) {
						mediaPlay.seekTo(song.get(0).getProgress());
						mHandler.sendEmptyMessage(0);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			if (isCreateNotification) {
				createNotification();
				// showButtonNotify();
				isCreateNotification = false;
			}
			if (isFirstStart) {
				isFirstStart = false;
			}
			if (!isFirstPlay) {
				isFirstPlay = true;
			}

			mediaPlay.start();
			sendMusicBroadcast(MessageConstant.ACTION_PLAY);
			saveLastSong(path);
			SongManager.getInstance(this).setmCurrentUrl(path);

			// setView();
			isPause = false;
			// mMusicPlay.setImageResource(R.drawable.statusbar_close);
			setRemoteViews();
			mRemoteView.setImageViewResource(R.id.iv_notify_play,
					R.drawable.img_button_notification_play_pause);
			mNotificationManager.notify(1, mNotification);
			// 更新进度条
			new Thread() {

				@Override
				public void run() {
					while (true) {
						try {
							sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mHandler.sendEmptyMessage(0);
					}
				}

			}.start();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 滚动进度条播发
	 * 
	 * @param position
	 */
	public void setProgress(int position) {
		mediaPlay.seekTo(position * mediaPlay.getDuration() / 1000);
	}

	/**
	 * 发送广播
	 * 
	 * @param action
	 */
	private void sendMusicBroadcast(String action) {
		Intent intent = new Intent(action);
		LocalBroadcastManager.getInstance(PlaySongService.this).sendBroadcast(
				intent);
	}

	/**
	 * 暂停播放
	 */
	public void pauseMusic() {
		if (mediaPlay == null) {
			return;
		}
		sendMusicBroadcast(MessageConstant.ACTION_PAUSE);
		if (!isPause && mediaPlay.isPlaying()) {
			mediaPlay.pause();
			isPause = true;
			// mMusicPlay.setImageResource(R.drawable.statusbar_btn_play);
			mRemoteView.setImageViewResource(R.id.iv_notify_play,
					R.drawable.img_button_notification_play_play);
			mNotificationManager.notify(1, mNotification);
		}
	}

	/**
	 * 继续播放
	 */
	public void keepPlay() {
		isKeepPlay = true;
		if (!isFirstPlay) {
			LastSongDao lastSongDao = new LastSongDao(this);
			List<LastSong> song;
			try {
				song = lastSongDao.findAllData();
				if (song != null && song.size() > 0) {
					playMusic(song.get(0).getUrl());
				} else {
					ToastUtils.show(this, "请选择音乐");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return;
		}
		if (mediaPlay == null) {
			return;
		}
		sendMusicBroadcast(MessageConstant.ACTION_KEEP_PLAY);
		if (isPause && !mediaPlay.isPlaying()) {
			mediaPlay.start();
			isPause = false;
			// mMusicPlay.setImageResource(R.drawable.statusbar_close);
			mRemoteView.setImageViewResource(R.id.iv_notify_play,
					R.drawable.img_button_notification_play_pause);
			mNotificationManager.notify(1, mNotification);
		}
	}

	/**
	 * 播放下一首
	 */
	public void playNext() {
		if (mediaPlay == null) {
			return;
		}
		sendMusicBroadcast(MessageConstant.ACTION_NEXT_PLAY);
		String path = SongManager.getInstance(this).getNextSongUrl();
		playMusic(path);
	}

	/**
	 * 播放前一首
	 */
	public void playPre() {
		if (mediaPlay == null) {
			return;
		}
		sendMusicBroadcast(MessageConstant.ACTION_PRE_PLAY);
		String path = SongManager.getInstance(this).getPreSongUrl();
		playMusic(path);
	}

	/**
	 * 停止播放
	 */
	public void stopPlay() {
		isPause = true;
		if (mediaPlay == null) {
			return;
		}
		mRemoteView.setImageViewResource(R.id.iv_notify_play,
				R.drawable.img_button_notification_play_play);
		if (!isCreateNotification) {
			mNotificationManager.notify(1, mNotification);
		}
		sendMusicBroadcast(MessageConstant.ACTION_STOP);
		if (mediaPlay.isPlaying()) {
			pauseMusic();
			int time = mediaPlay.getCurrentPosition();
			LastSongDao lastSongDao = new LastSongDao(this);
			List<LastSong> song;
			try {
				song = lastSongDao.findAllData();
				if (song != null && song.size() > 0) {
					song.get(0).setProgress(time);
				}
				lastSongDao.createOrUpdateDatas(song);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			mediaPlay.stop();
		}
		mediaPlay.reset();
		mediaPlay.release();
		mediaPlay = null;
		isFirstPlay = false;
	}

	/**
	 * 保存最后播放的一首歌的信息到数据库
	 * 
	 * @param path
	 */
	private void saveLastSong(String path) {
		SongDao songDao = new SongDao(this);
		try {
			List<Song> songs = songDao.findData("url", path);
			LastSong lastSong = new LastSong();
			lastSong.setTitle(songs.get(0).getTitle());
			lastSong.setArtist(songs.get(0).getArtist());
			lastSong.setAlbum(songs.get(0).getAlbum());
			lastSong.setId(songs.get(0).getId());
			lastSong.setUrl(songs.get(0).getUrl());
			lastSong.setDuration(songs.get(0).getDuration());
			LastSongDao lastSongDao = new LastSongDao(this);
			lastSongDao.deleteAll();
			lastSongDao.createOrUpdateData(lastSong);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setListener() {

		mediaPlay.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				playNext();
			}
		});

		mediaPlay.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				playNext();
				return false;
			}
		});
	}

	AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			/**
			 * AUDIOFOCUS_GAIN：获得音频焦点。
			 * AUDIOFOCUS_LOSS：失去音频焦点，并且会持续很长时间。这是我们需要停止MediaPlayer的播放。
			 * AUDIOFOCUS_LOSS_TRANSIENT
			 * ：失去音频焦点，但并不会持续很长时间，需要暂停MediaPlayer的播放，等待重新获得音频焦点。
			 * AUDIOFOCUS_REQUEST_GRANTED 永久获取媒体焦点（播放音乐）
			 * AUDIOFOCUS_GAIN_TRANSIENT 暂时获取焦点 适用于短暂的音频
			 * AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK 我们应用跟其他应用共用焦点 我们播放的时候其他音频会降低音量
			 */

			switch (focusChange) {
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				pauseMusic();
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				if (mediaPlay != null) {
					mediaPlay.setVolume(0.5f, 0.5f);
				}
				break;
			case AudioManager.AUDIOFOCUS_GAIN:
				if (mediaPlay != null) {
					mediaPlay.setVolume(1.0f, 1.0f);
				}
				break;
			case AudioManager.AUDIOFOCUS_LOSS:
				stopPlay();
				break;
			}

		}
	};

	/**
	 * 获取当前播发音乐时长
	 * 
	 * @return
	 */
	public int getDuration() {
		if (mediaPlay == null) {
			return -1;
		}
		return mediaPlay.getDuration();
	}

	/**
	 * 获取当前进度时间
	 * 
	 * @param position
	 *            进度条位置
	 * @return
	 */
	public int getCurrentDuration(int position) {
		if (mediaPlay == null) {
			return -1;
		}
		return position * mediaPlay.getDuration() / 1000;
	}

	public static Boolean getIsFirstStart() {
		return isFirstStart;
	}

	private void createNotification() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this);
		mRemoteView = new RemoteViews(getPackageName(),
				R.layout.notification_view);
		setRemoteViews();

		Intent intent = new Intent(MessageConstant.ACTION_NOTIFICATION);
		intent.putExtra(MessageConstant.NOTIFICATION_TAG,
				MessageConstant.NOTIFICATION_PLAY);
		PendingIntent playIntent = PendingIntent.getBroadcast(this, 1, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteView.setOnClickPendingIntent(R.id.iv_notify_play, playIntent);

		intent.putExtra(MessageConstant.NOTIFICATION_TAG,
				MessageConstant.NOTIFICATION_NEXT);
		PendingIntent nextIntent = PendingIntent.getBroadcast(this, 2, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteView.setOnClickPendingIntent(R.id.iv_notify_next, nextIntent);

		intent.putExtra(MessageConstant.NOTIFICATION_TAG,
				MessageConstant.NOTIFICATION_CLOSE);
		PendingIntent closeIntent = PendingIntent.getBroadcast(this, 3, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteView.setOnClickPendingIntent(R.id.iv_notify_cancel, closeIntent);

		Intent intent2 = new Intent(this, MusicPlayerActivity.class);
		intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 4,
				intent2, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder.setContent(mRemoteView).setOngoing(true)
				.setSmallIcon(R.drawable.lt_icon)
				.setContentIntent(pendingIntent);
		mNotification = mBuilder.build();
		mNotificationManager.notify(1, mNotification);
	}

	private void setRemoteViews() {
		LastSong song = SongManager.getInstance(this).getLastSong();
		mRemoteView.setTextViewText(R.id.tv_music_name, song.getTitle());
		mRemoteView.setTextViewText(R.id.tv_music_artist, song.getArtist());
		CircleDrawable circleDrawable = new CircleDrawable(
				MusicUtils.getAlbumBitmap(this, song.getId(),
						song.getAlbumId(), true, true));
		mRemoteView.setImageViewBitmap(R.id.iv_music_image,
				UnitConverterUtils.drawableToBitmap(circleDrawable));
	}

	// NotificationCompat.Builder mBuilder;
//	public ButtonBroadcastReceiver bReceiver;
//
//	public void initButtonReceiver() {
//		bReceiver = new ButtonBroadcastReceiver();
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(MessageConstant.ACTION_NOTIFICATION);
//		registerReceiver(mReciver, intentFilter);
//	}
//
//	public class ButtonBroadcastReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals(MessageConstant.ACTION_NOTIFICATION)) {
//				switch (intent.getStringExtra(MessageConstant.NOTIFICATION_TAG)) {
//				case MessageConstant.NOTIFICATION_PLAY:
//					if (isPause) {
//						keepPlay();
//					} else {
//						pauseMusic();
//					}
//					break;
//				case MessageConstant.NOTIFICATION_NEXT:
//					playNext();
//					break;
//				case MessageConstant.NOTIFICATION_CLOSE:
//					mNotificationManager.cancel(1);
//					isCreateNotification = true;
//					stopPlay();
//					break;
//				default:
//					break;
//				}
//			}
//		}
//	}
}
