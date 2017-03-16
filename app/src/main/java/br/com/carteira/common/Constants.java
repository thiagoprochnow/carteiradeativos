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
    }

    // Value of stock/fii status, buy or sell
    public static class Status {
        public static final int INVALID = -1;
        public static final int BUY = 0;
        public static final int SELL = 1;
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
    }
}
