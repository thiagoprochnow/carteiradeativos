package br.com.carteira.common;

/* Class to br used as a constants repository.
Here, we'll create static public sub-classes to properly separate
each groups of similar/related constants.
 */

public final class Constants {

    // Constants used to pass extras to Intents
    public static class Extra {
        public static final String EXTRA_PRODUCT_TYPE = "extra_product_type";
        public static final String EXTRA_PRODUCT_STATUS = "extra_product_status";
        public static final String EXTRA_PRODUCT_SYMBOL = "extra_product_symbol";
        public static final String EXTRA_INCOME_TYPE = "extra_income_type";
        public static final String EXTRA_INCOME_ID = "extra_income_id";
    }

    // Value of stock/fii type, buy, sell, bonification, grouping, split
    public static class Type {
        public static final int INVALID = -1;
        public static final int BUY = 0;
        public static final int SELL = 1;
        public static final int BONIFICATION = 2;
        public static final int GROUPING = 3;
        public static final int SPLIT = 4;
        public static final int EDIT = 5;
        public static final int DELETE_TRANSACION = 6;
    }

    // This should contains all product types in the portfolio
    public static class ProductType {
        public static final int INVALID = -1;
        public static final int STOCK = 0;
        public static final int FII = 1;
        public static final int CURRENCY = 2;
    }

    // This should contains all incomes types in the portfolio
    public static class IncomeType {
        public static final int INVALID = -1;
        public static final int DIVIDEND = 0;
        public static final int JCP = 1;
        public static final int BONIFICATION = 2;
        public static final int GROUPING = 3;
        public static final int SPLIT = 4;
        public static final int FII_INCOME = 5;
    }

    // Status of the a specific investment
    // If Active, means user still is in that investment
    // If Sold, means user already sold or expired that investment
    // For Stocks and FIIs will be used to know if user still has that stock in his portfolio
    // For other the same, will be used to know if it is a past investment or current.
    public static class Status{
        public static final int INVALID = -1;
        public static final int ACTIVE = 0;
        public static final int SOLD = 1;
    }

    // Constant for calling BroadcastReceivers
    public static class Receiver{
        public static final String STOCK = "UPDATE_STOCK_PORTFOLIO";
        public static final String FII = "UPDATE_FII_PORTFOLIO";
        public static final String CURRENCY = "UPDATE_CURRENCY_PORTFOLIO";
    }

    // Constants for Loaders IDs
    public static class Loaders{
        public static final int INVALID = -1;
        public static final int STOCK_DATA = 0;
        public static final int SOLD_STOCK_DATA = 1;
        public static final int STOCK_INCOME = 2;
        public static final int STOCK_OVERVIEW = 3;
        public static final int STOCK_DETAILS = 4;

        public static final int FII_DATA = 5;
        public static final int SOLD_FII_DATA = 6;
        public static final int FII_INCOME = 7;
        public static final int FII_OVERVIEW = 8;
        public static final int FII_DETAILS = 9;

        public static final int CURRENCY_DATA = 10;
        public static final int SOLD_CURRENCY_DATA = 11;
        public static final int CURRENCY_OVERVIEW = 12;
        public static final int CURRENCY_DETAILS = 13;
    }

    // Constants for Providers IDs
    public static class Provider{
        public static final int PORTFOLIO = 100;

        public static final int STOCK_PORTFOLIO = 1100;

        public static final int STOCK_DATA = 1200;
        public static final int STOCK_DATA_WITH_SYMBOL = 1201;
        public static final int STOCK_DATA_BULK_UPDATE = 1202;
        public static final int STOCK_DATA_BULK_UPDATE_FOR_CURRENT = 1203;

        public static final int SOLD_STOCK_DATA = 1300;
        public static final int SOLD_STOCK_DATA_WITH_SYMBOL = 1301;

        public static final int STOCK_TRANSACTION = 1400;
        public static final int STOCK_TRANSACTION_FOR_SYMBOL = 1401;

        public static final int STOCK_INCOME = 1500;
        public static final int STOCK_INCOME_FOR_SYMBOL = 1501;

        public static final int FII_PORTFOLIO = 2100;

        public static final int FII_DATA = 2200;
        public static final int FII_DATA_WITH_SYMBOL = 2201;
        public static final int FII_DATA_BULK_UPDATE = 2202;
        public static final int FII_DATA_BULK_UPDATE_FOR_CURRENT = 2203;

        public static final int SOLD_FII_DATA = 2300;
        public static final int SOLD_FII_DATA_WITH_SYMBOL = 2301;

        public static final int FII_TRANSACTION = 2400;
        public static final int FII_TRANSACTION_FOR_SYMBOL = 2401;

        public static final int FII_INCOME = 2500;
        public static final int FII_INCOME_FOR_SYMBOL = 2501;

        public static final int CURRENCY_PORTFOLIO = 3100;

        public static final int CURRENCY_DATA = 3200;
        public static final int CURRENCY_DATA_WITH_SYMBOL = 3201;
        public static final int CURRENCY_DATA_BULK_UPDATE = 3202;
        public static final int CURRENCY_DATA_BULK_UPDATE_FOR_CURRENT = 3203;

        public static final int SOLD_CURRENCY_DATA = 3300;
        public static final int SOLD_CURRENCY_DATA_WITH_SYMBOL = 3301;

        public static final int CURRENCY_TRANSACTION = 3400;
        public static final int CURRENCY_TRANSACTION_FOR_SYMBOL = 3401;

        public static final int CURRENCY_INCOME = 3500;
        public static final int CURRENCY_INCOME_FOR_SYMBOL = 3501;
    }
}
