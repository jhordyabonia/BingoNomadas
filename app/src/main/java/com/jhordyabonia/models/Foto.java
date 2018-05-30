package com.jhordyabonia.models;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jhordyabonia.bn.R;

public class Foto extends ArrayAdapter<Foto.Item>{

	int img=R.drawable.ic_launcher;
	Context context;
	private ArrayList<Item> locale;
	public Foto(Context c,ArrayList<Item> l)
	{	
		super(c, R.layout.item,l);
		locale=l;
		context=c;
	}
	public void setView(int drawable)
	{	img=drawable;}
	public void add(Item a)
	{
		locale.add(a);
		notifyDataSetChanged();
	}
	public static class Item 
	{
		int LOGO=0;
		boolean TO_IMPORT=false;
		Bitmap IMAGE=null;
		String NAME ="Image";
		public Item(){};
		public Item(Bitmap i,String n)
		{IMAGE=i;NAME=n;}		
		public Item(Bitmap i,boolean t)
		{IMAGE=i;TO_IMPORT=t;}		
		@Override
		public String toString()
		{return NAME;}
	}	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		
		final View root = inflater.inflate(R.layout.foto,null);
		Item data = locale.get(position);	
	    ((ImageView)root.findViewById(R.id.image))
	    .setImageBitmap(data.IMAGE);
		return root;
	}		
}
