package br.com.carteira.fragment.stock;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFormFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class JCPDividendFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = JCPDividendFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputPerStockView;
    private EditText mInputExDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addJCPDividend()) {
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
        mView = inflater.inflate(R.layout.fragment_jcp_dividend_form, container, false);
        mInputPerStockView = (EditText) mView.findViewById(R.id.inputReceivedPerStock);
        mInputExDateView = (EditText) mView.findViewById(R.id.inputJCPDividendExDate);

        // Adding current date to Buy Date field
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputExDateView.setText(simpleDateFormat.format(new Date()));

        // Configure to show Spinner when clicked on the Date EditText field
        mInputExDateView.setOnClickListener(setDatePicker(mInputExDateView));
        return mView;
    }

    // Validate inputted values and add the jcp or dividend to the income table
    private boolean addJCPDividend() {

        boolean isValidPerStock = isValidDouble(mInputPerStockView);
        boolean isValidExDate = isValidDate(mInputExDateView);

        if (isValidExDate && isValidPerStock){
            Intent intent = getActivity().getIntent();
            String symbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
            int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.INVALID);
            double tax = 0;

            // Get and handle inserted date value
            String inputDate = mInputExDateView.getText().toString();
            // Timestamp to be saved on SQLite database
            Long timestamp = DateToTimestamp(inputDate);

            // Get the stock quantity bought before the jcp/dividend is ex
            // Will be used to calculate the total R$ received of jcp/dividend
            int stockQuantity = getStockQuantity(symbol, timestamp);
            double perStock = Double.parseDouble(mInputPerStockView.getText().toString());
            double receiveValue = stockQuantity * perStock;
            double liquidValue = receiveValue;

            // If it is JCP, needs to calculate the tax and liquid value to be received
            if(incomeType == Constants.IncomeType.JCP){
                tax = calculateTax(receiveValue);
                liquidValue = receiveValue - tax;
            }

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_SYMBOL, symbol);
            // TODO: Type is hardcoded
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_TYPE, incomeType);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_PER_STOCK, perStock);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP, timestamp);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_AFFECTED_QUANTITY, stockQuantity);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_TOTAL, receiveValue);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_TAX, tax);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID, liquidValue);
            // TODO: Calculate the percent based on total stocks value that received the income
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_PERCENT, "5.32");
            // Adds to the database
            Uri insertedUri = mContext.getContentResolver().insert(PortfolioContract.StockIncome.URI,
                    incomeCV);
            // If error occurs to add, shows error message
            if (insertedUri != null) {
                boolean updateStockDataIncome = updateStockDataIncome(symbol, liquidValue);
                if (updateStockDataIncome) {
                    if (incomeType == Constants.IncomeType.DIVIDEND) {
                        Toast.makeText(mContext, R.string.add_dividend_success, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(mContext, R.string.add_jcp_success, Toast.LENGTH_LONG)
                                .show();
                    }
                    return true;
                }
            }

            if (incomeType == Constants.IncomeType.DIVIDEND) {
                Toast.makeText(mContext, R.string.add_dividend_fail, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(mContext, R.string.add_jcp_fail, Toast.LENGTH_LONG).show();
            }
            return false;
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

    private double calculateTax(double receiveValue){
        // Tax for JCP is of 15% of total receive value
        return receiveValue*0.15;
    }

    // Update Total Income on Stock Data by new income added
    private boolean updateStockDataIncome(String symbol, double valueReceived){
        // Prepare query to update stock data income
        // and the total income received
        String selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            double dbIncome = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_INCOME_TOTAL));
            double totalIncome = dbIncome + valueReceived;

            ContentValues updateCV = new ContentValues();

            updateCV.put(PortfolioContract.StockData.COLUMN_INCOME_TOTAL, totalIncome);

            int updateQueryCursor = mContext.getContentResolver().update(
                    PortfolioContract.StockData.URI,
                    updateCV, selection, selectionArguments);
            if (updateQueryCursor == 1){
                boolean updateStockPortfolio = updateStockPortfolio();
                if (updateStockPortfolio) {
                    return true;
                }
            }
        }
        return false;
    }
}
