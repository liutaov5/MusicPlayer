package com.lt.musicplayer.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.lt.musicplayer.R;
import com.lt.musicplayer.constants.MessageConstant;
import com.lt.musicplayer.customview.CircleDrawable;
import com.lt.musicplayer.db.LastSongDao;
import com.lt.musicplayer.fragment.AlbumFragment;
import com.lt.musicplayer.fragment.ArtistFragment;
import com.lt.musicplayer.fragment.FolderFragment;
import com.lt.musicplayer.fragment.MusicFragment;
import com.lt.musicplayer.interfaces.OnMusicStatusChangeListener;
import com.lt.musicplayer.model.LastSong;
import com.lt.musicplayer.service.PlaySongService;
import com.lt.musicplayer.service.ScanSongService;
import com.lt.musicplayer.utils.MusicUtils;
import com.lt.musicplayer.utils.ToastUtils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements
		OnNavigationItemSelectedListener, OnClickListener,
		OnMenuItemClickListener, OnMusicStatusChangeListener {

	private Toolbar mToolbar;
	private TabLayout mTab;
	private ViewPager mViewPager;
	private FloatingActionButton mFloatButton;
	private DrawerLayout mDrawer;
	private NavigationView mNavigation;
	private List<Fragment> mFragments;
	private String[] mTabText;
	private FragmentPagerAdapter mAdapter;
	private ImageView mUserHead;

	private ImageView mMusicImage;
	private TextView mMusicName;
	private TextView mMusicArtist;
	private ImageView mMusicMore;
	private ImageView mMusicPlay;
	private ImageView mMusicNext;
	private ProgressBar mMusicProgress;
	private RelativeLayout mBottomView;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_main);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mTab = (TabLayout) findViewById(R.id.tab);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mFloatButton = (FloatingActionButton) findViewById(R.id.float_button);
		mDrawer = (DrawerLayout) findViewById(R.id.drawer);
		mNavigation = (NavigationView) findViewById(R.id.navigation_view);
		mFragments = new ArrayList<Fragment>();
		mUserHead = (ImageView) findViewById(R.id.iv_user_head);
		mMusicImage = (ImageView) findViewById(R.id.iv_music_image);
		mMusicName = (TextView) findViewById(R.id.tv_music_name);
		mMusicArtist = (TextView) findViewById(R.id.tv_music_artist);
		mMusicMore = (ImageView) findViewById(R.id.iv_music_list);
		mMusicPlay = (ImageView) findViewById(R.id.iv_music_play);
		mMusicNext = (ImageView) findViewById(R.id.iv_music_next);
		mMusicProgress = (ProgressBar) findViewById(R.id.pb_music_progressbar);
		mBottomView=(RelativeLayout)findViewById(R.id.rl_bottom_view);

	}

	@Override
	protected void initData() {
		mToolbar.setTitle("Music Player");
		setSupportActionBar(mToolbar);
		mToolbar.setNavigationIcon(R.drawable.bt_localsonglist_move_press);

		MusicFragment musicfragment = new MusicFragment();
		ArtistFragment artistFragment = new ArtistFragment();
		AlbumFragment albumFragment = new AlbumFragment();
		FolderFragment folderFragment = new FolderFragment();

		mFragments.add(musicfragment);
		mFragments.add(artistFragment);
		mFragments.add(albumFragment);
		mFragments.add(folderFragment);

		mTabText = getResources().getStringArray(R.array.tab_text);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return mFragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return mFragments.get(arg0);
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return mTabText[position];
			}
		};

		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(mAdapter);
		mTab.setupWithViewPager(mViewPager);
		mTab.setTabsFromPagerAdapter(mAdapter);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.header);
		mUserHead.setImageDrawable(new CircleDrawable(bitmap));
	}

	@Override
	protected void initListener() {
		mNavigation.setNavigationItemSelectedListener(this);
		mToolbar.setOnMenuItemClickListener(this);
		mFloatButton.setOnClickListener(this);
		setMusicStatusChangeListener(this);
		mMusicPlay.setOnClickListener(this);
		mMusicNext.setOnClickListener(this);
		mMusicMore.setOnClickListener(this);
		mBottomView.setOnClickListener(this);
		// mToolbar.setNavigationOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// if (mDrawer.isDrawerOpen(Gravity.START)) {
		// mDrawer.closeDrawer(Gravity.START);
		// } else {
		// mDrawer.openDrawer(Gravity.START);
		// }
		// }
		// });
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem menuItem) {
		menuItem.setChecked(true);
		switch (menuItem.getItemId()) {
		case R.id.nav_local_music:
			ToastUtils.show(mContext, "本地音乐");
			break;
		case R.id.sub_exit:
			closeApp();
			break;
		default:
			break;
		}
		mDrawer.closeDrawers();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.float_button:
			if (PlaySongService.isPause) {
				mPlayService.keepPlay();
			} else {
				mPlayService.pauseMusic();
			}
			break;
		case R.id.iv_music_next:
			mPlayService.playNext();
			break;
		case R.id.iv_music_play:
			if (PlaySongService.isPause) {
				mPlayService.keepPlay();
			} else {
				mPlayService.pauseMusic();
			}
			break;
		case R.id.rl_bottom_view:
			Intent intent=new Intent(mContext, MusicPlayerActivity.class);
			intent.putExtra(MessageConstant.SONG_NAME, mMusicName.getText());
			intent.putExtra(MessageConstant.SONG_ARTIST, mMusicArtist.getText());
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_scan:
			Toast.makeText(this, "scan...", Toast.LENGTH_SHORT).show();
			ScanSongService.startScanSongService(this);
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		// ToolBar的NavigationIcon的点击事件
		if (id == android.R.id.home) {
			mDrawer.openDrawer(GravityCompat.START);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	// /**
	// * 返回键监听
	// */
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// exitBy2Click();
	// }
	// return false;
	// }

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	@SuppressWarnings("unused")
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			closeApp();
		}
	}

	/**
	 * 设置最下方播放条目的view
	 */
	private void setView() {

		LastSongDao lastSongDao = new LastSongDao(this);
		List<LastSong> song;
		try {
			song = lastSongDao.findAllData();
			if (song != null && song.size() > 0) {
				mMusicArtist.setText(song.get(0).getArtist());
				mMusicName.setText(song.get(0).getTitle());
				mMusicImage.setImageBitmap(MusicUtils.getAlbumBitmap(mContext,
						song.get(0).getId(), song.get(0).getAlbumId(), true,
						true));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (PlaySongService.isPause != null && PlaySongService.isPause) {
			mMusicPlay.setImageResource(R.drawable.img_button_notification_play_play);
		} else {
			mMusicPlay.setImageResource(R.drawable.img_button_notification_play_pause);
		}

	}

	private void updateProgress(int position) {
		mMusicProgress.setProgress(position);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		initAlbumPicture();
		setView();
	}

	@Override
	public void onMusicPlay() {
		mMusicPlay.setImageResource(R.drawable.img_button_notification_play_pause);
		setView();
	}

	@Override
	public void onMusicStop() {
		mMusicPlay.setImageResource(R.drawable.img_button_notification_play_play);
	}

	@Override
	public void onMusicKeep() {
		mMusicPlay.setImageResource(R.drawable.img_button_notification_play_pause);
	}

	@Override
	public void onMusicPause() {
		mMusicPlay.setImageResource(R.drawable.img_button_notification_play_play);
	}

	@Override
	public void onMusicNext() {
		setView();
	}

	@Override
	public void onMusicPre() {
		setView();
	}

	@Override
	public void onMusicUpdateProgress(int position) {
		updateProgress(position);
	}

}
