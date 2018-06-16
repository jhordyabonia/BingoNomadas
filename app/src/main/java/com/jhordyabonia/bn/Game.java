package com.jhordyabonia.bn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jhordyabonia.models.Adapter;
import com.jhordyabonia.models.Store;
import com.jhordyabonia.models.User;
import com.jhordyabonia.util.Server;
import com.jhordyabonia.webservice.Asynchtask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Game extends FragmentActivity
{  
	public static final String GAME = "com.jhordyabonia.bn.file",
	_AUDIO="audio",_MUSIC="music", ONPLAY="onPlay",TABLES="tables";
	public static  boolean MUSIC=true,AUDIO=true;
	SharedPreferences file=null;
	/**Prefix b as bVar is button */
	ImageView bMusic,bAudio,bRestart;
	MediaPlayer mAudio,mMusic,mWin,mLose,mLine;
	MakeDialog add;
	public static String ID="3158241412";
	TextToSpeech speaker;
    ViewPager mViewPager;
    User user;
    OnClickListener controlers=new OnClickListener()
    {
		@Override
		public void onClick(View arg0) 
		{
			SharedPreferences.Editor editor = file.edit();
			switch(arg0.getId())
			{
				case R.id.bingo:
					break;
				case R.id.restart:
					add.show(Game.this.getSupportFragmentManager());
					break;
				case R.id.music:
				if(MUSIC)
				{
					bMusic.setImageResource(R.drawable.music_);
					mMusic.pause();
				}else
				{
					bMusic.setImageResource(R.drawable.music);
					mMusic.start();
				}MUSIC=!MUSIC;
				editor.putBoolean(_MUSIC, MUSIC);
		        editor.commit();
				break;
			case R.id.audio:
				if(AUDIO)
				{
					bAudio.setImageResource(R.drawable.speak_);
					mAudio.start();
				}else {
					bAudio.setImageResource(R.drawable.speak);
					mAudio.pause();
					mLine.pause();
				}			    
				AUDIO=!AUDIO;
				editor.putBoolean(_AUDIO, AUDIO);
		        editor.commit();	
				break;
			}
		}
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(
        	    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
        	    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);		
        getWindow().setFlags(
        		WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA,
        	    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_game);
        
        bRestart=findViewById(R.id.restart);
        bRestart.setOnClickListener(controlers);
        bMusic=findViewById(R.id.music);
        bMusic.setOnClickListener(controlers);
        bAudio=findViewById(R.id.audio);
        bAudio.setOnClickListener(controlers);

        file =  getSharedPreferences(GAME, Context.MODE_PRIVATE);
        mAudio = MediaPlayer.create(this, R.raw.stick);
    	mMusic = MediaPlayer.create(this, R.raw.background_music);
    	mWin = MediaPlayer.create(this, R.raw.applause);
    	mLine = MediaPlayer.create(this, R.raw.line);
    	mLose = MediaPlayer.create(this, R.raw.lose);
        mMusic.setLooping(true);  
        
        MUSIC=file.getBoolean(_MUSIC, MUSIC);
        if(!AUDIO)        	
			bAudio.setImageResource(R.drawable.speak_);

        if(!MUSIC)
        	bMusic.setImageResource(R.drawable.music_);
        else mMusic.start();    
        
        user=new User(this);
        speaker=new TextToSpeech(this,  
        		new OnInitListener()
        		{
					@Override
					public void onInit(int arg0) {}
				});

        mViewPager =  findViewById(R.id.pager);
        mViewPager.setAdapter(
        	new  FragmentPagerAdapter(getSupportFragmentManager()) 
        	{
	           @Override
	            public Fragment getItem(int position) 
	            { Fragment fragment = new Table();
	                Bundle args = new Bundle();
	                args.putInt(Table.ARG_SECTION_NUMBER, position + 1);
	                fragment.setArguments(args);
	                return fragment;
	            }	
	            @Override
	            public int getCount() { return 1;}	
	            @Override
	            public CharSequence getPageTitle(int position){return null;}
        });
		add= new MakeDialog();
        Intent intent =getIntent();
        if(intent==null)
		{	finish();return;}
        ID=intent.getStringExtra(Server.ID);
       }
    @Override
    protected void onDestroy()
    {
		stopAudio();
    	super.onDestroy();
	}@Override
    protected void onPause()
    {
    	super.onPause();
		mMusic.pause();
    }
    @Override
    protected void onResume()
    {
    	super.onResume();
    	if(!AUDIO)      
    		bAudio.setImageResource(R.drawable.speak_);
    	if(!MUSIC)
        	bMusic.setImageResource(R.drawable.music_);
    	else	mMusic.start();	    	
	}
    public void stopAudio()
	{
		mAudio.stop();
		mMusic.stop();
	   	mWin.stop();
		mLine.stop();
	   	mAudio.release();
		mMusic.release();
		mWin.release();
		mLine.release();
	}

	//@SuppressLint()
	public static class MakeDialog extends DialogFragment implements Asynchtask
	{
			View root;
			Adapter base;
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState)
			{
				AlertDialog.Builder builder =
						new AlertDialog.Builder(getContext());
				LayoutInflater inflater = getActivity().getLayoutInflater();

				root=inflater.inflate(R.layout.details_on_play, null);
				ListView view =root.findViewById(R.id.details);

				base = new Adapter(getContext(),new ArrayList<Adapter.Item>(),R.layout.item2);
				view.setAdapter(base);
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{dialog.dismiss();}
				};

				builder.setTitle("Ganadores!!!")
						.setIcon(R.drawable.ic_launcher)
						.setView(root)
						.setNegativeButton("Cerrar", listener)
						.setPositiveButton(" Ok ", listener)
						.setCancelable(false);
				return builder.create();
			}
			@Override
			public void processFinish(String json)
			{
				String title="Bingo Nomada";
				try
				{
					JSONObject store=new JSONObject(json);
					JSONArray store_raw=store.getJSONArray(Game.TABLES);
					title=store.getString(Store.BINGO_NAME);
					if(title.equals("null"))
						title="Ninguno.";
					base.clear();
					for(int u=1;u<store_raw.length();u++) {
						JSONObject obj =store_raw.getJSONObject(u);

						Adapter.Item tt = new Adapter.Item(obj.getString(User._NAME)
								, u+ " Lugar!!!", "", "");
						base.add(tt);
					}
					base.setDropDownViewResource(base.getCount() - 1);
				}catch(JSONException e){}
				((TextView)root.findViewById(R.id.title))
						.setText(title);

			}
			public void show(FragmentManager arg0)
			{
				show(arg0, "missiles");
				HashMap<String, String> datos=new HashMap<String, String>();
				datos.put(Server.ID,ID);
				Server.setDataToSend(datos);
				Server.send("winners", null, this);
			}
		}

	public static int ID_NUMBERS[]=
		{
			R.id.TextView01,
		    R.id.TextView02,
		    R.id.TextView03,
		    R.id.TextView04,
		    R.id.TextView05,
		    R.id.TextView06,
		    R.id.TextView07,
		    R.id.TextView08,
		    R.id.TextView09,
		    R.id.TextView10,
		    R.id.TextView11,
		    R.id.TextView12,
		    R.id.TextView13,
		    R.id.TextView14,
		    R.id.TextView15,
		    R.id.TextView16,
		    R.id.TextView17,
		    R.id.TextView18,
		    R.id.TextView19,
		    R.id.TextView20,
		    R.id.TextView21,
		    R.id.TextView22,
		    R.id.TextView24,
		    R.id.TextView25
		};
}
