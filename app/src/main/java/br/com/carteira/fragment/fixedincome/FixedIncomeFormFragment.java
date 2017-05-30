package br.com.carteira.fragment.fixedincome;


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
public class FixedIncomeFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = FixedIncomeFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputPerFixedView;
    private EditText mInputExDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addFixedIncome()) {
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
        mView = inflater.inflate(R.layout.fragment_fixed_income_form, container, false);
        mInputPerFixedView = (EditText) mView.findViewById(R.id.inputReceivedPerFixed);
        mInputExDateView = (EditText) mView.findViewById(R.id.inputFixedIncomeExDate);
        Intent intent = getActivity().getIntent();
        // Set title according to income type
        int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.INVALID);

        getActivity().setTitle(R.string.form_title_fixed_income);

        // Adding current date to Buy Date field
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputExDateView.setText(simpleDateFormat.format(new Date()));

        // Configure to show Spinner when clicked on the Date EditText field
        mInputExDateView.setOnClickListener(setDatePicker(mInputExDateView));
        return mView;
    }

    // Validate inputted values and add the jcp or dividend to the income table
    private boolean addFixedIncome() {

        boolean isValidPerFixed = isValidDouble(mInputPerFixedView);
        boolean isValidExDate = isValidDate(mInputExDateView);

        if (isValidExDate && isValidPerFixed){
            Intent intent = getActivity().getIntent();
            String symbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
            int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.INVALID);
            double tax = 0;

            // Get and handle inserted date value
            String inputDate = mInputExDateView.getText().toString();
            // Timestamp to be saved on SQLite database
            Long timestamp = DateToTimestamp(inputDate);

            // Get the fixed income quantity bought before the jcp/dividend is ex
            // Will be used to calculate the total R$ received of jcp/dividend
            int fixedQuantity = getFixedQuantity(symbol, timestamp);
            double perFixed = Double.parseDouble(mInputPerFixedView.getText().toString());
            double receiveValue = fixedQuantity * perFixed;
            double liquidValue = receiveValue;

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.FixedIncome.COLUMN_SYMBOL, symbol);
            // TODO: Type is hardcoded
            incomeCV.put(PortfolioContract.FixedIncome.COLUMN_TYPE, incomeType);
            incomeCV.put(PortfolioContract.FixedIncome.COLUMN_PER_FIXED, perFixed);
            incomeCV.put(PortfolioContract.FixedIncome.COLUMN_EXDIVIDEND_TIMESTAMP, timestamp);
            incomeCV.put(PortfolioContract.FixedIncome.COLUMN_AFFECTED_QUANTITY, fixedQuantity);
            incomeCV.put(PortfolioContract.FixedIncome.COLUMN_RECEIVE_TOTAL, receiveValue);
            incomeCV.put(PortfolioContract.FixedIncome.COLUMN_TAX, tax);
            incomeCV.put(PortfolioContract.FixedIncome.COLUMN_RECEIVE_LIQUID, liquidValue);
            // TODO: Calculate the percent based on total fixeds value that received the income
            // Adds to the database
            Uri insertedUri = mContext.getContentResolver().insert(PortfolioContract.FixedIncome.URI,
                    incomeCV);
            // If error occurs to add, shows error message
            if (insertedUri != null) {
                boolean updateFixedDataIncome = updateFixedDataIncome(symbol, liquidValue, tax);
                if (updateFixedDataIncome) {
                    Toast.makeText(mContext, R.string.add_fixed_income_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }

            Toast.makeText(mContext, R.string.add_fixed_income_fail, Toast.LENGTH_LONG).show();

            return false;
        } else{
            // If validation fails, show validation error message
            if(!isValidExDate){
                mInputExDateView.setError(this.getString(R.string.wrong_date));
            }
            if(!isValidPerFixed){
                mInputPerFixedView.setError(this.getString(R.string.wrong_income_per_fixed));
            }
        }
        return false;
    }

    // Update Total Income on Fixed Data by new income added
    private boolean updateFixedDataIncome(String symbol, double valueReceived, double tax){
        // Prepare query to update fixed data income
        // and the total income received
        String selection = PortfolioContract.FixedData.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FixedData.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            double dbIncome = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_INCOME));
            double totalIncome = dbIncome + valueReceived;
            double totalTax = 0;

            ContentValues updateCV = new ContentValues();

            updateCV.put(PortfolioContract.FixedData.COLUMN_INCOME, totalIncome);
            updateCV.put(PortfolioContract.FixedData.COLUMN_INCOME_TAX, totalTax);

            int updateQueryCursor = mContext.getContentResolver().update(
                    PortfolioContract.FixedData.URI,
                    updateCV, selection, selectionArguments);

            if (updateQueryCursor == 1){
                return true;
            }
        }
        return false;
    }
}
