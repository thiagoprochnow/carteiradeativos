package br.com.carteira.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.security.Timestamp;

import br.com.carteira.R;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFormFragment;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StockDividendAdapter extends RecyclerView.Adapter<StockDividendAdapter
        .StockDividendViewHolder> {
    private static final String LOG_TAG = StockDividendAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private StockAdapterOnClickHandler mClickHandler;

    public StockDividendAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;

    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public StockDividendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock_incomes, parent, false);
        return new StockDividendViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockDividendViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        // TODO: Below values are stored in DB as REALs.
        // We'll need to format them to currency number format.
        Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
        Log.d(LOG_TAG, "Holder timestamp: " + timestamp);
        String date = TimestampToDate(timestamp);
        holder.incomeType.setText(mCursor.getString(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TYPE)));
        holder.incomeValue.setText("R$"+String.format("%.2f",mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockIncome.COLUMN_PER_STOCK))));
        holder.incomeDate.setText(date);
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
        void onClick(String symbol);
        void onLongClick(String symbol);
    }

    class StockDividendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        @BindView(R.id.incomeType)
        TextView incomeType;

        @BindView(R.id.incomeDate)
        TextView incomeDate;

        @BindView(R.id.incomePercent)
        TextView incomePercent;

        @BindView(R.id.incomeValue)
        TextView incomeValue;


        StockDividendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // TODO: Need to figure it we will make a detailed income activity to show taxess
        }

        @Override
        public boolean onLongClick(View v){
            // TODO: Make option to delete or edit the income values.
            return true;
        }
    }

    public String TimestampToDate(Long timestamp){
        String date = DateFormat.format("dd/MM/yyyy", timestamp).toString();
        return date;
    }
}
