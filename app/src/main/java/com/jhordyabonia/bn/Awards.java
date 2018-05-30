package com.jhordyabonia.bn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;

import com.jhordyabonia.models.Foto;
import com.jhordyabonia.models.Store;
import com.jhordyabonia.util.DownLoadImage;
import com.jhordyabonia.util.UploadService;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Awards extends FragmentActivity implements OnClickListener{
    private static final int CAPTURE_APUNTE = 777;
    private static final boolean EDITAR = false;
    public static String ID_ASIGNATURA = "";
    public static boolean fullScream = false,zoom=false;
    private String APUNTE_FOCUS="";
    private boolean adding=false;
    private ArrayList<String> apuntes= new ArrayList<String>();
    JSONArray awards_images;
    public Store store;
    int FOCUS=-1;
    Foto base;
    ListView gallery;
    private ObjectAnimator animator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_awards);
        store= new Store(this);
        try {
            awards_images=store.getImages();
        } catch (JSONException e1) {awards_images= new JSONArray();}

        findViewById(R.id.take_new)
                .setOnClickListener(this);
        findViewById(R.id.add_new)
                .setOnClickListener(this);
        findViewById(R.id.delete)
                .setOnClickListener(this);

        gallery = (ListView) findViewById(R.id.list_foto);
        gallery.setOnTouchListener(new OnTouchListener(){

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if(fullScream)
                {
                    findViewById(R.id.fullscreen_content_controls)
                            .setAlpha(1);
                    animator.start();
                }else
                    findViewById(R.id.fullscreen_content_controls)
                            .setAlpha(0);
                fullScream=!fullScream;
                System.gc();
                return false;
            }});
        OnScrollListener l= new OnScrollListener()
        {
            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3)
            {
                int t=gallery.getFirstVisiblePosition();
                try
                {
                    ((TextView)findViewById(R.id.editText1))
                            .setText(awards_images.getString(t));
                    FOCUS=t;
                } catch (JSONException e) {}
            }
            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1){}
        };
        gallery.setOnScrollListener(l);
        base = new Foto(this,new ArrayList<Foto.Item>());
        gallery.setAdapter(base);

        animator  = ObjectAnimator.ofFloat(findViewById(R.id.fullscreen_content_controls)
                ,"alpha",1,1,1,1,1,1,1,1,1,0);
        animator.setDuration(3000);
        animator.setRepeatMode(Animation.RESTART);
        load();
    }

    public void load(Bitmap b)
    {
        base.add(new Foto.Item(b,true));
        gallery.setSelection(base.getCount()-1);
    }
    private void load()  {
        base.clear();
        for(int h=0;h<awards_images.length();h++) try
        {
            (new DownLoadImage(this))
                    .execute(awards_images.getString(h));
        } catch (JSONException e) {}
    }

    private File createImageFile() throws IOException
    {
        File ruta_sd = Environment.getExternalStorageDirectory();
        File ruta = new File(ruta_sd.getAbsolutePath(), Store.DIRECTORY);
        if (!ruta.exists())
        {
            ruta.mkdir();
            (new File(ruta, ".nomedia")).mkdir();
        }

        return new File(ruta,makeName());
    }
    private String makeName()
    {	return store.id()+"_image"+base.getCount()+".jpg";	}
    private void addImage()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
        } catch (IOException e) {}
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, CAPTURE_APUNTE);
        else
            Toast.makeText(this, "Error, Camara no disponible",
                    Toast.LENGTH_LONG).show();
    }

    protected void setEnabled(int id, boolean v)
    {
        View view = findViewById(id);
        if (view != null)
            view.setEnabled(v);
    }
    private void show(boolean show)
    {
        setEnabled(R.id.name, show);
    }

    private void send(View v)
    {
        ImageView button = ((ImageView) v);
        if (EDITAR)
        {
            button.setImageResource(android.R.drawable.ic_menu_save);
            show(true);
            return;
        }else
            button.setImageResource(android.R.drawable.ic_menu_edit);
    }
    private String set(String value, int id)
    {

        EditText v = (EditText) findViewById(R.id.name);
        if (v != null)
            v.setText(value);
        return value;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(awards_images.length()>0)
            animator.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) try
        {
            if (requestCode == CAPTURE_APUNTE)
            {
                awards_images.put(makeName());
                (new DownLoadImage(this))
                        .execute(makeName());
            }else
            {
                Uri uri = data.getData();
                // Image captured and saved to fileUri specified in the Intent
                try
                {
                    InputStream in = getContentResolver().openInputStream(uri);

                    Bitmap imageBitmap = BitmapFactory.decodeStream(in);
                    base.add(new Foto.Item(imageBitmap,true));
                    gallery.setSelection(FOCUS);
                    DownLoadImage.save(getContentResolver()
                            .openInputStream(uri),makeName());
                }catch(FileNotFoundException e){}
            }

            awards_images.put(makeName());
            fix();
            store.putImages(awards_images);
            store.save(this);
            uploadImages();
        } catch (JSONException e) {}
    }
    private  void uploadImages()
    {
        String list="";
        for(int h=0;h<awards_images.length();h++)
        {
            try {list+=","+awards_images.getString(h);}
            catch (JSONException e) {continue;}
        }
        if(list.startsWith(","))
            list=list.substring(1);
        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra(UploadService.LIST_TO_UPLOAD, list);
        startService(intent);
    }
    private void fix()
    {
        JSONArray out=new JSONArray();
        for(int h=0;h<awards_images.length();h++)
        {
            try
            {
                if(!awards_images.isNull(h))
                    out.put(awards_images.getString(h));
            }catch (JSONException e) {continue;}
        }
        awards_images=out;
    }
    private void delete()
    {
        try
        {
            awards_images.put(FOCUS, null);
            base.remove(base.getItem(FOCUS));

            fix();
            store.putImages(awards_images);
            store.save(this);

            Toast.makeText(this, "Imagen eliminda",
                    Toast.LENGTH_LONG).show();

            int t=gallery.getFirstVisiblePosition();
            ((TextView)findViewById(R.id.editText1))
                    .setText(awards_images.getString(t));
            FOCUS=t;
            gallery.setSelection(FOCUS);
        } catch (JSONException e) {}
    }
    @Override
    public void onClick(View arg0)
    {
        switch(arg0.getId())
        {
            case R.id.add_new:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent. setTypeAndNormalize("image/JPG");

                Intent chooser = Intent.createChooser(intent, "Abrir Imagen");

                // Verify the intent will resolve to at least one activity
                if (chooser.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(chooser,1);
                }
                break;
            case R.id.take_new:
                addImage();
                break;
            case R.id.edit:
                addImage();
                break;
            case R.id.delete:
                delete();
                break;
            default:
        }
        animator.setupStartValues();
        animator.start();
    }
}