package com.grupoib3.schmidt.app_motorista.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grupoib3.schmidt.app_motorista.Config.Config;
import com.grupoib3.schmidt.app_motorista.Models.Usuario;
import com.grupoib3.schmidt.app_motorista.R;
import com.grupoib3.schmidt.app_motorista.Utils.BancoController;
import com.grupoib3.schmidt.app_motorista.Utils.CriaBanco;
import com.grupoib3.schmidt.app_motorista.Utils.HttpServices;
import com.grupoib3.schmidt.app_motorista.Utils.TransformaDados;
import com.grupoib3.schmidt.app_motorista.Utils.UsuarioServices;

import org.json.JSONObject;

import java.text.ParseException;

public class MarcacaoFragment extends Fragment {

    String JSON_STRING;
    TextView lblsMarc;
    TextView lblConvenio;
    TextView lblSenha;
    TextView lblPeriodo;
    TextView lbldNota;
    TextView lblNota;
    TextView lbldSerie;
    TextView lblSerie;
    TextView lbldPlaca;
    TextView lblPlaca;
    TextView lbldMotorista;
    TextView lblMotorista;
    TextView lbldDtMarc;
    TextView lblDtMarc;
    TextView ultimaSenha;
    TextView lblUltimaLib;
    TextView lblPeriodoLib;
    TextView lbldDtLib;
    TextView lblDtLib;
    TextView lbldQntFalt;
    TextView lblQntFalta;
    TextView lbldStatus;
    TextView lblStatus;
    View divider2;
    SwipeRefreshLayout swipeMarcacao;
    BancoController bd;
    Usuario user;
    ProgressDialog progressDialog;
    boolean swiping = false;

    public MarcacaoFragment() {
        // Required empty public constructor
    }

