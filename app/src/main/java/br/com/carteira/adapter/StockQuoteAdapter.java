package br.com.carteira.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    String getSymbolAtPosition(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(mCursor.getColumnIndex(PortfolioContract.StockQuote.COLUMN_SYMBOL));
    }

    @Override
    public StockQuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock, parent, false);
        return new StockQuoteViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockQuoteViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract.StockQuote.
                COLUMN_SYMBOL)));
        holder.stockQuantity.setText(Integer.toString(mCursor.getInt(mCursor.getColumnIndex
                (PortfolioContract.StockQuote.COLUMN_QUANTITY))));
        // TODO: Below values are stored in DB as REALs.
        // We'll need to format them to currency number format.
        holder.boughtTotal.setText(Double.toString(mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockQuote.COLUMN_BOUGHT_TOTAL))));
        holder.currentTotal.setText(Double.toString(mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockQuote.COLUMN_CURRENT_TOTAL))));
        holder.objectivePercent.setText(Double.toString(mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockQuote.COLUMN_OBJECTIVE_PERCENT))));
        holder.stockAppreciation.setText(Double.toString(mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockQuote.COLUMN_APPRECIATION))));
        holder.currentPercent.setText(Double.toString(mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockQuote.COLUMN_CURRENT_PERCENT))));
        holder.totalIncome.setText(Double.toString(mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockQuote.COLUMN_TOTAL_INCOME))));
        holder.totalGain.setText(Double.toString(mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockQuote.COLUMN_TOTAL_GAIN))));
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
    }

    class StockQuoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockQuote.COLUMN_SYMBOL);
            mClickHandler.onClick(mCursor.getString(symbolColumn));
        }
    }
}
