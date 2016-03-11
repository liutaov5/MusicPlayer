package com.lt.musicplayer.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.db.LastSongDao;
import com.lt.musicplayer.db.SongDao;
import com.lt.musicplayer.model.LastSong;
import com.lt.musicplayer.model.Song;
import com.lt.musicplayer.utils.ToastUtils;

import android.content.Context;
import android.util.SparseArray;

public class SongManager {

	private SparseArray<Song> mSongMap;
	private List<Song> mSongList;
	private static SongManager sSongManager;
	private SongDao mSongDao;
	private Context mContext;
	private String mCurrentUrl;

	private SongManager(Context context) {
		mContext = context;
		mSongMap = new SparseArray<Song>();
		mSongList = new ArrayList<Song>();
		mSongDao = new SongDao(mContext);
	}

	public static SongManager getInstance(Context context) {
		if (sSongManager == null) {
			sSongManager = new SongManager(context);
		}
		return sSongManager;
	}

	public void addSong(Song song) {
		mSongMap.put(song.getId(), song);
		mSongList.add(song);
	}

	public void clearSong() {
		mSongMap.clear();
		mSongList.clear();
	}

	public SparseArray<Song> getmSongMap() {
		return mSongMap;
	}

	public void setmSongMap(SparseArray<Song> mSongMap) {
		this.mSongMap = mSongMap;
	}

	public List<Song> getmSongList() {
		return mSongList;
	}

	public void setmSongList(List<Song> mSongList) {
		this.mSongList = mSongList;
	}

	public void saveAllSong() {
		try {
			mSongDao.createOrUpdateDatas(mSongList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Song> getAllSong() {
		if (mSongList.size() == 0) {
			try {
				mSongList.addAll(mSongDao.findAllData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (mSongMap.size() == 0) {
			for (Song song : mSongList) {
				mSongMap.put(song.getId(), song);
			}
		}
		return mSongList;
	}

	/**
	 * 获取下一首歌的路径
	 * @return
	 */
	public String getNextSongUrl() {

		if (mSongList.size() == 0) {
			return null;
		}

		for (int i = 0; i < mSongList.size(); i++) {
			if (mCurrentUrl.equals(mSongList.get(i).getUrl())) {
				return i + 1 == mSongList.size() ? mSongList.get(0).getUrl()
						: mSongList.get(i + 1).getUrl();
			}
		}

		return mSongList.get(0).getUrl();
	}
	/**
	 * 获取前一首歌的路径
	 * @return
	 */
	public String getPreSongUrl(){
		if (mSongList.size() == 0) {
			return null;
		}

		for (int i = 0; i < mSongList.size(); i++) {
			if (mCurrentUrl.equals(mSongList.get(i).getUrl())) {
				return i  == 0 ? mSongList.get(mSongList.size()-1).getUrl()
						: mSongList.get(i - 1).getUrl();
			}
		}

		return mSongList.get(0).getUrl();
	}

	public List<String> searchArtist() {
		return mSongDao.searchColumn("artist");
	}

	public List<String> searchAlbum() {
		return mSongDao.searchColumn("album");
	}

	public List<String> searchUrl() {
		return mSongDao.searchColumn("url");
	}

	public String getmCurrentUrl() {
		return mCurrentUrl;
	}

	public void setmCurrentUrl(String mCurrentUrl) {
		this.mCurrentUrl = mCurrentUrl;
	}
	
	/**
	 * 获取当前的歌曲
	 * @return
	 */
	public  LastSong getLastSong(){
		LastSongDao lastSongDao = new LastSongDao(mContext);
		List<LastSong> song;
		try {
			song = lastSongDao.findAllData();
			if (song != null && song.size() > 0) {
				return song.get(0);
			} else {
				return null;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
	}

}
