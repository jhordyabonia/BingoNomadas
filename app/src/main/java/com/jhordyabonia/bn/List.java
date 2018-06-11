package com.jhordyabonia.bn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jhordyabonia.models.Adapter;
import com.jhordyabonia.models.Store;
import com.jhordyabonia.models.User;
import com.jhordyabonia.util.Server;
import com.jhordyabonia.webservice.Asynchtask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class List extends FragmentActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,SearchView.OnQueryTextListener,Asynchtask {

    Adapter base;
    ListView view;
    SearchView mSearchView;
    User user;
    JSONArray store_raw;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFlags(
                WindowManager.LayoutParams.TYPE_APPLICATION_MEDIA,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_list);

        findViewById(R.id.share).setOnClickListener(this);
        findViewById(R.id.more_game).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);

        base = new Adapter(this,new ArrayList<Adapter.Item>());
        view =findViewById(R.id.tables);
        mSearchView =  findViewById(R.id.search);
        view.setAdapter(base);
        view.setDividerHeight(0);
        view.setTextFilterEnabled(true);
        view.setOnItemClickListener(this);
        setupSearchView();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        user=new User(this);

        if (user.cel().isEmpty()) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        pull();
       }

        private void setupSearchView() {
            mSearchView.setIconifiedByDefault(false);
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setSubmitButtonEnabled(false);
            mSearchView.setQueryHint("Buscar");
        }

        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                view.clearTextFilter();
            } else {
                view.setFilterText(newText.toString());
            }
            return true;
        }

        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        private void pull()
        {
            Server.setDataToSend(new HashMap<String, String>());
            Server.send("bingos", null, this);
        }
        @Override
        public void processFinish(String json)
        {
            String title="Bingo Nomada";

            ((TextView)findViewById(R.id.title))
                    .setText(title);
            try
            {
                store_raw=new JSONArray(json);

                base.clear();
                for(int u=0;u<store_raw.length();u++) {
                    JSONObject obj =
                        new JSONObject(store_raw.getJSONObject(u).getString(Server.BINGO));
                    int n=obj.getJSONArray(Game.TABLES).length();
                    Adapter.Item tt = new Adapter.Item(obj.getString(Store.BINGO_NAME)
                            , obj.getString(Store.BINGO_COST), "Tablas registradas: "+n
                            , obj.getString(Store.BINGO_LOGO));

                    base.add(tt);
                }
                base.setDropDownViewResource(base.getCount() - 1);
            }catch(JSONException e){}
        }
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
        {
           if(arg2<store_raw.length()) {
               try {
                   Intent intent = new Intent(List.this, Bingo.class);
                   String id = store_raw.getJSONObject(arg2).getString(Server.ID);
                   String bingo = store_raw.getJSONObject(arg2).getString(Server.BINGO);

                   int Mtimmer = store_raw.getJSONObject(arg2).getInt(Server._TIMMER);
                   boolean local = store_raw.getJSONObject(arg2).getInt(Server._LOCAL)!=0;
                   boolean lotto = store_raw.getJSONObject(arg2).getInt(Server._LOTTO)!=0;
                   intent.putExtra(Server._LOCAL, local);
                   intent.putExtra(Server._LOTTO, lotto);
                   intent.putExtra(Server._TIMMER, Mtimmer);

                   intent.putExtra(Server.ID, id);
                   intent.putExtra(Server.BINGO, bingo);
                   startActivity(intent);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
        }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msj_share) + Server.URL_SERVER + "dw");
            intent.setType("text/plain");
            Intent chooser = Intent.createChooser(intent, getString(R.string.share));

            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(chooser);
        }else if (view.getId() == R.id.settings) {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        } else if (view.getId()  == R.id.more_game) {
            String url = "http://123seller.azurewebsites.net/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }
}