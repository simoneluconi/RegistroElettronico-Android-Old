package com.sharpdroid.registroelettronico;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class Link extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri data = intent.getData();
        String path = data.getPath();
        Log.v("Link", data + path);

        Intent start = new Intent(this, MainActivity.class);
        if (path.contains("genitori_voti.php")) {
            start = new Intent(this, MainActivity.class);
            start.putExtra("com.sharpdroid.registroelettronico.notifiche.TAB", 3);
        } else if (path.contains("agenda_studenti.php")) {
            start = new Intent(this, MainActivity.class);
            start.putExtra("com.sharpdroid.registroelettronico.notifiche.TAB", 4);
        } else if (path.contains("gioprof_note_studente.php")) {
            start = new Intent(this, MainActivity.class);
            start.putExtra("com.sharpdroid.registroelettronico.notifiche.TAB", 5);
        } else if (path.contains("didattica_genitori.php")) {
            start = new Intent(this, MainActivity.class);
            start.putExtra("com.sharpdroid.registroelettronico.notifiche.TAB", 6);
        } else if (path.contains("regclasse.php"))
            start = new Intent(this, OggiAScuola.class);
        else if (path.contains("bacheca_utente.php"))
            start = new Intent(this, Circolari.class);
        else if (data.toString().contains("login")) {
            String custcode = "custcode=";
            String url = data.toString();
            if (url.contains(custcode)) {
                String codicescuola = null;
                try {
                    codicescuola = url.substring(url.indexOf(custcode) + custcode.length());
                    if (codicescuola.contains("&"))
                        codicescuola = codicescuola.substring(0, codicescuola.indexOf("&"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent login = new Intent(this, LoginActivity.class);
                login.putExtra("com.sharpdroid.registroelettronico.codicescuola", codicescuola);
                start = login;

            }
        } else start = new Intent(this, MainActivity.class);

        this.startActivity(start);
        finish();
    }
}
