package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

public class MyAccount {
    String username;
    String name;
    String codicescuola;

    public MyAccount() {

    }

    public String getCodicescuola() {
        return codicescuola;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public void setCodicescuola(String codicescuola) {
        this.codicescuola = codicescuola;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
