package br.com.guiainvestimento.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/* Contract class with helper methods and constants for the provier */
public class PortfolioContract {

    // TODO: We'll need to update this AUTHOROT with the final package name of the app
    public static final String AUTHORITY = "br.com.guiainvestimento";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String BASE_BULK_UPDATE = "update";

    public static final String PATH_PORTFOLIO = "portfolio";

    public static final String PATH_PORTFOLIO_GROWTH = "portfolio_growth";

    public static final String PATH_BUY_GROWTH = "buy_growth";

    public static final String PATH_INCOME_GROWTH = "income_growth";

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

    public static final String PATH_OTHERS_PORTFOLIO = "others_portfolio";

    public static final String PATH_OTHERS_DATA = "others_data";
    public static final String PATH_OTHERS_DATA_BULK_UPDATE = "others_data/update";
    public static final String PATH_OTHERS_DATA_BULK_UPDATE_WITH_CURRENT = "others_data/update/*";
    public static final String PATH_OTHERS_DATA_WITH_SYMBOL = "others_data/*";

    public static final String PATH_OTHERS_TRANSACTION = "others_transaction";
    public static final String PATH_OTHERS_TRANSACTION_WITH_SYMBOL = "others_transaction/*";

    public static final String PATH_OTHERS_INCOME = "others_income";
    public static final String PATH_OTHERS_INCOME_WITH_SYMBOL = "others_income/*";

    public static final String PATH_CDI = "cdi";
    public static final String PATH_CDI_WITH_DATE = "cdi/*";

    public static final String PATH_IPCA = "ipca";

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
        public static final String COLUMN_OTHERS_PERCENT = "others_percent";
        public static final String COLUMN_STOCK_PERCENT = "stock_percent";
        public static final String COLUMN_FII_PERCENT = "fii_percent";
        public static final String COLUMN_CURRENCY_PERCENT = "currency_percent";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_OTHERS_PERCENT,
                COLUMN_STOCK_PERCENT,
                COLUMN_FII_PERCENT,
                COLUMN_CURRENCY_PERCENT,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
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

