package com.jhordyabonia.bn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jhordyabonia.models.User;
import com.jhordyabonia.util.Server;
import com.jhordyabonia.webservice.Asynchtask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserActivity extends Activity implements View.OnClickListener,Asynchtask {

    EditText name, email, cel;
    TextView coins, new_user;//, pass, pass2;
    User user;
    private AdView mAdView;

    @Override
    public void onResume() {
        super.onResume();
        make();
    }

    private void resetPass() {
        HashMap<String, String> datos = new HashMap<String, String>();
        datos.put(User._CEL, user.cel());
        Server.setDataToSend(datos);
        Server.send("resetPass", null, this);
    }

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
        setContentView(R.layout.activity_user);
        user = new User(this);

        mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    private void make()
    {
        //share = root.findViewById(R.id.share);
        //new_user = (TextView) root.findViewById(R.id.new_user);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        cel = (EditText) findViewById(R.id.cel);
        name.setText(user.name());
        email.setText(user.email());
        cel.setText(user.cel());
        //ref = (EditText) root.findViewById(R.id.ref);
        // pass = (EditText) root.findViewById(R.id.pass);
        //  pass2 = (EditText) root.findViewById(R.id.pass2);
        findViewById(R.id.save).setOnClickListener(this);
        //findViewById(R.id.tableRow1).setOnClickListener(this);

        //share.setOnClickListener(this);
        //new_user.setOnClickListener(this);

        //coins = (TextView) root.findViewById(R.id.coins);
        //coins.setText("$" + user.coins());
    }

    @Override
    public void onClick(View arg0) {
        if (arg0.getId() == R.id.tableRow1) {
            //Intent intent = new Intent(this, Coins.class);
            //startActivity(intent);
        }  else if (arg0.getId() == R.id.save) {
            String _name = name.getText().toString().trim(),
                    // _pass = pass.getText().toString().trim(),
                    // _pass2 = pass2.getText().toString().trim(),
                    _cel = cel.getText().toString().trim(),
                    _email = email.getText().toString().trim();

            if (name.getVisibility() == View.VISIBLE) {
                // if (!validate("n" + _name, "e" + _email, "c" + _cel, "p" + _pass, "2" + _pass + ":=" + _pass2))
                if (!validate("n" + _name, "e" + _email, "c" + _cel))//temp no password now
                    return;
            } else if (!validate("c" + _cel))
                return;
            HashMap<String, String> datos = new HashMap<String, String>();
            datos.put(User._NAME, _name);
           // datos.put(User._PASS, _pass);
            datos.put(User._CEL, _cel);
            datos.put(User._EMAIL, _email);
            //if (user.ref().isEmpty())
            //    datos.put(User._REF, ref.getText().toString().trim());
           // else if (user.ref().equals("0"))
           //     datos.put(User._REF, ref.getText().toString().trim());
           // else datos.put(User._REF, "");

            Server.setDataToSend(datos);
            Server.send("singup",this, this);

        } else if (arg0.getId() == R.id.share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msj_share) + Server.URL_SERVER + "dw");
            intent.setType("text/plain");
            Intent chooser = Intent.createChooser(intent, getString(R.string.share));

            if (intent.resolveActivity(getPackageManager()) != null)
                startActivity(chooser);
        } else if (arg0.getId() == R.id.new_user) {
            name.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
            //pass2.setVisibility(View.VISIBLE);
            new_user.setVisibility(View.GONE);
        } else {
            coins.setText("$" + user.coins());
        }
    }

    private boolean validate(String... data) {
        int id = 0;
        String campo, result = "";
        for (String value : data) {
            campo = value.substring(1);
            switch (value.charAt(0)) {
                case 'n':
                    result = ValidacionNombre(campo);
                    id = R.id.name;
                    break;
                case 'e':
                    result = ValidacionEmail(campo);
                    id = R.id.email;
                    break;
                case 'c':
                    result = ValidacionCelular(campo);
                    id = R.id.cel;
                    break;
                case 'p':
                    if (campo.isEmpty())
                        if (!user.pass().isEmpty())
                            campo = user.pass();
                    result = ValidacionPassword(campo);
                    id = R.id.pass;
                    break;
                case '2':
                    if (user.name().isEmpty())
                        result = ValidacionPassword2(campo);
                    id = R.id.pass2;
                    break;
            }
            if (!result.isEmpty())
                break;
        }
        if (!result.isEmpty()) {
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            ((EditText) findViewById(id))
                    .setError(result);
            return false;
        }
        return true;
    }

    private String ValidacionPassword2(String input) {
        String _pass[] = input.split(":=");
        if (_pass.length == 2)
            if (_pass[0].equals(_pass[1]))
                return "";
        return "Las claves no coinciden";
    }

    private String ValidacionPassword(String input) {
        if (input.length() < 6)
            return "La clave debe tener minimo 6 caracteres";
        return "";
    }

    private String ValidacionNombre(String input) {
        if (!input.contains(" "))
            return "Ingrese su nombre completo";
        if (input.length() < 8)
            return "Ingrese su nombre completo";
        return "";
    }

    private String ValidacionCelular(String input) {
        if (input.length() < 10)
            return "El celular debe tener 10 digitos";
        return "";
    }

    private String ValidacionEmail(String input) {
        if (!input.contains("@"))
            return "El correo debe contener el caracter '@'";
        if (input.substring(0, input.lastIndexOf('@')).length() < 3)//Validacion email; debe tener dominio
            return "el correo debe tener nombre de usuario\ntest@test.com";
        String lt = input.substring(input.lastIndexOf('@'));//Validacion email; especifica el tipo de web, '.com','.com.co', '.co', '.net', etc
        if ((lt.length() < 3) || (!lt.contains(".")))
            return "el correo debe tener dominio";
        //if(input.lastIndexOf('.')!=-1)
        if (lt.substring(lt.lastIndexOf('.')).length() < 2)
            return "el correo debe tener dominio";
        if (input.replace("@", "").indexOf('@') != -1)
            return "el correo debe tener debe contener solo un caracter '@'";

        // comprueba que no contenga caracteres prohibidos
        Pattern p = Pattern.compile("[^A-Za-z0-9.@_-~#]+");
        Matcher m = p.matcher(input);
        StringBuffer sb = new StringBuffer();
        boolean resultado = m.find();
        boolean caracteresIlegales = false;

        while (resultado) {
            caracteresIlegales = true;
            m.appendReplacement(sb, "");
            resultado = m.find();
        }

        if (caracteresIlegales) {
            return "La cadena contiene caracteres ilegales";
        }
        return "";
    }

    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impos...!
        }
    }

    @Override
    public void processFinish(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject u = obj.getJSONObject("user");
            if (u != null) {
                user.setName(u.getString(User._NAME));
                user.setEmail(u.getString(User._EMAIL));
                user.setCel(u.getString(User._CEL));
                //String t = u.getString(User._REF);
                //if (!t.equals("null"))
                //   user.setRef(t);
                //String _pass = pass.getText().toString().trim();
                // if (u.getString(User._PASS).equals(md5(_pass)))
                //     user.setPass(u.getString(User._PASS));
            }
            JSONArray messages = obj.getJSONArray("reward");
            if (messages != null)
                for (int y = 0; y < messages.length(); y++) {
                    JSONObject message = messages.optJSONObject(y);
                    if (message != null){}
                    // rewards.add(message.getInt(User._COINS),
                    //       message.getString(Rewards._MESSAGE));
                }
            // rewards.save(getActivity());
        } catch (JSONException e) {
            try {
                new JSONObject(result);
            } catch (JSONException ee) {
                //  Toast.makeText(this, getString(R.string.network_err), Toast.LENGTH_SHORT).show();
            }
        }
    }

}