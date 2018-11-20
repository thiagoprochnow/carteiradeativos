package br.com.guiainvestimento.fragment.fund;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;
import br.com.guiainvestimento.util.InputFilterDecimal;


public class BuyFundFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = BuyFundFormFragment.class.getSimpleName();
    private View mView;

    private EditText mInputSymbolView;
    private EditText mInputBuyTotalView;
    private EditText mInputDateView;
    private EditText mInputGainRateView;
    private Spinner mInputGainTypeView;
    private TextView mGainRateLabelView;
    private int mType = Constants.FundType.CDI;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addFund()) {
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
        mView = inflater.inflate(R.layout.fragment_buy_fund_form, container, false);
        getActivity().setTitle(R.string.form_title_buy);
        mInputSymbolView = (EditText) mView.findViewById(R.id.inputSymbol);
        mInputBuyTotalView = (EditText) mView.findViewById(R.id.inputBuyTotal);
        mInputDateView = (EditText) mView.findViewById(R.id.inputBuyDate);
        mInputGainRateView = (EditText) mView.findViewById(R.id.inputGainRate);
        mInputGainTypeView = (Spinner) mView.findViewById(R.id.inputType);
        mGainRateLabelView = (TextView) mView.findViewById(R.id.gainRateLabel);

        String[] tipos = new String[]{"CDI","IPCA","Pré Fixado"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_spinner_dropdown_item,tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mInputGainTypeView.setAdapter(adapter);

        mInputGainTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    mType = Constants.FundType.CDI;
                    mGainRateLabelView.setText(R.string.fund_gain_rate);
                    mInputGainRateView.setHint(R.string.fund_gain_rate_hint);
                } else if(position == 1){
                    mType = Constants.FundType.IPCA;
                    mGainRateLabelView.setText(R.string.fund_gain_rate_ipca);
                    mInputGainRateView.setHint(R.string.fund_gain_rate_ipca_hint);
                } else {
                    mType = Constants.FundType.PRE;
                    mGainRateLabelView.setText(R.string.fund_gain_rate_pre);
                    mInputGainRateView.setHint(R.string.fund_gain_rate_pre_hint);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        String intentSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        // Place selling fund symbol on field
        if(intentSymbol != null && !intentSymbol.isEmpty()){
            mInputSymbolView.setText(intentSymbol);
        }

        // Adding current date to Buy Date field and set Listener to the Spinner
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputDateView.setText(simpleDateFormat.format(new Date()));
        mInputDateView.setOnClickListener(setDatePicker(mInputDateView));

        // Adding input filters
        mInputBuyTotalView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        mInputGainRateView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        return mView;
    }

    // Validate inputted values and add the fund to the portfolio
    private boolean addFund() {

        // Validate for each inputted value
        boolean isValidSymbol = isValidFundSymbol(mInputSymbolView);
        boolean isValidBuyTotal = isValidDouble(mInputBuyTotalView);
        boolean isValidGainRate = isValidDouble(mInputGainRateView);
        boolean isValidDate = isValidDate(mInputDateView);
        boolean isFutureDate = isFutureDate(mInputDateView);

        // If all validations pass, try to add the fund income
        if (isValidSymbol && isValidBuyTotal && isValidDate && !isFutureDate && isValidGainRate) {
            String inputSymbol = mInputSymbolView.getText().toString();
            double buyTotal = Double.parseDouble(mInputBuyTotalView.getText().toString());
            double gainRate = Double.parseDouble(mInputGainRateView.getText().toString())/100;
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);

            ContentValues fundCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_SYMBOL, inputSymbol);
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_TOTAL, buyTotal);
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_TIMESTAMP, timestamp);
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_GAIN_RATE, gainRate);
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_TYPE, Constants.Type.BUY);
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_GAIN_TYPE, mType);

            // Adds to the database
            Uri insertedFundTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .FundTransaction.URI,
                    fundCV);

            // If error occurs to add, shows error message
            if (insertedFundTransactionUri != null) {
                // Updates each fund income table with new value: Income, Data, FundPortfolio, CompletePortfolio
                boolean updateFundData = updateFundData(inputSymbol, Constants
                        .Type.BUY);
                if (updateFundData) {
                    Toast.makeText(mContext, R.string.buy_fund_success, Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            Toast.makeText(mContext, R.string.buy_fund_fail, Toast.LENGTH_LONG).show();
            return false;
        } else {
            // If validation fails, show validation error message
            if(!isValidSymbol){
                mInputSymbolView.setError(this.getString(R.string.wrong_fund_code));
            }
            if(!isValidBuyTotal){
                mInputBuyTotalView.setError(this.getString(R.string.wrong_total));
            }
            if(!isValidGainRate){
                mInputGainRateView.setError(this.getString(R.string.wrong_gain_rate));
            }
            if(!isValidDate){
                mInputDateView.setError(this.getString(R.string.wrong_date));
                Toast.makeText(mContext, R.string.wrong_date, Toast.LENGTH_LONG).show();
            }
            if(isFutureDate){
                mInputDateView.setError(this.getString(R.string.future_date));
                Toast.makeText(mContext, R.string.future_date, Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }
}
