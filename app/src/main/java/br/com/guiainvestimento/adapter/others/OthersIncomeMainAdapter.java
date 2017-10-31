package br.com.guiainvestimento.adapter.others;

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


public class OthersIncomeMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = OthersIncomeMainAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private OthersAdapterOnClickHandler mClickHandler;

    public OthersIncomeMainAdapter(Context context, OthersAdapterOnClickHandler clickHandler) {
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
            // If it is the first view, return viewholder for OthersIncome overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_others_income_overview, parent, false);
                return new OthersIncomeOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_others_incomes_main, parent, false);
                return new OthersIncomeViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        switch (holder.getItemViewType()) {
            case 0:
                OthersIncomeOverviewViewHolder overviewViewHolder = (OthersIncomeOverviewViewHolder) holder;
                if (mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    overviewViewHolder.itemView.setVisibility(View.VISIBLE);
                    Cursor dataCursor = getDataCursor();

                    double buyTotal = 0;
                    double tax = 0;
                    double netIncome = 0;
                    double grossIncome = 0;
                    double netPercent = 0;
                    double grossPercent = 0;
                    double taxPercent = 0;

                    if (dataCursor.getCount() > 0) {
                        dataCursor.moveToFirst();
                        do {
                            // Buy total is the sum of others in data portfolio and already sold ones
                            buyTotal += dataCursor.getDouble(
                                    (dataCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_BUY_VALUE_TOTAL)));
                            tax += dataCursor.getDouble(
                                    (dataCursor.getColumnIndex(PortfolioContract.OthersData
                                            .COLUMN_INCOME_TAX)));
                            netIncome += dataCursor.getDouble(
                                    (dataCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_INCOME)));
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
                    }
                } else {
                    overviewViewHolder.itemView.setVisibility(View.GONE);
                }
                break;
            default:
                OthersIncomeViewHolder viewHolder = (OthersIncomeViewHolder) holder;
                mCursor.moveToPosition(position-1);
                // TODO: Below values are stored in DB as REALs.
                // We'll need to format them to currency number format.
                Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                String incomeType = getIncomeType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_TYPE)));
                String date = TimestampToDate(timestamp);
                double receiveTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL));
                double receiveTax = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_TAX));
                viewHolder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_SYMBOL)));
                viewHolder.incomeType.setText(incomeType);
                viewHolder.incomeValue.setText(formatter.format(receiveTotal-receiveTax));
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
                    viewHolder.othersCardView.setLayoutParams(params);
                }

                viewHolder.othersIncomeViewClickable.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_TYPE));
                        int id = mCursor.getColumnIndex(PortfolioContract.OthersIncome._ID);
                        mClickHandler.onClick(mCursor.getString(id), type, Constants.AdapterClickable.MAIN);
                    }
                });

                viewHolder.menuEdit.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_TYPE));
                        int id = mCursor.getColumnIndex(PortfolioContract.OthersIncome._ID);
                        mClickHandler.onClick(mCursor.getString(id), type, Constants.AdapterClickable.EDIT);
                    }
                });

                viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_TYPE));
                        int id = mCursor.getColumnIndex(PortfolioContract.OthersIncome._ID);
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


    public interface OthersAdapterOnClickHandler {
        void onClick(String id, int type, int operation);
    }

    class OthersIncomeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.others_card_view)
        CardView othersCardView;

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.incomeType)
        TextView incomeType;

        @BindView(R.id.incomeDate)
        TextView incomeDate;

        @BindView(R.id.incomeValue)
        TextView incomeValue;

        @BindView(R.id.othersIncomeViewClickable)
        LinearLayout othersIncomeViewClickable;

        @BindView(R.id.menuEdit)
        ImageView menuEdit;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        OthersIncomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class OthersIncomeOverviewViewHolder extends RecyclerView.ViewHolder {

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


        OthersIncomeOverviewViewHolder(View itemView) {
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
            case Constants.IncomeType.OTHERS:
                return mContext.getResources().getString(R.string.others_income_type);
            default:
                return mContext.getResources().getString(R.string.others_income_type);
        }
    }

    private Cursor getDataCursor(){
        // Searches for existing OthersData
        return mContext.getContentResolver().query(
                PortfolioContract.OthersData.URI,
                null, null, null, null);
    }

}
