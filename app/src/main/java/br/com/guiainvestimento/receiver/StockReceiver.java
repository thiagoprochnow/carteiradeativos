package br.com.guiainvestimento.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;

public class StockReceiver {

    private static final String LOG_TAG = StockReceiver.class.getSimpleName();

    private Context mContext;

    public StockReceiver(Context context){
        mContext = context;
    }

    // Reads all of Stock Data value and sets the calculation on StockPortfolio table
    // Dosent need any data because it will not query for a specific stock, but for all of them.
    public void updateStockPortfolio(){

        double buyTotal = 0;
        double totalGain = 0;
        double variationTotal = 0;
        double sellTotal = 0;
        double brokerage = 0;
        double mCurrentTotal = 0;
        // Return column should be the sum of buy total, sell total, sell gain
        String[] soldAffectedColumn = {"sum("+ PortfolioContract.SoldStockData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.SoldStockData.COLUMN_SELL_TOTAL +"), " +
                "sum("+ PortfolioContract.SoldStockData.COLUMN_BROKERAGE +"), " +
                "sum("+PortfolioContract.SoldStockData.COLUMN_SELL_GAIN +")"};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.SoldStockData.URI,
                soldAffectedColumn, null, null, null);

        // Adds the value of the already sold stock to the portfolio
        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            buyTotal = queryCursor.getDouble(0);
            sellTotal = queryCursor.getDouble(1);
            brokerage = queryCursor.getDouble(2);
            // Variation cannot count brokerage discount
            variationTotal = queryCursor.getDouble(3) + queryCursor.getDouble(2);
            totalGain = queryCursor.getDouble(3);
        }

        // Return column should be the sum of value total, income total, value gain
        String[] affectedColumn = {"sum("+ PortfolioContract.StockData.COLUMN_VARIATION +"), " +
                "sum("+ PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.StockData.COLUMN_NET_INCOME +"), " +
                "sum("+ PortfolioContract.StockData.COLUMN_CURRENT_TOTAL +"), " +
                "sum("+ PortfolioContract.StockData.COLUMN_BROKERAGE +"), " +
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
            brokerage += queryCursor.getDouble(4);
            totalGain += queryCursor.getDouble(5);
            double variationPercent = variationTotal/buyTotal*100;
            double incomePercent = incomeTotal/buyTotal*100;
            double totalGainPercent = totalGain/buyTotal*100;

            // Values to be inserted or updated on StockPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_VARIATION_TOTAL, variationTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_BUY_TOTAL, buyTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_SOLD_TOTAL, sellTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL, incomeTotal);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_BROKERAGE, brokerage);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_TOTAL_GAIN, totalGain);
            portfolioCV.put(PortfolioContract.StockPortfolio.COLUMN_CURRENT_TOTAL, mCurrentTotal);

            // Query for the only stock portfolio, if dosent exist, creates one
            Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockPortfolio.URI,
                    null, null, null, null);
            // If exists, updates value, else create a new field and add values
            if(portfolioQueryCursor.getCount() > 0){
                portfolioQueryCursor.moveToFirst();
                String _id = String.valueOf(portfolioQueryCursor.getInt(portfolioQueryCursor.getColumnIndex(PortfolioContract.StockPortfolio._ID)));
                // Prepare query to update stock data
                String updateSelection = PortfolioContract.StockPortfolio._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                // Update value on stock data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.StockPortfolio.URI,
                        portfolioCV, updateSelection, updatedSelectionArguments);

            } else {
                // Creates table and add values
                Uri insertedStockPortfolioUri = mContext.getContentResolver().insert(PortfolioContract.StockPortfolio.URI,
                        portfolioCV);
            }
            // Prepare URI with Current Total to bulkupdate the Current Percent
            Uri updateCurrentURI = PortfolioContract.StockData.BULK_UPDATE_URI.buildUpon().appendPath(Double.toString(mCurrentTotal)).build();
            int updatedRows = mContext.getContentResolver().update(
                    updateCurrentURI, null, null, null);
        }
    }
}
