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
                PortfolioContract.Portfolio.COLUMN_INCOME_TOTAL + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_TOTAL_GAIN + " REAL, " +
                "UNIQUE (" + PortfolioContract.Portfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_stock_portfolio = "CREATE TABLE " + PortfolioContract.StockPortfolio.TABLE_NAME + " (" +
                PortfolioContract.StockPortfolio._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockPortfolio.COLUMN_BUY_TOTAL + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_SOLD_TOTAL + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_VARIATION_TOTAL + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_PORTFOLIO_PERCENT + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_CURRENT_TOTAL + " REAL, " +
                "UNIQUE (" + PortfolioContract.StockPortfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_stock_data = "CREATE TABLE " + PortfolioContract.StockData.TABLE_NAME + " (" +
                PortfolioContract.StockData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL + " INTEGER, " +
                PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.StockData.COLUMN_NET_INCOME + " REAL, " +
                PortfolioContract.StockData.COLUMN_INCOME_TAX + " REAL, " +
                PortfolioContract.StockData.COLUMN_VARIATION + " REAL, " +
                PortfolioContract.StockData.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.StockData.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.StockData.COLUMN_CURRENT_PERCENT + " REAL, " +
                PortfolioContract.StockData.COLUMN_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.StockData.COLUMN_CURRENT_PRICE + " REAL, " +
                PortfolioContract.StockData.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.StockData.COLUMN_STATUS + " INTEGER, " +
                "UNIQUE (" + PortfolioContract.StockData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_sold_stock_data = "CREATE TABLE " + PortfolioContract.SoldStockData.TABLE_NAME + " (" +
                PortfolioContract.SoldStockData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.SoldStockData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.SoldStockData.COLUMN_QUANTITY_TOTAL + " INTEGER, " +
                PortfolioContract.SoldStockData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.SoldStockData.COLUMN_SELL_GAIN + " REAL, " +
                PortfolioContract.SoldStockData.COLUMN_SELL_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.SoldStockData.COLUMN_SELL_TOTAL + " REAL, " +
                "UNIQUE (" + PortfolioContract.SoldStockData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

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
                " FOREIGN KEY (" + PortfolioContract.StockIncome.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.StockData.TABLE_NAME + " (" + PortfolioContract.StockData._ID + "));";

        String builder_fii_portfolio = "CREATE TABLE " + PortfolioContract.FiiPortfolio.TABLE_NAME + " (" +
                PortfolioContract.FiiPortfolio._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.FiiPortfolio.COLUMN_BUY_TOTAL + " REAL, " +
                PortfolioContract.FiiPortfolio.COLUMN_SOLD_TOTAL + " REAL, " +
                PortfolioContract.FiiPortfolio.COLUMN_VARIATION_TOTAL + " REAL, " +
                PortfolioContract.FiiPortfolio.COLUMN_INCOME_TOTAL + " REAL, " +
                PortfolioContract.FiiPortfolio.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.FiiPortfolio.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.FiiPortfolio.COLUMN_PORTFOLIO_PERCENT + " REAL, " +
                PortfolioContract.FiiPortfolio.COLUMN_CURRENT_TOTAL + " REAL, " +
                "UNIQUE (" + PortfolioContract.FiiPortfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_fii_data = "CREATE TABLE " + PortfolioContract.FiiData.TABLE_NAME + " (" +
                PortfolioContract.FiiData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.FiiData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.FiiData.COLUMN_QUANTITY_TOTAL + " INTEGER, " +
                PortfolioContract.FiiData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.FiiData.COLUMN_INCOME + " REAL, " +
                PortfolioContract.FiiData.COLUMN_INCOME_TAX + " REAL, " +
                PortfolioContract.FiiData.COLUMN_VARIATION + " REAL, " +
                PortfolioContract.FiiData.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.FiiData.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.FiiData.COLUMN_CURRENT_PERCENT + " REAL, " +
                PortfolioContract.FiiData.COLUMN_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.FiiData.COLUMN_CURRENT_PRICE + " REAL, " +
                PortfolioContract.FiiData.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.FiiData.COLUMN_STATUS + " INTEGER, " +
                "UNIQUE (" + PortfolioContract.FiiData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_sold_fii_data = "CREATE TABLE " + PortfolioContract.SoldFiiData.TABLE_NAME + " (" +
                PortfolioContract.SoldFiiData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.SoldFiiData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.SoldFiiData.COLUMN_QUANTITY_TOTAL + " INTEGER, " +
                PortfolioContract.SoldFiiData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.SoldFiiData.COLUMN_SELL_GAIN + " REAL, " +
                PortfolioContract.SoldFiiData.COLUMN_SELL_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.SoldFiiData.COLUMN_SELL_TOTAL + " REAL, " +
                "UNIQUE (" + PortfolioContract.SoldFiiData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_fii_transaction = "CREATE TABLE " + PortfolioContract.FiiTransaction.TABLE_NAME + " (" +
                PortfolioContract.FiiTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.FiiTransaction.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.FiiTransaction.COLUMN_QUANTITY + " INTEGER, " +
                PortfolioContract.FiiTransaction.COLUMN_PRICE + " REAL, " +
                PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.FiiTransaction.COLUMN_TYPE + " INTEGER, " +
                " FOREIGN KEY (" + PortfolioContract.FiiTransaction.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.FiiData.TABLE_NAME + " (" + PortfolioContract.FiiData._ID + "));";

        String builder_fii_income = "CREATE TABLE " + PortfolioContract.FiiIncome.TABLE_NAME + " (" +
                PortfolioContract.FiiIncome._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.FiiIncome.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.FiiIncome.COLUMN_TYPE + " INTEGER NOT NULL, " +
                PortfolioContract.FiiIncome.COLUMN_PER_FII + " REAL, " +
                PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " LONG, " +
                PortfolioContract.FiiIncome.COLUMN_RECEIVE_TOTAL + " REAL, " +
                PortfolioContract.FiiIncome.COLUMN_TAX + " REAL, " +
                PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID + " REAL, " +
                PortfolioContract.FiiIncome.COLUMN_AFFECTED_QUANTITY + " INTEGER, " +
                " FOREIGN KEY (" + PortfolioContract.FiiIncome.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.FiiData.TABLE_NAME + " (" + PortfolioContract.FiiData._ID + "));";

        String builder_currency_portfolio = "CREATE TABLE " + PortfolioContract.CurrencyPortfolio.TABLE_NAME + " (" +
                PortfolioContract.CurrencyPortfolio._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.CurrencyPortfolio.COLUMN_BUY_TOTAL + " REAL, " +
                PortfolioContract.CurrencyPortfolio.COLUMN_SOLD_TOTAL + " REAL, " +
                PortfolioContract.CurrencyPortfolio.COLUMN_VARIATION_TOTAL + " REAL, " +
                PortfolioContract.CurrencyPortfolio.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.CurrencyPortfolio.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.CurrencyPortfolio.COLUMN_PORTFOLIO_PERCENT + " REAL, " +
                PortfolioContract.CurrencyPortfolio.COLUMN_CURRENT_TOTAL + " REAL, " +
                "UNIQUE (" + PortfolioContract.CurrencyPortfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_currency_data = "CREATE TABLE " + PortfolioContract.CurrencyData.TABLE_NAME + " (" +
                PortfolioContract.CurrencyData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.CurrencyData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.CurrencyData.COLUMN_QUANTITY_TOTAL + " INTEGER, " +
                PortfolioContract.CurrencyData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_VARIATION + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_CURRENT_PERCENT + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_CURRENT_PRICE + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_STATUS + " INTEGER, " +
                "UNIQUE (" + PortfolioContract.CurrencyData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_sold_currency_data = "CREATE TABLE " + PortfolioContract.SoldCurrencyData.TABLE_NAME + " (" +
                PortfolioContract.SoldCurrencyData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.SoldCurrencyData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_QUANTITY_TOTAL + " INTEGER, " +
                PortfolioContract.SoldCurrencyData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_SELL_GAIN + " REAL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_SELL_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_SELL_TOTAL + " REAL, " +
                "UNIQUE (" + PortfolioContract.SoldCurrencyData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_currency_transaction = "CREATE TABLE " + PortfolioContract.CurrencyTransaction.TABLE_NAME + " (" +
                PortfolioContract.CurrencyTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY + " INTEGER, " +
                PortfolioContract.CurrencyTransaction.COLUMN_PRICE + " REAL, " +
                PortfolioContract.CurrencyTransaction.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.CurrencyTransaction.COLUMN_TYPE + " INTEGER, " +
                " FOREIGN KEY (" + PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.CurrencyData.TABLE_NAME + " (" + PortfolioContract.CurrencyData._ID + "));";

        String builder_fixed_portfolio = "CREATE TABLE " + PortfolioContract.FixedPortfolio.TABLE_NAME + " (" +
                PortfolioContract.FixedPortfolio._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.FixedPortfolio.COLUMN_BUY_TOTAL + " REAL, " +
                PortfolioContract.FixedPortfolio.COLUMN_SOLD_TOTAL + " REAL, " +
                PortfolioContract.FixedPortfolio.COLUMN_VARIATION_TOTAL + " REAL, " +
                PortfolioContract.FixedPortfolio.COLUMN_INCOME_TOTAL + " REAL, " +
                PortfolioContract.FixedPortfolio.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.FixedPortfolio.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.FixedPortfolio.COLUMN_PORTFOLIO_PERCENT + " REAL, " +
                PortfolioContract.FixedPortfolio.COLUMN_CURRENT_TOTAL + " REAL, " +
                "UNIQUE (" + PortfolioContract.FixedPortfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_fixed_data = "CREATE TABLE " + PortfolioContract.FixedData.TABLE_NAME + " (" +
                PortfolioContract.FixedData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.FixedData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.FixedData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.FixedData.COLUMN_SELL_VALUE_TOTAL + " REAL, " +
                PortfolioContract.FixedData.COLUMN_TAX + " REAL, " +
                PortfolioContract.FixedData.COLUMN_NET_GAIN + " REAL, " +
                PortfolioContract.FixedData.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.FixedData.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.FixedData.COLUMN_CURRENT_PERCENT + " REAL, " +
                PortfolioContract.FixedData.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.FixedData.COLUMN_STATUS + " INTEGER, " +
                "UNIQUE (" + PortfolioContract.FixedData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_fixed_transaction = "CREATE TABLE " + PortfolioContract.FixedTransaction.TABLE_NAME + " (" +
                PortfolioContract.FixedTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.FixedTransaction.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.FixedTransaction.COLUMN_TOTAL + " REAL, " +
                PortfolioContract.FixedTransaction.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.FixedTransaction.COLUMN_TYPE + " INTEGER, " +
                " FOREIGN KEY (" + PortfolioContract.FixedTransaction.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.FixedData.TABLE_NAME + " (" + PortfolioContract.FixedData._ID + "));";

        db.execSQL(builder_portfolio);
        db.execSQL(builder_stock_portfolio);
        db.execSQL(builder_stock_data);
        db.execSQL(builder_sold_stock_data);
        db.execSQL(builder_stock_transaction);
        db.execSQL(builder_stock_income);
        db.execSQL(builder_fii_portfolio);
        db.execSQL(builder_fii_data);
        db.execSQL(builder_sold_fii_data);
        db.execSQL(builder_fii_transaction);
        db.execSQL(builder_fii_income);
        db.execSQL(builder_currency_portfolio);
        db.execSQL(builder_currency_data);
        db.execSQL(builder_sold_currency_data);
        db.execSQL(builder_currency_transaction);
        db.execSQL(builder_fixed_portfolio);
        db.execSQL(builder_fixed_data);
        db.execSQL(builder_fixed_transaction);

    }


    // Here is the code that is executed when db's VERSION is upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.Portfolio.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockPortfolio.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockData.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.SoldStockData.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockTransaction.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.StockIncome.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.FiiPortfolio.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.FiiData.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.SoldFiiData.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.FiiTransaction.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.FiiIncome.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.CurrencyPortfolio.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.CurrencyData.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.SoldCurrencyData.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.CurrencyTransaction.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.FixedPortfolio.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.FixedData.TABLE_NAME);
        db.execSQL(" DROP TABLE IF EXISTS " + PortfolioContract.FixedTransaction.TABLE_NAME);
        onCreate(db);
    }
}