    // Table with information of growth on portfolio
    public static final class PortfolioGrowth implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_PORTFOLIO_GROWTH).build();

        public static final String TABLE_NAME = "portfolio_growth";

        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String MONTH = "month";
        public static final String YEAR = "year";
        public static final String COLUMN_TYPE = "type";

        public static final String[] PORTFOLIO_GROWTH_COLUMNS = {
                _ID,
                COLUMN_TOTAL,
                COLUMN_TIMESTAMP,
                MONTH,
                YEAR,
                COLUMN_TYPE
        };

        public static Uri makeUriForPortfolioGrowth(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildPortfolioGrowthUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getPortfolioGrowthFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of growth on byu values
    public static final class BuyGrowth implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_BUY_GROWTH).build();

        public static final String TABLE_NAME = "buy_growth";

        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String MONTH = "month";
        public static final String YEAR = "year";
        public static final String COLUMN_TYPE = "type";

        public static final String[] BUY_GROWTH_COLUMNS = {
                _ID,
                COLUMN_TOTAL,
                COLUMN_TIMESTAMP,
                MONTH,
                YEAR,
                COLUMN_TYPE
        };

        public static Uri makeUriForBuyGrowth(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildBuyGrowthUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getBuyGrowthFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of growth on income
    public static final class IncomeGrowth implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_INCOME_GROWTH).build();

        public static final String TABLE_NAME = "income_growth";

        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String MONTH = "month";
        public static final String YEAR = "year";
        public static final String COLUMN_TYPE = "type";

        public static final String[] INCOME_GROWTH_COLUMNS = {
                _ID,
                COLUMN_TOTAL,
                COLUMN_TIMESTAMP,
                MONTH,
                YEAR,
                COLUMN_TYPE
        };

        public static Uri makeUriForIncomeGrowth(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildIncomeGrowthUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getIncomeGrowthFromUri(Uri uri) {
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
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";
        public static final String COLUMN_UPDATE_STATUS = "update_status";
        public static final String COLUMN_CLOSING_PRICE = "closing_price";

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
                COLUMN_BROKERAGE,
                LAST_UPDATE,
                COLUMN_UPDATE_STATUS,
                COLUMN_CLOSING_PRICE
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
        public static final String LAST_UPDATE = "last_update";

        public static final String[] SOLD_STOCK_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_SELL_GAIN,
                COLUMN_SELL_MEDIUM_PRICE,
                COLUMN_SELL_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

        public static final String[] STOCK_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_PRICE,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";
        public static final String COLUMN_UPDATE_STATUS = "update_status";
        public static final String COLUMN_CLOSING_PRICE = "closing_price";

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
                COLUMN_BROKERAGE,
                LAST_UPDATE,
                COLUMN_UPDATE_STATUS,
                COLUMN_CLOSING_PRICE
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
        public static final String LAST_UPDATE = "last_update";

        public static final String[] SOLD_FII_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_SELL_GAIN,
                COLUMN_SELL_MEDIUM_PRICE,
                COLUMN_SELL_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

        public static final String[] FII_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_PRICE,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

        public static final String[] SOLD_CURRENCY_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_SELL_GAIN,
                COLUMN_SELL_MEDIUM_PRICE,
                COLUMN_SELL_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

        public static final String[] CURRENCY_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_PRICE,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String COLUMN_UPDATE_STATUS = "update_status";
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_BROKERAGE,
                COLUMN_UPDATE_STATUS,
                LAST_UPDATE
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
        public static final String COLUMN_GAIN_RATE = "gain_rate";
        public static final String COLUMN_GAIN_TYPE = "gain_type";
        public static final String LAST_UPDATE = "last_update";

        public static final String[] FIXED_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_TOTAL,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                COLUMN_GAIN_RATE,
                COLUMN_GAIN_TYPE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String COLUMN_UPDATE_STATUS = "update_status";
        public static final String LAST_UPDATE = "last_update";

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
                COLUMN_BROKERAGE,
                COLUMN_UPDATE_STATUS,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

        public static final String[] SOLD_TREASURY_DATA_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY_TOTAL,
                COLUMN_BUY_VALUE_TOTAL,
                COLUMN_SELL_GAIN,
                COLUMN_SELL_MEDIUM_PRICE,
                COLUMN_SELL_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

        public static final String[] TREASURY_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_QUANTITY,
                COLUMN_PRICE,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
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
        public static final String LAST_UPDATE = "last_update";

        public static final String[] TREASURY_INCOME_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_TYPE,
                COLUMN_EXDIVIDEND_TIMESTAMP,
                COLUMN_RECEIVE_TOTAL,
                COLUMN_TAX,
                COLUMN_RECEIVE_LIQUID,
                COLUMN_BROKERAGE,
                LAST_UPDATE
        };

        public static Uri makeUriForTreasuryIncome(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getTreasuryIncomeFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of whole others income portfolio
    public static final class OthersPortfolio implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_OTHERS_PORTFOLIO).build();

        public static final String TABLE_NAME = "others_portfolio";

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
        public static final String LAST_UPDATE = "last_update";

        public static final String[] OTHERS_PORTFOLIO_COLUMNS = {
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
                COLUMN_BROKERAGE,
                LAST_UPDATE
        };

        public static Uri makeUriForOthersPortfolio(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildOthersPortfolioUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getOthersPortfolioFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each others income owned.
    public static final class OthersData implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_OTHERS_DATA).build();
        public static final Uri BULK_UPDATE_URI = URI.buildUpon().appendPath(BASE_BULK_UPDATE).build();

        public static final String TABLE_NAME = "others_data";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_BUY_VALUE_TOTAL = "buy_value_total";
        public static final String COLUMN_SELL_VALUE_TOTAL = "sell_value_total";
        public static final String COLUMN_NET_GAIN = "net_gain";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_VARIATION = "variation";
        public static final String COLUMN_TOTAL_GAIN = "total_gain";
        public static final String COLUMN_OBJECTIVE_PERCENT = "objective_percent";
        public static final String COLUMN_CURRENT_PERCENT = "current_percent";
        public static final String COLUMN_CURRENT_TOTAL = "current_total";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_BROKERAGE = "brokerage";
        public static final String COLUMN_INCOME = "income_total";
        public static final String COLUMN_INCOME_TAX = "income_tax";
        public static final String LAST_UPDATE = "last_update";

        public static final String[] OTHERS_DATA_COLUMNS = {
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
                COLUMN_VARIATION,
                COLUMN_BROKERAGE,
                COLUMN_INCOME,
                COLUMN_INCOME_TAX,
                LAST_UPDATE
        };

        public static Uri makeUriForOthersData(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getOthersDataFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of each others income buy and sell
    public static final class OthersTransaction implements BaseColumns {

        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_OTHERS_TRANSACTION).build();

        public static final String TABLE_NAME = "others_transaction";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_TOTAL = "bought_total";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";
        public static final String LAST_UPDATE = "last_update";

        public static final String[] OTHERS_TRANSACTION_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_TOTAL,
                COLUMN_TIMESTAMP,
                COLUMN_TYPE,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
        };

        public static Uri buildTransactionUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static Uri makeUriForOthersTransaction(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getOthersTransactionFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of others incomes
    public static final class OthersIncome implements BaseColumns{
        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_OTHERS_INCOME).build();

        public static final String TABLE_NAME = "others_incomes";

        public static final String COLUMN_SYMBOL = "symbol";
        public static final String COLUMN_TYPE = "income_type";
        public static final String COLUMN_EXDIVIDEND_TIMESTAMP = "timestamp";
        public static final String COLUMN_RECEIVE_TOTAL = "receive_total";
        public static final String COLUMN_TAX = "tax";
        public static final String COLUMN_BROKERAGE = "brokerage";
        public static final String LAST_UPDATE = "last_update";

        public static final String[] OTHERS_INCOME_COLUMNS = {
                _ID,
                COLUMN_SYMBOL,
                COLUMN_TYPE,
                COLUMN_EXDIVIDEND_TIMESTAMP,
                COLUMN_RECEIVE_TOTAL,
                COLUMN_TAX,
                COLUMN_BROKERAGE,
                LAST_UPDATE
        };

        public static Uri makeUriForOthersIncome(String symbol) {
            return URI.buildUpon().appendPath(symbol).build();
        }

        public static String getOthersIncomeFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of cdi
    public static final class Cdi implements BaseColumns{
        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_CDI).build();

        public static final String TABLE_NAME = "cdi";

        public static final String COLUMN_VALUE = "value";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_DATA = "data";
        public static final String LAST_UPDATE = "last_update";

        public static final String[] CDI_COLUMNS = {
                _ID,
                COLUMN_VALUE,
                COLUMN_TIMESTAMP,
                COLUMN_DATA,
                LAST_UPDATE
        };

        public static Uri makeUriForCdi(String timestamp) {
            return URI.buildUpon().appendPath(timestamp).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getCdiFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    // Table with information of ipca
    public static final class Ipca implements BaseColumns{
        public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_IPCA).build();

        public static final String TABLE_NAME = "ipca";

        public static final String COLUMN_ANO = "ano";
        public static final String COLUMN_MES = "mes";
        public static final String COLUMN_VALUE = "valor";
        public static final String LAST_UPDATE = "last_update";

        public static final String[] IPCA_COLUMNS = {
                _ID,
                COLUMN_VALUE,
                COLUMN_ANO,
                COLUMN_MES,
                LAST_UPDATE
        };

        public static Uri makeUriForIpca(String timestamp) {
            return URI.buildUpon().appendPath(timestamp).build();
        }

        public static Uri buildDataUri(long id) {
            return ContentUris.withAppendedId(URI, id);
        }

        public static String getIpcaFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }
}
