package br.com.carteira.fragment.stock;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class SplitFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = SplitFormFragment.class.getSimpleName();
    private View mView;

    private String mSymbol;
    private EditText mInputQuantityView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addSplit()) {
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
        mView = inflater.inflate(R.layout.fragment_split_form, container, false);
        getActivity().setTitle(R.string.form_title_split);
        mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
        mInputDateView = (EditText) mView.findViewById(R.id.inputSplitDate);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        mSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        // Place selling stock symbol on field

        // Adding current date to Buy Date field and set Listener to the Spinner
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputDateView.setText(simpleDateFormat.format(new Date()));
        mInputDateView.setOnClickListener(setDatePicker(mInputDateView));

        return mView;
    }

    // Validate inputted values and add the split stock to the transaction table
    private boolean addSplit() {

        // Validate for each inputted value
        boolean isValidQuantity = isValidInt(mInputQuantityView);
        boolean isValidDate = isValidDate(mInputDateView);

        // If all validations pass, try to add the stock
        if (isValidQuantity && isValidDate) {
            String inputSymbol = mSymbol;
            int inputQuantity = Integer.parseInt(mInputQuantityView.getText().toString());
            double buyPrice = 0;
            double inputObjective = -1;
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);
            Log.d(LOG_TAG, "InputDate timestamp: " + timestamp);

            ContentValues stockCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            stockCV.put(PortfolioContract.StockTransaction.COLUMN_SYMBOL, inputSymbol);
            stockCV.put(PortfolioContract.StockTransaction.COLUMN_QUANTITY, inputQuantity);
            stockCV.put(PortfolioContract.StockTransaction.COLUMN_PRICE, 0);
            stockCV.put(PortfolioContract.StockTransaction.COLUMN_TIMESTAMP, timestamp);
            stockCV.put(PortfolioContract.StockTransaction.COLUMN_TYPE, Constants.Type.SPLIT);
            // Adds to the database
            Uri insertedStockTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .StockTransaction.URI,
                    stockCV);

            // If error occurs to add, shows error message
            if (insertedStockTransactionUri != null) {
                Log.d(LOG_TAG, "Added stock split " + inputSymbol);
                // Updates each stock table with new value: Income, Data, StockPortfolio, CompletePortfolio
                updateStockIncomes(inputSymbol, timestamp);
                boolean updateStockData = updateStockData(inputSymbol, inputObjective, Constants
                        .Type.SPLIT);
                if (updateStockData) {
                    Toast.makeText(mContext, R.string.split_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Toast.makeText(mContext, R.string.split_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            // If validation fails, show validation error message
            if(!isValidQuantity){
                mInputQuantityView.setError(this.getString(R.string.wrong_quantity));
            }
            if(!isValidDate){
                mInputDateView.setError(this.getString(R.string.wrong_date));
            }

        }
        return false;
    }
}
