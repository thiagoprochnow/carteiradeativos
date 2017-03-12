package br.com.carteira.fragment;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class DividendFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = DividendFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputPerStockView;
    private EditText mInputExDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addDividend()) {
                    getActivity().finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_dividend_form, container, false);
        mInputPerStockView = (EditText) mView.findViewById(R.id.inputReceivedPerStock);
        mInputExDateView = (EditText) mView.findViewById(R.id.inputDividendExDate);

        // Adding current date to Buy Date field
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputExDateView.setText(simpleDateFormat.format(new Date()));

        // Configure to show Spinner when clicked on the Date EditText field
        mInputExDateView.setOnClickListener(setDatePicker(mInputExDateView));
        return mView;
    }

    // Validate inputted values and add the dividend to the stock portfolio
    private boolean addDividend() {

        boolean isValidPerStock = isValidDouble(mInputPerStockView);
        boolean isValidExDate = isValidDate(mInputExDateView);

        if (isValidExDate && isValidPerStock){
            Intent intent = getActivity().getIntent();
            String symbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);

            // Get and handle inserted date value
            String inputDate = mInputExDateView.getText().toString();
            // Timestamp to be saved on SQLite database
            Long timestamp = DateToTimestamp(inputDate);

            // Get the stock quantity bought before the dividend ex dividend
            // Will be used to calculate the total R$ received of dividend
            int stockQuantity = getStockQuantity(symbol, timestamp);
            double perStock = Double.parseDouble(mInputPerStockView.getText().toString());
            double totalReceiveValue = stockQuantity * perStock;

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_SYMBOL, symbol);
            // TODO: Type is hardcoded
            int dividend = Constants.IncomeType.DIVIDEND;
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_TYPE, dividend);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_PER_STOCK, perStock);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP, timestamp);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_AFFECTED_QUANTITY, stockQuantity);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_TOTAL, totalReceiveValue);
            // TODO: Calculate the percent based on total stocks value that received the income
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_PERCENT, "5.32");
            // Adds to the database
            Uri insertedUri = mContext.getContentResolver().insert(PortfolioContract.StockIncome.URI,
                    incomeCV);
            // If error occurs to add, shows error message
            if (insertedUri != null) {
                Toast.makeText(mContext, R.string.add_dividend_success, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(mContext, R.string.add_dividend_fail, Toast.LENGTH_SHORT).show();
            }

        } else{
            // If validation fails, show validation error message
            if(!isValidExDate){
                mInputExDateView.setError(this.getString(R.string.wrong_date));
            }
            if(!isValidPerStock){
                mInputPerStockView.setError(this.getString(R.string.wrong_income_per_stock));
            }
        }
        return false;
    }

}
