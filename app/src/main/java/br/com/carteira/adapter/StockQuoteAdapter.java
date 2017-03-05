package br.com.carteira.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.com.carteira.R;
import br.com.carteira.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StockQuoteAdapter extends RecyclerView.Adapter<StockQuoteAdapter
        .StockQuoteViewHolder> {

    final private Context mContext;
    private Cursor mCursor;
    private StockAdapterOnClickHandler mClickHandler;

    public StockQuoteAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;

    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    public String getSymbolAtPosition(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(mCursor.getColumnIndex(PortfolioContract.StockSymbol.COLUMN_SYMBOL));
    }

    @Override
    public StockQuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock, parent, false);
        return new StockQuoteViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockQuoteViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        // Get handled values of StockQuote with current symbol
        String symbol = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.StockSymbol.
                COLUMN_SYMBOL));
        Bundle values = getValuesFromSymbol(symbol);
        if(!values.isEmpty()) {
            // Receive symbol from Loader and query for StockQuote information
            holder.symbol.setText(symbol);
            holder.stockQuantity.setText(Integer.toString(values.getInt("quantity")));
            // TODO: Below values are stored in DB as REALs.
            // We'll need to format them to currency number format.
            holder.boughtTotal.setText("R$" + String.format("%.2f", values.getDouble("bought_total")));
            holder.currentTotal.setText("R$" + String.format("%.2f", values.getDouble("bought_total")));
            holder.objectivePercent.setText(String.format("%.2f", values.getDouble("objective_percent"))+"%");
            holder.stockAppreciation.setText("R$" + String.format("%.2f", values.getDouble("bought_total")));
            holder.currentPercent.setText(String.format("%.2f", values.getDouble("objective_percent"))+"%");
            holder.totalIncome.setText("R$" + String.format("%.2f", values.getDouble("bought_total")));
            holder.totalGain.setText("R$" + String.format("%.2f", values.getDouble("bought_total")));
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null) {
            count = mCursor.getCount();
        }
        return count;
    }


    public interface StockAdapterOnClickHandler {
        void onClick(String symbol);
        void onLongClick(String symbol);
    }

    class StockQuoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.stockQuantity)
        TextView stockQuantity;

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.objectivePercent)
        TextView objectivePercent;

        @BindView(R.id.stockAppreciation)
        TextView stockAppreciation;

        @BindView(R.id.currentPercent)
        TextView currentPercent;

        @BindView(R.id.totalIncome)
        TextView totalIncome;

        @BindView(R.id.totalGain)
        TextView totalGain;


        StockQuoteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockSymbol.COLUMN_SYMBOL);
            mClickHandler.onClick(mCursor.getString(symbolColumn));
        }

        @Override
        public boolean onLongClick(View v){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockSymbol.COLUMN_SYMBOL);
            mClickHandler.onLongClick(mCursor.getString(symbolColumn));
            return true;
        }
    }

    // Receive the stock symbol and query for StockQuote information
    // If there is more then one line on table for same symbol, it will sum values and calculate
    // information
    public Bundle getValuesFromSymbol(String symbol){
        // Total quantity of specific stock bought by user
        int totalQuantity=0;
        // Total value bought of specific stock
        double bougthTotal=0;
        // Last imputted objective percent
        double objectivePercent=0;
        // New bundle to return calculated values to insert in adapter
        Bundle bundle = new Bundle();
        // Insert symbol in URI to make query for specific symbol
        Cursor StockQuotesCursor = mContext.getContentResolver().query(PortfolioContract.StockQuote.makeUriForStockQuote(symbol), null, null, null, null);
        if (StockQuotesCursor != null) {
            StockQuotesCursor.moveToFirst();
            do {
                // Get values of each "Buy Stock" inputed in AddStockForm
                int quantity = StockQuotesCursor.getInt(StockQuotesCursor.getColumnIndex(PortfolioContract.StockQuote.COLUMN_QUANTITY));
                double boughtPrice = StockQuotesCursor.getInt(StockQuotesCursor.getColumnIndex(PortfolioContract.StockQuote.COLUMN_BOUGHT_PRICE));
                objectivePercent = StockQuotesCursor.getInt(StockQuotesCursor.getColumnIndex(PortfolioContract.StockQuote.COLUMN_OBJECTIVE_PERCENT));
                bougthTotal += quantity*boughtPrice;
                totalQuantity += quantity;
            } while (StockQuotesCursor.moveToNext());
        }
        // Put calculated value on blundle to return and use on Adapter
        bundle.putInt("quantity", totalQuantity);
        bundle.putDouble("bought_total", bougthTotal);
        bundle.putDouble("objective_percent", objectivePercent);

        return bundle;
    }
}
