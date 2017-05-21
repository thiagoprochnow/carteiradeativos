package br.com.carteira.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Locale;

import br.com.carteira.common.Constants;

/* Content Provider for all portfolio items */
public class PortfolioProvider extends ContentProvider {

    private static final String LOG_TAG = PortfolioProvider.class.getSimpleName();

    static UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    /* This is the UriMatcher that is responsible to determine (based on the path used)
    which portfolio item (table) is going to be modified (add, delete, update, etc)
     */
    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_PORTFOLIO, Constants.Provider.PORTFOLIO);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_PORTFOLIO, Constants.Provider.STOCK_PORTFOLIO);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_DATA, Constants.Provider.STOCK_DATA);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_DATA_BULK_UPDATE, Constants.Provider.STOCK_DATA_BULK_UPDATE);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_DATA_BULK_UPDATE_WITH_CURRENT,
                Constants.Provider.STOCK_DATA_BULK_UPDATE_FOR_CURRENT);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_DATA_WITH_SYMBOL, Constants.Provider.STOCK_DATA_WITH_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_SOLD_STOCK_DATA, Constants.Provider.SOLD_STOCK_DATA);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_SOLD_STOCK_DATA_WITH_SYMBOL, Constants.Provider.SOLD_STOCK_DATA_WITH_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_TRANSACTION, Constants.Provider.STOCK_TRANSACTION);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_TRANSACTION_WITH_SYMBOL,
                Constants.Provider.STOCK_TRANSACTION_FOR_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_INCOME, Constants.Provider.STOCK_INCOME);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_INCOME_WITH_SYMBOL,
                Constants.Provider.STOCK_INCOME_FOR_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_FII_PORTFOLIO, Constants.Provider.FII_PORTFOLIO);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_FII_DATA, Constants.Provider.FII_DATA);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_FII_DATA_BULK_UPDATE, Constants.Provider.FII_DATA_BULK_UPDATE);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_FII_DATA_BULK_UPDATE_WITH_CURRENT,
                Constants.Provider.FII_DATA_BULK_UPDATE_FOR_CURRENT);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_FII_DATA_WITH_SYMBOL, Constants.Provider.FII_DATA_WITH_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_SOLD_FII_DATA, Constants.Provider.SOLD_FII_DATA);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_SOLD_FII_DATA_WITH_SYMBOL, Constants.Provider.SOLD_FII_DATA_WITH_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_FII_TRANSACTION, Constants.Provider.FII_TRANSACTION);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_FII_TRANSACTION_WITH_SYMBOL,
                Constants.Provider.FII_TRANSACTION_FOR_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_FII_INCOME, Constants.Provider.FII_INCOME);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_FII_INCOME_WITH_SYMBOL,
                Constants.Provider.FII_INCOME_FOR_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_CURRENCY_PORTFOLIO, Constants.Provider.CURRENCY_PORTFOLIO);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_CURRENCY_DATA, Constants.Provider.CURRENCY_DATA);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_CURRENCY_DATA_BULK_UPDATE, Constants.Provider.FII_DATA_BULK_UPDATE);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_CURRENCY_DATA_BULK_UPDATE_WITH_CURRENT,
                Constants.Provider.CURRENCY_DATA_BULK_UPDATE_FOR_CURRENT);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_CURRENCY_DATA_WITH_SYMBOL, Constants.Provider.CURRENCY_DATA_WITH_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_SOLD_CURRENCY_DATA, Constants.Provider.SOLD_CURRENCY_DATA);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_SOLD_CURRENCY_DATA_WITH_SYMBOL, Constants.Provider.SOLD_CURRENCY_DATA_WITH_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_CURRENCY_TRANSACTION, Constants.Provider.CURRENCY_TRANSACTION);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_CURRENCY_TRANSACTION_WITH_SYMBOL,
                Constants.Provider.CURRENCY_TRANSACTION_FOR_SYMBOL);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    /* Query the content provider and returns a Cursor with the objects found */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor returnCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            // Returns complete portfolio of user
            case Constants.Provider.PORTFOLIO:
                returnCursor = db.query(
                        PortfolioContract.Portfolio.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // Returns stock portfolio of user
            case Constants.Provider.STOCK_PORTFOLIO:
                returnCursor = db.query(
                        PortfolioContract.StockPortfolio.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // Returns all stock symbols possessed by user
            case Constants.Provider.STOCK_DATA:
                returnCursor = db.query(
                        PortfolioContract.StockData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case Constants.Provider.SOLD_STOCK_DATA:
                returnCursor = db.query(
                        PortfolioContract.SoldStockData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // Returns all stocks information possessed by user
            case Constants.Provider.STOCK_TRANSACTION:
                returnCursor = db.query(
                        PortfolioContract.StockTransaction.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // Returns all stock information possessed by user for a specific stock symbol
            case Constants.Provider.STOCK_TRANSACTION_FOR_SYMBOL:
                returnCursor = db.query(
                        PortfolioContract.StockTransaction.TABLE_NAME,
                        projection,
                        PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ?",
                        new String[]{PortfolioContract.StockTransaction.getStockTransactionFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case Constants.Provider.STOCK_INCOME:
                returnCursor = db.query(
                        PortfolioContract.StockIncome.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case Constants.Provider.STOCK_INCOME_FOR_SYMBOL:
                returnCursor = db.query(
                        PortfolioContract.StockIncome.TABLE_NAME,
                        projection,
                        PortfolioContract.StockIncome.COLUMN_SYMBOL + " = ?",
                        new String[]{PortfolioContract.StockIncome.getStockIncomeFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;

            // Returns fii portfolio of user
            case Constants.Provider.FII_PORTFOLIO:
                returnCursor = db.query(
                        PortfolioContract.FiiPortfolio.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            // Returns all fii symbols possessed by user
            case Constants.Provider.FII_DATA:
                returnCursor = db.query(
                        PortfolioContract.FiiData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case Constants.Provider.SOLD_FII_DATA:
                returnCursor = db.query(
                        PortfolioContract.SoldFiiData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // Returns all fiis information possessed by user
            case Constants.Provider.FII_TRANSACTION:
                returnCursor = db.query(
                        PortfolioContract.FiiTransaction.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // Returns all fii information possessed by user for a specific stock symbol
            case Constants.Provider.FII_TRANSACTION_FOR_SYMBOL:
                returnCursor = db.query(
                        PortfolioContract.FiiTransaction.TABLE_NAME,
                        projection,
                        PortfolioContract.FiiTransaction.COLUMN_SYMBOL + " = ?",
                        new String[]{PortfolioContract.FiiTransaction.getFiiTransactionFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case Constants.Provider.FII_INCOME:
                returnCursor = db.query(
                        PortfolioContract.FiiIncome.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case Constants.Provider.FII_INCOME_FOR_SYMBOL:
                returnCursor = db.query(
                        PortfolioContract.FiiIncome.TABLE_NAME,
                        projection,
                        PortfolioContract.FiiIncome.COLUMN_SYMBOL + " = ?",
                        new String[]{PortfolioContract.FiiIncome.getFiiIncomeFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;

            // Returns currency portfolio of user
            case Constants.Provider.CURRENCY_PORTFOLIO:
                returnCursor = db.query(
                        PortfolioContract.CurrencyPortfolio.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            // Returns all currency symbols possessed by user
            case Constants.Provider.CURRENCY_DATA:
                returnCursor = db.query(
                        PortfolioContract.CurrencyData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case Constants.Provider.SOLD_CURRENCY_DATA:
                returnCursor = db.query(
                        PortfolioContract.SoldCurrencyData.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            // Returns all currencies information possessed by user
            case Constants.Provider.CURRENCY_TRANSACTION:
                returnCursor = db.query(
                        PortfolioContract.CurrencyTransaction.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // Returns all currencies information possessed by user for a specific stock symbol
            case Constants.Provider.CURRENCY_TRANSACTION_FOR_SYMBOL:
                returnCursor = db.query(
                        PortfolioContract.CurrencyTransaction.TABLE_NAME,
                        projection,
                        PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL + " = ?",
                        new String[]{PortfolioContract.CurrencyTransaction.getCurrencyTransactionFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;


            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    /* Insert a new item to the table */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        long _id;
        switch (uriMatcher.match(uri)) {

            case Constants.Provider.PORTFOLIO:
                _id = db.insert(
                        PortfolioContract.Portfolio.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.Portfolio.buildPortfolioUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;

            case Constants.Provider.STOCK_PORTFOLIO:
                _id = db.insert(
                        PortfolioContract.StockPortfolio.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.StockPortfolio.buildStockPortfolioUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;

            case Constants.Provider.STOCK_DATA:
                _id = db.insert(
                        PortfolioContract.StockData.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.StockData.buildDataUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;


            case Constants.Provider.SOLD_STOCK_DATA:
                _id = db.insert(
                        PortfolioContract.SoldStockData.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.SoldStockData.buildDataUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;

            case Constants.Provider.STOCK_TRANSACTION:
                _id = db.insert(
                        PortfolioContract.StockTransaction.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.StockTransaction.buildTransactionUri(_id);
                    getContext().getContentResolver().notifyChange(PortfolioContract.StockData.URI, null);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;

            case Constants.Provider.STOCK_INCOME:
                db.insert(
                        PortfolioContract.StockIncome.TABLE_NAME,
                        null,
                        values
                );
                returnUri = PortfolioContract.StockIncome.URI;
                break;

            case Constants.Provider.FII_PORTFOLIO:
                _id = db.insert(
                        PortfolioContract.FiiPortfolio.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.FiiPortfolio.buildFiiPortfolioUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;

            case Constants.Provider.FII_DATA:
                _id = db.insert(
                        PortfolioContract.FiiData.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.FiiData.buildDataUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;


            case Constants.Provider.SOLD_FII_DATA:
                _id = db.insert(
                        PortfolioContract.SoldFiiData.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.SoldFiiData.buildDataUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;

            case Constants.Provider.FII_TRANSACTION:
                _id = db.insert(
                        PortfolioContract.FiiTransaction.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.FiiTransaction.buildTransactionUri(_id);
                    getContext().getContentResolver().notifyChange(PortfolioContract.FiiData.URI, null);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;

            case Constants.Provider.FII_INCOME:
                db.insert(
                        PortfolioContract.FiiIncome.TABLE_NAME,
                        null,
                        values
                );
                returnUri = PortfolioContract.FiiIncome.URI;
                break;
            case Constants.Provider.CURRENCY_PORTFOLIO:
                _id = db.insert(
                        PortfolioContract.CurrencyPortfolio.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.CurrencyPortfolio.buildCurrencyPortfolioUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;

            case Constants.Provider.CURRENCY_DATA:
                _id = db.insert(
                        PortfolioContract.CurrencyData.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.CurrencyData.buildDataUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;


            case Constants.Provider.SOLD_CURRENCY_DATA:
                _id = db.insert(
                        PortfolioContract.SoldCurrencyData.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.SoldCurrencyData.buildDataUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;

            case Constants.Provider.CURRENCY_TRANSACTION:
                _id = db.insert(
                        PortfolioContract.CurrencyTransaction.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.CurrencyTransaction.buildTransactionUri(_id);
                    getContext().getContentResolver().notifyChange(PortfolioContract.CurrencyData.URI, null);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /* Delete one or several items from a table. It returns the number of rows (items) deleted. */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;
        String symbol;

        if (null == selection) selection = "1";
        switch (uriMatcher.match(uri)) {

            case Constants.Provider.STOCK_DATA_WITH_SYMBOL:
                symbol = PortfolioContract.StockData.getStockDataFromUri(uri);
                rowsDeleted = db.delete(
                        PortfolioContract.StockData.TABLE_NAME,
                        '"' + symbol + '"' + " =" + PortfolioContract.StockData.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case Constants.Provider.SOLD_STOCK_DATA_WITH_SYMBOL:
                symbol = PortfolioContract.SoldStockData.getSoldStockDataFromUri(uri);
                rowsDeleted = db.delete(
                        PortfolioContract.SoldStockData.TABLE_NAME,
                        '"' + symbol + '"' + " =" + PortfolioContract.SoldStockData.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case Constants.Provider.SOLD_STOCK_DATA:
                rowsDeleted = db.delete(
                        PortfolioContract.SoldStockData.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case Constants.Provider.STOCK_TRANSACTION:
                rowsDeleted = db.delete(
                        PortfolioContract.StockTransaction.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;

            case Constants.Provider.STOCK_TRANSACTION_FOR_SYMBOL:
                symbol = PortfolioContract.StockTransaction.getStockTransactionFromUri(uri);
                rowsDeleted = db.delete(
                        PortfolioContract.StockTransaction.TABLE_NAME,
                        '"' + symbol + '"' + " =" + PortfolioContract.StockTransaction.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case Constants.Provider.STOCK_INCOME:
                rowsDeleted = db.delete(
                        PortfolioContract.StockIncome.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            case Constants.Provider.STOCK_INCOME_FOR_SYMBOL:
                // TODO: Needs to change, otherwise it will always delete all incomes of that stock symbol
                symbol = PortfolioContract.StockIncome.getStockIncomeFromUri(uri);
                rowsDeleted = db.delete(
                        PortfolioContract.StockIncome.TABLE_NAME,
                        '"' + symbol + '"' + " =" + PortfolioContract.StockIncome.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case Constants.Provider.FII_DATA_WITH_SYMBOL:
                symbol = PortfolioContract.FiiData.getFiiDataFromUri(uri);
                rowsDeleted = db.delete(
                        PortfolioContract.FiiData.TABLE_NAME,
                        '"' + symbol + '"' + " =" + PortfolioContract.FiiData.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case Constants.Provider.SOLD_FII_DATA_WITH_SYMBOL:
                symbol = PortfolioContract.SoldFiiData.getSoldFiiDataFromUri(uri);
                rowsDeleted = db.delete(
                        PortfolioContract.SoldFiiData.TABLE_NAME,
                        '"' + symbol + '"' + " =" + PortfolioContract.SoldFiiData.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case Constants.Provider.SOLD_FII_DATA:
                rowsDeleted = db.delete(
                        PortfolioContract.SoldFiiData.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case Constants.Provider.FII_TRANSACTION:
                rowsDeleted = db.delete(
                        PortfolioContract.FiiTransaction.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;

            case Constants.Provider.FII_TRANSACTION_FOR_SYMBOL:
                symbol = PortfolioContract.FiiTransaction.getFiiTransactionFromUri(uri);
                rowsDeleted = db.delete(
                        PortfolioContract.FiiTransaction.TABLE_NAME,
                        '"' + symbol + '"' + " =" + PortfolioContract.FiiTransaction.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case Constants.Provider.FII_INCOME:
                rowsDeleted = db.delete(
                        PortfolioContract.FiiIncome.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            case Constants.Provider.FII_INCOME_FOR_SYMBOL:
                // TODO: Needs to change, otherwise it will always delete all incomes of that fii symbol
                symbol = PortfolioContract.FiiIncome.getFiiIncomeFromUri(uri);
                rowsDeleted = db.delete(
                        PortfolioContract.FiiIncome.TABLE_NAME,
                        '"' + symbol + '"' + " =" + PortfolioContract.FiiIncome.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /* It updated one or several items in the provider */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated;
        String currentTotal;
        switch (uriMatcher.match(uri)) {

            case Constants.Provider.PORTFOLIO:
                rowsUpdated = db.update(PortfolioContract.Portfolio.TABLE_NAME, values,
                        selection,
                        selectionArgs);

            case Constants.Provider.STOCK_PORTFOLIO:
                rowsUpdated = db.update(PortfolioContract.StockPortfolio.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.STOCK_DATA:
                rowsUpdated = db.update(PortfolioContract.StockData.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.SOLD_STOCK_DATA:
                rowsUpdated = db.update(PortfolioContract.SoldStockData.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.STOCK_DATA_BULK_UPDATE:
                rowsUpdated = this.bulkStockUpdade(values);
                break;

            case Constants.Provider.STOCK_DATA_BULK_UPDATE_FOR_CURRENT:
                currentTotal = PortfolioContract.StockTransaction
                        .getStockTransactionFromUri(uri);
                if (currentTotal != null) {
                    rowsUpdated = this.updateStockCurrentPercent(Double.parseDouble(PortfolioContract
                            .StockTransaction.getStockTransactionFromUri(uri)));
                }else{
                    rowsUpdated = 0;
                }
                break;

            case Constants.Provider.STOCK_TRANSACTION:
                rowsUpdated = db.update(PortfolioContract.StockTransaction.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.STOCK_INCOME:
                rowsUpdated = db.update(PortfolioContract.StockIncome.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.FII_PORTFOLIO:
                rowsUpdated = db.update(PortfolioContract.FiiPortfolio.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.FII_DATA:
                rowsUpdated = db.update(PortfolioContract.FiiData.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.SOLD_FII_DATA:
                rowsUpdated = db.update(PortfolioContract.SoldFiiData.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.FII_DATA_BULK_UPDATE:
                rowsUpdated = this.bulkFiiUpdade(values);
                break;

            case Constants.Provider.FII_DATA_BULK_UPDATE_FOR_CURRENT:
                currentTotal = PortfolioContract.FiiTransaction
                        .getFiiTransactionFromUri(uri);
                if (currentTotal != null) {
                    rowsUpdated = this.updateStockCurrentPercent(Double.parseDouble(PortfolioContract
                            .FiiTransaction.getFiiTransactionFromUri(uri)));
                }else{
                    rowsUpdated = 0;
                }
                break;

            case Constants.Provider.FII_TRANSACTION:
                rowsUpdated = db.update(PortfolioContract.FiiTransaction.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.FII_INCOME:
                rowsUpdated = db.update(PortfolioContract.FiiIncome.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.CURRENCY_PORTFOLIO:
                rowsUpdated = db.update(PortfolioContract.CurrencyPortfolio.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.CURRENCY_DATA:
                rowsUpdated = db.update(PortfolioContract.CurrencyData.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            case Constants.Provider.SOLD_CURRENCY_DATA:
                rowsUpdated = db.update(PortfolioContract.SoldCurrencyData.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

           /* case Constants.Provider.CURRENCY_DATA_BULK_UPDATE:
                rowsUpdated = this.bulkCurrencyUpdade(values);
                break;
*/
            case Constants.Provider.CURRENCY_DATA_BULK_UPDATE_FOR_CURRENT:
                currentTotal = PortfolioContract.CurrencyTransaction
                        .getCurrencyTransactionFromUri(uri);
                if (currentTotal != null) {
                    rowsUpdated = this.updateStockCurrentPercent(Double.parseDouble(PortfolioContract
                            .CurrencyTransaction.getCurrencyTransactionFromUri(uri)));
                }else{
                    rowsUpdated = 0;
                }
                break;

            case Constants.Provider.CURRENCY_TRANSACTION:
                rowsUpdated = db.update(PortfolioContract.CurrencyTransaction.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /* Add several items at once (it's like insert being called several times */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int returnCount = 0;
        switch (uriMatcher.match(uri)) {
            case Constants.Provider.STOCK_TRANSACTION:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                PortfolioContract.StockTransaction.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case Constants.Provider.FII_TRANSACTION:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                PortfolioContract.FiiTransaction.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**
     * This function is responsible for update the value several values of the Stock of table
     * StockData according to the Symbols/CurrentPrice passed by parameter.
     * This action is done in only one transaction in order to not create a lot of I/O requests
     */
    private int bulkStockUpdade(ContentValues contValues) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int quantity;
        double currentPrice;
        double totalBuy;
        double incomeTotal;
        double currentTotal;
        double variation;
        double totalGain;

        db.beginTransaction();
        int returnCount = 0;
        try {
            String updateSelection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ?";
            for (String key : contValues.keySet()) {

                // Prepare query to update stock data
                String[] updatedSelectionArguments = {key};

                Cursor queryCursor = this.query(
                        PortfolioContract.StockData.URI,
                        null, updateSelection, updatedSelectionArguments, null);

                if (queryCursor.getCount() > 0) {
                    queryCursor.moveToFirst();

                    currentPrice = Double.parseDouble(contValues.get(key).toString());
                    quantity = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract
                            .StockData.COLUMN_QUANTITY_TOTAL));
                    totalBuy = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract
                            .StockData.COLUMN_BUY_VALUE_TOTAL));
                    incomeTotal = queryCursor.getDouble(queryCursor.getColumnIndex
                            (PortfolioContract.StockData.COLUMN_NET_INCOME));
                    currentTotal = quantity * currentPrice;
                    variation = currentTotal - totalBuy;
                    totalGain = currentTotal + incomeTotal - totalBuy;

                    ContentValues stockCV = new ContentValues();
                    stockCV.put(PortfolioContract.StockData.COLUMN_CURRENT_PRICE,
                            contValues.get(key).toString());
                    stockCV.put(PortfolioContract.StockData.COLUMN_CURRENT_TOTAL, currentTotal);
                    stockCV.put(PortfolioContract.StockData.COLUMN_VARIATION, variation);
                    stockCV.put(PortfolioContract.StockData.COLUMN_TOTAL_GAIN, totalGain);

                    returnCount += this.update(
                            PortfolioContract.StockData.URI,
                            stockCV, updateSelection, updatedSelectionArguments);

                } else {
                    Log.d(LOG_TAG, "StockData was not found for symbol: " + key);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        getContext().getContentResolver().notifyChange(PortfolioContract.StockData.URI, null);
        return returnCount;
    }

    /**
     * This function is responsible for update the value several values of the Fii of table
     * FiiData according to the Symbols/CurrentPrice passed by parameter.
     * This action is done in only one transaction in order to not create a lot of I/O requests
     */
    private int bulkFiiUpdade(ContentValues contValues) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int quantity;
        double currentPrice;
        double totalBuy;
        double incomeTotal;
        double currentTotal;
        double variation;
        double totalGain;

        db.beginTransaction();
        int returnCount = 0;
        try {
            String updateSelection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ?";
            for (String key : contValues.keySet()) {

                // Prepare query to update stock data
                String[] updatedSelectionArguments = {key};

                Cursor queryCursor = this.query(
                        PortfolioContract.FiiData.URI,
                        null, updateSelection, updatedSelectionArguments, null);

                if (queryCursor.getCount() > 0) {
                    queryCursor.moveToFirst();

                    currentPrice = Double.parseDouble(contValues.get(key).toString());
                    quantity = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract
                            .FiiData.COLUMN_QUANTITY_TOTAL));
                    totalBuy = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract
                            .FiiData.COLUMN_BUY_VALUE_TOTAL));
                    incomeTotal = queryCursor.getDouble(queryCursor.getColumnIndex
                            (PortfolioContract.FiiData.COLUMN_INCOME));
                    currentTotal = quantity * currentPrice;
                    variation = currentTotal - totalBuy;
                    totalGain = currentTotal + incomeTotal - totalBuy;

                    ContentValues fiiCV = new ContentValues();
                    fiiCV.put(PortfolioContract.FiiData.COLUMN_CURRENT_PRICE,
                            contValues.get(key).toString());
                    fiiCV.put(PortfolioContract.FiiData.COLUMN_CURRENT_TOTAL, currentTotal);
                    fiiCV.put(PortfolioContract.FiiData.COLUMN_VARIATION, variation);
                    fiiCV.put(PortfolioContract.FiiData.COLUMN_TOTAL_GAIN, totalGain);

                    returnCount += this.update(
                            PortfolioContract.FiiData.URI,
                            fiiCV, updateSelection, updatedSelectionArguments);

                } else {
                    Log.d(LOG_TAG, "FiiData was not found for symbol: " + key);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        getContext().getContentResolver().notifyChange(PortfolioContract.FiiData.URI, null);
        return returnCount;
    }

    /**
     * This function is responsible for update the value Current Percent of all the Stocks in the
     * table StockData according to the Current Total passed by parameter.
     * This action is done in only one transaction in order to not create a lot of I/O requests
     */
    private int updateStockCurrentPercent(double currentTotal) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        int returnCount = 0;

        try {
            // Check if the symbol exists in the db
            Cursor queryDataCursor = this.query(
                    PortfolioContract.StockData.URI,
                    null, null, null, null);
            double percentSum = 0;
            double currentPercent = 0;
            if (queryDataCursor.getCount() > 0) {
                queryDataCursor.moveToFirst();
                // Update the Current Percent of each StockData
                do {
                    String _id = String.valueOf(queryDataCursor.getInt(queryDataCursor
                            .getColumnIndex(
                                    PortfolioContract.StockData._ID)));
                    double currentDataTotal = queryDataCursor.getDouble(queryDataCursor
                            .getColumnIndex(
                            PortfolioContract.StockData.COLUMN_CURRENT_TOTAL));
                    if (queryDataCursor.isLast()) {
                        // If it is last, round last so sum of all will be 100%
                        currentPercent = 100 - percentSum;
                    } else {
                        // else calculates current percent for stock
                        String currentPercentString = String.format(Locale.US, "%.2f",
                                currentDataTotal / currentTotal * 100);
                        currentPercent = Double.valueOf(currentPercentString);
                        percentSum += currentPercent;
                    }

                    ContentValues stockDataCV = new ContentValues();
                    stockDataCV.put(PortfolioContract.StockData.COLUMN_CURRENT_PERCENT,
                            currentPercent);

                    // Update
                    // Prepare query to update stock data
                    String updateSelection = PortfolioContract.StockData._ID + " = ?";
                    String[] updatedSelectionArguments = {_id};

                    // Update value on stock data
                    returnCount += this.update(
                            PortfolioContract.StockData.URI,
                            stockDataCV, updateSelection, updatedSelectionArguments);

                } while (queryDataCursor.moveToNext());
            } else {
                Log.d(LOG_TAG, "No StockData found");
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(PortfolioContract.StockData.URI, null);
        return returnCount;
    }

    /**
     * This function is responsible for update the value Current Percent of all the Stocks in the
     * table StockData according to the Current Total passed by parameter.
     * This action is done in only one transaction in order to not create a lot of I/O requests
     */
    private int updateFiiCurrentPercent(double currentTotal) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        int returnCount = 0;

        try {
            // Check if the symbol exists in the db
            Cursor queryDataCursor = this.query(
                    PortfolioContract.FiiData.URI,
                    null, null, null, null);
            double percentSum = 0;
            double currentPercent = 0;
            if (queryDataCursor.getCount() > 0) {
                queryDataCursor.moveToFirst();
                // Update the Current Percent of each StockData
                do {
                    String _id = String.valueOf(queryDataCursor.getInt(queryDataCursor
                            .getColumnIndex(
                                    PortfolioContract.FiiData._ID)));
                    double currentDataTotal = queryDataCursor.getDouble(queryDataCursor
                            .getColumnIndex(
                                    PortfolioContract.FiiData.COLUMN_CURRENT_TOTAL));
                    if (queryDataCursor.isLast()) {
                        // If it is last, round last so sum of all will be 100%
                        currentPercent = 100 - percentSum;
                    } else {
                        // else calculates current percent for stock
                        String currentPercentString = String.format(Locale.US, "%.2f",
                                currentDataTotal / currentTotal * 100);
                        currentPercent = Double.valueOf(currentPercentString);
                        percentSum += currentPercent;
                    }

                    ContentValues fiiDataCV = new ContentValues();
                    fiiDataCV.put(PortfolioContract.FiiData.COLUMN_CURRENT_PERCENT,
                            currentPercent);

                    // Update
                    // Prepare query to update stock data
                    String updateSelection = PortfolioContract.FiiData._ID + " = ?";
                    String[] updatedSelectionArguments = {_id};

                    // Update value on stock data
                    returnCount += this.update(
                            PortfolioContract.FiiData.URI,
                            fiiDataCV, updateSelection, updatedSelectionArguments);

                } while (queryDataCursor.moveToNext());
            } else {
                Log.d(LOG_TAG, "No FiiData found");
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(PortfolioContract.FiiData.URI, null);
        return returnCount;
    }

}