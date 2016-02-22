package com.lt.musicplayer.fragment;

import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.R;
import com.lt.musicplayer.activity.MusicActivity;
import com.lt.musicplayer.adapter.AlbumAdapter;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.db.AlbumDao;
import com.lt.musicplayer.model.Album;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 
 * @author taoliu create at Jan 21, 2016
 */
public class AlbumFragment extends BaseFragment {

	private ListView mAlbumList;
	private AlbumAdapter mAdapter;
	private List<Album> mData;
	private AlbumDao mAlbumDao;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music, container, false);
		mAlbumList = (ListView) view.findViewById(R.id.lv_music_list);
		mData = new ArrayList<Album>();
		mAlbumDao = new AlbumDao(getActivity());
		try {
			if (mAlbumDao.findAllData().size() > 0) {
				updateList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAlbumList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent(getActivity(), MusicActivity.class);
				intent.putExtra(MessageConstant.MUSIC_TITLE, mData.get(position).getAlbum());
				intent.putExtra(MessageConstant.SEND_TYPE, "album");
				intent.putExtra(MessageConstant.SEND_CONTENT, mData.get(position).getAlbum());
				startActivity(intent);
			}
		});
		return view;
	}

	@Override
	protected void updateList() {
		if (mData != null) {
			try {
				mData.addAll(mAlbumDao.findAllData());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (mAdapter == null) {
			mAdapter = new AlbumAdapter(getActivity(), mData,
					R.layout.item_list_music);
			mAlbumList.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

}
