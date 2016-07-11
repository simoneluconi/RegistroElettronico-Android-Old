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

        Intent start;
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
        else start = new Intent(this, MainActivity.class);

        this.startActivity(start);

        finish();
    }
}
