package com.grupoib3.schmidt.app_motorista.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.grupoib3.schmidt.app_motorista.Models.Notificacao;
import com.grupoib3.schmidt.app_motorista.R;

import java.util.List;

public class ListaNotificacoesAdapter extends RecyclerView.Adapter<ListaNotificacoesAdapter.NotificacoesViewHolder> {

    private List<Notificacao> Notificacoes;
    private AoClicarNoItem mClicarItem;
    private Context context;

    public ListaNotificacoesAdapter(List<Notificacao> notificacoes, AoClicarNoItem aoClicarNoItem, Context context){
        this.Notificacoes = notificacoes;
        this.mClicarItem = aoClicarNoItem;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificacoesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notificacoes_item, parent, false);

        final NotificacoesViewHolder vh = new NotificacoesViewHolder(v);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClicarItem != null){
                    try {
                        int pos = vh.getAdapterPosition();
                        Notificacao notificacao = Notificacoes.get(pos);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(notificacao.getTitulo_notificacao());
                        builder.setMessage(notificacao.getMsg_notificacao());
                        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        mClicarItem.itemClicado(notificacao);
                    }catch (Exception e){
                        throw e;
                    }

                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacoesViewHolder holder, int position) {
        Notificacao notificacao = Notificacoes.get(position);
        if(notificacao != null){
            holder.txtTitleNotificacao.setText(notificacao.getTitulo_notificacao());
            holder.txtDataNotificacao.setText(notificacao.getData_notificacao());
            switch (notificacao.getStatus_notificacao()){
                case 0:
                    holder.imgIcon.setImageResource(R.drawable.truck_green);
                    break;
                case 1:
                    holder.imgIcon.setImageResource(R.drawable.truck_red);
                    break;
                default:
                    holder.imgIcon.setImageResource(R.drawable.truck_green);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return Notificacoes != null? Notificacoes.size(): 0;
    }

    public static class NotificacoesViewHolder extends RecyclerView.ViewHolder{

        //@BindView(R.id.txtConvenio)
        public TextView txtDataNotificacao;

        //@BindView(R.id.txtDtMarc)
        public TextView txtTitleNotificacao;
        public ImageView imgIcon;


        public NotificacoesViewHolder (View itemView) {
            super(itemView);
            txtTitleNotificacao = (TextView) itemView.findViewById(R.id.txtTitleNotifi);
            txtDataNotificacao = (TextView) itemView.findViewById(R.id.txtDataNotifi);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgIconNotifi);
        }
    }

    public interface AoClicarNoItem{
        void itemClicado(Notificacao notificacao);
    }
}
