package br.com.carteira.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/* Contract class with helper methods and constants for the provier */
public class PortfolioContract {

    // TODO: We'll need to update this AUTHOROT with the final package name of the app
    public static final String AUTHORITY = "br.com.carteira";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String BASE_BULK_UPDATE = "update";

    public static final String PATH_PORTFOLIO = "portfolio";

    public static final String PATH_STOCK_PORTFOLIO = "stock_portfolio";

    public static final String PATH_STOCK_DATA = "stock_data";
    public static final String PATH_STOCK_DATA_BULK_UPDATE = "stock_data/update";
    public static final String PATH_STOCK_DATA_BULK_UPDATE_WITH_CURRENT = "stock_data/update/*";
    public static final String PATH_STOCK_DATA_WITH_SYMBOL = "stock_data/*";

    public static final String PATH_SOLD_STOCK_DATA = "sold_stock_data";
    public static final String PATH_SOLD_STOCK_DATA_WITH_SYMBOL = "sold_stock_data/*";

    public static final String PATH_STOCK_TRANSACTION = "stock_transaction";
    public static final String PATH_STOCK_TRANSACTION_WITH_SYMBOL = "stock_transaction/*";

    public static final String PATH_STOCK_INCOME = "stock_income";
    public static final String PATH_STOCK_INCOME_WITH_SYMBOL = "stock_income/*";

    public static final String PATH_FII_PORTFOLIO = "fii_portfolio";

    public static final String PATH_FII_DATA = "fii_data";
    public static final String PATH_FII_DATA_BULK_UPDATE = "fii_data/update";
    public static final String PATH_FII_DATA_BULK_UPDATE_WITH_CURRENT = "fii_data/update/*";
    public static final String PATH_FII_DATA_WITH_SYMBOL = "fii_data/*";

    public static final String PATH_SOLD_FII_DATA = "sold_fii_data";
    public static final String PATH_SOLD_FII_DATA_WITH_SYMBOL = "sold_fii_data/*";

    public static final String PATH_FII_TRANSACTION = "fii_transaction";
    public static final String PATH_FII_TRANSACTION_WITH_SYMBOL = "fii_transaction/*";

    public static final String PATH_FII_INCOME = "fii_income";
    public static final String PATH_FII_INCOME_WITH_SYMBOL = "fii_income/*";

    public static final String PATH_CURRENCY_PORTFOLIO = "currency_portfolio";

    public static final String PATH_CURRENCY_DATA = "currency_data";
    public static final String PATH_CURRENCY_DATA_BULK_UPDATE = "currency_data/update";
    public static final String PATH_CURRENCY_DATA_BULK_UPDATE_WITH_CURRENT = "currency_data/update/*";
    public static final String PATH_CURRENCY_DATA_WITH_SYMBOL = "currency_data/*";

    public static final String PATH_SOLD_CURRENCY_DATA = "sold_currency_data";
    public static final String PATH_SOLD_CURRENCY_DATA_WITH_SYMBOL = "sold_currency_data/*";

    public static final String PATH_CURRENCY_TRANSACTION = "currency_transaction";
    public static final String PATH_CURRENCY_TRANSACTION_WITH_SYMBOL = "currency_transaction/*";

    public static final String PATH_FIXED_PORTFOLIO = "fixed_portfolio";

    public static final String PATH_FIXED_DATA = "fixed_data";
    public static final String PATH_FIXED_DATA_BULK_UPDATE = "fixed_data/update";
    public static final String PATH_FIXED_DATA_BULK_UPDATE_WITH_CURRENT = "fixed_data/update/*";
    public static final String PATH_FIXED_DATA_WITH_SYMBOL = "fixed_data/*";

    public static final String PATH_FIXED_TRANSACTION = "fixed_transaction";
    public static final String PATH_FIXED_TRANSACTION_WITH_SYMBOL = "fixed_transaction/*";

    public static final String PATH_TREASURY_PORTFOLIO = "treasury_portfolio";

