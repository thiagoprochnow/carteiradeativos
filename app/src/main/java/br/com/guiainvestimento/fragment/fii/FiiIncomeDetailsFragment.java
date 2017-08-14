package br.com.guiainvestimento.fragment.fii;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;
import br.com.guiainvestimento.fragment.BaseFragment;


public class FiiIncomeDetailsFragment extends BaseFragment {
    private View mView;
    private static final String LOG_TAG = BaseFormFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_fii_income_details, container, false);
        // TODO: Only for testing, will change later, sets text as symbol passed by intent
        Intent intent = getActivity().getIntent();
        String id = intent.getStringExtra(Constants.Extra.EXTRA_INCOME_ID);
        showIncomeDetails(id);
        return mView;
    }

    // Query the income information using the "id" and displays them on the screen
    public void showIncomeDetails(String id){
        TextView viewSymbol = (TextView) mView.findViewById(R.id.incomeSymbol);
        TextView viewExDate = (TextView) mView.findViewById(R.id.exDate);
        TextView viewQuantity = (TextView) mView.findViewById(R.id.quantity);
        TextView viewPerFii = (TextView) mView.findViewById(R.id.perFii);
        TextView viewTotalReceived = (TextView) mView.findViewById(R.id.totalReceived);
        TextView viewTax = (TextView) mView.findViewById(R.id.tax);
        TextView viewLiquidReceived = (TextView) mView.findViewById(R.id.liquidReceived);

        // Prepare query for fii income
        String selection = PortfolioContract.FiiIncome._ID + " = ? ";
        String[] selectionArguments = {id};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FiiIncome.URI,
                null, selection, selectionArguments, null);

        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            Log.d(LOG_TAG, "Income details found");

            Locale locale = new Locale("pt", "BR");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

            long timestamp = queryCursor.getLong(queryCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
            String symbol = String.valueOf(queryCursor.getString(queryCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_SYMBOL)));
            String exDate = TimestampToDate(timestamp);
            String quantity = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_AFFECTED_QUANTITY)));
            String perFii = String.format(formatter.format(queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_PER_FII))));
            String totalReceived = String.format(formatter.format(queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_RECEIVE_TOTAL))));
            String tax = String.format(formatter.format(queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_TAX))));
            String liquidReceived = String.format(formatter.format(queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID))));

            viewSymbol.setText(symbol);
            viewExDate.setText(exDate);
            viewQuantity.setText(quantity);
            viewPerFii.setText(perFii);
            viewTotalReceived.setText(totalReceived);
            viewTax.setText(tax);
            viewLiquidReceived.setText(liquidReceived);
        } else {
            Log.d(LOG_TAG, "No income details found");
        }
    }
}
