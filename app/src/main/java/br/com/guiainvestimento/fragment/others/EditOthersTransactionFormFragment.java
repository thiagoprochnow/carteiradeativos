package br.com.guiainvestimento.fragment.others;


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


public class EditOthersTransactionFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditOthersTransactionFormFragment.class.getSimpleName();
    private View mView;
    private String mId;
    private Cursor mTransactionCursor;
    private String mSymbol;
    private int mType;
    private double mTotal;
    private long mDate;
    private EditText mInputTotalView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateOthersTransaction()) {
                    updateOthersData(mSymbol, -1);
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
            mTotal = mTransactionCursor.getDouble(mTransactionCursor.getColumnIndex(PortfolioContract.OthersTransaction.COLUMN_TOTAL));
            mDate = mTransactionCursor.getLong(mTransactionCursor.getColumnIndex(PortfolioContract.OthersTransaction.COLUMN_TIMESTAMP));
            mSymbol = mTransactionCursor.getString(mTransactionCursor.getColumnIndex(PortfolioContract.OthersTransaction.COLUMN_SYMBOL));
        } else{
            getActivity().finish();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        Log.d(LOG_TAG, "mType: " + mType);
        switch (mType){
            case Constants.Type.BUY:
                getActivity().setTitle(getResources().getString(R.string.stock_buy));
                mView = inflater.inflate(R.layout.fragment_edit_others_buy_form, container, false);
                mInputTotalView = (EditText) mView.findViewById(R.id.inputBuyTotal);
                mInputDateView = (EditText) mView.findViewById(R.id.inputBuyDate);
                mInputTotalView.setText(String.valueOf(mTotal), EditText.BufferType.EDITABLE);
                mInputDateView.setText(simpleDateFormat.format(mDate));
                mInputDateView.setOnClickListener(setDatePicker(mInputDateView));
                break;
            case Constants.Type.SELL:
                getActivity().setTitle(getResources().getString(R.string.stock_sell));
                mView = inflater.inflate(R.layout.fragment_edit_others_sell_form, container, false);
                mInputTotalView = (EditText) mView.findViewById(R.id.inputSellTotal);
                mInputDateView = (EditText) mView.findViewById(R.id.inputSellDate);
                mInputTotalView.setText(String.valueOf(mTotal), EditText.BufferType.EDITABLE);
                mInputDateView.setText(simpleDateFormat.format(mDate));
                mInputDateView.setOnClickListener(setDatePicker(mInputDateView));
                break;
            default:
                getActivity().finish();
        }
        return mView;
    }

    // Validate inputted values on the form
    private boolean updateOthersTransaction() {
        double newTotal;
        String newDate;
        ContentValues updateValues = new ContentValues();
        boolean isValidTotal = true;
        boolean isValidDate = true;

        switch (mType) {
            case Constants.Type.BUY:
                isValidTotal = isValidDouble(mInputTotalView);
                isValidDate = isValidDate(mInputDateView);

                if (isValidTotal){
                    newTotal = Double.parseDouble(mInputTotalView.getText().toString());
                    updateValues.put(PortfolioContract.OthersTransaction.COLUMN_TOTAL, newTotal);
                } else {
                    mInputTotalView.setError(this.getString(R.string.wrong_total));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.OthersTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }

                break;
            case Constants.Type.SELL:

                isValidTotal = isValidDouble(mInputTotalView);
                isValidDate = isValidDate(mInputDateView);

                if (isValidTotal){
                    newTotal = Double.parseDouble(mInputTotalView.getText().toString());
                    updateValues.put(PortfolioContract.OthersTransaction.COLUMN_TOTAL, newTotal);
                } else {
                    mInputTotalView.setError(this.getString(R.string.wrong_total));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.OthersTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }

                break;
            default:
                return false;
        }

        if (updateValues != null){
            String updateSelection = PortfolioContract.OthersTransaction._ID + " = ?";
            String[] updatedSelectionArguments = {mId};
            // Update value on table
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.OthersTransaction.URI,
                    updateValues, updateSelection, updatedSelectionArguments);
            return true;
        }
        return false;
    }

    // Return query cursor of the transaction with that id
    private Cursor getTransactionCursor(String id){
        String selectionData = PortfolioContract.OthersTransaction._ID + " = ? ";
        String[] selectionDataArguments = {id};
        Cursor cursor = mContext.getContentResolver().query(
                PortfolioContract.OthersTransaction.URI,
                null, selectionData, selectionDataArguments, null);
        return cursor;
    }

    // Return the type of transaction (Buy, Sell) to edit it accordingly
    private int getTransactionType(Cursor transactionCursor){
        if (transactionCursor.moveToFirst()){
            return transactionCursor.getInt(transactionCursor.getColumnIndex(PortfolioContract.OthersTransaction.COLUMN_TYPE));
        } else{
            return -1;
        }
    }
}
