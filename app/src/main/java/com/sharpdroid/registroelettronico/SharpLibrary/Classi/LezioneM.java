package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import java.util.ArrayList;
import java.util.List;

public class LezioneM {

    private String id;
    private String materia;
    private final List<Lezione> lezioni = new ArrayList<>();

    public LezioneM()
    {

    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMateria() {
        return materia;
    }

    public List<Lezione> getLezioni() {
        return lezioni;
    }

    public void addLezione(Lezione lezione)
    {
        this.lezioni.add(lezione);
    }

    public String getId() {
        return id;
    }
}
