package com.lt.musicplayer.receiver;

import java.util.Timer;
import java.util.TimerTask;

import com.lt.musicplayer.constants.MessageConstant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

/**
 * 接收耳机线控按键广播
 * 
 * @author taoliu create at Mar 28, 2016
 */
public class MediaButtonReceiver extends BroadcastReceiver {

	String TAG="lt";
	private static int clickCount;
	private Timer timer = null;
	private MyTask myTask = null;
	private Context mContext;

	public MediaButtonReceiver() {
		timer = new Timer(true);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		mContext=context;
		String intentAction = intent.getAction();
		KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
		if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {
			if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
				if (clickCount == 0) {
					clickCount++;
					myTask = new MyTask();
					timer.schedule(myTask, 1000);
				} else if (clickCount == 1) {
					clickCount++;
				} else if (clickCount == 2) {
					clickCount=0;
//					myTask.cancel();
					timer.cancel();
					Intent intent2=new Intent(MessageConstant.ACTION_NOTIFICATION);
					intent2.putExtra(MessageConstant.NOTIFICATION_TAG,
							MessageConstant.NOTIFICATION_PRE);
					mContext.sendBroadcast(intent2);
				} 
			}
		}
	}

	class MyTask extends TimerTask {

		@Override
		public void run() {
			Intent intent=new Intent(MessageConstant.ACTION_NOTIFICATION);
			if (clickCount == 1) {
				intent.putExtra(MessageConstant.NOTIFICATION_TAG,
						MessageConstant.NOTIFICATION_PLAY);
				mContext.sendBroadcast(intent);
				Log.e(TAG, "clickcount.....................1");
			} else if (clickCount == 2) {
				intent.putExtra(MessageConstant.NOTIFICATION_TAG,
						MessageConstant.NOTIFICATION_NEXT);
				Log.e(TAG, "clickcount.....................2");
				mContext.sendBroadcast(intent);
			}
			
			clickCount = 0;
		}

	}

}
