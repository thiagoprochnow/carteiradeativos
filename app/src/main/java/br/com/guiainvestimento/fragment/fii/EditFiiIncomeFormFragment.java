package br.com.guiainvestimento.fragment.fii;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.api.service.FiiIntentService;
import br.com.guiainvestimento.api.service.StockIntentService;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;


public class EditFiiIncomeFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditFiiIncomeFormFragment.class.getSimpleName();
    private View mView;
    private String mId;
    private Cursor mIncomeCursor;
    private String mSymbol;
    private int mIncomeType;
    private double mPerFii;
    private double mOldLiquid;
    private double mLiquidDif;
    private long mDate;
    private EditText mInputPerFiiView;
    private EditText mInputExDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateFiiIncome()) {
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
            mPerFii = mIncomeCursor.getDouble(mIncomeCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_PER_FII));
            mDate = mIncomeCursor.getLong(mIncomeCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
            mSymbol = mIncomeCursor.getString(mIncomeCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_SYMBOL));
            mOldLiquid = mIncomeCursor.getDouble(mIncomeCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID));
        } else{
            getActivity().finish();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        getActivity().setTitle(getResources().getString(R.string.menu_edit));
        mView = inflater.inflate(R.layout.fragment_fii_income_edit_form, container, false);
        mInputPerFiiView = (EditText) mView.findViewById(R.id.inputReceivedPerFii);
        mInputPerFiiView.setText(String.valueOf(mPerFii), EditText.BufferType.EDITABLE);
        mInputExDateView = (EditText) mView.findViewById(R.id.inputFiiIncomeExDate);
        mInputExDateView.setText(simpleDateFormat.format(mDate));
        mInputExDateView.setOnClickListener(setDatePicker(mInputExDateView));

        return mView;
    }

    // Validate inputted values and add the income to the income table
    private boolean updateFiiIncome() {

        boolean isValidPerFii = isValidDouble(mInputPerFiiView);
        boolean isValidExDate = isValidDate(mInputExDateView);

        if (isValidExDate && isValidPerFii){
            Intent intent = getActivity().getIntent();

            // Get and handle inserted date value
            String inputDate = mInputExDateView.getText().toString();
            // Timestamp to be saved on SQLite database
            Long timestamp = DateToTimestamp(inputDate);

            // Get the fii quantity bought before the income is ex
            // Will be used to calculate the total R$ received of income
            double fiiQuantity = getFiiQuantity(mSymbol, timestamp);
            double perFii = Double.parseDouble(mInputPerFiiView.getText().toString());
            double receiveValue = fiiQuantity * perFii;
            double liquidValue = receiveValue;

            mLiquidDif = liquidValue - mOldLiquid;

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_PER_FII, perFii);
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP, timestamp);
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_AFFECTED_QUANTITY, fiiQuantity);
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_RECEIVE_TOTAL, receiveValue);
            incomeCV.put(PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID, liquidValue);

            // Updates the database
            String selection = PortfolioContract.FiiIncome._ID + " = ?";
            String[] selectionArguments = {mId};
            int updateQueryCursor = mContext.getContentResolver().update(
                    PortfolioContract.FiiIncome.URI,
                    incomeCV, selection, selectionArguments);
            // If error occurs to add, shows error message
            if (updateQueryCursor > 0) {
                boolean updateFiiDataIncome = updateFiiDataIncome(mSymbol, mLiquidDif, 0);
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
                mInputPerFiiView.setError(this.getString(R.string.wrong_income_per_stock));
            }
        }
        return false;
    }

    // Update Total Income on Fii Data by new income added
    private boolean updateFiiDataIncome(String symbol, double valueReceivedDif, double taxDif){
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
            double totalIncome = dbIncome + valueReceivedDif;

            ContentValues updateCV = new ContentValues();

            updateCV.put(PortfolioContract.FiiData.COLUMN_INCOME, totalIncome);

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

    // Return query cursor of the transaction with that id
    private Cursor getIncomeCursor(String id){
        String selectionData = PortfolioContract.FiiIncome._ID + " = ? ";
        String[] selectionDataArguments = {id};
        Cursor cursor = mContext.getContentResolver().query(
                PortfolioContract.FiiIncome.URI,
                null, selectionData, selectionDataArguments, null);
        return cursor;
    }
}
