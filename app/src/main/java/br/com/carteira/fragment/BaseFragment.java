package br.com.carteira.fragment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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
import br.com.carteira.api.service.StockIntentService;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;

public abstract class BaseFragment extends Fragment {

    protected Context mContext;

    private static final String LOG_TAG = BaseFragment.class.getSimpleName();

    private Menu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enables the menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    // Delete stock and all its information from database
    // This is different then selling a stock, that will maintain some information
    public boolean deleteStock(String symbol) {
        int deletedTransaction = getActivity().getContentResolver().delete(PortfolioContract
                .StockTransaction
                .makeUriForStockTransaction(symbol), null, null);
        int deletedData = getActivity().getContentResolver().delete(PortfolioContract.StockData
                .makeUriForStockData(symbol), null, null);

        int deletedSoldData = getActivity().getContentResolver().delete(PortfolioContract.SoldStockData
                .makeUriForSoldStockData(symbol), null, null);
        // Cannot check if deletedIncome > 0, because stock may not have any income to delete
        // Which is not an error
        int deletedIncome = getActivity().getContentResolver().delete(PortfolioContract.StockIncome
                .makeUriForStockIncome(symbol), null, null);
        Log.d(LOG_TAG, "DeletedTransaction: " + deletedTransaction + " DeletedData: " + deletedData + " DeletedIncome: " + deletedIncome);
        if (deletedData > 0) {
            mContext.sendBroadcast(new Intent(Constants.Receiver.STOCK));
            Toast.makeText(mContext, getString(R.string.toast_stock_successfully_removed, symbol)
                    , Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.toast_stock_not_removed, symbol), Toast
                    .LENGTH_SHORT).show();
            return false;
        }
    }

    // Delete stock income from table by using its id
    // symbol is used to update Stock Data table
    public boolean deleteStockIncome(String id, String symbol){
        String selection = PortfolioContract.StockIncome._ID + " = ? AND "
                + PortfolioContract.StockIncome.COLUMN_SYMBOL + " = ?";
        if (symbol == null){
            String selectionData = PortfolioContract.StockIncome._ID + " = ? ";
            String[] selectionDataArguments = {id};
            String[] affectedColumn = {PortfolioContract.StockIncome.COLUMN_SYMBOL};
            Cursor cursor = mContext.getContentResolver().query(
                    PortfolioContract.StockIncome.URI,
                    affectedColumn, selectionData, selectionDataArguments, null);

            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                symbol = cursor.getString(0);
            } else {
                Log.d(LOG_TAG, "No symbol for for that income");
            }
        }
        String[] selectionArguments = {id, symbol};

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.StockIncome.URI,
                selection, selectionArguments);
        Log.d(LOG_TAG, "ID: " + id + " Symbol: " + symbol);
        if (deletedResult > 0){
            // Update stock data for that symbol
            boolean updateStockData = updateStockData(symbol, -1, -1);
            if (updateStockData)
                return true;
        }
        return false;
    }

    // Delete stock transaction from table by using its id
    // symbol is used to update Stock Data table
    public boolean deleteStockTransaction(String id, String symbol){
        long timestamp;
        String[] affectedColumn = {PortfolioContract.StockTransaction.COLUMN_TIMESTAMP};
        String selection = PortfolioContract.StockTransaction._ID + " = ? AND "
                + PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {id, symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI, affectedColumn,
                selection, selectionArguments, null);

        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            timestamp = queryCursor.getLong(0);
        } else {
            return false;
        }

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.StockTransaction.URI,
                selection, selectionArguments);

        if (deletedResult > 0){
            // Update stock data and stock income for that symbol
            updateStockIncomes(symbol, timestamp);
            updateStockData(symbol, -1, Constants.Type.DELETE_TRANSACION);
        }

        // Check if there is any more transaction for this symbol
        // If not, delete this symbol from StockData

        String selectionTransaction = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.StockTransaction.COLUMN_TYPE + " = ?";
        String[] selectionArgumentsTransaction = {symbol, String.valueOf(Constants.Type.BUY)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI, null,
                selectionTransaction, selectionArgumentsTransaction, null);

        // If there is no more buy transaction for this symbol, delete the stock and finish activity
        if (queryCursor.getCount() == 0){
            deleteStock(symbol);
            getActivity().finish();
        }

        // Check if there is any more SELL transaction for this symbol
        // If not, delete this symbol from SoldStockData

        String sellSelectionTransaction = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.StockTransaction.COLUMN_TYPE + " = ?";
        String[] sellArgumentsTransaction = {symbol, String.valueOf(Constants.Type.SELL)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI, null,
                sellSelectionTransaction, sellArgumentsTransaction, null);

        // No more Sell transactions, delete SoldStockData
        if (queryCursor.getCount() == 0){
            String selectionSoldData = PortfolioContract.SoldStockData.COLUMN_SYMBOL + " = ?";
            String[] selectionArgumentsSoldData = {symbol};
            int rowsDeleted = mContext.getContentResolver().delete(
                    PortfolioContract.SoldStockData.URI, selectionSoldData,
                    selectionArgumentsSoldData);
            if (rowsDeleted == 1){
                Log.d(LOG_TAG, "SoldStockData deleted successfully");
            } else {
                Log.d(LOG_TAG, "Problem deleting SoldStockData");
            }
        }

        return false;
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

    // Transforms a timestamp into a string Date
    public String TimestampToDate(long timestamp){
        String date = android.text.format.DateFormat.format("dd/MM/yyyy", timestamp).toString();
        return date;
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

    // Validate if an EditText was set with date that is in the future of current date
    protected boolean isFutureDate(EditText date) {
        Editable editable = date.getText();
        String textDate = editable.toString();
        long timestamp = DateToTimestamp(textDate);
        long currentTime = new Date().getTime();
        // Check if it future date input
        if (timestamp > currentTime) {
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
    // income timestamp is to query only the quantity of stocks transactions before the timestamp
    public int getStockQuantity(String symbol, Long incomeTimestamp){
        // Return column should be only quantity of stock
        String selection = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " < ?";
        String[] selectionArguments = {symbol,String.valueOf(incomeTimestamp)};
        String sortOrder = PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " ASC";

        // Check if the symbol exists in the db
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI,
                null, selection, selectionArguments, sortOrder);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            int quantityTotal = 0;
            int currentType = 0;
            do {
                currentType = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        quantityTotal += queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.BONIFICATION:
                        quantityTotal += queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SPLIT:
                        quantityTotal = quantityTotal*queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.GROUPING:
                        quantityTotal = quantityTotal/queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    default:
                        Log.d(LOG_TAG, "getStockQuantity currentType Unknown");
                }
            } while (queryCursor.moveToNext());
            return quantityTotal;
        } else{
            Log.d(LOG_TAG, "");
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

    // Reads the StockTransaction entries and calculates value for StockData table for this symbol
    public boolean updateStockData(String symbol, double objective, int type){

        String selection = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in StockData
            int quantityTotal = 0;
            double buyValue = 0;
            // Buy quantity and total is to calculate correct medium buy price
            // Medium price is only for buys
            double buyQuantity = 0;
            double buyTotal = 0;
            double receiveIncome = 0;
            double taxIncome = 0;
            double mediumPrice = 0;
            int currentType;
            int bonificationQuantity = 0;
            // At the time of the sell, need to calculate the Medium price and total bought of that time
            // by using mediumPrice afterwards, will result in calculation error
            // Ex: In timestamp sequence, Buy 100 at 20,00, Sell 100 at 21,00, Buy 100 at 30,00
            // Ex: By that, medium price will be 25,00 and the sell by 21,00 will show as money loss, which is wrong
            // By using 20,00 at that time, sell at 21,00 will result in profit, which is correct
            double soldBuyValue = 0;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        buyQuantity += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        buyTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
                        quantityTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        buyValue += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
                        mediumPrice = buyTotal/buyQuantity;
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        buyValue = quantityTotal*mediumPrice;
                        // Add the value sold times the current medium buy price
                        soldBuyValue += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*mediumPrice;
                        break;
                    case Constants.Type.BONIFICATION:
                        bonificationQuantity += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SPLIT:
                        buyQuantity = buyQuantity*STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        quantityTotal = quantityTotal*STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        mediumPrice = mediumPrice/STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.GROUPING:
                        buyQuantity = buyQuantity/STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        quantityTotal = quantityTotal/STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        mediumPrice = mediumPrice*STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    default:
                        Log.d(LOG_TAG, "currentType Unknown");
                }
            } while (STQueryCursor.moveToNext());
            // Adds the bonification after the buyValue is totally calculated
            // Bonification cannot influence on the buy value
            quantityTotal += bonificationQuantity;
            ContentValues stockDataCV = new ContentValues();

            stockDataCV.put(PortfolioContract.StockData.COLUMN_SYMBOL, symbol);

            selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ? ";

            // Searches for existing StockData to update value.
            // If dosent exists, creates new one
            Cursor queryDataCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockData.URI,
                    null, selection, selectionArguments, null);

            double currentTotal = 0;
            double variation = 0;
            // Create new StockData for this symbol
            if (queryDataCursor.getCount() == 0){
                // Adds data to the database
                Uri insertedStockDataUri = mContext.getContentResolver().insert(PortfolioContract.StockData.URI,
                        stockDataCV);

                // If error occurs to add, shows error message
                if (insertedStockDataUri != null) {
                    Log.d(LOG_TAG, "Created stock data");
                    // Update Stock Portfolio
                } else {
                    Log.d(LOG_TAG, "Error creating stock data");
                    return false;
                }
            } else {
                // Needs to update current total and total gain with latest current price
                // If not, StockDetailsOverview will not update current total and total gain, unless refreshing the View
                if (type == Constants.Type.DELETE_TRANSACION || type == Constants.Type.BONIFICATION){
                    queryDataCursor.moveToFirst();
                    double currentPrice = queryDataCursor.getDouble(queryDataCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_CURRENT_PRICE));
                    currentTotal = currentPrice*quantityTotal;
                    variation = currentTotal - buyTotal;
                }
            }

            Intent mServiceIntent = new Intent(mContext, StockIntentService
                    .class);
            mServiceIntent.putExtra(StockIntentService.ADD_SYMBOL, symbol);
            getActivity().startService(mServiceIntent);

            // Query Income table to get total of this stock income
            String[] affectedColumn = {"sum("+ PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID+")",
                    "sum("+ PortfolioContract.StockIncome.COLUMN_TAX+")"};
            selection = PortfolioContract.StockIncome.COLUMN_SYMBOL + " = ?";

            Cursor incomeQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockIncome.URI,
                    affectedColumn, selection, selectionArguments, null);

            if (incomeQueryCursor.getCount() > 0){
                incomeQueryCursor.moveToFirst();
                receiveIncome = incomeQueryCursor.getDouble(0);
                taxIncome = incomeQueryCursor.getDouble(1);
            } else {
                receiveIncome = 0;
            }

            stockDataCV.put(PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL, quantityTotal);
            stockDataCV.put(PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL, buyValue);
            if (type == Constants.Type.BUY) {
                stockDataCV.put(PortfolioContract.StockData.COLUMN_OBJECTIVE_PERCENT, objective);
            }
            if ((type == Constants.Type.DELETE_TRANSACION || type == Constants.Type.BONIFICATION) && queryDataCursor.getCount() > 0){
                stockDataCV.put(PortfolioContract.StockData.COLUMN_CURRENT_TOTAL, currentTotal);
                stockDataCV.put(PortfolioContract.StockData.COLUMN_VARIATION, variation);
            }
            stockDataCV.put(PortfolioContract.StockData.COLUMN_NET_INCOME, receiveIncome);
            stockDataCV.put(PortfolioContract.StockData.COLUMN_INCOME_TAX, taxIncome);
            stockDataCV.put(PortfolioContract.StockData.COLUMN_MEDIUM_PRICE, mediumPrice);

            if(quantityTotal > 0){
                // Set stock as active
                stockDataCV.put(PortfolioContract.StockData.COLUMN_STATUS, Constants.Status.ACTIVE);
            } else {
                // Set stock as sold
                stockDataCV.put(PortfolioContract.StockData.COLUMN_STATUS, Constants.Status.SOLD);
            }
            // Searches for existing StockData to update value.
            // If dosent exists, creates new one
            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockData.URI,
                    null, selection, selectionArguments, null);

            queryCursor.moveToFirst();

            String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockData._ID)));

            // Update
            // Prepare query to update stock data
            String updateSelection = PortfolioContract.StockData._ID + " = ?";
            String[] updatedSelectionArguments = {_id};

            // Update value on stock data
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.StockData.URI,
                    stockDataCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                Log.d(LOG_TAG, "updateStockData successfully updated");
                // Update Stock Portfolio
                // Send broadcast so StockReceiver can update the rest
                updateSoldStockData(symbol, soldBuyValue);
                return true;
            } else {
                Log.d(LOG_TAG, "updateStockData failed update");
                return false;
            }
        } else{
            Log.d(LOG_TAG, "No StockTransaction found");
            return false;
        }
    }

    // Reads the StockTransaction entries and calculates value for StockData table for this symbol
    public boolean updateSoldStockData(String symbol, double soldBuyValue){

        String selection = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in StockData
            int quantityTotal = 0;
            double soldTotal = 0;
            double sellMediumPrice = 0;
            int currentType;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE));
                double price = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.SELL:
                        quantityTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        soldTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
                        sellMediumPrice = soldTotal/quantityTotal;
                        Log.d(LOG_TAG, "SELL: QuantityTotal: " + quantityTotal);
                        Log.d(LOG_TAG, "SELL: SoldTotal: " + quantityTotal);
                        Log.d(LOG_TAG, "SELL: soldBuyValue: " + soldBuyValue);
                        break;
                    default:
                        Log.d(LOG_TAG, "Do nothing");
                }
            } while (STQueryCursor.moveToNext());

            // If there is any sold stock
            if (quantityTotal > 0) {
                ContentValues stockDataCV = new ContentValues();

                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_SYMBOL, symbol);

                selection = PortfolioContract.SoldStockData.COLUMN_SYMBOL + " = ? ";

                // Searches for existing StockData to update value.
                // If dosent exists, creates new one
                Cursor queryDataCursor = mContext.getContentResolver().query(
                        PortfolioContract.SoldStockData.URI,
                        null, selection, selectionArguments, null);

                // Create new StockData for this symbol
                if (queryDataCursor.getCount() == 0) {
                    // Adds data to the database
                    Uri insertedStockDataUri = mContext.getContentResolver().insert(PortfolioContract.SoldStockData.URI,

                            stockDataCV);

                    // If error occurs to add, shows error message
                    if (insertedStockDataUri != null) {
                        Log.d(LOG_TAG, "Created stock sold data");
                        // Update Stock Portfolio
                    } else {
                        Log.d(LOG_TAG, "Error creating sold stock data");
                        return false;
                    }
                }

                double sellGain = soldTotal - soldBuyValue;
                double gainPercent = sellGain/soldBuyValue*100;
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_QUANTITY_TOTAL, quantityTotal);
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_BUY_VALUE_TOTAL, soldBuyValue);
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_SELL_MEDIUM_PRICE, sellMediumPrice);
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_SELL_TOTAL, soldTotal);
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_SELL_GAIN, sellGain);
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_SELL_GAIN_PERCENT, gainPercent);

                // Searches for existing StockData to update value.
                // If dosent exists, creates new one
                Cursor queryCursor = mContext.getContentResolver().query(
                        PortfolioContract.SoldStockData.URI,
                        null, selection, selectionArguments, null);

                queryCursor.moveToFirst();

                String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.SoldStockData._ID)));

                // Update
                // Prepare query to update stock data
                String updateSelection = PortfolioContract.SoldStockData._ID + " = ?";
                String[] updatedSelectionArguments = {_id};

                // Update value on stock data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.SoldStockData.URI,
                        stockDataCV, updateSelection, updatedSelectionArguments);
                // Log update success/fail result
                if (updatedRows > 0) {
                    Log.d(LOG_TAG, "updateSoldStockData successfully updated");
                    // Update Stock Portfolio
                    // Send broadcast so StockReceiver can update the rest
                    mContext.sendBroadcast(new Intent(Constants.Receiver.STOCK));
                    return true;
                } else {
                    Log.d(LOG_TAG, "updateSoldStockData failed update");
                    return false;
                }
            }
            Log.d(LOG_TAG, "No sold stock yet");
            return true;
        } else{
            Log.d(LOG_TAG, "No StockTransaction found");
            return false;
        }
    }
}
