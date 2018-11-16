package br.com.guiainvestimento.fragment.fii;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;

public class FiiGroupingFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = FiiGroupingFormFragment.class.getSimpleName();
    private View mView;

    private String mSymbol;
    private EditText mInputQuantityView;
    private EditText mInputDateView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addGrouping()) {
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
        mView = inflater.inflate(R.layout.fragment_grouping_form, container, false);
        getActivity().setTitle(R.string.form_title_grouping);
        mInputQuantityView = (EditText) mView.findViewById(R.id.inputQuantity);
        mInputDateView = (EditText) mView.findViewById(R.id.inputGroupingDate);

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

    // Validate inputted values and add the grouping stock to the transaction table
    private boolean addGrouping() {

        // Validate for each inputted value
        boolean isValidQuantity = isValidDouble(mInputQuantityView);
        boolean isValidDate = isValidDate(mInputDateView);

        // If all validations pass, try to add the stock
        if (isValidQuantity && isValidDate) {
            String inputSymbol = mSymbol;
            double inputQuantity = Double.parseDouble(mInputQuantityView.getText().toString());
            double buyPrice = 0;
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);

            ContentValues fiiCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_SYMBOL, inputSymbol);
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_QUANTITY, inputQuantity);
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_PRICE, 0);
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP, timestamp);
            fiiCV.put(PortfolioContract.FiiTransaction.COLUMN_TYPE, Constants.Type.GROUPING);
            // Adds to the database
            Uri insertedStockTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .FiiTransaction.URI,
                    fiiCV);

            // If error occurs to add, shows error message
            if (insertedStockTransactionUri != null) {
                // Updates each stock table with new value: Income, Data, StockPortfolio, CompletePortfolio
                updateFiiIncomes(inputSymbol, timestamp);
                boolean updateFiiData = updateFiiData(inputSymbol, Constants
                        .Type.GROUPING);
                if (updateFiiData) {
                    Toast.makeText(mContext, R.string.grouping_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Toast.makeText(mContext, R.string.grouping_fail, Toast.LENGTH_LONG).show();
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
