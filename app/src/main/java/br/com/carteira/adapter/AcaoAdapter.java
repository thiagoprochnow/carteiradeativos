package br.com.carteira.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.carteira.R;
import br.com.carteira.domain.Acao;

public class AcaoAdapter extends RecyclerView.Adapter<AcaoAdapter.AcoesViewHolder> {

    protected static final String TAG = "AcaoAdapter";
    private final List<Acao> acoes;
    private final Context context;
    AcaoOnClickListener acaoOnClickListener;
    public AcaoAdapter(Context context, List<Acao> acoes, AcaoOnClickListener acaoOnClickListener){
        this.context = context;
        this.acoes = acoes;
        this.acaoOnClickListener = acaoOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.acoes != null ? this.acoes.size() : 0;
    }

    @Override
    public AcoesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_acao, viewGroup, false);
        AcoesViewHolder holder = new AcoesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AcoesViewHolder holder, final int position){
        // Atualiza a view
        Acao acao = acoes.get(position);
        holder.ticker.setText(acao.ticker);
        holder.companyName.setText(acao.companyName);
        // Click on the CardView of the stock
        if(acaoOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    acaoOnClickListener.onClickAcao(holder.itemView, position);
                }
            });
        }
    }

    public interface AcaoOnClickListener{
        public void onClickAcao(View view, int idx);
    }

    public static class AcoesViewHolder extends RecyclerView.ViewHolder {
        public TextView ticker,companyName, boughtValue, currentValue;
        public AcoesViewHolder(View view){
            super(view);
            // Cria as views para salvar no ViewHolder
            ticker = (TextView) view.findViewById(R.id.ticker);
            companyName = (TextView) view.findViewById(R.id.companyName);
            boughtValue = (TextView) view.findViewById(R.id.boughtValue);
            currentValue = (TextView) view.findViewById(R.id.currentValue);
        }
    }

}
