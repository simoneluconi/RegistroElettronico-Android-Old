package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Compito {
    private DateTime DataInizio;
    private DateTime DataFine;
    private String autore;
    private String contenuto;
    private DateTime DataInserimento;
    private boolean TuttoIlGiorno = true;
    private final DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter dtfInserimento = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");

    public Compito() {
        this.DataInizio = new DateTime();
        this.DataFine = new DateTime();
        this.DataInserimento = new DateTime();
        this.TuttoIlGiorno = true;
    }

    public String getAutore() {
        return autore;
    }

    public String getContenuto() {
        return contenuto;
    }

    public DateTime getDataFine() {
        return DataFine;
    }

    public String getDataFineString() {
        return dtf.print(DataFine);
    }

    public String getDataInizioString() {
        return dtf.print(DataInizio);
    }

    public String getDataInserimentoString() {
        return dtfInserimento.print(DataInserimento);
    }

    public DateTime getDataInizio() {
        return DataInizio;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

    public void setDataFine(DateTime dataFine) {
        DataFine = dataFine;
    }

    public void setDataInizio(DateTime dataInizio) {
        DataInizio = dataInizio;
    }

    public void setDataInserimento(String dataInserimento) {
        DataInserimento = dtfInserimento.parseDateTime(dataInserimento);
    }

    public void setDataFine(String dataFine) {
        DataFine = dtf.parseDateTime(dataFine);
    }

    public void setDataInizio(String dataInizio) {
        DataInizio = dtf.parseDateTime(dataInizio);
    }

    public void setDataInserimento(DateTime dataInserimento) {
        DataInserimento = dataInserimento;
    }

    public DateTime getDataInserimento() {
        return DataInserimento;
    }


    public boolean isVerifica() {
        return contenuto.toLowerCase().contains("verifica") || contenuto.toLowerCase().contains("compito in classe") || contenuto.toLowerCase().contains("test ") ||
                contenuto.toLowerCase().toLowerCase().endsWith("test");
    }

    public boolean isTuttoIlGiorno() {
        return TuttoIlGiorno;
    }

    public void setTuttoIlGiorno(boolean tuttoIlGiorno) {
        TuttoIlGiorno = tuttoIlGiorno;
    }

    @Override
    public String toString() {
        return String.format("[%1$s] %2$s: %3$s", DataInizio.toString(), autore, contenuto);
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        Compito compito = (Compito) obj;
        return compito.autore.equals(autore) && compito.contenuto.equals(contenuto) && compito.getDataInserimento().compareTo(getDataInserimento()) == 0
                && compito.getDataInizio().compareTo(getDataInizio()) == 0 && compito.getDataFine().compareTo(getDataFine()) == 0;
    }
}