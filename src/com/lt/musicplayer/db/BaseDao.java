package com.lt.musicplayer.db;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * 
 * @author taoliu
 * create at Jan 20, 2016
 */
public abstract class BaseDao<T> {

	protected Context context;
	protected DataBaseHelper dataBaseHelper;
	
	public BaseDao(Context context) {
		this.context = context;
		dataBaseHelper = DataBaseHelper.getHelper(this.context);
	}
	
	protected abstract Dao<T, Integer> getDao();
	
	/**
	   * 增加或更新记录
	   * 
	   * @param t
	   * @throws SQLException
	   */
	  public void createOrUpdateData(T t) throws SQLException {
	    try {
	      getDao().createOrUpdate(t);
	    } catch (java.sql.SQLException e) {
	    	e.printStackTrace();
	    }
	  }

	  /**
	   * 批量增加数据
	   * 
	   * @param dataDao
	   * @param dataList
	   * @throws SQLException
	   */
	  public void createOrUpdateDatas(List<T> dataList) throws SQLException {
	    AndroidDatabaseConnection db = null;
	    try {
	      // 事务
	      db = new AndroidDatabaseConnection(dataBaseHelper.getWritableDatabase(), true);
	      db.setAutoCommit(false);
	      // 数据库操作
	      for (T t : dataList) {
	        getDao().createOrUpdate(t);
	      }
	      // 提交
	      db.commit(null);
	    } catch (java.sql.SQLException e) {
	      db.rollback(null);
	      e.printStackTrace();
	    }
	  }

	  /**
	   * 获取所有记录
	   * 
	   * @param dataDao
	   * @return
	   * @throws Exception
	   */
	  public List<T> findAllData() throws Exception {
	    return getDao().queryForAll();
	  }

	  /**
	   * 获取记录
	   * 
	   * @param dataDao
	   * @return
	   * @throws Exception
	   */
	  public List<T> findData(String columnName, String columnValue) throws Exception {
	    List<T> result = null;
	    try {
	      QueryBuilder<T, Integer> queryBuilder = getDao().queryBuilder();
	      queryBuilder.where().eq(columnName, columnValue);
	      PreparedQuery<T> preparedQuery = queryBuilder.prepare();
	      result = getDao().query(preparedQuery);
	    } catch (java.sql.SQLException e) {
	    	e.printStackTrace();
	    }
	    return result;
	  }



	  /**
	   * 删除指定id的记录
	   * 
	   * @param dataDao
	   * @param id
	   * @return
	   * @throws Exception
	   */
	  public int deleteDataById(String id) throws Exception {
	    Integer idInteger = Integer.valueOf(id);
	    return getDao().deleteById(idInteger);
	  }

	  /**
	   * 批量删除数据
	   * 
	   * @param dataDao
	   * @param dataList
	   * @return
	   * @throws Exception
	   */
	  public int deleteDatas(List<T> dataList) throws Exception {
	    return getDao().delete(dataList);
	  }

	  /**
	   * 清空数据
	   * 
	   * @param dataDao
	   * @param dataList
	   * @return
	   * @throws Exception
	   */
	  public int deleteAll() throws Exception {
	    return getDao().delete(findAllData());
	  }
}
