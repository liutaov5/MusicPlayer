package com.lt.musicplayer.activity;

import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.R;
import com.lt.musicplayer.adapter.MusicAdapter;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.db.SongDao;
import com.lt.musicplayer.model.Song;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

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
	}

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

}