    public static MarcacaoFragment newInstance( ) {
        MarcacaoFragment fragment = new MarcacaoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marcacao, container, false);
        bd = new BancoController(getContext());
        try {
            user = UsuarioServices.LoginMotorista(getContext());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        lblsMarc = (TextView) view.findViewById(R.id.lblsMarc);
        lblConvenio = (TextView) view.findViewById(R.id.lblConvenio);
        lblSenha = (TextView) view.findViewById(R.id.lblSenha);
        lblPeriodo = (TextView) view.findViewById(R.id.lblPeriodo);
        lbldNota = (TextView) view.findViewById(R.id.lbldNota);
        lblNota = (TextView) view.findViewById(R.id.lblNota);
        lbldSerie = (TextView) view.findViewById(R.id.lbldSerie);
        lblSerie = (TextView) view.findViewById(R.id.lblSerie);
        lbldPlaca = (TextView) view.findViewById(R.id.lbldPlaca);
        lblPlaca = (TextView) view.findViewById(R.id.lblPlaca);
        lbldMotorista = (TextView) view.findViewById(R.id.lbldMotorista);
        lblMotorista = (TextView) view.findViewById(R.id.lblMotorista);
        lbldDtMarc = (TextView) view.findViewById(R.id.lbldDtMarc);
        lblDtMarc = (TextView) view.findViewById(R.id.lblDtMarc);
        ultimaSenha = (TextView) view.findViewById(R.id.ultimaSenha);
        lblUltimaLib = (TextView) view.findViewById(R.id.lblUltimaLib);
        lblPeriodoLib = (TextView) view.findViewById(R.id.lblPeriodoLib);
        lbldDtLib = (TextView) view.findViewById(R.id.lbldDtLib);
        lblDtLib = (TextView) view.findViewById(R.id.lblDtLib);
        lbldQntFalt = (TextView) view.findViewById(R.id.lbldQntFalt);
        lblQntFalta = (TextView) view.findViewById(R.id.lblQntFalta);
        lbldStatus = (TextView) view.findViewById(R.id.lbldStatus);
        lblStatus = (TextView) view.findViewById(R.id.lblStatus);
        divider2 = (View) view.findViewById(R.id.divider2);

        divider2.setVisibility(View.INVISIBLE);

        swipeMarcacao = (SwipeRefreshLayout) view.findViewById(R.id.swipeMarc);

        swipeMarcacao.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swiping = true;
                getJson();
            }
        });
        swipeMarcacao.setColorSchemeColors(R.color.colorAccent);
        getJson();
        return view;
    }

    public void getJson(){
        class GetJson extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(!swiping)
                    progressDialog = ProgressDialog.show(getContext(), "Carregando Marcação", "Aguarde...", false, false);
            }

            @Override
            protected String doInBackground(Void... params) {
                HttpServices rh = new HttpServices();
                Cursor cursor = bd.carregaURLFilialByStatus();
                String url = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.URL_FILIAL)) + Config.URL_Marcacao;
                String s = rh.getJSONFromAPI(url, "", "GET", user.getAccessToken());
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showMarcacoes();
                swipeMarcacao.setRefreshing(false);
            }
        }
        GetJson gj = new GetJson();
        gj.execute();
    }

    private void showMarcacoes(){
        JSONObject jsonObject;
        try{
            jsonObject = new JSONObject(JSON_STRING);
            String erro = "";
            try{
                erro = jsonObject.getString(Config.TAG_ERRO);
            }catch (Exception ex){
                ex.printStackTrace();
            }

            if(erro.equals("")){
                int qntmarc = jsonObject.optInt(Config.TAG_QNTFRENTE);
                JSONObject result = jsonObject.optJSONObject(Config.TAG_MARCACAO);
                JSONObject senhalib = jsonObject.optJSONObject(Config.TAG_SENHALIB);

                if(result != null){
                    lblsMarc.setText("");
                    lblConvenio.setText(result.optString(Config.TAG_NOMECONV));
                    if(result.optInt(Config.TAG_REGRA) != 1){
                        lblSenha.setText(!result.optString(Config.TAG_SENPER).equals("")? "[ " + result.optString(Config.TAG_SENPER) + " ]" : "[ " +result.optString(Config.TAG_SENHA) + " ]");
                        lblUltimaLib.setText(!senhalib.optString(Config.TAG_SENPER).equals("")? "[ " + senhalib.optString(Config.TAG_SENPER) + " ]" : "[ " + senhalib.optString(Config.TAG_SENLIB) + " ]");
                        lblPeriodo.setText(result.getString(Config.TAG_DESC) + " | " + TransformaDados.FormataData(result.getString(Config.TAG_DTPERI)));
                        lblPeriodoLib.setText(senhalib.getString(Config.TAG_DESC) + " | " + TransformaDados.FormataData(senhalib.getString(Config.TAG_DTPERI)));
                    }else{
                        lblSenha.setText("[ " +result.optString(Config.TAG_SENHA) + " ]");
                        lblUltimaLib.setText("[ " + senhalib.optString(Config.TAG_SENLIB) + " ]");
                    }

                    lbldNota.setText("Nota: ");
                    lblNota.setText(result.getString(Config.TAG_NOTA));
                    lbldSerie.setText("Série: ");
                    lblSerie.setText(result.getString(Config.TAG_SERIE));
                    lbldPlaca.setText("Placa: ");
                    String placa = result.getString(Config.TAG_PLACA);
                    lblPlaca.setText(placa.substring(0,3) + "-" + placa.substring(3, placa.length()));
                    lbldMotorista.setText("Motorista: ");
                    lblMotorista.setText(TransformaDados.PrimeiraLetraMaius(result.getString(Config.TAG_NOMEMOT)));
                    lbldDtMarc.setText("Data/Hora Marcação: ");
                    lblDtMarc.setText(TransformaDados.FormataData(result.getString(Config.TAG_DTMAR)) + " - " + result.optString(Config.TAG_HRMAR));
                    ultimaSenha.setText("Última Senha Liberada");
                    divider2.setVisibility(View.VISIBLE);
                    lbldDtLib.setText("Data/Hora Liberação: ");
                    lblDtLib.setText(TransformaDados.FormataData(senhalib.getString(Config.TAG_DTLIB)) + " - " + senhalib.optString(Config.TAG_HRLIB));
                    lbldQntFalt.setText("Quant. Falta:");
                    lblQntFalta.setText("" + qntmarc);
                    lbldStatus.setText("Status:");
                    switch (Integer.parseInt(result.getString(Config.TAG_STATUS))){
                        case 0:
                            lblStatus.setText("Aguardando Liberação");
                            break;
                        case 1:
                            lblStatus.setText("Aguardando Retirada");
                            break;
                        case 3:
                            lblStatus.setText("Nota Retirada");
                            break;
                        default:
                            lblStatus.setText("Nota Retirada");
                            break;
                    }
                }
            }
            if(!swiping)
                progressDialog.dismiss();

            swiping = false;

        }catch (Exception e){
            e.printStackTrace();
            progressDialog.dismiss();
        }


    }


}
