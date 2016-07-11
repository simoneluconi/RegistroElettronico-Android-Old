package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import android.text.SpannableString;

import java.util.List;

public class CVData {
    public String title;
    public String des = "";
    public SpannableString dess;
    public String media;
    public Float prog;


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