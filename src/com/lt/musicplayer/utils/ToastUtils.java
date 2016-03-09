package com.lt.musicplayer.utils;

import android.content.Context;
import android.widget.Toast;
/**
 * Toast工具类
 * @author taoliu
 * create at Mar 3, 2016
 */
public class ToastUtils {

	public static void show(Context ctx, String text) {
		show(ctx, text, false);
	}

	public static void show(Context ctx, String text, boolean isLong) {

		if (isLong) {
			Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
		}
	}
	


}
