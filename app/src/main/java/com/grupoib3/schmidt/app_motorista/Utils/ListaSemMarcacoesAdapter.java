package com.grupoib3.schmidt.app_motorista.Utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grupoib3.schmidt.app_motorista.R;

import java.util.ArrayList;
import java.util.List;

public class ListaSemMarcacoesAdapter extends RecyclerView.Adapter<ListaSemMarcacoesAdapter.ViewHolder> {

    List<String> _sMarc;
    public ListaSemMarcacoesAdapter() {
        _sMarc = new ArrayList<>();
        _sMarc.add("Não há marcações nesta filial");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marcacoes_sem_item, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String des = _sMarc.get(position);
        holder.txt_semitem.setText(des);

    }

    @Override
    public int getItemCount() {
        return _sMarc.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_semitem;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_semitem = (TextView) itemView.findViewById(R.id.txt_sem_marcacao);
        }
    }
}
