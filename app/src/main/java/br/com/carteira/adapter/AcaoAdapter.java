package br.com.carteira.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.StringTokenizer;

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
        // Updates the view with the values of the elements on CardView
        Acao acao = acoes.get(position);
        // To know more of each acao variable, check Acao.java for coments
        holder.ticker.setText(acao.ticker);
        holder.stockQuantity.setText(String.valueOf(acao.stockQuantity));
        holder.boughtTotal.setText(String.format("R$%.2f", acao.boughtTotal));
        holder.currentTotal.setText(Html.fromHtml("<u>"+String.format("R$%.2f", acao.currentTotal)+"</u>"));
        holder.stockAppreciation.setText(String.format("R$%.2f", acao.stockAppreciation));
        holder.currentPercent.setText(String.valueOf(acao.currentPercent));
        holder.targetPercent.setText(String.valueOf(acao.targetPercent));
        holder.totalIncome.setText(String.format("R$%.2f", acao.totalIncome));
        holder.totalGain.setText(String.format("R$%.2f", acao.totalGain));
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
        public TextView ticker,stockQuantity, boughtTotal, currentTotal, stockAppreciation, currentPercent, targetPercent, totalIncome, totalGain;
        public AcoesViewHolder(View view){
            super(view);
            // Create the view to save on the ViewHolder
            ticker = (TextView) view.findViewById(R.id.ticker);
            stockQuantity = (TextView) view.findViewById(R.id.stockQuantity);
            boughtTotal = (TextView) view.findViewById(R.id.boughtTotal);
            currentTotal = (TextView) view.findViewById(R.id.currentTotal);
            stockAppreciation = (TextView) view.findViewById(R.id.stockAppreciation);
            currentPercent = (TextView) view.findViewById(R.id.currentPercent);
            targetPercent = (TextView) view.findViewById(R.id.targetPercent);
            totalIncome = (TextView) view.findViewById(R.id.totalIncome);
            totalGain = (TextView) view.findViewById(R.id.totalGain);
        }
    }

}
