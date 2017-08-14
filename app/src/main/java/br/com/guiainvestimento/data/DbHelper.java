package br.com.guiainvestimento.data;

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
                PortfolioContract.Portfolio.COLUMN_BUY_TOTAL + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_SOLD_TOTAL + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_VARIATION_TOTAL + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_INCOME_TOTAL + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_TREASURY_PERCENT + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_FIXED_PERCENT + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_OTHERS_PERCENT + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_STOCK_PERCENT + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_FII_PERCENT + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_CURRENCY_PERCENT + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_TAX + " REAL, " +
                PortfolioContract.Portfolio.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.Portfolio.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.Portfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_portfolio_growth = "CREATE TABLE " + PortfolioContract.PortfolioGrowth.TABLE_NAME + " (" +
                PortfolioContract.PortfolioGrowth._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.PortfolioGrowth.COLUMN_TOTAL + " REAL, " +
                PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.PortfolioGrowth.COLUMN_TYPE + " INTEGER NOT NULL, " +
                "UNIQUE (" + PortfolioContract.PortfolioGrowth._ID + ") ON CONFLICT REPLACE);";

        String builder_income_growth = "CREATE TABLE " + PortfolioContract.IncomeGrowth.TABLE_NAME + " (" +
                PortfolioContract.IncomeGrowth._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.IncomeGrowth.COLUMN_TOTAL + " REAL, " +
                PortfolioContract.IncomeGrowth.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.IncomeGrowth.COLUMN_TYPE + " INTEGER NOT NULL, " +
                "UNIQUE (" + PortfolioContract.IncomeGrowth._ID + ") ON CONFLICT REPLACE);";

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
                PortfolioContract.StockPortfolio.COLUMN_TAX + " REAL, " +
                PortfolioContract.StockPortfolio.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.StockPortfolio.LAST_UPDATE + " LONG, " +
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
                PortfolioContract.StockData.COLUMN_TAX + " REAL, " +
                PortfolioContract.StockData.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.StockData.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.StockData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_sold_stock_data = "CREATE TABLE " + PortfolioContract.SoldStockData.TABLE_NAME + " (" +
                PortfolioContract.SoldStockData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.SoldStockData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.SoldStockData.COLUMN_QUANTITY_TOTAL + " INTEGER, " +
                PortfolioContract.SoldStockData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.SoldStockData.COLUMN_SELL_GAIN + " REAL, " +
                PortfolioContract.SoldStockData.COLUMN_SELL_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.SoldStockData.COLUMN_SELL_TOTAL + " REAL, " +
                PortfolioContract.SoldStockData.COLUMN_TAX + " REAL, " +
                PortfolioContract.SoldStockData.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.SoldStockData.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.SoldStockData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_stock_transaction = "CREATE TABLE " + PortfolioContract.StockTransaction.TABLE_NAME + " (" +
                PortfolioContract.StockTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.StockTransaction.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.StockTransaction.COLUMN_QUANTITY + " INTEGER, " +
                PortfolioContract.StockTransaction.COLUMN_PRICE + " REAL, " +
                PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.StockTransaction.COLUMN_TYPE + " INTEGER, " +
                PortfolioContract.StockTransaction.COLUMN_TAX + " REAL, " +
                PortfolioContract.StockTransaction.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.StockTransaction.LAST_UPDATE + " LONG, " +
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
                PortfolioContract.StockIncome.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.StockIncome.LAST_UPDATE + " LONG, " +
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
                PortfolioContract.FiiPortfolio.COLUMN_TAX + " REAL, " +
                PortfolioContract.FiiPortfolio.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.FiiPortfolio.LAST_UPDATE + " LONG, " +
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
                PortfolioContract.FiiData.COLUMN_TAX + " REAL, " +
                PortfolioContract.FiiData.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.FiiData.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.FiiData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_sold_fii_data = "CREATE TABLE " + PortfolioContract.SoldFiiData.TABLE_NAME + " (" +
                PortfolioContract.SoldFiiData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.SoldFiiData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.SoldFiiData.COLUMN_QUANTITY_TOTAL + " INTEGER, " +
                PortfolioContract.SoldFiiData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.SoldFiiData.COLUMN_SELL_GAIN + " REAL, " +
                PortfolioContract.SoldFiiData.COLUMN_SELL_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.SoldFiiData.COLUMN_SELL_TOTAL + " REAL, " +
                PortfolioContract.SoldFiiData.COLUMN_TAX + " REAL, " +
                PortfolioContract.SoldFiiData.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.SoldFiiData.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.SoldFiiData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_fii_transaction = "CREATE TABLE " + PortfolioContract.FiiTransaction.TABLE_NAME + " (" +
                PortfolioContract.FiiTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.FiiTransaction.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.FiiTransaction.COLUMN_QUANTITY + " INTEGER, " +
                PortfolioContract.FiiTransaction.COLUMN_PRICE + " REAL, " +
                PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.FiiTransaction.COLUMN_TYPE + " INTEGER, " +
                PortfolioContract.FiiTransaction.COLUMN_TAX + " REAL, " +
                PortfolioContract.FiiTransaction.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.FiiTransaction.LAST_UPDATE + " LONG, " +
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
                PortfolioContract.FiiIncome.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.FiiIncome.LAST_UPDATE + " LONG, " +
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
                PortfolioContract.CurrencyPortfolio.COLUMN_TAX + " REAL, " +
                PortfolioContract.CurrencyPortfolio.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.CurrencyPortfolio.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.CurrencyPortfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_currency_data = "CREATE TABLE " + PortfolioContract.CurrencyData.TABLE_NAME + " (" +
                PortfolioContract.CurrencyData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.CurrencyData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.CurrencyData.COLUMN_QUANTITY_TOTAL + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_VARIATION + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_CURRENT_PERCENT + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_CURRENT_PRICE + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_STATUS + " INTEGER, " +
                PortfolioContract.CurrencyData.COLUMN_TAX + " REAL, " +
                PortfolioContract.CurrencyData.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.CurrencyData.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.CurrencyData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_sold_currency_data = "CREATE TABLE " + PortfolioContract.SoldCurrencyData.TABLE_NAME + " (" +
                PortfolioContract.SoldCurrencyData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.SoldCurrencyData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_QUANTITY_TOTAL + " REAL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_SELL_GAIN + " REAL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_SELL_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_SELL_TOTAL + " REAL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_TAX + " REAL, " +
                PortfolioContract.SoldCurrencyData.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.SoldCurrencyData.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.SoldCurrencyData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_currency_transaction = "CREATE TABLE " + PortfolioContract.CurrencyTransaction.TABLE_NAME + " (" +
                PortfolioContract.CurrencyTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY + " REAL, " +
                PortfolioContract.CurrencyTransaction.COLUMN_PRICE + " REAL, " +
                PortfolioContract.CurrencyTransaction.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.CurrencyTransaction.COLUMN_TYPE + " INTEGER, " +
                PortfolioContract.CurrencyTransaction.COLUMN_TAX + " REAL, " +
                PortfolioContract.CurrencyTransaction.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.CurrencyTransaction.LAST_UPDATE + " LONG, " +
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
                PortfolioContract.FixedPortfolio.COLUMN_TAX + " REAL, " +
                PortfolioContract.FixedPortfolio.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.FixedPortfolio.LAST_UPDATE + " LONG, " +
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
                PortfolioContract.FixedData.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.FixedData.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.FixedData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_fixed_transaction = "CREATE TABLE " + PortfolioContract.FixedTransaction.TABLE_NAME + " (" +
                PortfolioContract.FixedTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.FixedTransaction.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.FixedTransaction.COLUMN_TOTAL + " REAL, " +
                PortfolioContract.FixedTransaction.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.FixedTransaction.COLUMN_TYPE + " INTEGER, " +
                PortfolioContract.FixedTransaction.COLUMN_TAX + " REAL, " +
                PortfolioContract.FixedTransaction.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.FixedTransaction.LAST_UPDATE + " LONG, " +
                " FOREIGN KEY (" + PortfolioContract.FixedTransaction.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.FixedData.TABLE_NAME + " (" + PortfolioContract.FixedData._ID + "));";

        String builder_treasury_portfolio = "CREATE TABLE " + PortfolioContract.TreasuryPortfolio.TABLE_NAME + " (" +
                PortfolioContract.TreasuryPortfolio._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.TreasuryPortfolio.COLUMN_BUY_TOTAL + " REAL, " +
                PortfolioContract.TreasuryPortfolio.COLUMN_SOLD_TOTAL + " REAL, " +
                PortfolioContract.TreasuryPortfolio.COLUMN_VARIATION_TOTAL + " REAL, " +
                PortfolioContract.TreasuryPortfolio.COLUMN_INCOME_TOTAL + " REAL, " +
                PortfolioContract.TreasuryPortfolio.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.TreasuryPortfolio.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.TreasuryPortfolio.COLUMN_PORTFOLIO_PERCENT + " REAL, " +
                PortfolioContract.TreasuryPortfolio.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.TreasuryPortfolio.COLUMN_TAX + " REAL, " +
                PortfolioContract.TreasuryPortfolio.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.TreasuryPortfolio.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.TreasuryPortfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_treasury_data = "CREATE TABLE " + PortfolioContract.TreasuryData.TABLE_NAME + " (" +
                PortfolioContract.TreasuryData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.TreasuryData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.TreasuryData.COLUMN_QUANTITY_TOTAL + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_INCOME + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_INCOME_TAX + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_VARIATION + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_CURRENT_PERCENT + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_CURRENT_PRICE + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_STATUS + " INTEGER, " +
                PortfolioContract.TreasuryData.COLUMN_TAX + " REAL, " +
                PortfolioContract.TreasuryData.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.TreasuryData.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.TreasuryData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_sold_treasury_data = "CREATE TABLE " + PortfolioContract.SoldTreasuryData.TABLE_NAME + " (" +
                PortfolioContract.SoldTreasuryData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.SoldTreasuryData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.SoldTreasuryData.COLUMN_QUANTITY_TOTAL + " REAL, " +
                PortfolioContract.SoldTreasuryData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.SoldTreasuryData.COLUMN_SELL_GAIN + " REAL, " +
                PortfolioContract.SoldTreasuryData.COLUMN_SELL_MEDIUM_PRICE + " REAL, " +
                PortfolioContract.SoldTreasuryData.COLUMN_SELL_TOTAL + " REAL, " +
                PortfolioContract.SoldTreasuryData.COLUMN_TAX + " REAL, " +
                PortfolioContract.SoldTreasuryData.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.SoldTreasuryData.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.SoldTreasuryData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_treasury_transaction = "CREATE TABLE " + PortfolioContract.TreasuryTransaction.TABLE_NAME + " (" +
                PortfolioContract.TreasuryTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY + " REAL, " +
                PortfolioContract.TreasuryTransaction.COLUMN_PRICE + " REAL, " +
                PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.TreasuryTransaction.COLUMN_TYPE + " INTEGER, " +
                PortfolioContract.TreasuryTransaction.COLUMN_TAX + " REAL, " +
                PortfolioContract.TreasuryTransaction.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.TreasuryTransaction.LAST_UPDATE + " LONG, " +
                " FOREIGN KEY (" + PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.TreasuryData.TABLE_NAME + " (" + PortfolioContract.TreasuryData._ID + "));";

        String builder_treasury_income = "CREATE TABLE " + PortfolioContract.TreasuryIncome.TABLE_NAME + " (" +
                PortfolioContract.TreasuryIncome._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.TreasuryIncome.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.TreasuryIncome.COLUMN_TYPE + " INTEGER NOT NULL, " +
                PortfolioContract.TreasuryIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " LONG, " +
                PortfolioContract.TreasuryIncome.COLUMN_RECEIVE_TOTAL + " REAL, " +
                PortfolioContract.TreasuryIncome.COLUMN_TAX + " REAL, " +
                PortfolioContract.TreasuryIncome.COLUMN_RECEIVE_LIQUID + " REAL, " +
                PortfolioContract.TreasuryIncome.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.TreasuryIncome.LAST_UPDATE + " LONG, " +
                " FOREIGN KEY (" + PortfolioContract.TreasuryIncome.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.TreasuryData.TABLE_NAME + " (" + PortfolioContract.TreasuryData._ID + "));";

        String builder_others_portfolio = "CREATE TABLE " + PortfolioContract.OthersPortfolio.TABLE_NAME + " (" +
                PortfolioContract.OthersPortfolio._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.OthersPortfolio.COLUMN_BUY_TOTAL + " REAL, " +
                PortfolioContract.OthersPortfolio.COLUMN_SOLD_TOTAL + " REAL, " +
                PortfolioContract.OthersPortfolio.COLUMN_VARIATION_TOTAL + " REAL, " +
                PortfolioContract.OthersPortfolio.COLUMN_INCOME_TOTAL + " REAL, " +
                PortfolioContract.OthersPortfolio.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.OthersPortfolio.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.OthersPortfolio.COLUMN_PORTFOLIO_PERCENT + " REAL, " +
                PortfolioContract.OthersPortfolio.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.OthersPortfolio.COLUMN_TAX + " REAL, " +
                PortfolioContract.OthersPortfolio.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.OthersPortfolio.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.OthersPortfolio._ID + ") ON CONFLICT REPLACE);";

        String builder_others_data = "CREATE TABLE " + PortfolioContract.OthersData.TABLE_NAME + " (" +
                PortfolioContract.OthersData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.OthersData.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.OthersData.COLUMN_BUY_VALUE_TOTAL + " REAL, " +
                PortfolioContract.OthersData.COLUMN_SELL_VALUE_TOTAL + " REAL, " +
                PortfolioContract.OthersData.COLUMN_TAX + " REAL, " +
                PortfolioContract.OthersData.COLUMN_NET_GAIN + " REAL, " +
                PortfolioContract.OthersData.COLUMN_VARIATION + " REAL, " +
                PortfolioContract.OthersData.COLUMN_TOTAL_GAIN + " REAL, " +
                PortfolioContract.OthersData.COLUMN_OBJECTIVE_PERCENT + " REAL, " +
                PortfolioContract.OthersData.COLUMN_CURRENT_PERCENT + " REAL, " +
                PortfolioContract.OthersData.COLUMN_CURRENT_TOTAL + " REAL, " +
                PortfolioContract.OthersData.COLUMN_STATUS + " INTEGER, " +
                PortfolioContract.OthersData.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.OthersData.COLUMN_INCOME + " REAL, " +
                PortfolioContract.OthersData.COLUMN_INCOME_TAX + " REAL, " +
                PortfolioContract.OthersData.LAST_UPDATE + " LONG, " +
                "UNIQUE (" + PortfolioContract.OthersData.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        String builder_others_transaction = "CREATE TABLE " + PortfolioContract.OthersTransaction.TABLE_NAME + " (" +
                PortfolioContract.OthersTransaction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.OthersTransaction.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.OthersTransaction.COLUMN_TOTAL + " REAL, " +
                PortfolioContract.OthersTransaction.COLUMN_TIMESTAMP + " LONG, " +
                PortfolioContract.OthersTransaction.COLUMN_TYPE + " INTEGER, " +
                PortfolioContract.OthersTransaction.COLUMN_TAX + " REAL, " +
                PortfolioContract.OthersTransaction.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.OthersTransaction.LAST_UPDATE + " LONG, " +
                " FOREIGN KEY (" + PortfolioContract.OthersTransaction.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.OthersData.TABLE_NAME + " (" + PortfolioContract.OthersData._ID + "));";

        String builder_others_income = "CREATE TABLE " + PortfolioContract.OthersIncome.TABLE_NAME + " (" +
                PortfolioContract.OthersIncome._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PortfolioContract.OthersIncome.COLUMN_SYMBOL + " TEXT NOT NULL, " +
                PortfolioContract.OthersIncome.COLUMN_TYPE + " INTEGER NOT NULL, " +
                PortfolioContract.OthersIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " LONG, " +
                PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL + " REAL, " +
                PortfolioContract.OthersIncome.COLUMN_TAX + " REAL, " +
                PortfolioContract.OthersIncome.COLUMN_BROKERAGE + " REAL, " +
                PortfolioContract.OthersIncome.LAST_UPDATE + " LONG, " +
                " FOREIGN KEY (" + PortfolioContract.OthersIncome.COLUMN_SYMBOL + ") REFERENCES "
                + PortfolioContract.OthersData.TABLE_NAME + " (" + PortfolioContract.OthersData._ID + "));";

        db.execSQL(builder_portfolio);
        db.execSQL(builder_portfolio_growth);
        db.execSQL(builder_income_growth);
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
        db.execSQL(builder_treasury_portfolio);
        db.execSQL(builder_treasury_data);
        db.execSQL(builder_sold_treasury_data);
        db.execSQL(builder_treasury_transaction);
        db.execSQL(builder_treasury_income);
        db.execSQL(builder_others_portfolio);
        db.execSQL(builder_others_data);
        db.execSQL(builder_others_transaction);
        db.execSQL(builder_others_income);
    }


    // Here is the code that is executed when db's VERSION is upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
