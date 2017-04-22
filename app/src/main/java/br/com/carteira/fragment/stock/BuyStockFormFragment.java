package br.com.carteira.fragment.stock;


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
import br.com.carteira.util.InputFilterPercentage;


public class BuyStockFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = BuyStockFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputSymbolView;
    private EditText mInputQuantityView;
    private EditText mInputBuyPriceView;
    private EditText mInputObjectiveView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addStock()) {
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
        mView = inflater.inflate(R.layout.fragment_buy_stock_form, container, false);
        getActivity().setTitle(R.string.form_title_buy);
        mInputSymbolView = (EditText) mView.findViewById(R.id.inputSymbol);
        mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
        mInputBuyPriceView = (EditText) mView.findViewById(R.id.inputBuyPrice);
        mInputObjectiveView = (EditText) mView.findViewById(R.id.inputObjective);
        mInputDateView = (EditText) mView.findViewById(R.id.inputBuyDate);

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
        mInputObjectiveView.setFilters(new InputFilter[]{ new InputFilterPercentage("0", "100")});
        mInputBuyPriceView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        return mView;
    }

    // Validate inputted values and add the stock to the portfolio
    private boolean addStock() {

        // Validate for each inputted value
        boolean isValidSymbol = isValidStockSymbol(mInputSymbolView);
        boolean isValidQuantity = isValidInt(mInputQuantityView);
        boolean isValidBuyPrice = isValidDouble(mInputBuyPriceView);
        boolean isValidObjective = isValidPercent(mInputObjectiveView);
        boolean isValidDate = isValidDate(mInputDateView);
        boolean isFutureDate = isFutureDate(mInputDateView);

        // If all validations pass, try to add the stock
        if (isValidSymbol && isValidQuantity && isValidBuyPrice && isValidObjective && isValidDate && !isFutureDate) {
            String inputSymbol = mInputSymbolView.getText().toString();
            int inputQuantity = Integer.parseInt(mInputQuantityView.getText().toString());
            double buyPrice = Double.parseDouble(mInputBuyPriceView.getText().toString());
            double inputObjective = Double.parseDouble(mInputObjectiveView.getText().toString());
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);
            Log.d(LOG_TAG, "InputDate timestamp: " + timestamp);

            ContentValues stockCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            stockCV.put(PortfolioContract.StockTransaction.COLUMN_SYMBOL, inputSymbol);
            stockCV.put(PortfolioContract.StockTransaction.COLUMN_QUANTITY, inputQuantity);
            stockCV.put(PortfolioContract.StockTransaction.COLUMN_PRICE, buyPrice);
            stockCV.put(PortfolioContract.StockTransaction.COLUMN_TIMESTAMP, timestamp);
            stockCV.put(PortfolioContract.StockTransaction.COLUMN_TYPE, Constants.Type.BUY);
            // Adds to the database
            Uri insertedStockTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .StockTransaction.URI,
                    stockCV);

            // If error occurs to add, shows error message
            if (insertedStockTransactionUri != null) {
                Log.d(LOG_TAG, "Added stock transaction " + inputSymbol);
                // Updates each stock table with new value: Income, Data, StockPortfolio, CompletePortfolio
                updateStockIncomes(inputSymbol, timestamp);
                boolean updateStockData = updateStockData(inputSymbol, inputObjective, Constants
                        .Type.BUY);
                if (updateStockData) {
                    Toast.makeText(mContext, R.string.buy_stock_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Toast.makeText(mContext, R.string.buy_stock_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            // If validation fails, show validation error message
            if(!isValidSymbol){
                mInputSymbolView.setError(this.getString(R.string.wrong_stock_code));
            }
            if(!isValidQuantity){
                mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
            }
            if(!isValidBuyPrice){
                mInputBuyPriceView.setError(this.getString(R.string.wrong_price));
            }
            if(!isValidObjective){
                mInputObjectiveView.setError(this.getString(R.string.wrong_percentual_objective));
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
