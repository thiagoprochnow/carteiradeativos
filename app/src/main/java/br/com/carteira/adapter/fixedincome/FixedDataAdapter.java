package br.com.carteira.adapter.fixedincome;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class FixedDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = FixedDataAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private FixedAdapterOnClickHandler mClickHandler;

    public FixedDataAdapter(Context context, FixedAdapterOnClickHandler clickHandler) {
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
        item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fixed, parent, false);
        return new FixedDataViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // If it is one of the FixedData adapter views
        mCursor.moveToPosition(position);
        FixedDataViewHolder viewHolder = (FixedDataViewHolder) holder;
        double totalGain = mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_TOTAL_GAIN));
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

        // Set text colors according to positive or negative values

        if (totalGain >= 0){
            viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
            viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
        } else {
            viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
            viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }
        double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_BUY_VALUE_TOTAL));
        double sellTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SELL_VALUE_TOTAL));
        double totalGainPercent = totalGain/buyTotal*100;
        // Get handled values of FixedData with current symbol
        viewHolder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                .FixedData.
                COLUMN_SYMBOL)));
        viewHolder.soldTotal.setText(String.format(formatter.format(sellTotal)));
        viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
        viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_CURRENT_TOTAL)))));
        viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
        viewHolder.currentPercent.setText(String.format("%.2f", mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_CURRENT_PERCENT)))
                + "%");
        viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
        if(position == mCursor.getCount()-1){
            // If last item, apply margin in bottom to keep empty space for Floating button to occupy.
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            int leftDp = 10; // margin in dips
            int rightDp = 10; // margin in dips
            int bottomDp = 85; // margin in dips
            float d = mContext.getResources().getDisplayMetrics().density;
            int leftMargin = (int)(leftDp * d); // margin in pixels
            int rightMargin = (int)(rightDp * d); // margin in pixels
            int bottomMargin = (int)(bottomDp * d); // margin in pixels
            params.setMargins(leftMargin, 0, rightMargin, bottomMargin);
            viewHolder.fixedCardView.setLayoutParams(params);
        }
        viewHolder.fixedCardViewClickable.setOnClickListener(new LinearLayout.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.MAIN);
            }
        });

        viewHolder.menuAdd.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.ADD);
            }
        });

        viewHolder.menuEdit.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.EDIT);
            }
        });

        viewHolder.menuSell.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.SELL);
            }
        });

        viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener(){
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

    public interface FixedAdapterOnClickHandler {
        void onClick(String symbol, int id);
    }

    class FixedDataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fixed_card_view)
        CardView fixedCardView;

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.soldTotal)
        TextView soldTotal;

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.currentPercent)
        TextView currentPercent;

        @BindView(R.id.totalGain)
        TextView totalGain;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        @BindView(R.id.fixedCardViewClickable)
        LinearLayout fixedCardViewClickable;

        @BindView(R.id.menuAdd)
        ImageView menuAdd;

        @BindView(R.id.menuEdit)
        ImageView menuEdit;

        @BindView(R.id.menuSell)
        ImageView menuSell;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        public FixedDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
