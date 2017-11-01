package br.com.guiainvestimento.adapter.fixedincome;

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


public class FixedDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = FixedDetailAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private FixedAdapterOnClickHandler mClickHandler;

    public FixedDetailAdapter(Context context, FixedAdapterOnClickHandler clickHandler) {
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
            // If it is the first view, return viewholder for FixedDetails overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fixed_details_overview, parent, false);
                return new FixedDetailsOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fixed_details, parent, false);
                return new FixedDetailViewHolder(item);
            //item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fixed, parent, false);
            //return new FixedDataViewHolder(item);


        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        switch (holder.getItemViewType()) {
            case 0:
                FixedDetailsOverviewViewHolder viewOverviewHolder = (FixedDetailsOverviewViewHolder) holder;
                if (mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    // Get symbol to use on FixedData query
                    String symbol = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_SYMBOL));

                    Cursor dataCursor = getDataCursor(symbol);

                    double gainTotal = 0;
                    double buyTotal = 0;
                    double soldTotal = 0;

                    if (dataCursor.getCount() > 0) {
                        dataCursor.moveToFirst();
                        // Buy total is the sum of fixed income in data portfolio and already sold ones
                        buyTotal = dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_BUY_VALUE_TOTAL)));
                        soldTotal = dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SELL_VALUE_TOTAL)));
                        // Gain total is sum of gain from variation and sold fixed income
                        gainTotal = dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_TOTAL_GAIN)));

                        if (gainTotal >= 0){
                            viewOverviewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                            viewOverviewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                        } else {
                            viewOverviewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                            viewOverviewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                        }

                        viewOverviewHolder.currentTotal.setText(formatter.format(dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_CURRENT_TOTAL)))));
                        viewOverviewHolder.buyTotal.setText(formatter.format(buyTotal));
                        viewOverviewHolder.soldTotal.setText(formatter.format(soldTotal));
                        viewOverviewHolder.totalGain.setText(formatter.format(gainTotal));
                    } else{
                    }
                }
                break;
            default:
                FixedDetailViewHolder viewHolder = (FixedDetailViewHolder) holder;
                mCursor.moveToPosition(position-1);
                // TODO: Below values are stored in DB as REALs.
                // We'll need to format them to currency number format.
                String type = getDetailType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TYPE)));

                Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TIMESTAMP));
                String date = TimestampToDate(timestamp);
                String totalValue = formatter.format(mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TOTAL)));
                viewHolder.totalValue.setText(totalValue);
                viewHolder.transactionDate.setText(date);
                viewHolder.transactionType.setText(type);

                viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        String id = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.FixedTransaction._ID));
                        mClickHandler.onClick(id, Constants.AdapterClickable.DELETE);
                    }
                });

                viewHolder.menuEdit.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        String id = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.StockTransaction._ID));
                        mClickHandler.onClick(id, Constants.AdapterClickable.EDIT);
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

    public interface FixedAdapterOnClickHandler {
        void onClick(String id, int type);
    }

    class FixedDetailViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.transactionType)
        TextView transactionType;

        @BindView(R.id.transactionDate)
        TextView transactionDate;

        @BindView(R.id.totalValue)
        TextView totalValue;

        @BindView(R.id.fixedCardViewClickable)
        LinearLayout fixedCardViewClickable;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        @BindView(R.id.menuEdit)
        ImageView menuEdit;

        FixedDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FixedDetailsOverviewViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.buyTotal)
        TextView buyTotal;

        @BindView(R.id.soldTotal)
        TextView soldTotal;

        @BindView(R.id.totalGain)
        TextView totalGain;

        public FixedDetailsOverviewViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public String TimestampToDate(Long timestamp){
        String date = DateFormat.format("dd/MM/yyyy", timestamp).toString();
        return date;
    }

    public String getDetailType(int typeId){
        switch (typeId){
            case Constants.Type.INVALID:
                return "invalid";
            case Constants.Type.BUY:
                return mContext.getResources().getString(R.string.stock_buy);
            case Constants.Type.SELL:
                return mContext.getResources().getString(R.string.stock_sell);
            default:
                return mContext.getResources().getString(R.string.stock_buy);
        }
    }

    private Cursor getDataCursor(String symbol){
        String selection = PortfolioContract.FixedData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        // Searches for existing FixedData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.FixedData.URI,
                null, selection, selectionArguments, null);
    }
}
