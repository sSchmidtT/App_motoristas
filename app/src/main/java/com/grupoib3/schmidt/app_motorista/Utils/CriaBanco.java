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
        TABELA = new String[]{"USUARIO", "CONFIGURE", "FILIAL"};
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

    //Campos da tabela Filiais
    public static final String COD_FILIAL = "COD_FILIAL";
    public static final String NOME_FILIAL = "NOME_FILIAL";
    public static final String LOCAL_FILIAL = "LOCAL_FILIAL";
    public static final String URL_FILIAL = "URL_FILIAL";
    public static final String STATUS_FILIAL = "STATUS_FILIAL";
    public static final String ATIVO_FILIAL = "ATIVO_FILIAL";
    public static final String ID_FILIAL = "_id";
    //Versao do banco
    public static final int VERSAO = 1;

    public CriaBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String Sql = "CREATE TABLE " + TABELA[0] + " ( " +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CGC + " TEXT, " +
                DTNASC + " TEXT, " +
                AUTH + " INT, " +
                CREATED + " TEXT, " +
                EXPIRATION + " TEXT, " +
                ACCESSTOKEN + " TEXT, " +
                MESSAGE + " TEXT, " +
                USUARIO + " TEXT, " +
                FCMTOKEN + " TEXT" +
                ")";

        String filial = "CREATE TABLE " + TABELA[2] + " ( " +
                ID_FILIAL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COD_FILIAL + " TEXT NOT NULL, " +
                NOME_FILIAL + " TEXT NOT NULL," +
                LOCAL_FILIAL + " TEXT NOT NULL," +
                URL_FILIAL + " TEXT NOT NULL, " +
                STATUS_FILIAL + " INT, " +
                ATIVO_FILIAL + " INT" +
                ")";

        db.execSQL(Sql);
        db.execSQL(filial);

        insertFiliais(db);
    }

    public void insertFiliais(SQLiteDatabase db){
        try{
            ContentValues filiais = new ContentValues();
            filiais.put(CriaBanco.COD_FILIAL, "500501");
            filiais.put(CriaBanco.NOME_FILIAL, "Mpark Estacionamento");
            filiais.put(CriaBanco.LOCAL_FILIAL, "Candeias do Jamari/RO");
            filiais.put(CriaBanco.URL_FILIAL, "https://mpark-m4-ws.postomirian.com.br/AppMotoristas/Api/");
            filiais.put(CriaBanco.STATUS_FILIAL, 1);
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