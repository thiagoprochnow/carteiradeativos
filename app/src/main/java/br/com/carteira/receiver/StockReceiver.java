package br.com.carteira.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import br.com.carteira.data.PortfolioContract;

public class StockReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = StockReceiver.class.getSimpleName();

    private Context mContext;

    @Override
    public void onReceive(Context c, Intent intent){
        mContext = c;
        updateStockPortfolio();
    }

    // Reads all of Stock Data value and sets the calculation on StockPortfolio table
    // Dosent need any data because it will not query for a specific stock, but for all of them.
    public boolean updateStockPortfolio(){
        // Return column should be the sum of value total, income total, value gain
        String[] affectedColumn = {"sum("+ PortfolioContract.StockData.COLUMN_VARIATION +"), " +
                "sum("+ PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.StockData.COLUMN_INCOME_TOTAL +"), " +
                "sum("+PortfolioContract.StockData.COLUMN_TOTAL_GAIN +")"};

        // Check if the symbol exists in the db
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                affectedColumn, null, null, null);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            double variationTotal = queryCursor.getDouble(0);
            double buyTotal = queryCursor.getDouble(1);
            double incomeTotal = queryCursor.getDouble(2);
            double valueGain = queryCursor.getDouble(3);

            // Values to be inserted or updated on StockPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_VARIATION_TOTAL, variationTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_BUY_TOTAL, buyTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL, incomeTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_TOTAL_GAIN, valueGain);

            // Query for the only stock portfolio, if dosent exist, creates one
            Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockPortfolio.URI,
                    null, null, null, null);
            // If exists, updates value, else create a new field and add values
            if(portfolioQueryCursor.getCount() > 0){
                portfolioQueryCursor.moveToFirst();
                String _id = String.valueOf(portfolioQueryCursor.getInt(portfolioQueryCursor.getColumnIndex(PortfolioContract.StockPortfolio._ID)));
                // Prepare query to update stock data
                String updateSelection = PortfolioContract.StockData._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                // Update value on stock data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.StockPortfolio.URI,
                        portfolioCV, updateSelection, updatedSelectionArguments);

                if (updatedRows > 0 ){
                    return true;
                }
            } else {
                // Creates table and add values
                Uri insertedStockDataUri = mContext.getContentResolver().insert(PortfolioContract.StockPortfolio.URI,
                        portfolioCV);
                if(insertedStockDataUri != null){
                    return true;
                }
            }
            return false;
        } else{
            return false;
        }
    }

    // Reads all of Investment Portfolios value and sets the calculation on Portfolio table
    // Dosent need any data because it will not query for a specific investment, but for all of them.
    public boolean updatePortfolio(){
        // TODO: Develop function to read all Stock, FII, Fixed Income, etc table to get total value of portfolio
        return true;
    }
}
