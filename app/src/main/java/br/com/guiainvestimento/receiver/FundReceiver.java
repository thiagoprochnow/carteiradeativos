package br.com.guiainvestimento.receiver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import br.com.guiainvestimento.data.PortfolioContract;

public class FundReceiver {

    private static final String LOG_TAG = FundReceiver.class.getSimpleName();

    private Context mContext;

    public FundReceiver(Context context){
        mContext = context;
    }

    // Reads all of Fund Data value and sets the calculation on FundPortfolio table
    // Dosent need any data because it will not query for a specific fund income, but for all of them.
    public void updateFundPortfolio(){

        double buyTotal = 0;
        double totalGain = 0;
        double sellTotal = 0;
        double mCurrentTotal = 0;

        // Return column should be the sum of value total, income total, value gain
        String[] affectedColumn = {"sum("+ PortfolioContract.FundData.COLUMN_BUY_VALUE_TOTAL +"), " +
                "sum("+ PortfolioContract.FundData.COLUMN_CURRENT_TOTAL +"), " +
                "sum("+PortfolioContract.FundData.COLUMN_TOTAL_GAIN +"), " +
                "sum("+PortfolioContract.FundData.COLUMN_SELL_VALUE_TOTAL +")"};

        // Check if the symbol exists in the db
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FundData.URI,
                affectedColumn, null, null, null);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            buyTotal += queryCursor.getDouble(0);
            mCurrentTotal += queryCursor.getDouble(1);
            totalGain += queryCursor.getDouble(2);
            sellTotal += queryCursor.getDouble(3);
            double totalGainPercent = totalGain/buyTotal*100;

            // Values to be inserted or updated on FundPortfolio table
            ContentValues portfolioCV = new ContentValues();
            portfolioCV.put(PortfolioContract.FundPortfolio.COLUMN_BUY_TOTAL, buyTotal);
            portfolioCV.put(PortfolioContract.FundPortfolio.COLUMN_TOTAL_GAIN, totalGain);
            portfolioCV.put(PortfolioContract.FundPortfolio.COLUMN_CURRENT_TOTAL, mCurrentTotal);
            portfolioCV.put(PortfolioContract.FundPortfolio.COLUMN_SOLD_TOTAL, sellTotal);

            // Query for the only fund portfolio, if dosent exist, creates one
            Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.FundPortfolio.URI,
                    null, null, null, null);
            // If exists, updates value, else create a new field and add values
            if(portfolioQueryCursor.getCount() > 0){
                portfolioQueryCursor.moveToFirst();
                String _id = String.valueOf(portfolioQueryCursor.getInt(portfolioQueryCursor.getColumnIndex(PortfolioContract.FundPortfolio._ID)));
                // Prepare query to update fund data
                String updateSelection = PortfolioContract.FundPortfolio._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                // Update value on fund portfolio
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.FundPortfolio.URI,
                        portfolioCV, updateSelection, updatedSelectionArguments);

            } else {
                // Creates table and add values
                Uri insertedFundPortfolioUri = mContext.getContentResolver().insert(PortfolioContract.FundPortfolio.URI,
                        portfolioCV);
            }
            // Prepare URI with Current Total to bulkupdate the Current Percent
            Uri updateCurrentURI = PortfolioContract.FundData.BULK_UPDATE_URI.buildUpon().appendPath(Double.toString(mCurrentTotal)).build();
            int updatedRows = mContext.getContentResolver().update(
                    updateCurrentURI, null, null, null);
        }
    }
}
