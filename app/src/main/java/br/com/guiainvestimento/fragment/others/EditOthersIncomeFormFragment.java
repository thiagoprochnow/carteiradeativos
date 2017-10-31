package br.com.guiainvestimento.fragment.others;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;


public class EditOthersIncomeFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditOthersIncomeFormFragment.class.getSimpleName();
    private View mView;
    private String mId;
    private Cursor mIncomeCursor;
    private String mSymbol;
    private int mIncomeType;
    private double mReceiveTotal;
    private double mOldTax;
    private double mOldLiquid;
    private double mTaxDif;
    private double mLiquidDif;
    private long mDate;
    private EditText mInputOthersTotalView;
    private EditText mInputExDateView;
    private EditText mInputTaxOthers;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateOthersIncome()) {
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
        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        mId = intent.getStringExtra(Constants.Extra.EXTRA_TRANSACTION_ID);
        mIncomeCursor = getIncomeCursor(mId);
        mIncomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.Type.INVALID);

        if (mIncomeCursor.moveToFirst()) {
            mReceiveTotal = mIncomeCursor.getDouble(mIncomeCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL));
            mDate = mIncomeCursor.getLong(mIncomeCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
            mSymbol = mIncomeCursor.getString(mIncomeCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_SYMBOL));
            mOldLiquid = mIncomeCursor.getDouble(mIncomeCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL));
            mOldTax = mIncomeCursor.getDouble(mIncomeCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_TAX));
            mOldLiquid = mOldLiquid - mOldTax;
        } else{
            getActivity().finish();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        getActivity().setTitle(getResources().getString(R.string.menu_edit));
        mView = inflater.inflate(R.layout.fragment_others_income_edit_form, container, false);
        mInputOthersTotalView = (EditText) mView.findViewById(R.id.inputReceivedOthers);
        mInputOthersTotalView.setText(String.valueOf(mReceiveTotal), EditText.BufferType.EDITABLE);
        mInputExDateView = (EditText) mView.findViewById(R.id.inputOthersIncomeReceiveDate);
        mInputExDateView.setText(simpleDateFormat.format(mDate));
        mInputExDateView.setOnClickListener(setDatePicker(mInputExDateView));
        mInputTaxOthers = (EditText) mView.findViewById(R.id.inputTaxOthers);
        mInputTaxOthers.setText(String.valueOf(mOldTax), EditText.BufferType.EDITABLE);

        return mView;
    }

    // Validate inputted values and add the jcp or dividend to the income table
    private boolean updateOthersIncome() {

        boolean isValidTotal = isValidDouble(mInputOthersTotalView);
        boolean isValidExDate = isValidDate(mInputExDateView);
        boolean isValidTax = isValidDouble(mInputTaxOthers);

        if (isValidExDate && isValidTotal){
            Intent intent = getActivity().getIntent();
            double tax = 0;

            // Get and handle inserted date value
            String inputDate = mInputExDateView.getText().toString();
            // Timestamp to be saved on SQLite database
            Long timestamp = DateToTimestamp(inputDate);

            double receiveValue = Double.parseDouble(mInputOthersTotalView.getText().toString());
            double liquidValue = receiveValue;

            // If it is JCP, needs to calculate the tax and liquid value to be received
            tax = Double.valueOf(mInputTaxOthers.getText().toString());
            liquidValue = receiveValue - tax;

            mLiquidDif = liquidValue - mOldLiquid;
            mTaxDif = tax - mOldTax;

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL, receiveValue);
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_EXDIVIDEND_TIMESTAMP, timestamp);
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_TAX, tax);

            // Updates the database
            String selection = PortfolioContract.OthersIncome._ID + " = ?";
            String[] selectionArguments = {mId};
            int updateQueryCursor = mContext.getContentResolver().update(
                    PortfolioContract.OthersIncome.URI,
                    incomeCV, selection, selectionArguments);
            // If error occurs to add, shows error message
            if (updateQueryCursor > 0) {
                boolean updateOthersDataIncome = updateOthersDataIncome(mSymbol, mLiquidDif, mTaxDif);
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
                mInputExDateView.setError(this.getString(R.string.wrong_date));
            }
            if(!isValidTotal){
                mInputOthersTotalView.setError(this.getString(R.string.wrong_income_per_others));
            }
            if(!isValidTax){
                mInputTaxOthers.setError(this.getString(R.string.wrong_others_tax));
            }
        }
        return false;
    }

    // Update Total Income on Others Data by new income added
    private boolean updateOthersDataIncome(String symbol, double valueReceivedDif, double taxDif){
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
            double totalIncome = dbIncome + valueReceivedDif;
            double totalTax = dbTax + taxDif;

            ContentValues updateCV = new ContentValues();

            updateCV.put(PortfolioContract.OthersData.COLUMN_INCOME, totalIncome);
            updateCV.put(PortfolioContract.OthersData.COLUMN_INCOME_TAX, totalTax);

            int updateQueryCursor = mContext.getContentResolver().update(
                    PortfolioContract.OthersData.URI,
                    updateCV, selection, selectionArguments);
            if (updateQueryCursor == 1){
                mContext.sendBroadcast(new Intent(Constants.Receiver.OTHERS));
                return true;
            }
        }
        return false;
    }

    // Return query cursor of the transaction with that id
    private Cursor getIncomeCursor(String id){
        String selectionData = PortfolioContract.OthersIncome._ID + " = ? ";
        String[] selectionDataArguments = {id};
        Cursor cursor = mContext.getContentResolver().query(
                PortfolioContract.OthersIncome.URI,
                null, selectionData, selectionDataArguments, null);
        return cursor;
    }
}
