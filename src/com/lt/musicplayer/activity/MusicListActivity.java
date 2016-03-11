package com.lt.musicplayer.activity;

import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.R;
import com.lt.musicplayer.adapter.MusicAdapter;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.db.LastSongDao;
import com.lt.musicplayer.db.SongDao;
import com.lt.musicplayer.interfaces.OnMusicStatusChangeListener;
import com.lt.musicplayer.manager.SongManager;
import com.lt.musicplayer.model.LastSong;
import com.lt.musicplayer.model.Song;
import com.lt.musicplayer.service.PlaySongService;
import com.lt.musicplayer.utils.MusicUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author taoliu create at Jan 21, 2016
 */
public class MusicListActivity extends BaseActivity implements
		OnMusicStatusChangeListener, OnClickListener {

	private ListView mMusicList;
	private Toolbar mToolbar;
	private MusicAdapter mAdapter;
	private List<Song> mData;
	private SongDao mSongDao;

	private ImageView mMusicImage;
	private TextView mMusicName;
	private TextView mMusicArtist;
	private ImageView mMusicMore;
	private ImageView mMusicPlay;
	private ImageView mMusicNext;
	private ProgressBar mMusicProgress;
	private RelativeLayout mBottomView;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_music_list);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mMusicList = (ListView) findViewById(R.id.lv_music_list);
		mMusicImage = (ImageView) findViewById(R.id.iv_music_image);
		mMusicName = (TextView) findViewById(R.id.tv_music_name);
		mMusicArtist = (TextView) findViewById(R.id.tv_music_artist);
		mMusicMore = (ImageView) findViewById(R.id.iv_music_list);
		mMusicPlay = (ImageView) findViewById(R.id.iv_music_play);
		mMusicNext = (ImageView) findViewById(R.id.iv_music_next);
		mMusicProgress = (ProgressBar) findViewById(R.id.pb_music_progressbar);
		mBottomView = (RelativeLayout) findViewById(R.id.rl_bottom_view);
		setView();
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// if (!PlaySongService.isShow) {
	// if(mPlayService!=null){
	// mPlayService.showPopupWindow();
	// }else{
	// }
	// }
	// }

	@Override
	protected void initData() {
		mData = new ArrayList<Song>();
		mSongDao = new SongDao(this);
		Intent intent = getIntent();
		mToolbar.setTitle(intent.getStringExtra(MessageConstant.MUSIC_TITLE));
		setSupportActionBar(mToolbar);
		mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
		mToolbar.setNavigationIcon(R.drawable.btn_back);
		try {
			mData.addAll(mSongDao.findData(
					intent.getStringExtra(MessageConstant.SEND_TYPE),
					intent.getStringExtra(MessageConstant.SEND_CONTENT)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdapter = new MusicAdapter(this, mData, R.layout.item_list_music);
		mMusicList.setAdapter(mAdapter);
	}

	@Override
	protected void initListener() {
		setMusicStatusChangeListener(this);
		mMusicPlay.setOnClickListener(this);
		mMusicNext.setOnClickListener(this);
		mMusicMore.setOnClickListener(this);
		mBottomView.setOnClickListener(this);
		mMusicList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPlayService.playMusic(mData.get(position).getUrl());
				SongManager songManager = SongManager
						.getInstance(MusicListActivity.this);
				songManager.clearSong();
				for (Song song : mData) {
					songManager.addSong(song);
				}
			}
		});
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return false;
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
				mMusicImage.setImageBitmap(MusicUtils.getAlbumBitmap(mContext,
						song.get(0).getId(), song.get(0).getAlbumId(), true,
						true));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (PlaySongService.isPause != null && PlaySongService.isPause) {
			mMusicPlay.setImageResource(R.drawable.img_button_notification_play_play);
		} else {
			mMusicPlay.setImageResource(R.drawable.img_button_notification_play_pause);
		}

	}

	private void updateProgress(int position) {
		mMusicProgress.setProgress(position);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		initAlbumPicture();
		setView();
	}

	@Override
	public void onMusicPlay() {
		mMusicPlay.setImageResource(R.drawable.img_button_notification_play_pause);
		setView();
	}

	@Override
	public void onMusicStop() {
		mMusicPlay.setImageResource(R.drawable.img_button_notification_play_play);
	}

	@Override
	public void onMusicKeep() {
		mMusicPlay.setImageResource(R.drawable.img_button_notification_play_pause);
	}

	@Override
	public void onMusicPause() {
		mMusicPlay.setImageResource(R.drawable.img_button_notification_play_play);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_music_next:
			mPlayService.playNext();
			break;
		case R.id.iv_music_play:
			if (PlaySongService.isPause) {
				mPlayService.keepPlay();
			} else {
				mPlayService.pauseMusic();
			}
			break;
		case R.id.rl_bottom_view:
			Intent intent = new Intent(mContext, MusicPlayerActivity.class);
			intent.putExtra(MessageConstant.SONG_NAME, mMusicName.getText());
			intent.putExtra(MessageConstant.SONG_ARTIST, mMusicArtist.getText());
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
