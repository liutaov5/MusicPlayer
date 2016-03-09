package com.lt.musicplayer.db;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.lt.musicplayer.model.LastSong;

public class LastSongDao extends BaseDao<LastSong>{

	private Dao<LastSong, Integer> lastSongDao;
	
	public LastSongDao(Context context) {
		super(context);
		try {
			lastSongDao=dataBaseHelper.getDao(LastSong.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Dao<LastSong, Integer> getDao() {
		if(lastSongDao==null){
			try {
				lastSongDao=dataBaseHelper.getDao(LastSong.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return lastSongDao;
	}

}
