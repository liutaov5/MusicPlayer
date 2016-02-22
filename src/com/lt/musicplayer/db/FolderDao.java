package com.lt.musicplayer.db;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.lt.musicplayer.model.Folder;

public class FolderDao extends BaseDao<Folder>{

	Dao<Folder, Integer> folderDao;
	
	public FolderDao(Context context) {
		super(context);
		try {
			folderDao=dataBaseHelper.getDao(Folder.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Dao<Folder, Integer> getDao() {
		if(folderDao==null){
			try {
				folderDao=dataBaseHelper.getDao(Folder.class);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return folderDao;
	}

}
