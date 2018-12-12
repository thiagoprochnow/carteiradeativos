package br.com.guiainvestimento.fragment.fund;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.api.service.FundNameService;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFormFragment;
import br.com.guiainvestimento.util.InputFilterDecimal;
import br.com.guiainvestimento.util.MaskEditUtil;


public class BuyFundFormFragment extends BaseFormFragment {
    private static final String LOG_TAG = BuyFundFormFragment.class.getSimpleName();
    private View mView;

    private AutoCompleteTextView mInputSymbolView;
    private AutoCompleteTextView mInputCnpjView;
    private EditText mInputBuyTotalView;
    private EditText mInputDateView;
    private boolean buscou = false;
    //gets remote data asynchronously and adds it to AutoCompleteTextView
    FundNameService remoteData = new FundNameService(mContext);
    TextWatcher mask;

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
        mInputSymbolView = (AutoCompleteTextView) mView.findViewById(R.id.inputSymbol);
        mInputCnpjView = (AutoCompleteTextView) mView.findViewById(R.id.inputCnpj);
        mInputBuyTotalView = (EditText) mView.findViewById(R.id.inputBuyTotal);
        mInputDateView = (EditText) mView.findViewById(R.id.inputBuyDate);

        mask = MaskEditUtil.mask(mInputCnpjView, "##.###.###/####-##");

        mInputCnpjView.addTextChangedListener(mask);

        mInputSymbolView.addTextChangedListener(onTextChangedListener);

        mInputSymbolView.setOnItemClickListener(onItemClickListener);

        // Gets symbol received from selected CardView on intent
        Intent intent = getActivity().getIntent();
        String intentSymbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        // Place selling fund symbol on field
        if(intentSymbol != null && !intentSymbol.isEmpty()){
            String cnpj = getCnpj(intentSymbol);
            mInputSymbolView.setText(intentSymbol);
            mInputCnpjView.setText(cnpj);
        }

        // Adding current date to Buy Date field and set Listener to the Spinner
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        mInputDateView.setText(simpleDateFormat.format(new Date()));
        mInputDateView.setOnClickListener(setDatePicker(mInputDateView));

        // Adding input filters
        mInputBuyTotalView.setFilters(new InputFilter[]{ new InputFilterDecimal()});
        return mView;
    }

    // Validate inputted values and add the fund to the portfolio
    private boolean addFund() {

        // Validate for each inputted value
        boolean isValidSymbol = isValidFundSymbol(mInputSymbolView);
        boolean isValidCnpj = isValidFundCnpj(mInputCnpjView);
        boolean isValidBuyTotal = isValidDouble(mInputBuyTotalView);
        boolean isValidDate = isValidDate(mInputDateView);
        boolean isFutureDate = isFutureDate(mInputDateView);

        // If all validations pass, try to add the fund income
        if (isValidSymbol && isValidBuyTotal && isValidDate && isValidCnpj && !isFutureDate) {
            String inputSymbol = mInputSymbolView.getText().toString();
            String inputCnpj = mInputCnpjView.getText().toString();
            double buyTotal = Double.parseDouble(mInputBuyTotalView.getText().toString());
            // Get and handle inserted date value
            String inputDate = mInputDateView.getText().toString();
            Long timestamp = DateToTimestamp(inputDate);

            ContentValues fundCV = new ContentValues();

            // TODO: Check why inputSymbol(string) is working when COLUMN_SYMBOL is INTEGER
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_SYMBOL, inputSymbol);
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_CNPJ, inputCnpj);
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_TOTAL, buyTotal);
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_TIMESTAMP, timestamp);
            fundCV.put(PortfolioContract.FundTransaction.COLUMN_TYPE, Constants.Type.BUY);

            // Adds to the database
            Uri insertedFundTransactionUri = mContext.getContentResolver().insert(PortfolioContract
                    .FundTransaction.URI,
                    fundCV);

            // If error occurs to add, shows error message
            if (insertedFundTransactionUri != null) {
                // Updates each fund income table with new value: Income, Data, FundPortfolio, CompletePortfolio
                boolean updateFundData = updateFundData(inputSymbol, Constants.Type.BUY);
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
            if(!isValidCnpj){
                mInputCnpjView.setError(this.getString(R.string.wrong_fund_cnpj));
            }
            if(!isValidBuyTotal){
                mInputBuyTotalView.setError(this.getString(R.string.wrong_total));
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

    private String getCnpj(String symbol){
        String selection = PortfolioContract.FundData.COLUMN_SYMBOL + " = ?";
        String[] selectionArgs = {symbol};
        String value = "";

        Cursor data = mContext.getContentResolver().query(
                PortfolioContract.FundData.URI,
                null, selection, selectionArgs, null);

        if(data.moveToFirst()){
            String cnpj = data.getString(data.getColumnIndex(PortfolioContract.FundData.COLUMN_SYMBOL));
            value = cnpj;
        }
        return value;
    }

    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    String item = adapterView.getItemAtPosition(i).toString();
                    String nome = item.substring(0, item.length() - 27);
                    String cnpj = item.substring(item.length() - 19, item.length() - 1);

                    mInputSymbolView.setText(nome);
                    mInputCnpjView.removeTextChangedListener(mask);
                    mInputCnpjView.setText(cnpj);
                    mInputCnpjView.addTextChangedListener(mask);

                    Toast.makeText(getContext(),cnpj,Toast.LENGTH_LONG).show();
                }
            };

    private TextWatcher onTextChangedListener =
            new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String text = mInputSymbolView.getText().toString();
                    if(text.length() >= 3 && !buscou) {
                        buscou = true;
                        remoteData = new FundNameService(mContext);
                        remoteData.getFundData(text);
                    } else if(text.length() < 3){
                        buscou = false;
                    }
                }
            };
}
