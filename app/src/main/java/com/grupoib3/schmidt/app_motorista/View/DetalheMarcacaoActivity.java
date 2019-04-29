package com.grupoib3.schmidt.app_motorista.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.grupoib3.schmidt.app_motorista.Models.Marcacao;
import com.grupoib3.schmidt.app_motorista.R;

import java.util.Observable;
import java.util.Observer;

public class DetalheMarcacaoActivity extends AppCompatActivity implements Observer {


    AlertDialog alertDialog;

    public void createAlertError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mpark Motorista");
        builder.setMessage("");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detalhe_marcacao);

            Intent intent = getIntent();
            Marcacao marc = (Marcacao) intent.getSerializableExtra("marcacao");

            TextView convenio = (TextView) findViewById(R.id.lblDescConvenio);
            convenio.setText(marc.getlM5_NOME());
            TextView senha = (TextView) findViewById(R.id.lblDescSenha);
            senha.setText(marc.getlM4_SENHA());
            TextView periodo = (TextView) findViewById(R.id.lblDescDesc);
            periodo.setText(marc.getDescricao());
            TextView nota = (TextView) findViewById(R.id.lblDescNota);
            nota.setText(marc.getlM4_NOTA());
            TextView serie = (TextView) findViewById(R.id.lblDescSerie);
            serie.setText(marc.getlM4_SERIE());
            TextView placa = (TextView) findViewById(R.id.lblDescPlaca);
            placa.setText(marc.getlM3_PLACA());
            TextView motorista = (TextView) findViewById(R.id.lblDescMotorista);
            motorista.setText(marc.getdA4_NOME());
            TextView dtmarc = (TextView) findViewById(R.id.lblDHMarc);
            dtmarc.setText(marc.getlM4_DTMAR());
            TextView dtlib = (TextView) findViewById(R.id.lblDHLib);
            dtlib.setText(marc.getlM4_DTLIB());
            TextView dtret = (TextView) findViewById(R.id.lblDHRet);
            dtret.setText(marc.getlM4_DTLIB());
            TextView status = (TextView) findViewById(R.id.lblDescStatus);
            status.setText(marc.getLm4_DSTATUS());

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


        }catch (Exception ex){
            createAlertError();
            alertDialog.setMessage(ex.getMessage());
            alertDialog.show();
            finish();
        }


    }



    @Override
    public void update(Observable o, Object arg) {

        try{
            setContentView(R.layout.activity_detalhe_marcacao);
            Intent intent = getIntent();
            Marcacao marc = (Marcacao) intent.getSerializableExtra("marcacao");

            TextView convenio = (TextView) findViewById(R.id.lblDescConvenio);
            convenio.setText(marc.getlM5_NOME());
            TextView senha = (TextView) findViewById(R.id.lblDescSenha);
            senha.setText(marc.getlM4_SENHA());
            TextView periodo = (TextView) findViewById(R.id.lblDescDesc);
            periodo.setText(marc.getDescricao());
            TextView nota = (TextView) findViewById(R.id.lblDescNota);
            nota.setText(marc.getlM4_NOTA());
            TextView serie = (TextView) findViewById(R.id.lblDescSerie);
            serie.setText(marc.getlM4_SERIE());
            TextView placa = (TextView) findViewById(R.id.lblDescPlaca);
            placa.setText(marc.getlM3_PLACA());
            TextView motorista = (TextView) findViewById(R.id.lblDescMotorista);
            motorista.setText(marc.getdA4_NOME());
            TextView dtmarc = (TextView) findViewById(R.id.lblDHMarc);
            dtmarc.setText(marc.getlM4_DTMAR());
            TextView dtlib = (TextView) findViewById(R.id.lblDHLib);
            dtlib.setText(marc.getlM4_DTLIB());
            TextView dtret = (TextView) findViewById(R.id.lblDHRet);
            dtret.setText(marc.getlM4_DTLIB());
            TextView status = (TextView) findViewById(R.id.lblDescStatus);
            status.setText(marc.getlM4_STATUS());

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }catch (Exception ex){
            createAlertError();
            alertDialog.setMessage(ex.getMessage());
            alertDialog.show();
            finish();
        }
    }

}
