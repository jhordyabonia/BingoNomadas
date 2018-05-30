package com.jhordyabonia.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.jhordyabonia.bn.Awards;
import com.jhordyabonia.models.Store;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

public class DownLoadImage  extends AsyncTask<String, Void, Bitmap> 
{
	private Awards awards;
	private View view;
    private boolean local=true;
    public DownLoadImage(Awards awards) 
    {this.awards=awards;  }
   
	 @Override
    protected Bitmap doInBackground(String... params) 
    {
    	String foto=params[0];
        Bitmap imagen=null ;
		try 
		{			
			if(local)
	        {
				File ruta_sd = Environment.getExternalStorageDirectory();
				File ruta = new File(ruta_sd.getAbsolutePath(), Store.DIRECTORY);
				if (!ruta.exists())
				{
					ruta.mkdir();
					(new File(ruta, ".nomedia")).mkdir();
				}

				File f = new File(ruta.getAbsolutePath(), foto);
				if (f.exists())
					imagen = BitmapFactory
	    				.decodeStream(new FileInputStream(f.getAbsolutePath()));
	        }
	        if(imagen==null)
	        {
				URL imageUrl = new URL(Server.URL_SERVER+"foto/"+foto);
				HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
				conn.connect();
				BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
				imagen = BitmapFactory.decodeFile(save(in,foto));
	        }
		}catch (IOException e)
		{Toast.makeText(view!=null?view.getContext():awards,"Imagen ("+foto+") no disponible", Toast.LENGTH_SHORT).show();}
		  
		return imagen;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) 
    {
	    if ( bitmap != null) 
	    	awards.load(bitmap);
    }
	public static String save(InputStream data, String file)
	{		
		Bitmap imagen = BitmapFactory.decodeStream(data);
		try 
		{
			File ruta_sd = Environment.getExternalStorageDirectory();
			File ruta = new File(ruta_sd.getAbsolutePath(), Store.DIRECTORY);
			if (!ruta.exists())
			{
				ruta.mkdir();
				(new File(ruta, ".nomedia")).mkdir();
			}

			File f = new File(ruta.getAbsolutePath(), file);
			try                 
			{
                f.createNewFile();
                FileOutputStream ostream = new FileOutputStream(f);
                imagen.compress(CompressFormat.JPEG, 100, ostream);
                ostream.close();
            }catch (Exception e){}
			return f.getAbsolutePath();
		} catch (Exception ex){}
		return "";
	}
   }

