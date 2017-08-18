package br.com.guiainvestimento.fragment.others;


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


public class OthersIncomeDetailsFragment extends BaseFragment {
    private View mView;
    private static final String LOG_TAG = BaseFormFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_others_income_details, container, false);
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
        TextView viewTotalReceived = (TextView) mView.findViewById(R.id.totalReceived);
        TextView viewTax = (TextView) mView.findViewById(R.id.tax);
        TextView viewLiquidReceived = (TextView) mView.findViewById(R.id.liquidReceived);

        // Prepare query for others income
        String selection = PortfolioContract.OthersIncome._ID + " = ? ";
        String[] selectionArguments = {id};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.OthersIncome.URI,
                null, selection, selectionArguments, null);

        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();

            Locale locale = new Locale("pt", "BR");
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

            long timestamp = queryCursor.getLong(queryCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
            String symbol = String.valueOf(queryCursor.getString(queryCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_SYMBOL)));
            String exDate = TimestampToDate(timestamp);
            Double totalReceived = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL));
            Double tax = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_TAX));
            Double liquidReceived = totalReceived-tax;

            String StotalReceived = String.format(formatter.format(queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL))));
            String Stax = String.format(formatter.format(queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_TAX))));
            String SliquidReceived = String.format(formatter.format(liquidReceived));

            viewSymbol.setText(symbol);
            viewExDate.setText(exDate);
            viewTotalReceived.setText(StotalReceived);
            viewTax.setText(Stax);
            viewLiquidReceived.setText(SliquidReceived);
        } else {
        }
    }
}
