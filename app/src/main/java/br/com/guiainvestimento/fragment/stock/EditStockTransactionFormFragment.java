package br.com.guiainvestimento.fragment.stock;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;


public class EditStockTransactionFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditStockTransactionFormFragment.class.getSimpleName();
    private View mView;
    private String mId;
    private Cursor mTransactionCursor;
    private String mSymbol;
    private int mType;
    private int mQuantity;
    private double mPrice;
    private double mBrokerage;
    private long mDate;
    private EditText mInputQuantityView;
    private EditText mInputPriceView;
    private EditText mInputDateView;
    private EditText mInputBrokerage;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateStockTransaction()) {
                    updateStockData(mSymbol, mType);
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
            mQuantity = mTransactionCursor.getInt(mTransactionCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
            mPrice = mTransactionCursor.getDouble(mTransactionCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
            mBrokerage = mTransactionCursor.getDouble(mTransactionCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_BROKERAGE));
            mDate = mTransactionCursor.getLong(mTransactionCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TIMESTAMP));
            mSymbol = mTransactionCursor.getString(mTransactionCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_SYMBOL));
        } else{
            getActivity().finish();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        switch (mType){
            case Constants.Type.BUY:
                getActivity().setTitle(getResources().getString(R.string.stock_buy));
                mView = inflater.inflate(R.layout.fragment_edit_stock_buy_form, container, false);
                mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
                mInputPriceView = (EditText) mView.findViewById(R.id.inputBuyPrice);
                mInputBrokerage = (EditText) mView.findViewById(R.id.inputBrokerage);
                mInputDateView = (EditText) mView.findViewById(R.id.inputBuyDate);
                mInputQuantityView.setText(String.valueOf(mQuantity), EditText.BufferType.EDITABLE);
                mInputPriceView.setText(String.valueOf(mPrice), EditText.BufferType.EDITABLE);
                mInputBrokerage.setText(String.valueOf(mBrokerage), EditText.BufferType.EDITABLE);
                mInputDateView.setText(simpleDateFormat.format(mDate));
                mInputDateView.setOnClickListener(setDatePicker(mInputDateView));
                break;
            case Constants.Type.SELL:
                getActivity().setTitle(getResources().getString(R.string.stock_sell));
                mView = inflater.inflate(R.layout.fragment_edit_stock_sell_form, container, false);
                mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
                mInputPriceView = (EditText) mView.findViewById(R.id.inputSellPrice);
                mInputBrokerage = (EditText) mView.findViewById(R.id.inputBrokerage);
                mInputDateView = (EditText) mView.findViewById(R.id.inputSellDate);
                mInputQuantityView.setText(String.valueOf(mQuantity), EditText.BufferType.EDITABLE);
                mInputPriceView.setText(String.valueOf(mPrice), EditText.BufferType.EDITABLE);
                mInputBrokerage.setText(String.valueOf(mBrokerage), EditText.BufferType.EDITABLE);
                mInputDateView.setText(simpleDateFormat.format(mDate));
                mInputDateView.setOnClickListener(setDatePicker(mInputDateView));
                break;
            case Constants.Type.BONIFICATION:
                getActivity().setTitle(getResources().getString(R.string.stock_bonification));
                mView = inflater.inflate(R.layout.fragment_edit_stock_bonification_form, container, false);
                mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
                mInputDateView = (EditText) mView.findViewById(R.id.inputBonificationDate);
                mInputQuantityView.setText(String.valueOf(mQuantity), EditText.BufferType.EDITABLE);
                mInputDateView.setText(simpleDateFormat.format(mDate));
                mInputDateView.setOnClickListener(setDatePicker(mInputDateView));
                break;
            case Constants.Type.GROUPING:
                getActivity().setTitle(getResources().getString(R.string.stock_grouping));
                mView = inflater.inflate(R.layout.fragment_edit_stock_grouping_form, container, false);
                mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
                mInputDateView = (EditText) mView.findViewById(R.id.inputGroupingDate);
                mInputQuantityView.setText(String.valueOf(mQuantity), EditText.BufferType.EDITABLE);
                mInputDateView.setText(simpleDateFormat.format(mDate));
                mInputDateView.setOnClickListener(setDatePicker(mInputDateView));
                break;
            case Constants.Type.SPLIT:
                getActivity().setTitle(getResources().getString(R.string.stock_split));
                mView = inflater.inflate(R.layout.fragment_edit_stock_split_form, container, false);
                mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
                mInputDateView = (EditText) mView.findViewById(R.id.inputSplitDate);
                mInputQuantityView.setText(String.valueOf(mQuantity), EditText.BufferType.EDITABLE);
                mInputDateView.setText(simpleDateFormat.format(mDate));
                mInputDateView.setOnClickListener(setDatePicker(mInputDateView));
                break;
            default:
                getActivity().finish();
        }
        return mView;
    }

    // Validate inputted values on the form
    private boolean updateStockTransaction() {
        int newQuantity;
        double newPrice;
        String newDate;
        double newBrokerage;
        ContentValues updateValues = new ContentValues();
        boolean isValidQuantity = true;
        boolean isValidPrice = true;
        boolean isValidDate = true;
        boolean isValidBrokerage = true;

        switch (mType) {
            case Constants.Type.BUY:
                isValidQuantity = isValidInt(mInputQuantityView);
                isValidPrice = isValidDouble(mInputPriceView);
                isValidDate = isValidDate(mInputDateView);
                isValidBrokerage = isValidDouble(mInputBrokerage);
                if (isValidQuantity){
                    newQuantity = Integer.parseInt(mInputQuantityView.getText().toString());
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_QUANTITY, newQuantity);
                } else {
                    mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
                    return false;
                }

                if (isValidPrice){
                    newPrice = Double.parseDouble(mInputPriceView.getText().toString());
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_PRICE, newPrice);
                } else {
                    mInputPriceView.setError(this.getString(R.string.wrong_price));
                    return false;
                }

                if (isValidBrokerage){
                    newBrokerage = Double.parseDouble(mInputBrokerage.getText().toString());
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_BROKERAGE, newBrokerage);
                } else {
                    mInputPriceView.setError(this.getString(R.string.wrong_brokerage));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }

                break;
            case Constants.Type.SELL:

                isValidQuantity = isValidInt(mInputQuantityView);
                isValidPrice = isValidDouble(mInputPriceView);
                isValidDate = isValidDate(mInputDateView);
                isValidBrokerage = isValidDouble(mInputBrokerage);
                if (isValidQuantity){
                    newQuantity = Integer.parseInt(mInputQuantityView.getText().toString());
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_QUANTITY, newQuantity);
                } else {
                    mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
                    return false;
                }

                if (isValidPrice){
                    newPrice = Double.parseDouble(mInputPriceView.getText().toString());
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_PRICE, newPrice);
                } else {
                    mInputPriceView.setError(this.getString(R.string.wrong_price));
                    return false;
                }

                if (isValidBrokerage){
                    newBrokerage = Double.parseDouble(mInputBrokerage.getText().toString());
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_BROKERAGE, newBrokerage);
                } else {
                    mInputPriceView.setError(this.getString(R.string.wrong_brokerage));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }

                break;
            case Constants.Type.BONIFICATION:

                isValidQuantity = isValidInt(mInputQuantityView);
                isValidDate = isValidDate(mInputDateView);
                if (isValidQuantity){
                    newQuantity = Integer.parseInt(mInputQuantityView.getText().toString());
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_QUANTITY, newQuantity);
                } else {
                    mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }
                break;
            case Constants.Type.GROUPING:

                isValidQuantity = isValidInt(mInputQuantityView);
                isValidDate = isValidDate(mInputDateView);
                if (isValidQuantity){
                    newQuantity = Integer.parseInt(mInputQuantityView.getText().toString());
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_QUANTITY, newQuantity);
                } else {
                    mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }
                break;
            case Constants.Type.SPLIT:

                isValidQuantity = isValidInt(mInputQuantityView);
                isValidDate = isValidDate(mInputDateView);
                if (isValidQuantity){
                    newQuantity = Integer.parseInt(mInputQuantityView.getText().toString());
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_QUANTITY, newQuantity);
                } else {
                    mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.StockTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }
                break;
            default:
                return false;
        }

        if (updateValues != null){
            String updateSelection = PortfolioContract.StockTransaction._ID + " = ?";
            String[] updatedSelectionArguments = {mId};
            // Update value on table
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.StockTransaction.URI,
                    updateValues, updateSelection, updatedSelectionArguments);
            return true;
        }
        return false;
    }

    // Return query cursor of the transaction with that id
    private Cursor getTransactionCursor(String id){
        String selectionData = PortfolioContract.StockTransaction._ID + " = ? ";
        String[] selectionDataArguments = {id};
        Cursor cursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI,
                null, selectionData, selectionDataArguments, null);
        return cursor;
    }

    // Return the type of transaction (Buy, Sell, Bonification, Split, Grouping) to edit it accordingly
    private int getTransactionType(Cursor transactionCursor){
        if (transactionCursor.moveToFirst()){
            return transactionCursor.getInt(transactionCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE));
        } else{
            return -1;
        }
    }
}
