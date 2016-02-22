package com.lt.musicplayer.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MyBaseAdapter<T>  extends BaseAdapter{

	protected Context mContext;
	protected int mLayout;
	protected List<T> mData;
	
	public MyBaseAdapter(Context mContext, List<T> mData, int mLayout) {
		super();
		this.mContext = mContext;
		this.mData = mData;
		this.mLayout = mLayout;
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public T getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return initView(position, convertView, parent);
	}

	abstract protected View initView(int position, View convertView, ViewGroup parent);
}
