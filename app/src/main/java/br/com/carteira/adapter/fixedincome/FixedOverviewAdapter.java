package br.com.carteira.adapter.fixedincome;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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


public class FixedOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = FixedOverviewAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;

    public FixedOverviewAdapter(Context context) {
        this.mContext = context;
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
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fixed_overview, parent, false);
                return new FixedOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fixed_overview, parent, false);
                return new FixedOverviewViewHolder(item);
                //item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fixed, parent, false);
                //return new FixedDataViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                FixedOverviewViewHolder viewHolder = (FixedOverviewViewHolder) holder;
                mCursor.moveToPosition(position);

                double totalGain = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.FixedPortfolio.COLUMN_TOTAL_GAIN));
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
                double buyTotal =  mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FixedPortfolio.COLUMN_BUY_TOTAL));
                double totalGainPercent = 0;
                if (buyTotal != 0 ) {
                    totalGainPercent = totalGain / buyTotal * 100;
                }
                viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
                viewHolder.soldTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FixedPortfolio.COLUMN_SOLD_TOTAL)))));
                viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.FixedPortfolio.COLUMN_CURRENT_TOTAL)))));
                viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
                viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
                break;
            default:
                // If it is one of the FixedData adapter views
                Log.d(LOG_TAG, "No ViewHolder found");
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

    class FixedOverviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.soldTotal)
        TextView soldTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.totalGain)
        TextView totalGain;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        public FixedOverviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
