package br.com.carteira.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/* DbHelper class that creates all tables and perform the db upgrade logic */

public class DbHelper extends SQLiteOpenHelper {


    // TODO: Need to change db name to the final app name or to anything meaningful
    static final String NAME = "Portfolio.db";
    private static final int VERSION = 1;


    public DbHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // This is the Stock table skeleton.
        // We'll need to add/remove columns here to reflect the actual data we'll store in db.
        String builder = "CREATE TABLE " + PortfolioContract.StockQuote.TABLE_NAME + " (" +
                PortfolioContract.StockQuote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockQuote.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.StockQuote.COLUMN_QUANTITY + " INTEGER, " +
                PortfolioContract.StockQuote.COLUMN_BOUGHT_TOTAL + " REAL, " +
                PortfolioContract.StockQuote.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.StockQuote.COLUMN_APPRECIATION + " REAL, " +
                PortfolioContract.StockQuote.COLUMN_CURRENT_PERCENT + " REAL, " +
                PortfolioContract.StockQuote.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.StockQuote.COLUMN_TOTAL_INCOME + " REAL, " +
                PortfolioContract.StockQuote.COLUMN_TOTAL_GAIN + " REAL, " +
                "UNIQUE (" + PortfolioContract.StockQuote.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";
        db.execSQL(builder);

    }


    // Here is the code that is executed when db's VERSION is upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockQuote.TABLE_NAME);
        onCreate(db);
    }
}
