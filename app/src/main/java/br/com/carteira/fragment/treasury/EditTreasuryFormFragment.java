package br.com.carteira.fragment.treasury;


import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFormFragment;
import br.com.carteira.util.InputFilterDecimal;


public class EditTreasuryFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditTreasuryFormFragment.class.getSimpleName();
    private View mView;
    private String mSymbol;
    private EditText mInputObjectiveView;
    private EditText mInputCurrentPriceView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateTreasury()) {
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
        mView = inflater.inflate(R.layout.fragment_edit_treasury_form, container, false);
        mInputObjectiveView = (EditText) mView.findViewById(R.id.inputObjective);
        mInputCurrentPriceView = (EditText) mView.findViewById(R.id.inputCurrentPrice);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        mSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        getActivity().setTitle(mSymbol);
        // Adding input filters
        mInputObjectiveView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        return mView;
    }

    // Validate inputted values and edit the treasury objective on the portfolio
    private boolean updateTreasury() {

        // Validate for each inputted value
        boolean isValidObjective = isValidDouble(mInputObjectiveView);
        boolean isValidCurrentPrice = isValidDouble(mInputCurrentPriceView);

        // If all validations pass, try to update the treasury objective
        if (isValidObjective || isValidCurrentPrice) {
            boolean isEmptyObjective = TextUtils.isEmpty(mInputObjectiveView.getText().toString());
            boolean isEmptyCurrentPrice = TextUtils.isEmpty(mInputCurrentPriceView.getText().toString());

            ContentValues treasuryCV = new ContentValues();

            // Update objective
            int updatedRows = 0;
            if (!isEmptyObjective) {
                double objective = Double.parseDouble(mInputObjectiveView.getText().toString());
                treasuryCV.put(PortfolioContract.TreasuryData.COLUMN_OBJECTIVE_PERCENT, objective);
                String updateSelection = PortfolioContract.TreasuryData.COLUMN_SYMBOL + " = ?";
                String[] updatedSelectionArguments = {mSymbol};

                // Update objective on treasury data table
                updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.TreasuryData.URI,
                        treasuryCV, updateSelection, updatedSelectionArguments);
            }
            // Update current price
            int updatedCurrentRows = 0;
            if (!isEmptyCurrentPrice){
                ContentValues currentPriceCV = new ContentValues();
                double currentPrice = Double.parseDouble(mInputCurrentPriceView.getText().toString());
                currentPriceCV.put(mSymbol, currentPrice);
                // Update values on treasury data
                updatedCurrentRows = mContext.getContentResolver().update(
                        PortfolioContract.TreasuryData.BULK_UPDATE_URI,
                        currentPriceCV, null, null);
                if (updatedCurrentRows > 0) {
                    // Send Broadcast to update other values on TreasuryPortfolio
                    mContext.sendBroadcast(new Intent(Constants.Receiver.TREASURY));
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.TREASURY));
                }
            }

            // If error occurs to add, shows error message
            if (updatedRows > 0 || updatedCurrentRows > 0) {
                Toast.makeText(mContext, R.string.treasury_update_success, Toast.LENGTH_LONG).show();
                return true;
            }
            Toast.makeText(mContext, R.string.treasury_update_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (TextUtils.isEmpty(mInputObjectiveView.getText().toString()) &&
                    TextUtils.isEmpty(mInputCurrentPriceView.getText().toString())){
                return true;
            }
            // If validation fails, show validation error message
            if(!isValidObjective && !TextUtils.isEmpty(mInputObjectiveView.getText().toString())){
                mInputObjectiveView.setError(this.getString(R.string.wrong_percentual_objective));
            }
            if(!isValidCurrentPrice && !TextUtils.isEmpty(mInputCurrentPriceView.getText().toString())){
                mInputCurrentPriceView.setError(this.getString(R.string.wrong_current_price));
            }
        }
        return false;
    }
}
