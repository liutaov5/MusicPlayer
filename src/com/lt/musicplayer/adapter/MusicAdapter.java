package com.lt.musicplayer.adapter;

import java.util.List;

import com.lt.musicplayer.R;
import com.lt.musicplayer.model.Song;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MusicAdapter extends MyBaseAdapter<Song> {

	public MusicAdapter(Context mContext, List<Song> mList, int mLayout) {
		super(mContext,mList,mLayout);
	}

	@Override
	protected View initView(int position, View convertView, ViewGroup parent) {
		Song song = mData.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					mLayout, parent, false);
			holder = new ViewHolder();
			holder.songName = (TextView) convertView
					.findViewById(R.id.tv_song_name);
			holder.songArtist = (TextView) convertView
					.findViewById(R.id.tv_song_artist);
			holder.more = (ImageView) convertView
					.findViewById(R.id.iv_music_more);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.songName.setText(song.getTitle());
		holder.songArtist.setText(song.getArtist());
		holder.more.setTag(song);
		holder.more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, ((Song)v.getTag()).getId()+"", Toast.LENGTH_SHORT).show();
			}
		});
		return convertView;
	}
	
	class ViewHolder {
		public TextView songName;
		public TextView songArtist;
		public ImageView more;
	}

}
