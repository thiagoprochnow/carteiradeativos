package br.com.carteira.adapter.fii;

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


public class FiiOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = FiiOverviewAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;

    public FiiOverviewAdapter(Context context) {
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
            // If it is the first view, return viewholder for FiiPortfolio overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii_overview, parent, false);
                return new FiiOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii_overview, parent, false);
                return new FiiOverviewViewHolder(item);
                //item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii, parent, false);
                //return new FiiDataViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                FiiOverviewViewHolder viewHolder = (FiiOverviewViewHolder) holder;
                mCursor.moveToPosition(position);

                double totalAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.FiiPortfolio.COLUMN_VARIATION_TOTAL));
                double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.FiiPortfolio.COLUMN_INCOME_TOTAL));
                double totalGain = totalAppreciation + totalIncome;
                Locale locale = new Locale("pt", "BR");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

                // Set text colors according to positive or negative values
                if (totalAppreciation >= 0){
                    viewHolder.fiiAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.fiiAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.fiiAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.fiiAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
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
                        mCursor.getColumnIndex(PortfolioContract.FiiPortfolio.COLUMN_BUY_TOTAL));
                double fiiAppreciationPercent = 0;
                double totalGainPercent = 0;
                double incomePercent = 0;
                if(buyTotal != 0) {
                    fiiAppreciationPercent = totalAppreciation / buyTotal * 100;
                    totalGainPercent = totalGain / buyTotal * 100;
                    incomePercent = totalIncome / buyTotal * 100;
                }
                viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
                viewHolder.soldTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FiiPortfolio.COLUMN_SOLD_TOTAL)))));
                viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FiiPortfolio.COLUMN_CURRENT_TOTAL)))));
                viewHolder.fiiAppreciation.setText(String.format(formatter.format(totalAppreciation)));
                viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
                viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
                viewHolder.fiiAppreciationPercent.setText("(" + String.format("%.2f", fiiAppreciationPercent) + "%)");
                viewHolder.totalIncomePercent.setText("(" + String.format("%.2f", incomePercent) + "%)");
                viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
                break;
            default:
                // If it is one of the FiiData adapter views
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

    class FiiOverviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.soldTotal)
        TextView soldTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.fiiAppreciation)
        TextView fiiAppreciation;

        @BindView(R.id.totalIncome)
        TextView totalIncome;

        @BindView(R.id.totalGain)
        TextView totalGain;

        @BindView(R.id.fiiAppreciationPercent)
        TextView fiiAppreciationPercent;

        @BindView(R.id.totalIncomePercent)
        TextView totalIncomePercent;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        public FiiOverviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
