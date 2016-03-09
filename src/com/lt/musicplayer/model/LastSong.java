package com.lt.musicplayer.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * 最后播放的一首歌
 * @author taoliu
 * create at Mar 2, 2016
 */
@DatabaseTable(tableName = "tb_lastsong")
public class LastSong {

	/**
	 * 歌曲id
	 */
	@DatabaseField(id = true)
	private int id;
	/**
	 * 歌曲内部名称
	 */
	@DatabaseField(columnName = "title")
	private String title;
	/**
	 * 专辑
	 */
	@DatabaseField(columnName = "album")
	private String album;
	/**
	 * 专辑id
	 */
	@DatabaseField(columnName = "albumId")
	private long albumId;
	/**
	 * 歌曲显示的名称
	 */
	@DatabaseField(columnName = "displayName")
	private String displayName;
	/**
	 * 歌手
	 */
	@DatabaseField(columnName = "artist")
	private String artist;
	/**
	 * 歌曲时长
	 */
	@DatabaseField(columnName = "duration")
	private long duration;
	/**
	 * 歌曲大小
	 */
	@DatabaseField(columnName = "size")
	private long size;
	/**
	 * 歌曲路径
	 */
	@DatabaseField(columnName = "url")
	private String url;
	/**
	 * 歌曲所在文件夹路径
	 */
	@DatabaseField(columnName = "folderPath")
	private String folderPath;
	/**
	 * 歌词名称
	 */
	@DatabaseField(columnName = "lrcTitle")
	private String lrcTitle;
	/**
	 * 歌词大小
	 */
	@DatabaseField(columnName = "lrcSize")
	private String lrcSize;
	/**
	 * 进度
	 */
	@DatabaseField(columnName = "progress")
	private int progress;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getLrcTitle() {
		return lrcTitle;
	}

	public void setLrcTitle(String lrcTitle) {
		this.lrcTitle = lrcTitle;
	}

	public String getLrcSize() {
		return lrcSize;
	}

	public void setLrcSize(String lrcSize) {
		this.lrcSize = lrcSize;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

}
