package br.com.guiainvestimento.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;

public class FiiReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = FiiReceiver.class.getSimpleName();

    private Context mContext;

    @Override
    public void onReceive(Context c, Intent intent){
        mContext = c;
        updateFiiPortfolio();
    }

    // Reads all of Fii Data value and sets the calculation on FiiPortfolio table
    // Dosent need any data because it will not query for a specific fii, but for all of them.
    public void updateFiiPortfolio(){

        double buyTotal = 0;
        double totalGain = 0;
        double variationTotal = 0;
        double sellTotal = 0;
        double brokerage = 0;
        double mCurrentTotal = 0;
        // Return column should be the sum of buy total, sell total, sell gain
        String[] soldAffectedColumn = {"sum("+ PortfolioContract.SoldFiiData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.SoldFiiData.COLUMN_SELL_TOTAL +"), " +
                "sum("+ PortfolioContract.SoldFiiData.COLUMN_BROKERAGE +"), " +
                "sum("+PortfolioContract.SoldFiiData.COLUMN_SELL_GAIN +")"};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.SoldFiiData.URI,
                soldAffectedColumn, null, null, null);

        // Adds the value of the already sold fii to the portfolio
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
        String[] affectedColumn = {"sum("+ PortfolioContract.FiiData.COLUMN_VARIATION +"), " +
                "sum("+ PortfolioContract.FiiData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.FiiData.COLUMN_INCOME +"), " +
                "sum("+ PortfolioContract.FiiData.COLUMN_CURRENT_TOTAL +"), " +
                "sum("+ PortfolioContract.FiiData.COLUMN_BROKERAGE +"), " +
                "sum("+PortfolioContract.FiiData.COLUMN_TOTAL_GAIN +")"};

        // Check if the symbol exists in the db
        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FiiData.URI,
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

            // Values to be inserted or updated on FiiPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_VARIATION_TOTAL, variationTotal);
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_BUY_TOTAL, buyTotal);
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_SOLD_TOTAL, sellTotal);
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_INCOME_TOTAL, incomeTotal);
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_BROKERAGE, brokerage);
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_TOTAL_GAIN, totalGain);
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_CURRENT_TOTAL, mCurrentTotal);

            // Query for the only fii portfolio, if dosent exist, creates one
            Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.FiiPortfolio.URI,
                    null, null, null, null);
            // If exists, updates value, else create a new field and add values
            if(portfolioQueryCursor.getCount() > 0){
                portfolioQueryCursor.moveToFirst();
                String _id = String.valueOf(portfolioQueryCursor.getInt(portfolioQueryCursor.getColumnIndex(PortfolioContract.FiiPortfolio._ID)));
                // Prepare query to update fii data
                String updateSelection = PortfolioContract.FiiPortfolio._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                // Update value on fii data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.FiiPortfolio.URI,
                        portfolioCV, updateSelection, updatedSelectionArguments);

            } else {
                // Creates table and add values
                Uri insertedFiiPortfolioUri = mContext.getContentResolver().insert(PortfolioContract.FiiPortfolio.URI,
                        portfolioCV);
            }
            // Prepare URI with Current Total to bulkupdate the Current Percent
            Uri updateCurrentURI = PortfolioContract.FiiData.BULK_UPDATE_URI.buildUpon().appendPath(Double.toString(mCurrentTotal)).build();
            int updatedRows = mContext.getContentResolver().update(
                    updateCurrentURI, null, null, null);
            // Send Broadcast to update other values on Portfolio
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
        }
    }
}
