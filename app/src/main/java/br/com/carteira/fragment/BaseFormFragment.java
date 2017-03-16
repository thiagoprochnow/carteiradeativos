package br.com.carteira.fragment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;

public abstract class BaseFormFragment extends BaseFragment {

    private static final String LOG_TAG = BaseFormFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enables the menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Common menu for add forms
        inflater.inflate(R.menu.add_form_menu, menu);
    }

    // Validate if an EditText field is empty
    protected boolean isEditTextEmpty(EditText text) {
        Editable editable = text.getText();
        if (editable != null && TextUtils.isEmpty(editable.toString())) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid Stock Symbol
    protected boolean isValidStockSymbol(EditText symbol) {
        Editable editable = symbol.getText();
        // Regex Pattern for Stock (EX: PETR4) or ETF (EX: BOVA11 or SMAL11)
        Pattern pattern = Pattern.compile("^[A-Z]{4}([0-9]|[0-9][0-9])$");
        if (!isEditTextEmpty(symbol) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid int and that there is enough quantity of stock
    protected boolean isValidSellQuantity(EditText editQuantity, EditText editSymbol) {
        // TODO
        Editable editableQuantity = editQuantity.getText();
        int quantity = Integer.parseInt(editableQuantity.toString());
        String symbol = editSymbol.getText().toString();

        // Check if it is digit only
        boolean isDigitOnly = TextUtils.isDigitsOnly(editableQuantity.toString());
        boolean isQuantityEnough = false;

        // Prepare query for stock portfolio
        String selection = PortfolioContract.StockPortfolio.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockPortfolio.URI,
                null, selection, selectionArguments, null);
        // Gets portfolio quantity to see if bought quantity is enough
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            int boughtQuantity = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_QUANTITY_TOTAL));
            if(boughtQuantity >= quantity){
                // Bought quantity is bigger then quantity trying to sell
                isQuantityEnough = true;
            } else{
                return false;
            }
        } else{
            return false;
        }

        if (!isEditTextEmpty(editQuantity) && isDigitOnly && isQuantityEnough) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid int
    protected boolean isValidInt(EditText symbol) {
        Editable editable = symbol.getText();
        // Check if it is digit only
        boolean isDigitOnly = TextUtils.isDigitsOnly(editable.toString());
        if (!isEditTextEmpty(symbol) && isDigitOnly) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid double value
    protected boolean isValidDouble(EditText symbol) {
        Editable editable = symbol.getText();
        // Check if it is double input
        Pattern pattern = Pattern.compile("^[0-9]+\\.?[0-9]*$");
        if (!isEditTextEmpty(symbol) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid double value
    protected boolean isValidPercent(EditText symbol) {
        Editable editable = symbol.getText();
        // Check if it is percent input
        Pattern pattern = Pattern.compile("^[0-9]+\\.?[0-9]*$");
        // Check inputted value is lower or equal to 100%, no sense being bigger

        try {
            double value = Double.parseDouble(editable.toString());
            boolean isPercent = (value >= 0 && value <= 100);
            if (!isEditTextEmpty(symbol) && pattern.matcher(editable.toString()).matches() &&
                    isPercent) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException ex) {
            Log.d(LOG_TAG, "Something wrong while parsing number");
            ex.printStackTrace();
            return false;
        }
    }

    // Validate if an EditText was set with valid date value
    protected boolean isValidDate(EditText date) {
        Editable editable = date.getText();
        // Check if it is date input
        Pattern pattern = Pattern.compile("^[0-9][0-9]\\/[0-9][0-9]\\/[0-9][0-9][0-9][0-9]$");
        if (!isEditTextEmpty(date) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Sets DatePicker and return the OnClickListener
    public View.OnClickListener setDatePicker(final EditText inputDateView) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the inputBuyDate field
                final Calendar mCalendar = Calendar.getInstance();
                final String mDateFormat = "dd/MM/yyyy";
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mDateFormat,
                        Locale.getDefault());
                // To show current date or inputted date on datepicker
                if (inputDateView.getText().length() > 0) {
                    String mDate = inputDateView.getText().toString();
                    try {
                        Date inputtedDate = simpleDateFormat.parse(mDate);
                        // Sets the current date to the previously inputted date
                        mCalendar.setTime(inputtedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                int mYear = mCalendar.get(Calendar.YEAR);
                int mMonth = mCalendar.get(Calendar.MONTH);
                int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(mContext, new
                        DatePickerDialog.OnDateSetListener() {
                            // When the date is selected and clicked "OK" on the Spinner
                            public void onDateSet(DatePicker datepicker, int year, int month, int
                                    day) {
                                // Sets the date on the EditText field value
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, month);
                                mCalendar.set(Calendar.DAY_OF_MONTH, day);
                                inputDateView.setText(simpleDateFormat.format(mCalendar.getTime()));
                            }
                        }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        };
        return onClickListener;
    }

    // Transform a date value of dd/MM/yyyy into a timestamp value
    public Long DateToTimestamp(String inputDate){
        Log.d(LOG_TAG, "InputDate String: " + inputDate);
        DateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        Date date = new Date();
        try{
            date = (Date) dateFormat.parse(inputDate);
        } catch (ParseException e){
            e.printStackTrace();
        }

        return date.getTime();
    }

    // Get stock quantity that will receive the dividend per stock
    // symbol is to query by specific symbol only
    // timestamp is to query only the quantity of stocks bought before the timestamp
    public int getStockQuantity(String symbol, Long timestamp){
        // Return column should be only quantity of stock
        String[] affectedColumn = {"sum("+PortfolioContract.StockQuote.COLUMN_QUANTITY+")"};
        String selection = PortfolioContract.StockQuote.COLUMN_SYMBOL + " = ? AND " + PortfolioContract.StockQuote.COLUMN_TIMESTAMP + " < ?";
        String[] selectionArguments = {symbol,String.valueOf(timestamp)};

        // Check if the symbol exists in the db
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockQuote.URI,
                affectedColumn, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            return queryCursor.getInt(0);
        } else{
            return 0;
        }
    }

    // By using the timestamp of bought/sold stock, function will check if any added income
    // is affected by this buy/sell stock.
    // If any income is affected, it will update income line with new value by using
    // getStockQuantity function for each affected line
    public double updateStockIncomes(String symbol, long timestamp){
        // Prepare query for checking affected incomes
        String selection = PortfolioContract.StockIncome.COLUMN_SYMBOL + " = ? AND " + PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " > ?";
        String[] selectionArguments = {symbol, String.valueOf(timestamp)};

        // Check if any income is affected by stock buy/sell
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockIncome.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            // Sum that will be returned and updated on StockPortfolio table by updateStockPortfolio()
            double sumReceiveTotal = 0;
            do{
                String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockIncome._ID)));
                long incomeTimestamp = queryCursor.getLong(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                int quantity = getStockQuantity(symbol, incomeTimestamp);
                double perStock = queryCursor.getDouble((queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_PER_STOCK)));
                double receiveTotal = quantity * perStock;

                // Prepare query to update stock quantity applied for that dividend
                // and the total income received
                String updateSelection = PortfolioContract.StockIncome._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                ContentValues incomeCV = new ContentValues();
                incomeCV.put(PortfolioContract.StockIncome.COLUMN_AFFECTED_QUANTITY, quantity);
                incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_TOTAL, receiveTotal);

                sumReceiveTotal += receiveTotal;

                // Update value on incomes table
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.StockIncome.URI,
                        incomeCV, updateSelection, updatedSelectionArguments);
                // Log update success/fail result
                if (updatedRows > 0){
                    Log.d(LOG_TAG, "updateStockIncomes successfully updated");
                } else {
                    Log.d(LOG_TAG, "updateStockIncomes failed update");
                }
            } while (queryCursor.moveToNext());
            return sumReceiveTotal;
        } else {
            Log.d(LOG_TAG, "No incomes affected by buy/sell stock");
            return 0;
        }
    }

    public boolean updateStockPortfolio(String symbol, int quantity, double buyPrice, double objective, double sumReceiveIncome, int status){
        // Prepare query for stock portfolio
        String selection = PortfolioContract.StockPortfolio.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockPortfolio.URI,
                null, selection, selectionArguments, null);

        double value = quantity*buyPrice;

        // Check if there already is a portfolio for this stock to update
        // If not, will create one new
        if(queryCursor.getCount() > 0 ){
            queryCursor.moveToFirst();
            Log.d(LOG_TAG, "Updating portfolio for " + symbol);

            int quantityTotal;
            double valueTotal;
            double receiveIncome;
            double mediumPrice;

            String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockPortfolio._ID)));
            // Check if buying or selling stock
            if(status == Constants.Status.BUY) {
                quantityTotal = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_QUANTITY_TOTAL)) + quantity;
                valueTotal = queryCursor.getDouble((queryCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_VALUE_TOTAL))) + value;
                mediumPrice = valueTotal/quantityTotal;
                receiveIncome = queryCursor.getDouble((queryCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL))) + sumReceiveIncome;
            // Sell
            } else {
                quantityTotal = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_QUANTITY_TOTAL)) - quantity;
                mediumPrice = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_MEDIUM_PRICE));
                valueTotal = quantityTotal*mediumPrice;
                receiveIncome = queryCursor.getDouble((queryCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL))) - sumReceiveIncome;
            }

            ContentValues updatePortfolioCV = new ContentValues();
            updatePortfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_QUANTITY_TOTAL, quantityTotal);
            updatePortfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_VALUE_TOTAL, valueTotal);
            if (status == Constants.Status.BUY){
                updatePortfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_OBJECTIVE_PERCENT, objective);
            }
            if (status == Constants.Status.BUY){
                updatePortfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_MEDIUM_PRICE, mediumPrice);
            }
            updatePortfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL, receiveIncome);

            // Prepare query to update stock portfolio
            String updateSelection = PortfolioContract.StockPortfolio._ID + " = ?";
            String[] updatedSelectionArguments = {_id};

            // Update value on stock portfolio
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.StockPortfolio.URI,
                    updatePortfolioCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                Log.d(LOG_TAG, "updateStockPortfolio successfully updated");
                return true;
            } else {
                Log.d(LOG_TAG, "updateStockPortfolio failed update");
                return false;
            }

        } else {
            Log.d(LOG_TAG, "No portfolio created for " + symbol + ". Creating new one");
            // Only way to create is if status = "Buy", so quantity here will always be positive

            ContentValues newPortfolioCV = new ContentValues();
            newPortfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_SYMBOL, symbol);
            newPortfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_QUANTITY_TOTAL, quantity);
            newPortfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_VALUE_TOTAL, value);
            newPortfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_OBJECTIVE_PERCENT, objective);
            newPortfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL, sumReceiveIncome);

            // Adds portfolio to the database
            Uri insertedStockPortfolioUri = mContext.getContentResolver().insert(PortfolioContract.StockPortfolio.URI,
                    newPortfolioCV);

            // If error occurs to add, shows error message
            if (insertedStockPortfolioUri != null) {
                Log.d(LOG_TAG, "Added stock portfolio");
                return true;
            } else {
                Log.d(LOG_TAG, "Error adding stock portfolio");
            }
        }
        return false;
    }
}
