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
	public static int dp2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    } 
}
