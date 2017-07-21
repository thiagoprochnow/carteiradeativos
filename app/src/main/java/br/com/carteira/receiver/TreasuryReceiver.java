package br.com.carteira.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;

public class TreasuryReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = TreasuryReceiver.class.getSimpleName();

    private Context mContext;

    private double mCurrentTotal = 0;

    @Override
    public void onReceive(Context c, Intent intent){
        mContext = c;
        updateTreasuryPortfolio();
    }

    // Reads all of Treasury Data value and sets the calculation on TreasuryPortfolio table
    // Dosent need any data because it will not query for a specific treasury, but for all of them.
    public void updateTreasuryPortfolio(){

        double buyTotal = 0;
        double totalGain = 0;
        double variationTotal = 0;
        double sellTotal = 0;
        // Return column should be the sum of buy total, sell total, sell gain
        String[] soldAffectedColumn = {"sum("+ PortfolioContract.SoldTreasuryData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.SoldTreasuryData.COLUMN_SELL_TOTAL +"), " +
                "sum("+PortfolioContract.SoldTreasuryData.COLUMN_SELL_GAIN +")"};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.SoldTreasuryData.URI,
                soldAffectedColumn, null, null, null);

        // Adds the value of the already sold treasury to the portfolio
        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            buyTotal = queryCursor.getDouble(0);
            sellTotal = queryCursor.getDouble(1);
            variationTotal = queryCursor.getDouble(2);
            totalGain = queryCursor.getDouble(2);
        }

        // Return column should be the sum of value total, income total, value gain
        String[] affectedColumn = {"sum("+ PortfolioContract.TreasuryData.COLUMN_VARIATION +"), " +
                "sum("+ PortfolioContract.TreasuryData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.TreasuryData.COLUMN_INCOME +"), " +
                "sum("+ PortfolioContract.TreasuryData.COLUMN_CURRENT_TOTAL +"), " +
                "sum("+PortfolioContract.TreasuryData.COLUMN_TOTAL_GAIN +")"};

        // Check if the symbol exists in the db
        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.TreasuryData.URI,
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

            // Values to be inserted or updated on TreasuryPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.TreasuryPortfolio.COLUMN_VARIATION_TOTAL, variationTotal);
            portfolioCV.put(PortfolioContract.TreasuryPortfolio.COLUMN_BUY_TOTAL, buyTotal);
            portfolioCV.put(PortfolioContract.TreasuryPortfolio.COLUMN_SOLD_TOTAL, sellTotal);
            portfolioCV.put(PortfolioContract.TreasuryPortfolio.COLUMN_INCOME_TOTAL, incomeTotal);
            portfolioCV.put(PortfolioContract.TreasuryPortfolio.COLUMN_TOTAL_GAIN, totalGain);
            portfolioCV.put(PortfolioContract.TreasuryPortfolio.COLUMN_CURRENT_TOTAL, mCurrentTotal);

            // Query for the only treasury portfolio, if dosent exist, creates one
            Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.TreasuryPortfolio.URI,
                    null, null, null, null);
            // If exists, updates value, else create a new field and add values
            if(portfolioQueryCursor.getCount() > 0){
                portfolioQueryCursor.moveToFirst();
                String _id = String.valueOf(portfolioQueryCursor.getInt(portfolioQueryCursor.getColumnIndex(PortfolioContract.TreasuryPortfolio._ID)));
                // Prepare query to update treasury data
                String updateSelection = PortfolioContract.TreasuryPortfolio._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                // Update value on treasury data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.TreasuryPortfolio.URI,
                        portfolioCV, updateSelection, updatedSelectionArguments);

            } else {
                // Creates table and add values
                Uri insertedTreasuryPortfolioUri = mContext.getContentResolver().insert(PortfolioContract.TreasuryPortfolio.URI,
                        portfolioCV);
            }
            // Prepare URI with Current Total to bulkupdate the Current Percent
            Uri updateCurrentURI = PortfolioContract.TreasuryData.BULK_UPDATE_URI.buildUpon().appendPath(Double.toString(mCurrentTotal)).build();
            int updatedRows = mContext.getContentResolver().update(
                    updateCurrentURI, null, null, null);
            if (updatedRows > 0){
                // Send Broadcast to update other values on Portfolio
                mContext.sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
            } else {
                Log.d(LOG_TAG, "Rows could not be updated");
            }
        }
    }
}
