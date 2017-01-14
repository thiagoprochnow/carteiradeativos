package br.com.carteira.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class WalletProvider extends ContentProvider {

    static final int STOCK_QUOTE = 100;
    static final int STOCK_QUOTE_FOR_SYMBOL = 101;

    static UriMatcher uriMatcher = buildUriMatcher();

    private DbHelper dbHelper;

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(WalletContract.AUTHORITY, WalletContract.PATH_STOCK_QUOTE, STOCK_QUOTE);
        matcher.addURI(WalletContract.AUTHORITY, WalletContract.PATH_STOCK_QUOTE_WITH_SYMBOL,
                STOCK_QUOTE_FOR_SYMBOL);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor returnCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case STOCK_QUOTE:
                returnCursor = db.query(
                        WalletContract.StockQuote.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case STOCK_QUOTE_FOR_SYMBOL:
                returnCursor = db.query(
                        WalletContract.StockQuote.TABLE_NAME,
                        projection,
                        WalletContract.StockQuote.COLUMN_SYMBOL + " = ?",
                        new String[]{WalletContract.StockQuote.getStockQuoteFromUri(uri)},
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

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case STOCK_QUOTE:
                db.insert(
                        WalletContract.StockQuote.TABLE_NAME,
                        null,
                        values
                );
                returnUri = WalletContract.StockQuote.URI;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        if (null == selection) selection = "1";
        switch (uriMatcher.match(uri)) {
            case STOCK_QUOTE:
                rowsDeleted = db.delete(
                        WalletContract.StockQuote.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;

            case STOCK_QUOTE_FOR_SYMBOL:
                String symbol = WalletContract.StockQuote.getStockQuoteFromUri(uri);
                rowsDeleted = db.delete(
                        WalletContract.StockQuote.TABLE_NAME,
                        '"' + symbol + '"' + " =" + WalletContract.StockQuote.COLUMN_SYMBOL,
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

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated;
        switch (uriMatcher.match(uri)) {
            case STOCK_QUOTE:
                rowsUpdated = db.update(WalletContract.StockQuote.TABLE_NAME, values,
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
                                WalletContract.StockQuote.TABLE_NAME,
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