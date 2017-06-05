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
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.carteira.R;
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
        switch (viewType){
            // If it is the first view, return viewholder for FixedPortfolio overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.fixed_summary, parent, false);
                return new FixedSummaryViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fixed, parent, false);
                return new FixedDataViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                FixedSummaryViewHolder summaryViewHolder = (FixedSummaryViewHolder) holder;
                // If it is the first view, return viewholder for FixedPortfolio overview
                if (mCursor != null && summaryViewHolder != null) {
                    if (mCursor.getCount() != 0) {
                        summaryViewHolder.itemView.setVisibility(View.VISIBLE);
                    } else {
                        summaryViewHolder.itemView.setVisibility(View.GONE);
                    }
                }
                break;
            default:
                // If it is one of the FixedData adapter views
                mCursor.moveToPosition(position-1);
                FixedDataViewHolder viewHolder = (FixedDataViewHolder) holder;
                double fixedAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.FixedData.COLUMN_VARIATION));
                double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.FixedData.COLUMN_INCOME));
                double totalGain = fixedAppreciation + totalIncome;
                Locale locale = new Locale("pt", "BR");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

                // Set text colors according to positive or negative values
                if (fixedAppreciation >= 0){
                    viewHolder.fixedAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.fixedAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }

                if (totalIncome >= 0){
                    viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }

                if (totalGain >= 0){
                    viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }
                double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_BUY_VALUE_TOTAL));
                // Get handled values of FixedData with current symbol
                viewHolder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                        .FixedData.
                        COLUMN_SYMBOL)));
                viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
                viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_CURRENT_TOTAL)))));
                viewHolder.objectivePercent.setText(String.format("%.2f", mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_OBJECTIVE_PERCENT))) + "%");

                viewHolder.fixedAppreciation.setText(String.format(formatter.format(fixedAppreciation)));
                viewHolder.currentPercent.setText(String.format("%.2f", mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_CURRENT_PERCENT)))
                        + "%");
                viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
                viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
                if(position == mCursor.getCount()){
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


    public interface FixedAdapterOnClickHandler {
        void onClick(String symbol);
        void onCreateContextMenu(ContextMenu menu, View v,
                                 ContextMenu.ContextMenuInfo menuInfo, String symbol);
    }

    class FixedDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        @BindView(R.id.fixed_card_view)
        CardView fixedCardView;

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.objectivePercent)
        TextView objectivePercent;

        @BindView(R.id.fixedAppreciation)
        TextView fixedAppreciation;

        @BindView(R.id.currentPercent)
        TextView currentPercent;

        @BindView(R.id.totalIncome)
        TextView totalIncome;

        @BindView(R.id.totalGain)
        TextView totalGain;

        public FixedDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition-1);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SYMBOL);
            mClickHandler.onClick(mCursor.getString(symbolColumn));
        }

        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition-1);
            int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_SYMBOL);
            mClickHandler.onCreateContextMenu(menu, v , menuInfo, mCursor.getString(symbolColumn));
        }
    }

    class FixedSummaryViewHolder extends RecyclerView.ViewHolder{

        public FixedSummaryViewHolder(View itemView){
            super(itemView);
        }
    }
}
