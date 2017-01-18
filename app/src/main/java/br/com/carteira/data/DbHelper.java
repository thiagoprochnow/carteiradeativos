package br.com.carteira.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* DbHelper class that creates all tables and perform the db upgrade logic */

public class DbHelper extends SQLiteOpenHelper {


    // TODO: Need to change db name to the final app name or to anything meaningful
    static final String NAME = "Wallet.db";
    private static final int VERSION = 1;


    public DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // This is the Stock table skeleton.
        // We'll need to add/remove columns here to reflect the actual data we'll store in db.
        String builder = "CREATE TABLE " + WalletContract.StockQuote.TABLE_NAME + " (" +
                WalletContract.StockQuote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WalletContract.StockQuote.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                WalletContract.StockQuote.COLUMN_QUANTITY + " INTEGER, " +
                WalletContract.StockQuote.COLUMN_BOUGHT_TOTAL + " REAL, " +
                WalletContract.StockQuote.COLUMN_CURRENT_TOTAL + " REAL, " +
                WalletContract.StockQuote.COLUMN_APPRECIATION + " REAL, " +
                WalletContract.StockQuote.COLUMN_CURRENT_PERCENT + " REAL, " +
                WalletContract.StockQuote.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                WalletContract.StockQuote.COLUMN_TOTAL_INCOME + " REAL, " +
                WalletContract.StockQuote.COLUMN_TOTAL_GAIN + " REAL, " +
                "UNIQUE (" + WalletContract.StockQuote.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";
        db.execSQL(builder);

    }


    // Here is the code that is executed when db's VERSION is upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + WalletContract.StockQuote.TABLE_NAME);
        onCreate(db);
    }
}
