package com.jhordyabonia.bn;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jhordyabonia.models.Store;
import com.jhordyabonia.models.User;
import com.jhordyabonia.util.Server;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailBingo extends Fragment implements View.OnClickListener{
    View root;
    NoTable noTable;
    private JSONObject store;
    Bingo BINGO;
    User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        root =inflater.inflate(R.layout.fragment_detail_bingo,container,false);
        root.findViewById(R.id.launch).setOnClickListener(this);
        BINGO=(Bingo)getActivity();
        user= new User(BINGO);
        try {
            Bundle arg = getArguments();
            store = new JSONObject(arg.getString(Server.BINGO));
            load();
        } catch(JSONException e){}

        return root;
    }
    @Override
    public void onClick(View v) {
        String tables="";
        try {tables=store.getString(Game.TABLES);}
        catch(JSONException e){}
        if(BINGO.LOCAL)
            BINGO.launchGame();
        else if (tables.contains(user.cel()))
            BINGO.launchGame();
        else    noTable.show(BINGO.getSupportFragmentManager(),"missiles");
    }
    public static class NoTable extends DialogFragment
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getContext());

            Bundle args = getArguments();
            builder.setTitle(args.getString(Store.PAY_INFO));
            return builder.create();
        }
    };
    private void load() throws JSONException {

        ((TextView)root.findViewById(R.id.bingo_date))
                .setText(store.getString(Store.BINGO_DATE));
        ((TextView)root.findViewById(R.id.bingo_cost))
                .setText("$"+store.getString(Store.BINGO_COST));
        ((TextView)root.findViewById(R.id.awards_name))
                .setText(store.getString(Store.AWARDS_NAME));

        if(BINGO.LOCAL)
            ((TextView)root.findViewById(R.id.launch))
                 .setText("Jugar");
        else  if(store.getString(Game.TABLES).contains(user.cel()))
            ((TextView)root.findViewById(R.id.launch))
                    .setText("Jugar");
        else ((TextView)root.findViewById(R.id.launch))
                 .setText("Comprar Tabla");

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

        Bundle b= new Bundle();
        b.putString(Store.PAY_INFO,store.getString(Store.PAY_INFO));
        noTable= new NoTable();
        noTable.setArguments(b);
    }
}
