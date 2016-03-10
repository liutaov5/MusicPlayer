package com.lt.musicplayer.interfaces;

public interface OnMusicStatusChangeListener {

	public void onMusicPlay();
	public void onMusicStop();
	public void onMusicKeep();
	public void onMusicPause();
	public void onMusicNext();
	public void onMusicPre();
	public void onMusicUpdateProgress(int position);
}
