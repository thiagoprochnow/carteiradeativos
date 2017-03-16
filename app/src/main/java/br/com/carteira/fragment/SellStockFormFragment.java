package br.com.carteira.fragment;


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
import br.com.carteira.util.InputFilterDecimal;


public class SellStockFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = SellStockFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputSymbolView;
    private EditText mInputQuantityView;
    private EditText mInputSellPriceView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (sellStock()) {
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
        mView = inflater.inflate(R.layout.fragment_sell_stock_form, container, false);

        mInputSymbolView = (EditText) mView.findViewById(R.id.inputSymbol);
        mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
        mInputSellPriceView = (EditText) mView.findViewById(R.id.inputSellPrice);
        mInputDateView = (EditText) mView.findViewById(R.id.inputSellDate);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        String intentSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        // Place selling stock symbol on field
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

    // Validate inputted values and add the stock to the portfolio
    private boolean sellStock() {

        // Validate for each inputted value
        boolean isValidSymbol = isValidStockSymbol(mInputSymbolView);
        boolean isValidQuantity = isValidSellQuantity(mInputQuantityView, mInputSymbolView);
        boolean isValidSellPrice = isValidDouble(mInputSellPriceView);
        boolean isValidDate = isValidDate(mInputDateView);

        // If all validations pass, try to sell the stock to the portfolio database
        if (isValidSymbol && isValidQuantity && isValidSellPrice && isValidDate) {
            String inputSymbol = mInputSymbolView.getText().toString();
            int inputQuantity = Integer.parseInt(mInputQuantityView.getText().toString());
            double buyPrice = Double.parseDouble(mInputSellPriceView.getText().toString());
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);
            Log.d(LOG_TAG, "InputDate timestamp: " + timestamp);

            ContentValues stockCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            stockCV.put(PortfolioContract.StockQuote.COLUMN_SYMBOL, inputSymbol);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_QUANTITY, inputQuantity);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_PRICE, buyPrice);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_TIMESTAMP, timestamp);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_STATUS, Constants.Status.SELL);
            // Adds to the database
            Uri insertedStockQuoteUri = mContext.getContentResolver().insert(PortfolioContract.StockQuote.URI,
                    stockCV);

            // If error occurs to add, shows error message
            if (insertedStockQuoteUri != null) {
                // Rescan incomes tables to check if added stock changed their receive values.
                double sumReceiveIncome = updateStockIncomes(inputSymbol, timestamp);
                Toast.makeText(mContext, R.string.sell_stock_success, Toast.LENGTH_SHORT).show();
                boolean updateStockPortfolio = updateStockPortfolio(inputSymbol, inputQuantity, buyPrice, -1, sumReceiveIncome, Constants.Status.SELL);
                if (updateStockPortfolio){
                    Toast.makeText(mContext, R.string.sell_stock_success, Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    Toast.makeText(mContext, R.string.sell_stock_fail, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, R.string.sell_stock_fail, Toast.LENGTH_SHORT).show();
            }
        } else {
            // If validation fails, show validation error message
            if(!isValidSymbol){
                mInputSymbolView.setError(this.getString(R.string.wrong_stock_code));
            }
            if(!isValidQuantity){
                mInputQuantityView.setError(this.getString(R.string.wrong_sell_quantity));
            }
            if(!isValidSellPrice){
                mInputSellPriceView.setError(this.getString(R.string.wrong_price));
            }
            if(!isValidDate){
                mInputDateView.setError(this.getString(R.string.wrong_date));
            }

        }
        return false;
    }
}
