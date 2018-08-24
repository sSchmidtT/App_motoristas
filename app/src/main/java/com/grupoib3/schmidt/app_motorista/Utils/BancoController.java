package com.grupoib3.schmidt.app_motorista.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.common.util.CrashUtils;
import com.grupoib3.schmidt.app_motorista.Models.Usuario;

public class BancoController {

    private SQLiteDatabase db;
    private CriaBanco banco;

    public BancoController(Context context) {
        banco = new CriaBanco(context);
    }

    public  boolean insereUsuario(Usuario usuario){
        long resultado = 0;
        //db.beginTransaction();
        try {
            ContentValues val;

            db = banco.getWritableDatabase();

            val = new ContentValues();
            val.put(CriaBanco.CGC, usuario.getCgc());
            val.put(CriaBanco.DTNASC, usuario.getDtNasc().toString());
            val.put(CriaBanco.AUTH, usuario.getAuthenticated());
            val.put(CriaBanco.CREATED, usuario.getCreated().toString());
            val.put(CriaBanco.EXPIRATION, usuario.getExpiration().toString());
            val.put(CriaBanco.ACCESSTOKEN, usuario.getAccessToken());
            val.put(CriaBanco.MESSAGE, usuario.getMessage());
            val.put(CriaBanco.USUARIO, usuario.getUser());
            val.put(CriaBanco.FCMTOKEN, usuario.getFCMToken());

            if(usuario.getId() != 0){
                String where = CriaBanco.ID + " = " + usuario.getId();
                resultado = db.update(CriaBanco.TABELA[0], val, where, null);
            }else{

                resultado = db.insert(CriaBanco.TABELA[0], null, val);
            }

            db.close();


            //db.setTransactionSuccessful();
        }catch (Exception ex){
            throw ex;
        }finally {
            //db.endTransaction();
            if(resultado <= 0){
                return false;
            }else{
                return true;
            }
        }
    }

    public Cursor carregaUserByAuth(int auth){

        try{
            Cursor cursor;
            String[] campos = {CriaBanco.ID, CriaBanco.CGC, CriaBanco.DTNASC, CriaBanco.AUTH, CriaBanco.CREATED, CriaBanco.EXPIRATION, CriaBanco.ACCESSTOKEN, CriaBanco.MESSAGE, CriaBanco.USUARIO, CriaBanco.FCMTOKEN};
            String where = CriaBanco.AUTH + " = " + auth;

            db = banco.getReadableDatabase();
            cursor = db.query(CriaBanco.TABELA[0], campos, where, null, null, null, null);

            if(cursor != null){
                cursor.moveToFirst();
            }
            db.close();
            return cursor;
        }catch (Exception ex){
            throw ex;
        }
    }

    public Cursor carregaUserByCgc(String Cgc){

        try{
            Cursor cursor;
            String[] campos = {CriaBanco.ID};
            String where = CriaBanco.CGC + " = " + Cgc;

            db = banco.getReadableDatabase();
            cursor = db.query(CriaBanco.TABELA[0], campos, where, null, null, null, null);

            if(cursor != null){
                cursor.moveToFirst();
            }
            db.close();
            return cursor;
        }catch (Exception ex){
            throw ex;
        }
    }

    public Cursor carregaFiliaisByAtivo(){

        try{
            Cursor cursor;
            String[] campos = {CriaBanco.ID_FILIAL, CriaBanco.COD_FILIAL, CriaBanco.NOME_FILIAL, CriaBanco.LOCAL_FILIAL, CriaBanco.URL_FILIAL, CriaBanco.STATUS_FILIAL, CriaBanco.ATIVO_FILIAL};
            String where = CriaBanco.ATIVO_FILIAL + " =  1";

            db = banco.getReadableDatabase();
            cursor = db.query(CriaBanco.TABELA[2], campos, where, null, null, null, null);

            if(cursor != null){
                cursor.moveToFirst();
            }
            db.close();
            return cursor;
        }catch (Exception ex){
            throw ex;
        }
    }

    public Cursor carregaURLFilialByStatus(){

        try{
            Cursor cursor;
            String[] campos = {CriaBanco.ID_FILIAL, CriaBanco.COD_FILIAL, CriaBanco.NOME_FILIAL, CriaBanco.LOCAL_FILIAL, CriaBanco.URL_FILIAL, CriaBanco.STATUS_FILIAL, CriaBanco.ATIVO_FILIAL};
            String where = CriaBanco.STATUS_FILIAL + " =  1";

            db = banco.getReadableDatabase();
            cursor = db.query(CriaBanco.TABELA[2], campos, where, null, null, null, null);

            if(cursor != null){
                cursor.moveToFirst();
            }
            db.close();
            return cursor;
        }catch (Exception ex){
            throw ex;
        }
    }

