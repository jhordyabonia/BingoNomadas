package com.jhordyabonia.bn;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jhordyabonia.models.Store;
import com.jhordyabonia.util.DownLoadImage;
import com.jhordyabonia.util.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Bingo extends FragmentActivity {
	public static boolean fullScream = false,zoom=false;
	private Gallery collection;
	private ViewPager galery;
	private AdView mAdView;
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
		setContentView(R.layout.activity_bingo);

		galery = ((ViewPager) findViewById(R.id.pager));
		collection = new Gallery(getSupportFragmentManager());
		galery.setAdapter(collection);

		Intent intent =getIntent();
		if(intent==null)
		{	finish();return;}

		try
		{
			bingo = new JSONObject(intent.getStringExtra(Server.BINGO));
			ID = intent.getStringExtra(Server.ID);
			((TextView)findViewById(R.id.bingo_name))
					.setText(bingo.getString(Store.BINGO_NAME));

			String[] images= bingo.getString(Store.AWARDS_IMAGES).split(",");
			for(String image:images)
				if(!image.isEmpty())
					collection.loadItem(image);
		}catch(JSONException e){}


		mAdView = (AdView)findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
	}
	public void launchGame(){
		Intent intent = new Intent(Bingo.this, Game.class);
		intent.putExtra(Server.ID,ID);
		startActivity(intent);
		finish();
	}
	public class Gallery extends FragmentPagerAdapter
	{
		private ArrayList<Bitmap> data = new ArrayList<Bitmap>();

		public Gallery(FragmentManager fm){	super(fm);}
		public void addItem(Bitmap img)
		{
			data.add(img);
			notifyDataSetChanged();
			Toast.makeText(Bingo.this, "Apunte "+(data.size()-1)
					+" agregado!",Toast.LENGTH_SHORT).show();
			galery.setCurrentItem(data.size()-1);
		}
		public void loadItem(String img)
		{
			DownLoadImage loader = new DownLoadImage(Bingo.this);
			loader.setCallBack(this);
			loader.execute(img);
			//loader.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,img);

			Toast.makeText(Bingo.this,img,Toast.LENGTH_SHORT).show();
		}
		@Override
		public Fragment getItem(int i)
		{	switch(i)
			{
				case 0:
					Bundle bundle=new Bundle();
					bundle.putString(Server.BINGO,bingo.toString());
					DetailBingo out=new DetailBingo();
					out.setArguments(bundle);
					return out;
				case 1: case 5: case 10:case 15:
					return  new Ads();
				default:
					Bitmap tmp = data.get(i);
					Bundle args = new Bundle();
					args.putParcelable("image", tmp);
					Fragment fragment = new Image(tmp==null);
					fragment.setArguments(args);
					return fragment;
			}
		}
		@Override
		public int getCount()
        {
            int i= data.size(),out=2;
            switch(i)
            {
                case 0:out=2;break;
                case 1:case 2:case 3:out=3;break;
                case 4: case 5:out=4;break;
                default:out=5;
            }
            return i+out;
        }
		@Override
		public CharSequence getPageTitle(int position)
		{
			if(position==0)return "Agregar apunte";
			return "apunte " + position;
		}
	}
	private class Image extends Fragment
	{
		private boolean camera;
		public Image(boolean camera)
		{	zoom=false;this.camera=camera;		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState)
		{
			final View root = inflater.inflate(R.layout.image, container, false);

			Bundle args = getArguments();
			Bitmap tmp = (Bitmap) args.get("image");
			ImageView image = ((ImageView) root.findViewById(R.id.image));
			ImageView imageFull = ((ImageView) root.findViewById(R.id.imageFull));
			if (tmp != null && image != null )
			{
				View.OnClickListener list=new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{	 fullScream(root);	}

				};
				image.setImageBitmap(tmp);
				imageFull.setImageBitmap(tmp);
				image.setOnClickListener(list);
				imageFull.setOnClickListener(list);
			}
			return root;
		}
		public void fullScream(View root)
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
