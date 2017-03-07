package br.com.carteira.fragment;


import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.carteira.R;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.util.InputFilterDecimal;
import br.com.carteira.util.InputFilterPercentage;


public class AddStockFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = AddStockFormFragment.class.getSimpleName();
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
        mView = inflater.inflate(R.layout.fragment_add_stock_form, container, false);

        mInputSymbolView = (EditText) mView.findViewById(R.id.inputSymbol);
        mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
        mInputBuyPriceView = (EditText) mView.findViewById(R.id.inputBuyPrice);
        mInputObjectiveView = (EditText) mView.findViewById(R.id.inputObjective);
        mInputDateView = (EditText) mView.findViewById(R.id.inputBuyDate);

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

        // If all validations pass, try to add the stock to the portfolio database
        if (isValidSymbol && isValidQuantity && isValidBuyPrice && isValidObjective && isValidDate) {
            String inputSymbol = mInputSymbolView.getText().toString();
            int inputQuantity = Integer.parseInt(mInputQuantityView.getText().toString());
            double buyPrice = Double.parseDouble(mInputBuyPriceView.getText().toString());
            double inputObjective = Double.parseDouble(mInputObjectiveView.getText().toString());
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);
            Log.d(LOG_TAG, "InputDate timestamp: " + timestamp);

            ContentValues stockCV = new ContentValues();

            long symbolId = addSymbol(inputSymbol);
            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            stockCV.put(PortfolioContract.StockQuote.COLUMN_SYMBOL, inputSymbol);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_QUANTITY, inputQuantity);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_BOUGHT_PRICE, buyPrice);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_OBJECTIVE_PERCENT, inputObjective);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_TIMESTAMP, timestamp);
            // Adds to the database
            Uri insertedStockQuoteUri = mContext.getContentResolver().insert(PortfolioContract.StockQuote.URI,
                    stockCV);

            // If error occurs to add, shows error message
            if (insertedStockQuoteUri != null) {
                Toast.makeText(mContext, R.string.add_stock_success, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(mContext, R.string.add_stock_fail, Toast.LENGTH_SHORT).show();
            }
        } else {
            // If validation fails, show validation error message
            if(!isValidSymbol){
                mInputSymbolView.setError(this.getString(R.string.wrong_stock_code));
            }
            if(!isValidQuantity){
                mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
            }
            if(!isValidBuyPrice){
                mInputBuyPriceView.setError(this.getString(R.string.wrong_buy_price));
            }
            if(!isValidObjective){
                mInputObjectiveView.setError(this.getString(R.string.wrong_percentual_objective));
            }
            if(!isValidDate){
                mInputDateView.setError(this.getString(R.string.wrong_date));
            }

        }
        return false;
    }

    /**
     * Add the symbol if it not exists otherwise just return the ID of the symbol
     * @param symbol
     * @return - Id of the register
     */
    long addSymbol(String symbol) {
        long symbolId;

        // Check if the symbol exists in the db
        Cursor symbolCursor = getContext().getContentResolver().query(
                PortfolioContract.StockSymbol.URI,
                new String[]{PortfolioContract.StockSymbol._ID},
                PortfolioContract.StockSymbol.COLUMN_SYMBOL + " = ?",
                new String[]{symbol},
                null);

        if (symbolCursor.moveToFirst()) {
            int symbolIdIndex = symbolCursor.getColumnIndex(PortfolioContract.StockSymbol._ID);
            symbolId = symbolCursor.getLong(symbolIdIndex);
        } else {
            // Prepare the symbol values to be inserted
            ContentValues symbolValues = new ContentValues();
            symbolValues.put(PortfolioContract.StockSymbol.COLUMN_SYMBOL, symbol);

            // Insert into database.
            Uri insertedStockSymbolUri = getContext().getContentResolver()
                    .insert(PortfolioContract.StockSymbol.URI, symbolValues);

            // Extract the symbolId from the Uri.
            symbolId = ContentUris.parseId(insertedStockSymbolUri);
        }

        symbolCursor.close();
        return symbolId;
    }
}
