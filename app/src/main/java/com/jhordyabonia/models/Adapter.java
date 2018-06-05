package com.jhordyabonia.models;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jhordyabonia.bn.R;
import com.jhordyabonia.util.Server;

public class Adapter extends ArrayAdapter<Adapter.Item>{

    Context context;
    int layout=R.layout.item;
    private ArrayList<Item> locale;
    public Adapter(Context c,ArrayList<Item> l)
    {
        super(c,R.layout.item,l);
        locale=l;
        context=c;
    }

    public Adapter(Context c,ArrayList<Item> l,int layout)
    {
        super(c,layout,l);
        locale=l;
        context=c;
        this.layout=layout;
    }
    private void setImage(final View root,final String image)
    {
        (new AsyncTask<String, Void, Bitmap>()
        {
            @Override
            protected synchronized Bitmap doInBackground(String... fotos)
            {
                Bitmap imagen=null ;
                try
                {
                    URL imageUrl = new URL(Server.URL_SERVER.replace("bn","uploads/bn/")+image);
                    HttpURLConnection urlConnection = (HttpURLConnection) imageUrl.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    imagen = BitmapFactory.decodeStream(inputStream);
                }catch (IOException e){}

                return imagen;
            }
            @Override
            protected void onPostExecute(Bitmap bitmap)
            {
                ((ImageView)root.findViewById(R.id.img))
                        .setImageBitmap(bitmap);
            }
        }).execute();
    }
    public void add(Item a)
    {
        locale.add(a);
        notifyDataSetChanged();
    }
    public static class Item
    {
        String LOGO, N_TABLES,NAME ,COST;
        public Item(String n,String c,String nt,String l)
        { NAME=n;COST=c;N_TABLES=nt;LOGO=l; }
        @Override
        public String toString()
        {return NAME+" "+COST+" "+N_TABLES;}
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        final View root = inflater.inflate(layout,null);
        Item tmp = locale.get(position);

        ((TextView)root.findViewById(R.id.cellular))
                .setText("$"+tmp.COST);
        ((TextView)root.findViewById(R.id.name))
                .setText(tmp.NAME);
        ((TextView)root.findViewById(R.id.email))
                .setText(tmp.N_TABLES);

        if(!tmp.LOGO.isEmpty())
            setImage(root,tmp.LOGO);

        return root;
    }

}

