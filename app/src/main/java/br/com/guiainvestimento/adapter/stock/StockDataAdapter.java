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


public class StockDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = StockDataAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private StockAdapterOnClickHandler mClickHandler;

    public StockDataAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
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
        item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock, parent, false);
        return new StockDataViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // If it is one of the StockData adapter views
        mCursor.moveToPosition(position);
        StockDataViewHolder viewHolder = (StockDataViewHolder) holder;

        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

        double stockAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockData.COLUMN_VARIATION));
        double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockData.COLUMN_NET_INCOME));
        double brokerage = mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockData.COLUMN_BROKERAGE));
        double totalGain = mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockData.COLUMN_TOTAL_GAIN));
        int updateStatus = mCursor.getInt(mCursor.getColumnIndex
                (PortfolioContract.StockData.COLUMN_UPDATE_STATUS));
        // Show daily gain or loss
        if (updateStatus == Constants.UpdateStatus.UPDATED){
            double currentPrice = mCursor.getDouble(mCursor.getColumnIndex
                    (PortfolioContract.StockData.COLUMN_CURRENT_PRICE));
            double closingPrice = mCursor.getDouble(mCursor.getColumnIndex
                    (PortfolioContract.StockData.COLUMN_CLOSING_PRICE));
            double dailyGain = (currentPrice - closingPrice)/closingPrice * 100;
            String dailyGainString = String.format("%.2f", dailyGain);
            String dailyPrice = formatter.format(currentPrice);
            if (dailyGain >= 0){
                viewHolder.dailyPercent.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                viewHolder.dailyPercent.setText("(" + dailyGainString + "%)");
            } else {
                viewHolder.dailyPercent.setTextColor(ContextCompat.getColor(mContext, R.color.red2));
                viewHolder.dailyPercent.setText("(" + dailyGainString + "%)");
            }

            if (currentPrice >= closingPrice){
                viewHolder.dailyPrice.setTextColor(ContextCompat.getColor(mContext, R.color.green));
                viewHolder.dailyPrice.setText(dailyPrice);
            } else {
                viewHolder.dailyPrice.setTextColor(ContextCompat.getColor(mContext, R.color.red2));
                viewHolder.dailyPrice.setText(dailyPrice);
            }
        }

        // Set text colors according to positive or negative values
        if (stockAppreciation >= 0) {
            viewHolder.stockAppreciation.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            viewHolder.stockAppreciationPercent.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        } else {
            viewHolder.stockAppreciation.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            viewHolder.stockAppreciationPercent.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }

        if (totalIncome >= 0) {
            viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        } else {
            viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }

        if (totalGain >= 0) {
            viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext, R.color.green));
        } else {
            viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }

        viewHolder.totalBrokerage.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        viewHolder.totalBrokeragePercent.setTextColor(ContextCompat.getColor(mContext, R.color.red));

        double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL));
        double variationPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(stockAppreciation / buyTotal * 100)));
        double netIncomePercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalIncome / buyTotal * 100)));
        double brokeragePercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(brokerage / buyTotal * 100)));
        double totalGainPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalGain / buyTotal * 100)));
        // Get handled values of StockData with current symbol
        viewHolder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                .StockData.
                COLUMN_SYMBOL)));
        viewHolder.stockQuantity.setText(Integer.toString(mCursor.getInt(mCursor.getColumnIndex
                (PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL))));
        viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
        viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_CURRENT_TOTAL)))));

        viewHolder.stockAppreciation.setText(String.format(formatter.format(stockAppreciation)));
        viewHolder.currentPercent.setText(String.format("%.2f", mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_CURRENT_PERCENT)))
                + "%");
        viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
        viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
        viewHolder.totalBrokerage.setText(String.format(formatter.format(brokerage)));
        viewHolder.stockAppreciationPercent.setText("(" + String.format("%.2f", variationPercent) + "%)");
        viewHolder.totalIncomePercent.setText("(" + String.format("%.2f", netIncomePercent) + "%)");
        viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
        viewHolder.totalBrokeragePercent.setText("(" + String.format("%.2f", brokeragePercent) + "%)");

        if (position == mCursor.getCount()-1) {
            // If last item, apply margin in bottom to keep empty space for Floating button to occupy.
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            int leftDp = 10; // margin in dips
            int rightDp = 10; // margin in dips
            int topDp = 10; // margin in dips
            int bottomDp = 85; // margin in dips
            float d = mContext.getResources().getDisplayMetrics().density;
            int leftMargin = (int) (leftDp * d); // margin in pixels
            int rightMargin = (int) (rightDp * d); // margin in pixels
            int bottomMargin = (int) (bottomDp * d); // margin in pixels
            int topMargin = (int) (topDp * d); // margin in pixels
            params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            viewHolder.stockCardView.setLayoutParams(params);
        }

        // If the stock could not be updated automatically, give notice and option to update it manually
        if (updateStatus == Constants.UpdateStatus.UPDATED){
            viewHolder.updateError.setVisibility(View.GONE);
        } else {
            viewHolder.updateError.setVisibility(View.VISIBLE);
            viewHolder.updateError.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Use the Builder class for convenient dialog construction
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                    dialog.setMessage(R.string.dialog_stock_update_failed_message)
                            .setPositiveButton(R.string.menu_edit, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    mCursor.moveToPosition(position);
                                    int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_SYMBOL);
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

        viewHolder.stockCardViewClickable.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.MAIN);
            }
        });

        viewHolder.menuAdd.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.ADD);
            }
        });

        if (updateStatus == Constants.UpdateStatus.UPDATED){
            viewHolder.menuEdit.setVisibility(View.GONE);
        } else {
            viewHolder.menuEdit.setVisibility(View.VISIBLE);
            viewHolder.menuEdit.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCursor.moveToPosition(position);
                    int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_SYMBOL);
                    mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.EDIT);
                }
            });
        }

        viewHolder.menuSell.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.SELL);
            }
        });

        viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_SYMBOL);
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

    class StockDataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.stock_card_view)
        CardView stockCardView;

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.stockQuantity)
        TextView stockQuantity;

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.stockAppreciation)
        TextView stockAppreciation;

        @BindView(R.id.currentPercent)
        TextView currentPercent;

        @BindView(R.id.totalIncome)
        TextView totalIncome;

        @BindView(R.id.totalGain)
        TextView totalGain;

        @BindView(R.id.dailyPrice)
        TextView dailyPrice;

        @BindView(R.id.totalBrokerage)
        TextView totalBrokerage;

        @BindView(R.id.totalBrokeragePercent)
        TextView totalBrokeragePercent;

        @BindView(R.id.dailyPercent)
        TextView dailyPercent;

        @BindView(R.id.stockAppreciationPercent)
        TextView stockAppreciationPercent;

        @BindView(R.id.totalIncomePercent)
        TextView totalIncomePercent;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        @BindView(R.id.stockCardViewClickable)
        LinearLayout stockCardViewClickable;

        @BindView(R.id.menuAdd)
        ImageView menuAdd;

        @BindView(R.id.updateError)
        ImageView updateError;

        @BindView(R.id.menuEdit)
        ImageView menuEdit;

        @BindView(R.id.menuSell)
        ImageView menuSell;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        public StockDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
