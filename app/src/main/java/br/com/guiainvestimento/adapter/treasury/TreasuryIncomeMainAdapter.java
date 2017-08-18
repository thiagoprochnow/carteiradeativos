package br.com.guiainvestimento.adapter.treasury;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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


public class TreasuryIncomeMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = TreasuryIncomeMainAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private TreasuryAdapterOnClickHandler mClickHandler;

    public TreasuryIncomeMainAdapter(Context context, TreasuryAdapterOnClickHandler clickHandler) {
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
            // If it is the first view, return viewholder for TreasuryIncome overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_treasury_income_overview, parent, false);
                return new TreasuryIncomeOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_treasury_incomes_main, parent, false);
                return new TreasuryIncomeViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        switch (holder.getItemViewType()) {
            case 0:
                TreasuryIncomeOverviewViewHolder overviewViewHolder = (TreasuryIncomeOverviewViewHolder) holder;
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

                    // Check if there is any sold treasury first and add values
                    if (soldDataCursor.getCount() > 0){
                        soldDataCursor.moveToFirst();
                        do {
                            buyTotal += soldDataCursor.getDouble(
                                    (soldDataCursor.getColumnIndex(PortfolioContract.SoldTreasuryData.COLUMN_BUY_VALUE_TOTAL)));
                        } while (soldDataCursor.moveToNext());
                    }

                    if (dataCursor.getCount() > 0) {
                        dataCursor.moveToFirst();
                        do {
                            // Buy total is the sum of treasury in data portfolio and already sold ones
                            buyTotal += dataCursor.getDouble(
                                    (dataCursor.getColumnIndex(PortfolioContract.TreasuryData.COLUMN_BUY_VALUE_TOTAL)));
                            tax += dataCursor.getDouble(
                                    (dataCursor.getColumnIndex(PortfolioContract.TreasuryData
                                            .COLUMN_INCOME_TAX)));
                            netIncome += dataCursor.getDouble(
                                    (dataCursor.getColumnIndex(PortfolioContract.TreasuryData.COLUMN_INCOME)));
                        } while (dataCursor.moveToNext());
                        grossIncome = netIncome + tax;
                        grossPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(grossIncome/buyTotal*100)));
                        taxPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(tax/buyTotal*100)));
                        netPercent = grossPercent - taxPercent;

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
                        Log.d(LOG_TAG, "(Income) No Treasury Data found");
                    }
                } else {
                    overviewViewHolder.itemView.setVisibility(View.GONE);
                }
                break;
            default:
                TreasuryIncomeViewHolder viewHolder = (TreasuryIncomeViewHolder) holder;
                mCursor.moveToPosition(position-1);
                // TODO: Below values are stored in DB as REALs.
                // We'll need to format them to currency number format.
                Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.TreasuryIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                String incomeType = getIncomeType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.TreasuryIncome.COLUMN_TYPE)));
                String date = TimestampToDate(timestamp);
                viewHolder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract.TreasuryIncome.COLUMN_SYMBOL)));
                viewHolder.incomeType.setText(incomeType);
                viewHolder.incomeValue.setText(formatter.format(mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.TreasuryIncome.COLUMN_RECEIVE_LIQUID))));
                viewHolder.incomeDate.setText(date);
                if(position == mCursor.getCount()){
                    // If last item, apply margin in bottom to keep empty space for Floating button to occupy.
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    int leftDp = 10; // margin in dips
                    int rightDp = 10; // margin in dips
                    int bottomDp = 85; // margin in dips
                    float d = mContext.getResources().getDisplayMetrics().density;
                    int leftMargin = (int)(leftDp * d); // margin in pixels
                    int rightMargin = (int)(rightDp * d); // margin in pixels
                    int bottomMargin = (int)(bottomDp * d); // margin in pixels
                    params.setMargins(leftMargin, 0, rightMargin, bottomMargin);
                    viewHolder.treasuryCardView.setLayoutParams(params);
                }

                viewHolder.treasuryIncomeViewClickable.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.TreasuryIncome.COLUMN_TYPE));
                        int id = mCursor.getColumnIndex(PortfolioContract.TreasuryIncome._ID);
                        mClickHandler.onClick(mCursor.getString(id), type, Constants.AdapterClickable.MAIN);
                    }
                });

                viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.TreasuryIncome.COLUMN_TYPE));
                        int id = mCursor.getColumnIndex(PortfolioContract.TreasuryIncome._ID);
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


    public interface TreasuryAdapterOnClickHandler {
        void onClick(String id, int type, int operation);
    }

    class TreasuryIncomeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.treasury_card_view)
        CardView treasuryCardView;

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.incomeType)
        TextView incomeType;

        @BindView(R.id.incomeDate)
        TextView incomeDate;

        @BindView(R.id.incomeValue)
        TextView incomeValue;

        @BindView(R.id.treasuryIncomeViewClickable)
        LinearLayout treasuryIncomeViewClickable;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        TreasuryIncomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TreasuryIncomeOverviewViewHolder extends RecyclerView.ViewHolder {

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


        TreasuryIncomeOverviewViewHolder(View itemView) {
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
            case Constants.IncomeType.TREASURY:
                Log.d(LOG_TAG, "Income IncomeType");
                return mContext.getResources().getString(R.string.treasury_income_type);
            default:
                Log.d(LOG_TAG, "Default IncomeType");
                return mContext.getResources().getString(R.string.treasury_income_type);
        }
    }

    private Cursor getDataCursor(){
        // Searches for existing TreasuryData
        return mContext.getContentResolver().query(
                PortfolioContract.TreasuryData.URI,
                null, null, null, null);
    }

    private Cursor getSoldDataCursor(){
        // Searches for existing SoldTreasuryData
        return mContext.getContentResolver().query(
                PortfolioContract.SoldTreasuryData.URI,
                null, null, null, null);
    }
}
