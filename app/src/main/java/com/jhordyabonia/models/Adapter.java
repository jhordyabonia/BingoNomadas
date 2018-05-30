package com.jhordyabonia.models;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jhordyabonia.bn.R;

public class Adapter extends ArrayAdapter<Adapter.Item>{

    int img=R.drawable.ic_launcher;
    Context context;
    private ArrayList<Item> locale;
    public Adapter(Context c,ArrayList<Item> l)
    {
        super(c,R.layout.item,l);
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
        int LOGO=0;;
        String NAME ,CELLULAR,EMAIL;
        public Item(String n,String c,String e)
        {
            NAME=n;CELLULAR=c;EMAIL=e;
        }
        Item(String n,String m,int l)
        {
            NAME=n;CELLULAR=m;LOGO=l;
        }
        @Override
        public String toString()
        {return NAME+" "+CELLULAR+" "+EMAIL;}
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        final View root = inflater.inflate(R.layout.item,null);
        Item tmp = locale.get(position);
        ((ImageView)root.findViewById(R.id.img))
                .setImageResource(img);

        ((TextView)root.findViewById(R.id.cellular))
                .setText(tmp.CELLULAR);
        ((TextView)root.findViewById(R.id.email))
                .setText(tmp.EMAIL);
        ((TextView)root.findViewById(R.id.name))
                .setText(tmp.NAME);

        if(tmp.LOGO!=0)
            ((ImageView)root.findViewById(R.id.img))
                    .setImageResource(tmp.LOGO);

        return root;
    }

}