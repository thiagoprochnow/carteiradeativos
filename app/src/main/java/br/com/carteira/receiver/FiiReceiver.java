package br.com.carteira.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import br.com.carteira.data.PortfolioContract;

public class FiiReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = FiiReceiver.class.getSimpleName();

    private Context mContext;

    private double mCurrentTotal = 0;

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
        // Return column should be the sum of buy total, sell total, sell gain
        String[] soldAffectedColumn = {"sum("+ PortfolioContract.SoldFiiData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.SoldFiiData.COLUMN_SELL_TOTAL +"), " +
                "sum("+PortfolioContract.SoldFiiData.COLUMN_SELL_GAIN +")"};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.SoldFiiData.URI,
                soldAffectedColumn, null, null, null);

        // Adds the value of the already sold fii to the portfolio
        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            buyTotal = queryCursor.getDouble(0);
            sellTotal = queryCursor.getDouble(1);
            variationTotal = queryCursor.getDouble(2);
            totalGain = queryCursor.getDouble(2);
        }

        // Return column should be the sum of value total, income total, value gain
        String[] affectedColumn = {"sum("+ PortfolioContract.FiiData.COLUMN_VARIATION +"), " +
                "sum("+ PortfolioContract.FiiData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.FiiData.COLUMN_INCOME +"), " +
                "sum("+ PortfolioContract.FiiData.COLUMN_CURRENT_TOTAL +"), " +
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
            totalGain += queryCursor.getDouble(4);
            double variationPercent = variationTotal/buyTotal*100;
            double incomePercent = incomeTotal/buyTotal*100;
            double totalGainPercent = totalGain/buyTotal*100;

            // Values to be inserted or updated on FiiPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_VARIATION_TOTAL, variationTotal);
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_BUY_TOTAL, buyTotal);
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_SOLD_TOTAL, sellTotal);
            portfolioCV.put(PortfolioContract.FiiPortfolio.COLUMN_INCOME_TOTAL, incomeTotal);
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
        }
    }

    // Reads all of Investment Portfolios value and sets the calculation on Portfolio table
    // Dosent need any data because it will not query for a specific investment, but for all of them.
    public void updatePortfolio(){
        // TODO: Develop function to read all Stock, FII, Fixed Income, etc table to get total value of portfolio
        // updateCurrentPercent();
    }
}
