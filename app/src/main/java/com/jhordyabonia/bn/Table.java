package com.jhordyabonia.bn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Messenger;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jhordyabonia.models.Store;
import com.jhordyabonia.models.User;
import com.jhordyabonia.util.Server;
import com.jhordyabonia.webservice.Asynchtask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public  class Table extends Fragment implements OnClickListener, Connect.Inbox
{
	public static boolean WINNER=false;
	public static final String ARG_SECTION_NUMBER = "section_number";
	ArrayList<Integer> already= new ArrayList<Integer>();
	TextView number_now,last;
	private AdView mAdView;
	Popup noWin= new Popup(),Win=new Popup();
	ArrayList<Integer> table_values;
	View root;
	Game GAME;
	OnClickListener controlers=new OnClickListener() {
		@Override
		public void onClick(View arg0) {win();}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		GAME=(Game)getActivity();

		root= inflater.inflate(R.layout.table, container, false);
		root.findViewById(R.id.bingo).setOnClickListener(controlers);
		number_now=((TextView)root.findViewById(R.id.number_now));
		last=((TextView)root.findViewById(R.id.last));


		Messenger messenger= new Messenger(new Connect.MHandler(this));
		Intent intent = new Intent(GAME,Connect.class);
		intent.putExtra(Connect.MESSENGER, messenger);
		GAME.startService(intent);

		((AnimationDrawable)root.findViewById(R.id.tuto).getBackground()).start();

		Bundle args= new Bundle();
		args.putString(Store.PAY_INFO,"El organizador se contactará con los ganadores en un plazo no mayor, a 24 Horas. Para más información, revisa los datos de contacto.");
		Win.setArguments(args);

		makeTable();
		mAdView = (AdView)root.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		return root;
	}
	private ArrayList<Integer>  makeTable()
	{
		int count=75;
		table_values= new ArrayList<Integer>();
		Random r=new Random();
		int n=(r.nextInt(count)+1),c=0;
		do
		{
			while(table_values.indexOf(n)!=-1)
				if(c++>=count) {n=-1;break;}
				else n=(r.nextInt(count)+1);
			table_values.add(n);
		}while(table_values.size()<75);
		int m=0;
		for(int t:Game.ID_NUMBERS)
		{
			TextView v=(TextView)root.findViewById(t);
			v.setOnClickListener(this);
			String nn="0"+table_values.get(m++);
			String now=nn.substring(nn.length()-2,nn.length());
			v.setText(now);
		}
		return table_values;
	}
	@Override
	public void onClick(View arg0)
	{
		if(!WINNER) {
			try {
				TextView number = (TextView) arg0;
				int m = Integer.valueOf(number.getText().toString());
				for (int t : already)
					if (t == m)
				    {
						number.setBackgroundResource(R.drawable.number_marked);
						number.setTextColor(Color.WHITE);
						break;
					}
			} catch (NumberFormatException e) {}
		}else Win.show(GAME.getSupportFragmentManager(),"missiles");
	}
	@Override
	public void add_msj(int number)
	{
		already.add(number);
		String n="0"+number;
		String now=n.substring(n.length()-2,n.length());;
		String tmp=("12345678901"+last.getText()+" "+number_now.getText());
		last.setText(tmp.substring(tmp.length()-11,tmp.length()));
		number_now.setText(now);
		if(Game.AUDIO)
		{
			if(now.equals("00"))
			{
				now="Ha terminado";
				GAME.stopService(new Intent(GAME,Connect.class));
			}
			GAME.speaker.speak(now, TextToSpeech.QUEUE_FLUSH, null);
		}
	}
	@Override
	public void onDestroy()
	{
		GAME.stopService(new Intent(GAME,Connect.class));
		super.onDestroy();
	}

	public static class Popup extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder =
					new AlertDialog.Builder(getContext());
			String msj="Aun no has armado el Bingo";
			Bundle arg=getArguments();
			if(arg!=null) {
				msj = "Felicidades!!!";

				LayoutInflater inflater = getActivity().getLayoutInflater();
				TextView text =(TextView)inflater.inflate(R.layout.base, null);
				text.setText(arg.getString(Store.PAY_INFO));
				text.setTextColor(Color.BLACK);
				text.setBackgroundColor(Color.WHITE);
				builder.setView(text);
			}builder.setTitle(msj);
			return builder.create();
		}
	};
	private boolean get(int id)
	{
		return root.findViewById(id).getBackground()!=null;
	}
	private JSONObject toJSONObject(ArrayList t)
	{
		JSONObject out= new JSONObject();
		try
		{
			JSONArray table= new JSONArray();
			for(Integer i:table_values)
				table.put(i);
			out.put("table",table);
		}catch (JSONException e){}
		return out;
	}
    private void showWin()
	{
		root.findViewById(R.id.win).setOnClickListener(this);
		AnimationDrawable anim =(AnimationDrawable)
						root.findViewById(R.id.win).getBackground();
		ObjectAnimator animator= ObjectAnimator.ofFloat(
			root.findViewById(R.id.table),"alpha",1f,0f);
		animator.setInterpolator(new LinearInterpolator());
		animator.setDuration(anim.getDuration(0)*5);
		animator.addListener(new Animator.AnimatorListener() {
			@Override public void onAnimationStart(Animator animator) {}
			@Override public void onAnimationCancel(Animator animator) {}
			@Override public void onAnimationRepeat(Animator animator) {}
			@Override
			public void onAnimationEnd(Animator animator) {
				root.findViewById(R.id.last).setVisibility(View.GONE);
				root.findViewById(R.id.bingo).setVisibility(View.GONE);
				GAME.findViewById(R.id.commads).setVisibility(View.GONE);
				root.findViewById(R.id.win).setVisibility(View.VISIBLE);
				GAME.findViewById(R.id.pager).setBackgroundColor(Color.BLACK);
			}

		});
		animator.start();
		anim.start();
		WINNER=true;
	}
	private void win()
	{
		for(int y=0;y<WIN.length;y++) {
			int k = 0;
			for (int t = 0; t < WIN[y].length; t++)
				k+=get(WIN[y][t])?1:0;
			if(k==4)
			{
				if(Bingo.LOCAL)
				{
					showWin();
					GAME.stopService(new Intent(GAME,Connect.class));
					return;
				}

				HashMap<String, String> datos=new HashMap<String, String>();
				datos.put(Server.ID,Game.ID);
				datos.put(Server.CELLULAR, GAME.user.cel());
				datos.put(Game.TABLES, toJSONObject(table_values).toString());
				Server.setDataToSend(datos);
				Server.send("win", GAME,new Asynchtask()
				{
					@Override
					public void processFinish(String result) {
						try{
								Integer.valueOf(result);
								showWin();
							}catch(NumberFormatException e)
						{Toast.makeText(GAME,"Número máximo de ganadores superado o no tienes conexión a internet.",Toast.LENGTH_SHORT).show();}
						Toast.makeText(GAME,result,Toast.LENGTH_SHORT).show();
						GAME.stopService(new Intent(GAME,Connect.class));
					}
				});
				return;
			}
		}
		noWin.show(GAME.getSupportFragmentManager(),"missiles");
	}

	int[][] WIN=
		{
			{R.id.TextView15,R.id.TextView02,R.id.TextView24,R.id.TextView06},
			{R.id.TextView04,R.id.TextView17,R.id.TextView01,R.id.TextView05},
			{R.id.TextView18,R.id.TextView08,R.id.TextView03,R.id.TextView14},
			{R.id.TextView10,R.id.TextView02,R.id.TextView21,R.id.TextView06},
			{R.id.TextView12,R.id.TextView24,R.id.TextView07,R.id.TextView15}
		};
}