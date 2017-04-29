package br.com.carteira.adapter;

import android.content.Context;
import android.database.Cursor;
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

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StockIncomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = StockIncomeAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private StockAdapterOnClickHandler mClickHandler;

    public StockIncomeAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
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
            // If it is the first view, return viewholder for StockIncome overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock_income_overview, parent, false);
                return new StockIncomeOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock_incomes, parent, false);
                return new StockIncomeViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        switch (holder.getItemViewType()) {
            case 0:
                StockIncomeOverviewViewHolder overviewViewHolder = (StockIncomeOverviewViewHolder) holder;
                if (mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    // Get symbol to use on StockIncome query
                    String symbol = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_SYMBOL));

                    Cursor dataCursor = getDataCursor(symbol);
                    Cursor soldDataCursor = getSoldDataCursor(symbol);

                    double buyTotal = 0;
                    double tax = 0;
                    double netIncome = 0;
                    double grossIncome = 0;
                    double netPercent = 0;
                    double grossPercent = 0;
                    double taxPercent = 0;

                    // Check if there is any sold stocks first and add values
                    if (soldDataCursor.getCount() > 0){
                        soldDataCursor.moveToFirst();
                        buyTotal = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_BUY_VALUE_TOTAL)));
                    }

                    if (dataCursor.getCount() > 0) {
                        dataCursor.moveToFirst();
                        // Buy total is the sum of stock in data portfolio and already sold ones
                        buyTotal += dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL)));
                        tax = dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_INCOME_TAX)));
                        netIncome= dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_NET_INCOME)));
                        grossIncome = netIncome + tax;
                        netPercent = netIncome/buyTotal*100;
                        grossPercent = grossIncome/buyTotal*100;
                        taxPercent = tax/buyTotal*100;
                        overviewViewHolder.boughtTotal.setText(formatter.format(buyTotal));
                        overviewViewHolder.grossIncome.setText(formatter.format(grossIncome));
                        overviewViewHolder.taxIncome.setText(formatter.format(tax));
                        overviewViewHolder.netIncome.setText(formatter.format(netIncome));
                        overviewViewHolder.grossIncomePercent.setText("(" + String.format("%.2f",grossPercent)+"%)");
                        overviewViewHolder.taxIncomePercent.setText("(" + String.format("%.2f",taxPercent)+"%)");
                        overviewViewHolder.netIncomePercent.setText("(" + String.format("%.2f",netPercent)+"%)");
                    } else{
                        Log.d(LOG_TAG, "(Income) No Stock Data found for symbol: " + symbol);
                    }
                }
                break;
            default:
                StockIncomeViewHolder viewHolder = (StockIncomeViewHolder) holder;
                mCursor.moveToPosition(position-1);
                // TODO: Below values are stored in DB as REALs.
                // We'll need to format them to currency number format.
                Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                String incomeType = getIncomeType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TYPE)));
                String date = TimestampToDate(timestamp);
                Log.d(LOG_TAG, "IncomeType: " + incomeType);
                Log.d(LOG_TAG, "IncomeValue: " + formatter.format(mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID))));
                Log.d(LOG_TAG, "Date: " + date);
                viewHolder.incomeType.setText(incomeType);
                viewHolder.incomeValue.setText(formatter.format(mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID))));
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


    public interface StockAdapterOnClickHandler {
        void onClick(String symbol, int type);
        void onCreateContextMenu(ContextMenu menu, View v,
                                 ContextMenu.ContextMenuInfo menuInfo, String id, int type);
    }

    class StockIncomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

        @BindView(R.id.incomeType)
        TextView incomeType;

        @BindView(R.id.incomeDate)
        TextView incomeDate;

        @BindView(R.id.incomeValue)
        TextView incomeValue;


        StockIncomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition-1);
            int idColumn = mCursor.getColumnIndex(PortfolioContract.StockIncome._ID);
            int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TYPE));
            mClickHandler.onClick(mCursor.getString(idColumn), type);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                           ContextMenu.ContextMenuInfo menuInfo){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition-1);
            int idColumn = mCursor.getColumnIndex(PortfolioContract.StockIncome._ID);
            int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TYPE));
            mClickHandler.onCreateContextMenu(menu, v , menuInfo, mCursor.getString(idColumn), type);
        }
    }

    class StockIncomeOverviewViewHolder extends RecyclerView.ViewHolder {

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


        StockIncomeOverviewViewHolder(View itemView) {
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
                Log.d(LOG_TAG, "Invalid IncomeType");
                return "invalid";
            case Constants.IncomeType.DIVIDEND:
                Log.d(LOG_TAG, "Dividend IncomeType");
                return mContext.getResources().getString(R.string.dividend_income_type);
            case Constants.IncomeType.JCP:
                Log.d(LOG_TAG, "JCP IncomeType");
                return mContext.getResources().getString(R.string.jcp_income_type);
            default:
                Log.d(LOG_TAG, "Default IncomeType");
                return mContext.getResources().getString(R.string.dividend_income_type);
        }
    }

    private Cursor getDataCursor(String symbol){
        String selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        // Searches for existing StockData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                null, selection, selectionArguments, null);
    }

    private Cursor getSoldDataCursor(String symbol){
        String selection = PortfolioContract.SoldStockData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        // Searches for existing StockData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.SoldStockData.URI,
                null, selection, selectionArguments, null);
    }
}
