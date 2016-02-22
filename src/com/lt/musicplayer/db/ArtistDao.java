package com.lt.musicplayer.db;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.lt.musicplayer.model.Artist;

public class ArtistDao extends BaseDao<Artist> {

	Dao<Artist, Integer> artistDao;

	public ArtistDao(Context context) {
		super(context);
		try {
			artistDao = dataBaseHelper.getDao(Artist.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Dao<Artist, Integer> getDao() {
		if (artistDao == null) {
			try {
				artistDao = dataBaseHelper.getDao(Artist.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return artistDao;
	}
}
