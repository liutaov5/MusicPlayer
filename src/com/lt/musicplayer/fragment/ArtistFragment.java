package com.lt.musicplayer.fragment;

import java.util.ArrayList;
import java.util.List;

import com.lt.musicplayer.R;
import com.lt.musicplayer.activity.MusicActivity;
import com.lt.musicplayer.adapter.ArtistAdapter;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.db.ArtistDao;
import com.lt.musicplayer.model.Artist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ArtistFragment extends BaseFragment{

	private ListView mArtistList;
	private ArtistAdapter mAdapter;
	private List<Artist> mData;
	private ArtistDao mArtistDao;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music, container, false);
		mArtistList=(ListView)view.findViewById(R.id.lv_music_list);
		mData=new ArrayList<Artist>();
		mArtistDao=new ArtistDao(getActivity());
		try {
			if(mArtistDao.findAllData().size()>0){
				updateList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mArtistList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent=new Intent(getActivity(), MusicActivity.class);
				intent.putExtra(MessageConstant.MUSIC_TITLE, mData.get(position).getArtist());
				intent.putExtra(MessageConstant.SEND_TYPE, "artist");
				intent.putExtra(MessageConstant.SEND_CONTENT, mData.get(position).getArtist());
				startActivity(intent);
			}
		});
		return view;
	}
	
	@Override
	protected void updateList() {
		if(mData!=null){
			mData.clear();
		}
		try {
			mData.addAll(mArtistDao.findAllData());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(mAdapter==null){
			mAdapter=new ArtistAdapter(getActivity(), mData, R.layout.item_list_music);
			mArtistList.setAdapter(mAdapter);
		}else{
			mAdapter.notifyDataSetChanged();
		}
	}




}
