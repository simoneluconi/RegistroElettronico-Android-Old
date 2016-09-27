package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import static com.sharpdroid.registroelettronico.SharpLibrary.Metodi.MateriaDecente;

public class Medie {
    private int nVotiGenerale = 0;
    private int nVotiScritto = 0;
    private int nVotiOrale = 0;
    private int nVotiPratico = 0;
    private String Materia;
    private double SommaOrale = 0;
    private double SommaScritto = 0;
    private double SommaPratico = 0;
    private double SommaGenerale;

    public Medie() {

    }

    public void setMateria(String Materia) {
        this.Materia = MateriaDecente(Materia);
    }

    public void addVoto(Voto v) {
        if (v.getVotod() != -1) {
            switch (v.getTipo()) {
                case "Pratico": {
                    SommaPratico += v.getVotod();
                    nVotiPratico++;
                }
                break;

                case "Orale": {
                    SommaOrale += v.getVotod();
                    nVotiOrale++;
                }
                break;
                case "Scritto/Grafico": {
                    SommaScritto += v.getVotod();
                    nVotiScritto++;
                }
                break;
            }

            SommaGenerale += v.getVotod();
            nVotiGenerale++;
        }
    }

    public double getMediaOrale() {
        return SommaOrale / (double) nVotiOrale;
    }

    public double getMediaScritto() {
        return SommaScritto / (double) nVotiScritto;
    }

    public double getMediaPratico() {
        return SommaPratico / (double) nVotiPratico;
    }

    public double getMediaGenerale() {
        return SommaGenerale / (double) nVotiGenerale;
    }

    public String getMateria() {
        return Materia;
    }

    public int getnVotiGenerale() {
        return nVotiGenerale;
    }

    public double getSommaGenerale() {
        return SommaGenerale;
    }
}
