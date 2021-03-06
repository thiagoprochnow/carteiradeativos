package br.com.guiainvestimento.fragment.fixedincome;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
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
import br.com.guiainvestimento.util.InputFilterDecimal;


public class SellFixedFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = SellFixedFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputSymbolView;
    private EditText mInputSellTotalView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (sellFixed()) {
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
        mView = inflater.inflate(R.layout.fragment_sell_fixed_form, container, false);
        getActivity().setTitle(R.string.form_title_sell);
        mInputSymbolView = (EditText) mView.findViewById(R.id.inputSymbol);
        mInputSellTotalView = (EditText) mView.findViewById(R.id.inputSellTotal);
        mInputDateView = (EditText) mView.findViewById(R.id.inputSellDate);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        String intentSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        // Place selling fixed symbol on field
        if(intentSymbol != null && !intentSymbol.isEmpty()){
            mInputSymbolView.setText(intentSymbol);
        }

        // Adding current date to Buy Date field and set Listener to the Spinner
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputDateView.setText(simpleDateFormat.format(new Date()));
        mInputDateView.setOnClickListener(setDatePicker(mInputDateView));

        // Adding input filters
        mInputSellTotalView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        return mView;
    }

    // Validate inputted values and add the fixed to the portfolio
    private boolean sellFixed() {

        // Validate for each inputted value
        boolean isValidSymbol = isValidFixedSymbol(mInputSymbolView);
        boolean isValidSellTotal = isValidSellFixed(mInputSellTotalView, mInputSymbolView);
        boolean isValidDate = isValidDate(mInputDateView);
        boolean isFutureDate = isFutureDate(mInputDateView);

        // If all validations pass, try to sell the fixed income
        if (isValidSymbol && isValidSellTotal && isValidDate && !isFutureDate) {
            String inputSymbol = mInputSymbolView.getText().toString();
            double sellTotal = Double.parseDouble(mInputSellTotalView.getText().toString());
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);

            ContentValues fixedCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            fixedCV.put(PortfolioContract.FixedTransaction.COLUMN_SYMBOL, inputSymbol);
            fixedCV.put(PortfolioContract.FixedTransaction.COLUMN_TOTAL, sellTotal);
            fixedCV.put(PortfolioContract.FixedTransaction.COLUMN_TIMESTAMP, timestamp);
            fixedCV.put(PortfolioContract.FixedTransaction.COLUMN_TYPE, Constants.Type.SELL);
            // Adds to the database
            Uri insertedFixedTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .FixedTransaction.URI,
                    fixedCV);

            // If error occurs to add, shows error message
            if (insertedFixedTransactionUri != null) {
                // Rescan incomes tables to check if added fixed income changed their receive values.
                boolean updateFixedData = updateFixedData(inputSymbol, Constants.Type.SELL);
                if (updateFixedData){
                    Toast.makeText(mContext, R.string.sell_fixed_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Toast.makeText(mContext, R.string.sell_fixed_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            // If validation fails, show validation error message
            if(!isValidSymbol){
                mInputSymbolView.setError(this.getString(R.string.wrong_fixed_code));
            }
            if(!isValidSellTotal){
                mInputSellTotalView.setError(this.getString(R.string.wrong_fixed_sell_quantity));
            }
            if(!isValidDate){
                mInputDateView.setError(this.getString(R.string.wrong_date));
                Toast.makeText(mContext, R.string.wrong_date, Toast.LENGTH_LONG).show();
            }
            if(isFutureDate){
                mInputDateView.setError(this.getString(R.string.future_date));
                Toast.makeText(mContext, R.string.future_date, Toast.LENGTH_LONG).show();
            }

        }
        return false;
    }
}
