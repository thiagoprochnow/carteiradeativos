package br.com.carteira.adapter.stock;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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


public class StockOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = StockOverviewAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;

    public StockOverviewAdapter(Context context) {
        this.mContext = context;
    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item;
        switch (viewType){
            // If it is the first view, return viewholder for StockPortfolio overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock_overview, parent, false);
                return new StockOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock_overview, parent, false);
                return new StockOverviewViewHolder(item);
                //item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock, parent, false);
                //return new StockDataViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                StockOverviewViewHolder viewHolder = (StockOverviewViewHolder) holder;
                mCursor.moveToPosition(position);

                double totalAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.StockPortfolio.COLUMN_VARIATION_TOTAL));
                double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL));
                double totalGain = totalAppreciation + totalIncome;
                Locale locale = new Locale("pt", "BR");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

                // Set text colors according to positive or negative values
                if (totalAppreciation >= 0){
                    viewHolder.stockAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.stockAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.stockAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.stockAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }

                if (totalIncome >= 0){
                    viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }

                if (totalGain >= 0){
                    viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }
                double buyTotal =  mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_BUY_TOTAL));
                double stockAppreciationPercent = totalAppreciation/buyTotal*100;
                double totalGainPercent = totalGain/buyTotal*100;
                double incomePercent = totalIncome/buyTotal*100;
                viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
                viewHolder.soldTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_SOLD_TOTAL)))));
                viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_CURRENT_TOTAL)))));
                viewHolder.stockAppreciation.setText(String.format(formatter.format(totalAppreciation)));
                viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
                viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
                viewHolder.stockAppreciationPercent.setText("(" + String.format("%.2f", stockAppreciationPercent) + "%)");
                viewHolder.totalIncomePercent.setText("(" + String.format("%.2f", incomePercent) + "%)");
                viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
                break;
            default:
                // If it is one of the StockData adapter views
                Log.d(LOG_TAG, "No ViewHolder found");
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

    class StockOverviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.soldTotal)
        TextView soldTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.stockAppreciation)
        TextView stockAppreciation;

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

        public StockOverviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
