package br.com.carteira.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;


public class IncomeDetailsFragment extends BaseFragment {
    private View mView;
    private static final String LOG_TAG = BaseFormFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_income_details, container, false);
        // TODO: Only for testing, will change later, sets text as symbol passed by intent
        Intent intent = getActivity().getIntent();
        String id = intent.getStringExtra(Constants.Extra.EXTRA_INCOME_ID);
        showIncomeDetails(id);
        return mView;
    }

    // Query the income information using the "id" and displays them on the screen
    public void showIncomeDetails(String id){
        TextView viewExDate = (TextView) mView.findViewById(R.id.exDate);
        TextView viewQuantity = (TextView) mView.findViewById(R.id.quantity);
        TextView viewPerStock = (TextView) mView.findViewById(R.id.perStock);
        TextView viewTotalReceived = (TextView) mView.findViewById(R.id.totalReceived);
        TextView viewTax = (TextView) mView.findViewById(R.id.tax);
        TextView viewLiquidReceived = (TextView) mView.findViewById(R.id.liquidReceived);

        // Prepare query for stock income
        String selection = PortfolioContract.StockIncome._ID + " = ? ";
        String[] selectionArguments = {id};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockIncome.URI,
                null, selection, selectionArguments, null);

        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            Log.d(LOG_TAG, "Income details found");

            long timestamp = queryCursor.getLong(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
            String exDate = TimestampToDate(timestamp);
            String quantity = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_AFFECTED_QUANTITY)));
            String perStock = String.valueOf(queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_PER_STOCK)));
            String totalReceived = String.valueOf(queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_RECEIVE_TOTAL)));
            String tax = String.valueOf(queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TAX)));
            String liquidReceived = String.valueOf(queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID)));

            viewExDate.setText(exDate);
            viewQuantity.setText(quantity);
            viewPerStock.setText(perStock);
            viewTotalReceived.setText(totalReceived);
            viewTax.setText(tax);
            viewLiquidReceived.setText(liquidReceived);
        } else {
            Log.d(LOG_TAG, "No income details found");
        }
    }
}
