package br.com.guiainvestimento.fragment.treasury;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;
import br.com.guiainvestimento.receiver.PortfolioReceiver;
import br.com.guiainvestimento.receiver.TreasuryReceiver;

/**
 * A simple {@link Fragment} subclass.
 */
public class TreasuryIncomeFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = TreasuryIncomeFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputReceiveView;
    private EditText mInputReceiveDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addTreasuryIncome()) {
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
        mView = inflater.inflate(R.layout.fragment_treasury_income_form, container, false);
        mInputReceiveView = (EditText) mView.findViewById(R.id.inputReceivedTreasury);
        mInputReceiveDateView = (EditText) mView.findViewById(R.id.inputTreasuryIncomeReceiveDate);
        Intent intent = getActivity().getIntent();
        // Set title according to income type
        int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.INVALID);

        getActivity().setTitle(R.string.form_title_treasury_income);

        // Adding current date to Buy Date field
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputReceiveDateView.setText(simpleDateFormat.format(new Date()));

        // Configure to show Spinner when clicked on the Date EditText field
        mInputReceiveDateView.setOnClickListener(setDatePicker(mInputReceiveDateView));
        return mView;
    }

    // Validate inputted values and add the jcp or dividend to the income table
    private boolean addTreasuryIncome() {

        boolean isValidPerTreasury = isValidDouble(mInputReceiveView);
        boolean isValidExDate = isValidDate(mInputReceiveDateView);

        if (isValidExDate && isValidPerTreasury){
            Intent intent = getActivity().getIntent();
            String symbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
            int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.INVALID);

            // Get and handle inserted date value
            String inputDate = mInputReceiveView.getText().toString();
            // Timestamp to be saved on SQLite database
            Long timestamp = DateToTimestamp(inputDate);

            // Get the treasury quantity bought before the jcp/dividend is ex
            // Will be used to calculate the total R$ received of jcp/dividend
            double receiveValue = Double.parseDouble(mInputReceiveView.getText().toString());
            double tax = 0.15 * receiveValue;
            double liquidValue = receiveValue - tax;

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.TreasuryIncome.COLUMN_SYMBOL, symbol);
            // TODO: Type is hardcoded
            incomeCV.put(PortfolioContract.TreasuryIncome.COLUMN_TYPE, incomeType);
            incomeCV.put(PortfolioContract.TreasuryIncome.COLUMN_EXDIVIDEND_TIMESTAMP, timestamp);
            incomeCV.put(PortfolioContract.TreasuryIncome.COLUMN_RECEIVE_TOTAL, receiveValue);
            incomeCV.put(PortfolioContract.TreasuryIncome.COLUMN_TAX, tax);
            incomeCV.put(PortfolioContract.TreasuryIncome.COLUMN_RECEIVE_LIQUID, liquidValue);
            // TODO: Calculate the percent based on total treasury value that received the income
            // Adds to the database
            Uri insertedUri = mContext.getContentResolver().insert(PortfolioContract.TreasuryIncome.URI,
                    incomeCV);
            // If error occurs to add, shows error message
            if (insertedUri != null) {
                boolean updateTreasuryDataIncome = updateTreasuryDataIncome(symbol, liquidValue, tax);
                if (updateTreasuryDataIncome) {
                    Toast.makeText(mContext, R.string.add_treasury_income_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }

            Toast.makeText(mContext, R.string.add_treasury_income_fail, Toast.LENGTH_LONG).show();

            return false;
        } else{
            // If validation fails, show validation error message
            if(!isValidExDate){
                mInputReceiveDateView.setError(this.getString(R.string.wrong_date));
            }
        }
        return false;
    }

    // Update Total Income on Treasury Data by new income added
    private boolean updateTreasuryDataIncome(String symbol, double valueReceived, double tax){
        // Prepare query to update treasury data income
        // and the total income received
        String selection = PortfolioContract.TreasuryData.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.TreasuryData.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            double dbIncome = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.TreasuryData.COLUMN_INCOME));
            double dbTax = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.TreasuryData.COLUMN_INCOME_TAX));
            double totalIncome = dbIncome + valueReceived;
            double totalTax = dbTax + tax;

            ContentValues updateCV = new ContentValues();

            updateCV.put(PortfolioContract.TreasuryData.COLUMN_INCOME, totalIncome);
            updateCV.put(PortfolioContract.TreasuryData.COLUMN_INCOME_TAX, totalTax);

            int updateQueryCursor = mContext.getContentResolver().update(
                    PortfolioContract.TreasuryData.URI,
                    updateCV, selection, selectionArguments);
            //TODO temporary solution until Treasury is ready for IntentService
            double currentPrice = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.TreasuryData.COLUMN_CURRENT_PRICE));
            ContentValues treasuryBulkCV = new ContentValues();
            treasuryBulkCV.put(symbol, currentPrice);
            int updatedRows2 = mContext.getContentResolver().update(
                    PortfolioContract.TreasuryData.BULK_UPDATE_URI,
                    treasuryBulkCV, null, null);
            if (updateQueryCursor == 1){
                // Send broadcast so TreasuryReceiver can update the rest
                TreasuryReceiver treasuryReceiver = new TreasuryReceiver(mContext);
                treasuryReceiver.updateTreasuryPortfolio();
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.TREASURY));

                PortfolioReceiver portfolioReceiver = new PortfolioReceiver(mContext);
                portfolioReceiver.updatePortfolio();
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
                return true;
            }
        }
        return false;
    }
}
