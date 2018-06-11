package com.jhordyabonia.util;

import java.util.HashMap;

import com.jhordyabonia.webservice.Asynchtask;
import com.jhordyabonia.webservice.Client;

import android.app.Activity;

public final class Server {
	//public static String URL_SERVER = "http://floverso-001-site20.gtempurl.com/sample/bn/";
	public static String URL_SERVER = "http://123seller.azurewebsites.net/bn/";
	//public static String URL_SERVER = "http://192.168.33.10/bn/bn/";
	public static final String ID="id",BINGO="bingo",CELLULAR="cellular",_TIMMER="timmer",_LOCAL="local",_LOTTO="lotto";
	
	private static Client ws;
	private static HashMap<String, String> data_toSend = new HashMap<String, String>();

	public static final void setDataToSend(HashMap<String, String> toSend) {
		data_toSend = toSend;
	}

	public static void send(String url, Activity a, Asynchtask t) {
		ws = new Client();
		ws.setUrl(URL_SERVER + url);
		ws.setDatos(data_toSend);
		ws.setActividad(a);
		ws.setCallback(t);
		ws.execute("");
	}
}
