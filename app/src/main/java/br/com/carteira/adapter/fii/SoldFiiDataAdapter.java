package br.com.carteira.adapter.fii;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.carteira.R;
import br.com.carteira.data.PortfolioContract;
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
    public void onBindViewHolder(FiiPortfolioViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Locale locale = new Locale( "pt", "BR" );
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

        double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_BUY_VALUE_TOTAL));
        // Get handled values of FiiTransaction with current symbol
        double sellGain = mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_SELL_GAIN));
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
                .SoldFiiData.
                COLUMN_SYMBOL)));
        holder.fiiQuantity.setText(Integer.toString(mCursor.getInt(mCursor.getColumnIndex
                (PortfolioContract.SoldFiiData.COLUMN_QUANTITY_TOTAL))));
        holder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
        holder.sellTotal.setText(String.format(formatter.format(mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_SELL_TOTAL)))));
        holder.sellGain.setText(String.format(formatter.format(sellGain)));
        holder.sellGainPercent.setText("("+ String.format("%.2f",sellGainPercent) + "%)");
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
        void onClick(String symbol);
        void onCreateContextMenu(ContextMenu menu, View v,
                                 ContextMenu.ContextMenuInfo menuInfo, String symbol);
    }

    class FiiPortfolioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.fiiQuantity)
        TextView fiiQuantity;

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.sellTotal)
        TextView sellTotal;

        @BindView(R.id.sellGain)
        TextView sellGain;

        @BindView(R.id.sellGainPercent)
        TextView sellGainPercent;


        FiiPortfolioViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_SYMBOL);
            mClickHandler.onClick(mCursor.getString(symbolColumn));
        }

        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.SoldFiiData.COLUMN_SYMBOL);
            mClickHandler.onCreateContextMenu(menu, v , menuInfo, mCursor.getString(symbolColumn));
        }
    }
}
