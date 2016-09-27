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
                .description(R.string.intro_app_desc)
                .image(R.mipmap.ic_launcher)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.dettagli_medie_titolo)
                .description(R.string.dettaglio_media_desc)
                .image(R.drawable.intro1)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.media_ipotetica_titolo)
                .description(R.string.media_ipotetica_desc)
                .image(R.drawable.intro2)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.dettaglio_voti_titolo)
                .description(R.string.dettaglio_voti_desc)
                .image(R.drawable.intro3)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.agenda_titolo)
                .description(R.string.agenda_desc)
                .image(R.drawable.intro4)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.ultima_cosa_titolo)
                    .description(R.string.ultima_cosa_desc)
                    .image(R.drawable.download)
                    .background(R.color.intro_blue)
                    .backgroundDark(R.color.intro_blue_dark)
                    .permissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
                    .build());
        }
        addSlide(new SimpleSlide.Builder()
                .title(R.string.sei_pronto_titolo)
                .description(R.string.sei_pronto_desc)
                .image(R.drawable.done)
                .background(R.color.intro_blue)
                .backgroundDark(R.color.intro_blue_dark)
                .build());
    }
}
