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

        String builder_stock_symbols = "CREATE TABLE " + PortfolioContract.StockSymbol.TABLE_NAME + " (" +
                PortfolioContract.StockSymbol._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockSymbol.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                "UNIQUE (" + PortfolioContract.StockSymbol.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_stock_quote = "CREATE TABLE " + PortfolioContract.StockQuote.TABLE_NAME + " (" +
                PortfolioContract.StockQuote._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockQuote.COLUMN_SYMBOL + " INTEGER NOT NULL, " +
                PortfolioContract.StockQuote.COLUMN_QUANTITY + " INTEGER, " +
                PortfolioContract.StockQuote.COLUMN_BOUGHT_PRICE + " REAL, " +
                PortfolioContract.StockQuote.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                " FOREIGN KEY (" + PortfolioContract.StockQuote.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.StockSymbol.TABLE_NAME + " (" + PortfolioContract.StockSymbol._ID + "));";

        String builder_stock_income = "CREATE TABLE " + PortfolioContract.StockIncome.TABLE_NAME + " (" +
                PortfolioContract.StockIncome._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockIncome.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.StockIncome.COLUMN_TYPE + " TEXT NOT NULL, " +
                PortfolioContract.StockIncome.COLUMN_PER_STOCK + " REAL, " +
                PortfolioContract.StockIncome.COLUMN_PERCENT + " REAL);";

        db.execSQL(builder_stock_symbols);
        db.execSQL(builder_stock_quote);
        db.execSQL(builder_stock_income);

    }


    // Here is the code that is executed when db's VERSION is upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockSymbol.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockQuote.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockIncome.TABLE_NAME);
        onCreate(db);
    }
}
