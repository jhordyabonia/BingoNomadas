package com.jhordyabonia.models;

import java.util.Calendar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.jhordyabonia.bn.Game;

public class Store 
{
	public static final String STORE = "com.jhordyabonia.sbn.store",LAST_LOGUP="last_logup",
			BINGO_NAME="bingo_name",BINGO_DATE="bingo_date",BINGO_LOGO="bingo_logo",BINGO_COST="bingo_cost",DIRECTORY=".BingoNomadas",//DIRECTORY=".BingoNomadas",
			AUTHOR_NAME="author_name",AUTHOR_ADDRESS="author_address",AUTHOR_EMAIL="author_email",AUTHOR_CELLULAR="author_cellular",
			AWARDS_NAME="awards_name",AWARDS_IMAGES="wards_images",PAY_INFO="pay_info",PAY_ACCOUNT_NUMBER="pay_account_number";
		
	JSONObject store;
	public Store(Context t)
	{		
		SharedPreferences sharedPref = 
				t.getSharedPreferences(STORE, Context.MODE_PRIVATE);
		String data=sharedPref.getString(STORE,"");
		
		try {store=new JSONObject(data);}
		catch (JSONException e) 
		{store=new JSONObject();}
	}
	public String id()
	{ 
		try{return store.getString(AUTHOR_CELLULAR);}
		catch(JSONException e){return "";}
	}
	public String last_logup()
	{
		try{return store.getString(LAST_LOGUP);}
		catch(JSONException e){return "";}
	}
	private void _last_logup()
	{
		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		
		String date=""+c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE)+ " "
				+c.get(Calendar.DAY_OF_MONTH)+"-"+c.get(Calendar.MONTH)+"-"+c.get(Calendar.YEAR);
		
		try{ store.put(LAST_LOGUP,date);}
		catch(JSONException e){}
	}
	public JSONArray getImages() throws JSONException
	{return store.getJSONArray(Store.AWARDS_IMAGES);}
	public void putImages(JSONArray images) throws JSONException
	{store.put(Store.AWARDS_IMAGES,images);}
	public void putTable(String table)throws JSONException
	{store.getJSONArray(Game.TABLES).put(table);_last_logup();}
	public JSONObject get(){return store;}
	public void put(String bn,String bd,String bl,String bc,String an,
			String aa,String ac,String ae,String awn,String awi,String pi,String pa) throws JSONException
	{
		store.put(BINGO_NAME, bn);
		store.put(BINGO_DATE, bd);
		store.put(BINGO_LOGO, bl);
		store.put(BINGO_COST, bc);

		store.put(AUTHOR_NAME, an);
		store.put(AUTHOR_ADDRESS, aa);
		store.put(AUTHOR_CELLULAR, ac);
		store.put(AUTHOR_EMAIL, ae);

		store.put(AWARDS_NAME, awn);
		store.put(AWARDS_IMAGES, awi);

		store.put(PAY_INFO, pi);
		store.put(PAY_ACCOUNT_NUMBER, pa);	
		if(store.isNull(Game.TABLES))
			store.put(Game.TABLES,new JSONArray());
	}
	public void save(Context t)
	{
		SharedPreferences sharedPref = 
				t.getSharedPreferences(STORE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(STORE, store.toString());
        editor.commit();		
	}

	public void set(Context t,String data)
	{
		try {store=new JSONObject(data);}
		catch (JSONException e)
		{store=new JSONObject();}
		save(t);
	}
}
