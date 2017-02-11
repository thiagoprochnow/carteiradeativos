package br.com.carteira.data;

import android.net.Uri;
import android.provider.BaseColumns;

/* Contract class with helper methods and constants for the provier */
public class PortfolioContract {

    // TODO: We'll need to update this AUTHOROT with the final package name of the app
    public static final String AUTHORITY = "br.com.carteira";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_STOCK_QUOTE = "quote";
    public static final String PATH_STOCK_QUOTE_WITH_SYMBOL = "quote/*";

    public static final String PATH_STOCK_INCOME = "income";
    public static final String PATH_STOCK_INCOME_WITH_SYMBOL = "income/*";

    /* This is the methods and constants used for Stocks table */
    public static final class StockQuote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_QUOTE).build();

        public static final String TABLE_NAME = "stock_quotes";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_BOUGHT_TOTAL = "bought_total";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_APPRECIATION = "appreciation";
        public static final String COLUMN_CURRENT_PERCENT = "current_percent";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_TOTAL_INCOME = "total_income";
        public static final String COLUMN_TOTAL_GAIN = "total_gain";

        public static final String[] STOCK_QUOTE_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_BOUGHT_TOTAL,
                COLUMN_CURRENT_TOTAL,
                COLUMN_APPRECIATION,
                COLUMN_CURRENT_PERCENT,
                COLUMN_OBJECTIVE_PERCENT,
                COLUMN_TOTAL_INCOME,
                COLUMN_TOTAL_GAIN
        };


        public static Uri makeUriForStockQuote(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getStockQuoteFromUri(Uri uri) {
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

        public static final String[] STOCK_INCOME_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_TYPE,
                COLUMN_PER_STOCK,
                COLUMN_PERCENT,
        };

        public static Uri makeUriForStockIncome(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getStockIncomeFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
}
