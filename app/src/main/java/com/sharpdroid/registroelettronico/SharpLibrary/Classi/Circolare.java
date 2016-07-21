package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

public class Circolare {
    String NCircolare;
    String Titolo;
    String Data;
    String Tipo;
    String id;

    public Circolare() {
    }

    public String getData() {
        return Data;
    }

    public String getId() {
        return id;
    }

    public String getNCircolare() {
        return NCircolare;
    }

    public String getTipo() {
        return Tipo;
    }

    public String getTitolo() {
        return Titolo;
    }

    public void setData(String data) {
        Data = data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNCircolare(String NCircolare) {
        this.NCircolare = NCircolare;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public void setTitolo(String titolo) {
        Titolo = titolo;
    }

    @Override
    public String toString() {
        return String.format("Titolo: %1$s | Tipo: %2$s | Data %3$s | N. %4$s", Titolo, Tipo, Data, NCircolare);
    }
}
