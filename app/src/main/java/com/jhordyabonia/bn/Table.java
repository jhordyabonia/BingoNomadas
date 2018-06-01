package com.jhordyabonia.bn;

import java.util.ArrayList;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public  class Table extends Fragment implements OnClickListener, Connect.Inbox
{
	public static final String ARG_SECTION_NUMBER = "section_number";    
	ArrayList<Integer> already= new ArrayList<Integer>();
	TextView number_now;
	private AdView mAdView;
	View root;
    Game GAME;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        root= inflater.inflate(R.layout.table, container, false);
        number_now=((TextView)root.findViewById(R.id.number_now));
        GAME=(Game)getActivity();

		Messenger messenger= new Messenger(new Connect.MHandler(this));
		Intent intent = new Intent(GAME,Connect.class);
		intent.putExtra(Connect.MESSENGER, messenger);
		GAME.startService(intent);

        makeTable();
		mAdView = (AdView)root.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
    	return root;
    }
    private void makeTable()
    {
    	int count=75;
		ArrayList<Integer> _already= new ArrayList<Integer>();
		Random r=new Random();
		int n=(r.nextInt(count)+1),c=0;
		do
		{
			while(_already.indexOf(n)!=-1)
				if(c++>=count) {n=-1;break;}
				else n=(r.nextInt(count)+1);
			_already.add(n);
		}while(_already.size()<75);
		int m=0;		
		for(int t:Game.ID_NUMBERS)
		{   
			TextView v=(TextView)root.findViewById(t);
			v.setOnClickListener(this);
			String nn="0"+_already.get(m++);
			String now=nn.substring(nn.length()-2,nn.length());
			v.setText(now);
		}	
    }
    @Override
	public void onClick(View arg0) 
	{
	    try
	    {
			int m=Integer.valueOf(((TextView)arg0).getText().toString());			
    		for(int t:already)
    			if(t==m)
    			{
    				arg0.setBackgroundResource(R.drawable.number_marked);
    				if(Game.AUDIO)
    					GAME.mAudio.start();
    				break;
    			}
    	}catch(NumberFormatException e){}
	}
	@Override
	public void add_msj(int number)
	{
		already.add(number);
		String n="0"+number;
		String now=n.substring(n.length()-2,n.length());
		number_now.setText(now);
		if(Game.AUDIO)
		{
			if(now.equals("00"))
				now="Ha terminado";
			GAME.speaker.speak(now, TextToSpeech.QUEUE_FLUSH, null);
		}
	} 	
	@Override
	public void onDestroy()
	{
		GAME.stopService(new Intent(GAME,Connect.class));
		super.onDestroy();
	}
}