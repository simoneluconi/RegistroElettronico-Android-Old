package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

public class Nota {
    String prof;
    String tipo;
    String data;
    String contenuto;

    public Nota() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getContenuto() {
        return contenuto;
    }

    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

    public String getData() {
        return data;
    }

    public String getProf() {
        return prof;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    @Override
    public String toString() {
        return String.format("Nota = { %1$s: (%2$s) - %3$s > %4$s }", prof, data, tipo, contenuto);
    }
}
