
package com.lt.musicplayer.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * 文件夹信息
 * @author taoliu
 * create at Jan 20, 2016
 */
@DatabaseTable(tableName = "tb_folder")
public class Folder {

	/**
	 * 文件夹
	 */
	@DatabaseField(columnName = "folder")
	private String folder;

	/**
	 * 路径
	 */
	@DatabaseField(columnName = "path",id=true)
	private String path;

	/**
	 * 歌的数量
	 */
	@DatabaseField(columnName = "count")
	private int count;

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
