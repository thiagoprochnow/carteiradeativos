package br.com.carteira.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.carteira.R;
import br.com.carteira.domain.Stock;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StocksViewHolder> {

    protected static final String TAG = "StockAdapter";
    private final List<Stock> stocks;
    private final Context context;
    StockOnClickListener stockOnClickListener;
    public StockAdapter(Context context, List<Stock> stocks, StockOnClickListener stockOnClickListener){
        this.context = context;
        this.stocks = stocks;
        this.stockOnClickListener = stockOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.stocks != null ? this.stocks.size() : 0;
    }

    @Override
    public StocksViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_stock, viewGroup, false);
        StocksViewHolder holder = new StocksViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final StocksViewHolder holder, final int position){
        // Updates the view with the values of the elements on CardView
        Stock stock = stocks.get(position);
        // To know more of each stock variable, check Stock.java for coments
        holder.ticker.setText(stock.getTicker());
        holder.stockQuantity.setText(String.valueOf(stock.getStockQuantity()));
        holder.boughtTotal.setText(String.format("R$%.2f", stock.getBoughtTotal()));
        holder.currentTotal.setText(Html.fromHtml("<u>"+String.format("R$%.2f", stock.getCurrentTotal())+"</u>"));
        holder.stockAppreciation.setText(String.format("R$%.2f", stock.getStockAppreciation()));
        holder.currentPercent.setText(String.valueOf(stock.getCurrentPercent())+"%");
        holder.objectivePercent.setText(String.valueOf(stock.getObjectivePercent())+"%");
        holder.totalIncome.setText(String.format("R$%.2f", stock.getTotalIncome()));
        holder.totalGain.setText(String.format("R$%.2f", stock.getTotalGain()));
        // Click on the CardView of the stock
        if(stockOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    stockOnClickListener.onClickStock(holder.itemView, position);
                }
            });
        }
    }

    public interface StockOnClickListener{
        public void onClickStock(View view, int idx);
    }

    public static class StocksViewHolder extends RecyclerView.ViewHolder {
        public TextView ticker,stockQuantity, boughtTotal, currentTotal, stockAppreciation, currentPercent, objectivePercent, totalIncome, totalGain;
        public StocksViewHolder(View view){
            super(view);
            // Create the view to save on the ViewHolder
            ticker = (TextView) view.findViewById(R.id.ticker);
            stockQuantity = (TextView) view.findViewById(R.id.stockQuantity);
            boughtTotal = (TextView) view.findViewById(R.id.boughtTotal);
            currentTotal = (TextView) view.findViewById(R.id.currentTotal);
            stockAppreciation = (TextView) view.findViewById(R.id.stockAppreciation);
            currentPercent = (TextView) view.findViewById(R.id.currentPercent);
            objectivePercent = (TextView) view.findViewById(R.id.objectivePercent);
            totalIncome = (TextView) view.findViewById(R.id.totalIncome);
            totalGain = (TextView) view.findViewById(R.id.totalGain);
        }
    }

}
