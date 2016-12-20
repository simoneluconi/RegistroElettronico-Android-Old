package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import java.util.Date;

public class Utente {
    int id;
    String target;
    String sede_codice;
    String account_string;
    String account_desc;
    String nome;
    String wsc_cat;
    String dinsert;
    String account_type;
    String scuola_descrizione;
    String scuola_intitolazione;
    String scuola_luogo;


    public Utente() {
    }


    public String getNome()
    {
        return  nome;
    }

    public String getAccount_type()
    {
        return  account_type;
    }

    public String getAccount_string()
    {
        return account_string;
    }
}
