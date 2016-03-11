package com.lt.musicplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
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
	/**
	 * 时间转换
	 * @param duration 毫秒
	 * @return
	 */
	public static String durationToString(int duration){
		int secondAll =duration/1000;
		int minute=secondAll/60;
		int second=secondAll%60;
		return String.format("%02d:%02d", minute,second);
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap  
	{  
	    int width = drawable.getIntrinsicWidth();// 取drawable的长宽  
	    int height = drawable.getIntrinsicHeight();  
	    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;// 取drawable的颜色格式  
	    Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap  
	    Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布  
	    drawable.setBounds(0, 0, width, height);  
	    drawable.draw(canvas);// 把drawable内容画到画布中  
	    return bitmap;  
	} 
}
