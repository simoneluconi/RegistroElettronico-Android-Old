package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

public class Firma {

    String prof;
    String ora;
    String materia;
    String attivita;
    String descrizione;

    public Firma() {
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public void setAttivita(String attivita) {
        this.attivita = attivita;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getProf() {
        return this.prof;
    }

    public String getOra() {
        return this.ora;
    }

    public String getMateria() {
        return this.materia;
    }

    public String getAttivita() {
        return this.attivita;
    }

    public String getDescrizione() {
        return this.descrizione;
    }

    @Override
    public String toString() {
        return String.format("%1$s. %2$s - %3$s: %4$s > %5$s ", ora, prof, materia, attivita, descrizione);
    }
}
