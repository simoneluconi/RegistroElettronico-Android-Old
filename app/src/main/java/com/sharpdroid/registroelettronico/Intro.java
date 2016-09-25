package com.sharpdroid.registroelettronico;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class Intro extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder()
                .title(getString(R.string.benvenuto))
                .description("Verrai introdotto ad alcune funzionalità dell'app.")
                .image(R.mipmap.ic_launcher)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Dettaglio medie")
                .description("Nei primi tre tab premi sulla scheda di una materia per visualizzare il dettaglio delle medie")
                .image(R.drawable.intro1)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Media ipotetica")
                .description("oppure tieni premuto per visualizzare la media ipotetica")
                .image(R.drawable.intro2)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Dettaglio voti")
                .description("Nel tab voti clicca su una scheda per visualizzare i commenti dei voti")
                .image(R.drawable.intro3)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Agenda")
                .description("Nell'agenda le verifiche vengono indicate con dei pallini rossi, mentre i compiti con dei pallini gialli")
                .image(R.drawable.intro4)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addSlide(new SimpleSlide.Builder()
                    .title("Un ultima cosa")
                    .description("Abbiamo bisogno dei permessi di archiviazione per scaricare le circolari e i file dalla didattica")
                    .image(R.drawable.download)
                    .background(R.color.intro_blue)
                    .backgroundDark(R.color.intro_blue_dark)
                    .permissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
                    .build());
        }
        addSlide(new SimpleSlide.Builder()
                .title("Sei pronto!")
                .description("Ora sei pronto per utilizzare al meglio quest app. Ricorda, per qualsiasi problema contattaci a sharpdroidmail@gmail.com")
                .image(R.drawable.done)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
    }
}
