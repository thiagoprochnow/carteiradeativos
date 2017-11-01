package br.com.guiainvestimento.fragment.stock;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import br.com.guiainvestimento.api.service.StockIntentService;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;


public class EditJCPDividendFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditJCPDividendFormFragment.class.getSimpleName();
    private View mView;
    private String mId;
    private Cursor mIncomeCursor;
    private String mSymbol;
    private int mIncomeType;
    private double mPerStock;
    private double mOldTax;
    private double mOldLiquid;
    private double mTaxDif;
    private double mLiquidDif;
    private long mDate;
    private EditText mInputPerStockView;
    private EditText mInputExDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateJCPDividendIncome()) {
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
            mPerStock = mIncomeCursor.getDouble(mIncomeCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_PER_STOCK));
            mDate = mIncomeCursor.getLong(mIncomeCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
            mSymbol = mIncomeCursor.getString(mIncomeCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_SYMBOL));
            mOldLiquid = mIncomeCursor.getDouble(mIncomeCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID));
            mOldTax = mIncomeCursor.getDouble(mIncomeCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TAX));
        } else{
            getActivity().finish();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        getActivity().setTitle(getResources().getString(R.string.menu_edit));
        mView = inflater.inflate(R.layout.fragment_jcp_dividend_edit_form, container, false);
        mInputPerStockView = (EditText) mView.findViewById(R.id.inputReceivedPerStock);
        mInputPerStockView.setText(String.valueOf(mPerStock), EditText.BufferType.EDITABLE);
        mInputExDateView = (EditText) mView.findViewById(R.id.inputJCPDividendExDate);
        mInputExDateView.setText(simpleDateFormat.format(mDate));
        mInputExDateView.setOnClickListener(setDatePicker(mInputExDateView));

        return mView;
    }

    // Validate inputted values and add the jcp or dividend to the income table
    private boolean updateJCPDividendIncome() {

        boolean isValidPerStock = isValidDouble(mInputPerStockView);
        boolean isValidExDate = isValidDate(mInputExDateView);

        if (isValidExDate && isValidPerStock){
            Intent intent = getActivity().getIntent();
            double tax = 0;

            // Get and handle inserted date value
            String inputDate = mInputExDateView.getText().toString();
            // Timestamp to be saved on SQLite database
            Long timestamp = DateToTimestamp(inputDate);

            // Get the stock quantity bought before the jcp/dividend is ex
            // Will be used to calculate the total R$ received of jcp/dividend
            int stockQuantity = getStockQuantity(mSymbol, timestamp);
            double perStock = Double.parseDouble(mInputPerStockView.getText().toString());
            double receiveValue = stockQuantity * perStock;
            double liquidValue = receiveValue;

            // If it is JCP, needs to calculate the tax and liquid value to be received
            if(mIncomeType == Constants.IncomeType.JCP){
                tax = calculateTax(receiveValue);
                liquidValue = receiveValue - tax;
            }

            mLiquidDif = liquidValue - mOldLiquid;
            mTaxDif = tax - mOldTax;

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_PER_STOCK, perStock);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP, timestamp);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_AFFECTED_QUANTITY, stockQuantity);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_TOTAL, receiveValue);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_TAX, tax);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID, liquidValue);

            // Updates the database
            String selection = PortfolioContract.StockIncome._ID + " = ?";
            String[] selectionArguments = {mId};
            int updateQueryCursor = mContext.getContentResolver().update(
                    PortfolioContract.StockIncome.URI,
                    incomeCV, selection, selectionArguments);
            // If error occurs to add, shows error message
            if (updateQueryCursor > 0) {
                boolean updateStockDataIncome = updateStockDataIncome(mSymbol, mLiquidDif, mTaxDif);
                if (updateStockDataIncome) {
                    if (mIncomeType == Constants.IncomeType.DIVIDEND) {
                        Toast.makeText(mContext, R.string.add_dividend_success, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(mContext, R.string.add_jcp_success, Toast.LENGTH_LONG)
                                .show();
                    }
                    return true;
                }
            }

            if (mIncomeType == Constants.IncomeType.DIVIDEND) {
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
    private boolean updateStockDataIncome(String symbol, double valueReceivedDif, double taxDif){
        // Prepare query to update stock data income
        // and the total income received
        String selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            double dbIncome = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_NET_INCOME));
            double dbTax = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_INCOME_TAX));
            double totalIncome = dbIncome + valueReceivedDif;
            double totalTax = dbTax + taxDif;

            ContentValues updateCV = new ContentValues();

            updateCV.put(PortfolioContract.StockData.COLUMN_NET_INCOME, totalIncome);
            updateCV.put(PortfolioContract.StockData.COLUMN_INCOME_TAX, totalTax);

            int updateQueryCursor = mContext.getContentResolver().update(
                    PortfolioContract.StockData.URI,
                    updateCV, selection, selectionArguments);
            if (updateQueryCursor == 1){
                Intent mServiceIntent = new Intent(mContext, StockIntentService
                        .class);
                mServiceIntent.putExtra(StockIntentService.ADD_SYMBOL, symbol);
                getActivity().startService(mServiceIntent);
                return true;
            }
        }
        return false;
    }

    // Return query cursor of the transaction with that id
    private Cursor getIncomeCursor(String id){
        String selectionData = PortfolioContract.StockIncome._ID + " = ? ";
        String[] selectionDataArguments = {id};
        Cursor cursor = mContext.getContentResolver().query(
                PortfolioContract.StockIncome.URI,
                null, selectionData, selectionDataArguments, null);
        return cursor;
    }
}
