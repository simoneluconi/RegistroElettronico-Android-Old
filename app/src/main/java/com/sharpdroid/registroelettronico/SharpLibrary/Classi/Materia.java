package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import java.util.ArrayList;
import java.util.List;

import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.MateriaDecente;

public class Materia {

    List<Voto> voti = new ArrayList<>();
    String materia;

    public Materia(String materia) {
        this.materia = MateriaDecente(materia);
    }

    public String getMateria() {
        return materia;
    }

    public List<Voto> getVoti() {
        return voti;
    }

    public void setVoti(List<Voto> voti) {
        this.voti = voti;
    }

    public void addVoto(Voto voto) {
        voti.add(voto);
    }

    public double getMediaGenerale() {
        double m = 0;
        for (Voto v : voti) {
            if (v.getVotod() != -1)
                m += v.getVotod();
        }
        m = m / (double) voti.size();
        return m;
    }
}



