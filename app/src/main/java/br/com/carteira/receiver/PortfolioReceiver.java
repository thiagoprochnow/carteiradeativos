package br.com.carteira.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import br.com.carteira.data.PortfolioContract;

public class PortfolioReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = PortfolioReceiver.class.getSimpleName();

    private Context mContext;

    private double mCurrentTotal = 0;

    @Override
    public void onReceive(Context c, Intent intent){
        mContext = c;
        updatePortfolio();
    }


    // Reads all of Investment Portfolios value and sets the calculation on Portfolio table
    // Dosent need any data because it will not query for a specific investment, but for all of them.
    public void updatePortfolio(){

        // Treasury
        Cursor treasuryPortfolioCursor = getTreasuryPortfolio();

        double treasuryBuy = 0;
        double treasurySold = 0;
        double treasuryCurrent = 0;
        double treasuryVariation = 0;
        double treasuryIncome = 0;
        double treasuryGain = 0;

        if (treasuryPortfolioCursor.getCount() > 0) {
            treasuryPortfolioCursor.moveToFirst();
            treasuryBuy = treasuryPortfolioCursor.getDouble(0);
            treasurySold = treasuryPortfolioCursor.getDouble(1);
            treasuryCurrent = treasuryPortfolioCursor.getDouble(2);
            treasuryVariation = treasuryPortfolioCursor.getDouble(3);
            treasuryIncome = treasuryPortfolioCursor.getDouble(4);
            treasuryGain = treasuryPortfolioCursor.getDouble(5);
        } else {
            Log.d(LOG_TAG, "Treasury portfolio not found");
        }

        // Fixed
        Cursor fixedPortfolioCursor = getFixedPortfolio();

        double fixedBuy = 0;
        double fixedSold = 0;
        double fixedCurrent = 0;
        double fixedVariation = 0;
        double fixedIncome = 0;
        double fixedGain = 0;

        if (fixedPortfolioCursor.getCount() > 0) {
            fixedPortfolioCursor.moveToFirst();
            fixedBuy = fixedPortfolioCursor.getDouble(0);
            fixedSold = fixedPortfolioCursor.getDouble(1);
            fixedCurrent = fixedPortfolioCursor.getDouble(2);
            fixedVariation = fixedPortfolioCursor.getDouble(3);
            fixedIncome = fixedPortfolioCursor.getDouble(4);
            fixedGain = fixedPortfolioCursor.getDouble(5);
        } else {
            Log.d(LOG_TAG, "Fixed portfolio not found");
        }

        // Stock
        Cursor stockPortfolioCursor = getStockPortfolio();

        double stockBuy = 0;
        double stockSold = 0;
        double stockCurrent = 0;
        double stockVariation = 0;
        double stockIncome = 0;
        double stockGain = 0;

        if (stockPortfolioCursor.getCount() > 0) {
            stockPortfolioCursor.moveToFirst();
            stockBuy = stockPortfolioCursor.getDouble(0);
            stockSold = stockPortfolioCursor.getDouble(1);
            stockCurrent = stockPortfolioCursor.getDouble(2);
            stockVariation = stockPortfolioCursor.getDouble(3);
            stockIncome = stockPortfolioCursor.getDouble(4);
            stockGain = stockPortfolioCursor.getDouble(5);
        } else {
            Log.d(LOG_TAG, "Stock portfolio not found");
        }

        // Fii
        Cursor fiiPortfolioCursor = getFiiPortfolio();

        double fiiBuy = 0;
        double fiiSold = 0;
        double fiiCurrent = 0;
        double fiiVariation = 0;
        double fiiIncome = 0;
        double fiiGain = 0;

        if (fiiPortfolioCursor.getCount() > 0) {
            fiiPortfolioCursor.moveToFirst();
            fiiBuy = fiiPortfolioCursor.getDouble(0);
            fiiSold = fiiPortfolioCursor.getDouble(1);
            fiiCurrent = fiiPortfolioCursor.getDouble(2);
            fiiVariation = fiiPortfolioCursor.getDouble(3);
            fiiIncome = fiiPortfolioCursor.getDouble(4);
            fiiGain = fiiPortfolioCursor.getDouble(5);
        } else {
            Log.d(LOG_TAG, "Fii portfolio not found");
        }

        // Currency
        Cursor currencyPortfolioCursor = getCurrencyPortfolio();

        double currencyBuy = 0;
        double currencySold = 0;
        double currencyCurrent = 0;
        double currencyVariation = 0;
        double currencyGain = 0;

        if (currencyPortfolioCursor.getCount() > 0) {
            currencyPortfolioCursor.moveToFirst();
            currencyBuy = currencyPortfolioCursor.getDouble(0);
            currencySold = currencyPortfolioCursor.getDouble(1);
            currencyCurrent = currencyPortfolioCursor.getDouble(2);
            currencyVariation = currencyPortfolioCursor.getDouble(3);
            currencyGain = currencyPortfolioCursor.getDouble(4);
        } else {
            Log.d(LOG_TAG, "Currency portfolio not found");
        }

        // Sums all portfolio to get main portfolio
        double portfolioBuy = treasuryBuy + fixedBuy + stockBuy + fiiBuy + currencyBuy;
        double portfolioSold = treasurySold + fixedSold + stockSold + fiiSold + currencySold;
        double portfolioCurrent = treasuryCurrent + fixedCurrent + stockCurrent + fiiCurrent + currencyCurrent;
        double portfolioVariation = treasuryVariation + fixedVariation + stockVariation + fiiVariation + currencyVariation;
        double portfolioIncome = treasuryIncome + fixedIncome + stockIncome + fiiIncome;
        double portfolioGain =  treasuryGain + fixedGain + stockGain + fiiGain + currencyGain;

        // Values to be inserted or updated on Portfolio table
        ContentValues portfolioCV = new ContentValues();
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_BUY_TOTAL, portfolioBuy);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_SOLD_TOTAL, portfolioSold);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_CURRENT_TOTAL, portfolioCurrent);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_VARIATION_TOTAL, portfolioVariation);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_INCOME_TOTAL, portfolioIncome);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_TOTAL_GAIN, portfolioGain);

        // Query for the only portfolio, if dosent exist, creates one
        Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.Portfolio.URI,
                null, null, null, null);
        // If exists, updates value, else create a new field and add values
        if(portfolioQueryCursor.getCount() > 0){
            portfolioQueryCursor.moveToFirst();
            String _id = String.valueOf(portfolioQueryCursor.getInt(portfolioQueryCursor.getColumnIndex(PortfolioContract.Portfolio._ID)));
            // Prepare query to update portfolio
            String updateSelection = PortfolioContract.Portfolio._ID + " = ?";
            String[] updatedSelectionArguments = {_id};
            // Update value on portfolio
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.Portfolio.URI,
                    portfolioCV, updateSelection, updatedSelectionArguments);

        } else {
            // Creates table and add values
            Uri insertedFixedPortfolioUri = mContext.getContentResolver().insert(PortfolioContract.Portfolio.URI,
                    portfolioCV);
        }
    }

    // Return useful value to update portfolio table
    public Cursor getTreasuryPortfolio(){
        String[] affectedColumn = {"sum("+ PortfolioContract.TreasuryPortfolio.COLUMN_BUY_TOTAL+")",
                "sum("+ PortfolioContract.TreasuryPortfolio.COLUMN_SOLD_TOTAL+")",
                "sum("+ PortfolioContract.TreasuryPortfolio.COLUMN_CURRENT_TOTAL+")",
                "sum("+ PortfolioContract.TreasuryPortfolio.COLUMN_VARIATION_TOTAL+")",
                "sum("+ PortfolioContract.TreasuryPortfolio.COLUMN_INCOME_TOTAL+")",
                "sum("+ PortfolioContract.TreasuryPortfolio.COLUMN_TOTAL_GAIN+")"};

        return mContext.getContentResolver().query(
                PortfolioContract.TreasuryPortfolio.URI,
                affectedColumn, null, null, null);
    }

    // Return useful value to update portfolio table
    public Cursor getFixedPortfolio(){
        String[] affectedColumn = {"sum("+ PortfolioContract.FixedPortfolio.COLUMN_BUY_TOTAL+")",
                "sum("+ PortfolioContract.FixedPortfolio.COLUMN_SOLD_TOTAL+")",
                "sum("+ PortfolioContract.FixedPortfolio.COLUMN_CURRENT_TOTAL+")",
                "sum("+ PortfolioContract.FixedPortfolio.COLUMN_VARIATION_TOTAL+")",
                "sum("+ PortfolioContract.FixedPortfolio.COLUMN_INCOME_TOTAL+")",
                "sum("+ PortfolioContract.FixedPortfolio.COLUMN_TOTAL_GAIN+")"};

        return mContext.getContentResolver().query(
                PortfolioContract.FixedPortfolio.URI,
                affectedColumn, null, null, null);
    }

    // Return useful value to update portfolio table
    public Cursor getStockPortfolio(){
        String[] affectedColumn = {"sum("+ PortfolioContract.StockPortfolio.COLUMN_BUY_TOTAL+")",
                "sum("+ PortfolioContract.StockPortfolio.COLUMN_SOLD_TOTAL+")",
                "sum("+ PortfolioContract.StockPortfolio.COLUMN_CURRENT_TOTAL+")",
                "sum("+ PortfolioContract.StockPortfolio.COLUMN_VARIATION_TOTAL+")",
                "sum("+ PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL+")",
                "sum("+ PortfolioContract.StockPortfolio.COLUMN_TOTAL_GAIN+")"};

        return mContext.getContentResolver().query(
                PortfolioContract.StockPortfolio.URI,
                affectedColumn, null, null, null);
    }

    // Return useful value to update portfolio table
    public Cursor getFiiPortfolio(){
        String[] affectedColumn = {"sum("+ PortfolioContract.FiiPortfolio.COLUMN_BUY_TOTAL+")",
                "sum("+ PortfolioContract.FiiPortfolio.COLUMN_SOLD_TOTAL+")",
                "sum("+ PortfolioContract.FiiPortfolio.COLUMN_CURRENT_TOTAL+")",
                "sum("+ PortfolioContract.FiiPortfolio.COLUMN_VARIATION_TOTAL+")",
                "sum("+ PortfolioContract.FiiPortfolio.COLUMN_INCOME_TOTAL+")",
                "sum("+ PortfolioContract.FiiPortfolio.COLUMN_TOTAL_GAIN+")"};

        return mContext.getContentResolver().query(
                PortfolioContract.FiiPortfolio.URI,
                affectedColumn, null, null, null);
    }

    // Return useful value to update portfolio table
    public Cursor getCurrencyPortfolio(){
        String[] affectedColumn = {"sum("+ PortfolioContract.CurrencyPortfolio.COLUMN_BUY_TOTAL+")",
                "sum("+ PortfolioContract.CurrencyPortfolio.COLUMN_SOLD_TOTAL+")",
                "sum("+ PortfolioContract.CurrencyPortfolio.COLUMN_CURRENT_TOTAL+")",
                "sum("+ PortfolioContract.CurrencyPortfolio.COLUMN_VARIATION_TOTAL+")",
                "sum("+ PortfolioContract.CurrencyPortfolio.COLUMN_TOTAL_GAIN+")"};

        return mContext.getContentResolver().query(
                PortfolioContract.CurrencyPortfolio.URI,
                affectedColumn, null, null, null);
    }
}
