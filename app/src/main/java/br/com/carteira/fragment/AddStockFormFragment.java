package br.com.carteira.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import br.com.carteira.R;
import br.com.carteira.data.PortfolioContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStockFormFragment extends BaseAddFormFragment {
    private Context mContext;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_stock_form, container, false);
        View buyButton = view.findViewById(R.id.buyButton);
        View cancelButton = view.findViewById(R.id.cancelButton);

        // Sets the action when clicking on the Buy Stock button
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addStock()){
                    getActivity().finish();
                }
            }
        });

        // Sets the action when clickin on the Cancell button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return view;
    }

    // Function to validate inputted values and add the stock to the portfolio
    private boolean addStock(){
        // Parse the information inputted to add the stock
        EditText inputTickerView = (EditText) view.findViewById(R.id.inputTicker);
        EditText inputQuantityView = (EditText) view.findViewById(R.id.inputQuantity);
        EditText inputBuyPriceView = (EditText) view.findViewById(R.id.inputBuyPrice);
        EditText inputObjectiveView = (EditText) view.findViewById(R.id.inputObjective);

        // Validate for each inputted value
        Boolean validateTicker = validateStockTicker(inputTickerView);
        Boolean validateQuantity = validateNotEmpty(inputQuantityView);
        Boolean validateBuyPrice = validateNotEmpty(inputBuyPriceView);
        Boolean validateObjective = validateNotEmpty(inputObjectiveView);

        // If all validations pass, try to add the stock to the portfolio database
        if (validateTicker && validateQuantity && validateBuyPrice && validateObjective) {
            String inputTicker = inputTickerView.getText().toString();
            int inputQuantity = Integer.parseInt(inputQuantityView.getText().toString());
            double buyPrice = Double.parseDouble(inputBuyPriceView.getText().toString());
            double boughtTotal = inputQuantity * buyPrice;
            double inputObjective = Double.parseDouble(inputObjectiveView.getText().toString());

            ContentValues stockCV = new ContentValues();
            stockCV.put(PortfolioContract.StockQuote.COLUMN_SYMBOL, inputTicker);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
