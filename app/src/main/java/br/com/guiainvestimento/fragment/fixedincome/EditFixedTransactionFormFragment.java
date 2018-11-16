package br.com.guiainvestimento.fragment.fixedincome;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;


public class EditFixedTransactionFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditFixedTransactionFormFragment.class.getSimpleName();
    private View mView;
    private String mId;
    private Cursor mTransactionCursor;
    private String mSymbol;
    private double mGainRate;
    private int mType;
    private double mTotal;
    private long mDate;
    private EditText mInputTotalView;
    private EditText mInputDateView;
    private EditText mInputGainRate;
    private Spinner mInputGainTypeView;
    private TextView mGainRateLabelView;
    private int mGainType = Constants.FixedType.CDI;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateFixedTransaction()) {
                    updateFixedData(mSymbol, -1);
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
            mTotal = mTransactionCursor.getDouble(mTransactionCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TOTAL));
            mDate = mTransactionCursor.getLong(mTransactionCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TIMESTAMP));
            mSymbol = mTransactionCursor.getString(mTransactionCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_SYMBOL));
            mGainRate = mTransactionCursor.getDouble(mTransactionCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_GAIN_RATE));
            mGainType = mTransactionCursor.getInt(mTransactionCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_GAIN_TYPE));
        } else{
            getActivity().finish();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        Log.d(LOG_TAG, "mType: " + mType);
        switch (mType){
            case Constants.Type.BUY:
                getActivity().setTitle(getResources().getString(R.string.stock_buy));
                mView = inflater.inflate(R.layout.fragment_edit_fixed_buy_form, container, false);
                mInputTotalView = (EditText) mView.findViewById(R.id.inputBuyTotal);
                mInputDateView = (EditText) mView.findViewById(R.id.inputBuyDate);
                mInputGainRate = (EditText) mView.findViewById(R.id.inputGainRate);
                mInputGainTypeView = (Spinner) mView.findViewById(R.id.inputType);
                mGainRateLabelView = (TextView) mView.findViewById(R.id.gainRateLabel);

                String[] tipos = new String[]{"CDI","IPCA","Pré Fixado"};


                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item,tipos);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                mInputGainTypeView.setAdapter(adapter);

                mInputGainTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0){
                            mGainType = Constants.FixedType.CDI;
                            mGainRateLabelView.setText(R.string.fixed_gain_rate);
                            mInputGainRate.setHint(R.string.fixed_gain_rate_hint);
                        } else if(position == 1){
                            mGainType = Constants.FixedType.IPCA;
                            mGainRateLabelView.setText(R.string.fixed_gain_rate_ipca);
                            mInputGainRate.setHint(R.string.fixed_gain_rate_ipca_hint);
                        } else {
                            mGainType = Constants.FixedType.PRE;
                            mGainRateLabelView.setText(R.string.fixed_gain_rate_pre);
                            mInputGainRate.setHint(R.string.fixed_gain_rate_pre_hint);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                mInputGainTypeView.setSelection(mGainType);

                if (mGainRate != 0){
                    double gainRate = mGainRate*100;
                    gainRate = (double) Math.round(gainRate * 100) / 100;
                    mInputGainRate.setText(String.valueOf(gainRate), EditText.BufferType.EDITABLE);
                }
                mInputTotalView.setText(String.valueOf(mTotal), EditText.BufferType.EDITABLE);
                mInputDateView.setText(simpleDateFormat.format(mDate));
                mInputDateView.setOnClickListener(setDatePicker(mInputDateView));
                break;
            case Constants.Type.SELL:
                getActivity().setTitle(getResources().getString(R.string.stock_sell));
                mView = inflater.inflate(R.layout.fragment_edit_fixed_sell_form, container, false);
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
    private boolean updateFixedTransaction() {
        double newTotal;
        String newDate;
        double newGainRate;
        ContentValues updateValues = new ContentValues();
        boolean isValidTotal = true;
        boolean isValidDate = true;

        switch (mType) {
            case Constants.Type.BUY:
                isValidTotal = isValidDouble(mInputTotalView);
                isValidDate = isValidDate(mInputDateView);
                boolean isValidGainRate = isValidDouble(mInputGainRate);

                if (isValidTotal){
                    newTotal = Double.parseDouble(mInputTotalView.getText().toString());
                    updateValues.put(PortfolioContract.FixedTransaction.COLUMN_TOTAL, newTotal);
                } else {
                    mInputTotalView.setError(this.getString(R.string.wrong_total));
                    return false;
                }

                if (isValidGainRate){
                    newGainRate = Double.parseDouble(mInputGainRate.getText().toString())/100;
                    updateValues.put(PortfolioContract.FixedTransaction.COLUMN_GAIN_RATE, newGainRate);
                } else {
                    mInputGainRate.setError(this.getString(R.string.wrong_gain_rate));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.FixedTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }
                updateValues.put(PortfolioContract.FixedTransaction.COLUMN_GAIN_TYPE, mGainType);

                break;
            case Constants.Type.SELL:

                isValidTotal = isValidDouble(mInputTotalView);
                isValidDate = isValidDate(mInputDateView);

                if (isValidTotal){
                    newTotal = Double.parseDouble(mInputTotalView.getText().toString());
                    updateValues.put(PortfolioContract.FixedTransaction.COLUMN_TOTAL, newTotal);
                } else {
                    mInputTotalView.setError(this.getString(R.string.wrong_total));
                    return false;
                }

                if (isValidDate){
                    newDate = mInputDateView.getText().toString();
                    Long timestamp = DateToTimestamp(newDate);
                    updateValues.put(PortfolioContract.FixedTransaction.COLUMN_TIMESTAMP, timestamp);
                } else {
                    mInputDateView.setError(this.getString(R.string.wrong_date));
                    return false;
                }

                break;
            default:
                return false;
        }

        if (updateValues != null){
            String updateSelection = PortfolioContract.FixedTransaction._ID + " = ?";
            String[] updatedSelectionArguments = {mId};
            // Update value on table
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.FixedTransaction.URI,
                    updateValues, updateSelection, updatedSelectionArguments);
            return true;
        }
        return false;
    }

    // Return query cursor of the transaction with that id
    private Cursor getTransactionCursor(String id){
        String selectionData = PortfolioContract.FixedTransaction._ID + " = ? ";
        String[] selectionDataArguments = {id};
        Cursor cursor = mContext.getContentResolver().query(
                PortfolioContract.FixedTransaction.URI,
                null, selectionData, selectionDataArguments, null);
        return cursor;
    }

    // Return the type of transaction (Buy, Sell) to edit it accordingly
    private int getTransactionType(Cursor transactionCursor){
        if (transactionCursor.moveToFirst()){
            return transactionCursor.getInt(transactionCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TYPE));
        } else{
            return -1;
        }
    }
}
