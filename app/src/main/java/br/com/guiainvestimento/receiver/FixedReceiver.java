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

public class FixedReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = FixedReceiver.class.getSimpleName();

    private Context mContext;

    @Override
    public void onReceive(Context c, Intent intent){
        mContext = c;
        updateFixedPortfolio();
    }

    // Reads all of Fixed Data value and sets the calculation on FixedPortfolio table
    // Dosent need any data because it will not query for a specific fixed income, but for all of them.
    public void updateFixedPortfolio(){

        double buyTotal = 0;
        double totalGain = 0;
        double sellTotal = 0;
        double mCurrentTotal = 0;

        // Return column should be the sum of value total, income total, value gain
        String[] affectedColumn = {"sum("+ PortfolioContract.FixedData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.FixedData.COLUMN_CURRENT_TOTAL +"), " +
                "sum("+PortfolioContract.FixedData.COLUMN_TOTAL_GAIN +"), " +
                "sum("+PortfolioContract.FixedData.COLUMN_SELL_VALUE_TOTAL +")"};

        // Check if the symbol exists in the db
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FixedData.URI,
                affectedColumn, null, null, null);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            buyTotal += queryCursor.getDouble(0);
            mCurrentTotal += queryCursor.getDouble(1);
            totalGain += queryCursor.getDouble(2);
            sellTotal += queryCursor.getDouble(3);
            double totalGainPercent = totalGain/buyTotal*100;

            // Values to be inserted or updated on FixedPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.FixedPortfolio.COLUMN_BUY_TOTAL, buyTotal);
            portfolioCV.put(PortfolioContract.FixedPortfolio.COLUMN_TOTAL_GAIN, totalGain);
            portfolioCV.put(PortfolioContract.FixedPortfolio.COLUMN_CURRENT_TOTAL, mCurrentTotal);
            portfolioCV.put(PortfolioContract.FixedPortfolio.COLUMN_SOLD_TOTAL, sellTotal);

            // Query for the only fixed portfolio, if dosent exist, creates one
            Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.FixedPortfolio.URI,
                    null, null, null, null);
            // If exists, updates value, else create a new field and add values
            if(portfolioQueryCursor.getCount() > 0){
                portfolioQueryCursor.moveToFirst();
                String _id = String.valueOf(portfolioQueryCursor.getInt(portfolioQueryCursor.getColumnIndex(PortfolioContract.FixedPortfolio._ID)));
                // Prepare query to update fixed data
                String updateSelection = PortfolioContract.FixedPortfolio._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                // Update value on fixed portfolio
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.FixedPortfolio.URI,
                        portfolioCV, updateSelection, updatedSelectionArguments);

            } else {
                // Creates table and add values
                Uri insertedFixedPortfolioUri = mContext.getContentResolver().insert(PortfolioContract.FixedPortfolio.URI,
                        portfolioCV);
            }
            // Prepare URI with Current Total to bulkupdate the Current Percent
            Uri updateCurrentURI = PortfolioContract.FixedData.BULK_UPDATE_URI.buildUpon().appendPath(Double.toString(mCurrentTotal)).build();
            int updatedRows = mContext.getContentResolver().update(
                    updateCurrentURI, null, null, null);
            // Send Broadcast to update other values on Portfolio
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
        }
    }
}
