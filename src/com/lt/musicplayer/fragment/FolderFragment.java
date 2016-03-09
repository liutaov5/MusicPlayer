package com.lt.musicplayer.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lt.musicplayer.R;
import com.lt.musicplayer.activity.BaseActivity;
import com.lt.musicplayer.activity.MusicActivity;
import com.lt.musicplayer.adapter.FolderAdapter;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.db.FolderDao;
import com.lt.musicplayer.model.Folder;
import com.lt.musicplayer.service.PlaySongService;

/**
 * 
 * @author taoliu create at Jan 21, 2016
 */
public class FolderFragment extends BaseFragment {

	private ListView mFolderList;
	private FolderAdapter mAdapter;
	private List<Folder> mData;
	private FolderDao mFolderDao;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music, container, false);
		mFolderList = (ListView) view.findViewById(R.id.lv_music_list);
		mData = new ArrayList<Folder>();
		mFolderDao = new FolderDao(getActivity());
		try {
			if (mFolderDao.findAllData().size() > 0) {
				updateList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mFolderList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PlaySongService.isInside=true;
				Intent intent=new Intent(getActivity(), MusicActivity.class);
				intent.putExtra(MessageConstant.MUSIC_TITLE, mData.get(position).getFolder());
				intent.putExtra(MessageConstant.SEND_TYPE, "folderPath");
				intent.putExtra(MessageConstant.SEND_CONTENT, mData.get(position).getPath());
				startActivity(intent);
			}
		});
		return view;
	}

	@Override
	protected void updateList() {
		if (mData != null) {
			mData.clear();
		}
		try {
			mData.addAll(mFolderDao.findAllData());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mAdapter == null) {
			mAdapter = new FolderAdapter(getActivity(), mData,
					R.layout.item_list_music);
			mFolderList.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

}
