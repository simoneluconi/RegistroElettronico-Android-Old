package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import java.util.Calendar;

import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.ConvertiInVoto;

public class Voto {
    String tipo;
    String data;
    String periodo;
    String voto;
    Double votod;
    String commento;
    public static final String P1 = "q1";
    public static final String P2 = "q3";
    boolean votoblu = false;

    public Voto() {
    }

    public void setVoto(String voto) {

        this.voto = voto;
        votod = ConvertiInVoto(voto);
    }

    public void setVoto(double voto) {
        this.votod = voto;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setData(String data) {

        int mm = Integer.parseInt(data.split("/")[1]);
        if (mm >= 1 && mm <= 8) {
            data = data + "/" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        } else {
            data = data + "/" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1);
        }
        this.data = data;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public boolean isVotoblu() {
        return votoblu;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }

    public void setVotoblu(boolean votoblu) {
        this.votoblu = votoblu;
    }

    public String getVoto() {
        return this.voto;
    }

    public double getVotod() {
        return this.votod;
    }

    public String getTipo() {
        return this.tipo;
    }

    public String getData() {
        return this.data;
    }

    public String getCommento() {
        return commento;
    }

    @Override
    public String toString() {
        return String.format("Voto: %1$s | Tipo: %2$s | Data: %3$s | Periodo: %4$s |Blu?: %5$s", voto, tipo, data, periodo, votoblu);
    }

}
