package com.lt.musicplayer.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 专辑信息
 * @author taoliu
 * create at Jan 20, 2016
 */
@DatabaseTable(tableName = "tb_album")
public class Album {

	/**
	 * 歌曲id
	 */
	@DatabaseField(columnName="id")
	private int id;
	
	/**
	 * 专辑
	 */
	@DatabaseField(columnName = "album",id=true)
	private String album;
	
	/**
	 * 专辑id
	 */
	@DatabaseField(columnName = "albumId")
	private long albumId;

	/**
	 * 歌的数量
	 */
	@DatabaseField(columnName = "count")
	private int count;
	/**
	 * 歌手
	 */
	@DatabaseField(columnName="artist")
	private String artist;

	/**
	 * 专辑图片
	 */
	@DatabaseField(columnName = "picture")
	private String picture;

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
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

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
