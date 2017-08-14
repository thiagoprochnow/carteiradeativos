package br.com.guiainvestimento.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;

public class OthersReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = OthersReceiver.class.getSimpleName();

    private Context mContext;

    private double mCurrentTotal = 0;

    @Override
    public void onReceive(Context c, Intent intent){
        mContext = c;
        updateOthersPortfolio();
    }

    // Reads all of Others Data value and sets the calculation on OthersPortfolio table
    // Dosent need any data because it will not query for a specific others income, but for all of them.
    public void updateOthersPortfolio(){

        double buyTotal = 0;
        double totalGain = 0;
        double sellTotal = 0;
        double variationTotal = 0;
        double incomeTotal = 0;

        // Return column should be the sum of value total, income total, value gain
        String[] affectedColumn = {"sum("+ PortfolioContract.OthersData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.OthersData.COLUMN_CURRENT_TOTAL +"), " +
                "sum("+PortfolioContract.OthersData.COLUMN_TOTAL_GAIN +"), " +
                "sum("+PortfolioContract.OthersData.COLUMN_SELL_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.OthersData.COLUMN_INCOME +"), " +
                "sum("+PortfolioContract.OthersData.COLUMN_VARIATION +")"};

        // Check if the symbol exists in the db
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.OthersData.URI,
                affectedColumn, null, null, null);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            buyTotal += queryCursor.getDouble(0);
            mCurrentTotal += queryCursor.getDouble(1);
            totalGain += queryCursor.getDouble(2);
            sellTotal += queryCursor.getDouble(3);
            double totalGainPercent = totalGain/buyTotal*100;
            incomeTotal += queryCursor.getDouble(4);
            variationTotal += queryCursor.getDouble(5);

            // Values to be inserted or updated on OthersPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.OthersPortfolio.COLUMN_VARIATION_TOTAL, variationTotal);
            portfolioCV.put(PortfolioContract.OthersPortfolio.COLUMN_INCOME_TOTAL, incomeTotal);
            portfolioCV.put(PortfolioContract.OthersPortfolio.COLUMN_BUY_TOTAL, buyTotal);
            portfolioCV.put(PortfolioContract.OthersPortfolio.COLUMN_TOTAL_GAIN, totalGain);
            portfolioCV.put(PortfolioContract.OthersPortfolio.COLUMN_CURRENT_TOTAL, mCurrentTotal);
            portfolioCV.put(PortfolioContract.OthersPortfolio.COLUMN_SOLD_TOTAL, sellTotal);

            // Query for the only others portfolio, if dosent exist, creates one
            Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.OthersPortfolio.URI,
                    null, null, null, null);
            // If exists, updates value, else create a new field and add values
            if(portfolioQueryCursor.getCount() > 0){
                portfolioQueryCursor.moveToFirst();
                String _id = String.valueOf(portfolioQueryCursor.getInt(portfolioQueryCursor.getColumnIndex(PortfolioContract.OthersPortfolio._ID)));
                // Prepare query to update others data
                String updateSelection = PortfolioContract.OthersPortfolio._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                // Update value on others portfolio
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.OthersPortfolio.URI,
                        portfolioCV, updateSelection, updatedSelectionArguments);

            } else {
                // Creates table and add values
                Uri insertedOthersPortfolioUri = mContext.getContentResolver().insert(PortfolioContract.OthersPortfolio.URI,
                        portfolioCV);
            }
            // Prepare URI with Current Total to bulkupdate the Current Percent
            Uri updateCurrentURI = PortfolioContract.OthersData.BULK_UPDATE_URI.buildUpon().appendPath(Double.toString(mCurrentTotal)).build();
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
