package com.lt.musicplayer.activity;

import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.R;
import com.lt.musicplayer.adapter.MusicAdapter;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.db.SongDao;
import com.lt.musicplayer.manager.SongManager;
import com.lt.musicplayer.model.Song;
import com.lt.musicplayer.service.PlaySongService;
import com.lt.musicplayer.utils.ToastUtils;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author taoliu create at Jan 21, 2016
 */
public class MusicActivity extends BaseActivity {

	private ListView mMusicList;
	private Toolbar mToolbar;
	private MusicAdapter mAdapter;
	private List<Song> mData;
	private SongDao mSongDao;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_music);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mMusicList = (ListView) findViewById(R.id.lv_music_list);
		mMusicImage = (ImageView) findViewById(R.id.iv_music_image);
		mMusicName = (TextView) findViewById(R.id.tv_music_name);
		mMusicArtist = (TextView) findViewById(R.id.tv_music_artist);
		mMusicMore= (ImageView) findViewById(R.id.iv_music_list);
		mMusicPlay = (ImageView) findViewById(R.id.iv_music_play);
		mMusicNext = (ImageView) findViewById(R.id.iv_music_next);
		mMusicProgress = (ProgressBar) findViewById(R.id.pb_music_progressbar);
		setView();
	}

//	@Override
//	protected void onResume() {
//		super.onResume();
//		if (!PlaySongService.isShow) {
//			if(mPlayService!=null){
//				mPlayService.showPopupWindow();
//			}else{
//			}
//		}
//	}

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
		setListener();
		mMusicList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPlayService.playMusic(mData.get(position).getUrl());
				SongManager songManager=SongManager.getInstance(MusicActivity.this);
				songManager.clearSong();
				for(Song song:mData){
					songManager.addSong(song);
				}	
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		if (id == android.R.id.home) {
			PlaySongService.isInside=true;
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			PlaySongService.isInside=true;
			finish();
		}
		return false;
	}

}
