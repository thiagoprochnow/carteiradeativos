package br.com.carteira.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.carteira.R;
import br.com.carteira.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StockPortfolioAdapter extends RecyclerView.Adapter<StockPortfolioAdapter.StockPortfolioViewHolder> {
    private static final String LOG_TAG = StockPortfolioAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private StockAdapterOnClickHandler mClickHandler;

    public StockPortfolioAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;

    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public StockPortfolioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock, parent, false);
        return new StockPortfolioViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockPortfolioViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        double stockAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockData.COLUMN_VARIATION));
        double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockData.COLUMN_INCOME_TOTAL));
        double totalGain = stockAppreciation+totalIncome;
        Locale locale = new Locale( "pt", "BR" );
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        // Get handled values of StockTransaction with current symbol
        holder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                .StockTransaction.
                COLUMN_SYMBOL)));
        holder.stockQuantity.setText(Integer.toString(mCursor.getInt(mCursor.getColumnIndex
                (PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL))));
        holder.boughtTotal.setText(String.format(formatter.format(mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL)))));
        holder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_CURRENT_TOTAL)))));
        holder.objectivePercent.setText(String.format("%.2f",mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_OBJECTIVE_PERCENT))) + "%");
        holder.stockAppreciation.setText(String.format(formatter.format(stockAppreciation)));
        holder.currentPercent.setText(String.format("%.2f",mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_CURRENT_PERCENT))) + "%");
        holder.totalIncome.setText(String.format(formatter.format(totalIncome)));
        holder.totalGain.setText(String.format(formatter.format(totalGain)));
        holder.stockAppreciationPercent.setText("("+ String.format("%.2f",mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_VARIATION_PERCENT))) + "%)");
        holder.totalIncomePercent.setText("("+ String.format("%.2f",mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_INCOME_TOTAL_PERCENT))) + "%)");
        holder.totalGainPercent.setText("("+ String.format("%.2f",mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_TOTAL_GAIN_PERCENT))) + "%)");

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
        void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo, String symbol);
    }

    class StockPortfolioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

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

        @BindView(R.id.stockAppreciationPercent)
        TextView stockAppreciationPercent;

        @BindView(R.id.totalIncomePercent)
        TextView totalIncomePercent;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        StockPortfolioViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_SYMBOL);
            mClickHandler.onClick(mCursor.getString(symbolColumn));
        }

        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_SYMBOL);
            mClickHandler.onCreateContextMenu(menu, v , menuInfo, mCursor.getString(symbolColumn));
        }
    }
}
