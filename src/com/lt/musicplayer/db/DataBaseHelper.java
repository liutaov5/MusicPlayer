package com.lt.musicplayer.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lt.musicplayer.model.Album;
import com.lt.musicplayer.model.Artist;
import com.lt.musicplayer.model.Folder;
import com.lt.musicplayer.model.Song;

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

	private static final int DB_VERSION = 20;
	private static final String DB_NAME = "music-player.db";
	private static DataBaseHelper sDataBaseHelper;
//	private Map<String, Dao> daos = new HashMap<String, Dao>();

	private DataBaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Song.class);
			TableUtils.createTable(connectionSource, Artist.class);
			TableUtils.createTable(connectionSource, Album.class);
			TableUtils.createTable(connectionSource, Folder.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Song.class, true);
			TableUtils.dropTable(connectionSource, Artist.class, true);
			TableUtils.dropTable(connectionSource, Album.class, true);
			TableUtils.dropTable(connectionSource, Folder.class, true);
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static synchronized DataBaseHelper getHelper(Context context) {
		context = context.getApplicationContext();
		if (sDataBaseHelper == null) {
			sDataBaseHelper = new DataBaseHelper(context);
		}
		return sDataBaseHelper;
	}

//	public synchronized Dao getDao(Class clazz) throws SQLException {
//		Dao dao = null;
//		String className = clazz.getSimpleName();
//
//		if (daos.containsKey(className)) {
//			dao = daos.get(className);
//		}
//		if (dao == null) {
//			dao = super.getDao(clazz);
//			daos.put(className, dao);
//		}
//		return dao;
//	}
//
//	@Override
//	public void close() {
//		super.close();
//		for(String key:daos.keySet()){
//			Dao dao=daos.get(key);
//			dao=null;
//		}
//	}
	
	
}
