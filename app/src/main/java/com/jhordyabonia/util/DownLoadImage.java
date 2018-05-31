package com.jhordyabonia.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import com.jhordyabonia.bn.Bingo;
import com.jhordyabonia.models.Store;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

public class DownLoadImage  extends AsyncTask<String, Void, Bitmap> 
{
    final Activity activity;
    boolean local=true;
    Bingo.Gallery callback=null;
    public DownLoadImage(Activity activity)
    {this.activity=activity;}
    public void setCallBack(final  Bingo.Gallery callback)
    {this.callback=callback;}

	public static String save(InputStream data, String file)
	{
		try 
		{
			File ruta_sd = Environment.getExternalStorageDirectory();
			File ruta = new File(ruta_sd.getAbsolutePath(), Store.DIRECTORY);
			if (!ruta.exists())
			{
				ruta.mkdir();
				(new File(ruta, ".nomedia")).mkdir();
			}
			File f =  new File(ruta,file);
			Bitmap imagen = BitmapFactory.decodeStream(data);
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
    // Decode image in background.
    @Override
    protected synchronized Bitmap doInBackground(String... fotos)
    {
        Bitmap imagen=null ;
		try 
		{
			//BitmapFactory.Options options = new BitmapFactory.Options();
	        //options.inSampleSize = 2; // el factor de escala a minimizar la imagen, siempre es potencia de 2
	        if(local)
	        {
	    		File ruta_sd = Environment.getExternalStorageDirectory(); 
	    		File ruta = new File(ruta_sd.getAbsolutePath(),Store.DIRECTORY+"//"+fotos[0]);
	    		if(ruta.exists())
	    		{
	    			FileInputStream in= new FileInputStream(ruta);	    		
		    		imagen = BitmapFactory.decodeStream(in);
		    		if(imagen==null) local=false;
	    		}else local=false;
	        }
	        if(!local)
	        {
				URL imageUrl = new URL(Server.URL_SERVER.replace("bn/","uploads/bn/")+fotos[0]);
				HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
				conn.connect();
				BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
				imagen = BitmapFactory.decodeFile(save(in,fotos[0]));
	        }
		}catch (IOException e){}
				     
        return imagen;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) 
    {
    	if(bitmap != null)
        {
        	if(callback!=null)
            	callback.addItem(bitmap);
            return;
        }
    }
}

