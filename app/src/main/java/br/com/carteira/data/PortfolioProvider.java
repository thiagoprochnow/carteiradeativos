package br.com.carteira.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* Content Provider for all portfolio items */
public class PortfolioProvider extends ContentProvider {

    static final int STOCK_SYMBOLS = 100;
    static final int STOCK_SYMBOLS_WITH_SYMBOL = 101;

    static final int STOCK_QUOTE = 200;
    static final int STOCK_QUOTE_FOR_SYMBOL = 201;

    static final int STOCK_INCOME = 300;
    static final int STOCK_INCOME_FOR_SYMBOL = 301;

    static UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    /* This is the UriMatcher that is responsible to determine (based on the path used)
    which portfolio item (table) is going to be modified (add, delete, update, etc)
     */
    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_SYMBOLS, STOCK_SYMBOLS);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_SYMBOLS_WITH_SYMBOL, STOCK_SYMBOLS_WITH_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_QUOTE, STOCK_QUOTE);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_QUOTE_WITH_SYMBOL,
                STOCK_QUOTE_FOR_SYMBOL);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_INCOME, STOCK_INCOME);
        matcher.addURI(PortfolioContract.AUTHORITY, PortfolioContract.PATH_STOCK_INCOME_WITH_SYMBOL,
                STOCK_INCOME_FOR_SYMBOL);
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
            // Returns all stock symbols possessed by user
            case STOCK_SYMBOLS:
                returnCursor = db.query(
                        PortfolioContract.StockSymbol.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // Returns all stocks information possessed by user
            case STOCK_QUOTE:
                returnCursor = db.query(
                        PortfolioContract.StockQuote.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // Returns all stock information possessed by user for a specific stock symbol
            case STOCK_QUOTE_FOR_SYMBOL:
                returnCursor = db.query(
                        PortfolioContract.StockQuote.TABLE_NAME,
                        projection,
                        PortfolioContract.StockQuote.COLUMN_SYMBOL + " = ?",
                        new String[]{PortfolioContract.StockQuote.getStockQuoteFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case STOCK_INCOME:
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

            case STOCK_INCOME_FOR_SYMBOL:
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
            case STOCK_SYMBOLS:
                _id = db.insert(
                        PortfolioContract.StockSymbol.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.StockSymbol.buildSymbolUri(_id);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;
            case STOCK_QUOTE:
                _id = db.insert(
                        PortfolioContract.StockQuote.TABLE_NAME,
                        null,
                        values
                );
                if(_id > 0) {
                    returnUri = PortfolioContract.StockQuote.buildQuoteUri(_id);
                    getContext().getContentResolver().notifyChange(PortfolioContract.StockSymbol.URI, null);
                }else{
                    throw new UnsupportedOperationException("Unknown URI:" + uri);
                }
                break;
            case STOCK_INCOME:
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
            case STOCK_SYMBOLS_WITH_SYMBOL:
                symbol = PortfolioContract.StockSymbol.getStockSymbolFromUri(uri);
                rowsDeleted = db.delete(
                        PortfolioContract.StockSymbol.TABLE_NAME,
                        '"' + symbol + '"' + " =" + PortfolioContract.StockQuote.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case STOCK_QUOTE:
                rowsDeleted = db.delete(
                        PortfolioContract.StockQuote.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;

            case STOCK_QUOTE_FOR_SYMBOL:
                symbol = PortfolioContract.StockQuote.getStockQuoteFromUri(uri);
                rowsDeleted = db.delete(
                        PortfolioContract.StockQuote.TABLE_NAME,
                        '"' + symbol + '"' + " =" + PortfolioContract.StockQuote.COLUMN_SYMBOL,
                        selectionArgs
                );
                break;

            case STOCK_INCOME:
                rowsDeleted = db.delete(
                        PortfolioContract.StockIncome.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;

            case STOCK_INCOME_FOR_SYMBOL:
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
            case STOCK_SYMBOLS:
                rowsUpdated = db.update(PortfolioContract.StockSymbol.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;
            case STOCK_QUOTE:
                rowsUpdated = db.update(PortfolioContract.StockQuote.TABLE_NAME, values,
                        selection,
                        selectionArgs);
                break;
            case STOCK_INCOME:
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
            case STOCK_QUOTE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                PortfolioContract.StockQuote.TABLE_NAME,
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