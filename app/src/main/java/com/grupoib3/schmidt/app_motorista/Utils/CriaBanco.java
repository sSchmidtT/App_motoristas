package com.grupoib3.schmidt.app_motorista.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Date;

public class CriaBanco extends SQLiteOpenHelper{

    public static final String NOME_BANCO = "App_Mpark-Motorista.db";
    public static final String[] TABELA;

    static {
        TABELA = new String[]{"USUARIO", "NOTIFICATION", "FILIAL"};
    }

    //Campos da tabela de usuario
    public static final String ID = "ID";
    public static final String CGC = "CGC";
    public static final String DTNASC = "DTNASC";
    public static final String AUTH = "AUTHENTICATED";
    public static final String CREATED = "CREATED";
    public static final String EXPIRATION = "EXPIRATION";
    public static final String ACCESSTOKEN = "ACCESSTOKEN";
    public static final String MESSAGE = "MESSAGE";
    public static final String USUARIO = "USER";
    public static final String FCMTOKEN = "FCMTOKEN";
    public static final String ID_FILIAL_FK = "ID_FILIAL";


    //Campos da tabela Filiais
    public static final String COD_FILIAL = "COD_FILIAL";
    public static final String NOME_FILIAL = "NOME_FILIAL";
    public static final String LOCAL_FILIAL = "LOCAL_FILIAL";
    public static final String URL_FILIAL = "URL_FILIAL";
    public static final String STATUS_FILIAL = "STATUS_FILIAL";
    public static final String ATIVO_FILIAL = "ATIVO_FILIAL";
    public static final String ID_FILIAL = "_id";
    public static final String UTC_FILIAL = "UTC_FILIAL";

    //Campos da tabela Notification
    public static final String ID_NOTIFI = "ID";
    public static final String ID_USER_FK = "ID_USUARIO";
    public static final String DATE_NOTIFI = "DATA_NOTIFICACAO";
    public static final String TITLE_NOTIFI = "TITULO_NOTIFICACAO";
    public static final String MSG_NOTIFI = "MSG_NOTIFICACAO";
    public static final String STATUS_NOTIFI = "STATUS_NOTIFICACAO";
    public static final String URL_NOTIFI = "URL_NOTIFICACAO";

    //Versao do banco
    public static final int VERSAO = 7;

    public CriaBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String user = "CREATE TABLE " + TABELA[0] + " ( " +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ID_FILIAL_FK + " INTEGER, " +
                CGC + " TEXT, " +
                DTNASC + " TEXT, " +
                AUTH + " INT, " +
                CREATED + " TEXT, " +
                EXPIRATION + " TEXT, " +
                ACCESSTOKEN + " TEXT, " +
                MESSAGE + " TEXT, " +
                USUARIO + " TEXT, " +
                FCMTOKEN + " TEXT, " +
                "FOREIGN KEY(" + ID_FILIAL_FK + ") REFERENCES " + TABELA[2] + "( " + ID_FILIAL +" )" +
                ")";

        String filial = "CREATE TABLE " + TABELA[2] + " ( " +
                ID_FILIAL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COD_FILIAL + " TEXT NOT NULL, " +
                NOME_FILIAL + " TEXT NOT NULL, " +
                LOCAL_FILIAL + " TEXT NOT NULL, " +
                URL_FILIAL + " TEXT NOT NULL, " +
                UTC_FILIAL + " TEXT NOT NULL, " +
                STATUS_FILIAL + " INT, " +
                ATIVO_FILIAL + " INT" +
                ")";

        String notifi = "CREATE TABLE " + TABELA[1] + " ( " +
                ID_NOTIFI + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ID_FILIAL_FK + " INTEGER NOT NULL, " +
                ID_USER_FK + " INTEGER NOT NULL, " +
                DATE_NOTIFI + " TEXT NOT NULL, " +
                TITLE_NOTIFI + " TEXT NOT NULL, " +
                MSG_NOTIFI + " TEXT NOT NULL, " +
                STATUS_NOTIFI + " INT, " +
                URL_NOTIFI + " TEXT, " +
                "FOREIGN KEY(" + ID_FILIAL_FK + ") REFERENCES " + TABELA[2] + "( " + ID_FILIAL +" ), " +
                "FOREIGN KEY(" + ID_USER_FK + ") REFERENCES " + TABELA[0] + "( " + ID_USER_FK +" )" +
                ")";

        db.execSQL(filial);

        db.execSQL(user);

        db.execSQL(notifi);

        insertFiliais(db);
    }

    public void insertFiliais(SQLiteDatabase db){
        try{
            ContentValues filiais = new ContentValues();
            filiais.put(CriaBanco.COD_FILIAL, "500501");
            filiais.put(CriaBanco.NOME_FILIAL, "Mpark Estacionamento");
            filiais.put(CriaBanco.LOCAL_FILIAL, "Candeias do Jamari/RO");
            filiais.put(CriaBanco.URL_FILIAL, "https://mparkm4.postomirian.com.br/AppMotoristas/Api/");
            filiais.put(CriaBanco.UTC_FILIAL, "America/Porto_Velho");
            filiais.put(CriaBanco.STATUS_FILIAL, 0);
            filiais.put(CriaBanco.ATIVO_FILIAL, 1);
            db.insert(CriaBanco.TABELA[2], null, filiais);

            filiais = new ContentValues();
            filiais.put(CriaBanco.COD_FILIAL, "500503");
            filiais.put(CriaBanco.NOME_FILIAL, "Mpark Estacionamento");
            filiais.put(CriaBanco.LOCAL_FILIAL, "Itaituba/PA");
            filiais.put(CriaBanco.URL_FILIAL, "https://mparkm5.postomirian.com.br/AppMotoristas/Api/");
            filiais.put(CriaBanco.UTC_FILIAL, "America/Belem");
            filiais.put(CriaBanco.STATUS_FILIAL, 0);
            filiais.put(CriaBanco.ATIVO_FILIAL, 1);
            db.insert(CriaBanco.TABELA[2], null, filiais);

            filiais = new ContentValues();
            filiais.put(CriaBanco.COD_FILIAL, "500502");
            filiais.put(CriaBanco.NOME_FILIAL, "Mpark Estacionamento");
            filiais.put(CriaBanco.LOCAL_FILIAL, "Candeias do Jamari TST/RO");
            filiais.put(CriaBanco.URL_FILIAL, "https://mparkm4.postomirian.com.br/Teste-AppMotoristas/Api/");
            filiais.put(CriaBanco.UTC_FILIAL, "America/Porto_Velho");
            filiais.put(CriaBanco.STATUS_FILIAL, 0);
            filiais.put(CriaBanco.ATIVO_FILIAL, 1);
            db.insert(CriaBanco.TABELA[2], null, filiais);

        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String tabela: TABELA) {
            db.execSQL("DROP TABLE IF EXISTS " + tabela);
        }
        onCreate(db);
    }
}
