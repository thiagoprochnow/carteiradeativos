package br.com.carteira.adapter.fii;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
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


public class FiiDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = FiiDataAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private FiiAdapterOnClickHandler mClickHandler;

    public FiiDataAdapter(Context context, FiiAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;

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
                item = LayoutInflater.from(mContext).inflate(R.layout.fii_summary, parent, false);
                return new FiiSummaryViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii, parent, false);
                return new FiiDataViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                FiiSummaryViewHolder summaryViewHolder = (FiiSummaryViewHolder) holder;
                // If it is the first view, return viewholder for FiiPortfolio overview
                if (mCursor != null && summaryViewHolder != null) {
                    if (mCursor.getCount() != 0) {
                        summaryViewHolder.itemView.setVisibility(View.VISIBLE);
                    } else {
                        summaryViewHolder.itemView.setVisibility(View.GONE);
                    }
                }
                break;
            default:
                // If it is one of the FiiData adapter views
                mCursor.moveToPosition(position-1);
                FiiDataViewHolder viewHolder = (FiiDataViewHolder) holder;
                double fiiAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.FiiData.COLUMN_VARIATION));
                double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.FiiData.COLUMN_INCOME));
                double totalGain = fiiAppreciation + totalIncome;
                Locale locale = new Locale("pt", "BR");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

                // Set text colors according to positive or negative values
                if (fiiAppreciation >= 0){
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
                double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_BUY_VALUE_TOTAL));
                double variationPercent = fiiAppreciation/buyTotal*100;
                double netIncomePercent = totalIncome/buyTotal*100;
                double totalGainPercent = totalGain/buyTotal*100;
                // Get handled values of FiiData with current symbol
                viewHolder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                        .FiiData.
                        COLUMN_SYMBOL)));
                viewHolder.fiiQuantity.setText(Integer.toString(mCursor.getInt(mCursor.getColumnIndex
                        (PortfolioContract.FiiData.COLUMN_QUANTITY_TOTAL))));
                viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
                viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_CURRENT_TOTAL)))));
                viewHolder.objectivePercent.setText(String.format("%.2f", mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_OBJECTIVE_PERCENT))) + "%");

                viewHolder.fiiAppreciation.setText(String.format(formatter.format(fiiAppreciation)));
                viewHolder.currentPercent.setText(String.format("%.2f", mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_CURRENT_PERCENT)))
                        + "%");
                viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
                viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
                viewHolder.fiiAppreciationPercent.setText("(" + String.format("%.2f", variationPercent) + "%)");
                viewHolder.totalIncomePercent.setText("(" + String.format("%.2f", netIncomePercent) + "%)");
                viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
        }

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null) {
            count = mCursor.getCount();
            count++;
        }
        return count;
    }


    public interface FiiAdapterOnClickHandler {
        void onClick(String symbol);
        void onCreateContextMenu(ContextMenu menu, View v,
                                 ContextMenu.ContextMenuInfo menuInfo, String symbol);
    }

    class FiiDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.fiiQuantity)
        TextView fiiQuantity;

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.objectivePercent)
        TextView objectivePercent;

        @BindView(R.id.fiiAppreciation)
        TextView fiiAppreciation;

        @BindView(R.id.currentPercent)
        TextView currentPercent;

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

        public FiiDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition-1);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_SYMBOL);
            mClickHandler.onClick(mCursor.getString(symbolColumn));
        }

        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition-1);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_SYMBOL);
            mClickHandler.onCreateContextMenu(menu, v , menuInfo, mCursor.getString(symbolColumn));
        }
    }

    class FiiSummaryViewHolder extends RecyclerView.ViewHolder{

        public FiiSummaryViewHolder(View itemView){
            super(itemView);
        }
    }
}
