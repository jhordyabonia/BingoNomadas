package com.jhordyabonia.bn;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.jhordyabonia.models.Store;
import com.jhordyabonia.util.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Bingo extends FragmentActivity {
	public static boolean zoom=false;
	private Gallery collection;
	private ViewPager galery;
	private AdView mAdView;
	private InterstitialAd interstitialAd;
	private JSONObject bingo =new JSONObject();
	String id="";
	public static boolean LOCAL=true,LOTTO=true;
	public static int TIMMER,LEVEL;

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
		setContentView(R.layout.activity_bingo);

		galery =  findViewById(R.id.pager);
		collection = new Gallery(getSupportFragmentManager());
		galery.setAdapter(collection);

		Intent intent =getIntent();
		if(intent==null)
		{	finish();   return; }

		try
		{
			bingo = new JSONObject(intent.getStringExtra(Server.BINGO));
			id = intent.getStringExtra(Server.ID);
			TIMMER = intent.getIntExtra(Server._TIMMER,5000);
			LOCAL = intent.getBooleanExtra(Server._LOCAL,true);
			LOTTO= intent.getBooleanExtra(Server._LOTTO,true);
			LEVEL= intent.getIntExtra(Server._LEVEL,0);
			((TextView)findViewById(R.id.bingo_name))
					.setText(bingo.getString(Store.BINGO_NAME));

			String[] images= bingo.getString(Store.AWARDS_IMAGES).split(",");
			for(String image:images)
				if(!image.isEmpty())
					collection.addItem(image);
		}catch(JSONException e){}

		// Initialize the Mobile Ads SDK.
		MobileAds.initialize(this, "ca-app-pub-7036101536380541~6190945049");
		//test
		//MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
		mAdView = findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		// Create the InterstitialAd and set the adUnitId.
		interstitialAd = new InterstitialAd(this);

		interstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
		//test
		//interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed(){startGame();}
			@Override
			public void onAdLoaded(){}
			@Override
			public void onAdFailedToLoad(int errorCode)
			{ loadInterstitial();}
		});
		loadInterstitial();
	}
	@Override
	public void onDestroy()
	{
		stopService(new Intent(this,Connect.class));
		super.onDestroy();
	}
	public void launchGame(){
		if(LOCAL)
			interstitialAd.show();
		else startGame();
	}
	private void loadInterstitial()
	{
		// Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
		if (!interstitialAd.isLoading()/* && !interstitialAd.isLoaded()*/){
			AdRequest.Builder builder = new AdRequest.Builder();
			//builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
			interstitialAd.loadAd(builder.build());
		}
	}
	private void startGame() {
		Intent intent = new Intent(Bingo.this, Game.class);
		intent.putExtra(Server.BINGO,bingo.toString());
		intent.putExtra(Server.ID,id);
		intent.putExtra(Server._LEVEL,LEVEL);
		startActivity(intent);
		finish();
	}
	public class Gallery extends FragmentPagerAdapter
	{
		private ArrayList<String> data = new ArrayList<String>();

		public Gallery(FragmentManager fm){	super(fm);}
		public void addItem(String img)
		{
            data.add(img);
			notifyDataSetChanged();
			galery.setCurrentItem(data.size()-1);
		}
		@Override
		public Fragment getItem(int i)
		{
			Fragment out;
			Bundle args = new Bundle();
		    switch(i)
			{
				case 0:
					args.putString(Server.BINGO,bingo.toString());
					out=new DetailBingo();
					break;
				case 1: case 5: case 10:case 15:
					StringBuilder CONTAC_INFO= new StringBuilder();
					CONTAC_INFO.append("Descripción:\n");
					try {
						CONTAC_INFO.append(bingo.getString(Store.BINGO_NAME));
						CONTAC_INFO.append(" ");
						CONTAC_INFO.append(bingo.getString(Store.AWARDS_NAME));
					}catch(JSONException e){}
					args.putString(Store.BINGO_NAME, CONTAC_INFO.toString());
					out=new Ads();
					break;
				default:
					String tmp = data.get(0);
					args.putString("image", tmp);
					out = new Foto();
			}
			out.setArguments(args);
			return out;
		}
		@Override
		public int getCount()
        {
            int i= data.size();
            switch(i)
            {
                case 0: return 2;
                case 1: case 2: case 3: return i+3;
                case 4: case 5: return i+4;
                default: return i+5;
            }
        }
		@Override
		public CharSequence getPageTitle(int position)
		{
			if(position==0)return "Agregar apunte";
			return "apunte " + position;
		}
	}
	public static class Foto extends Fragment
	{
		View root;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState)
		{
			root = inflater.inflate(R.layout.image, container, false);

			Bundle args = getArguments();
			setImage(args.getString("image"));

			View.OnClickListener list=new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{	 fullScream();	}
				};
			root.findViewById(R.id.image).setOnClickListener(list);
			root.findViewById(R.id.imageFull).setOnClickListener(list);

			return root;
		}

		private void setImage(final String image)
		{
			(new AsyncTask<String, Void, Bitmap>()
			{
				@Override
				protected Bitmap doInBackground(String... fotos)
				{
					Bitmap imagen=null ;
					try
					{
						URL imageUrl = new URL(Server.URL_SERVER.replace("bn","uploads/bn/")+image);
						HttpURLConnection urlConnection = (HttpURLConnection) imageUrl.openConnection();
						InputStream inputStream = urlConnection.getInputStream();
						imagen = BitmapFactory.decodeStream(inputStream);
					}catch (IOException e){}

					return imagen;
				}
				@Override
				protected void onPostExecute(Bitmap bitmap)
				{
					((ImageView)root.findViewById(R.id.image))
							.setImageBitmap(bitmap);
					((ImageView) root.findViewById(R.id.imageFull))
							.setImageBitmap(bitmap);
				}
			}).execute();
		}
		public void fullScream()
		{
			if(zoom)
			{
				root.findViewById(R.id.image)
						.setVisibility(View.GONE);
				root.findViewById(R.id.viewImageFull)
						.setVisibility(View.VISIBLE);
			}else
			{
				root.findViewById(R.id.image)
						.setVisibility(View.VISIBLE);
				root.findViewById(R.id.viewImageFull)
						.setVisibility(View.GONE);
			}
			zoom=!zoom;
		}
	}
}
