package br.com.guiainvestimento.fragment.fii;


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

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.api.service.FiiIntentService;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FiiIncomeFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = FiiIncomeFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputPerFiiView;
    private EditText mInputExDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addFiiIncome()) {
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
        mView = inflater.inflate(R.layout.fragment_fii_income_form, container, false);
        mInputPerFiiView = (EditText) mView.findViewById(R.id.inputReceivedPerFii);
        mInputExDateView = (EditText) mView.findViewById(R.id.inputFiiIncomeExDate);
        Intent intent = getActivity().getIntent();
        // Set title according to income type
        int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.INVALID);

        getActivity().setTitle(R.string.form_title_fii_income);

        // Adding current date to Buy Date field
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputExDateView.setText(simpleDateFormat.format(new Date()));

        // Configure to show Spinner when clicked on the Date EditText field
        mInputExDateView.setOnClickListener(setDatePicker(mInputExDateView));
        return mView;
    }

    // Validate inputted values and add the jcp or dividend to the income table
    private boolean addFiiIncome() {

        boolean isValidPerFii = isValidDouble(mInputPerFiiView);
        boolean isValidExDate = isValidDate(mInputExDateView);

        if (isValidExDate && isValidPerFii){
            Intent intent = getActivity().getIntent();
            String symbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
            int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.INVALID);
            double tax = 0;

            // Get and handle inserted date value
            String inputDate = mInputExDateView.getText().toString();
            // Timestamp to be saved on SQLite database
            Long timestamp = DateToTimestamp(inputDate);

            // Get the fii quantity bought before the jcp/dividend is ex
            // Will be used to calculate the total R$ received of jcp/dividend
            double fiiQuantity = getFiiQuantity(symbol, timestamp);
            double perFii = Double.parseDouble(mInputPerFiiView.getText().toString());
            double receiveValue = fiiQuantity * perFii;
            double liquidValue = receiveValue;

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_SYMBOL, symbol);
            // TODO: Type is hardcoded
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_TYPE, incomeType);
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_PER_FII, perFii);
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP, timestamp);
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_AFFECTED_QUANTITY, fiiQuantity);
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_RECEIVE_TOTAL, receiveValue);
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_TAX, tax);
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID, liquidValue);
            // TODO: Calculate the percent based on total fiis value that received the income
            // Adds to the database
            Uri insertedUri = mContext.getContentResolver().insert(PortfolioContract.FiiIncome.URI,
                    incomeCV);
            // If error occurs to add, shows error message
            if (insertedUri != null) {
                boolean updateFiiDataIncome = updateFiiDataIncome(symbol, liquidValue, tax);
                if (updateFiiDataIncome) {
                    Toast.makeText(mContext, R.string.add_fii_income_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }

            Toast.makeText(mContext, R.string.add_fii_income_fail, Toast.LENGTH_LONG).show();

            return false;
        } else{
            // If validation fails, show validation error message
            if(!isValidExDate){
                mInputExDateView.setError(this.getString(R.string.wrong_date));
            }
            if(!isValidPerFii){
                mInputPerFiiView.setError(this.getString(R.string.wrong_income_per_fii));
            }
        }
        return false;
    }

    // Update Total Income on Fii Data by new income added
    private boolean updateFiiDataIncome(String symbol, double valueReceived, double tax){
        // Prepare query to update fii data income
        // and the total income received
        String selection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FiiData.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            double dbIncome = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_INCOME));
            double totalIncome = dbIncome + valueReceived;
            double totalTax = 0;

            ContentValues updateCV = new ContentValues();

            updateCV.put(PortfolioContract.FiiData.COLUMN_INCOME, totalIncome);
            updateCV.put(PortfolioContract.FiiData.COLUMN_INCOME_TAX, totalTax);

            int updateQueryCursor = mContext.getContentResolver().update(
                    PortfolioContract.FiiData.URI,
                    updateCV, selection, selectionArguments);
            if (updateQueryCursor == 1){
                Intent mServiceIntent = new Intent(mContext, FiiIntentService
                        .class);
                mServiceIntent.putExtra(FiiIntentService.ADD_SYMBOL, symbol);
                getActivity().startService(mServiceIntent);
                return true;
            }
        }
        return false;
    }
}