    public static final String PATH_TREASURY_DATA = "treasury_data";
    public static final String PATH_TREASURY_DATA_BULK_UPDATE = "treasury_data/update";
    public static final String PATH_TREASURY_DATA_BULK_UPDATE_WITH_CURRENT = "treasury_data/update/*";
    public static final String PATH_TREASURY_DATA_WITH_SYMBOL = "treasury_data/*";

    public static final String PATH_SOLD_TREASURY_DATA = "sold_treasury_data";
    public static final String PATH_SOLD_TREASURY_DATA_WITH_SYMBOL = "sold_treasury_data/*";

    public static final String PATH_TREASURY_TRANSACTION = "treasury_transaction";
    public static final String PATH_TREASURY_TRANSACTION_WITH_SYMBOL = "treasury_transaction/*";

    public static final String PATH_TREASURY_INCOME = "treasury_income";
    public static final String PATH_TREASURY_INCOME_WITH_SYMBOL = "treasury_income/*";

    // Table with information of whole user portfolio
    public static final class Portfolio implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_PORTFOLIO).build();

        public static final String TABLE_NAME = "portfolio";

        public static final String COLUMN_BUY_TOTAL = "buy_total";
        public static final String COLUMN_SOLD_TOTAL = "sold_total";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_VARIATION_TOTAL = "variation_total";
        public static final String COLUMN_INCOME_TOTAL = "income_total";
        public static final String COLUMN_TOTAL_GAIN = "total_gain";
        public static final String COLUMN_TREASURY_PERCENT = "treasury_percent";
        public static final String COLUMN_FIXED_PERCENT = "fixed_percent";
        public static final String COLUMN_STOCK_PERCENT = "stock_percent";
        public static final String COLUMN_FII_PERCENT = "fii_percent";
        public static final String COLUMN_CURRENCY_PERCENT = "currency_percent";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] PORTFOLIO_COLUMNS = {
                _ID,
                COLUMN_BUY_TOTAL,
                COLUMN_SOLD_TOTAL,
                COLUMN_CURRENT_TOTAL,
                COLUMN_VARIATION_TOTAL,
                COLUMN_INCOME_TOTAL,
                COLUMN_TOTAL_GAIN,
                COLUMN_TREASURY_PERCENT,
                COLUMN_FIXED_PERCENT,
                COLUMN_STOCK_PERCENT,
                COLUMN_FII_PERCENT,
                COLUMN_CURRENCY_PERCENT,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForPortfolio(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildPortfolioUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getPortfolioFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of whole stock portfolio
    public static final class StockPortfolio implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_PORTFOLIO).build();

        public static final String TABLE_NAME = "stock_portfolio";

        public static final String COLUMN_BUY_TOTAL = "value_total";
        public static final String COLUMN_SOLD_TOTAL = "sold_total";
        public static final String COLUMN_VARIATION_TOTAL = "variation_total";
        public static final String COLUMN_INCOME_TOTAL = "income_total";
        public static final String COLUMN_TOTAL_GAIN = "value_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_PORTFOLIO_PERCENT = "portfolio_percent";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] STOCK_PORTFOLIO_COLUMNS = {
                _ID,
                COLUMN_BUY_TOTAL,
                COLUMN_SOLD_TOTAL,
                COLUMN_VARIATION_TOTAL,
                COLUMN_INCOME_TOTAL,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_PORTFOLIO_PERCENT,
                COLUMN_CURRENT_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForStockPortfolio(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildStockPortfolioUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getStockPortfolioFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each stock owned, ITUB4, PETR4, etc
    public static final class StockData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_DATA).build();
        public static final Uri BULK_UPDATE_URI = URI.buildUpon().appendPath(BASE_BULK_UPDATE).build();

        public static final String TABLE_NAME = "stock_data";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY_TOTAL = "quantity_total";
        public static final String COLUMN_BUY_VALUE_TOTAL = "value_total";
        public static final String COLUMN_NET_INCOME = "income_total";
        public static final String COLUMN_INCOME_TAX = "income_tax";
        public static final String COLUMN_VARIATION = "variation";
        public static final String COLUMN_TOTAL_GAIN = "total_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_CURRENT_PERCENT = "current_percent";
        public static final String COLUMN_MEDIUM_PRICE = "medium_price";
        public static final String COLUMN_CURRENT_PRICE = "current_price";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] STOCK_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_NET_INCOME,
                COLUMN_INCOME_TAX,
                COLUMN_VARIATION,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_CURRENT_PERCENT,
                COLUMN_MEDIUM_PRICE,
                COLUMN_CURRENT_PRICE,
                COLUMN_CURRENT_TOTAL,
                COLUMN_STATUS,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForStockData(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getStockDataFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each stock owned, ITUB4, PETR4, etc
    public static final class SoldStockData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_SOLD_STOCK_DATA).build();

        public static final String TABLE_NAME = "sold_stock_data";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY_TOTAL = "quantity_total";
        public static final String COLUMN_BUY_VALUE_TOTAL = "value_total";
        public static final String COLUMN_SELL_GAIN = "sell_gain";
        public static final String COLUMN_SELL_MEDIUM_PRICE = "current_price";
        public static final String COLUMN_SELL_TOTAL = "current_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] SOLD_STOCK_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_SELL_GAIN,
                COLUMN_SELL_MEDIUM_PRICE,
                COLUMN_SELL_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForSoldStockData(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getSoldStockDataFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each stock buy and sell
    public static final class StockTransaction implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_TRANSACTION).build();

        public static final String TABLE_NAME = "stock_transaction";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "bought_price";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] STOCK_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_PRICE,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri buildTransactionUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static Uri makeUriForStockTransaction(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getStockTransactionFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of stock incomes
    public static final class StockIncome implements BaseColumns{
        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_INCOME).build();

        public static final String TABLE_NAME = "stock_incomes";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_TYPE = "income_type";
        public static final String COLUMN_PER_STOCK = "per_stock";
        public static final String COLUMN_EXDIVIDEND_TIMESTAMP = "timestamp";
        public static final String COLUMN_RECEIVE_TOTAL = "receive_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_RECEIVE_LIQUID = "receive_liquid";
        public static final String COLUMN_AFFECTED_QUANTITY = "affected_quantity";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] STOCK_INCOME_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_TYPE,
                COLUMN_PER_STOCK,
                COLUMN_EXDIVIDEND_TIMESTAMP,
                COLUMN_RECEIVE_TOTAL,
                COLUMN_TAX,
                COLUMN_RECEIVE_LIQUID,
                COLUMN_AFFECTED_QUANTITY,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForStockIncome(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getStockIncomeFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of whole fii portfolio
    public static final class FiiPortfolio implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_FII_PORTFOLIO).build();

        public static final String TABLE_NAME = "fii_portfolio";

        public static final String COLUMN_BUY_TOTAL = "value_total";
        public static final String COLUMN_SOLD_TOTAL = "sold_total";
        public static final String COLUMN_VARIATION_TOTAL = "variation_total";
        public static final String COLUMN_INCOME_TOTAL = "income_total";
        public static final String COLUMN_TOTAL_GAIN = "value_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_PORTFOLIO_PERCENT = "portfolio_percent";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] FII_PORTFOLIO_COLUMNS = {
                _ID,
                COLUMN_BUY_TOTAL,
                COLUMN_SOLD_TOTAL,
                COLUMN_VARIATION_TOTAL,
                COLUMN_INCOME_TOTAL,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_PORTFOLIO_PERCENT,
                COLUMN_CURRENT_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForFiiPortfolio(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildFiiPortfolioUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getFiiPortfolioFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each fii owned, KNRI11, BRCR11, etc
    public static final class FiiData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_FII_DATA).build();
        public static final Uri BULK_UPDATE_URI = URI.buildUpon().appendPath(BASE_BULK_UPDATE).build();

        public static final String TABLE_NAME = "fii_data";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY_TOTAL = "quantity_total";
        public static final String COLUMN_BUY_VALUE_TOTAL = "value_total";
        public static final String COLUMN_INCOME = "income_total";
        public static final String COLUMN_INCOME_TAX = "income_tax";
        public static final String COLUMN_VARIATION = "variation";
        public static final String COLUMN_TOTAL_GAIN = "total_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_CURRENT_PERCENT = "current_percent";
        public static final String COLUMN_MEDIUM_PRICE = "medium_price";
        public static final String COLUMN_CURRENT_PRICE = "current_price";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] FII_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_INCOME,
                COLUMN_INCOME_TAX,
                COLUMN_VARIATION,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_CURRENT_PERCENT,
                COLUMN_MEDIUM_PRICE,
                COLUMN_CURRENT_PRICE,
                COLUMN_CURRENT_TOTAL,
                COLUMN_STATUS,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForFiiData(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getFiiDataFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each stock owned, ITUB4, PETR4, etc
    public static final class SoldFiiData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_SOLD_FII_DATA).build();

        public static final String TABLE_NAME = "sold_fii_data";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY_TOTAL = "quantity_total";
        public static final String COLUMN_BUY_VALUE_TOTAL = "value_total";
        public static final String COLUMN_SELL_GAIN = "sell_gain";
        public static final String COLUMN_SELL_MEDIUM_PRICE = "current_price";
        public static final String COLUMN_SELL_TOTAL = "current_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] SOLD_FII_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_SELL_GAIN,
                COLUMN_SELL_MEDIUM_PRICE,
                COLUMN_SELL_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForSoldFiiData(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getSoldFiiDataFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each stock buy and sell
    public static final class FiiTransaction implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_FII_TRANSACTION).build();

        public static final String TABLE_NAME = "fii_transaction";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "bought_price";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] FII_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_PRICE,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri buildTransactionUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static Uri makeUriForFiiTransaction(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getFiiTransactionFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of stock incomes
    public static final class FiiIncome implements BaseColumns{
        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_FII_INCOME).build();

        public static final String TABLE_NAME = "fii_incomes";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_TYPE = "income_type";
        public static final String COLUMN_PER_FII = "per_fii";
        public static final String COLUMN_EXDIVIDEND_TIMESTAMP = "timestamp";
        public static final String COLUMN_RECEIVE_TOTAL = "receive_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_RECEIVE_LIQUID = "receive_liquid";
        public static final String COLUMN_AFFECTED_QUANTITY = "affected_quantity";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] FII_INCOME_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_TYPE,
                COLUMN_PER_FII,
                COLUMN_EXDIVIDEND_TIMESTAMP,
                COLUMN_RECEIVE_TOTAL,
                COLUMN_TAX,
                COLUMN_RECEIVE_LIQUID,
                COLUMN_AFFECTED_QUANTITY,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForFiiIncome(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getFiiIncomeFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of whole currency portfolio
    public static final class CurrencyPortfolio implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_CURRENCY_PORTFOLIO).build();

        public static final String TABLE_NAME = "currency_portfolio";

        public static final String COLUMN_BUY_TOTAL = "value_total";
        public static final String COLUMN_SOLD_TOTAL = "sold_total";
        public static final String COLUMN_VARIATION_TOTAL = "variation_total";
        public static final String COLUMN_TOTAL_GAIN = "value_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_PORTFOLIO_PERCENT = "portfolio_percent";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] CURRENCY_PORTFOLIO_COLUMNS = {
                _ID,
                COLUMN_BUY_TOTAL,
                COLUMN_SOLD_TOTAL,
                COLUMN_VARIATION_TOTAL,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_PORTFOLIO_PERCENT,
                COLUMN_CURRENT_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForCurrencyPortfolio(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildCurrencyPortfolioUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getCurrencyPortfolioFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each currency owned, DOLAR, EURO, etc
    public static final class CurrencyData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_CURRENCY_DATA).build();
        public static final Uri BULK_UPDATE_URI = URI.buildUpon().appendPath(BASE_BULK_UPDATE).build();

        public static final String TABLE_NAME = "currency_data";

        // Symbol will be currency name, Dolar, Euro, Bitcoin.
        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY_TOTAL = "quantity_total";
        public static final String COLUMN_BUY_VALUE_TOTAL = "value_total";
        public static final String COLUMN_VARIATION = "variation";
        public static final String COLUMN_TOTAL_GAIN = "total_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_CURRENT_PERCENT = "current_percent";
        public static final String COLUMN_MEDIUM_PRICE = "medium_price";
        public static final String COLUMN_CURRENT_PRICE = "current_price";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] CURRENCY_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_VARIATION,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_CURRENT_PERCENT,
                COLUMN_MEDIUM_PRICE,
                COLUMN_CURRENT_PRICE,
                COLUMN_CURRENT_TOTAL,
                COLUMN_STATUS,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForCurrencyData(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getCurrencyDataFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each currency sold, DOLAR, EURO, etc
    public static final class SoldCurrencyData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_SOLD_CURRENCY_DATA).build();

        public static final String TABLE_NAME = "sold_currency_data";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY_TOTAL = "quantity_total";
        public static final String COLUMN_BUY_VALUE_TOTAL = "value_total";
        public static final String COLUMN_SELL_GAIN = "sell_gain";
        public static final String COLUMN_SELL_MEDIUM_PRICE = "current_price";
        public static final String COLUMN_SELL_TOTAL = "current_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] SOLD_CURRENCY_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_SELL_GAIN,
                COLUMN_SELL_MEDIUM_PRICE,
                COLUMN_SELL_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForSoldCurrencyData(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getSoldCurrencyDataFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each currency buy and sell
    public static final class CurrencyTransaction implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_CURRENCY_TRANSACTION).build();

        public static final String TABLE_NAME = "currency_transaction";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "bought_price";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] CURRENCY_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_PRICE,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri buildTransactionUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static Uri makeUriForCurrencyTransaction(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getCurrencyTransactionFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of whole fixed income portfolio
    public static final class FixedPortfolio implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_FIXED_PORTFOLIO).build();

        public static final String TABLE_NAME = "fixed_portfolio";

        public static final String COLUMN_BUY_TOTAL = "value_total";
        public static final String COLUMN_SOLD_TOTAL = "sold_total";
        public static final String COLUMN_VARIATION_TOTAL = "variation_total";
        public static final String COLUMN_INCOME_TOTAL = "income_total";
        public static final String COLUMN_TOTAL_GAIN = "value_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_PORTFOLIO_PERCENT = "portfolio_percent";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] FIXED_PORTFOLIO_COLUMNS = {
                _ID,
                COLUMN_BUY_TOTAL,
                COLUMN_SOLD_TOTAL,
                COLUMN_VARIATION_TOTAL,
                COLUMN_INCOME_TOTAL,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_PORTFOLIO_PERCENT,
                COLUMN_CURRENT_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForFixedPortfolio(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildFixedPortfolioUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getFixedPortfolioFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each fixed income owned.
    public static final class FixedData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_FIXED_DATA).build();
        public static final Uri BULK_UPDATE_URI = URI.buildUpon().appendPath(BASE_BULK_UPDATE).build();

        public static final String TABLE_NAME = "fixed_data";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_BUY_VALUE_TOTAL = "buy_value_total";
        public static final String COLUMN_SELL_VALUE_TOTAL = "sell_value_total";
        public static final String COLUMN_NET_GAIN = "net_gain";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_TOTAL_GAIN = "total_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_CURRENT_PERCENT = "current_percent";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] FIXED_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_SELL_VALUE_TOTAL,
                COLUMN_NET_GAIN,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_CURRENT_PERCENT,
                COLUMN_CURRENT_TOTAL,
                COLUMN_STATUS,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForFixedData(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getFixedDataFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each fixed income buy and sell
    public static final class FixedTransaction implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_FIXED_TRANSACTION).build();

        public static final String TABLE_NAME = "fixed_transaction";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_TOTAL = "bought_total";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] FIXED_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_TOTAL,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri buildTransactionUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static Uri makeUriForFixedTransaction(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getFixedTransactionFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of whole treasury portfolio
    public static final class TreasuryPortfolio implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_TREASURY_PORTFOLIO).build();

        public static final String TABLE_NAME = "treasury_portfolio";

        public static final String COLUMN_BUY_TOTAL = "value_total";
        public static final String COLUMN_SOLD_TOTAL = "sold_total";
        public static final String COLUMN_VARIATION_TOTAL = "variation_total";
        public static final String COLUMN_INCOME_TOTAL = "income_total";
        public static final String COLUMN_TOTAL_GAIN = "value_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_PORTFOLIO_PERCENT = "portfolio_percent";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] TREASURY_PORTFOLIO_COLUMNS = {
                _ID,
                COLUMN_BUY_TOTAL,
                COLUMN_SOLD_TOTAL,
                COLUMN_VARIATION_TOTAL,
                COLUMN_INCOME_TOTAL,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_PORTFOLIO_PERCENT,
                COLUMN_CURRENT_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForTreasuryPortfolio(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildTreasuryPortfolioUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getTreasuryPortfolioFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each tresury owned, LFT, NTNB
    public static final class TreasuryData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_TREASURY_DATA).build();
        public static final Uri BULK_UPDATE_URI = URI.buildUpon().appendPath(BASE_BULK_UPDATE).build();

        public static final String TABLE_NAME = "treasury_data";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY_TOTAL = "quantity_total";
        public static final String COLUMN_BUY_VALUE_TOTAL = "value_total";
        public static final String COLUMN_INCOME = "income_total";
        public static final String COLUMN_INCOME_TAX = "income_tax";
        public static final String COLUMN_VARIATION = "variation";
        public static final String COLUMN_TOTAL_GAIN = "total_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_CURRENT_PERCENT = "current_percent";
        public static final String COLUMN_MEDIUM_PRICE = "medium_price";
        public static final String COLUMN_CURRENT_PRICE = "current_price";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] TREASURY_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_INCOME,
                COLUMN_INCOME_TAX,
                COLUMN_VARIATION,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_CURRENT_PERCENT,
                COLUMN_MEDIUM_PRICE,
                COLUMN_CURRENT_PRICE,
                COLUMN_CURRENT_TOTAL,
                COLUMN_STATUS,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForTreasuryData(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getTreasuryDataFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each treasury sold, LFT. NTNB, etc
    public static final class SoldTreasuryData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_SOLD_TREASURY_DATA).build();

        public static final String TABLE_NAME = "sold_treasury_data";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY_TOTAL = "quantity_total";
        public static final String COLUMN_BUY_VALUE_TOTAL = "value_total";
        public static final String COLUMN_SELL_GAIN = "sell_gain";
        public static final String COLUMN_SELL_MEDIUM_PRICE = "current_price";
        public static final String COLUMN_SELL_TOTAL = "current_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] SOLD_TREASURY_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_SELL_GAIN,
                COLUMN_SELL_MEDIUM_PRICE,
                COLUMN_SELL_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForSoldTreasuryData(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getSoldTreasuryDataFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each stock buy and sell
    public static final class TreasuryTransaction implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_TREASURY_TRANSACTION).build();

        public static final String TABLE_NAME = "treasury_transaction";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "bought_price";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] TREASURY_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_PRICE,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE
        };

        public static Uri buildTransactionUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static Uri makeUriForTreasuryTransaction(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getTreasuryTransactionFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of treasury incomes
    public static final class TreasuryIncome implements BaseColumns{
        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_TREASURY_INCOME).build();

        public static final String TABLE_NAME = "treasury_incomes";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_TYPE = "income_type";
        public static final String COLUMN_EXDIVIDEND_TIMESTAMP = "timestamp";
        public static final String COLUMN_RECEIVE_TOTAL = "receive_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_RECEIVE_LIQUID = "receive_liquid";
        public static final String COLUMN_BROKERAGE = "brokerage";

        public static final String[] TREASURY_INCOME_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_TYPE,
                COLUMN_EXDIVIDEND_TIMESTAMP,
                COLUMN_RECEIVE_TOTAL,
                COLUMN_TAX,
                COLUMN_RECEIVE_LIQUID,
                COLUMN_BROKERAGE
        };

        public static Uri makeUriForTreasuryIncome(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getTreasuryIncomeFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
}
