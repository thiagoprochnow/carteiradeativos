package br.com.guiainvestimento.adapter.fii;

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


public class SoldFiiDataAdapter extends RecyclerView.Adapter<SoldFiiDataAdapter.FiiPortfolioViewHolder> {
    private static final String LOG_TAG = SoldFiiDataAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private FiiAdapterOnClickHandler mClickHandler;

    public SoldFiiDataAdapter(Context context, FiiAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;

    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public FiiPortfolioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_sold_fii, parent, false);
        return new FiiPortfolioViewHolder(item);
    }

    @Override
    public void onBindViewHolder(FiiPortfolioViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        mCursor.moveToPosition(position);
        Locale locale = new Locale( "pt", "BR" );
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

        double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_BUY_VALUE_TOTAL));
        // Get handled values of FiiTransaction with current symbol
        double sellGain = mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_SELL_GAIN));
        double brokerage = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_BROKERAGE));
        double sellGainPercent = sellGain/buyTotal*100;
        // Set text colors according to positive or negative values

        String symbol = mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                .SoldFiiData.
                COLUMN_SYMBOL));

        if (sellGain >=0){
            holder.sellGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
            holder.sellGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
        } else {
            holder.sellGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
            holder.sellGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }

        holder.brokerage.setTextColor(ContextCompat.getColor(mContext,R.color.red));

        holder.symbol.setText(symbol);
        holder.fiiQuantity.setText(Integer.toString(mCursor.getInt(mCursor.getColumnIndex
                (PortfolioContract.SoldFiiData.COLUMN_QUANTITY_TOTAL))));
        holder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
        holder.sellTotal.setText(String.format(formatter.format(mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_SELL_TOTAL)))));
        holder.brokerage.setText(String.format(formatter.format(brokerage)));
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
            holder.fiiCardView.setLayoutParams(params);
        }

        holder.fiiCardViewClickable.setOnClickListener(new LinearLayout.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.MAIN);
            }
        });

        holder.menuAdd.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.ADD);
            }
        });

        holder.menuSell.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.SELL);
            }
        });

        holder.menuDelete.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.SoldStockData.COLUMN_SYMBOL);
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


    public interface FiiAdapterOnClickHandler {
        void onClick(String symbol, int id);
    }

    class FiiPortfolioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fii_card_view)
        CardView fiiCardView;

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.fiiQuantity)
        TextView fiiQuantity;

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.brokerage)
        TextView brokerage;

        @BindView(R.id.sellTotal)
        TextView sellTotal;

        @BindView(R.id.sellGain)
        TextView sellGain;

        @BindView(R.id.sellGainPercent)
        TextView sellGainPercent;

        @BindView(R.id.fiiCardViewClickable)
        LinearLayout fiiCardViewClickable;

        @BindView(R.id.menuAdd)
        ImageView menuAdd;

        @BindView(R.id.menuSell)
        ImageView menuSell;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        FiiPortfolioViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private Cursor getFiiDataCursor(String symbol){
        String selection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        return mContext.getContentResolver().query(
                PortfolioContract.FiiData.URI,
                null, selection, selectionArguments, null);
    }
}
