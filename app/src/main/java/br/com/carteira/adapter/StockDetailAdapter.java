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


public class StockDetailAdapter extends RecyclerView.Adapter<StockDetailAdapter.
        StockDetailViewHolder> {
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
    public StockDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock_details, parent, false);
        return new StockDetailViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockDetailViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        // TODO: Below values are stored in DB as REALs.
        // We'll need to format them to currency number format.
        String type = getDetailType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE)));
        Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TIMESTAMP));
        String date = TimestampToDate(timestamp);
        int quantity = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
        double price = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
        // If price is 0, then it is bonification, grouping or split which should not show price or totalValue
        if (price > 0) {
            Locale locale = new Locale( "pt", "BR" );
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
            String totalValue = formatter.format(price * quantity);
            String priceText = formatter.format(price);
            holder.price.setText(priceText);
            holder.totalValue.setText(totalValue);
        }
        String quantityText = String.valueOf(quantity);

        holder.transactionType.setText(type);
        holder.transactionDate.setText(date);
        holder.quantity.setText(quantityText);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null) {
            count = mCursor.getCount();
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
            mCursor.moveToPosition(adapterPosition);
            int idColumn = mCursor.getColumnIndex(PortfolioContract.StockTransaction._ID);
            int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE));
            mClickHandler.onCreateContextMenu(menu, v , menuInfo, mCursor.getString(idColumn), type);
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
}
