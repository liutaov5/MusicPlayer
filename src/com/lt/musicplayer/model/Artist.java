package com.lt.musicplayer.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 歌手信息
 * @author taoliu
 * create at Jan 20, 2016
 */
@DatabaseTable(tableName = "tb_artist")
public class Artist {
	
	/**
	 * 歌手
	 */
	@DatabaseField(columnName = "artist",id=true)
	private String artist;

	/**
	 * 歌的数量
	 */
	@DatabaseField(columnName = "count")
	private int count;

	/**
	 * 歌手图片
	 */
	@DatabaseField(columnName = "picture")
	private String picture;

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

}
