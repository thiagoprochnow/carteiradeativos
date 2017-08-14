package br.com.guiainvestimento.fragment.others;


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


public class SellOthersFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = SellOthersFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputSymbolView;
    private EditText mInputSellTotalView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (sellOthers()) {
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
        mView = inflater.inflate(R.layout.fragment_sell_others_form, container, false);
        getActivity().setTitle(R.string.form_title_sell);
        mInputSymbolView = (EditText) mView.findViewById(R.id.inputSymbol);
        mInputSellTotalView = (EditText) mView.findViewById(R.id.inputSellTotal);
        mInputDateView = (EditText) mView.findViewById(R.id.inputSellDate);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        String intentSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        // Place selling others symbol on field
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

    // Validate inputted values and add the others to the portfolio
    private boolean sellOthers() {

        // Validate for each inputted value
        boolean isValidSymbol = isValidOthersSymbol(mInputSymbolView);
        boolean isValidSellTotal = isValidSellOthers(mInputSellTotalView, mInputSymbolView);
        boolean isValidDate = isValidDate(mInputDateView);
        boolean isFutureDate = isFutureDate(mInputDateView);

        // If all validations pass, try to sell the others income
        if (isValidSymbol && isValidSellTotal && isValidDate && !isFutureDate) {
            String inputSymbol = mInputSymbolView.getText().toString();
            double sellTotal = Double.parseDouble(mInputSellTotalView.getText().toString());
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);
            Log.d(LOG_TAG, "InputDate timestamp: " + timestamp);

            ContentValues othersCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            othersCV.put(PortfolioContract.OthersTransaction.COLUMN_SYMBOL, inputSymbol);
            othersCV.put(PortfolioContract.OthersTransaction.COLUMN_TOTAL, sellTotal);
            othersCV.put(PortfolioContract.OthersTransaction.COLUMN_TIMESTAMP, timestamp);
            othersCV.put(PortfolioContract.OthersTransaction.COLUMN_TYPE, Constants.Type.SELL);
            // Adds to the database
            Uri insertedOthersTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .OthersTransaction.URI,
                    othersCV);

            // If error occurs to add, shows error message
            if (insertedOthersTransactionUri != null) {
                // Rescan incomes tables to check if added others income changed their receive values.
                boolean updateOthersData = updateOthersData(inputSymbol, Constants.Type.SELL);
                if (updateOthersData){
                    Toast.makeText(mContext, R.string.sell_others_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Toast.makeText(mContext, R.string.sell_others_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            // If validation fails, show validation error message
            if(!isValidSymbol){
                mInputSymbolView.setError(this.getString(R.string.wrong_others_code));
            }
            if(!isValidSellTotal){
                mInputSellTotalView.setError(this.getString(R.string.wrong_others_sell_quantity));
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
