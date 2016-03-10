package com.lt.musicplayer.utils;

import android.content.Context;
/**
 * 单位换算工具类
 * @author taoliu
 * create at Mar 1, 2016
 */
public class UnitConverterUtils {

	/**
	 * dp转换为px
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dpToPx(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
	
	public static String durationToString(int duration){
		int secondAll =duration/1000;
		int minute=secondAll/60;
		int second=secondAll%60;
		return String.format("%02d:%02d", minute,second);
	}
}
