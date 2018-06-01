package com.jhordyabonia.bn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jhordyabonia.models.Store;
import com.jhordyabonia.util.Server;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailBingo extends Fragment implements View.OnClickListener{
    View root;
    private JSONObject store;
    Bingo BINGO;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        root =inflater.inflate(R.layout.fragment_detail_bingo,container,false);
        root.findViewById(R.id.launch).setOnClickListener(this);
        BINGO=(Bingo)getActivity();
        Bundle arg = getArguments();
        try {
            store = new JSONObject(arg.getString(Server.BINGO));
            load();
        } catch(JSONException e){}

        return root;
    }
    @Override
    public void onClick(View v) {
        BINGO.launchGame();
    }
    private void load() throws JSONException {

        ((TextView)root.findViewById(R.id.bingo_date))
                .setText(store.getString(Store.BINGO_DATE));
        ((TextView)root.findViewById(R.id.bingo_cost))
                .setText("$"+store.getString(Store.BINGO_COST));
        ((TextView)root.findViewById(R.id.awards_name))
                .setText(store.getString(Store.AWARDS_NAME));

       // ((TextView)root.findViewById(R.id.bingo_description))
       //         .setText("");

        ((TextView)root.findViewById(R.id.author_name))
                .setText(store.getString(Store.AUTHOR_NAME));

        StringBuilder CONTAC_INFO= new StringBuilder();

        CONTAC_INFO.append("Email: ");
        CONTAC_INFO.append(store.getString(Store.AUTHOR_EMAIL));
        CONTAC_INFO.append("\nWhatsapp: ");
        CONTAC_INFO.append(store.getString(Store.AUTHOR_CELLULAR));
        ((TextView)root.findViewById(R.id.author_contac))
                .setText(CONTAC_INFO);

        ((TextView)root.findViewById(R.id.author_address))
                .setText(store.getString(Store.AUTHOR_ADDRESS));
    }
}
