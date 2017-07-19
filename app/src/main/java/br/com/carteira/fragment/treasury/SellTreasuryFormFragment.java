package br.com.carteira.fragment.treasury;


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

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFormFragment;
import br.com.carteira.util.InputFilterDecimal;


public class SellTreasuryFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = SellTreasuryFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputSymbolView;
    private EditText mInputQuantityView;
    private EditText mInputSellPriceView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (sellTreasury()) {
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
        mView = inflater.inflate(R.layout.fragment_sell_treasury_form, container, false);
        getActivity().setTitle(R.string.form_title_sell);
        mInputSymbolView = (EditText) mView.findViewById(R.id.inputSymbol);
        mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
        mInputSellPriceView = (EditText) mView.findViewById(R.id.inputSellPrice);
        mInputDateView = (EditText) mView.findViewById(R.id.inputSellDate);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        String intentSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        // Place selling treasury symbol on field
        if(intentSymbol != null && !intentSymbol.isEmpty()){
            mInputSymbolView.setText(intentSymbol);
        }

        // Adding current date to Buy Date field and set Listener to the Spinner
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputDateView.setText(simpleDateFormat.format(new Date()));
        mInputDateView.setOnClickListener(setDatePicker(mInputDateView));

        // Adding input filters
        mInputSellPriceView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        return mView;
    }

    // Validate inputted values and add the treasury to the portfolio
    private boolean sellTreasury() {

        // Validate for each inputted value
        boolean isValidSymbol = isValidTreasurySymbol(mInputSymbolView);
        boolean isValidQuantity = isValidSellQuantity(mInputQuantityView, mInputSymbolView, Constants.ProductType.TREASURY);
        boolean isValidSellPrice = isValidDouble(mInputSellPriceView);
        boolean isValidDate = isValidDate(mInputDateView);
        boolean isFutureDate = isFutureDate(mInputDateView);

        // If all validations pass, try to sell the treasury
        if (isValidSymbol && isValidQuantity && isValidSellPrice && isValidDate && !isFutureDate) {
            String inputSymbol = mInputSymbolView.getText().toString();
            double inputQuantity = Double.parseDouble(mInputQuantityView.getText().toString());
            double sellPrice = Double.parseDouble(mInputSellPriceView.getText().toString());
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);
            Log.d(LOG_TAG, "InputDate timestamp: " + timestamp);

            ContentValues treasuryCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            treasuryCV.put(PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL, inputSymbol);
            treasuryCV.put(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY, inputQuantity);
            treasuryCV.put(PortfolioContract.TreasuryTransaction.COLUMN_PRICE, sellPrice);
            treasuryCV.put(PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP, timestamp);
            treasuryCV.put(PortfolioContract.TreasuryTransaction.COLUMN_TYPE, Constants.Type.SELL);
            // Adds to the database
            Uri insertedTreasuryTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .TreasuryTransaction.URI,
                    treasuryCV);

            // If error occurs to add, shows error message
            if (insertedTreasuryTransactionUri != null) {
                // Rescan incomes tables to check if added treasury changed their receive values.
                updateTreasuryIncomes(inputSymbol, timestamp);
                boolean updateTreasuryData = updateTreasuryData(inputSymbol, Constants.Type.SELL);
                if (updateTreasuryData){
                    Toast.makeText(mContext, R.string.sell_treasury_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Toast.makeText(mContext, R.string.sell_treasury_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            // If validation fails, show validation error message
            if(!isValidSymbol){
                mInputSymbolView.setError(this.getString(R.string.wrong_treasury_code));
            }
            if(!isValidQuantity){
                mInputQuantityView.setError(this.getString(R.string.wrong_treasury_sell_quantity));
            }
            if(!isValidSellPrice){
                mInputSellPriceView.setError(this.getString(R.string.wrong_price));
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
