package com.sharpdroid.registroelettronico.SharpLibrary.Classi;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class FileDid {

    private int pos;
    private DateTime DataInserimento;
    private String name;
    private String id;
    private String cksum;
    private String type;
    private final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");

    public FileDid() {

    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    public void setCksum(String cksum) {
        this.cksum = cksum;
    }

    public String getCksum() {
        return cksum;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public boolean isFile() {
        return type.equals("file");
    }

    public boolean isLink() {
        return type.equals("link");
    }

    public String getName() {
        if (!name.equals(""))
            return name;
        else return "Senza nome";
    }

    public void setDataInserimento(DateTime dataInserimento) {
        DataInserimento = dataInserimento;
    }

    public void setDataInserimento(String dataInserimento) {
        DataInserimento = dtf.parseDateTime(dataInserimento);
    }

    public DateTime getDataInserimento() {
        return DataInserimento;
    }

    public String getDataInserimentoString() {
        return dtf.print(DataInserimento);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("id: %1$s | cksum: %2$s | type: %3$s", id, cksum, type);
    }
}