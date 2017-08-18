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


public class FiiDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = FiiDetailAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private FiiAdapterOnClickHandler mClickHandler;

    public FiiDetailAdapter(Context context, FiiAdapterOnClickHandler clickHandler) {
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
            // If it is the first view, return viewholder for FiiDetails overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii_details_overview, parent, false);
                return new FiiDetailsOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii_details, parent, false);
                return new FiiDetailViewHolder(item);
            //item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii, parent, false);
            //return new FiiDataViewHolder(item);


        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        switch (holder.getItemViewType()) {
            case 0:
                FiiDetailsOverviewViewHolder viewOverviewHolder = (FiiDetailsOverviewViewHolder) holder;
                if (mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    // Get symbol to use on FiiData query
                    String symbol = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_SYMBOL));

                    Cursor dataCursor = getDataCursor(symbol);
                    Cursor soldDataCursor = getSoldDataCursor(symbol);

                    double soldPrice = 0;
                    double soldTotal = 0;
                    double gainTotal = 0;
                    double buyTotal = 0;
                    double quantity = 0;

                    // Check if there is any sold fiis first and add values
                    if (soldDataCursor.getCount() > 0){
                        soldDataCursor.moveToFirst();
                        soldPrice = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_SELL_MEDIUM_PRICE)));
                        soldTotal = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_SELL_TOTAL)));
                        buyTotal = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_BUY_VALUE_TOTAL)));
                        gainTotal = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_SELL_GAIN)));
                        quantity = soldDataCursor.getInt(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_QUANTITY_TOTAL)));
                    }

                    if (dataCursor.getCount() > 0) {
                        dataCursor.moveToFirst();
                        // Buy total is the sum of fii in data portfolio and already sold ones
                        buyTotal += dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_BUY_VALUE_TOTAL)));
                        quantity += dataCursor.getInt(
                                (dataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_QUANTITY_TOTAL)));
                        // Gain total is sum of gain from variation and sold fiis
                        gainTotal += dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_VARIATION)));

                        if (gainTotal >= 0){
                            viewOverviewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                            viewOverviewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                        } else {
                            viewOverviewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                            viewOverviewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                        }

                        viewOverviewHolder.currentPrice.setText(formatter.format(dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FiiData
                                        .COLUMN_CURRENT_PRICE)))));
                        viewOverviewHolder.mediumPrice.setText(formatter.format(dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_MEDIUM_PRICE)))));
                        viewOverviewHolder.currentTotal.setText(formatter.format(dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_CURRENT_TOTAL)))));
                        viewOverviewHolder.mediumTotal.setText(formatter.format(buyTotal));
                        viewOverviewHolder.totalGain.setText(formatter.format(gainTotal));
                        viewOverviewHolder.soldPrice.setText(formatter.format(soldPrice));
                        viewOverviewHolder.soldTotal.setText(formatter.format(soldTotal));
                    } else{
                    }
                }
                break;
            default:
                FiiDetailViewHolder viewHolder = (FiiDetailViewHolder) holder;
                mCursor.moveToPosition(position-1);
                // TODO: Below values are stored in DB as REALs.
                // We'll need to format them to currency number format.
                String type = getDetailType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_TYPE)));

                Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP));
                String date = TimestampToDate(timestamp);
                int quantity = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                double price = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                // If price is 0, then it is bonification, grouping or split which should not show price or totalValue
                if (price > 0) {
                    String totalValue = formatter.format(price * quantity);
                    String priceText = formatter.format(price);
                    viewHolder.price.setText(priceText);
                    viewHolder.totalValue.setText(totalValue);
                }
                String quantityText = String.valueOf(quantity);

                viewHolder.transactionType.setText(type);
                viewHolder.transactionDate.setText(date);
                viewHolder.quantity.setText(quantityText);

                viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        String id = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.FiiTransaction._ID));
                        mClickHandler.onClick(id, Constants.AdapterClickable.DELETE);
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

    public interface FiiAdapterOnClickHandler {
        void onClick(String id, int type);
    }

    class FiiDetailViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.transactionType)
        TextView transactionType;

        @BindView(R.id.transactionDate)
        TextView transactionDate;

        @BindView(R.id.quantity)
        TextView quantity;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.totalValue)
        TextView totalValue;

        @BindView(R.id.fiiCardViewClickable)
        LinearLayout fiiCardViewClickable;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        FiiDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FiiDetailsOverviewViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.currentPrice)
        TextView currentPrice;

        @BindView(R.id.mediumPrice)
        TextView mediumPrice;

        @BindView(R.id.soldPrice)
        TextView soldPrice;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.buyTotal)
        TextView mediumTotal;

        @BindView(R.id.soldTotal)
        TextView soldTotal;

        @BindView(R.id.totalGain)
        TextView totalGain;

        public FiiDetailsOverviewViewHolder(View itemView){
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
