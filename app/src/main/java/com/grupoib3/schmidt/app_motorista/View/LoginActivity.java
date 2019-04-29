package com.grupoib3.schmidt.app_motorista.View;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.grupoib3.schmidt.app_motorista.Config.Config;
import com.grupoib3.schmidt.app_motorista.Models.Usuario;
import com.grupoib3.schmidt.app_motorista.R;
import com.grupoib3.schmidt.app_motorista.Utils.BancoController;
import com.grupoib3.schmidt.app_motorista.Utils.CriaBanco;
import com.grupoib3.schmidt.app_motorista.Utils.HttpServices;
import com.grupoib3.schmidt.app_motorista.Utils.TransformaDados;
import com.grupoib3.schmidt.app_motorista.Utils.UsuarioServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Random;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txtCpf;
    TextView txtDtNasc;
    Button btnLogin;
    Spinner spinnerFiliais;
    AlertDialog alertDialog;
    ProgressDialog progressDialog;
    Usuario user;
    String JSON_STRING;
    BancoController bc;
    Cursor filial;
    String utc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            //FirebaseApp.initializeApp(this);
            if(verificaConexao()){
                try {
                    user = UsuarioServices.LoginMotorista(getBaseContext());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(user.getAuthenticated() == 1){ //se autenticado, vai para pagina inicial
                    startActivity(
                            new Intent(
                                    LoginActivity.this, MainActivity.class));
                    finish();
                }else{ //se nao autenticado, entra na tela de login

                    bc = new BancoController(getBaseContext());
                    txtCpf = this.findViewById(R.id.txtCpf);
                    txtDtNasc = this.findViewById(R.id.txtDtNasc);
                    spinnerFiliais = this.findViewById(R.id.spinnerFiliais);
                    btnLogin = this.findViewById(R.id.btnLogin);
                    btnLogin.setOnClickListener(this);
                    Cursor cursor = bc.carregaFiliaisByAtivo();
                    cursor.moveToFirst();

                    SimpleCursorAdapter _adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item, cursor, new String[]{CriaBanco.LOCAL_FILIAL, CriaBanco.COD_FILIAL}, new int[]{android.R.id.text1, android.R.id.textAssist}, 0);
                    _adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerFiliais.setAdapter(_adapter);
                }
            }
        }catch (Exception ex){
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                if(!TransformaDados.isValidCPF(txtCpf.getText().toString()) ){
                    Toast toast = Toast.makeText(getBaseContext(), "CPF inválido!", Toast.LENGTH_LONG);
                    toast.show();
                }else if(txtDtNasc.getText().length() != 8){
                    Toast toast = Toast.makeText(getBaseContext(), "Data de nascimento incorreta!", Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    filial = (Cursor) spinnerFiliais.getSelectedItem();
                    if(filial.getInt(filial.getColumnIndexOrThrow(CriaBanco.STATUS_FILIAL)) == 0 )
                         bc.MarcaFilialAtiva(filial);
                    getJson();
                }

                break;
        }
    }

    @Override
    public void onBackPressed(){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Mpark Motorista");
            builder.setMessage("Deseja fechar o programa?");

            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });
            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    alertDialog.dismiss();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();
        }catch (Exception ex){
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean verificaConexao(){
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
            if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isAvailable() && connectivityManager.getActiveNetworkInfo().isConnected()){
                return true;
            }else{
                return false;
            }
        }catch (Exception ex){
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void getJson(){
        class GetJson extends AsyncTask<Void, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog =
                        ProgressDialog.show(LoginActivity.this,"Carregando",
                                "Aguarde...",true, false);
            }

            @Override
            protected String doInBackground(Void... params) {
                try{
                    HttpServices rh = new HttpServices();
                    String ano = txtDtNasc.getText().toString().substring(4,8);
                    String mes = txtDtNasc.getText().toString().substring(2,4);
                    String dia = txtDtNasc.getText().toString().substring(0,2);
                    String converted = txtCpf.getText().toString().replace(".","").replace("-", "") + ":" + ano + mes + dia;

                    user = new Usuario();
                    user.setCgc(txtCpf.getText().toString());
                    user.setDtNasc(txtDtNasc.getText().toString());
                    user.setFCMToken(FirebaseInstanceId.getInstance().getToken());
                    //String key = Base64.encodeToString(converted.toString().getBytes(), Base64.DEFAULT);
                    int[] iKey = new int[converted.length()];
                    Random rand = new Random();
                    for(int i = 0; i < converted.length(); i++){
                        iKey[i] = rand.nextInt((5 - 1) + 1) + 1;
                    }
                    String token = converted.length() + "-";
                    for(int i = 0; i < iKey.length; i++){
                        token += iKey[i];
                    }

                    for(int i = 0; i < converted.length(); i++){
                        String p = "";
                        for(int j = 0; j < iKey[i]; j++){
                            p += rand.nextInt((9 - 1) + 1) + 1;
                        }
                        token += p + converted.charAt(i);
                    }

                    String key = Base64.encodeToString(token.getBytes(), Base64.DEFAULT);
                    JSONObject json = new JSONObject();
                    try {
                        json.put("key", key);
                        json.put("FCMToken", user.getFCMToken());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String url = filial.getString(filial.getColumnIndexOrThrow(CriaBanco.URL_FILIAL)) + Config.URL_GetUser;
                    utc = filial.getString(filial.getColumnIndexOrThrow(CriaBanco.UTC_FILIAL));

                    String s = rh.getJSONFromAPI(url, json.toString(), "POST", "");
                    return s;
                }catch (Exception ex){
                    throw ex;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showUser();
            }
        }
        GetJson gj = new GetJson();
        gj.execute();
    }

    private void showUser(){
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
                    user.setCreated(TransformaDados.ReturnData(autenticado.getString(Config.TAG_USERCRT), utc));
                    user.setExpiration(TransformaDados.ReturnData(autenticado.getString(Config.TAG_USEREXP), utc));
                    user.setUser(autenticado.getString(Config.TAG_USER));
                    user.setMessage(autenticado.getString(Config.TAG_USERMSG));

                    bc.insereUsuario(user);

                    startActivity(
                            new Intent(
                                    LoginActivity.this, MainActivity.class));
                    finish();

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), autenticado.getString(Config.TAG_USERMSG), Toast.LENGTH_LONG).show();
                }
            }
            else{
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), autenticado.getString("mensagem"), Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
        }catch (Exception e){
            progressDialog.dismiss();
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
}
