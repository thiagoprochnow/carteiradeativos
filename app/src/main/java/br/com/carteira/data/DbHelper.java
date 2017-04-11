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

        String builder_portfolio = "CREATE TABLE " + PortfolioContract.Portfolio.TABLE_NAME + " (" +
                PortfolioContract.Portfolio._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.Portfolio.COLUMN_VALUE_TOTAL + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_VALUE_GAIN + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_VALUE_GAIN_PERCENT + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_INCOME_TOTAL + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_INCOME_PERCENT + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_TOTAL_GAIN_PERCENT + " REAL, " +
                "UNIQUE (" + PortfolioContract.Portfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_stock_portfolio = "CREATE TABLE " + PortfolioContract.StockPortfolio.TABLE_NAME + " (" +
                PortfolioContract.StockPortfolio._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockPortfolio.COLUMN_BUY_TOTAL + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_VARIATION_TOTAL + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_PORTFOLIO_PERCENT + " REAL, " +
                "UNIQUE (" + PortfolioContract.StockPortfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_stock_data = "CREATE TABLE " + PortfolioContract.StockData.TABLE_NAME + " (" +
                PortfolioContract.StockData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL + " INTEGER, " +
                PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.StockData.COLUMN_INCOME_TOTAL + " REAL, " +
                PortfolioContract.StockData.COLUMN_VARIATION + " REAL, " +
                PortfolioContract.StockData.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.StockData.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.StockData.COLUMN_CURRENT_PERCENT + " REAL, " +
                PortfolioContract.StockData.COLUMN_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.StockData.COLUMN_CURRENT_PRICE + " REAL, " +
                PortfolioContract.StockData.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.StockData.COLUMN_STATUS + " INTEGER, " +
                "UNIQUE (" + PortfolioContract.StockData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_stock_transaction = "CREATE TABLE " + PortfolioContract.StockTransaction.TABLE_NAME + " (" +
                PortfolioContract.StockTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockTransaction.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.StockTransaction.COLUMN_QUANTITY + " INTEGER, " +
                PortfolioContract.StockTransaction.COLUMN_PRICE + " REAL, " +
                PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.StockTransaction.COLUMN_TYPE + " INTEGER, " +
                " FOREIGN KEY (" + PortfolioContract.StockTransaction.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.StockData.TABLE_NAME + " (" + PortfolioContract.StockData._ID + "));";

        String builder_stock_income = "CREATE TABLE " + PortfolioContract.StockIncome.TABLE_NAME + " (" +
                PortfolioContract.StockIncome._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockIncome.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.StockIncome.COLUMN_TYPE + " INTEGER NOT NULL, " +
                PortfolioContract.StockIncome.COLUMN_PER_STOCK + " REAL, " +
                PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " LONG, " +
                PortfolioContract.StockIncome.COLUMN_RECEIVE_TOTAL + " REAL, " +
                PortfolioContract.StockIncome.COLUMN_TAX + " REAL, " +
                PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID + " REAL, " +
                PortfolioContract.StockIncome.COLUMN_AFFECTED_QUANTITY + " INTEGER, " +
                PortfolioContract.StockIncome.COLUMN_PERCENT + " REAL, " +
                " FOREIGN KEY (" + PortfolioContract.StockIncome.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.StockData.TABLE_NAME + " (" + PortfolioContract.StockData._ID + "));";

        db.execSQL(builder_portfolio);
        db.execSQL(builder_stock_portfolio);
        db.execSQL(builder_stock_data);
        db.execSQL(builder_stock_transaction);
        db.execSQL(builder_stock_income);

    }


    // Here is the code that is executed when db's VERSION is upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.Portfolio.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockPortfolio.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockData.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockTransaction.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockIncome.TABLE_NAME);
        onCreate(db);
    }
}
