package br.com.guiainvestimento.fragment.others;


import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;


public class EditOthersFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = EditOthersFormFragment.class.getSimpleName();
    private View mView;
    private String mSymbol;
    private EditText mInputCurrentTotalView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (updateOthers()) {
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
        mView = inflater.inflate(R.layout.fragment_edit_others_form, container, false);
        mInputCurrentTotalView = (EditText) mView.findViewById(R.id.inputCurrentTotal);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        mSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        getActivity().setTitle(mSymbol
        );
        // Adding input filters
        return mView;
    }

    // Validate inputted values on the portfolio
    private boolean updateOthers() {

        // Validate for each inputted value
        boolean isValidCurrentTotal = isValidDouble(mInputCurrentTotalView);

        // If all validations pass
        if (isValidCurrentTotal) {
            boolean isEmptyCurrentTotal = TextUtils.isEmpty(mInputCurrentTotalView.getText().toString());

            ContentValues othersCV = new ContentValues();

            // Update current price
            int updatedCurrentRows = 0;
            if (!isEmptyCurrentTotal){
                ContentValues currentTotalCV = new ContentValues();
                double currentTotal = Double.parseDouble(mInputCurrentTotalView.getText().toString());
                currentTotalCV.put(mSymbol, currentTotal);
                // Update values on others data
                updatedCurrentRows = mContext.getContentResolver().update(
                        PortfolioContract.OthersData.BULK_UPDATE_URI,
                        currentTotalCV, null, null);
                if (updatedCurrentRows > 0) {
                    // Send Broadcast to update other values on OthersPortfolio
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.OTHERS));
                }
            }

            // If error occurs to add, shows error message
            if ( updatedCurrentRows > 0) {
                Toast.makeText(mContext, R.string.others_update_success, Toast.LENGTH_LONG).show();
                return true;
            }
            Toast.makeText(mContext, R.string.others_update_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {

            if(!isValidCurrentTotal && !TextUtils.isEmpty(mInputCurrentTotalView.getText().toString())){
                mInputCurrentTotalView.setError(this.getString(R.string.wrong_current_total));
            }
        }
        return false;
    }
}
