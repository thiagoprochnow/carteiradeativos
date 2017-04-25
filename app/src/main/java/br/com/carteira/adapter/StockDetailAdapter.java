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


public class StockDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = StockDetailAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private StockAdapterOnClickHandler mClickHandler;

    public StockDetailAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
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
            // If it is the first view, return viewholder for StockDetails overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock_details_overview, parent, false);
                return new StockDetailsOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock_details, parent, false);
                return new StockDetailViewHolder(item);
            //item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock, parent, false);
            //return new StockDataViewHolder(item);


        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        switch (holder.getItemViewType()) {
            case 0:
                StockDetailsOverviewViewHolder viewOverviewHolder = (StockDetailsOverviewViewHolder) holder;
                if (mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    // Get symbol to use on StockData query
                    String symbol = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_SYMBOL));

                    Cursor dataCursor = getDataCursor(symbol);
                    Cursor soldDataCursor = getSoldDataCursor(symbol);

                    double soldPrice = 0;
                    double soldTotal = 0;
                    double gainTotal = 0;
                    double buyTotal = 0;
                    double quantity = 0;

                    // Check if there is any sold stocks first and add values
                    if (soldDataCursor.getCount() > 0){
                        soldDataCursor.moveToFirst();
                        soldPrice = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SELL_MEDIUM_PRICE)));
                        soldTotal = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SELL_TOTAL)));
                        buyTotal = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_BUY_VALUE_TOTAL)));
                        gainTotal = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SELL_GAIN)));
                        quantity = soldDataCursor.getInt(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_QUANTITY_TOTAL)));
                    }

                    if (dataCursor.getCount() > 0) {
                        dataCursor.moveToFirst();
                        // Buy total is the sum of stock in data portfolio and already sold ones
                        buyTotal += dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL)));
                        quantity += dataCursor.getInt(
                                (dataCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL)));
                        // Gain total is sum of gain from variation and sold stocks
                        gainTotal += dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_VARIATION)));

                        viewOverviewHolder.currentPrice.setText(formatter.format(dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.StockData
                                        .COLUMN_CURRENT_PRICE)))));
                        viewOverviewHolder.mediumPrice.setText(formatter.format(dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_MEDIUM_PRICE)))));
                        viewOverviewHolder.currentTotal.setText(formatter.format(dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_CURRENT_TOTAL)))));
                        viewOverviewHolder.mediumTotal.setText(formatter.format(buyTotal));
                        viewOverviewHolder.totalGain.setText(formatter.format(gainTotal));
                        viewOverviewHolder.soldPrice.setText(formatter.format(soldPrice));
                        viewOverviewHolder.soldTotal.setText(formatter.format(soldTotal));
                    } else{
                        Log.d(LOG_TAG, "No Stock Data found for symbol: " + symbol);
                    }
                }
                break;
            default:
                StockDetailViewHolder viewHolder = (StockDetailViewHolder) holder;
                mCursor.moveToPosition(position-1);
                // TODO: Below values are stored in DB as REALs.
                // We'll need to format them to currency number format.
                String type = getDetailType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE)));

                Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TIMESTAMP));
                String date = TimestampToDate(timestamp);
                int quantity = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                double price = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
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
        void onCreateContextMenu(ContextMenu menu, View v,
                                 ContextMenu.ContextMenuInfo menuInfo, String id, int type);
    }

    class StockDetailViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

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


        StockDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition - 1);
            int idColumn = mCursor.getColumnIndex(PortfolioContract.StockTransaction._ID);
            int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE));
            mClickHandler.onCreateContextMenu(menu, v , menuInfo, mCursor.getString(idColumn), type);
        }
    }

    class StockDetailsOverviewViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.currentPrice)
        TextView currentPrice;

        @BindView(R.id.mediumPrice)
        TextView mediumPrice;

        @BindView(R.id.soldPrice)
        TextView soldPrice;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.mediumTotal)
        TextView mediumTotal;

        @BindView(R.id.soldTotal)
        TextView soldTotal;

        @BindView(R.id.totalGain)
        TextView totalGain;

        public StockDetailsOverviewViewHolder(View itemView){
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
                Log.d(LOG_TAG, "Invalid Transaction Type");
                return "invalid";
            case Constants.Type.BUY:
                Log.d(LOG_TAG, "Buy Transaction Type");
                return mContext.getResources().getString(R.string.stock_buy);
            case Constants.Type.SELL:
                Log.d(LOG_TAG, "Sell Transaction Type");
                return mContext.getResources().getString(R.string.stock_sell);
            case Constants.Type.BONIFICATION:
                Log.d(LOG_TAG, "Bonification Transaction Type");
                return mContext.getResources().getString(R.string.stock_bonification);
            case Constants.Type.SPLIT:
                Log.d(LOG_TAG, "Split Transaction Type");
                return mContext.getResources().getString(R.string.stock_split);
            case Constants.Type.GROUPING:
                Log.d(LOG_TAG, "Grouping Transaction Type");
                return mContext.getResources().getString(R.string.stock_grouping);
            default:
                Log.d(LOG_TAG, "Default Transaction Type");
                return mContext.getResources().getString(R.string.stock_buy);
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
