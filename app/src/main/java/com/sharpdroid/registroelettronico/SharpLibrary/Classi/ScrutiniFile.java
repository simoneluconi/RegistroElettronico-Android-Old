package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

public class ScrutiniFile {
    private String nome;
    private String link;
    private String azione;
    private String ImgLink;

    public ScrutiniFile() {
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setAzione(String azione) {
        this.azione = azione;
    }

    public void setImgLink(String ImgLink) {
        this.ImgLink = ImgLink;
    }

    public String getNome() {
        return this.nome;
    }

    public String getLink() {
        return this.link;
    }

    public String getAzione() {
        return this.azione;
    }

    public String getImgLink() {
        return this.ImgLink;
    }

}
