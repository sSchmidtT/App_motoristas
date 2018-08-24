package com.grupoib3.schmidt.app_motorista.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import com.grupoib3.schmidt.app_motorista.Config.Config;
import com.grupoib3.schmidt.app_motorista.Models.Usuario;
import com.grupoib3.schmidt.app_motorista.View.LoginActivity;
import com.grupoib3.schmidt.app_motorista.View.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UsuarioServices {

    private static Usuario user;
    private static String JSON_STRING;
    private static BancoController dbController;

    public static Usuario LoginMotorista(Context context) throws ParseException {
        try{
            dbController = new BancoController(context);
            TransformaDados tData = new TransformaDados();
            Cursor cursor = dbController.carregaUserByAuth(1);
            user = new Usuario();

            if(cursor.moveToFirst()){

                String pattern = "EEE MMM d HH:mm:ss zzz yyyy";
                Date sDtCreated = new SimpleDateFormat(pattern, Locale.US).parse(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CREATED)));
                Date sDtExpiration = new SimpleDateFormat(pattern, Locale.US).parse(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.EXPIRATION)));

                user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ID)));
                user.setCgc(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CGC)));
                user.setDtNasc(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTNASC)));
                user.setAuthenticated(cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.AUTH)));
                user.setCreated(sDtCreated);
                user.setExpiration(sDtExpiration);
                user.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.MESSAGE)));
                user.setAccessToken(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.ACCESSTOKEN)));
                user.setUser(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.USUARIO)));
                user.setFCMToken(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.FCMTOKEN)));

                cursor.close();
                //dbController.insereUsuario(user);
                Date atualdata = new Date();

                if(atualdata.after(user.getExpiration())){
                    getJson();
                }
                return user;
            }else{

                user.setAuthenticated(0);
                return user;

            }
        }catch (Exception ex){
            throw ex;
        }
    }

    public static boolean DeslogarUser(Context context) throws ParseException{
        try {
            dbController = new BancoController(context);
            Cursor cursor = dbController.carregaUserByAuth(1);
            
            if(cursor != null){
                cursor.moveToFirst();
                boolean ret = false;
                do {
                    Usuario user = new Usuario();

                    //FirebaseInstanceId.getInstance().deleteToken(cursor.getString(cursor.getColumnIndex(CriaBanco.FCMTOKEN)), "1:1053027080642:android:583f5864ab7f0e6a");

                    String pattern = "EEE MMM d HH:mm:ss zzz yyyy";
                    Date sDtCreated = new SimpleDateFormat(pattern, Locale.US).parse(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CREATED)));
                    Date sDtExpiration = new SimpleDateFormat(pattern, Locale.US).parse(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.EXPIRATION)));

                    user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ID)));
                    user.setCgc(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.CGC)));
                    user.setDtNasc(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.DTNASC)));
                    user.setAuthenticated(0);
                    user.setCreated(sDtCreated);
                    user.setExpiration(sDtExpiration);
                    user.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.MESSAGE)));
                    user.setAccessToken("");
                    user.setFCMToken("");
                    ret = dbController.insereUsuario(user);
                }while (cursor.moveToNext());
                cursor.close();

                if(ret){
                    Cursor filial = dbController.carregaURLFilialByStatus();
                    if(filial.moveToFirst()){
                        do {
                            dbController.MarcaFilialInativa(filial);
                        }while (filial.moveToNext());
                        filial.close();
                    }
                }
                return ret;
            }
            return false;
        }catch (Exception ex){
            throw ex;
        }
    }

    public static void getJson(){
        class GetJson extends AsyncTask<Void, Void, String> {


            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(Void... params) {
                HttpServices rh = new HttpServices();
                String ano = user.getDtNasc().toString().substring(4,8);
                String mes = user.getDtNasc().toString().substring(2,4);
                String dia = user.getDtNasc().toString().substring(0,2);
                String converted = user.getCgc().toString().replace(".","").replace("-", "") + ":" + ano + mes + dia;

                String key = Base64.encodeToString(converted.toString().getBytes(), Base64.DEFAULT);
                JSONObject json = new JSONObject();
                try {
                    json.put("key", key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Cursor cursor = dbController.carregaURLFilialByStatus();
                String url = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.URL_FILIAL)) + Config.URL_GetUser;

                String s = rh.getJSONFromAPI(url, json.toString(), "POST", "");
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showUsers();
            }
        }
        GetJson gj = new GetJson();
        gj.execute();
    }

    private static void showUsers(){
        JSONObject jsonObject;
        try{
            jsonObject = new JSONObject(JSON_STRING);
            String erro = "";
            JSONObject autenticado = jsonObject.optJSONObject(Config.TAG_AUTH);
            try{
                erro = autenticado.getString(Config.TAG_ERRO);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            if(erro.equals("")){

                if(autenticado.getBoolean(Config.TAG_USERAUTH)){
                    user.setAccessToken(autenticado.getString(Config.TAG_USERTK));
                    user.setAuthenticated(1);
                    user.setCreated(TransformaDados.ReturnData(autenticado.getString(Config.TAG_USERCRT)));
                    user.setExpiration(TransformaDados.ReturnData(autenticado.getString(Config.TAG_USEREXP)));
                    user.setUser(autenticado.getString(Config.TAG_USER));
                    user.setMessage(autenticado.getString(Config.TAG_USERMSG));

                    dbController.insereUsuario(user);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}