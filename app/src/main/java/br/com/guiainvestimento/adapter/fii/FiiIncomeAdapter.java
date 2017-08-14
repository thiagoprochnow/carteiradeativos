package br.com.guiainvestimento.adapter.fii;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class FiiIncomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = FiiIncomeAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private FiiAdapterOnClickHandler mClickHandler;

    public FiiIncomeAdapter(Context context, FiiAdapterOnClickHandler clickHandler) {
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
                return new FiiIncomeOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii_incomes, parent, false);
                return new FiiIncomeViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        switch (holder.getItemViewType()) {
            case 0:
                FiiIncomeOverviewViewHolder overviewViewHolder = (FiiIncomeOverviewViewHolder) holder;
                if (mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    overviewViewHolder.itemView.setVisibility(View.VISIBLE);
                    // Get symbol to use on FiiIncome query
                    String symbol = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_SYMBOL));

                    Cursor dataCursor = getDataCursor(symbol);
                    Cursor soldDataCursor = getSoldDataCursor(symbol);

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
                        buyTotal = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_BUY_VALUE_TOTAL)));
                    }

                    if (dataCursor.getCount() > 0) {
                        dataCursor.moveToFirst();
                        // Buy total is the sum of fii in data portfolio and already sold ones
                        buyTotal += dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_BUY_VALUE_TOTAL)));
                        tax = dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_INCOME_TAX)));
                        netIncome= dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_INCOME)));
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
                        Log.d(LOG_TAG, "(Income) No Fii Data found for symbol: " + symbol);
                    }
                } else {
                    overviewViewHolder.itemView.setVisibility(View.GONE);
                }
                break;
            default:
                FiiIncomeViewHolder viewHolder = (FiiIncomeViewHolder) holder;
                mCursor.moveToPosition(position-1);
                // TODO: Below values are stored in DB as REALs.
                // We'll need to format them to currency number format.
                Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                String incomeType = getIncomeType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_TYPE)));
                String date = TimestampToDate(timestamp);
                Log.d(LOG_TAG, "IncomeType: " + incomeType);
                Log.d(LOG_TAG, "IncomeValue: " + formatter.format(mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID))));
                Log.d(LOG_TAG, "Date: " + date);
                viewHolder.incomeType.setText(incomeType);
                viewHolder.incomeValue.setText(formatter.format(mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID))));
                viewHolder.incomeDate.setText(date);

                viewHolder.fiiIncomeViewClickable.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_TYPE));
                        int id = mCursor.getColumnIndex(PortfolioContract.FiiIncome._ID);
                        mClickHandler.onClick(mCursor.getString(id), type, Constants.AdapterClickable.MAIN);
                    }
                });

                viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_TYPE));
                        int id = mCursor.getColumnIndex(PortfolioContract.FiiIncome._ID);
                        mClickHandler.onClick(mCursor.getString(id), type, Constants.AdapterClickable.DELETE);
                    }
                });

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


    public interface FiiAdapterOnClickHandler {
        void onClick(String id, int type, int operation);
    }

    class FiiIncomeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.incomeType)
        TextView incomeType;

        @BindView(R.id.incomeDate)
        TextView incomeDate;

        @BindView(R.id.incomeValue)
        TextView incomeValue;

        @BindView(R.id.fiiIncomeViewClickable)
        LinearLayout fiiIncomeViewClickable;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        FiiIncomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FiiIncomeOverviewViewHolder extends RecyclerView.ViewHolder {

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


        FiiIncomeOverviewViewHolder(View itemView) {
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
                Log.d(LOG_TAG, "Income IncomeType");
                return mContext.getResources().getString(R.string.fii_income_type);
            default:
                Log.d(LOG_TAG, "Default IncomeType");
                return mContext.getResources().getString(R.string.fii_income_type);
        }
    }

    private Cursor getDataCursor(String symbol){
        String selection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        // Searches for existing FiiData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.FiiData.URI,
                null, selection, selectionArguments, null);
    }

    private Cursor getSoldDataCursor(String symbol){
        String selection = PortfolioContract.SoldFiiData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        // Searches for existing FiiData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.SoldFiiData.URI,
                null, selection, selectionArguments, null);
    }
}
