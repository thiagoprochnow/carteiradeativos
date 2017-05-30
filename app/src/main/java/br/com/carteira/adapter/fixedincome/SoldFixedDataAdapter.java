package br.com.carteira.adapter.fixedincome;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.carteira.R;
import br.com.carteira.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class SoldFixedDataAdapter extends RecyclerView.Adapter<SoldFixedDataAdapter.FixedPortfolioViewHolder> {
    private static final String LOG_TAG = SoldFixedDataAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private FixedAdapterOnClickHandler mClickHandler;

    public SoldFixedDataAdapter(Context context, FixedAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;

    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public FixedPortfolioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_sold_fixed, parent, false);
        return new FixedPortfolioViewHolder(item);
    }

    @Override
    public void onBindViewHolder(FixedPortfolioViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        mCursor.moveToPosition(position);
        Locale locale = new Locale( "pt", "BR" );
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

        double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.SoldFixedData.COLUMN_BUY_VALUE_TOTAL));
        // Get handled values of FixedTransaction with current symbol
        double sellGain = mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.SoldFixedData.COLUMN_SELL_GAIN));
        double sellGainPercent = sellGain/buyTotal*100;
        // Set text colors according to positive or negative values

        if (sellGain >=0){
            holder.sellGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
            holder.sellGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
        } else {
            holder.sellGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
            holder.sellGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }

        holder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                .SoldFixedData.
                COLUMN_SYMBOL)));
        holder.fixedQuantity.setText(Integer.toString(mCursor.getInt(mCursor.getColumnIndex
                (PortfolioContract.SoldFixedData.COLUMN_QUANTITY_TOTAL))));
        holder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
        holder.sellTotal.setText(String.format(formatter.format(mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.SoldFixedData.COLUMN_SELL_TOTAL)))));
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
            holder.fixedCardView.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null) {
            count = mCursor.getCount();
        }
        return count;
    }


    public interface FixedAdapterOnClickHandler {
        void onClick(String symbol);
        void onCreateContextMenu(ContextMenu menu, View v,
                                 ContextMenu.ContextMenuInfo menuInfo, String symbol);
    }

    class FixedPortfolioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        @BindView(R.id.fixed_card_view)
        CardView fixedCardView;

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.fixedQuantity)
        TextView fixedQuantity;

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.sellTotal)
        TextView sellTotal;

        @BindView(R.id.sellGain)
        TextView sellGain;

        @BindView(R.id.sellGainPercent)
        TextView sellGainPercent;


        FixedPortfolioViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.SoldFixedData.COLUMN_SYMBOL);
            mClickHandler.onClick(mCursor.getString(symbolColumn));
        }

        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.SoldFixedData.COLUMN_SYMBOL);
            mClickHandler.onCreateContextMenu(menu, v , menuInfo, mCursor.getString(symbolColumn));
        }
    }
}
