package br.com.carteira.fragment.fixedincome;


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


public class EditFixedFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditFixedFormFragment.class.getSimpleName();
    private View mView;
    private String mSymbol;
    private EditText mInputObjectiveView;
    private EditText mInputCurrentTotalView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateFixed()) {
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
        mView = inflater.inflate(R.layout.fragment_edit_fixed_form, container, false);
        mInputObjectiveView = (EditText) mView.findViewById(R.id.inputObjective);
        mInputCurrentTotalView = (EditText) mView.findViewById(R.id.inputCurrentTotal);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        mSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        getActivity().setTitle(mSymbol
        );
        // Adding input filters
        mInputObjectiveView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        return mView;
    }

    // Validate inputted values and edit the fixed income objective on the portfolio
    private boolean updateFixed() {

        // Validate for each inputted value
        boolean isValidObjective = isValidDouble(mInputObjectiveView);
        boolean isValidCurrentTotal = isValidDouble(mInputCurrentTotalView);

        // If all validations pass, try to update the fixed income objective
        if (isValidObjective || isValidCurrentTotal) {
            boolean isEmptyObjective = TextUtils.isEmpty(mInputObjectiveView.getText().toString());
            boolean isEmptyCurrentTotal = TextUtils.isEmpty(mInputCurrentTotalView.getText().toString());

            ContentValues fixedCV = new ContentValues();

            // Update objective
            int updatedRows = 0;
            if (!isEmptyObjective) {
                double objective = Double.parseDouble(mInputObjectiveView.getText().toString());
                fixedCV.put(PortfolioContract.FixedData.COLUMN_OBJECTIVE_PERCENT, objective);
                String updateSelection = PortfolioContract.FixedData.COLUMN_SYMBOL + " = ?";
                String[] updatedSelectionArguments = {mSymbol};

                // Update objective on fixed data table
                updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.FixedData.URI,
                        fixedCV, updateSelection, updatedSelectionArguments);
            }
            // Update current price
            int updatedCurrentRows = 0;
            if (!isEmptyCurrentTotal){
                ContentValues currentTotalCV = new ContentValues();
                double currentTotal = Double.parseDouble(mInputCurrentTotalView.getText().toString());
                currentTotalCV.put(mSymbol, currentTotal);
                // Update values on fixed data
                updatedCurrentRows = mContext.getContentResolver().update(
                        PortfolioContract.FixedData.BULK_UPDATE_URI,
                        currentTotalCV, null, null);
                if (updatedCurrentRows > 0) {
                    // Send Broadcast to update other values on FixedPortfolio
                    mContext.sendBroadcast(new Intent(Constants.Receiver.FIXED));
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.FIXED));
                }
            }

            // If error occurs to add, shows error message
            if (updatedRows > 0 || updatedCurrentRows > 0) {
                Toast.makeText(mContext, R.string.fixed_update_success, Toast.LENGTH_LONG).show();
                return true;
            }
            Toast.makeText(mContext, R.string.fixed_update_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (TextUtils.isEmpty(mInputObjectiveView.getText().toString()) &&
                    TextUtils.isEmpty(mInputCurrentTotalView.getText().toString())){
                return true;
            }
            // If validation fails, show validation error message
            if(!isValidObjective && !TextUtils.isEmpty(mInputObjectiveView.getText().toString())){
                mInputObjectiveView.setError(this.getString(R.string.wrong_percentual_objective));
            }
            if(!isValidCurrentTotal && !TextUtils.isEmpty(mInputCurrentTotalView.getText().toString())){
                mInputCurrentTotalView.setError(this.getString(R.string.wrong_current_total));
            }
        }
        return false;
    }
}
