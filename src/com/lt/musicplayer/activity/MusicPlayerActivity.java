package com.lt.musicplayer.activity;

import java.util.List;

import android.R.menu;
import android.animation.FloatArrayEvaluator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lt.musicplayer.R;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.customview.CircleDrawable;
import com.lt.musicplayer.db.LastSongDao;
import com.lt.musicplayer.interfaces.OnMusicStatusChangeListener;
import com.lt.musicplayer.model.LastSong;
import com.lt.musicplayer.service.PlaySongService;
import com.lt.musicplayer.utils.MusicUtils;
import com.lt.musicplayer.utils.UnitConverterUtils;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class MusicPlayerActivity extends BaseActivity implements
		OnMusicStatusChangeListener, OnClickListener {

	private SeekBar mSeekBar;
	private Toolbar mToolBar;
	private ImageView mPlaySquence;
	private ImageView mPlayPre;
	private ImageView mPlay;
	private ImageView mPlayNext;
	private ImageView mPlayList;
	private ImageView mPlayBackground;
	private TextView mStartTime;
	private TextView mEndTime;
	private FloatingActionButton mPlayButton;
	private ImageView mAlbumImage;
	/**
	 * 用于控制进度条更新
	 */
	private Boolean isRefresh = true;

	@Override
	protected void initView() {
		// 透明状态栏，导航栏
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		setContentView(R.layout.activity_music_play);
		mSeekBar = (SeekBar) findViewById(R.id.sb_progress);
		mToolBar = (Toolbar) findViewById(R.id.toolbar);
		mPlay = (ImageView) findViewById(R.id.iv_play_pause);
		mPlayList = (ImageView) findViewById(R.id.iv_play_list);
		mPlayNext = (ImageView) findViewById(R.id.iv_play_next);
		mPlayPre = (ImageView) findViewById(R.id.iv_play_pre);
		mPlaySquence = (ImageView) findViewById(R.id.iv_play_sequence);
		mPlayBackground = (ImageView) findViewById(R.id.iv_background);
		mStartTime = (TextView) findViewById(R.id.tv_start_time);
		mEndTime = (TextView) findViewById(R.id.tv_end_time);
		mPlayButton = (FloatingActionButton) findViewById(R.id.fb_play_pause);
		mAlbumImage = (ImageView) findViewById(R.id.iv_album_image);
		setView();
	}

	@Override
	protected void initData() {
		Intent intent = getIntent();
		String name = intent.getStringExtra(MessageConstant.SONG_NAME);
		String artist = intent.getStringExtra(MessageConstant.SONG_ARTIST);
		mToolBar.setTitle(name);
		mToolBar.setSubtitle(artist);
		setSupportActionBar(mToolBar);
		mToolBar.setNavigationIcon(R.drawable.btn_back);
	}

	@Override
	protected void initListener() {
		setMusicStatusChangeListener(this);
		mPlayNext.setOnClickListener(this);
		mPlayPre.setOnClickListener(this);
		mPlay.setOnClickListener(this);
		mPlayButton.setOnClickListener(this);
		mPlayList.setOnClickListener(this);
		mPlaySquence.setOnClickListener(this);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				isRefresh = true;
				mPlayService.setProgress(seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				isRefresh = false;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mStartTime.setText(UnitConverterUtils
						.durationToString(mPlayService
								.getCurrentDuration(progress)));
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.iv_play_next:
			mPlayService.playNext();
			break;
		case R.id.iv_play_pause:
			if (PlaySongService.isPause) {
				mPlayService.keepPlay();
			} else {
				mPlayService.pauseMusic();
			}
			break;
		case R.id.fb_play_pause:
			if (PlaySongService.isPause) {
				mPlayService.keepPlay();
			} else {
				mPlayService.pauseMusic();
			}
			break;
		case R.id.iv_play_pre:
			mPlayService.playPre();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
				mToolBar.setSubtitle(song.get(0).getArtist());
				mToolBar.setTitle(song.get(0).getTitle());
				mEndTime.setText(UnitConverterUtils.durationToString((int) song
						.get(0).getDuration()));
				if(!PlaySongService.getIsFirstStart()){
					mStartTime.setText(UnitConverterUtils
							.durationToString((int) song.get(0).getProgress()));
					mSeekBar.setProgress((int) song.get(0).getProgress() * 1000
							/ (int) song.get(0).getDuration());
				}				
				// mPlayBackground.setImageBitmap(MusicUtils.getArtwork(mContext,
				// song.get(0).getId(), song.get(0).getAlbumId(), true,
				// false));
				mAlbumImage.setImageDrawable(new CircleDrawable(MusicUtils
						.getAlbumBitmap(mContext, song.get(0).getId(), song
								.get(0).getAlbumId(), true, true)));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (PlaySongService.isPause != null && PlaySongService.isPause) {
			mPlay.setImageResource(R.drawable.statusbar_btn_play);
			mPlayButton.setImageResource(R.drawable.play);
		} else {
			mPlay.setImageResource(R.drawable.statusbar_close);
			mPlayButton.setImageResource(R.drawable.play_pause);
		}

	}

	private void updateProgress(int position) {
		if (isRefresh) {
			mSeekBar.setProgress(position);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		initAlbumPicture();
		setView();
	}

	@Override
	public void onMusicPlay() {
		mPlay.setImageResource(R.drawable.statusbar_close);
		mPlayButton.setImageResource(R.drawable.play_pause);
	}

	@Override
	public void onMusicStop() {
		mPlay.setImageResource(R.drawable.statusbar_btn_play);
		mPlayButton.setImageResource(R.drawable.play);
	}

	@Override
	public void onMusicKeep() {
		mPlay.setImageResource(R.drawable.statusbar_close);
		mPlayButton.setImageResource(R.drawable.play_pause);
	}

	@Override
	public void onMusicPause() {
		mPlay.setImageResource(R.drawable.statusbar_btn_play);
		mPlayButton.setImageResource(R.drawable.play);
	}

	@Override
	public void onMusicNext() {
		setView();
	}

	@Override
	public void onMusicPre() {
		setView();
	}

	@Override
	public void onMusicUpdateProgress(int position) {
		updateProgress(position);
	}

}
