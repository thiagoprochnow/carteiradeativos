package br.com.guiainvestimento.adapter.fund;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class FundDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = FundDataAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private FundAdapterOnClickHandler mClickHandler;

    public FundDataAdapter(Context context, FundAdapterOnClickHandler clickHandler) {
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
        item = LayoutInflater.from(mContext).inflate(R.layout.adapter_fund, parent, false);
        return new FundDataViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // If it is one of the FundData adapter views
        mCursor.moveToPosition(position);
        FundDataViewHolder viewHolder = (FundDataViewHolder) holder;
        double totalGain = mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_TOTAL_GAIN));
        int updateStatus = mCursor.getInt(mCursor.getColumnIndex
                (PortfolioContract.FundData.COLUMN_UPDATE_STATUS));
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
        double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_BUY_VALUE_TOTAL));
        double sellTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_SELL_VALUE_TOTAL));
        double totalGainPercent = totalGain/buyTotal*100;
        // Get handled values of FundData with current symbol
        viewHolder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                .FundData.
                COLUMN_SYMBOL)));
        viewHolder.soldTotal.setText(String.format(formatter.format(sellTotal)));
        viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
        viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_CURRENT_TOTAL)))));
        viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
        viewHolder.currentPercent.setText(String.format("%.2f", mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_CURRENT_PERCENT)))
                + "%");
        viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
        if(position == mCursor.getCount()-1){
            // If last item, apply margin in bottom to keep empty space for Floating button to occupy.
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            int leftDp = 10; // margin in dips
            int rightDp = 10; // margin in dips
            int topDp = 10; // margin in dips
            int bottomDp = 85; // margin in dips
            float d = mContext.getResources().getDisplayMetrics().density;
            int leftMargin = (int)(leftDp * d); // margin in pixels
            int rightMargin = (int)(rightDp * d); // margin in pixels
            int topMargin = (int) (topDp * d); // margin in pixels
            int bottomMargin = (int)(bottomDp * d); // margin in pixels
            params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
            viewHolder.fundCardView.setLayoutParams(params);
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
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.fund_update_failed_message), Toast.LENGTH_LONG).show();
                }
            });
        }

        viewHolder.fundCardViewClickable.setOnClickListener(new LinearLayout.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.MAIN);
            }
        });

        viewHolder.menuAdd.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.ADD);
            }
        });

        viewHolder.menuEdit.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.EDIT);
            }
        });

        viewHolder.menuSell.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.SELL);
            }
        });

        viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_SYMBOL);
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

    public interface FundAdapterOnClickHandler {
        void onClick(String symbol, int id);
    }

    class FundDataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.fund_card_view)
        CardView fundCardView;

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

        @BindView(R.id.fundCardViewClickable)
        LinearLayout fundCardViewClickable;

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

        public FundDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
