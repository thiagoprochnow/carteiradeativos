package br.com.carteira.adapter.currency;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CurrencyDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = CurrencyDataAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private CurrencyAdapterOnClickHandler mClickHandler;

    public CurrencyDataAdapter(Context context, CurrencyAdapterOnClickHandler clickHandler) {
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
            // If it is the first view, return viewholder for CurrencyPortfolio overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.currency_summary, parent, false);
                return new CurrencySummaryViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_currency, parent, false);
                return new CurrencyDataViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
                CurrencySummaryViewHolder summaryViewHolder = (CurrencySummaryViewHolder) holder;
                // If it is the first view, return viewholder for CurrencyPortfolio overview
                if (mCursor != null && summaryViewHolder != null) {
                    if (mCursor.getCount() != 0) {
                        summaryViewHolder.itemView.setVisibility(View.VISIBLE);
                    } else {
                        summaryViewHolder.itemView.setVisibility(View.GONE);
                    }
                }
                break;
            default:
                // If it is one of the CurrencyData adapter views
                mCursor.moveToPosition(position-1);
                CurrencyDataViewHolder viewHolder = (CurrencyDataViewHolder) holder;
                double currencyAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.CurrencyData.COLUMN_VARIATION));
               // double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                //        (PortfolioContract.CurrencyData.COLUMN_INCOME));
                double totalGain = currencyAppreciation;
                Locale locale = new Locale("pt", "BR");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

                // Set text colors according to positive or negative values
                if (currencyAppreciation >= 0){
                    viewHolder.currencyAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.currencyAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.currencyAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.currencyAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }

              /*  if (totalIncome >= 0){
                    viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }*/

                if (totalGain >= 0){
                    viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }
                double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.CurrencyData.COLUMN_BUY_VALUE_TOTAL));
                double variationPercent = currencyAppreciation/buyTotal*100;
               // double netIncomePercent = totalIncome/buyTotal*100;
                double totalGainPercent = totalGain/buyTotal*100;
                // Get handled values of CurrencyData with current symbol
                viewHolder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                        .CurrencyData.
                        COLUMN_SYMBOL)));
                viewHolder.currencyQuantity.setText(Integer.toString(mCursor.getInt(mCursor.getColumnIndex
                        (PortfolioContract.CurrencyData.COLUMN_QUANTITY_TOTAL))));
                viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
                viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.CurrencyData.COLUMN_CURRENT_TOTAL)))));

                viewHolder.currencyAppreciation.setText(String.format(formatter.format(currencyAppreciation)));
                viewHolder.currentPercent.setText(String.format("%.2f", mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.CurrencyData.COLUMN_CURRENT_PERCENT)))
                        + "%");
               // viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
                viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
                viewHolder.currencyAppreciationPercent.setText("(" + String.format("%.2f", variationPercent) + "%)");
               // viewHolder.totalIncomePercent.setText("(" + String.format("%.2f", netIncomePercent) + "%)");
                viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");

                viewHolder.currencyCardViewClickable.setOnClickListener(new LinearLayout.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position-1);
                        int symbolColumn = mCursor.getColumnIndex(PortfolioContract.CurrencyData.COLUMN_SYMBOL);
                        mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.MAIN);
                    }
                });

                viewHolder.menuAdd.setOnClickListener(new ImageView.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position-1);
                        int symbolColumn = mCursor.getColumnIndex(PortfolioContract.CurrencyData.COLUMN_SYMBOL);
                        mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.ADD);
                    }
                });

                viewHolder.menuEdit.setOnClickListener(new ImageView.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position-1);
                        int symbolColumn = mCursor.getColumnIndex(PortfolioContract.CurrencyData.COLUMN_SYMBOL);
                        mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.EDIT);
                    }
                });

                viewHolder.menuSell.setOnClickListener(new ImageView.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position-1);
                        int symbolColumn = mCursor.getColumnIndex(PortfolioContract.CurrencyData.COLUMN_SYMBOL);
                        mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.SELL);
                    }
                });

                viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position-1);
                        int symbolColumn = mCursor.getColumnIndex(PortfolioContract.CurrencyData.COLUMN_SYMBOL);
                        mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.DELETE);
                    }
                });
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


    public interface CurrencyAdapterOnClickHandler {
        void onClick(String symbol, int id);
    }

    class CurrencyDataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.currencyQuantity)
        TextView currencyQuantity;

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.currencyAppreciation)
        TextView currencyAppreciation;

        @BindView(R.id.currentPercent)
        TextView currentPercent;

        @BindView(R.id.totalIncome)
        TextView totalIncome;

        @BindView(R.id.totalGain)
        TextView totalGain;

        @BindView(R.id.currencyAppreciationPercent)
        TextView currencyAppreciationPercent;

        @BindView(R.id.totalIncomePercent)
        TextView totalIncomePercent;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        @BindView(R.id.currencyCardViewClickable)
        LinearLayout currencyCardViewClickable;

        @BindView(R.id.menuAdd)
        ImageView menuAdd;

        @BindView(R.id.menuEdit)
        ImageView menuEdit;

        @BindView(R.id.menuSell)
        ImageView menuSell;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        public CurrencyDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class CurrencySummaryViewHolder extends RecyclerView.ViewHolder{

        public CurrencySummaryViewHolder(View itemView){
            super(itemView);
        }
    }
}
