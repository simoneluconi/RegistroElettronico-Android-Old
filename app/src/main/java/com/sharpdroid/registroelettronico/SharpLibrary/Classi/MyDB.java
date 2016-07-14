package com.sharpdroid.registroelettronico.SharpLibrary.Classi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class MyDB extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_COMPITI =
            "CREATE TABLE " + CompitoEntry.TABLE_NAME + " (" +
                    CompitoEntry._ID + " INTEGER PRIMARY KEY," +
                    CompitoEntry.COLUMN_NAME_AUTORE + TEXT_TYPE + COMMA_SEP +
                    CompitoEntry.COLUMN_NAME_DATAINIZIO + TEXT_TYPE + COMMA_SEP +
                    CompitoEntry.COLUMN_NAME_DATAFINE + TEXT_TYPE + COMMA_SEP +
                    CompitoEntry.COLUMN_NAME_TUTTOILGIORNO + " INTEGER" + COMMA_SEP +
                    CompitoEntry.COLUMN_NAME_DATAINSERIMENTO + TEXT_TYPE + COMMA_SEP +
                    CompitoEntry.COLUMN_NAME_CONTENUTO + TEXT_TYPE + " )";

    private static final String SQL_CREATE_MYCOMPITI =
            "CREATE TABLE " + MyCompitoEntry.TABLE_NAME + " (" +
                    MyCompitoEntry._ID + " INTEGER PRIMARY KEY," +
                    MyCompitoEntry.COLUMN_NAME_AUTORE + TEXT_TYPE + COMMA_SEP +
                    MyCompitoEntry.COLUMN_NAME_DATA + TEXT_TYPE + COMMA_SEP +
                    MyCompitoEntry.COLUMN_NAME_DATAINSERIMENTO + TEXT_TYPE + COMMA_SEP +
                    MyCompitoEntry.COLUMN_NAME_CONTENUTO + TEXT_TYPE + " )";

    private static final String SQL_CREATE_VOTI =
            "CREATE TABLE " + VotoEntry.TABLE_NAME + " (" +
                    VotoEntry._ID + " INTEGER PRIMARY KEY," +
                    VotoEntry.COLUMN_NAME_MATERIA + TEXT_TYPE + COMMA_SEP +
                    VotoEntry.COLUMN_NAME_VOTO + TEXT_TYPE + COMMA_SEP +
                    VotoEntry.COLUMN_NAME_VOTOBLU + " INTEGER" + COMMA_SEP +
                    VotoEntry.COLUMN_NAME_DATA + TEXT_TYPE + COMMA_SEP +
                    VotoEntry.COLUMN_NAME_PERIODO + TEXT_TYPE + COMMA_SEP +
                    VotoEntry.COLUMN_NAME_TIPO + TEXT_TYPE + COMMA_SEP +
                    VotoEntry.COLUMN_NAME_COMMENTO + TEXT_TYPE + " )";

    private static final String SQL_CREATE_NOTE =
            "CREATE TABLE " + NotaEntry.TABLE_NAME + " (" +
                    NotaEntry._ID + " INTEGER PRIMARY KEY," +
                    NotaEntry.COLUMN_NAME_AUTORE + TEXT_TYPE + COMMA_SEP +
                    NotaEntry.COLUMN_NAME_DATA + TEXT_TYPE + COMMA_SEP +
                    NotaEntry.COLUMN_NAME_CONTENUTO + " INTEGER" + COMMA_SEP +
                    NotaEntry.COLUMN_NAME_TIPO + TEXT_TYPE + " )";


    private static final String SQL_DELETE_COMPITI =
            "DROP TABLE IF EXISTS " + CompitoEntry.TABLE_NAME;

    private static final String SQL_DELETE_MYCOMPITI =
            "DROP TABLE IF EXISTS " + MyCompitoEntry.TABLE_NAME;

    private static final String SQL_DELETE_VOTI =
            "DROP TABLE IF EXISTS " + VotoEntry.TABLE_NAME;

    private static final String SQL_DELETE_NOTE =
            "DROP TABLE IF EXISTS " + NotaEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "MyData.db";

    public MyDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_COMPITI);
        db.execSQL(SQL_CREATE_MYCOMPITI);
        db.execSQL(SQL_CREATE_VOTI);
        db.execSQL(SQL_CREATE_NOTE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_COMPITI);
        db.execSQL(SQL_DELETE_MYCOMPITI);
        db.execSQL(SQL_DELETE_VOTI);
        db.execSQL(SQL_DELETE_NOTE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public class CompitoEntry implements BaseColumns {
        public CompitoEntry() {
        }

        public static final String TABLE_NAME = "Compiti";
        public static final String COLUMN_NAME_AUTORE = "AUTORE";
        public static final String COLUMN_NAME_DATAINIZIO = "DATAINIZIO";
        public static final String COLUMN_NAME_DATAFINE = "DATAFINE";
        public static final String COLUMN_NAME_TUTTOILGIORNO = "TUTTOILGIORNO";
        public static final String COLUMN_NAME_DATAINSERIMENTO = "DATAINSERIMENTO";
        public static final String COLUMN_NAME_CONTENUTO = "CONTENUTO";
        public static final String COLUMN_NAME_NULLABLE = "Empty";
    }

    public final class MyCompitoEntry implements BaseColumns {
        public MyCompitoEntry() {
        }

        public static final String TABLE_NAME = "MyCompiti";
        public static final String COLUMN_NAME_AUTORE = "AUTORE";
        public static final String COLUMN_NAME_DATA = "DATA";
        public static final String COLUMN_NAME_DATAINSERIMENTO = "DATAINSERIMENTO";
        public static final String COLUMN_NAME_CONTENUTO = "CONTENUTO";
        public static final String COLUMN_NAME_NULLABLE = "Empty";
    }

    public final class VotoEntry implements BaseColumns {
        public VotoEntry() {
        }

        public static final String TABLE_NAME = "Voti";
        public static final String COLUMN_NAME_MATERIA = "MATERIA";
        public static final String COLUMN_NAME_VOTO = "VOTO";
        public static final String COLUMN_NAME_VOTOBLU = "VOTOBLU";
        public static final String COLUMN_NAME_DATA = "DATA";
        public static final String COLUMN_NAME_PERIODO = "PERIODO";
        public static final String COLUMN_NAME_TIPO = "TIPO";
        public static final String COLUMN_NAME_COMMENTO = "COMMENTO";
        public static final String COLUMN_NAME_NULLABLE = "Empty";
    }

    public final class NotaEntry implements BaseColumns {
        public NotaEntry() {
        }

        public static final String TABLE_NAME = "Note";
        public static final String COLUMN_NAME_AUTORE = "AUTORE";
        public static final String COLUMN_NAME_DATA = "DATA";
        public static final String COLUMN_NAME_TIPO = "TIPO";
        public static final String COLUMN_NAME_CONTENUTO = "CONTENUTO";
        public static final String COLUMN_NAME_NULLABLE = "Empty";
    }
}
