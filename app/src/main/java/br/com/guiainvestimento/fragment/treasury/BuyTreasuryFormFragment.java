package br.com.guiainvestimento.fragment.treasury;


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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;
import br.com.guiainvestimento.util.InputFilterDecimal;


public class BuyTreasuryFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = BuyTreasuryFormFragment.class.getSimpleName();
    private View mView;

    private AutoCompleteTextView mInputSymbolView;
    private EditText mInputQuantityView;
    private EditText mInputBuyPriceView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addTreasury()) {
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
        mView = inflater.inflate(R.layout.fragment_buy_treasury_form, container, false);
        getActivity().setTitle(R.string.form_title_buy);
        mInputSymbolView = (AutoCompleteTextView) mView.findViewById(R.id.inputSymbol);
        mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
        mInputBuyPriceView = (EditText) mView.findViewById(R.id.inputBuyPrice);
        mInputDateView = (EditText) mView.findViewById(R.id.inputBuyDate);

        // Sets autocomplete for stock symbol
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                R.layout.dropdown_custom, Constants.Symbols.TREASURY);
        mInputSymbolView.setAdapter(adapter);

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
        mInputBuyPriceView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        return mView;
    }

    // Validate inputted values and add the treasury to the portfolio
    private boolean addTreasury() {

        // Validate for each inputted value
        boolean isValidSymbol = isValidTreasurySymbol(mInputSymbolView);
        boolean isValidQuantity = isValidDouble(mInputQuantityView);
        boolean isValidBuyPrice = isValidDouble(mInputBuyPriceView);
        boolean isValidDate = isValidDate(mInputDateView);
        boolean isFutureDate = isFutureDate(mInputDateView);

        // If all validations pass, try to add the treasury
        if (isValidSymbol && isValidQuantity && isValidBuyPrice && isValidDate && !isFutureDate) {
            String inputSymbol = mInputSymbolView.getText().toString();
            double inputQuantity = Double.parseDouble(mInputQuantityView.getText().toString());
            double buyPrice = Double.parseDouble(mInputBuyPriceView.getText().toString());
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);

            ContentValues treasuryCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            treasuryCV.put(PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL, inputSymbol);
            treasuryCV.put(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY, inputQuantity);
            treasuryCV.put(PortfolioContract.TreasuryTransaction.COLUMN_PRICE, buyPrice);
            treasuryCV.put(PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP, timestamp);
            treasuryCV.put(PortfolioContract.TreasuryTransaction.COLUMN_TYPE, Constants.Type.BUY);
            // Adds to the database
            Uri insertedTreasuryTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .TreasuryTransaction.URI,
                    treasuryCV);

            // If error occurs to add, shows error message
            if (insertedTreasuryTransactionUri != null) {
                // Updates each treasury table with new value: Income, Data, TreasuryPortfolio, CompletePortfolio
                updateTreasuryIncomes(inputSymbol, timestamp);
                boolean updateTreasuryData = updateTreasuryData(inputSymbol, Constants
                        .Type.BUY);
                if (updateTreasuryData) {
                    Toast.makeText(mContext, R.string.buy_treasury_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Toast.makeText(mContext, R.string.buy_treasury_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            // If validation fails, show validation error message
            if(!isValidSymbol){
                mInputSymbolView.setError(this.getString(R.string.wrong_treasury_code));
            }
            if(!isValidQuantity){
                mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
            }
            if(!isValidBuyPrice){
                mInputBuyPriceView.setError(this.getString(R.string.wrong_price));
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
