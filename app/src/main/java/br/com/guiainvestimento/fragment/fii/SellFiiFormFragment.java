package br.com.guiainvestimento.fragment.fii;


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


public class SellFiiFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = SellFiiFormFragment.class.getSimpleName();
    private View mView;

    private AutoCompleteTextView mInputSymbolView;
    private EditText mInputQuantityView;
    private EditText mInputSellPriceView;
    private EditText mInputDateView;
    private EditText mInputBrokerage;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (sellFii()) {
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
        mView = inflater.inflate(R.layout.fragment_sell_fii_form, container, false);
        getActivity().setTitle(R.string.form_title_sell);
        mInputSymbolView = (AutoCompleteTextView) mView.findViewById(R.id.inputSymbol);
        mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
        mInputSellPriceView = (EditText) mView.findViewById(R.id.inputSellPrice);
        mInputBrokerage = (EditText) mView.findViewById(R.id.inputBrokerage);
        mInputDateView = (EditText) mView.findViewById(R.id.inputSellDate);

        // Sets autocomplete for stock symbol
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_dropdown_item_1line, Constants.Symbols.FII);
        mInputSymbolView.setAdapter(adapter);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        String intentSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        // Place selling fii symbol on field
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

    // Validate inputted values and add the fii to the portfolio
    private boolean sellFii() {

        // Validate for each inputted value
        boolean isValidSymbol = isValidFiiSymbol(mInputSymbolView);
        boolean isValidQuantity;
        String inputSymbol = "";
        if (isValidSymbol){
            inputSymbol = mInputSymbolView.getText().toString();
            isValidQuantity = isValidSellQuantity(mInputQuantityView, inputSymbol, Constants.ProductType.FII);
        } else {
            isValidQuantity = false;
        }
        boolean isValidBrokerage = isValidDouble(mInputBrokerage);
        boolean isValidSellPrice = isValidDouble(mInputSellPriceView);
        boolean isValidDate = isValidDate(mInputDateView);
        boolean isFutureDate = isFutureDate(mInputDateView);

        // If all validations pass, try to sell the fii
        if (isValidSymbol && isValidQuantity && isValidBrokerage && isValidSellPrice && isValidDate && !isFutureDate) {
            int inputQuantity = Integer.parseInt(mInputQuantityView.getText().toString());
            double sellPrice = Double.parseDouble(mInputSellPriceView.getText().toString());
            double brokerage = Double.parseDouble(mInputBrokerage.getText().toString());
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);

            ContentValues fiiCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_SYMBOL, inputSymbol);
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_QUANTITY, inputQuantity);
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_PRICE, sellPrice);
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP, timestamp);
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_TYPE, Constants.Type.SELL);
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_BROKERAGE, brokerage);
            // Adds to the database
            Uri insertedFiiTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .FiiTransaction.URI,
                    fiiCV);

            // If error occurs to add, shows error message
            if (insertedFiiTransactionUri != null) {
                // Rescan incomes tables to check if added fii changed their receive values.
                updateFiiIncomes(inputSymbol, timestamp);
                boolean updateFiiData = updateFiiData(inputSymbol, Constants.Type.SELL);
                if (updateFiiData){
                    Toast.makeText(mContext, R.string.sell_fii_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Toast.makeText(mContext, R.string.sell_fii_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            // If validation fails, show validation error message
            if(!isValidSymbol){
                mInputSymbolView.setError(this.getString(R.string.wrong_fii_code));
            }
            if(!isValidQuantity){
                mInputQuantityView.setError(this.getString(R.string.wrong_fii_sell_quantity));
            }
            if(!isValidBrokerage){
                 mInputBrokerage.setError(this.getString(R.string.wrong_brokerage));
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
