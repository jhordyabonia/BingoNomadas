package com.jhordyabonia.models;

import android.content.Context;
import android.content.SharedPreferences;

public class User 
{
	public static final String USER = "com.jhordyabonia.bn.user",
		_NAME="name",_EMAIL="email",_CEL="cel",_REF="ref",_COINS="coins", _PASS = "pass" ;

	SharedPreferences user;
	SharedPreferences.Editor editor;
	public User(Context t)
	{		
		user = t.getSharedPreferences(USER, Context.MODE_PRIVATE);
		editor = user.edit();
	}
	public void setName(String name)
	{
		 editor.putString(_NAME,name);
		 editor.commit();	
	}
	public void setEmail(String email)
	{
		 editor.putString(_EMAIL,email);
		 editor.commit();
	}
	public void setCel(String cel )
	{
		if(!cel().isEmpty())
		  if(!cel().equals(cel))
			setCoins(0);
		 editor.putString(_CEL,cel);
		 editor.commit();
	}
	public void setRef(String ref )
	{
		if(ref().equals("0"))
			ref="";
		editor.putString(_REF,ref);
		editor.commit();
	}
	public void setCoins(int coins)
	{
		if(coins>99999)return;
		 editor.putInt(_COINS,coins);
		 editor.commit();
	}
	public String name()
	{return user.getString(_NAME,"");}
	public String email()
	{return user.getString(_EMAIL,"");}
	public String cel()
	{return user.getString(_CEL,"");}
	public int coins()
	{return user.getInt(_COINS,0);	}
	public String ref()
	{return user.getString(_REF,"");}
	public String pass()
	{return user.getString(_PASS,"");}

	public void setPass(String pass) {
		if(pass().equals("0"))
			pass="";
		editor.putString(_PASS,pass);
		editor.commit();
	}
}
