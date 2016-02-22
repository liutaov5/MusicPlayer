package com.lt.musicplayer.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.db.SongDao;
import com.lt.musicplayer.model.Song;

import android.content.Context;
import android.util.SparseArray;

public class SongManager {

	private SparseArray<Song> mSongMap;
	private List<Song> mSongList;
	private static SongManager sSongManager;
	private SongDao mSongDao;
	private Context mContext;
	
	private SongManager(Context context){
		mContext=context;
		mSongMap=new SparseArray<Song>();
		mSongList=new ArrayList<Song>();
		mSongDao=new SongDao(mContext);
	}
	
	public static SongManager getInstance(Context context){
		if(sSongManager==null){
			sSongManager=new SongManager(context);
		}
		return sSongManager;
	}
	
	public void addSong(Song song){
		mSongMap.put(song.getId(), song);
		mSongList.add(song);
	}
	
	public void clearSong(){
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
	
	public void saveAllSong(){
		try {
			mSongDao.createOrUpdateDatas(mSongList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Song> getAllSong(){
		if(mSongList.size()==0){
			try {
				mSongList.addAll(mSongDao.findAllData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		if(mSongMap.size()==0){
			for(Song song:mSongList){
				mSongMap.put(song.getId(), song);
			}
		}
		return mSongList;
	}
	
	public List<String> searchArtist(){
		return mSongDao.searchColumn("artist");
	}
	
	public List<String> searchAlbum(){
		return mSongDao.searchColumn("album");
	}
	
	public List<String> searchUrl(){
		return mSongDao.searchColumn("url");
	}
	
}
