package br.com.guiainvestimento.adapter.stock;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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


public class SoldStockDataAdapter extends RecyclerView.Adapter<SoldStockDataAdapter.StockPortfolioViewHolder> {
    private static final String LOG_TAG = SoldStockDataAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private StockAdapterOnClickHandler mClickHandler;

    public SoldStockDataAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;

    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public StockPortfolioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_sold_stock, parent, false);
        return new StockPortfolioViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockPortfolioViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        mCursor.moveToPosition(position);
        Locale locale = new Locale( "pt", "BR" );
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        int updateStatus = -1;

        double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_BUY_VALUE_TOTAL));
        // Get handled values of StockTransaction with current symbol
        double sellGain = mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SELL_GAIN));
        double sellGainPercent = sellGain/buyTotal*100;
        // Set text colors according to positive or negative values
        String symbol = mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                .SoldStockData.
                COLUMN_SYMBOL));

        Cursor dataCursor = getStockDataCursor(symbol);
        if (dataCursor.moveToFirst()){
            updateStatus = dataCursor.getInt(dataCursor.getColumnIndex
                    (PortfolioContract.StockData.COLUMN_UPDATE_STATUS));
        }

        if (sellGain >=0){
            holder.sellGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
            holder.sellGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
        } else {
            holder.sellGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
            holder.sellGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }

        holder.symbol.setText(symbol);
        holder.stockQuantity.setText(Integer.toString(mCursor.getInt(mCursor.getColumnIndex
                (PortfolioContract.SoldStockData.COLUMN_QUANTITY_TOTAL))));
        holder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
        holder.sellTotal.setText(String.format(formatter.format(mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SELL_TOTAL)))));
        holder.sellGain.setText(String.format(formatter.format(sellGain)));
        holder.sellGainPercent.setText("("+ String.format("%.2f",sellGainPercent) + "%)");
        if(position == mCursor.getCount()-1){
            // If last item, apply margin in bottom to keep empty space for Floating button to occupy.
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            int leftDp = 10; // margin in dips
            int topDp = 10; // margin in dips
            int rightDp = 10; // margin in dips
            int bottomDp = 85; // margin in dips
            float d = mContext.getResources().getDisplayMetrics().density;
            int leftMargin = (int)(leftDp * d); // margin in pixels
            int topMargin = (int)(topDp * d); // margin in pixels
            int rightMargin = (int)(rightDp * d); // margin in pixels
            int bottomMargin = (int)(bottomDp * d); // margin in pixels
            params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            holder.stockCardView.setLayoutParams(params);
        }

        // If the stock could not be updated automatically, give notice and option to update it manually
        if (updateStatus == Constants.UpdateStatus.UPDATED){
            holder.updateError.setVisibility(View.GONE);
        } else {
            holder.updateError.setVisibility(View.VISIBLE);
            holder.updateError.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Use the Builder class for convenient dialog construction
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setMessage(R.string.dialog_stock_update_failed_message)
                            .setPositiveButton(R.string.menu_edit, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mCursor.moveToPosition(position);
                                    int symbolColumn = mCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SYMBOL);
                                    mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.EDIT);
                                }
                            })
                            .setNegativeButton(R.string.edit_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    // Create the AlertDialog object and return it
                    dialog.create().show();
                }
            });
        }

        holder.stockCardViewClickable.setOnClickListener(new LinearLayout.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.MAIN);
            }
        });

        holder.menuAdd.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.ADD);
            }
        });

        if (updateStatus == Constants.UpdateStatus.UPDATED){
            holder.menuEdit.setVisibility(View.GONE);
        } else {
            holder.menuEdit.setVisibility(View.VISIBLE);
            holder.menuEdit.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCursor.moveToPosition(position);
                    int symbolColumn = mCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SYMBOL);
                    mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.EDIT);
                }
            });
        }

        holder.menuSell.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.SELL);
            }
        });

        holder.menuDelete.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.DELETE);
            }
        });
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
        void onClick(String symbol, int id);
    }

    class StockPortfolioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.stock_card_view)
        CardView stockCardView;

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.stockQuantity)
        TextView stockQuantity;

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.sellTotal)
        TextView sellTotal;

        @BindView(R.id.sellGain)
        TextView sellGain;

        @BindView(R.id.sellGainPercent)
        TextView sellGainPercent;

        @BindView(R.id.stockCardViewClickable)
        LinearLayout stockCardViewClickable;

        @BindView(R.id.updateError)
        ImageView updateError;

        @BindView(R.id.menuAdd)
        ImageView menuAdd;

        @BindView(R.id.menuEdit)
        ImageView menuEdit;

        @BindView(R.id.menuSell)
        ImageView menuSell;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;


        StockPortfolioViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    private Cursor getStockDataCursor(String symbol){
        String selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        return mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                null, selection, selectionArguments, null);
    }
}
