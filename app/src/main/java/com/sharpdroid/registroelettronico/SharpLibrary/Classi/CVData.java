package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import android.text.SpannableString;

public class CVData {
    public final String title;
    public String des = "";
    public SpannableString dess;
    public final String media;
    public final Float prog;


    public CVData(String title, String des, String media, Float prog) {
        this.title = title;
        this.des = des;
        this.media = media;
        this.prog = prog;
    }

    public CVData(String title, SpannableString dess, String media, Float prog) {
        this.title = title;
        this.dess = dess;
        this.media = media;
        this.prog = prog;
    }

}