package com.jhordyabonia.bn;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONException;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.jhordyabonia.models.User;
import com.jhordyabonia.util.Server;
import com.jhordyabonia.webservice.Asynchtask;

 public class Connect extends Service implements Asynchtask 
    {	
		public  static final String MESSENGER = "messenger",ALREADY="already",MENSAJE_NUEVO = "mensaje_nuevo";
		private Random ram=new Random();
		private Messenger messenger;
		private int count=75,win=75;
		private  ArrayList<Integer> already= new ArrayList<Integer>();
    	User user;
		public static boolean STOP=false;

    	public interface Inbox
    	{
    		 void add_msj(int number) throws JSONException;
    	} 
    	public static class MHandler extends Handler 
    	{
    		private Inbox mInbox;
    		public MHandler(Inbox inbox)
    		{mInbox=inbox;}
    	    public void handleMessage(Message message) 
    	    {
    	         Bundle data = message.getData();             
    	         if(data != null) 
    	         {
    	        	int nuevo = data.getInt(MENSAJE_NUEVO);
    	     		try 
    	     		{	
    	     			mInbox.add_msj(nuevo);
    	     		} catch (JSONException e) {}
    	         }
    	    }
    	}
    	@Override
    	public int onStartCommand(Intent intent, int flags, int startId) 
    	{
    		Bundle extras = intent.getExtras();
    		STOP=false;
    		user=new User(this);
    		if(extras !=null) {
				messenger = (Messenger) extras.get(MESSENGER);
				already= (ArrayList<Integer>) extras.get(ALREADY);
			}
			Random r= new Random();
    		win=15+r.nextInt(25);
			get();
    		return super.onStartCommand(intent, flags, startId);	
    	}	
    	@Override
    	public void processFinish(String result) 
    	{
    		try
    		{
    			int t=Integer.valueOf(result);
    			nuevos(t);
    			if(t==0)
    				STOP=true;
    		}catch(NumberFormatException e){}
    		if(!STOP)
    			get();
    		else {
    			this.stopSelf(0);
				this.onDestroy();
    		}
    	}
    	private void nuevos(int result) 
    	{
    		if(messenger==null)
    			return;
    		
    		Message msg = Message.obtain();   
        	Bundle bundle =new Bundle();
        	bundle.putInt(MENSAJE_NUEVO, result);
        	msg.setData(bundle);
        	try{messenger.send(msg);}
        	catch(android.os.RemoteException e1) {}
        }
        private void pull()
		{
			HashMap<String, String> datos=new HashMap<String, String>();
			datos.put(Server.CELLULAR,Game.ID);
			Server.setDataToSend(datos);
			Server.send(Game.ONPLAY, null, this);
		}
		private void get()
    	{(new AsyncTask<Boolean, Void, Boolean>()
			{
		        @Override
				protected Boolean doInBackground(Boolean... arg0)
				{
					try {Thread.sleep(Bingo.TIMMER);}
					catch (InterruptedException e) {}
					return false;
				}
				@Override
				protected void onPostExecute(Boolean v)
				{
					if(!Bingo.LOCAL)
					{	pull(); }
					else {
					int n=(ram.nextInt(count)+1),c=0;
					while(already.indexOf(n)!=-1)
						if(c++>=count) {n=0;STOP=true;break;}
						else n=(ram.nextInt(count)+1);
					already.add(n);
					if(already.size()>win)
						STOP=true;
					processFinish(""+n);
					count=75;
					}
				}	
			}).execute();
    	}
		@Override
		public IBinder onBind(Intent arg0) {return null;}
    }