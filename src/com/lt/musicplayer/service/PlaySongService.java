package com.lt.musicplayer.service;

import java.io.IOException;
import java.util.List;

import com.lt.musicplayer.R;
import com.lt.musicplayer.activity.BaseActivity;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.db.LastSongDao;
import com.lt.musicplayer.db.SongDao;
import com.lt.musicplayer.manager.SongManager;
import com.lt.musicplayer.model.LastSong;
import com.lt.musicplayer.model.Song;
import com.lt.musicplayer.utils.ToastUtils;
import com.lt.musicplayer.utils.UnitConverterUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlaySongService extends Service {

	private MediaPlayer mediaPlay = null;
	private IBinder mBinder = new MyBinder();
	public static Boolean isPause = true;
	private static Boolean isFirstPlay = false;
	private Boolean isFirstStart = true;
	private static Boolean isKeepPlay = false;
	private AudioManager mAudioManager;
	public static WindowManager mWindowManager = null;

	/**
	 * 悬浮窗是否显示
	 */
	public static Boolean isShow = false;
	/**
	 * 用于判断是否在切换界面需要隐藏悬浮窗
	 */
	public static Boolean isInside = false;

	private static View mView = null;
	private static ImageView mMusicImage;
	private static TextView mMusicName;
	private static TextView mMusicArtist;
	private static ImageView mMusicList;
	private static ImageView mMusicPlay;
	private static ImageView mMusicNext;
	private static ProgressBar mMusicProgress;

	@Override
	public void onCreate() {
		super.onCreate();
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		mWindowManager = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		mView = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.window_music_play, null);
		mMusicImage = (ImageView) mView.findViewById(R.id.iv_music_image);
		mMusicName = (TextView) mView.findViewById(R.id.tv_music_name);
		mMusicArtist = (TextView) mView.findViewById(R.id.tv_music_artist);
		mMusicList = (ImageView) mView.findViewById(R.id.iv_music_list);
		mMusicPlay = (ImageView) mView.findViewById(R.id.iv_music_play);
		mMusicNext = (ImageView) mView.findViewById(R.id.iv_music_next);
		mMusicProgress = (ProgressBar) mView
				.findViewById(R.id.pb_music_progressbar);

	}

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
				mMusicProgress.setProgress(currentPosition * 1000 / time);
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		mAudioManager.abandonAudioFocus(audioFocusChangeListener);
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
				isFirstPlay = true;
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
			if (isFirstStart) {
				isFirstStart = false;
			}

			mediaPlay.start();
			sendMusicBroadcast(MessageConstant.ACTION_PLAY);
			saveLastSong(path);
			SongManager.getInstance(this).setmCurrentUrl(path);

			// setView();
			isPause = false;
			mMusicPlay.setImageResource(R.drawable.statusbar_close);
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
			mMusicPlay.setImageResource(R.drawable.statusbar_btn_play);
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
			mMusicPlay.setImageResource(R.drawable.statusbar_close);
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
	}

	/**
	 * 停止播放
	 */
	public void stopPlay() {
		isPause = true;
		if (mediaPlay == null) {
			return;
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
			LastSongDao lastSongDao = new LastSongDao(this);
			lastSongDao.deleteAll();
			lastSongDao.createOrUpdateData(lastSong);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示悬浮窗 最下面播放界面使用的悬浮窗形式显示的
	 */
	public void showPopupWindow() {
		// setView();
		// WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		// params.type = WindowManager.LayoutParams.TYPE_PHONE;
		// params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// params.format = PixelFormat.TRANSLUCENT;
		// params.alpha = 0.9f;
		// // 获取屏幕的大小
		// Point outSize = new Point();
		// mWindowManager.getDefaultDisplay().getSize(outSize);
		// // 位置设为屏幕下方
		// params.x = 0;
		// params.y = outSize.y / 2;
		// params.width = WindowManager.LayoutParams.MATCH_PARENT;
		// params.height = UnitConverterUtils.dp2px(getApplicationContext(),
		// 65);
		// mWindowManager.addView(mView, params);
		// isShow = true;
	}

	/**
	 * 隐藏悬浮窗
	 */
	public void hidePopupWindow() {
		// if (isShow) {
		// mWindowManager.removeView(mView);
		// isShow = false;
		// }
	}

	/**
	 * 设置最下方播放条目的view
	 */
	private void setView() {

		LastSongDao lastSongDao = new LastSongDao(this);
		List<LastSong> song;
		try {
			song = lastSongDao.findAllData();
			if (song != null && song.size() > 0) {
				mMusicArtist.setText(song.get(0).getArtist());
				mMusicName.setText(song.get(0).getTitle());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (isPause) {
			mMusicPlay.setImageResource(R.drawable.statusbar_btn_play);
		} else {
			mMusicPlay.setImageResource(R.drawable.statusbar_close);
		}

	}

	private void setListener() {
		mMusicPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isPause) {
					keepPlay();
				} else {
					pauseMusic();
				}
			}
		});

		mMusicNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				playNext();
			}
		});

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

}
