package com.lt.musicplayer.db;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.lt.musicplayer.model.Album;

public class AlbumDao extends BaseDao<Album>{

	Dao<Album, Integer> albumDao;
	
	public AlbumDao(Context context) {
		super(context);
		try {
			albumDao=dataBaseHelper.getDao(Album.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Dao<Album, Integer> getDao() {
		if(albumDao==null){
			try {
				albumDao=dataBaseHelper.getDao(Album.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return albumDao;
	}

}
