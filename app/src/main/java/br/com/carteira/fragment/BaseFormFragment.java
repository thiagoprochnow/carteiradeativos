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

        // Prepare query for stock data
        String selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                null, selection, selectionArguments, null);
        // Gets data quantity to see if bought quantity is enough
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            int boughtQuantity = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract
                    .StockData.COLUMN_QUANTITY_TOTAL));
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

    // Get stock quantity that will receive the dividend per stock
    // symbol is to query by specific symbol only
    // income timestamp is to query only the quantity of stocks bought-sold before the timestamp
    public int getStockQuantity(String symbol, Long incomeTimestamp){
        // Return column should be only quantity of stock
        String[] affectedColumn = {"sum("+ PortfolioContract.StockTransaction.COLUMN_QUANTITY+")"};
        String selectionBuy = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " < ? AND "
                + PortfolioContract.StockTransaction.COLUMN_STATUS + " = ? OR "
                +PortfolioContract.StockTransaction.COLUMN_STATUS + " = ?";
        String[] selectionArgumentsBuy = {symbol,String.valueOf(incomeTimestamp),String.valueOf(Constants.Status.BUY), String.valueOf(Constants.Status.BONIFICATION)};

        // Check if the symbol exists in the db
        Cursor queryCursorBuy = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI,
                affectedColumn, selectionBuy, selectionArgumentsBuy, null);
        if(queryCursorBuy.getCount() > 0) {
            queryCursorBuy.moveToFirst();
            int quantity = queryCursorBuy.getInt(0);
            String selectionSell = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? AND "
                    + PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " <= ? AND "
                    + PortfolioContract.StockTransaction.COLUMN_STATUS + " = ?";
            String[] selectionArgumentsSell = {symbol,String.valueOf(incomeTimestamp),String.valueOf(Constants.Status.SELL)};

            Cursor queryCursorSell = mContext.getContentResolver().query(
                    PortfolioContract.StockTransaction.URI,
                    affectedColumn, selectionSell, selectionArgumentsSell, null);
            if (queryCursorSell.getCount() > 0){
                queryCursorSell.moveToFirst();
                // Return buy quantity - sell quantity
                return quantity - queryCursorSell.getInt(0);
            } else{
                return quantity;
            }
        } else{
            return 0;
        }
    }

    // By using the timestamp of bought/sold stock, function will check if any added income
    // is affected by this buy/sell stock.
    // If any income is affected, it will update income line with new value by using
    // getStockQuantity function for each affected line
    public void updateStockIncomes(String symbol, long timestamp){
        // Prepare query for checking affected incomes
        String selection = PortfolioContract.StockIncome.COLUMN_SYMBOL + " = ? AND " + PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " > ?";
        String[] selectionArguments = {symbol, String.valueOf(timestamp)};

        // Check if any income is affected by stock buy/sell
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockIncome.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            // Sum that will be returned and updated on StockData table by updateStockData()
            double sumReceiveTotal = 0;
            do{
                String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockIncome._ID)));
                long incomeTimestamp = queryCursor.getLong(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                int quantity = getStockQuantity(symbol, incomeTimestamp);
                double perStock = queryCursor.getDouble((queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_PER_STOCK)));
                int incomeType = queryCursor.getInt((queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TYPE)));
                double receiveTotal = quantity * perStock;

                // Prepare query to update stock quantity applied for that dividend
                // and the total income received
                String updateSelection = PortfolioContract.StockIncome._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                ContentValues incomeCV = new ContentValues();
                incomeCV.put(PortfolioContract.StockIncome.COLUMN_AFFECTED_QUANTITY, quantity);
                incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_TOTAL, receiveTotal);
                if(incomeType == Constants.IncomeType.DIVIDEND) {
                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID, receiveTotal);
                } else {
                    double tax = receiveTotal * 0.15;
                    double receiveLiquid = receiveTotal - tax;
                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_TAX, tax);
                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID, receiveLiquid);
                }

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
        } else {
            Log.d(LOG_TAG, "No incomes affected by buy/sell stock");
        }
    }

    public boolean updateStockData(String symbol, int quantity, double buyPrice, double objective, int status){
        // Prepare query for stock data
        String selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                null, selection, selectionArguments, null);

        double value = quantity*buyPrice;

        // Check if there already is a data for this stock to update
        // If not, will create one new
        if(queryCursor.getCount() > 0 ){
            queryCursor.moveToFirst();
            Log.d(LOG_TAG, "Updating data for " + symbol);

            int quantityTotal;
            double valueTotal;
            double receiveIncome;
            double mediumPrice;

            String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockData._ID)));
            // Check if buying or selling stock
            if(status == Constants.Status.BUY || status == Constants.Status.BONIFICATION) {
                quantityTotal = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL)) + quantity;
                valueTotal = queryCursor.getDouble((queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_VALUE_TOTAL))) + value;
                mediumPrice = valueTotal/quantityTotal;
            // Sell
            } else {
                quantityTotal = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL)) - quantity;
                mediumPrice = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_MEDIUM_PRICE));
                valueTotal = quantityTotal*mediumPrice;
            }

            // Query Income table to get total of this stock income
            String[] affectedColumn = {"sum("+ PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID+")"};
            selection = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ?";

            Cursor incomeQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockIncome.URI,
                    affectedColumn, selection, selectionArguments, null);

            if (incomeQueryCursor.getCount() > 0){
                incomeQueryCursor.moveToFirst();
                receiveIncome = incomeQueryCursor.getDouble(0);
            } else {
                receiveIncome = 0;
            }

            ContentValues updateDataCV = new ContentValues();
            updateDataCV.put(PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL, quantityTotal);
            updateDataCV.put(PortfolioContract.StockData.COLUMN_VALUE_TOTAL, valueTotal);
            if (status == Constants.Status.BUY){
                updateDataCV.put(PortfolioContract.StockData.COLUMN_OBJECTIVE_PERCENT, objective);
            }
            if (status == Constants.Status.BUY || status == Constants.Status.BONIFICATION){
                updateDataCV.put(PortfolioContract.StockData.COLUMN_MEDIUM_PRICE, mediumPrice);
            }
            updateDataCV.put(PortfolioContract.StockData.COLUMN_INCOME_TOTAL, receiveIncome);

            // Prepare query to update stock data
            String updateSelection = PortfolioContract.StockData._ID + " = ?";
            String[] updatedSelectionArguments = {_id};

            // Update value on stock data
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.StockData.URI,
                    updateDataCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                Log.d(LOG_TAG, "updateStockData successfully updated");
                // Update Stock Portfolio
                boolean updateStockPortfolio = updateStockPortfolio();
                if(updateStockPortfolio) {
                    return true;
                }
            } else {
                Log.d(LOG_TAG, "updateStockData failed update");
                return false;
            }

        } else {
            Log.d(LOG_TAG, "No data created for " + symbol + ". Creating new one");
            // Only way to create is if status = "Buy", so quantity here will always be positive

            ContentValues newDataCV = new ContentValues();
            newDataCV.put(PortfolioContract.StockData.COLUMN_SYMBOL, symbol);
            newDataCV.put(PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL, quantity);
            newDataCV.put(PortfolioContract.StockData.COLUMN_VALUE_TOTAL, value);
            newDataCV.put(PortfolioContract.StockData.COLUMN_OBJECTIVE_PERCENT, objective);
            newDataCV.put(PortfolioContract.StockData.COLUMN_INCOME_TOTAL, 0);
            newDataCV.put(PortfolioContract.StockData.COLUMN_MEDIUM_PRICE, buyPrice);

            // Adds data to the database
            Uri insertedStockDataUri = mContext.getContentResolver().insert(PortfolioContract.StockData.URI,

                    newDataCV);

            // If error occurs to add, shows error message
            if (insertedStockDataUri != null) {
                Log.d(LOG_TAG, "Added stock data");
                // Update Stock Portfolio
                boolean updateStockPortfolio = updateStockPortfolio();
                if (updateStockPortfolio) {
                    return true;
                }
            } else {
                Log.d(LOG_TAG, "Error adding stock data");
            }
        }
        return false;
    }

    // Reads all of Stock Data value and sets the calculation on StockPortfolio table
    // Dosent need any data because it will not query for a specific stock, but for all of them.
    public boolean updateStockPortfolio(){
        // Return column should be the sum of value total, income total, value gain
        String[] affectedColumn = {"sum("+ PortfolioContract.StockData.COLUMN_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.StockData.COLUMN_INCOME_TOTAL +"), " +
                "sum("+PortfolioContract.StockData.COLUMN_VALUE_GAIN+")"};

        // Check if the symbol exists in the db
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                affectedColumn, null, null, null);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            double valueTotal = queryCursor.getInt(0);
            double incomeTotal = queryCursor.getInt(1);
            double valueGain = queryCursor.getInt(2);

            // Values to be inserted or updated on StockPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_VALUE_TOTAL, valueTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL, incomeTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_VALUE_GAIN, valueGain);

            // Query for the only stock portfolio, if dosent exist, creates one
            Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockPortfolio.URI,
                    null, null, null, null);
            // If exists, updates value, else create a new field and add values
            if(portfolioQueryCursor.getCount() > 0){
                portfolioQueryCursor.moveToFirst();
                String _id = String.valueOf(portfolioQueryCursor.getInt(portfolioQueryCursor.getColumnIndex(PortfolioContract.StockPortfolio._ID)));
                // Prepare query to update stock data
                String updateSelection = PortfolioContract.StockData._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                // Update value on stock data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.StockPortfolio.URI,
                        portfolioCV, updateSelection, updatedSelectionArguments);

                if (updatedRows > 0 ){
                    return true;
                }
            } else {
                // Creates table and add values
                Uri insertedStockDataUri = mContext.getContentResolver().insert(PortfolioContract.StockPortfolio.URI,
                        portfolioCV);
                if(insertedStockDataUri != null){
                    return true;
                }
            }
            return false;
        } else{
            return false;
        }
    }

    // Reads all of Investment Portfolios value and sets the calculation on Portfolio table
    // Dosent need any data because it will not query for a specific investment, but for all of them.
    public boolean updatePortfolio(){
        // TODO: Develop function to read all Stock, FII, Fixed Income, etc table to get total value of portfolio
        return true;
    }
}
