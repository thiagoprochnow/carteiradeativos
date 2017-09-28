package br.com.guiainvestimento.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

import java.security.Timestamp;
import java.util.Calendar;

import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;

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
        }

        // Others
        Cursor othersPortfolioCursor = getOthersPortfolio();

        double othersBuy = 0;
        double othersSold = 0;
        double othersCurrent = 0;
        double othersVariation = 0;
        double othersIncome = 0;
        double othersGain = 0;

        if (othersPortfolioCursor.getCount() > 0) {
            othersPortfolioCursor.moveToFirst();
            othersBuy = othersPortfolioCursor.getDouble(0);
            othersSold = othersPortfolioCursor.getDouble(1);
            othersCurrent = othersPortfolioCursor.getDouble(2);
            othersVariation = othersPortfolioCursor.getDouble(3);
            othersIncome = othersPortfolioCursor.getDouble(4);
            othersGain = othersPortfolioCursor.getDouble(5);
        } else {
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
        }

        // Sums all portfolio to get main portfolio
        double portfolioBuy = treasuryBuy + fixedBuy + stockBuy + fiiBuy + currencyBuy + othersBuy;
        double portfolioSold = treasurySold + fixedSold + stockSold + fiiSold + currencySold + othersSold;
        double portfolioCurrent = treasuryCurrent + fixedCurrent + stockCurrent + fiiCurrent + currencyCurrent + othersCurrent;
        double portfolioVariation = treasuryVariation + fixedVariation + stockVariation + fiiVariation + currencyVariation + othersVariation;
        double portfolioIncome = treasuryIncome + fixedIncome + stockIncome + fiiIncome + othersIncome;
        double portfolioGain =  treasuryGain + fixedGain + stockGain + fiiGain + currencyGain + othersGain;
        double treasuryPercent = treasuryCurrent/portfolioCurrent*100;
        double fixedPercent = fixedCurrent/portfolioCurrent*100;
        double stockPercent = stockCurrent/portfolioCurrent*100;
        double fiiPercent = fiiCurrent/portfolioCurrent*100;
        double currencyPercent = currencyCurrent/portfolioCurrent*100;
        double othersPercent = othersCurrent/portfolioCurrent*100;

        // Values to be inserted or updated on Portfolio table
        ContentValues portfolioCV = new ContentValues();
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_BUY_TOTAL, portfolioBuy);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_SOLD_TOTAL, portfolioSold);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_CURRENT_TOTAL, portfolioCurrent);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_VARIATION_TOTAL, portfolioVariation);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_INCOME_TOTAL, portfolioIncome);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_TOTAL_GAIN, portfolioGain);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_TREASURY_PERCENT, treasuryPercent);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_FIXED_PERCENT, fixedPercent);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_STOCK_PERCENT, stockPercent);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_FII_PERCENT, fiiPercent);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_CURRENCY_PERCENT, currencyPercent);
        portfolioCV.put(PortfolioContract.Portfolio.COLUMN_OTHERS_PERCENT, othersPercent);

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

        try {
            updatePortfolioGrowth(portfolioCurrent, treasuryCurrent, fixedCurrent, stockCurrent, fiiCurrent, currencyCurrent, othersCurrent);
            updateBuyGrowth(portfolioBuy, treasuryBuy, fixedBuy, stockBuy, fiiBuy, currencyBuy, othersBuy);
        } catch (SQLException e){
            Log.e(LOG_TAG, e.toString());
        }
    }

    // Function to update Portfolio, Stock, Fii... Growth values, this values will be used to make the Growth graph for each investment.
    // Only one value will be saved per month, so if the refresh was done in same month as already existing value, this new one will overwrite old value until a new month is done refresh.
    public void updatePortfolioGrowth(double portfolioCurrent, double treasuryCurrent, double fixedCurrent, double stockCurrent, double fiiCurrent, double currencyCurrent, double othersCurrent){
        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        // Portfolio Growth
        ContentValues portfolioCV = new ContentValues();
        portfolioCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TOTAL, portfolioCurrent);
        portfolioCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TYPE, Constants.ProductType.PORTFOLIO);
        portfolioCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP, timestamp);
        portfolioCV.put(PortfolioContract.PortfolioGrowth.MONTH, currentMonth);
        portfolioCV.put(PortfolioContract.PortfolioGrowth.YEAR, currentYear);

        String selection = PortfolioContract.PortfolioGrowth.MONTH + " = ? AND "
                + PortfolioContract.PortfolioGrowth.YEAR + " = ? AND "
                + PortfolioContract.PortfolioGrowth.COLUMN_TYPE + " = ?";

        String[] selectionArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.PORTFOLIO)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.PortfolioGrowth.URI, null,
                selection, selectionArguments, null);

        if(portfolioQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.PortfolioGrowth.URI,
                    portfolioCV, selection, selectionArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.PortfolioGrowth.URI,
                    portfolioCV);
        }

        // Treasury Growth
        ContentValues treasuryCV = new ContentValues();
        treasuryCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TOTAL, treasuryCurrent);
        treasuryCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TYPE, Constants.ProductType.TREASURY);
        treasuryCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP, timestamp);
        treasuryCV.put(PortfolioContract.PortfolioGrowth.MONTH, currentMonth);
        treasuryCV.put(PortfolioContract.PortfolioGrowth.YEAR, currentYear);

        String[] treasuryArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.TREASURY)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor treasuryQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.PortfolioGrowth.URI, null,
                selection, treasuryArguments, null);

        if(treasuryQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.PortfolioGrowth.URI,
                    treasuryCV, selection, treasuryArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.PortfolioGrowth.URI,
                    treasuryCV);
        }

        // Fixed Growth
        ContentValues fixedCV = new ContentValues();
        fixedCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TOTAL, fixedCurrent);
        fixedCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TYPE, Constants.ProductType.FIXED);
        fixedCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP, timestamp);
        fixedCV.put(PortfolioContract.PortfolioGrowth.MONTH, currentMonth);
        fixedCV.put(PortfolioContract.PortfolioGrowth.YEAR, currentYear);

        String[] fixedArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.FIXED)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor fixedQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.PortfolioGrowth.URI, null,
                selection, fixedArguments, null);

        if(fixedQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.PortfolioGrowth.URI,
                    fixedCV, selection, fixedArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.PortfolioGrowth.URI,
                    fixedCV);
        }

        // Stock Growth
        ContentValues stockCV = new ContentValues();
        stockCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TOTAL, stockCurrent);
        stockCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TYPE, Constants.ProductType.STOCK);
        stockCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP, timestamp);
        stockCV.put(PortfolioContract.PortfolioGrowth.MONTH, currentMonth);
        stockCV.put(PortfolioContract.PortfolioGrowth.YEAR, currentYear);

        String[] stockArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.STOCK)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor stockQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.PortfolioGrowth.URI, null,
                selection, stockArguments, null);

        if(stockQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.PortfolioGrowth.URI,
                    stockCV, selection, stockArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.PortfolioGrowth.URI,
                    stockCV);
        }

        // Fii Growth
        ContentValues fiiCV = new ContentValues();
        fiiCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TOTAL, fiiCurrent);
        fiiCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TYPE, Constants.ProductType.FII);
        fiiCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP, timestamp);
        fiiCV.put(PortfolioContract.PortfolioGrowth.MONTH, currentMonth);
        fiiCV.put(PortfolioContract.PortfolioGrowth.YEAR, currentYear);

        String[] fiiArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.FII)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor fiiQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.PortfolioGrowth.URI, null,
                selection, fiiArguments, null);

        if(fiiQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.PortfolioGrowth.URI,
                    fiiCV, selection, fiiArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.PortfolioGrowth.URI,
                    fiiCV);
        }

        // Currency Growth
        ContentValues currencyCV = new ContentValues();
        currencyCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TOTAL, currencyCurrent);
        currencyCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TYPE, Constants.ProductType.CURRENCY);
        currencyCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP, timestamp);
        currencyCV.put(PortfolioContract.PortfolioGrowth.MONTH, currentMonth);
        currencyCV.put(PortfolioContract.PortfolioGrowth.YEAR, currentYear);

        String[] currencyArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.CURRENCY)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor currencyQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.PortfolioGrowth.URI, null,
                selection, currencyArguments, null);

        if(currencyQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.PortfolioGrowth.URI,
                    currencyCV, selection, currencyArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.PortfolioGrowth.URI,
                    currencyCV);
        }

        // Others Growth
        ContentValues othersCV = new ContentValues();
        othersCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TOTAL, othersCurrent);
        othersCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TYPE, Constants.ProductType.OTHERS);
        othersCV.put(PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP, timestamp);
        othersCV.put(PortfolioContract.PortfolioGrowth.MONTH, currentMonth);
        othersCV.put(PortfolioContract.PortfolioGrowth.YEAR, currentYear);

        String[] othersArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.OTHERS)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor othersQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.PortfolioGrowth.URI, null,
                selection, othersArguments, null);

        if(othersQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.PortfolioGrowth.URI,
                    othersCV, selection, othersArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.PortfolioGrowth.URI,
                    othersCV);
        }
    }

    // Function to update Portfolio, Stock, Fii... Buy Growth values, this values will be used to make the Buy Growth graph for each investment.
    // Only one value will be saved per month, so if the refresh was done in same month as already existing value, this new one will overwrite old value until a new month is done refresh.
    public void updateBuyGrowth(double portfolioBuy, double treasuryBuy, double fixedBuy, double stockBuy, double fiiBuy, double currencyBuy, double othersBuy){
        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        // Portfolio Buy Growth
        ContentValues portfolioCV = new ContentValues();
        portfolioCV.put(PortfolioContract.BuyGrowth.COLUMN_TOTAL, portfolioBuy);
        portfolioCV.put(PortfolioContract.BuyGrowth.COLUMN_TYPE, Constants.ProductType.PORTFOLIO);
        portfolioCV.put(PortfolioContract.BuyGrowth.COLUMN_TIMESTAMP, timestamp);
        portfolioCV.put(PortfolioContract.BuyGrowth.MONTH, currentMonth);
        portfolioCV.put(PortfolioContract.BuyGrowth.YEAR, currentYear);

        String selection = PortfolioContract.BuyGrowth.MONTH + " = ? AND "
                + PortfolioContract.BuyGrowth.YEAR + " = ? AND "
                + PortfolioContract.BuyGrowth.COLUMN_TYPE + " = ?";

        String[] selectionArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.PORTFOLIO)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor portfolioQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.BuyGrowth.URI, null,
                selection, selectionArguments, null);

        if(portfolioQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.BuyGrowth.URI,
                    portfolioCV, selection, selectionArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.BuyGrowth.URI,
                    portfolioCV);
        }

        // Treasury Growth
        ContentValues treasuryCV = new ContentValues();
        treasuryCV.put(PortfolioContract.BuyGrowth.COLUMN_TOTAL, treasuryBuy);
        treasuryCV.put(PortfolioContract.BuyGrowth.COLUMN_TYPE, Constants.ProductType.TREASURY);
        treasuryCV.put(PortfolioContract.BuyGrowth.COLUMN_TIMESTAMP, timestamp);
        treasuryCV.put(PortfolioContract.BuyGrowth.MONTH, currentMonth);
        treasuryCV.put(PortfolioContract.BuyGrowth.YEAR, currentYear);

        String[] treasuryArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.TREASURY)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor treasuryQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.BuyGrowth.URI, null,
                selection, treasuryArguments, null);

        if(treasuryQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.BuyGrowth.URI,
                    treasuryCV, selection, treasuryArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.BuyGrowth.URI,
                    treasuryCV);
        }

        // Fixed Growth
        ContentValues fixedCV = new ContentValues();
        fixedCV.put(PortfolioContract.BuyGrowth.COLUMN_TOTAL, fixedBuy);
        fixedCV.put(PortfolioContract.BuyGrowth.COLUMN_TYPE, Constants.ProductType.FIXED);
        fixedCV.put(PortfolioContract.BuyGrowth.COLUMN_TIMESTAMP, timestamp);
        fixedCV.put(PortfolioContract.BuyGrowth.MONTH, currentMonth);
        fixedCV.put(PortfolioContract.BuyGrowth.YEAR, currentYear);

        String[] fixedArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.FIXED)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor fixedQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.BuyGrowth.URI, null,
                selection, fixedArguments, null);

        if(fixedQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.BuyGrowth.URI,
                    fixedCV, selection, fixedArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.BuyGrowth.URI,
                    fixedCV);
        }

        // Stock Growth
        ContentValues stockCV = new ContentValues();
        stockCV.put(PortfolioContract.BuyGrowth.COLUMN_TOTAL, stockBuy);
        stockCV.put(PortfolioContract.BuyGrowth.COLUMN_TYPE, Constants.ProductType.STOCK);
        stockCV.put(PortfolioContract.BuyGrowth.COLUMN_TIMESTAMP, timestamp);
        stockCV.put(PortfolioContract.BuyGrowth.MONTH, currentMonth);
        stockCV.put(PortfolioContract.BuyGrowth.YEAR, currentYear);

        String[] stockArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.STOCK)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor stockQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.BuyGrowth.URI, null,
                selection, stockArguments, null);

        if(stockQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.BuyGrowth.URI,
                    stockCV, selection, stockArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.BuyGrowth.URI,
                    stockCV);
        }

        // Fii Growth
        ContentValues fiiCV = new ContentValues();
        fiiCV.put(PortfolioContract.BuyGrowth.COLUMN_TOTAL, fiiBuy);
        fiiCV.put(PortfolioContract.BuyGrowth.COLUMN_TYPE, Constants.ProductType.FII);
        fiiCV.put(PortfolioContract.BuyGrowth.COLUMN_TIMESTAMP, timestamp);
        fiiCV.put(PortfolioContract.BuyGrowth.MONTH, currentMonth);
        fiiCV.put(PortfolioContract.BuyGrowth.YEAR, currentYear);

        String[] fiiArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.FII)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor fiiQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.BuyGrowth.URI, null,
                selection, fiiArguments, null);

        if(fiiQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.BuyGrowth.URI,
                    fiiCV, selection, fiiArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.BuyGrowth.URI,
                    fiiCV);
        }

        // Currency Growth
        ContentValues currencyCV = new ContentValues();
        currencyCV.put(PortfolioContract.BuyGrowth.COLUMN_TOTAL, currencyBuy);
        currencyCV.put(PortfolioContract.BuyGrowth.COLUMN_TYPE, Constants.ProductType.CURRENCY);
        currencyCV.put(PortfolioContract.BuyGrowth.COLUMN_TIMESTAMP, timestamp);
        currencyCV.put(PortfolioContract.BuyGrowth.MONTH, currentMonth);
        currencyCV.put(PortfolioContract.BuyGrowth.YEAR, currentYear);

        String[] currencyArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.CURRENCY)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor currencyQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.BuyGrowth.URI, null,
                selection, currencyArguments, null);

        if(currencyQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.BuyGrowth.URI,
                    currencyCV, selection, currencyArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.BuyGrowth.URI,
                    currencyCV);
        }

        // Others Growth
        ContentValues othersCV = new ContentValues();
        othersCV.put(PortfolioContract.BuyGrowth.COLUMN_TOTAL, othersBuy);
        othersCV.put(PortfolioContract.BuyGrowth.COLUMN_TYPE, Constants.ProductType.OTHERS);
        othersCV.put(PortfolioContract.BuyGrowth.COLUMN_TIMESTAMP, timestamp);
        othersCV.put(PortfolioContract.BuyGrowth.MONTH, currentMonth);
        othersCV.put(PortfolioContract.BuyGrowth.YEAR, currentYear);

        String[] othersArguments = {String.valueOf(currentMonth), String.valueOf(currentYear), String.valueOf(Constants.ProductType.OTHERS)};

        // Query for the only portfolio groth for this month and year, if dosent exist, creates one
        Cursor othersQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.BuyGrowth.URI, null,
                selection, othersArguments, null);

        if(othersQueryCursor.getCount() > 0){
            // Update
            mContext.getContentResolver().update(
                    PortfolioContract.BuyGrowth.URI,
                    othersCV, selection, othersArguments);
        } else {
            // Create and insert
            mContext.getContentResolver().insert(PortfolioContract.BuyGrowth.URI,
                    othersCV);
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
                "sum("+ PortfolioContract.FixedPortfolio.COLUMN_TOTAL_GAIN+")",
                "sum("+ PortfolioContract.FixedPortfolio.COLUMN_INCOME_TOTAL+")",
                "sum("+ PortfolioContract.FixedPortfolio.COLUMN_TOTAL_GAIN+")"};

        return mContext.getContentResolver().query(
                PortfolioContract.FixedPortfolio.URI,
                affectedColumn, null, null, null);
    }

    // Return useful value to update portfolio table
    public Cursor getOthersPortfolio(){
        String[] affectedColumn = {"sum("+ PortfolioContract.OthersPortfolio.COLUMN_BUY_TOTAL+")",
                "sum("+ PortfolioContract.OthersPortfolio.COLUMN_SOLD_TOTAL+")",
                "sum("+ PortfolioContract.OthersPortfolio.COLUMN_CURRENT_TOTAL+")",
                "sum("+ PortfolioContract.OthersPortfolio.COLUMN_VARIATION_TOTAL+")",
                "sum("+ PortfolioContract.OthersPortfolio.COLUMN_INCOME_TOTAL+")",
                "sum("+ PortfolioContract.OthersPortfolio.COLUMN_TOTAL_GAIN+")"};

        return mContext.getContentResolver().query(
                PortfolioContract.OthersPortfolio.URI,
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
