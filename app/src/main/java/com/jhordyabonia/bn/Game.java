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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
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

public class Game extends FragmentActivity implements Asynchtask
{  
	public static final String GAME = "com.jhordyabonia.bn.file",
	_AUDIO="audio",_MUSIC="music", ONPLAY="onPlay",TABLES="tables";
	public static  boolean MUSIC=true,AUDIO=true;
	SharedPreferences file=null;
	/**Prefix b as bVar is button */
	ImageView bMusic,bAudio,bRestart;
	MediaPlayer mAudio,mMusic,mWin,mLose,mLine;
	DialogFragment add;
	Adapter base;
	public static int TIMMER=5000;
	public static boolean LOCAL=true;
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
				case R.id.restart:
					pull();
					add.show(Game.this.getSupportFragmentManager(), "missiles");
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

		base = new Adapter(this,new ArrayList<Adapter.Item>());
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
		makeDialog();
        Intent intent =getIntent();
        if(intent==null)
		{	finish();return;}
        ID=intent.getStringExtra(Server.ID);
		TIMMER = intent.getIntExtra(Server._TIMMER,5000);
		LOCAL = intent.getBooleanExtra(Server._LOCAL,true);
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
	@Override
	public void processFinish(String json)
	{
		String title="Bingo Nomada";

		((TextView)findViewById(R.id.title))
				.setText(title);
		try
		{
			JSONArray store_raw=new JSONArray(json);

			base.clear();
			for(int u=0;u<store_raw.length();u++) {
				JSONObject obj =
						new JSONObject(store_raw.getJSONObject(u).getString(Server.BINGO));

				/*Adapter.Item tt = new Adapter.Item(obj.getString(Store.AUTHOR_NAME)
						, obj.getString(Store.TYPE), "Tablas registradas: "
						,  obj.getString(Store.AUTHOR_FOTO));*/

				Adapter.Item tt = new Adapter.Item(obj.getString(Store.AUTHOR_NAME)
						, "Biiingo!!!", "no se que poner aqui "
						, "4.jpg");
				base.add(tt);
			}
			base.setDropDownViewResource(base.getCount() - 1);
		}catch(JSONException e){}
		//Dummy
		base.clear();
		for(int u=0;u<4;u++) {

			Adapter.Item tt = new Adapter.Item("Usuario " + u
					, "Biiingo!!!", "no se que poner aqui "
					, "4.jpg");
			base.add(tt);
		}
		//Dummy

	}
	private void pull()
	{
		HashMap<String, String> datos=new HashMap<String, String>();
		datos.put(Server.ID,ID);
		Server.setDataToSend(datos);
		Server.send("details", this, this);
	}
	private void makeDialog()
	{
		add = new DialogFragment()
		{
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState)
			{
				AlertDialog.Builder builder =
						new AlertDialog.Builder(Game.this);

				View root = Game.this.getLayoutInflater()
						.inflate(R.layout.details_on_play, null);

				ListView view =(ListView)findViewById(R.id.tables);

				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						/*if(which==DialogInterface.BUTTON_POSITIVE)
						{
						}
						else */dialog.dismiss();
					}
				};

				builder.setTitle("Detalles del juego")
						.setIcon(R.drawable.ic_launcher)
						.setView(view)
						.setNegativeButton("Cerrar", listener)
						.setPositiveButton(" Ok ", listener)
						.setCancelable(false);
				return builder.create();
			}
		};
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
