package br.com.carteira.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/* Contract class with helper methods and constants for the provier */
public class PortfolioContract {

    // TODO: We'll need to update this AUTHOROT with the final package name of the app
    public static final String AUTHORITY = "br.com.carteira";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_STOCK_DATA = "stock_data";
    public static final String PATH_STOCK_DATA_WITH_SYMBOL = "stock_data/*";

    public static final String PATH_STOCK_TRANSACTION = "stock_transaction";
    public static final String PATH_STOCK_TRANSACTION_WITH_SYMBOL = "stock_transaction/*";

    public static final String PATH_STOCK_INCOME = "stock_income";
    public static final String PATH_STOCK_INCOME_WITH_SYMBOL = "stock_income/*";

    public static final class StockData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_DATA).build();

        public static final String TABLE_NAME = "stock_data";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY_TOTAL = "quantity_total";
        public static final String COLUMN_VALUE_TOTAL = "value_total";
        public static final String COLUMN_INCOME_TOTAL = "income_total";
        public static final String COLUMN_VALUE_GAIN = "value_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_CURRENT_PERCENT = "current_percent";
        public static final String COLUMN_MEDIUM_PRICE = "medium_price";

        public static final String[] STOCK_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_VALUE_TOTAL,
                COLUMN_INCOME_TOTAL,
                COLUMN_VALUE_GAIN,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_CURRENT_PERCENT,
                COLUMN_MEDIUM_PRICE
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

    /* This is the methods and constants used for Stocks table */
    public static final class StockTransaction implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_TRANSACTION).build();

        public static final String TABLE_NAME = "stock_transaction";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "bought_price";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_STATUS = "status";

        public static final String[] STOCK_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_PRICE,
                COLUMN_TIMESTAMP,
                COLUMN_STATUS
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

    public static final class StockIncome implements BaseColumns{
        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_INCOME).build();

        public static final String TABLE_NAME = "stock_incomes";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_TYPE = "income_type";
        public static final String COLUMN_PER_STOCK = "per_stock";
        public static final String COLUMN_PERCENT = "current_percent";
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
                COLUMN_PERCENT,
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
