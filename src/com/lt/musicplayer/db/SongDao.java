package com.lt.musicplayer.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.lt.musicplayer.model.Song;

import android.content.Context;

public class SongDao extends BaseDao<Song>{

	private Dao<Song, Integer> songDao;

	public SongDao(Context context) {
		super(context);
		try {
			songDao = dataBaseHelper.getDao(Song.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询一列不同的数据
	 * @param column
	 * @return
	 */
	public List<String> searchColumn(String column) {
		List<String> list = new ArrayList<String>();
		try {
			GenericRawResults<String[]> rawResults = songDao.queryRaw("select distinct " + column
					+ " from tb_song");
			List<String[]> results = rawResults.getResults();
			for (String[] resultArray : results) {
				list.add(resultArray[0]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	protected Dao<Song, Integer> getDao() {
		if(songDao==null){
			try {
				songDao = dataBaseHelper.getDao(Song.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return this.songDao;
	}

}
