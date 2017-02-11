package br.com.carteira.fragment;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class DividendFormFragment extends BaseFormFragment {
    private View mView;

    private EditText mInputPerStockView;
    private EditText mInputIncomeDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addDividend()) {
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
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_dividend_form, container, false);
        mInputPerStockView = (EditText) mView.findViewById(R.id.inputReceivedPerStock);
        mInputIncomeDateView = (EditText) mView.findViewById(R.id.inputDividendReceiveDate);

        // Adding current date to Buy Date field
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputIncomeDateView.setText(simpleDateFormat.format(new Date()));

        // Configure to show Spinner when clicked on the Date EditText field
        mInputIncomeDateView.setOnClickListener(setDatePicker(mInputIncomeDateView));
        return mView;
    }

    // Validate inputted values and add the dividend to the stock portfolio
    private boolean addDividend() {

        boolean isValidPerStock = isValidDouble(mInputPerStockView);
        boolean isValidReceiveDate = isValidDate(mInputIncomeDateView);

        if (isValidReceiveDate && isValidPerStock){
            Intent intent = getActivity().getIntent();
            String symbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
            // TODO: Needs to get real stock quantity that will receive the income. This will be done by matching the receive ex date with the bought stock date
            int stockQuantity = 100;
            double perStock = Double.parseDouble(mInputPerStockView.getText().toString());
            double totalReceiveValue = stockQuantity * perStock;

            ContentValues stockCV = new ContentValues();
            stockCV.put(PortfolioContract.StockIncome.COLUMN_SYMBOL, symbol);
            // TODO: Type is hardcoded
            String dividend = "Dividend";
            stockCV.put(PortfolioContract.StockIncome.COLUMN_TYPE, dividend);
            stockCV.put(PortfolioContract.StockIncome.COLUMN_PER_STOCK, perStock);
            // TODO: Calculate the percent based on total stocks value that received the income
            stockCV.put(PortfolioContract.StockIncome.COLUMN_PERCENT, "5.32");
            // Adds to the database
            Uri insertedUri = mContext.getContentResolver().insert(PortfolioContract.StockIncome.URI,
                    stockCV);
            // If error occurs to add, shows error message
            if (insertedUri != null) {
                Toast.makeText(mContext, R.string.add_dividend_success, Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(mContext, R.string.add_dividend_fail, Toast.LENGTH_SHORT).show();
            }

        } else{
            // If validation fails, show validation error message
            if(!isValidReceiveDate){
                mInputIncomeDateView.setError(this.getString(R.string.wrong_date));
            }
            if(!isValidPerStock){
                mInputPerStockView.setError(this.getString(R.string.wrong_income_per_stock));
            }
        }
        return false;
    }

}
