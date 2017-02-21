package br.com.carteira.data;

import android.net.Uri;
import android.provider.BaseColumns;

/* Contract class with helper methods and constants for the provier */
public class PortfolioContract {

    // TODO: We'll need to update this AUTHOROT with the final package name of the app
    public static final String AUTHORITY = "br.com.carteira";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_STOCK_SYMBOLS = "symbols";

    public static final String PATH_STOCK_QUOTE = "quote";
    public static final String PATH_STOCK_QUOTE_WITH_SYMBOL = "quote/*";

    public static final String PATH_STOCK_INCOME = "income";
    public static final String PATH_STOCK_INCOME_WITH_SYMBOL = "income/*";

    public static final class StockSymbol implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_SYMBOLS).build();

        public static final String TABLE_NAME = "stock_symbols";

        public static final String COLUMN_SYMBOL = "symbol";

        public static final String[] STOCK_QUOTE_COLUMNS = {
                _ID,
                COLUMN_SYMBOL
        };

        public static Uri makeUriForStockSymbol(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getStockSymbolFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    /* This is the methods and constants used for Stocks table */
    public static final class StockQuote implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_STOCK_QUOTE).build();

        public static final String TABLE_NAME = "stock_quotes";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_BOUGHT_PRICE = "bought_price";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";

        public static final String[] STOCK_QUOTE_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_BOUGHT_PRICE,
                COLUMN_OBJECTIVE_PERCENT
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
