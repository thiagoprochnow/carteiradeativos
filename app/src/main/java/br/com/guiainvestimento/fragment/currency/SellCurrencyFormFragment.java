package br.com.guiainvestimento.fragment.currency;


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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;
import br.com.guiainvestimento.util.InputFilterDecimal;


public class SellCurrencyFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = SellCurrencyFormFragment.class.getSimpleName();
    private View mView;

    HashMap<String, String> currencyMap = new HashMap<String, String>() {{
        put("Dollar","USD");
        put("Euro","EUR");
        put("Bitcoin","BTC");
        put("USD", "Dollar");
        put("EUR","Euro");
        put("BTC","Bitcoin");
    }};

    private Spinner mInputSymbolView;
    private EditText mInputQuantityView;
    private EditText mInputSellPriceView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (sellCurrency()) {
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
        mView = inflater.inflate(R.layout.fragment_sell_currency_form, container, false);
        getActivity().setTitle(R.string.form_title_sell);
        mInputSymbolView = (Spinner) mView.findViewById(R.id.inputSymbolSpinner);
        mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
        mInputSellPriceView = (EditText) mView.findViewById(R.id.inputSellPrice);
        mInputDateView = (EditText) mView.findViewById(R.id.inputSellDate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.currency_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mInputSymbolView.setAdapter(adapter);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        String intentSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);

        if(intentSymbol != null && !intentSymbol.isEmpty()){
            List<String> myArrayList = Arrays.asList(getResources().getStringArray(R.array.currency_array));
            mInputSymbolView.setSelection(myArrayList.indexOf(currencyMap.get(intentSymbol)));
        }

        // Adding current date to Buy Date field and set Listener to the Spinner
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputDateView.setText(simpleDateFormat.format(new Date()));
        mInputDateView.setOnClickListener(setDatePicker(mInputDateView));

        // Adding input filters
        mInputSellPriceView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        return mView;
    }

    // Validate inputted values and add the Currency to the portfolio
    private boolean sellCurrency() {

        // Aux because common ValidSellQuantity receives a Edit text not a String
        EditText editTextAux = new EditText(mContext);
        editTextAux.setText(currencyMap.get(mInputSymbolView.getSelectedItem().toString()));
        // Validate for each inputted value
        boolean isValidQuantity = isValidSellQuantity(mInputQuantityView, editTextAux, Constants.ProductType.CURRENCY);
        boolean isValidSellPrice = isValidDouble(mInputSellPriceView);
        boolean isValidDate = isValidDate(mInputDateView);
        boolean isFutureDate = isFutureDate(mInputDateView);

        // If all validations pass, try to sell the Currency
        if (isValidQuantity && isValidSellPrice && isValidDate && !isFutureDate) {
            String inputSymbol = currencyMap.get(mInputSymbolView.getSelectedItem().toString());
            double inputQuantity = Double.parseDouble(mInputQuantityView.getText().toString());
            double sellPrice = Double.parseDouble(mInputSellPriceView.getText().toString());
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);

            ContentValues currencyCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            currencyCV.put(PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL, inputSymbol);
            currencyCV.put(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY, inputQuantity);
            currencyCV.put(PortfolioContract.CurrencyTransaction.COLUMN_PRICE, sellPrice);
            currencyCV.put(PortfolioContract.CurrencyTransaction.COLUMN_TIMESTAMP, timestamp);
            currencyCV.put(PortfolioContract.CurrencyTransaction.COLUMN_TYPE, Constants.Type.SELL);
            // Adds to the database
            Uri insertedCurrencyTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .CurrencyTransaction.URI,
                    currencyCV);

            // If error occurs to add, shows error message
            if (insertedCurrencyTransactionUri != null) {
                // Rescan incomes tables to check if added Currency changed their receive values.
                boolean updateCurrencyData = updateCurrencyData(inputSymbol, Constants.Type.SELL);
                if (updateCurrencyData){
                    Toast.makeText(mContext, R.string.sell_currency_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Toast.makeText(mContext, R.string.sell_currency_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            if(!isValidQuantity){
                mInputQuantityView.setError(this.getString(R.string.wrong_currency_sell_quantity));
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
