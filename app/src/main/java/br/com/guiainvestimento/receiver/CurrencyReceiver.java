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

public class CurrencyReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = CurrencyReceiver.class.getSimpleName();

    private Context mContext;

    private double mCurrentTotal = 0;

    @Override
    public void onReceive(Context c, Intent intent){
        mContext = c;
        updateCurrencyPortfolio();
    }

    // Reads all of Currencies Data value and sets the calculation on CurrencyPortfolio table
    // Dosent need any data because it will not query for a specific Currency, but for all of them.
    public void updateCurrencyPortfolio(){

        double buyTotal = 0;
        double totalGain = 0;
        double variationTotal = 0;
        double sellTotal = 0;
        // Return column should be the sum of buy total, sell total, sell gain
        String[] soldAffectedColumn = {"sum("+ PortfolioContract.SoldCurrencyData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.SoldCurrencyData.COLUMN_SELL_TOTAL +"), " +
                "sum("+PortfolioContract.SoldCurrencyData.COLUMN_SELL_GAIN +")"};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.SoldCurrencyData.URI,
                soldAffectedColumn, null, null, null);

        // Adds the value of the already sold Currency to the portfolio
        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            buyTotal = queryCursor.getDouble(0);
            sellTotal = queryCursor.getDouble(1);
            variationTotal = queryCursor.getDouble(2);
            totalGain = queryCursor.getDouble(2);
        }

        // Return column should be the sum of value total, income total, value gain
        String[] affectedColumn = {"sum("+ PortfolioContract.CurrencyData.COLUMN_VARIATION +"), " +
                "sum("+ PortfolioContract.CurrencyData.COLUMN_BUY_VALUE_TOTAL +"), " +
                // "sum("+ PortfolioContract.CurrencyData.COLUMN_INCOME +"), " +
                "sum("+ PortfolioContract.CurrencyData.COLUMN_CURRENT_TOTAL +"), " +
                "sum("+PortfolioContract.CurrencyData.COLUMN_TOTAL_GAIN +")"};

        // Check if the symbol exists in the db
        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.CurrencyData.URI,
                affectedColumn, null, null, null);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            variationTotal += queryCursor.getDouble(0);
            buyTotal += queryCursor.getDouble(1);
            mCurrentTotal += queryCursor.getDouble(2);
            totalGain += queryCursor.getDouble(3);
            double variationPercent = variationTotal/buyTotal*100;
            double totalGainPercent = totalGain/buyTotal*100;

            // Values to be inserted or updated on CurrencyPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.CurrencyPortfolio.COLUMN_VARIATION_TOTAL, variationTotal);
            portfolioCV.put(PortfolioContract.CurrencyPortfolio.COLUMN_BUY_TOTAL, buyTotal);
            portfolioCV.put(PortfolioContract.CurrencyPortfolio.COLUMN_SOLD_TOTAL, sellTotal);
            portfolioCV.put(PortfolioContract.CurrencyPortfolio.COLUMN_TOTAL_GAIN, totalGain);
            portfolioCV.put(PortfolioContract.CurrencyPortfolio.COLUMN_CURRENT_TOTAL, mCurrentTotal);

            // Query for the only Currency portfolio, if dosent exist, creates one
            Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.CurrencyPortfolio.URI,
                    null, null, null, null);
            // If exists, updates value, else create a new field and add values
            if(portfolioQueryCursor.getCount() > 0){
                portfolioQueryCursor.moveToFirst();
                String _id = String.valueOf(portfolioQueryCursor.getInt(portfolioQueryCursor.getColumnIndex(PortfolioContract.CurrencyPortfolio._ID)));
                // Prepare query to update Currency data
                String updateSelection = PortfolioContract.CurrencyPortfolio._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                // Update value on Currency data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.CurrencyPortfolio.URI,
                        portfolioCV, updateSelection, updatedSelectionArguments);

            } else {
                // Creates table and add values
                Uri insertedCurrencyPortfolioUri = mContext.getContentResolver().insert(PortfolioContract.CurrencyPortfolio.URI,
                        portfolioCV);
            }
            // Prepare URI with Current Total to bulkupdate the Current Percent
            Uri updateCurrentURI = PortfolioContract.CurrencyData.BULK_UPDATE_URI.buildUpon().appendPath(Double.toString(mCurrentTotal)).build();
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
