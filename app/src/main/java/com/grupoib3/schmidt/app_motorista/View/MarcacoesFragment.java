package com.grupoib3.schmidt.app_motorista.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.grupoib3.schmidt.app_motorista.Config.Config;
import com.grupoib3.schmidt.app_motorista.Models.Marcacao;
import com.grupoib3.schmidt.app_motorista.Models.Usuario;
import com.grupoib3.schmidt.app_motorista.Utils.BancoController;
import com.grupoib3.schmidt.app_motorista.Utils.CriaBanco;
import com.grupoib3.schmidt.app_motorista.R;
import com.grupoib3.schmidt.app_motorista.Utils.HttpServices;
import com.grupoib3.schmidt.app_motorista.Utils.ListaMarcacoesAdapter;
import com.grupoib3.schmidt.app_motorista.Utils.ListaSemMarcacoesAdapter;
import com.grupoib3.schmidt.app_motorista.Utils.TransformaDados;
import com.grupoib3.schmidt.app_motorista.Utils.UsuarioServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class MarcacoesFragment extends Fragment {


    ListaMarcacoesAdapter adapter;
    List<Marcacao> marcacaoList;
    RecyclerView recyclerView;
    String JSON_STRING;
    Context context;
    SearchView searchView;
    SwipeRefreshLayout swipeMarcList;
    BancoController bd;
    Usuario user;
    ProgressDialog progressDialog;
    boolean swiping = false;

    public MarcacoesFragment() {
        // Required empty public constructor
    }

    public static MarcacoesFragment newInstance() {
        MarcacoesFragment fragment = new MarcacoesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_marcacoes, container, false);

            bd = new BancoController(getContext());
            try {
                user = UsuarioServices.LoginMotorista(getContext());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            recyclerView = (RecyclerView) view.findViewById(R.id.recycler_marcacoes);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

            ListaSemMarcacoesAdapter adapter_null = new ListaSemMarcacoesAdapter();


            recyclerView.setAdapter(adapter_null);

            searchView = (SearchView) view.findViewById(R.id.search_marc);


            swipeMarcList = (SwipeRefreshLayout) view.findViewById(R.id.swipeListMarc);

            swipeMarcList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swiping = true;
                    getJson();
                }
            });

            getJson();

            context = view.getContext();
            return view;
    }


    public void getJson(){
        class GetJson extends AsyncTask<Void, Void, String> {

            private int total = 0;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(!swiping)
                    progressDialog = ProgressDialog.show(getContext(), "Carregando Marcações", "Aguarde...", false, false);
            }

            @Override
            protected String doInBackground(Void... params) {
                try{

                    HttpServices rh = new HttpServices();
                    Cursor cursor = bd.carregaURLFilialByStatus();
                    String url = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.URL_FILIAL)) + Config.URL_Display;
                    String s = rh.getJSONFromAPI(url, "", "GET", user.getAccessToken());
                    return s;
                }catch (Exception ex){
                    throw ex;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showMarcacoes();
                swipeMarcList.setRefreshing(false);
            }
        }
        GetJson gj = new GetJson();
        gj.execute();
    }

    private void showMarcacoes(){
        JSONObject jsonObject;
        try{
            jsonObject = new JSONObject(JSON_STRING);
            int qntmarc = jsonObject.optInt(Config.TAG_ARRAYQNT);
            if(qntmarc > 0){
                JSONArray result = jsonObject.optJSONArray(Config.TAG_ARRAYFILA);
                marcacaoList = new ArrayList<>();

                for (int i = 0 ; i < result.length(); i++){
                    JSONObject j  = result.optJSONObject(i);
                    Marcacao marc = new Marcacao();
                    marc.setlM4_FILIAL(j.optString(Config.TAG_FILIAL));
                    marc.setdA4_COD(j.optString(Config.TAG_CODMOT));
                    marc.setdA4_NOME(TransformaDados.PrimeiraLetraMaius(j.optString(Config.TAG_NOMEMOT)));
                    marc.setlM5_COD(j.optString(Config.TAG_CODCONV));
                    marc.setlM5_NOME(j.optString(Config.TAG_NOMECONV));
                    marc.setlM5_REGRA(j.optInt(Config.TAG_REGRA));
                    String placa = j.optString(Config.TAG_PLACA);
                    marc.setlM3_PLACA(placa.substring(0,3) + "-" + placa.substring(3, placa.length()));
                    marc.setlM3_TIPO(j.optString(Config.TAG_TIPO));
                    marc.setlM4_COD(j.optString(Config.TAG_CODMARC));
                    marc.setlM4_STATUS(j.optString(Config.TAG_STATUS));
                    marc.setlM4_SENHA(j.optString(Config.TAG_SENHA));
                    marc.setlM4_CICLO(j.optString(Config.TAG_CICLO));
                    marc.setlM4_DTMAR(j.optString(Config.TAG_DTMAR));
                    marc.setlM4_HRMAR(j.optString(Config.TAG_HRMAR));
                    marc.setlM4_DTLIB(j.optString(Config.TAG_DTLIB));
                    marc.setlM4_HRLIB(j.optString(Config.TAG_HRLIB));
                    marc.setlM4_DTREC(j.optString(Config.TAG_DTREC));
                    marc.setlM4_HRREC(j.optString(Config.TAG_HRREC));
                    marc.setlM4_NOTA(j.optString(Config.TAG_NOTA));
                    marc.setlM4_SERIE(j.optString(Config.TAG_SERIE));
                    marc.setlM4_DTPERI(j.optString(Config.TAG_DTPERI));
                    marc.setlM4_SENPER(j.optString(Config.TAG_SENPER));
                    marc.setlM4_PERIOD(j.optString(Config.TAG_PERIOD));
                    marc.setDescricao(j.optString(Config.TAG_DESC));

                    if(marc.getlM5_REGRA() != 1){
                        marc.setDescricao(marc.getDescricao() + " | " + TransformaDados.FormataData(marc.getlM4_DTPERI()));
                        marc.setlM4_SENHA(!marc.getlM4_SENPER().equals("")? "[ " + marc.getlM4_SENPER() + " ]" : "[ " + marc.getlM4_SENHA() + " ]");
                    }else{
                        marc.setlM4_SENHA("[ " + marc.getlM4_SENHA() + " ]");
                        marc.setDescricao("");
                    }

                    switch (Integer.parseInt(marc.getlM4_STATUS())){
                        case 0:
                            marc.setLm4_DSTATUS("Aguardando Liberação");
                            break;
                        case 1:
                            marc.setLm4_DSTATUS("Aguardando Retirada");
                            break;
                        case 3:
                            marc.setLm4_DSTATUS("Nota Retirada");
                            break;
                        default:
                            marc.setLm4_DSTATUS("Nota Retirada");
                            break;
                    }

                    marc.setlM4_DTLIB(TransformaDados.FormataData(marc.getlM4_DTLIB()) + " - " + marc.getlM4_HRLIB());
                    marc.setlM4_DTMAR(TransformaDados.FormataData(marc.getlM4_DTMAR()) + " - " + marc.getlM4_HRMAR());
                    marc.setlM4_DTREC(TransformaDados.FormataData(marc.getlM4_DTREC()) + " - " + marc.getlM4_HRREC());
                    marcacaoList.add(marc);
                }

                adapter = new ListaMarcacoesAdapter(marcacaoList, mListener);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String query) {
                        //FILTER AS YOU TYPE
                        adapter.getFilter().filter(query);
                        return false;
                    }
                });
            }
            if(!swiping)
                progressDialog.dismiss();
            swiping = false;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public ListaMarcacoesAdapter.AoClicarNoItem mListener=
            new ListaMarcacoesAdapter.AoClicarNoItem() {
                @Override
                public void itemClicado(Marcacao marcacao) {

                    Intent intent = new Intent(context, DetalheMarcacaoActivity.class);
                    //Bundle bundle = new Bundle();
                    //bundle.putSerializable("marcacao", marcacao);
                    intent.putExtra("marcacao", marcacao);
                    context.startActivity(intent);
                }
            };
}
