package br.com.carteira.fragment;


import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.carteira.R;
import br.com.carteira.data.PortfolioContract;


public class AddStockFormFragment extends BaseAddFormFragment {
    private View mView;

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
        EditText inputDateView = (EditText) mView.findViewById(R.id.inputBuyDate);
        // Adding current date to Buy Date field
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        inputDateView.setText(simpleDateFormat.format(new Date()));
        // Configure to show Spinner when clicked on the Date EditText field
        inputDateView.setOnClickListener(setDatePicker(inputDateView));
        return mView;
    }

    // Function to validate inputted values and add the stock to the portfolio
    private boolean addStock() {
        // Parse the information inputted to add the stock
        EditText inputSymbolView = (EditText) mView.findViewById(R.id.inputSymbol);
        EditText inputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
        EditText inputBuyPriceView = (EditText) mView.findViewById(R.id.inputBuyPrice);
        EditText inputObjectiveView = (EditText) mView.findViewById(R.id.inputObjective);

        // Validate for each inputted value
        /* TODO: For now, we are only validating if this is empty or not.
         We should create more robust methods to validate it.*/
        boolean isValidSymbol = !isEditTextEmpty(inputSymbolView);
        boolean isValidQuantity = !isEditTextEmpty(inputQuantityView);
        boolean isValidBuyPrice = !isEditTextEmpty(inputBuyPriceView);
        boolean isValidObjective = !isEditTextEmpty(inputObjectiveView);

        // If all validations pass, try to add the stock to the portfolio database
        if (isValidSymbol && isValidQuantity && isValidBuyPrice && isValidObjective) {
            String inputSymbol = inputSymbolView.getText().toString();
            int inputQuantity = Integer.parseInt(inputQuantityView.getText().toString());
            double buyPrice = Double.parseDouble(inputBuyPriceView.getText().toString());
            double boughtTotal = inputQuantity * buyPrice;
            double inputObjective = Double.parseDouble(inputObjectiveView.getText().toString());

            ContentValues stockCV = new ContentValues();
            stockCV.put(PortfolioContract.StockQuote.COLUMN_SYMBOL, inputSymbol);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_QUANTITY, inputQuantity);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_BOUGHT_TOTAL, boughtTotal);
            stockCV.put(PortfolioContract.StockQuote.COLUMN_OBJECTIVE_PERCENT, inputObjective);
            // Adds to the database
            Uri insertedUri = mContext.getContentResolver().insert(PortfolioContract.StockQuote.URI,
                    stockCV);
            // If error occurs to add, shows error message
            if (insertedUri != null) {
                Toast.makeText(mContext, R.string.add_stock_success, Toast.LENGTH_SHORT);
                return true;
            } else {
                Toast.makeText(mContext, R.string.add_stock_fail, Toast.LENGTH_SHORT);
            }
        } else {
            // If validation fails, show validation error message
            Toast.makeText(getContext(), R.string.wrong_inputs, Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
