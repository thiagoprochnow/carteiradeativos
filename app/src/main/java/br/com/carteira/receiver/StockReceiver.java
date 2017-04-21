package br.com.carteira.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.Locale;

import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;

public class StockReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = StockReceiver.class.getSimpleName();

    private Context mContext;

    private double mCurrentTotal = 0;

    @Override
    public void onReceive(Context c, Intent intent){
        mContext = c;
        updateStockPortfolio();
    }

    // Reads all of Stock Data value and sets the calculation on StockPortfolio table
    // Dosent need any data because it will not query for a specific stock, but for all of them.
    public void updateStockPortfolio(){

        double buyTotal = 0;
        double totalGain = 0;
        double variationTotal = 0;
        double sellTotal = 0;
        // Return column should be the sum of buy total, sell total, sell gain
        String[] soldAffectedColumn = {"sum("+ PortfolioContract.SoldStockData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.SoldStockData.COLUMN_SELL_TOTAL +"), " +
                "sum("+PortfolioContract.SoldStockData.COLUMN_SELL_GAIN +")"};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.SoldStockData.URI,
                soldAffectedColumn, null, null, null);

        // Adds the value of the already sold stock to the portfolio
        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            buyTotal = queryCursor.getDouble(0);
            sellTotal = queryCursor.getDouble(1);
            variationTotal = queryCursor.getDouble(2);
            totalGain = queryCursor.getDouble(2);
        }

        // Return column should be the sum of value total, income total, value gain
        String[] affectedColumn = {"sum("+ PortfolioContract.StockData.COLUMN_VARIATION +"), " +
                "sum("+ PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.StockData.COLUMN_INCOME_TOTAL +"), " +
                "sum("+ PortfolioContract.StockData.COLUMN_CURRENT_TOTAL +"), " +
                "sum("+PortfolioContract.StockData.COLUMN_TOTAL_GAIN +")"};

        // Check if the symbol exists in the db
        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                affectedColumn, null, null, null);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            variationTotal += queryCursor.getDouble(0);
            buyTotal += queryCursor.getDouble(1);
            double incomeTotal = queryCursor.getDouble(2);
            mCurrentTotal += queryCursor.getDouble(3);
            totalGain += queryCursor.getDouble(4);
            double variationPercent = variationTotal/buyTotal*100;
            double incomePercent = incomeTotal/buyTotal*100;
            double totalGainPercent = totalGain/buyTotal*100;

            // Values to be inserted or updated on StockPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_VARIATION_TOTAL, variationTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_BUY_TOTAL, buyTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_SOLD_TOTAL, sellTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL, incomeTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_TOTAL_GAIN, totalGain);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_CURRENT_TOTAL, mCurrentTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_VARIATION_PERCENT, variationPercent);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_INCOME_PERCENT, incomePercent);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_TOTAL_GAIN_PERCENT, totalGainPercent);

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

            } else {
                // Creates table and add values
                Uri insertedStockDataUri = mContext.getContentResolver().insert(PortfolioContract.StockPortfolio.URI,
                        portfolioCV);
            }
            updatePortfolio();
        }
    }

    // Reads all of Investment Portfolios value and sets the calculation on Portfolio table
    // Dosent need any data because it will not query for a specific investment, but for all of them.
    public void updatePortfolio(){
        // TODO: Develop function to read all Stock, FII, Fixed Income, etc table to get total value of portfolio
        updateCurrentPercent();
    }

    public void updateCurrentPercent(){
        // Check if the symbol exists in the stock data db
        Cursor queryDataCursor = mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                null, null, null, null);
        double percentSum = 0;
        double currentPercent = 0;
        if (queryDataCursor.getCount() > 0){
            queryDataCursor.moveToFirst();
            // Update the Current Percent of each StockData
            // TODO: Need to change this to bulk_insert, so it wont open and close db lots of times
            do {
                String _id = String.valueOf(queryDataCursor.getInt(queryDataCursor.getColumnIndex(
                        PortfolioContract.StockData._ID)));
                double currentDataTotal = queryDataCursor.getDouble(queryDataCursor.getColumnIndex(
                        PortfolioContract.StockData.COLUMN_CURRENT_TOTAL));
                if (queryDataCursor.isLast()){
                    // If it is last, round last so sum of all will be 100%
                    Log.d(LOG_TAG, "isLast() sum: " + percentSum);
                    currentPercent = 100 - percentSum;
                } else {
                    // else calculates current percent for stock
                    String currentPercentString = String.format(Locale.US, "%.2f",currentDataTotal/mCurrentTotal*100);
                    currentPercent = Double.valueOf(currentPercentString);
                    percentSum += currentPercent;
                }

                ContentValues stockDataCV = new ContentValues();
                stockDataCV.put(PortfolioContract.StockData.COLUMN_CURRENT_PERCENT, currentPercent);

                // Update
                // Prepare query to update stock data
                String updateSelection = PortfolioContract.StockData._ID + " = ?";
                String[] updatedSelectionArguments = {_id};

                // Update value on stock data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.StockData.URI,
                        stockDataCV, updateSelection, updatedSelectionArguments);
                // Log update success/fail result
                if (updatedRows > 0){
                    Log.d(LOG_TAG, "updateStockData successfully updated");
                } else {
                    Log.d(LOG_TAG, "updateStockData failed update");
                }
            } while (queryDataCursor.moveToNext());
        } else {
            Log.d(LOG_TAG, "No StockData found");
        }
    }
}
