package com.grupoib3.schmidt.app_motorista.Utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.grupoib3.schmidt.app_motorista.Models.Marcacao;
import com.grupoib3.schmidt.app_motorista.R;

import java.util.ArrayList;
import java.util.List;

public class ListaMarcacoesAdapter extends RecyclerView.Adapter<ListaMarcacoesAdapter.MarcacoesViewHolder> implements Filterable {

    private AoClicarNoItem mClicarItem;
    private List<Marcacao> Marcacoes;
    private List<Marcacao> FiltraMarcacoes;
    //private ObservableField<List<Marcacao>> Marcacoes = new ObservableField<>();

    public ListaMarcacoesAdapter(List<Marcacao> marcacoes, AoClicarNoItem mclicaritem){
        Marcacoes = marcacoes;
        mClicarItem = mclicaritem;
        FiltraMarcacoes = marcacoes;
    }
    @NonNull
    @Override
    public MarcacoesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marcacoes_item, parent, false);

        final MarcacoesViewHolder vh = new MarcacoesViewHolder(v);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClicarItem != null){
                    int pos = vh.getAdapterPosition();
                    Marcacao marcacao = FiltraMarcacoes.get(pos);
                    mClicarItem.itemClicado(marcacao);
                }

            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MarcacoesViewHolder holder, int position) {
        Marcacao marcacao = FiltraMarcacoes.get(position);
        if(marcacao != null){
            holder.txtConvenio.setText(marcacao.getlM5_NOME());
            holder.txtDtMarc.setText(marcacao.getlM4_DTMAR());
            holder.imgIcon.setContentDescription(marcacao.getLm4_DSTATUS());
            switch (Integer.parseInt(marcacao.getlM4_STATUS())){
                case 0:
                    holder.imgIcon.setImageResource(R.drawable.truck_green);
                    break;
                case 1:
                    holder.imgIcon.setImageResource(R.drawable.truck_yellow);
                    break;
                case 3:
                    holder.imgIcon.setImageResource(R.drawable.truck_red);
                    break;
                default:
                    holder.imgIcon.setImageResource(R.drawable.truck_red);
                    break;
            }
        }

     }

    public void clear(){FiltraMarcacoes.clear();}

    /**
     * Método publico chamado para atualziar a lista.
     * @param marc Novo objeto que será incluido na lista.
     */

    public void updateList(List<Marcacao> marc) {
        insertItem(marc);
    }

    // Método responsável por inserir um novo usuário na lista
    //e notificar que há novos itens.
    private void insertItem(List<Marcacao> marc) {
        try{
            //int posInicial = getItemCount();
            //Marcacoes.add(marc);
            FiltraMarcacoes.addAll(marc);
            notifyDataSetChanged();

        }catch (Exception e){
            //Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return FiltraMarcacoes != null ? FiltraMarcacoes.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    FiltraMarcacoes = Marcacoes;
                } else {

                    ArrayList<Marcacao> filteredList = new ArrayList<>();

                    for (Marcacao marcacao : Marcacoes) {

                        if (marcacao.getlM5_NOME().toLowerCase().contains(charString) || marcacao.getlM4_DTMAR().toLowerCase().contains(charString) || marcacao.getlM4_DTPERI().toLowerCase().contains(charString)
                                || marcacao.getlM4_DTREC().toLowerCase().contains(charString) || marcacao.getlM4_DTLIB().toLowerCase().contains(charString) || marcacao.getlM4_PERIOD().toLowerCase().contains(charString)) {

                            filteredList.add(marcacao);
                        }
                    }

                    FiltraMarcacoes = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = FiltraMarcacoes;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                FiltraMarcacoes = (ArrayList<Marcacao>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class MarcacoesViewHolder extends RecyclerView.ViewHolder{

        //@BindView(R.id.txtConvenio)
        public TextView txtConvenio;

        //@BindView(R.id.txtDtMarc)
        public TextView txtDtMarc;

        public ImageView imgIcon;

        public MarcacoesViewHolder (View itemView) {
            super(itemView);
            txtConvenio = (TextView) itemView.findViewById(R.id.txtConvenio);
            txtDtMarc = (TextView) itemView.findViewById(R.id.txtDtMarc);
            imgIcon = (ImageView) itemView.findViewById(R.id.imgIcon);
        }
    }

    public interface AoClicarNoItem{
        void itemClicado(Marcacao marcacao);
    }
}
