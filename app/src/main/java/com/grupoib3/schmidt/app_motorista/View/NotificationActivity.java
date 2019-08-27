package com.grupoib3.schmidt.app_motorista.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.grupoib3.schmidt.app_motorista.Models.Notificacao;
import com.grupoib3.schmidt.app_motorista.Models.Usuario;
import com.grupoib3.schmidt.app_motorista.R;
import com.grupoib3.schmidt.app_motorista.Utils.BancoController;
import com.grupoib3.schmidt.app_motorista.Utils.ListaNotificacoesAdapter;
import com.grupoib3.schmidt.app_motorista.Utils.UsuarioServices;

import java.text.ParseException;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    BancoController bd;
    Usuario user;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeNotifiList;
    boolean swiping = false;
    List<Notificacao> notificacoes;
    ListaNotificacoesAdapter adapter;
    Context context;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        try {
            context = NotificationActivity.this;
            bd = new BancoController(getBaseContext());
            try {
                user = UsuarioServices.LoginMotorista(getBaseContext());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            swipeNotifiList = (SwipeRefreshLayout) this.findViewById(R.id.swipeListNotifi);
            recyclerView = (RecyclerView) this.findViewById(R.id.recycler_notificacoes);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));

            //ListaSemNotificacaoesAdapter adapter_null = new ListaSemNotificacaoesAdapter();
            notificacoes = bd.carregaNotificacoes(user.getId());

            adapter = new ListaNotificacoesAdapter(notificacoes, mListener, context);

            recyclerView.setAdapter(adapter);

            swipeNotifiList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swiping = true;
                    showNotificacoes();
                }
            });
        }catch (Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            startActivity(
                    new Intent(
                            NotificationActivity.this, MainActivity.class));
            finish();
        }

    }

    private void showNotificacoes(){
        try{
            notificacoes = bd.carregaNotificacoes(user.getId());
            if(notificacoes.size() > 0){
                adapter = new ListaNotificacoesAdapter(notificacoes, mListener, context);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            if(!swiping)
                progressDialog.dismiss();
            swiping = false;
            swipeNotifiList.setRefreshing(false);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public ListaNotificacoesAdapter.AoClicarNoItem mListener =
            new ListaNotificacoesAdapter.AoClicarNoItem() {
                @Override
                public void itemClicado(Notificacao notificacao) {
                    if(notificacao.getStatus_notificacao() == 0){
                        notificacao.setStatus_notificacao(1);
                        bd.InsereNotification(notificacao);
                        showNotificacoes();
                    }
                }
            };
}
