package br.com.guiainvestimento.adapter.currency;

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


public class CurrencyDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = CurrencyDetailAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private CurrencyAdapterOnClickHandler mClickHandler;

    public CurrencyDetailAdapter(Context context, CurrencyAdapterOnClickHandler clickHandler) {
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
            // If it is the first view, return viewholder for CurrencyDetails overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_currency_details_overview, parent, false);
                return new CurrencyDetailsOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_currency_details, parent, false);
                return new CurrencyDetailViewHolder(item);
            //item = LayoutInflater.from(mContext).inflate(R.layout.adapter_currency, parent, false);
            //return new CurrencyDataViewHolder(item);


        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        switch (holder.getItemViewType()) {
            case 0:
                CurrencyDetailsOverviewViewHolder viewOverviewHolder = (CurrencyDetailsOverviewViewHolder) holder;
                if (mCursor.getCount() > 0) {
                    mCursor.moveToFirst();
                    // Get symbol to use on CurrencyData query
                    String symbol = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL));

                    Cursor dataCursor = getDataCursor(symbol);
                    Cursor soldDataCursor = getSoldDataCursor(symbol);

                    double soldPrice = 0;

                    // Check if there is any sold currency first and add values
                    if (soldDataCursor.getCount() > 0){
                        soldDataCursor.moveToFirst();
                        soldPrice = soldDataCursor.getDouble(
                                (soldDataCursor.getColumnIndex(PortfolioContract.SoldCurrencyData.COLUMN_SELL_MEDIUM_PRICE)));
                    }

                    if (dataCursor.getCount() > 0) {
                        dataCursor.moveToFirst();

                        viewOverviewHolder.currentPrice.setText(formatter.format(dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.CurrencyData
                                        .COLUMN_CURRENT_PRICE)))));
                        viewOverviewHolder.mediumPrice.setText(formatter.format(dataCursor.getDouble(
                                (dataCursor.getColumnIndex(PortfolioContract.CurrencyData.COLUMN_MEDIUM_PRICE)))));
                        viewOverviewHolder.soldPrice.setText(formatter.format(soldPrice));
                    } else{
                    }
                }
                break;
            default:
                CurrencyDetailViewHolder viewHolder = (CurrencyDetailViewHolder) holder;
                mCursor.moveToPosition(position-1);
                // TODO: Below values are stored in DB as REALs.
                // We'll need to format them to currency number format.
                String type = getDetailType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_TYPE)));

                // Get symbol to use on CurrencyData query
                String symbol = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL));

                Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_TIMESTAMP));
                String date = TimestampToDate(timestamp);
                double quantity = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY));
                double price = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_PRICE));
                // If price is 0, then it is bonification, grouping or split which should not show price or totalValue
                if (price > 0) {
                    String totalValue = formatter.format(price * quantity);
                    String priceText = formatter.format(price);
                    viewHolder.price.setText(priceText);
                    viewHolder.totalValue.setText(totalValue);
                }

                String quantityText;

                if (symbol.equalsIgnoreCase("BTC") || symbol.equals("LTC")){
                    quantityText = String.format("%.6f", quantity);
                } else {
                    quantityText = String.format("%.2f", quantity);
                }

                viewHolder.transactionType.setText(type);
                viewHolder.transactionDate.setText(date);
                viewHolder.quantity.setText(quantityText);

                viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCursor.moveToPosition(position - 1);
                        String id = mCursor.getString(mCursor.getColumnIndex(PortfolioContract.CurrencyTransaction._ID));
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

    public interface CurrencyAdapterOnClickHandler {
        void onClick(String id, int type);
    }

    class CurrencyDetailViewHolder extends RecyclerView.ViewHolder{

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

        @BindView(R.id.currencyCardViewClickable)
        LinearLayout currencyCardViewClickable;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        @BindView(R.id.menuEdit)
        ImageView menuEdit;

        CurrencyDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class CurrencyDetailsOverviewViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.currentPrice)
        TextView currentPrice;

        @BindView(R.id.mediumPrice)
        TextView mediumPrice;

        @BindView(R.id.soldPrice)
        TextView soldPrice;

        public CurrencyDetailsOverviewViewHolder(View itemView){
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
        String selection = PortfolioContract.CurrencyData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        // Searches for existing CurrencyData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.CurrencyData.URI,
                null, selection, selectionArguments, null);
    }

    private Cursor getSoldDataCursor(String symbol){
        String selection = PortfolioContract.SoldCurrencyData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        // Searches for existing CurrencyData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.SoldCurrencyData.URI,
                null, selection, selectionArguments, null);
    }
}
