package com.grupoib3.schmidt.app_motorista.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grupoib3.schmidt.app_motorista.Models.Notificacao;
import com.grupoib3.schmidt.app_motorista.Models.Usuario;
import com.grupoib3.schmidt.app_motorista.R;
import com.grupoib3.schmidt.app_motorista.Utils.BancoController;
import com.grupoib3.schmidt.app_motorista.Utils.ListaNotificacoesAdapter;
import com.grupoib3.schmidt.app_motorista.Utils.ListaSemNotificacaoesAdapter;
import com.grupoib3.schmidt.app_motorista.Utils.UsuarioServices;

import java.text.ParseException;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private BancoController bd;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeNotifiList;
    private boolean swiping = false;
    private List<Notificacao> notificacoes;
    private ListaNotificacoesAdapter adapter;
    private Context context;
    private int userId;
    private int userIdFilial;
    private TextView badge;
    private FrameLayout redCircle;
    private int qntNotifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);


            context = NotificationActivity.this;
            bd = new BancoController(getBaseContext());

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            userId = (int) bundle.getSerializable("userId");
            userIdFilial = (int) bundle.getSerializable("userIdFilial");

            swipeNotifiList = (SwipeRefreshLayout) this.findViewById(R.id.swipeListNotifi);
            recyclerView = (RecyclerView) this.findViewById(R.id.recycler_notificacoes);
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));

            notificacoes = bd.carregaNotificacoes(userId, userIdFilial);

            if(notificacoes.size() > 0){
                adapter = new ListaNotificacoesAdapter(notificacoes, mListener, context);

                recyclerView.setAdapter(adapter);
            }else{
                ListaSemNotificacaoesAdapter adapter_null = new ListaSemNotificacaoesAdapter();
                recyclerView.setAdapter(adapter_null);
            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try{
            // Inflate the menu; this adds items to the action bar if it is present.
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.menu_notificacao, menu);
            return true;
        }catch (Exception ex){
            throw ex;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem notifi = menu.findItem(R.id.action_notifi);
        FrameLayout rootView = (FrameLayout) notifi.getActionView();
        rootView.setPadding(0,0,50,0);

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        badge = (TextView) redCircle.findViewById(R.id.badge_notification_txt);

        qntNotifi = bd.contaNotificacoesAtivas(userId, userIdFilial);

        if(qntNotifi > 0){
            badge.setText(String.valueOf(qntNotifi));
            badge.setVisibility(View.VISIBLE);
        }
        else
            if(redCircle.getVisibility() == View.VISIBLE)
                redCircle.setVisibility(View.INVISIBLE);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Intent intent = new Intent(this.getBaseContext(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("userId", userId);
                bundle.putSerializable("userIdFilial", userIdFilial);
                intent.putExtras(bundle);
                this.startActivity(intent);
                finish();
                break;
            default:break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this.getBaseContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("userId", userId);
        bundle.putSerializable("userIdFilial", userIdFilial);
        intent.putExtras(bundle);
        this.startActivity(intent);
        finish();
    }

    private void showNotificacoes(){
        try{
            notificacoes = bd.carregaNotificacoes(userId, userIdFilial);
            if(notificacoes.size() > 0){
                adapter = new ListaNotificacoesAdapter(notificacoes, mListener, context);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            qntNotifi = bd.contaNotificacoesAtivas(userId, userIdFilial);

            if(qntNotifi > 0){
                badge.setText(String.valueOf(qntNotifi));
                badge.setVisibility(View.VISIBLE);
            }
            else
            if(redCircle.getVisibility() == View.VISIBLE)
                redCircle.setVisibility(View.INVISIBLE);
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
                    }if(notificacao.getUrl() != null){
                        Intent intent = new Intent(context, WebActivity.class);
                        //Bundle bundle = new Bundle();
                        //bundle.putSerializable("marcacao", marcacao);
                        intent.putExtra("url", notificacao.getUrl());
                        context.startActivity(intent);
                    }
                }
            };
}
