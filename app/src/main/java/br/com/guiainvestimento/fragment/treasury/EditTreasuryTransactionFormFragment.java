package br.com.guiainvestimento.fragment.treasury;


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

import java.text.SimpleDateFormat;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;


public class EditTreasuryTransactionFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditTreasuryTransactionFormFragment.class.getSimpleName();
    private View mView;
    private String mId;
    private Cursor mTransactionCursor;
    private String mSymbol;
    private int mType;
    private double mQuantity;
    private double mPrice;
    private long mDate;
    private EditText mInputQuantityView;
    private EditText mInputPriceView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateTreasuryTransaction()) {
                    updateTreasuryData(mSymbol, mType);
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
        mTransactionCursor = getTransactionCursor(mId);
        mType = getTransactionType(mTransactionCursor);

        if (mTransactionCursor.moveToFirst()) {
            mQuantity = mTransactionCursor.getDouble(mTransactionCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY));
            mPrice = mTransactionCursor.getDouble(mTransactionCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_PRICE));
            mDate = mTransactionCursor.getLong(mTransactionCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP));
            mSymbol = mTransactionCursor.getString(mTransactionCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL));
        } else{
            getActivity().finish();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        Log.d(LOG_TAG, "mType: " + mType);
        switch (mType){
            case Constants.Type.BUY:
                getActivity().setTitle(getResources().getString(R.string.stock_buy));
                mView = inflater.inflate(R.layout.fragment_edit_treasury_buy_form, container, false);
                mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
                mInputPriceView = (EditText) mView.findViewById(R.id.inputBuyPrice);
                mInputDateView = (EditText) mView.findViewById(R.id.inputBuyDate);
                mInputQuantityView.setText(String.valueOf(mQuantity), EditText.BufferType.EDITABLE);
                mInputPriceView.setText(String.valueOf(mPrice), EditText.BufferType.EDITABLE);
                mInputDateView.setText(simpleDateFormat.format(mDate));
                mInputDateView.setOnClickListener(setDatePicker(mInputDateView));
                break;
            case Constants.Type.SELL:
                getActivity().setTitle(getResources().getString(R.string.stock_sell));
                mView = inflater.inflate(R.layout.fragment_edit_treasury_sell_form, container, false);
                mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
                mInputPriceView = (EditText) mView.findViewById(R.id.inputSellPrice);
                mInputDateView = (EditText) mView.findViewById(R.id.inputSellDate);
                mInputQuantityView.setText(String.valueOf(mQuantity), EditText.BufferType.EDITABLE);
                mInputPriceView.setText(String.valueOf(mPrice), EditText.BufferType.EDITABLE);
                mInputDateView.setText(simpleDateFormat.format(mDate));
                mInputDateView.setOnClickListener(setDatePicker(mInputDateView));
                break;
            default:
                getActivity().finish();
        }
        return mView;
    }

    // Validate inputted values on the form
    private boolean updateTreasuryTransaction() {
        double newQuantity;
        double newPrice;
        String newDate;
        ContentValues updateValues = new ContentValues();
        boolean isValidQuantity = true;
        boolean isValidPrice = true;
        boolean isValidDate = true;

        switch (mType) {
            case Constants.Type.BUY:
                isValidQuantity = isValidDouble(mInputQuantityView);
                isValidPrice = isValidDouble(mInputPriceView);
                isValidDate = isValidDate(mInputDateView);
                if (isValidQuantity){
                    newQuantity = Double.parseDouble(mInputQuantityView.getText().toString());
                    updateValues.put(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY, newQuantity);
                } else {
                    mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
                    return false;
                }

                if (isValidPrice){
                    newPrice = Double.parseDouble(mInputPriceView.getText().toString());
                    updateValues.put(PortfolioContract.TreasuryTransaction.COLUMN_PRICE, newPrice);
                } else {
                    mInputPriceView.setError(this.getString(R.string.wrong_price));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }

                break;
            case Constants.Type.SELL:

                isValidQuantity = isValidDouble(mInputQuantityView);
                isValidPrice = isValidDouble(mInputPriceView);
                isValidDate = isValidDate(mInputDateView);
                if (isValidQuantity){
                    newQuantity = Double.parseDouble(mInputQuantityView.getText().toString());
                    updateValues.put(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY, newQuantity);
                } else {
                    mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
                    return false;
                }

                if (isValidPrice){
                    newPrice = Double.parseDouble(mInputPriceView.getText().toString());
                    updateValues.put(PortfolioContract.TreasuryTransaction.COLUMN_PRICE, newPrice);
                } else {
                    mInputPriceView.setError(this.getString(R.string.wrong_price));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }

                break;
            default:
                return false;
        }

        if (updateValues != null){
            String updateSelection = PortfolioContract.TreasuryTransaction._ID + " = ?";
            String[] updatedSelectionArguments = {mId};
            // Update value on table
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.TreasuryTransaction.URI,
                    updateValues, updateSelection, updatedSelectionArguments);
            return true;
        }
        return false;
    }

    // Return query cursor of the transaction with that id
    private Cursor getTransactionCursor(String id){
        String selectionData = PortfolioContract.TreasuryTransaction._ID + " = ? ";
        String[] selectionDataArguments = {id};
        Cursor cursor = mContext.getContentResolver().query(
                PortfolioContract.TreasuryTransaction.URI,
                null, selectionData, selectionDataArguments, null);
        return cursor;
    }

    // Return the type of transaction (Buy, Sell) to edit it accordingly
    private int getTransactionType(Cursor transactionCursor){
        if (transactionCursor.moveToFirst()){
            return transactionCursor.getInt(transactionCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_TYPE));
        } else{
            return -1;
        }
    }
}
