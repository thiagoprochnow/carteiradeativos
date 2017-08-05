package br.com.carteira.fragment.others;


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
public class OthersIncomeFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = OthersIncomeFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputTaxView;
    private EditText mInputReceiveView;
    private EditText mInputReceiveDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addOthersIncome()) {
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
        mView = inflater.inflate(R.layout.fragment_others_income_form, container, false);
        mInputReceiveView = (EditText) mView.findViewById(R.id.inputReceivedOthers);
        mInputReceiveDateView = (EditText) mView.findViewById(R.id.inputOthersIncomeReceiveDate);
        mInputTaxView = (EditText) mView.findViewById(R.id.inputTaxOthers);
        Intent intent = getActivity().getIntent();
        // Set title according to income type
        int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.INVALID);

        getActivity().setTitle(R.string.form_title_others_income);

        // Adding current date to Buy Date field
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputReceiveDateView.setText(simpleDateFormat.format(new Date()));

        // Configure to show Spinner when clicked on the Date EditText field
        mInputReceiveDateView.setOnClickListener(setDatePicker(mInputReceiveDateView));
        return mView;
    }

    // Validate inputted values and add the jcp or dividend to the income table
    private boolean addOthersIncome() {

        boolean isValidPerOthers = isValidDouble(mInputReceiveView);
        boolean isValidExDate = isValidDate(mInputReceiveDateView);
        boolean isValidTax = isValidDouble(mInputTaxView);

        if (isValidExDate && isValidPerOthers && isValidTax){
            Intent intent = getActivity().getIntent();
            String symbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
            int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.INVALID);

            // Get and handle inserted date value
            String inputDate = mInputReceiveDateView.getText().toString();
            // Timestamp to be saved on SQLite database
            Long timestamp = DateToTimestamp(inputDate);

            // Get the others quantity bought before the jcp/dividend is ex
            // Will be used to calculate the total R$ received of jcp/dividend
            double receiveValue = Double.parseDouble(mInputReceiveView.getText().toString());
            double tax = Double.parseDouble(mInputTaxView.getText().toString());
            double liquidValue = receiveValue - tax;

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_SYMBOL, symbol);
            // TODO: Type is hardcoded
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_TYPE, incomeType);
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_EXDIVIDEND_TIMESTAMP, timestamp);
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL, receiveValue);
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_TAX, tax);
            // TODO: Calculate the percent based on total others value that received the income
            // Adds to the database
            Uri insertedUri = mContext.getContentResolver().insert(PortfolioContract.OthersIncome.URI,
                    incomeCV);
            // If error occurs to add, shows error message
            if (insertedUri != null) {
                boolean updateOthersDataIncome = updateOthersDataIncome(symbol, liquidValue, tax);
                if (updateOthersDataIncome) {
                    Toast.makeText(mContext, R.string.add_others_income_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }

            Toast.makeText(mContext, R.string.add_others_income_fail, Toast.LENGTH_LONG).show();

            return false;
        } else{
            // If validation fails, show validation error message
            if(!isValidExDate){
                mInputReceiveDateView.setError(this.getString(R.string.wrong_date));
            }
        }
        return false;
    }

    // Update Total Income on Others Data by new income added
    private boolean updateOthersDataIncome(String symbol, double valueReceived, double tax){
        // Prepare query to update others data income
        // and the total income received
        String selection = PortfolioContract.OthersData.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.OthersData.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            double dbIncome = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_INCOME));
            double dbTax = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_INCOME_TAX));
            double totalIncome = dbIncome + valueReceived;
            double totalTax = dbTax + tax;

            ContentValues updateCV = new ContentValues();

            updateCV.put(PortfolioContract.OthersData.COLUMN_INCOME, totalIncome);
            updateCV.put(PortfolioContract.OthersData.COLUMN_INCOME_TAX, totalTax);

            int updateQueryCursor = mContext.getContentResolver().update(
                    PortfolioContract.OthersData.URI,
                    updateCV, selection, selectionArguments);

            //TODO temporary solution until others is ready for IntentService
            double currentTotal = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_CURRENT_TOTAL));
            ContentValues othersBulkCV = new ContentValues();
            othersBulkCV.put(symbol, currentTotal);
            int updatedRows2 = mContext.getContentResolver().update(
                    PortfolioContract.OthersData.BULK_UPDATE_URI,
                    othersBulkCV, null, null);
            if (updateQueryCursor == 1){
                // Send broadcast so OthersReceiver can update the rest
                mContext.sendBroadcast(new Intent(Constants.Receiver.OTHERS));
                return true;
            }
        }
        return false;
    }
}
