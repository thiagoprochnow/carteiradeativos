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

    /* STOCK TABLES */

    // Table with information of whole user portfolio
    public static final class Portfolio implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_PORTFOLIO).build();

        public static final String TABLE_NAME = "portfolio";

        public static final String COLUMN_VALUE_TOTAL = "value_total";
        public static final String COLUMN_VALUE_GAIN = "value_gain";
        public static final String COLUMN_INCOME_TOTAL = "income_total";
        public static final String COLUMN_TOTAL_GAIN = "total_gain";

        public static final String[] PORTFOLIO_COLUMNS = {
                _ID,
                COLUMN_VALUE_TOTAL,
                COLUMN_VALUE_GAIN,
                COLUMN_INCOME_TOTAL,
                COLUMN_TOTAL_GAIN,
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

        public static final String[] STOCK_PORTFOLIO_COLUMNS = {
                _ID,
                COLUMN_BUY_TOTAL,
                COLUMN_SOLD_TOTAL,
                COLUMN_VARIATION_TOTAL,
                COLUMN_INCOME_TOTAL,
                COLUMN_TOTAL_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_PORTFOLIO_PERCENT,
                COLUMN_CURRENT_TOTAL
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
                COLUMN_STATUS
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

        public static final String[] SOLD_STOCK_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_SELL_GAIN,
                COLUMN_SELL_MEDIUM_PRICE,
                COLUMN_SELL_TOTAL
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

        public static final String[] STOCK_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_PRICE,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE
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

        public static final String[] STOCK_INCOME_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_TYPE,
                COLUMN_PER_STOCK,
                COLUMN_EXDIVIDEND_TIMESTAMP,
                COLUMN_RECEIVE_TOTAL,
                COLUMN_TAX,
                COLUMN_RECEIVE_LIQUID,
                COLUMN_AFFECTED_QUANTITY
        };

        public static Uri makeUriForStockIncome(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getStockIncomeFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
}
