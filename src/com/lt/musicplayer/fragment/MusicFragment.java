package com.lt.musicplayer.fragment;

import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.R;
import com.lt.musicplayer.adapter.MusicAdapter;
import com.lt.musicplayer.manager.SongManager;
import com.lt.musicplayer.model.Song;
import com.lt.musicplayer.service.ScanSongService;
import com.lt.musicplayer.utils.ToastUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MusicFragment extends BaseFragment {

	private ListView mMusicList;
	private MusicAdapter mAdapter;
	private List<Song> mData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music, container, false);
		mMusicList = (ListView) view.findViewById(R.id.lv_music_list);
		mData=new ArrayList<Song>();
		if(SongManager.getInstance(getActivity()).getAllSong()!=null){
			updateList();
		}
		mMusicList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mPlayService.playMusic(mData.get(position).getUrl());
				SongManager songManager=SongManager.getInstance(getActivity());
				songManager.clearSong();
				for(Song song:mData){
					songManager.addSong(song);
				}	
			}
		});
		return view;
	}

	protected void updateList() {
		if(mData!=null){
			mData.clear();
		}
		mData.addAll(SongManager.getInstance(getActivity()).getAllSong());
		if (mAdapter == null) {
			mAdapter = new MusicAdapter(getActivity(), mData,
					R.layout.item_list_music);
			mMusicList.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}
	
}
