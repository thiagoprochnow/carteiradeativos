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


public class EditStockFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditStockFormFragment.class.getSimpleName();
    private View mView;
    private String mSymbol;
    private EditText mInputObjectiveView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateObjective()) {
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
        mView = inflater.inflate(R.layout.fragment_edit_stock_form, container, false);
        getActivity().setTitle(R.string.form_title_objective);
        mInputObjectiveView = (EditText) mView.findViewById(R.id.inputObjective);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        mSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);

        // Adding input filters
        mInputObjectiveView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        return mView;
    }

    // Validate inputted values and edit the stock objective on the portfolio
    private boolean updateObjective() {

        // Validate for each inputted value
        boolean isValidObjective = isValidDouble(mInputObjectiveView);

        // If all validations pass, try to update the stock objective
        if (isValidObjective) {
            double objective = Double.parseDouble(mInputObjectiveView.getText().toString());

            ContentValues stockCV = new ContentValues();

            stockCV.put(PortfolioContract.StockData.COLUMN_OBJECTIVE_PERCENT, objective);

            String updateSelection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
            String[] updatedSelectionArguments = {mSymbol};

            // Update objective on stock data table
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.StockData.URI,
                    stockCV, updateSelection, updatedSelectionArguments);

            // If error occurs to add, shows error message
            if (updatedRows > 0) {
                Toast.makeText(mContext, R.string.objective_update_success, Toast.LENGTH_LONG).show();
                return true;
            }
            Toast.makeText(mContext, R.string.objective_update_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            // If validation fails, show validation error message
            if(!isValidObjective){
                mInputObjectiveView.setError(this.getString(R.string.wrong_percentual_objective));
            }
        }
        return false;
    }
}
