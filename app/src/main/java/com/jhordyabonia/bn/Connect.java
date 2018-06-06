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
		public  static final String MESSENGER = "messenger";
		private  static final String MENSAJE_NUEVO = "mensaje_nuevo";
		private Random ram=new Random();
		private Messenger messenger;
		private int count=75;
		private  ArrayList<Integer> already= new ArrayList<Integer>();
    	User user;
		protected boolean STOP=false;
    	public interface Inbox
    	{
    		public void add_msj(int number) throws JSONException;
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
    		already.clear();
    		user=new User(this);
    		if(extras !=null)
				messenger = (Messenger) extras.get(MESSENGER);
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
    		}catch(NumberFormatException e){}
    		if(!STOP)
    			get();
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
					processFinish(""+n);
					count=75;
					}
				}	
			}).execute();
    	}
		@Override
		public IBinder onBind(Intent arg0) {return null;}
    }