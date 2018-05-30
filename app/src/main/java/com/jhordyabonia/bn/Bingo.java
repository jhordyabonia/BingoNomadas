package com.jhordyabonia.bn;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jhordyabonia.models.Store;
import com.jhordyabonia.util.Server;

import org.json.JSONException;
import org.json.JSONObject;

public class Bingo extends FragmentActivity {
	private AdView mAdView;
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	private JSONObject bingo;
	String ID="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);	
        getWindow().setFlags(
        		WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA,
        	    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    getActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		setContentView(R.layout.activity_bingo);
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		setPage(0);

		Intent intent =getIntent();
		if(intent==null)
		{	finish();return;}

		try
		{
			bingo = new JSONObject(intent.getStringExtra(Server.BINGO));
			ID = intent.getStringExtra(Server.ID);
			((TextView)findViewById(R.id.bingo_name))
					.setText(bingo.getString(Store.BINGO_NAME));
		}catch(JSONException e){}


		mAdView = (AdView)findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	}
	void setPage(int t)
	{
		Intent intent =getIntent();
		setResult(-2);
    	if(intent==null)
    		return ;
    	mViewPager.setCurrentItem(intent.getIntExtra("page", t));
	}
	public void launchGame(){
		Intent intent = new Intent(Bingo.this, Game.class);
		intent.putExtra(Server.ID,ID);
		startActivity(intent);
		finish();
	}
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				Bundle bundle=new Bundle();
				bundle.putString(Server.BINGO,bingo.toString());
				DetailBingo out=new DetailBingo();
				out.setArguments(bundle);
				return out;
			}
			return  new Ads();
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}
	}
}
