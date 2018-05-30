package com.jhordyabonia.bn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jhordyabonia.models.User;
import com.jhordyabonia.util.Server;

public class Game extends FragmentActivity
{  
	public static final String GAME = "com.jhordyabonia.bn.file",
	_AUDIO="audio",_MUSIC="music",_RESULT="result", DISPLAY="display",ONPLAY="onPlay",TABLES="tables";
	public static  boolean MUSIC=true,AUDIO=true,LOOCK=false;
	SharedPreferences file=null;
	/**Prefix b as bVar is button */
	ImageView bMusic,bAudio,bSettings, bRestart,bCoins;
	MediaPlayer mAudio,mMusic,mWin,mLose,mLine;
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
			case R.id.music:
				if(MUSIC)
				{
					bMusic.setImageResource(android.R.drawable.ic_lock_silent_mode);
					mMusic.pause();
				}else
				{
					bMusic.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
					mMusic.start();
				}MUSIC=!MUSIC;
				editor.putBoolean(_MUSIC, MUSIC);
		        editor.commit();
				break;
			case R.id.audio:
				if(AUDIO)
				{
					bAudio.setImageResource(android.R.drawable.ic_lock_silent_mode);
					mAudio.start();
				}else {
					bAudio.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
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
        
        bRestart=(ImageView)findViewById(R.id.restart); 
        bRestart.setOnClickListener(controlers);
        bMusic=(ImageView)findViewById(R.id.music);
        bMusic.setOnClickListener(controlers);
        bAudio=(ImageView)findViewById(R.id.audio);
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
			bAudio.setImageResource(android.R.drawable.ic_lock_silent_mode); 

        if(!MUSIC)
        	bMusic.setImageResource(android.R.drawable.ic_lock_silent_mode);
        else mMusic.start();    
        
        user=new User(this);
        speaker=new TextToSpeech(this,  
        		new OnInitListener()
        		{
					@Override
					public void onInit(int arg0) {}
				});
        
        mViewPager = (ViewPager) findViewById(R.id.pager);
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
        Intent intent =getIntent();
        if(intent==null)
		{	finish();return;}
        ID=intent.getStringExtra(Server.ID);
       }
    @Override
    protected void onDestroy()
    {
    	super.onDestroy();
    	stopAudio();
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
    		bAudio.setImageResource(android.R.drawable.ic_lock_silent_mode); 
    	if(!MUSIC)
        	bMusic.setImageResource(android.R.drawable.ic_lock_silent_mode);
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