    public Cursor carregaFilialByCod(String cod_filial){

        try{
            Cursor cursor;
            String[] campos = {CriaBanco.ID_FILIAL, CriaBanco.COD_FILIAL, CriaBanco.NOME_FILIAL, CriaBanco.LOCAL_FILIAL, CriaBanco.URL_FILIAL, CriaBanco.STATUS_FILIAL, CriaBanco.ATIVO_FILIAL};
            String where = CriaBanco.COD_FILIAL + " = " + cod_filial;

            db = banco.getReadableDatabase();
            cursor = db.query(CriaBanco.TABELA[2], campos, where, null, null, null, null);

            if(cursor != null){
                cursor.moveToFirst();
            }
            db.close();
            return cursor;
        }catch (Exception ex){
            throw ex;
        }
    }

    public void MarcaFilialAtiva(Cursor cursor){
        try{

            db = banco.getWritableDatabase();

            ContentValues filiais = new ContentValues();
            filiais.put(CriaBanco.COD_FILIAL, cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COD_FILIAL)));
            filiais.put(CriaBanco.NOME_FILIAL, cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.NOME_FILIAL)));
            filiais.put(CriaBanco.LOCAL_FILIAL, cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.LOCAL_FILIAL)));
            filiais.put(CriaBanco.URL_FILIAL, cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.URL_FILIAL)));
            filiais.put(CriaBanco.STATUS_FILIAL, 1);
            filiais.put(CriaBanco.ATIVO_FILIAL, cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ATIVO_FILIAL)));
            filiais.put(CriaBanco.ID_FILIAL, cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ID_FILIAL)));

            String where = CriaBanco.ID_FILIAL + " = " + cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ID_FILIAL));
            db.update(CriaBanco.TABELA[2],filiais, where, null);

            db.close();
        }catch (Exception ex){
            throw ex;
        }
    }

    public void MarcaFilialInativa(Cursor cursor){
        try{

            db = banco.getWritableDatabase();

            ContentValues filiais = new ContentValues();
            filiais.put(CriaBanco.COD_FILIAL, cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.COD_FILIAL)));
            filiais.put(CriaBanco.NOME_FILIAL, cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.NOME_FILIAL)));
            filiais.put(CriaBanco.LOCAL_FILIAL, cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.LOCAL_FILIAL)));
            filiais.put(CriaBanco.URL_FILIAL, cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.URL_FILIAL)));
            filiais.put(CriaBanco.STATUS_FILIAL, 0);
            filiais.put(CriaBanco.ATIVO_FILIAL, cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ATIVO_FILIAL)));
            filiais.put(CriaBanco.ID_FILIAL, cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ID_FILIAL)));

            String where = CriaBanco.ID_FILIAL + " = " + cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ID_FILIAL));
            db.update(CriaBanco.TABELA[2],filiais, where, null);

            db.close();
        }catch (Exception ex){
            throw ex;
        }
    }

    public boolean InsereFilial(ContentValues filial){
        long ret = 0;
        try{

            Cursor cursor = carregaFilialByCod(filial.getAsString(CriaBanco.COD_FILIAL));
            db = banco.getWritableDatabase();

            if(filial.getAsBoolean(CriaBanco.ATIVO_FILIAL)){
                filial.put(CriaBanco.ATIVO_FILIAL, 1);
            }else{
                filial.put(CriaBanco.ATIVO_FILIAL, 0);
            }
            filial.put(CriaBanco.STATUS_FILIAL, cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.STATUS_FILIAL)));

            if(!cursor.moveToFirst()){

                ret = db.insert(CriaBanco.TABELA[2], null, filial);

            }else{
                int cod = cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ID_FILIAL));
                String where = CriaBanco.ID_FILIAL + " = " + cod;
                filial.put(CriaBanco.ID_FILIAL, cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ID_FILIAL)));
                ret = db.update(CriaBanco.TABELA[2], filial, where, null);
            }
            db.close();

        }catch (Exception e){
            throw e;
        }finally {
            if(ret <= 0){
                return false;
            }else{
                return true;
            }
        }
    }


}
