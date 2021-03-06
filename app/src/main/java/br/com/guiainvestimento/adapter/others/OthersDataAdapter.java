package br.com.guiainvestimento.adapter.others;

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

import java.text.NumberFormat;
import java.util.Locale;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class OthersDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = OthersDataAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private OthersAdapterOnClickHandler mClickHandler;

    public OthersDataAdapter(Context context, OthersAdapterOnClickHandler clickHandler) {
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
        item = LayoutInflater.from(mContext).inflate(R.layout.adapter_others, parent, false);
        return new OthersDataViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        // If it is one of the OthersData adapter views
        mCursor.moveToPosition(position);
        OthersDataViewHolder viewHolder = (OthersDataViewHolder) holder;
        double othersAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.OthersData.COLUMN_VARIATION));
        double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.OthersData.COLUMN_INCOME));
        double totalGain = othersAppreciation + totalIncome;
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

        // Set text colors according to positive or negative values
        if (othersAppreciation >= 0){
            viewHolder.othersAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.green));
            viewHolder.othersAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
        } else {
            viewHolder.othersAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.red));
            viewHolder.othersAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }

        if (totalIncome >= 0){
            viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.green));
            viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
        } else {
            viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.red));
            viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }

        if (totalGain >= 0){
            viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
            viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
        } else {
            viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
            viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }
        double buyTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_BUY_VALUE_TOTAL));
        double sellTotal = mCursor.getDouble(mCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_SELL_VALUE_TOTAL));
        double variationPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(othersAppreciation/buyTotal*100)));
        double netIncomePercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalIncome/buyTotal*100)));
        double totalGainPercent = variationPercent + netIncomePercent;
        // Get handled values of OthersData with current symbol
        viewHolder.symbol.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract
                .OthersData.
                COLUMN_SYMBOL)));
        viewHolder.soldTotal.setText(String.format(formatter.format(sellTotal)));
        viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
        viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_CURRENT_TOTAL)))));
        viewHolder.othersAppreciation.setText(String.format(formatter.format(othersAppreciation)));
        viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
        viewHolder.othersAppreciationPercent.setText("(" + String.format("%.2f", variationPercent) + "%)");
        viewHolder.totalIncomePercent.setText("(" + String.format("%.2f", netIncomePercent) + "%)");
        viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
        viewHolder.currentPercent.setText(String.format("%.2f", mCursor.getDouble(
                mCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_CURRENT_PERCENT)))
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
            viewHolder.othersCardView.setLayoutParams(params);
        }
        viewHolder.othersCardViewClickable.setOnClickListener(new LinearLayout.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.MAIN);
            }
        });

        viewHolder.menuAdd.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.ADD);
            }
        });

        viewHolder.menuEdit.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.EDIT);
            }
        });

        viewHolder.menuSell.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_SYMBOL);
                mClickHandler.onClick(mCursor.getString(symbolColumn), Constants.AdapterClickable.SELL);
            }
        });

        viewHolder.menuDelete.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCursor.moveToPosition(position);
                int symbolColumn = mCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_SYMBOL);
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

    public interface OthersAdapterOnClickHandler {
        void onClick(String symbol, int id);
    }

    class OthersDataViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.others_card_view)
        CardView othersCardView;

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

        @BindView(R.id.othersAppreciation)
        TextView othersAppreciation;

        @BindView(R.id.othersAppreciationPercent)
        TextView othersAppreciationPercent;

        @BindView(R.id.totalIncome)
        TextView totalIncome;

        @BindView(R.id.totalIncomePercent)
        TextView totalIncomePercent;

        @BindView(R.id.totalGain)
        TextView totalGain;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        @BindView(R.id.othersCardViewClickable)
        LinearLayout othersCardViewClickable;

        @BindView(R.id.menuAdd)
        ImageView menuAdd;

        @BindView(R.id.menuEdit)
        ImageView menuEdit;

        @BindView(R.id.menuSell)
        ImageView menuSell;

        @BindView(R.id.menuDelete)
        ImageView menuDelete;

        public OthersDataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
