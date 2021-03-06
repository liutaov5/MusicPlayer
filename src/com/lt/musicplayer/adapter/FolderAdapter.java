package com.lt.musicplayer.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lt.musicplayer.R;
import com.lt.musicplayer.model.Folder;

/**
 * 
 * @author taoliu create at Jan 21, 2016
 */
public class FolderAdapter extends MyBaseAdapter<Folder> {

	public FolderAdapter(Context mContext, List<Folder> mData, int mLayout) {
		super(mContext, mData, mLayout);
	}

	@Override
	protected View initView(int position, View convertView, ViewGroup parent) {
		Folder folder = mData.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(mLayout,
					parent, false);
			holder = new ViewHolder();
			holder.songArtist = (TextView) convertView
					.findViewById(R.id.tv_song_name);
			holder.count = (TextView) convertView
					.findViewById(R.id.tv_song_artist);
			holder.more = (ImageView) convertView
					.findViewById(R.id.iv_music_more);
			holder.picture = (ImageView) convertView
					.findViewById(R.id.iv_picture);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.songArtist.setText(folder.getFolder());
		holder.count.setText(folder.getCount() + "首 " + folder.getPath());
		holder.more.setTag(folder);
		holder.picture.setVisibility(View.VISIBLE);
		holder.picture.setImageResource(R.drawable.file);
		holder.more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		return convertView;
	}

	class ViewHolder {
		public TextView songArtist;
		public TextView count;
		public ImageView more;
		public ImageView picture;
	}

}
