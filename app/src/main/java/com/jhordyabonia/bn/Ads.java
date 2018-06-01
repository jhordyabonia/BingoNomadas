package com.jhordyabonia.bn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jhordyabonia.models.Store;
import com.jhordyabonia.util.Server;

import org.json.JSONObject;

public class Ads extends Fragment {
    private AdView mAdView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.ads, container, false);

        mAdView = (AdView)view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Bundle arg = getArguments();
        ((TextView)view.findViewById(R.id.info1))
            .setText(arg.getString(Store.BINGO_NAME));
        return view;
    }
}
