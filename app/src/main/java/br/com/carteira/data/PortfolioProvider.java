package br.com.carteira.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import br.com.carteira.common.Constants;

/* Content Provider for all portfolio items */
public class PortfolioProvider extends ContentProvider {

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
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_DATA_WITH_SYMBOL, Constants.Provider.STOCK_DATA_WITH_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_SOLD_STOCK_DATA, Constants.Provider.SOLD_STOCK_DATA);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_SOLD_STOCK_DATA_WITH_SYMBOL, Constants.Provider.SOLD_STOCK_DATA_WITH_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_TRANSACTION, Constants.Provider.STOCK_TRANSACTION);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_TRANSACTION_WITH_SYMBOL,
                Constants.Provider.STOCK_TRANSACTION_FOR_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_INCOME, Constants.Provider.STOCK_INCOME);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_INCOME_WITH_SYMBOL,
                Constants.Provider.STOCK_INCOME_FOR_SYMBOL);
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

        switch (uriMatcher.match(uri)) {
            case Constants.Provider.STOCK_TRANSACTION:
                db.beginTransaction();
                int returnCount = 0;
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
            default:
                return super.bulkInsert(uri, values);
        }
    }



}