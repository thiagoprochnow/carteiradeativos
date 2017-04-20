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
    }

    // This should contains all product types in the portfolio
    public static class ProductType {
        public static final int INVALID = -1;
        public static final int STOCK = 0;
        public static final int FII = 1;
    }

    // This should contains all incomes types in the portfolio
    public static class IncomeType {
        public static final int INVALID = -1;
        public static final int DIVIDEND = 0;
        public static final int JCP = 1;
        public static final int BONIFICATION = 2;
        public static final int GROUPING = 3;
        public static final int SPLIT = 4;
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
    }

    // Constants for Loaders IDs
    public static class Loaders{
        public static final int INVALID = -1;
        public static final int STOCK_DATA = 0;
        public static final int SOLD_STOCK_DATA = 1;
        public static final int STOCK_INCOME = 2;
        public static final int STOCK_OVERVIEW = 3;
        public static final int STOCK_DETAILS = 4;
    }
}
