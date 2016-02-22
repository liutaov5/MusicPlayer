package com.lt.musicplayer.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.db.AlbumDao;
import com.lt.musicplayer.db.ArtistDao;
import com.lt.musicplayer.db.FolderDao;
import com.lt.musicplayer.db.SongDao;
import com.lt.musicplayer.manager.SongManager;
import com.lt.musicplayer.model.Album;
import com.lt.musicplayer.model.Artist;
import com.lt.musicplayer.model.Folder;
import com.lt.musicplayer.model.Song;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 扫描本地音乐服务
 * 
 * @author taoliu create at Jan 21, 2016
 */
public class ScanSongService extends IntentService {

	public static final String ACTION_SCAN_FINISH = "action_scan_finish";

	public ScanSongService() {
		super("ScanSongService");
	}

	/**
	 * 启动service
	 * 
	 * @param context
	 */
	public static void startScanSongService(Context context) {
		Intent intent = new Intent(context, ScanSongService.class);
		context.startService(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Cursor cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

		SongManager songManager = SongManager.getInstance(this);
		songManager.clearSong();

		while (cursor.moveToNext()) {
			int isMusic = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));
			if (isMusic == 0) {
				continue;
			}
			String displayName = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
			if (displayName.contains(".mp3")) {
				String[] temp = displayName.split(".mp3");
				displayName = temp[0].trim();
			} else {
				continue;
			}
			int id = cursor.getInt(cursor
					.getColumnIndex(MediaStore.Audio.Media._ID));
			String title = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.TITLE));
			String album = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM));
			long albumId = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

			String artist = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			long duration = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.DURATION));
			long size = cursor.getLong(cursor
					.getColumnIndex(MediaStore.Audio.Media.SIZE));
			String url = cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Media.DATA));

			Song song = new Song();
			song.setId(id);
			song.setTitle(title);
			song.setAlbum(album);
			song.setAlbumId(albumId);
			song.setDisplayName(displayName);
			song.setArtist(artist);
			song.setDuration(duration);
			song.setSize(size);
			song.setUrl(url);
			song.setFolderPath(url.substring(0, url.lastIndexOf("/")));
			songManager.addSong(song);

			Artist artist2 = new Artist();
			artist2.setArtist(artist);
		}
		cursor.close();
		songManager.saveAllSong();
		saveToArtist();
		saveToAlbum();
		saveToFolder();
		Intent scanIntent = new Intent(ACTION_SCAN_FINISH);
		LocalBroadcastManager.getInstance(this).sendBroadcast(scanIntent);
	}

	/**
	 * 保存歌手信息到数据库
	 */
	private void saveToArtist() {
		SongDao songDao = new SongDao(this);
		List<String> names = songDao.searchColumn("artist");
		List<Artist> artists = new ArrayList<Artist>();
		for (String name : names) {
			Artist artist = new Artist();
			artist.setArtist(name);
			try {
				artist.setCount(songDao.findData("artist", name).size());
			} catch (Exception e) {
				e.printStackTrace();
			}
			artists.add(artist);
		}
		ArtistDao artistDao = new ArtistDao(this);
		try {
			artistDao.deleteAll();
			artistDao.createOrUpdateDatas(artists);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存专辑信息到数据库
	 */
	private void saveToAlbum() {
		SongDao songDao = new SongDao(this);
		List<String> names = songDao.searchColumn("album");
		List<Album> albums = new ArrayList<Album>();
		for (String name : names) {
			Album album = new Album();
			album.setAlbum(name);
			try {
				album.setAlbumId(songDao.findData("album", name).get(0)
						.getAlbumId());
				album.setCount(songDao.findData("album", name).size());
				album.setArtist(songDao.findData("album", name).get(0)
						.getArtist());
			} catch (Exception e) {
				e.printStackTrace();
			}
			albums.add(album);
		}
		AlbumDao albumDao = new AlbumDao(this);
		try {
			albumDao.deleteAll();
			albumDao.createOrUpdateDatas(albums);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存文件信息到数据库
	 */
	private void saveToFolder() {
		SongDao songDao = new SongDao(this);
		List<String> urls = songDao.searchColumn("folderPath");
		List<Folder> Folders = new ArrayList<Folder>();
		for (String url : urls) {
			Folder folder = new Folder();
			folder.setFolder(url.substring(url.lastIndexOf("/") + 1,
					url.length()));
			folder.setPath(url);
			try {
				folder.setCount(songDao.findData("folderPath", url).size());
			} catch (Exception e) {
				e.printStackTrace();
			}
			Folders.add(folder);
		}
		FolderDao folderDao = new FolderDao(this);
		try {
			folderDao.deleteAll();
			folderDao.createOrUpdateDatas(Folders);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
