package br.com.carteira.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StockIncomeAdapter extends RecyclerView.Adapter<StockIncomeAdapter.
        StockIncomeViewHolder> {
    private static final String LOG_TAG = StockIncomeAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private StockAdapterOnClickHandler mClickHandler;

    public StockIncomeAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;

    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public StockIncomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock_incomes, parent, false);
        return new StockIncomeViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockIncomeViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        // TODO: Below values are stored in DB as REALs.
        // We'll need to format them to currency number format.
        Locale locale = new Locale( "pt", "BR" );
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        Long timestamp = mCursor.getLong(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
        String incomeType = getIncomeType(mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TYPE)));
        String date = TimestampToDate(timestamp);
        Log.d(LOG_TAG, "IncomeType: " + incomeType);
        Log.d(LOG_TAG, "IncomeValue: " + formatter.format(mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID))));
        Log.d(LOG_TAG, "Date: " + date);
        holder.incomeType.setText(incomeType);
        holder.incomeValue.setText(formatter.format(mCursor.getDouble(mCursor.getColumnIndex
                (PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID))));
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
        void onClick(String symbol, int type);
        void onCreateContextMenu(ContextMenu menu, View v,
                                 ContextMenu.ContextMenuInfo menuInfo, String id, int type);
    }

    class StockIncomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

        @BindView(R.id.incomeType)
        TextView incomeType;

        @BindView(R.id.incomeDate)
        TextView incomeDate;

        @BindView(R.id.incomePercent)
        TextView incomePercent;

        @BindView(R.id.incomeValue)
        TextView incomeValue;


        StockIncomeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int idColumn = mCursor.getColumnIndex(PortfolioContract.StockIncome._ID);
            int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TYPE));
            mClickHandler.onClick(mCursor.getString(idColumn), type);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                           ContextMenu.ContextMenuInfo menuInfo){
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int idColumn = mCursor.getColumnIndex(PortfolioContract.StockIncome._ID);
            int type = mCursor.getInt(mCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TYPE));
            mClickHandler.onCreateContextMenu(menu, v , menuInfo, mCursor.getString(idColumn), type);
        }
    }

    public String TimestampToDate(Long timestamp){
        String date = DateFormat.format("dd/MM/yyyy", timestamp).toString();
        return date;
    }

    public String getIncomeType(int incomeTypeId){
        switch (incomeTypeId){
            case Constants.IncomeType.INVALID:
                Log.d(LOG_TAG, "Invalid IncomeType");
                return "invalid";
            case Constants.IncomeType.DIVIDEND:
                Log.d(LOG_TAG, "Dividend IncomeType");
                return mContext.getResources().getString(R.string.dividend_income_type);
            case Constants.IncomeType.JCP:
                Log.d(LOG_TAG, "JCP IncomeType");
                return mContext.getResources().getString(R.string.jcp_income_type);
            default:
                Log.d(LOG_TAG, "Default IncomeType");
                return mContext.getResources().getString(R.string.dividend_income_type);
        }
    }
}
