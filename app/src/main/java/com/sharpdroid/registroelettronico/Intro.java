package com.sharpdroid.registroelettronico;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class Intro extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(getString(R.string.benvenuto), "Verrai introdotto ad alcune funzionalità dell'app.", R.mipmap.ic_launcher, ContextCompat.getColor(this, R.color.bluematerial)));
        addSlide(AppIntroFragment.newInstance("Dettaglio medie", "Nei primi tre tab premi sulla scheda di una materia per visualizzare il dettaglio delle medie", R.drawable.intro1, ContextCompat.getColor(this, R.color.bluematerial)));
        addSlide(AppIntroFragment.newInstance("Media ipotetica", "oppure tieni premuto per visualizzare la media ipotetica", R.drawable.intro2, ContextCompat.getColor(this, R.color.bluematerial)));
        addSlide(AppIntroFragment.newInstance("Dettaglio voti", "Nel tab voti clicca su una scheda per visualizzare i commenti dei voti", R.drawable.intro3, ContextCompat.getColor(this, R.color.bluematerial)));
        addSlide(AppIntroFragment.newInstance("Agenda", "Nell'agenda le verifiche vengono indicate con dei pallini rossi, mentre i compiti con dei pallini gialli", R.drawable.intro4, ContextCompat.getColor(this, R.color.bluematerial)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addSlide(AppIntroFragment.newInstance("Un ultima cosa", "Abbiamo bisogno dei permessi di archiviazione per scaricare le circolari e i file dalla didattica", R.drawable.download, ContextCompat.getColor(this, R.color.bluematerial)));
            askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 6);
        }
        addSlide(AppIntroFragment.newInstance("Sei pronto!", "Ora sei pronto per utilizzare al meglio quest app. Ricorda, per qualsiasi problema contattaci a sharpdroidmail@gmail.com", R.drawable.done, ContextCompat.getColor(this, R.color.bluematerial)));
        setBarColor(ContextCompat.getColor(this, R.color.bluematerial));
        setSeparatorColor(Color.parseColor("#2196F3"));

        showSkipButton(true);
        setProgressButtonEnabled(true);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        ChiediPermessi();
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        ChiediPermessi();
        finish();
    }

    void ChiediPermessi() {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(Intro.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, result);
        }
    }
}
