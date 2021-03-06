package br.com.guiainvestimento.adapter.currency;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CurrencyMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = CurrencyMainAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private CurrencyAdapterOnClickHandler mClickHandler;

    public CurrencyMainAdapter(Context context, CurrencyAdapterOnClickHandler clickHandler) {
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
            // If it is the first view, return viewholder for FiiIncome overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii_income_overview, parent, false);
                return new CurrencyOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii_incomes_main, parent, false);
                return new CurrencyIncomeViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        switch (holder.getItemViewType()) {
            case 0:
                CurrencyOverviewViewHolder overviewViewHolder = (CurrencyOverviewViewHolder) holder;
                if (mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    overviewViewHolder.itemView.setVisibility(View.VISIBLE);
                    Cursor dataCursor = getDataCursor();
                    Cursor soldDataCursor = getSoldDataCursor();

                    double buyTotal = 0;
                    double tax = 0;
                    double netIncome = 0;
                    double grossIncome = 0;
                    double netPercent = 0;
                    double grossPercent = 0;
                    double taxPercent = 0;

                    // Check if there is any sold fiis first and add values
                    if (soldDataCursor.getCount() > 0){
                        soldDataCursor.moveToFirst();
                        do {
                            buyTotal += soldDataCursor.getDouble(
                                    (soldDataCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_BUY_VALUE_TOTAL)));
                        } while (soldDataCursor.moveToNext());
                    }

                    if (dataCursor.getCount() > 0) {
                        dataCursor.moveToFirst();
                        do {
                            // Buy total is the sum of fii in data portfolio and already sold ones
                            buyTotal += dataCursor.getDouble(
                                    (dataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_BUY_VALUE_TOTAL)));
                            tax += dataCursor.getDouble(
                                    (dataCursor.getColumnIndex(PortfolioContract.FiiData
                                            .COLUMN_INCOME_TAX)));
                            netIncome += dataCursor.getDouble(
                                    (dataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_INCOME)));
                        } while (dataCursor.moveToNext());
                        grossIncome = netIncome + tax;
                        netPercent = netIncome/buyTotal*100;
                        grossPercent = grossIncome/buyTotal*100;
                        taxPercent = tax/buyTotal*100;

                        if (grossIncome >= 0){
                            overviewViewHolder.grossIncome.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                            overviewViewHolder.grossIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                        } else {
                            overviewViewHolder.grossIncome.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                            overviewViewHolder.grossIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                        }

                        if (netIncome >= 0){
                            overviewViewHolder.netIncome.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                            overviewViewHolder.netIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                        } else {
                            overviewViewHolder.netIncome.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                            overviewViewHolder.netIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                        }

                        if (tax >= 0){
                            overviewViewHolder.taxIncome.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                            overviewViewHolder.taxIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                        } else {
                            overviewViewHolder.taxIncome.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                            overviewViewHolder.taxIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                        }

                        overviewViewHolder.boughtTotal.setText(formatter.format(buyTotal));
                        overviewViewHolder.grossIncome.setText(formatter.format(grossIncome));
                        overviewViewHolder.taxIncome.setText(formatter.format(tax));
                        overviewViewHolder.netIncome.setText(formatter.format(netIncome));
                        overviewViewHolder.grossIncomePercent.setText("(" + String.format("%.2f",grossPercent)+"%)");
                        overviewViewHolder.taxIncomePercent.setText("(" + String.format("%.2f",taxPercent)+"%)");
                        overviewViewHolder.netIncomePercent.setText("(" + String.format("%.2f",netPercent)+"%)");
                    } else{
                    }
                } else {
                    overviewViewHolder.itemView.setVisibility(View.GONE);
                }
                break;
            default:
                CurrencyIncomeViewHolder viewHolder = (CurrencyIncomeViewHolder) holder;
                mCursor.moveToPosition(position-1);
                // TODO: Below values are stored in DB as REALs.
                // We'll need to format them to currency number format.
                Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                String incomeType = getIncomeType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_TYPE)));
                String date = TimestampToDate(timestamp);
                viewHolder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_SYMBOL)));
                viewHolder.incomeType.setText(incomeType);
                viewHolder.incomeValue.setText(formatter.format(mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID))));
                viewHolder.incomeDate.setText(date);
                break;
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
        void onClick(String symbol, int type);
        void onCreateContextMenu(ContextMenu menu, View v,
                                 ContextMenu.ContextMenuInfo menuInfo, String id, int type);
    }

    class CurrencyIncomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.incomeType)
        TextView incomeType;

        @BindView(R.id.incomeDate)
        TextView incomeDate;

        @BindView(R.id.incomeValue)
        TextView incomeValue;


        CurrencyIncomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition-1);
            int idColumn = mCursor.getColumnIndex(PortfolioContract.FiiIncome._ID);
            int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_TYPE));
            mClickHandler.onClick(mCursor.getString(idColumn), type);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                           ContextMenu.ContextMenuInfo menuInfo){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition-1);
            int idColumn = mCursor.getColumnIndex(PortfolioContract.FiiIncome._ID);
            int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_TYPE));
            mClickHandler.onCreateContextMenu(menu, v , menuInfo, mCursor.getString(idColumn), type);
        }
    }

    class CurrencyOverviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.grossIncome)
        TextView grossIncome;

        @BindView(R.id.taxIncome)
        TextView taxIncome;

        @BindView(R.id.netIncome)
        TextView netIncome;

        @BindView(R.id.grossIncomePercent)
        TextView grossIncomePercent;

        @BindView(R.id.taxIncomePercent)
        TextView taxIncomePercent;

        @BindView(R.id.netIncomePercent)
        TextView netIncomePercent;


        CurrencyOverviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public String TimestampToDate(Long timestamp){
        String date = DateFormat.format("dd/MM/yyyy", timestamp).toString();
        return date;
    }

    public String getIncomeType(int incomeTypeId){
        switch (incomeTypeId){
            case Constants.IncomeType.INVALID:
                return "invalid";
            case Constants.IncomeType.FII:
                return mContext.getResources().getString(R.string.fii_income_type);
            default:
                return mContext.getResources().getString(R.string.fii_income_type);
        }
    }

    private Cursor getDataCursor(){
        // Searches for existing FiiData
        return mContext.getContentResolver().query(
                PortfolioContract.FiiData.URI,
                null, null, null, null);
    }

    private Cursor getSoldDataCursor(){
        // Searches for existing SoldFiiData
        return mContext.getContentResolver().query(
                PortfolioContract.SoldFiiData.URI,
                null, null, null, null);
    }
}
