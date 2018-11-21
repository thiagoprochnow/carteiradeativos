package br.com.guiainvestimento.fragment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.api.service.FixedIntentService;
import br.com.guiainvestimento.api.service.CryptoIntentService;
import br.com.guiainvestimento.api.service.CurrencyIntentService;
import br.com.guiainvestimento.api.service.FiiIntentService;
import br.com.guiainvestimento.api.service.StockIntentService;
import br.com.guiainvestimento.api.service.TreasuryIntentService;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.domain.Currency;
import br.com.guiainvestimento.receiver.CurrencyReceiver;
import br.com.guiainvestimento.receiver.FiiReceiver;
import br.com.guiainvestimento.receiver.FixedReceiver;
import br.com.guiainvestimento.receiver.FundReceiver;
import br.com.guiainvestimento.receiver.OthersReceiver;
import br.com.guiainvestimento.receiver.PortfolioReceiver;
import br.com.guiainvestimento.receiver.StockReceiver;
import br.com.guiainvestimento.receiver.TreasuryReceiver;
import br.com.guiainvestimento.util.Util;

public abstract class BaseFragment extends Fragment {

    protected Context mContext;

    private static final String LOG_TAG = BaseFragment.class.getSimpleName();

    private Menu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enables the menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    // Delete stock and all its information from database
    // This is different then selling a stock, that will maintain some information
    public boolean deleteStock(String symbol) {
        int deletedTransaction = getActivity().getContentResolver().delete(PortfolioContract
                .StockTransaction
                .makeUriForStockTransaction(symbol), null, null);
        int deletedData = getActivity().getContentResolver().delete(PortfolioContract.StockData
                .makeUriForStockData(symbol), null, null);

        int deletedSoldData = getActivity().getContentResolver().delete(PortfolioContract.SoldStockData
                .makeUriForSoldStockData(symbol), null, null);
        // Cannot check if deletedIncome > 0, because stock may not have any income to delete
        // Which is not an error
        int deletedIncome = getActivity().getContentResolver().delete(PortfolioContract.StockIncome
                .makeUriForStockIncome(symbol), null, null);
        if (deletedData > 0) {
            StockReceiver stockReceiver = new StockReceiver(mContext);
            stockReceiver.updateStockPortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.STOCK));

            PortfolioReceiver portfolioReceiver = new PortfolioReceiver(mContext);
            portfolioReceiver.updatePortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
            Toast.makeText(mContext, getString(R.string.toast_stock_successfully_removed, symbol)
                    , Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.toast_stock_not_removed, symbol), Toast
                    .LENGTH_SHORT).show();
            return false;
        }
    }

    // Delete stock income from table by using its id
    // symbol is used to update Stock Data table
    public boolean deleteStockIncome(String id, String symbol){
        String selection = PortfolioContract.StockIncome._ID + " = ? AND "
                + PortfolioContract.StockIncome.COLUMN_SYMBOL + " = ?";
        if (symbol == null){
            String selectionData = PortfolioContract.StockIncome._ID + " = ? ";
            String[] selectionDataArguments = {id};
            String[] affectedColumn = {PortfolioContract.StockIncome.COLUMN_SYMBOL};
            Cursor cursor = mContext.getContentResolver().query(
                    PortfolioContract.StockIncome.URI,
                    affectedColumn, selectionData, selectionDataArguments, null);

            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                symbol = cursor.getString(0);
            } else {
            }
        }
        String[] selectionArguments = {id, symbol};

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.StockIncome.URI,
                selection, selectionArguments);
        if (deletedResult > 0){
            // Update stock data for that symbol
            boolean updateStockData = updateStockData(symbol, -1);
            if (updateStockData)
                return true;
        }
        return false;
    }

    // Delete stock transaction from table by using its id
    // symbol is used to update Stock Data table
    public boolean deleteStockTransaction(String id, String symbol){
        long timestamp;
        String[] affectedColumn = {PortfolioContract.StockTransaction.COLUMN_TIMESTAMP};
        String selection = PortfolioContract.StockTransaction._ID + " = ? AND "
                + PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {id, symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI, affectedColumn,
                selection, selectionArguments, null);

        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            timestamp = queryCursor.getLong(0);
        } else {
            return false;
        }

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.StockTransaction.URI,
                selection, selectionArguments);

        if (deletedResult > 0){
            // Update stock data and stock income for that symbol
            updateStockIncomes(symbol, timestamp);
            updateStockData(symbol, Constants.Type.DELETE_TRANSACION);
        }

        // Check if there is any more transaction for this symbol
        // If not, delete this symbol from StockData

        String selectionTransaction = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.StockTransaction.COLUMN_TYPE + " = ?";
        String[] selectionArgumentsTransaction = {symbol, String.valueOf(Constants.Type.BUY)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI, null,
                selectionTransaction, selectionArgumentsTransaction, null);

        // If there is no more buy transaction for this symbol, delete the stock and finish activity
        if (queryCursor.getCount() == 0){
            deleteStock(symbol);
            getActivity().finish();
        }

        // Check if there is any more SELL transaction for this symbol
        // If not, delete this symbol from SoldStockData

        String sellSelectionTransaction = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.StockTransaction.COLUMN_TYPE + " = ?";
        String[] sellArgumentsTransaction = {symbol, String.valueOf(Constants.Type.SELL)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI, null,
                sellSelectionTransaction, sellArgumentsTransaction, null);

        // No more Sell transactions, delete SoldStockData
        if (queryCursor.getCount() == 0){
            String selectionSoldData = PortfolioContract.SoldStockData.COLUMN_SYMBOL + " = ?";
            String[] selectionArgumentsSoldData = {symbol};
            int rowsDeleted = mContext.getContentResolver().delete(
                    PortfolioContract.SoldStockData.URI, selectionSoldData,
                    selectionArgumentsSoldData);
            if (rowsDeleted == 1){
            } else {
            }
        }

        return false;
    }

    // Delete fii and all its information from database
    // This is different then selling a fii, that will maintain some information
    public boolean deleteFii(String symbol) {
        int deletedTransaction = getActivity().getContentResolver().delete(PortfolioContract
                .FiiTransaction
                .makeUriForFiiTransaction(symbol), null, null);
        int deletedData = getActivity().getContentResolver().delete(PortfolioContract.FiiData
                .makeUriForFiiData(symbol), null, null);

        int deletedSoldData = getActivity().getContentResolver().delete(PortfolioContract.SoldFiiData
                .makeUriForSoldFiiData(symbol), null, null);
        // Cannot check if deletedIncome > 0, because fii may not have any income to delete
        // Which is not an error
        int deletedIncome = getActivity().getContentResolver().delete(PortfolioContract.FiiIncome
                .makeUriForFiiIncome(symbol), null, null);
        if (deletedData > 0) {
            FiiReceiver fiiReceiver = new FiiReceiver(mContext);
            fiiReceiver.updateFiiPortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.FII));

            PortfolioReceiver portfolioReceiver = new PortfolioReceiver(mContext);
            portfolioReceiver.updatePortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
            Toast.makeText(mContext, getString(R.string.toast_fii_successfully_removed, symbol)
                    , Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.toast_fii_not_removed, symbol), Toast
                    .LENGTH_SHORT).show();
            return false;
        }
    }

    // Delete Currency and all its information from database
    // This is different then selling a Currency, that will maintain some information
    public boolean deleteCurrency(String symbol) {
        int deletedTransaction = getActivity().getContentResolver().delete(PortfolioContract
                .CurrencyTransaction
                .makeUriForCurrencyTransaction(symbol), null, null);
        int deletedData = getActivity().getContentResolver().delete(PortfolioContract.CurrencyData
                .makeUriForCurrencyData(symbol), null, null);

        int deletedSoldData = getActivity().getContentResolver().delete(PortfolioContract.SoldCurrencyData
                .makeUriForSoldCurrencyData(symbol), null, null);
        String label = Util.convertCurrencySymbol(mContext, symbol);
        if (deletedData > 0) {
            CurrencyReceiver currencyReceiver = new CurrencyReceiver(mContext);
            currencyReceiver.updateCurrencyPortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.CURRENCY));

            PortfolioReceiver portfolioReceiver = new PortfolioReceiver(mContext);
            portfolioReceiver.updatePortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
            Toast.makeText(mContext, getString(R.string.toast_currency_successfully_removed, label)
                    , Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.toast_currency_not_removed, label), Toast
                    .LENGTH_SHORT).show();
            return false;
        }
    }

    // Delete fixed income and all its information from database
    // This is different then selling a fixed income, that will maintain some information
    public boolean deleteFixed(String symbol) {
        int deletedTransaction = getActivity().getContentResolver().delete(PortfolioContract
                .FixedTransaction
                .makeUriForFixedTransaction(symbol), null, null);
        int deletedData = getActivity().getContentResolver().delete(PortfolioContract.FixedData
                .makeUriForFixedData(symbol), null, null);

        if (deletedData > 0) {
            FixedReceiver fixedReceiver = new FixedReceiver(mContext);
            fixedReceiver.updateFixedPortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.FIXED));

            PortfolioReceiver portfolioReceiver = new PortfolioReceiver(mContext);
            portfolioReceiver.updatePortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
            Toast.makeText(mContext, getString(R.string.toast_fixed_successfully_removed, symbol)
                    , Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.toast_fixed_not_removed, symbol), Toast
                    .LENGTH_SHORT).show();
            return false;
        }
    }

    // Delete fund income and all its information from database
    // This is different then selling a fund income, that will maintain some information
    public boolean deleteFund(String symbol) {
        int deletedTransaction = getActivity().getContentResolver().delete(PortfolioContract
                .FundTransaction
                .makeUriForFundTransaction(symbol), null, null);
        int deletedData = getActivity().getContentResolver().delete(PortfolioContract.FundData
                .makeUriForFundData(symbol), null, null);

        if (deletedData > 0) {
            FundReceiver fundReceiver = new FundReceiver(mContext);
            fundReceiver.updateFundPortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.FUND));

            PortfolioReceiver portfolioReceiver = new PortfolioReceiver(mContext);
            portfolioReceiver.updatePortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
            Toast.makeText(mContext, getString(R.string.toast_fund_successfully_removed, symbol)
                    , Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.toast_fund_not_removed, symbol), Toast
                    .LENGTH_SHORT).show();
            return false;
        }
    }

    // Delete treasury and all its information from database
    // This is different then selling a treasury, that will maintain some information
    public boolean deleteTreasury(String symbol) {
        int deletedTransaction = getActivity().getContentResolver().delete(PortfolioContract
                .TreasuryTransaction
                .makeUriForTreasuryTransaction(symbol), null, null);
        int deletedData = getActivity().getContentResolver().delete(PortfolioContract.TreasuryData
                .makeUriForTreasuryData(symbol), null, null);

        int deletedSoldData = getActivity().getContentResolver().delete(PortfolioContract.SoldTreasuryData
                .makeUriForSoldTreasuryData(symbol), null, null);
        // Cannot check if deletedIncome > 0, because treasury may not have any income to delete
        // Which is not an error
        int deletedIncome = getActivity().getContentResolver().delete(PortfolioContract.TreasuryIncome
                .makeUriForTreasuryIncome(symbol), null, null);
        if (deletedData > 0) {
            TreasuryReceiver treasuryReceiver = new TreasuryReceiver(mContext);
            treasuryReceiver.updateTreasuryPortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.TREASURY));

            PortfolioReceiver portfolioReceiver = new PortfolioReceiver(mContext);
            portfolioReceiver.updatePortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
            Toast.makeText(mContext, getString(R.string.toast_treasury_successfully_removed, symbol)
                    , Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.toast_treasury_not_removed, symbol), Toast
                    .LENGTH_SHORT).show();
            return false;
        }
    }

    // Delete others income and all its information from database
    // This is different then selling a others income, that will maintain some information
    public boolean deleteOthers(String symbol) {
        int deletedTransaction = getActivity().getContentResolver().delete(PortfolioContract
                .OthersTransaction
                .makeUriForOthersTransaction(symbol), null, null);
        int deletedData = getActivity().getContentResolver().delete(PortfolioContract.OthersData
                .makeUriForOthersData(symbol), null, null);

        if (deletedData > 0) {
            OthersReceiver othersReceiver = new OthersReceiver(mContext);
            othersReceiver.updateOthersPortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.OTHERS));

            PortfolioReceiver portfolioReceiver = new PortfolioReceiver(mContext);
            portfolioReceiver.updatePortfolio();
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
            Toast.makeText(mContext, getString(R.string.toast_others_successfully_removed, symbol)
                    , Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.toast_others_not_removed, symbol), Toast
                    .LENGTH_SHORT).show();
            return false;
        }
    }

    // Delete fii income from table by using its id
    // symbol is used to update Fii Data table
    public boolean deleteFiiIncome(String id, String symbol){
        String selection = PortfolioContract.FiiIncome._ID + " = ? AND "
                + PortfolioContract.FiiIncome.COLUMN_SYMBOL + " = ?";
        if (symbol == null){
            String selectionData = PortfolioContract.FiiIncome._ID + " = ? ";
            String[] selectionDataArguments = {id};
            String[] affectedColumn = {PortfolioContract.FiiIncome.COLUMN_SYMBOL};
            Cursor cursor = mContext.getContentResolver().query(
                    PortfolioContract.FiiIncome.URI,
                    affectedColumn, selectionData, selectionDataArguments, null);

            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                symbol = cursor.getString(0);
            } else {
            }
        }
        String[] selectionArguments = {id, symbol};

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.FiiIncome.URI,
                selection, selectionArguments);
        if (deletedResult > 0){
            // Update fii data for that symbol
            boolean updateFiiData = updateFiiData(symbol, -1);
            if (updateFiiData)
                return true;
        }
        return false;
    }

    // Delete treasury income from table by using its id
    // symbol is used to update Treasury Data table
    public boolean deleteTreasuryIncome(String id, String symbol){
        String selection = PortfolioContract.TreasuryIncome._ID + " = ? AND "
                + PortfolioContract.TreasuryIncome.COLUMN_SYMBOL + " = ?";
        if (symbol == null){
            String selectionData = PortfolioContract.TreasuryIncome._ID + " = ? ";
            String[] selectionDataArguments = {id};
            String[] affectedColumn = {PortfolioContract.TreasuryIncome.COLUMN_SYMBOL};
            Cursor cursor = mContext.getContentResolver().query(
                    PortfolioContract.TreasuryIncome.URI,
                    affectedColumn, selectionData, selectionDataArguments, null);

            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                symbol = cursor.getString(0);
            } else {
            }
        }
        String[] selectionArguments = {id, symbol};

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.TreasuryIncome.URI,
                selection, selectionArguments);
        if (deletedResult > 0){
            // Update treasury data for that symbol
            boolean updateTreasuryData = updateTreasuryData(symbol, -1);
            if (updateTreasuryData)
                return true;
        }
        return false;
    }

    // Delete others income from table by using its id
    // symbol is used to update Treasury Data table
    public boolean deleteOthersIncome(String id, String symbol){
        String selection = PortfolioContract.OthersIncome._ID + " = ? AND "
                + PortfolioContract.OthersIncome.COLUMN_SYMBOL + " = ?";
        if (symbol == null){
            String selectionData = PortfolioContract.OthersIncome._ID + " = ? ";
            String[] selectionDataArguments = {id};
            String[] affectedColumn = {PortfolioContract.OthersIncome.COLUMN_SYMBOL};
            Cursor cursor = mContext.getContentResolver().query(
                    PortfolioContract.OthersIncome.URI,
                    affectedColumn, selectionData, selectionDataArguments, null);

            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                symbol = cursor.getString(0);
            } else {
            }
        }
        String[] selectionArguments = {id, symbol};

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.OthersIncome.URI,
                selection, selectionArguments);
        if (deletedResult > 0){
            // Update others data for that symbol
            boolean updateOthersData = updateOthersData(symbol, -1);
            if (updateOthersData)
                return true;
        }
        return false;
    }

    // Delete fii transaction from table by using its id
    // symbol is used to update Fii Data table
    public boolean deleteFiiTransaction(String id, String symbol){
        long timestamp;
        String[] affectedColumn = {PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP};
        String selection = PortfolioContract.FiiTransaction._ID + " = ? AND "
                + PortfolioContract.FiiTransaction.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {id, symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FiiTransaction.URI, affectedColumn,
                selection, selectionArguments, null);

        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            timestamp = queryCursor.getLong(0);
        } else {
            return false;
        }

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.FiiTransaction.URI,
                selection, selectionArguments);

        if (deletedResult > 0){
            // Update fii data and fii income for that symbol
            updateFiiIncomes(symbol, timestamp);
            updateFiiData(symbol, Constants.Type.DELETE_TRANSACION);
        }

        // Check if there is any more transaction for this symbol
        // If not, delete this symbol from FiiData

        String selectionTransaction = PortfolioContract.FiiTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.FiiTransaction.COLUMN_TYPE + " = ?";
        String[] selectionArgumentsTransaction = {symbol, String.valueOf(Constants.Type.BUY)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FiiTransaction.URI, null,
                selectionTransaction, selectionArgumentsTransaction, null);

        // If there is no more buy transaction for this symbol, delete the fii and finish activity
        if (queryCursor.getCount() == 0){
            deleteFii(symbol);
            getActivity().finish();
        }

        // Check if there is any more SELL transaction for this symbol
        // If not, delete this symbol from SoldFiiData

        String sellSelectionTransaction = PortfolioContract.FiiTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.FiiTransaction.COLUMN_TYPE + " = ?";
        String[] sellArgumentsTransaction = {symbol, String.valueOf(Constants.Type.SELL)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FiiTransaction.URI, null,
                sellSelectionTransaction, sellArgumentsTransaction, null);

        // No more Sell transactions, delete SoldFiiData
        if (queryCursor.getCount() == 0){
            String selectionSoldData = PortfolioContract.SoldFiiData.COLUMN_SYMBOL + " = ?";
            String[] selectionArgumentsSoldData = {symbol};
            int rowsDeleted = mContext.getContentResolver().delete(
                    PortfolioContract.SoldFiiData.URI, selectionSoldData,
                    selectionArgumentsSoldData);
            if (rowsDeleted == 1){
            } else {
            }
        }

        return false;
    }

    // Delete Currency transaction from table by using its id
    // symbol is used to update Currency Data table
    public boolean deleteCurrencyTransaction(String id, String symbol){
        long timestamp;
        String[] affectedColumn = {PortfolioContract.CurrencyTransaction.COLUMN_TIMESTAMP};
        String selection = PortfolioContract.CurrencyTransaction._ID + " = ? AND "
                + PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {id, symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.CurrencyTransaction.URI, affectedColumn,
                selection, selectionArguments, null);

        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            timestamp = queryCursor.getLong(0);
        } else {
            return false;
        }

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.CurrencyTransaction.URI,
                selection, selectionArguments);

        if (deletedResult > 0){
            // Update Currency data and Currency income for that symbol
            updateCurrencyData(symbol, Constants.Type.DELETE_TRANSACION);
        }

        // Check if there is any more transaction for this symbol
        // If not, delete this symbol from CurrencyData

        String selectionTransaction = PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.CurrencyTransaction.COLUMN_TYPE + " = ?";
        String[] selectionArgumentsTransaction = {symbol, String.valueOf(Constants.Type.BUY)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.CurrencyTransaction.URI, null,
                selectionTransaction, selectionArgumentsTransaction, null);

        // If there is no more buy transaction for this symbol, delete the Currency and finish activity
        if (queryCursor.getCount() == 0){
            deleteCurrency(symbol);
            getActivity().finish();
        }

        // Check if there is any more SELL transaction for this symbol
        // If not, delete this symbol from SoldCurrencyData

        String sellSelectionTransaction = PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.CurrencyTransaction.COLUMN_TYPE + " = ?";
        String[] sellArgumentsTransaction = {symbol, String.valueOf(Constants.Type.SELL)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.CurrencyTransaction.URI, null,
                sellSelectionTransaction, sellArgumentsTransaction, null);

        // No more Sell transactions, delete SoldCurrencyData
        if (queryCursor.getCount() == 0){
            String selectionSoldData = PortfolioContract.SoldCurrencyData.COLUMN_SYMBOL + " = ?";
            String[] selectionArgumentsSoldData = {symbol};
            int rowsDeleted = mContext.getContentResolver().delete(
                    PortfolioContract.SoldCurrencyData.URI, selectionSoldData,
                    selectionArgumentsSoldData);
            if (rowsDeleted == 1){
            } else {
            }
        }

        return false;
    }

    // Delete fixed transaction from table by using its id
    // symbol is used to update fixed Data table
    public boolean deleteFixedTransaction(String id, String symbol){
        long timestamp;
        String[] affectedColumn = {PortfolioContract.FixedTransaction.COLUMN_TIMESTAMP};
        String selection = PortfolioContract.FixedTransaction._ID + " = ? AND "
                + PortfolioContract.FixedTransaction.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {id, symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FixedTransaction.URI, affectedColumn,
                selection, selectionArguments, null);

        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            timestamp = queryCursor.getLong(0);
        } else {
            return false;
        }

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.FixedTransaction.URI,
                selection, selectionArguments);

        if (deletedResult > 0){
            // Update fixed data for that symbol
            updateFixedData(symbol, Constants.Type.DELETE_TRANSACION);
        }

        // Check if there is any more transaction for this symbol
        // If not, delete this symbol from FixedData

        String selectionTransaction = PortfolioContract.FixedTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.FixedTransaction.COLUMN_TYPE + " = ?";
        String[] selectionArgumentsTransaction = {symbol, String.valueOf(Constants.Type.BUY)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FixedTransaction.URI, null,
                selectionTransaction, selectionArgumentsTransaction, null);

        // If there is no more buy transaction for this symbol, delete the fixed income and finish activity
        if (queryCursor.getCount() == 0){
            deleteFixed(symbol);
            getActivity().finish();
        }

        String sellSelectionTransaction = PortfolioContract.FixedTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.FixedTransaction.COLUMN_TYPE + " = ?";
        String[] sellArgumentsTransaction = {symbol, String.valueOf(Constants.Type.SELL)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FixedTransaction.URI, null,
                sellSelectionTransaction, sellArgumentsTransaction, null);

        return false;
    }

    // Delete fund transaction from table by using its id
    // symbol is used to update fund Data table
    public boolean deleteFundTransaction(String id, String symbol){
        long timestamp;
        String[] affectedColumn = {PortfolioContract.FundTransaction.COLUMN_TIMESTAMP};
        String selection = PortfolioContract.FundTransaction._ID + " = ? AND "
                + PortfolioContract.FundTransaction.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {id, symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FundTransaction.URI, affectedColumn,
                selection, selectionArguments, null);

        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            timestamp = queryCursor.getLong(0);
        } else {
            return false;
        }

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.FundTransaction.URI,
                selection, selectionArguments);

        if (deletedResult > 0){
            // Update fund data for that symbol
            updateFundData(symbol, Constants.Type.DELETE_TRANSACION);
        }

        // Check if there is any more transaction for this symbol
        // If not, delete this symbol from FundData

        String selectionTransaction = PortfolioContract.FundTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.FundTransaction.COLUMN_TYPE + " = ?";
        String[] selectionArgumentsTransaction = {symbol, String.valueOf(Constants.Type.BUY)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FundTransaction.URI, null,
                selectionTransaction, selectionArgumentsTransaction, null);

        // If there is no more buy transaction for this symbol, delete the fund income and finish activity
        if (queryCursor.getCount() == 0){
            deleteFund(symbol);
            getActivity().finish();
        }

        String sellSelectionTransaction = PortfolioContract.FundTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.FundTransaction.COLUMN_TYPE + " = ?";
        String[] sellArgumentsTransaction = {symbol, String.valueOf(Constants.Type.SELL)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FundTransaction.URI, null,
                sellSelectionTransaction, sellArgumentsTransaction, null);

        return false;
    }

    // Delete treasury transaction from table by using its id
    // symbol is used to update Treasury Data table
    public boolean deleteTreasuryTransaction(String id, String symbol){
        long timestamp;
        String[] affectedColumn = {PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP};
        String selection = PortfolioContract.TreasuryTransaction._ID + " = ? AND "
                + PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {id, symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.TreasuryTransaction.URI, affectedColumn,
                selection, selectionArguments, null);

        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            timestamp = queryCursor.getLong(0);
        } else {
            return false;
        }

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.TreasuryTransaction.URI,
                selection, selectionArguments);

        if (deletedResult > 0){
            // Update treasury data and treasury income for that symbol
            updateTreasuryIncomes(symbol, timestamp);
            updateTreasuryData(symbol, Constants.Type.DELETE_TRANSACION);
        }

        // Check if there is any more transaction for this symbol
        // If not, delete this symbol from TreasuryData

        String selectionTransaction = PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.TreasuryTransaction.COLUMN_TYPE + " = ?";
        String[] selectionArgumentsTransaction = {symbol, String.valueOf(Constants.Type.BUY)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.TreasuryTransaction.URI, null,
                selectionTransaction, selectionArgumentsTransaction, null);

        // If there is no more buy transaction for this symbol, delete the treasury and finish activity
        if (queryCursor.getCount() == 0){
            deleteTreasury(symbol);
            getActivity().finish();
        }

        // Check if there is any more SELL transaction for this symbol
        // If not, delete this symbol from SoldTreasuryData

        String sellSelectionTransaction = PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.TreasuryTransaction.COLUMN_TYPE + " = ?";
        String[] sellArgumentsTransaction = {symbol, String.valueOf(Constants.Type.SELL)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.TreasuryTransaction.URI, null,
                sellSelectionTransaction, sellArgumentsTransaction, null);

        // No more Sell transactions, delete SoldTreasuryData
        if (queryCursor.getCount() == 0){
            String selectionSoldData = PortfolioContract.SoldTreasuryData.COLUMN_SYMBOL + " = ?";
            String[] selectionArgumentsSoldData = {symbol};
            int rowsDeleted = mContext.getContentResolver().delete(
                    PortfolioContract.SoldTreasuryData.URI, selectionSoldData,
                    selectionArgumentsSoldData);
            if (rowsDeleted == 1){
            } else {
            }
        }

        return false;
    }

    // Delete others transaction from table by using its id
    // symbol is used to update others Data table
    public boolean deleteOthersTransaction(String id, String symbol){
        long timestamp;
        String[] affectedColumn = {PortfolioContract.OthersTransaction.COLUMN_TIMESTAMP};
        String selection = PortfolioContract.OthersTransaction._ID + " = ? AND "
                + PortfolioContract.OthersTransaction.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {id, symbol};

        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.OthersTransaction.URI, affectedColumn,
                selection, selectionArguments, null);

        if (queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            timestamp = queryCursor.getLong(0);
        } else {
            return false;
        }

        int deletedResult = mContext.getContentResolver().delete(
                PortfolioContract.OthersTransaction.URI,
                selection, selectionArguments);

        if (deletedResult > 0){
            // Update others data for that symbol
            updateOthersData(symbol, Constants.Type.DELETE_TRANSACION);
        }

        // Check if there is any more transaction for this symbol
        // If not, delete this symbol from OthersData

        String selectionTransaction = PortfolioContract.OthersTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.OthersTransaction.COLUMN_TYPE + " = ?";
        String[] selectionArgumentsTransaction = {symbol, String.valueOf(Constants.Type.BUY)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.OthersTransaction.URI, null,
                selectionTransaction, selectionArgumentsTransaction, null);

        // If there is no more buy transaction for this symbol, delete the others income and finish activity
        if (queryCursor.getCount() == 0){
            deleteOthers(symbol);
            getActivity().finish();
        }

        String sellSelectionTransaction = PortfolioContract.OthersTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.OthersTransaction.COLUMN_TYPE + " = ?";
        String[] sellArgumentsTransaction = {symbol, String.valueOf(Constants.Type.SELL)};

        queryCursor = mContext.getContentResolver().query(
                PortfolioContract.OthersTransaction.URI, null,
                sellSelectionTransaction, sellArgumentsTransaction, null);

        return false;
    }

    // Transform a date value of dd/MM/yyyy into a timestamp value
    public Long DateToTimestamp(String inputDate){
        DateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        Date date = new Date();
        try{
            date = (Date) dateFormat.parse(inputDate);
        } catch (ParseException e){
            e.printStackTrace();
        }

        return date.getTime();
    }

    // Transforms a timestamp into a string Date
    public String TimestampToDate(long timestamp){
        String date = android.text.format.DateFormat.format("dd/MM/yyyy", timestamp).toString();
        return date;
    }


    // Validate if an EditText field is empty
    protected boolean isEditTextEmpty(EditText text) {
        Editable editable = text.getText();
        if (editable != null && TextUtils.isEmpty(editable.toString())) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText field is empty
    protected boolean isAutoTextEmpty(AutoCompleteTextView text) {
        Editable editable = text.getText();
        if (editable != null && TextUtils.isEmpty(editable.toString())) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid Stock Symbol
    protected boolean isValidStockSymbol(AutoCompleteTextView symbol) {
        Editable editable = symbol.getText();
        // Regex Pattern for Stock (EX: PETR4) or ETF (EX: BOVA11 or SMAL11)
        Pattern pattern = Pattern.compile("^[A-Z0-9]{4}([0-9]|[0-9][0-9])$");
        if (!isAutoTextEmpty(symbol) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid Fii Symbol
    protected boolean isValidFiiSymbol(AutoCompleteTextView symbol) {
        Editable editable = symbol.getText();
        // Regex Pattern for Fii (EX: KNRI11)
        Pattern pattern = Pattern.compile("^[A-Z]{4}([0-9][0-9][A-Z]|[0-9][0-9])$");
        if (!isAutoTextEmpty(symbol) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid Fii Symbol
    protected boolean isValidCurrencySymbol(EditText symbol) {
        Editable editable = symbol.getText();
        // Regex Pattern for Fii (EX: EURO)
        // Pattern pattern = Pattern.compile("^[A-Z]");
       // if (!isEditTextEmpty(symbol) && pattern.matcher(editable.toString()).matches()) {
        if (!isEditTextEmpty(symbol)) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid Stock Symbol
    protected boolean isValidSymbol(AutoCompleteTextView symbol) {
        Editable editable = symbol.getText();
        // Regex Pattern for Stock (EX: PETR4) or ETF (EX: BOVA11 or SMAL11)
        Pattern patternStock = Pattern.compile("^[A-Z]{4}([0-9]|[0-9][0-9])$");
        Pattern patternFii = Pattern.compile("^[A-Z]{4}([0-9][0-9][A-Z]|[0-9][0-9])$");
        Pattern patternCurrency = Pattern.compile("[a-zA-Z]*");
        if (patternStock.matcher(editable.toString()).matches() || patternFii.matcher(editable.toString()).matches() || patternCurrency.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid Fixed Symbol
    protected boolean isValidFixedSymbol(EditText symbol) {
        Editable editable = symbol.getText();
        // Regex Pattern for Fixed income (Only letters and numbers)
        Pattern pattern = Pattern.compile("[a-zA-Z\\s0-9]*");
        if (!isEditTextEmpty(symbol) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid Fund Symbol
    protected boolean isValidFundSymbol(EditText symbol) {
        Editable editable = symbol.getText();
        // Regex Pattern for Fund income (Only letters and numbers)
        Pattern pattern = Pattern.compile("[a-zA-Z\\s0-9]*");
        if (!isEditTextEmpty(symbol) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid Others Symbol
    protected boolean isValidOthersSymbol(EditText symbol) {
        Editable editable = symbol.getText();
        // Regex Pattern for Others income (Only letters and numbers)
        Pattern pattern = Pattern.compile("[a-zA-Z\\s0-9]*");
        if (!isEditTextEmpty(symbol) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid Treasury Symbol
    protected boolean isValidTreasurySymbol(AutoCompleteTextView symbol) {
        Editable editable = symbol.getText();
        // Regex Pattern for Treasury income (Only letters and numbers)
        Pattern pattern = Pattern.compile("[a-zA-Z\\s0-9]*");
        if (!isAutoTextEmpty(symbol) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid int and that there is enough quantity of that investment to sell
    protected boolean isValidSellQuantity(EditText editQuantity, String editSymbol, int type) {
        // TODO
        Editable editableQuantity = editQuantity.getText();
        double quantity = 0;
        int quantity2 = 0;
        boolean isDigitOnly = false;
        if (type == Constants.ProductType.CURRENCY || type == Constants.ProductType.TREASURY){
            quantity = 0;
            // Check if it is digit only
            isDigitOnly = true;
            if (!editableQuantity.toString().isEmpty()) {
                quantity = Double.parseDouble(editableQuantity.toString());
            }
        } else {
            quantity2 = 0;
            // Check if it is digit only
            isDigitOnly = TextUtils.isDigitsOnly(editableQuantity.toString());
            if (!editableQuantity.toString().isEmpty()) {
                quantity2 = Integer.parseInt(editableQuantity.toString());
            }
        }
        String symbol = editSymbol;

        boolean isQuantityEnough = false;

        if (type == Constants.ProductType.STOCK) {
            // Prepare query for stock data
            String selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ? ";
            String[] selectionArguments = {symbol};

            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockData.URI,
                    null, selection, selectionArguments, null);
            // Gets data quantity to see if bought quantity is enough
            if (queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();
                int boughtQuantity = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract
                        .StockData.COLUMN_QUANTITY_TOTAL));
                if (boughtQuantity >= quantity2) {
                    // Bought quantity is bigger then quantity trying to sell
                    isQuantityEnough = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (type == Constants.ProductType.FII){
            // Prepare query for fii data
            String selection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ? ";
            String[] selectionArguments = {symbol};

            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.FiiData.URI,
                    null, selection, selectionArguments, null);
            // Gets data quantity to see if bought quantity is enough
            if (queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();
                int boughtQuantity = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract
                        .FiiData.COLUMN_QUANTITY_TOTAL));
                if (boughtQuantity >= quantity2) {
                    // Bought quantity is bigger then quantity trying to sell
                    isQuantityEnough = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (type == Constants.ProductType.CURRENCY){
            // Prepare query for currency data
            String selection = PortfolioContract.CurrencyData.COLUMN_SYMBOL + " = ? ";
            String[] selectionArguments = {symbol};

            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.CurrencyData.URI,
                    null, selection, selectionArguments, null);
            // Gets data quantity to see if bought quantity is enough
            if (queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();
                double boughtQuantity = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract
                        .CurrencyData.COLUMN_QUANTITY_TOTAL));
                if (boughtQuantity >= quantity) {
                    // Bought quantity is bigger then quantity trying to sell
                    isQuantityEnough = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (type == Constants.ProductType.TREASURY){
            // Prepare query for treasury data
            String selection = PortfolioContract.TreasuryData.COLUMN_SYMBOL + " = ? ";
            String[] selectionArguments = {symbol};

            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.TreasuryData.URI,
                    null, selection, selectionArguments, null);
            // Gets data quantity to see if bought quantity is enough
            if (queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();
                double boughtQuantity = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract
                        .TreasuryData.COLUMN_QUANTITY_TOTAL));
                boughtQuantity = Util.round(boughtQuantity, 2);
                if (boughtQuantity >= quantity) {
                    // Bought quantity is bigger then quantity trying to sell
                    isQuantityEnough = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (!isEditTextEmpty(editQuantity) && isDigitOnly && isQuantityEnough) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid double and that there is enough quantity of that investment to sell
    protected boolean isValidSellFixed(EditText value, EditText editSymbol) {
        String symbol = editSymbol.getText().toString();
        // Prepare query for currency data
        String selection = PortfolioContract.FixedData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        boolean isQuantityEnough = false;
        if (!value.toString().isEmpty()) {
            double sellTotal = Double.parseDouble(value.getText().toString());

            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.FixedData.URI,
                    null, selection, selectionArguments, null);
            // Gets data quantity to see if bought quantity is enough
            if (queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();
                double currentTotal = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract
                        .FixedData.COLUMN_CURRENT_TOTAL));
                if (currentTotal >= sellTotal) {
                    // Bought quantity is bigger then quantity trying to sell
                    isQuantityEnough = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            // Field is empty
            return false;
        }

        Editable editable = value.getText();
        // Check if it is double input
        Pattern pattern = Pattern.compile("^[0-9]+\\.?[0-9]*$");
        if (!isEditTextEmpty(value) && pattern.matcher(editable.toString()).matches() && isQuantityEnough) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid double and that there is enough quantity of that investment to sell
    protected boolean isValidSellFund(EditText value, EditText editSymbol) {
        String symbol = editSymbol.getText().toString();
        // Prepare query for currency data
        String selection = PortfolioContract.FundData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        boolean isQuantityEnough = false;
        if (!value.toString().isEmpty()) {
            double sellTotal = Double.parseDouble(value.getText().toString());

            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.FundData.URI,
                    null, selection, selectionArguments, null);
            // Gets data quantity to see if bought quantity is enough
            if (queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();
                double currentTotal = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract
                        .FundData.COLUMN_CURRENT_TOTAL));
                if (currentTotal >= sellTotal) {
                    // Bought quantity is bigger then quantity trying to sell
                    isQuantityEnough = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            // Field is empty
            return false;
        }

        Editable editable = value.getText();
        // Check if it is double input
        Pattern pattern = Pattern.compile("^[0-9]+\\.?[0-9]*$");
        if (!isEditTextEmpty(value) && pattern.matcher(editable.toString()).matches() && isQuantityEnough) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid double and that there is enough quantity of that investment to sell
    protected boolean isValidSellOthers(EditText value, EditText editSymbol) {
        String symbol = editSymbol.getText().toString();
        // Prepare query for currency data
        String selection = PortfolioContract.OthersData.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};

        boolean isQuantityEnough = false;
        if (!value.toString().isEmpty()) {
            double sellTotal = Double.parseDouble(value.getText().toString());

            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.OthersData.URI,
                    null, selection, selectionArguments, null);
            // Gets data quantity to see if bought quantity is enough
            if (queryCursor.getCount() > 0) {
                queryCursor.moveToFirst();
                double currentTotal = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract
                        .OthersData.COLUMN_CURRENT_TOTAL));
                if (currentTotal >= sellTotal) {
                    // Bought quantity is bigger then quantity trying to sell
                    isQuantityEnough = true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            // Field is empty
            return false;
        }

        Editable editable = value.getText();
        // Check if it is double input
        Pattern pattern = Pattern.compile("^[0-9]+\\.?[0-9]*$");
        if (!isEditTextEmpty(value) && pattern.matcher(editable.toString()).matches() && isQuantityEnough) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid int
    protected boolean isValidInt(EditText symbol) {
        Editable editable = symbol.getText();
        // Check if it is digit only
        boolean isDigitOnly = TextUtils.isDigitsOnly(editable.toString());
        if (!isEditTextEmpty(symbol) && isDigitOnly) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid double value
    protected boolean isValidDouble(EditText symbol) {
        Editable editable = symbol.getText();
        // Check if it is double input
        Pattern pattern = Pattern.compile("^[0-9]+\\.?[0-9]*$");
        if (!isEditTextEmpty(symbol) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with valid double value
    protected boolean isValidPercent(EditText symbol) {
        Editable editable = symbol.getText();
        // Check if it is percent input
        Pattern pattern = Pattern.compile("^[0-9]+\\.?[0-9]*$");
        // Check inputted value is lower or equal to 100%, no sense being bigger

        try {
            double value = Double.parseDouble(editable.toString());
            boolean isPercent = (value >= 0 && value <= 100);
            if (!isEditTextEmpty(symbol) && pattern.matcher(editable.toString()).matches() &&
                    isPercent) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Validate if an EditText was set with valid date value
    protected boolean isValidDate(EditText date) {
        Editable editable = date.getText();
        // Check if it is date input
        Pattern pattern = Pattern.compile("^[0-9][0-9]\\/[0-9][0-9]\\/[0-9][0-9][0-9][0-9]$");
        if (!isEditTextEmpty(date) && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was set with date that is in the future of current date
    protected boolean isFutureDate(EditText date) {
        Editable editable = date.getText();
        String textDate = editable.toString();
        long timestamp = DateToTimestamp(textDate);
        long currentTime = new Date().getTime();
        // Check if it future date input
        if (timestamp > currentTime) {
            return true;
        } else {
            return false;
        }
    }

    // Sets DatePicker and return the OnClickListener
    public View.OnClickListener setDatePicker(final EditText inputDateView) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the inputBuyDate field
                final Calendar mCalendar = Calendar.getInstance();
                final String mDateFormat = "dd/MM/yyyy";
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mDateFormat,
                        Locale.getDefault());
                // To show current date or inputted date on datepicker
                if (inputDateView.getText().length() > 0) {
                    String mDate = inputDateView.getText().toString();
                    try {
                        Date inputtedDate = simpleDateFormat.parse(mDate);
                        // Sets the current date to the previously inputted date
                        mCalendar.setTime(inputtedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                int mYear = mCalendar.get(Calendar.YEAR);
                int mMonth = mCalendar.get(Calendar.MONTH);
                int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(mContext, new
                        DatePickerDialog.OnDateSetListener() {
                            // When the date is selected and clicked "OK" on the Spinner
                            public void onDateSet(DatePicker datepicker, int year, int month, int
                                    day) {
                                // Sets the date on the EditText field value
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, month);
                                mCalendar.set(Calendar.DAY_OF_MONTH, day);
                                inputDateView.setText(simpleDateFormat.format(mCalendar.getTime()));
                            }
                        }, mYear, mMonth, mDay);
                mDatePicker.show();
            }
        };
        return onClickListener;
    }

    // Get stock quantity that will receive the dividend per stock
    // symbol is to query by specific symbol only
    // income timestamp is to query only the quantity of stocks transactions before the timestamp
    public double getStockQuantity(String symbol, Long incomeTimestamp){
        // Return column should be only quantity of stock
        String selection = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " < ?";
        String[] selectionArguments = {symbol,String.valueOf(incomeTimestamp)};
        String sortOrder = PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " ASC";

        // Check if the symbol exists in the db
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI,
                null, selection, selectionArguments, sortOrder);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            double quantityTotal = 0;
            int currentType = 0;
            do {
                currentType = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        quantityTotal += queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.BONIFICATION:
                        quantityTotal += queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SPLIT:
                        quantityTotal = quantityTotal*queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.GROUPING:
                        quantityTotal = quantityTotal/queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    default:
                }
            } while (queryCursor.moveToNext());
            return quantityTotal;
        } else{
            return 0;
        }
    }

    // By using the timestamp of bought/sold stock, function will check if any added income
    // is affected by this buy/sell stock.
    // If any income is affected, it will update income line with new value by using
    // getStockQuantity function for each affected line
    public void updateStockIncomes(String symbol, long timestamp){
        // Prepare query for checking affected incomes
        String selection = PortfolioContract.StockIncome.COLUMN_SYMBOL + " = ? AND " + PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " > ?";
        String[] selectionArguments = {symbol, String.valueOf(timestamp)};

        // Check if any income is affected by stock buy/sell
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockIncome.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            // Sum that will be returned and updated on StockData table by updateStockData()
            double sumReceiveTotal = 0;
            do{
                String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockIncome._ID)));
                long incomeTimestamp = queryCursor.getLong(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                double quantity = getStockQuantity(symbol, incomeTimestamp);
                double perStock = queryCursor.getDouble((queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_PER_STOCK)));
                int incomeType = queryCursor.getInt((queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_TYPE)));
                double receiveTotal = quantity * perStock;

                // Prepare query to update stock quantity applied for that dividend
                // and the total income received
                String updateSelection = PortfolioContract.StockIncome._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                ContentValues incomeCV = new ContentValues();
                incomeCV.put(PortfolioContract.StockIncome.COLUMN_AFFECTED_QUANTITY, quantity);
                incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_TOTAL, receiveTotal);
                if(incomeType == Constants.IncomeType.DIVIDEND) {
                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID, receiveTotal);
                } else {
                    double tax = receiveTotal * 0.15;
                    double receiveLiquid = receiveTotal - tax;
                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_TAX, tax);
                    incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID, receiveLiquid);
                }

                // Update value on incomes table
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.StockIncome.URI,
                        incomeCV, updateSelection, updatedSelectionArguments);
                // Log update success/fail result
                if (updatedRows > 0){
                } else {
                }
            } while (queryCursor.moveToNext());
        } else {
        }
    }

    // Reads the StockTransaction entries and calculates value for StockData table for this symbol
    public boolean updateStockData(String symbol, int type){

        String selection = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in StockData
            double quantityTotal = 0;
            double buyValue = 0;
            // Buy quantity and total is to calculate correct medium buy price
            // Medium price is only for buys
            double buyQuantity = 0;
            double buyTotal = 0;
            double receiveIncome = 0;
            double taxIncome = 0;
            double mediumPrice = 0;
            int currentType;
            double bonificationQuantity = 0;
            // Used for brokerage calculation for buy and sell
            // See file calculo brokerage.txt for math
            double buyBrokerage = 0;
            double buyQuantityBrokerage = 0;
            double sellBrokerage = 0;
            // At the time of the sell, need to calculate the Medium price and total bought of that time
            // by using mediumPrice afterwards, will result in calculation error
            // Ex: In timestamp sequence, Buy 100 at 20,00, Sell 100 at 21,00, Buy 100 at 30,00
            // Ex: By that, medium price will be 25,00 and the sell by 21,00 will show as money loss, which is wrong
            // By using 20,00 at that time, sell at 21,00 will result in profit, which is correct
            double soldBuyValue = 0;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        buyQuantity += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        buyTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
                        quantityTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        buyValue += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
                        mediumPrice = buyValue/quantityTotal;
                        // Brokerage
                        buyBrokerage += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_BROKERAGE));
                        buyQuantityBrokerage += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        buyValue -= STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*mediumPrice;
                        // Add the value sold times the current medium buy price
                        soldBuyValue += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*mediumPrice;
                        // Brokerage
                        double partialSell = 0;
                        partialSell = buyBrokerage/buyQuantityBrokerage * STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        buyBrokerage -=partialSell;
                        sellBrokerage += partialSell + STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_BROKERAGE));
                        buyQuantityBrokerage -= STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.BONIFICATION:
                        bonificationQuantity += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SPLIT:
                        buyQuantity = buyQuantity*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        quantityTotal = quantityTotal*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        bonificationQuantity = bonificationQuantity*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        mediumPrice = mediumPrice/STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.GROUPING:
                        buyQuantity = buyQuantity/STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        quantityTotal = quantityTotal/STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        bonificationQuantity = bonificationQuantity/STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        mediumPrice = mediumPrice*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());
            // Adds the bonification after the buyValue is totally calculated
            // Bonification cannot influence on the buy value
            quantityTotal += bonificationQuantity;
            ContentValues stockDataCV = new ContentValues();

            stockDataCV.put(PortfolioContract.StockData.COLUMN_SYMBOL, symbol);

            selection = PortfolioContract.StockData.COLUMN_SYMBOL + " = ? ";

            // Searches for existing StockData to update value.
            // If dosent exists, creates new one
            Cursor queryDataCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockData.URI,
                    null, selection, selectionArguments, null);

            double currentTotal = 0;
            double variation = 0;
            // Create new StockData for this symbol
            if (queryDataCursor.getCount() == 0){
                // Adds data to the database
                Uri insertedStockDataUri = mContext.getContentResolver().insert(PortfolioContract.StockData.URI,
                        stockDataCV);

                // If error occurs to add, shows error message
                if (insertedStockDataUri != null) {
                    // Update Stock Portfolio
                } else {
                    return false;
                }
            } else {
                // Needs to update current total and total gain with latest current price
                // If not, StockDetailsOverview will not update current total and total gain, unless refreshing the View
                if (type == Constants.Type.DELETE_TRANSACION || type == Constants.Type.BONIFICATION || type == Constants.Type.SELL){
                    queryDataCursor.moveToFirst();
                    double currentPrice = queryDataCursor.getDouble(queryDataCursor.getColumnIndex(PortfolioContract.StockData.COLUMN_CURRENT_PRICE));
                    currentTotal = currentPrice*quantityTotal;
                    variation = currentTotal - buyTotal;
                }
            }

            Intent mServiceIntent = new Intent(mContext, StockIntentService
                    .class);
            mServiceIntent.putExtra(StockIntentService.ADD_SYMBOL, symbol);
            getActivity().startService(mServiceIntent);

            // Query Income table to get total of this stock income
            String[] affectedColumn = {"sum("+ PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID+")",
                    "sum("+ PortfolioContract.StockIncome.COLUMN_TAX+")"};
            selection = PortfolioContract.StockIncome.COLUMN_SYMBOL + " = ?";

            Cursor incomeQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockIncome.URI,
                    affectedColumn, selection, selectionArguments, null);

            if (incomeQueryCursor.getCount() > 0){
                incomeQueryCursor.moveToFirst();
                receiveIncome = incomeQueryCursor.getDouble(0);
                taxIncome = incomeQueryCursor.getDouble(1);
            } else {
                receiveIncome = 0;
            }

            stockDataCV.put(PortfolioContract.StockData.COLUMN_QUANTITY_TOTAL, quantityTotal);
            stockDataCV.put(PortfolioContract.StockData.COLUMN_BUY_VALUE_TOTAL, buyValue);

            if ((type == Constants.Type.DELETE_TRANSACION || type == Constants.Type.BONIFICATION || type == Constants.Type.SELL) && queryDataCursor.getCount() > 0){
                stockDataCV.put(PortfolioContract.StockData.COLUMN_CURRENT_TOTAL, currentTotal);
                stockDataCV.put(PortfolioContract.StockData.COLUMN_VARIATION, variation);
            }
            stockDataCV.put(PortfolioContract.StockData.COLUMN_NET_INCOME, receiveIncome);
            stockDataCV.put(PortfolioContract.StockData.COLUMN_INCOME_TAX, taxIncome);
            stockDataCV.put(PortfolioContract.StockData.COLUMN_MEDIUM_PRICE, mediumPrice);
            stockDataCV.put(PortfolioContract.StockData.COLUMN_BROKERAGE, buyBrokerage);

            if(quantityTotal > 0){
                // Set stock as active
                stockDataCV.put(PortfolioContract.StockData.COLUMN_STATUS, Constants.Status.ACTIVE);
            } else {
                // Set stock as sold
                stockDataCV.put(PortfolioContract.StockData.COLUMN_STATUS, Constants.Status.SOLD);
            }
            // Searches for existing StockData to update value.
            // If dosent exists, creates new one
            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.StockData.URI,
                    null, selection, selectionArguments, null);

            queryCursor.moveToFirst();

            String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.StockData._ID)));

            // Update
            // Prepare query to update stock data
            String updateSelection = PortfolioContract.StockData._ID + " = ?";
            String[] updatedSelectionArguments = {_id};

            // Update value on stock data
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.StockData.URI,
                    stockDataCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                // Update Stock Portfolio
                // Send broadcast so StockReceiver can update the rest
                updateSoldStockData(symbol, soldBuyValue, sellBrokerage);
                return true;
            } else {
                return false;
            }
        } else{
            return false;
        }
    }

    // Reads the StockTransaction entries and calculates value for StockData table for this symbol
    public boolean updateSoldStockData(String symbol, double soldBuyValue, double sellBrokerage){

        String selection = PortfolioContract.StockTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.StockTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in StockData
            int quantityTotal = 0;
            double soldTotal = 0;
            double sellMediumPrice = 0;
            int currentType;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_TYPE));
                double price = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.SELL:
                        quantityTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY));
                        soldTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
                        sellMediumPrice = soldTotal/quantityTotal;
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());

            // If there is any sold stock
            if (quantityTotal > 0) {
                ContentValues stockDataCV = new ContentValues();

                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_SYMBOL, symbol);

                selection = PortfolioContract.SoldStockData.COLUMN_SYMBOL + " = ? ";

                // Searches for existing StockData to update value.
                // If dosent exists, creates new one
                Cursor queryDataCursor = mContext.getContentResolver().query(
                        PortfolioContract.SoldStockData.URI,
                        null, selection, selectionArguments, null);

                // Create new StockData for this symbol
                if (queryDataCursor.getCount() == 0) {
                    // Adds data to the database
                    Uri insertedStockDataUri = mContext.getContentResolver().insert(PortfolioContract.SoldStockData.URI,

                            stockDataCV);

                    // If error occurs to add, shows error message
                    if (insertedStockDataUri != null) {
                        // Update Stock Portfolio
                    } else {
                        return false;
                    }
                }

                double sellGain = soldTotal - soldBuyValue - sellBrokerage;
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_QUANTITY_TOTAL, quantityTotal);
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_BUY_VALUE_TOTAL, soldBuyValue);
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_SELL_MEDIUM_PRICE, sellMediumPrice);
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_SELL_TOTAL, soldTotal);
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_SELL_GAIN, sellGain);
                stockDataCV.put(PortfolioContract.SoldStockData.COLUMN_BROKERAGE, sellBrokerage);

                // Searches for existing StockData to update value.
                // If dosent exists, creates new one
                Cursor queryCursor = mContext.getContentResolver().query(
                        PortfolioContract.SoldStockData.URI,
                        null, selection, selectionArguments, null);

                queryCursor.moveToFirst();

                String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.SoldStockData._ID)));

                // Update
                // Prepare query to update stock data
                String updateSelection = PortfolioContract.SoldStockData._ID + " = ?";
                String[] updatedSelectionArguments = {_id};

                // Update value on stock data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.SoldStockData.URI,
                        stockDataCV, updateSelection, updatedSelectionArguments);
                // Log update success/fail result
                if (updatedRows > 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        } else{
            return false;
        }
    }

    // Reads the CurrencyTransaction entries and calculates value for CurrencyData table for this symbol
    public boolean updateCurrencyData(String symbol, int type){

        String selection = PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.CurrencyTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.CurrencyTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in CurrencyData
            double quantityTotal = 0;
            double buyValue = 0;
            // Buy quantity and total is to calculate correct medium buy price
            // Medium price is only for buys
            double buyQuantity = 0;
            double buyTotal = 0;
            double receiveIncome = 0;
            double taxIncome = 0;
            double mediumPrice = 0;
            int currentType;
            // At the time of the sell, need to calculate the Medium price and total bought of that time
            // by using mediumPrice afterwards, will result in calculation error
            // Ex: In timestamp sequence, Buy 100 at 20,00, Sell 100 at 21,00, Buy 100 at 30,00
            // Ex: By that, medium price will be 25,00 and the sell by 21,00 will show as money loss, which is wrong
            // By using 20,00 at that time, sell at 21,00 will result in profit, which is correct
            double soldBuyValue = 0;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        buyQuantity += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY));
                        buyTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                        quantityTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY));
                        buyValue += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                        mediumPrice = buyValue/quantityTotal;
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY));
                        buyValue -= STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY))*mediumPrice;
                        // Add the value sold times the current medium buy price
                        soldBuyValue += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY))*mediumPrice;
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());
            ContentValues currencyDataCV = new ContentValues();

            currencyDataCV.put(PortfolioContract.CurrencyData.COLUMN_SYMBOL, symbol);

            selection = PortfolioContract.CurrencyData.COLUMN_SYMBOL + " = ? ";

            // Searches for existing CurrencyData to update value.
            // If dosent exists, creates new one
            Cursor queryDataCursor = mContext.getContentResolver().query(
                    PortfolioContract.CurrencyData.URI,
                    null, selection, selectionArguments, null);

            double currentTotal = 0;
            double variation = 0;
            // Create new CurrencyData for this symbol
            if (queryDataCursor.getCount() == 0){
                // Adds data to the database
                Uri insertedCurrencyDataUri = mContext.getContentResolver().insert(PortfolioContract.CurrencyData.URI,
                        currencyDataCV);

                // If error occurs to add, shows error message
                if (insertedCurrencyDataUri != null) {
                    // Update Currency Portfolio
                } else {
                    return false;
                }
            } else {
                // Needs to update current total and total gain with latest current price
                // If not, CurrencyDetailsOverview will not update current total and total gain, unless refreshing the View
                if (type == Constants.Type.DELETE_TRANSACION){
                    queryDataCursor.moveToFirst();
                    double currentPrice = queryDataCursor.getDouble(queryDataCursor.getColumnIndex(PortfolioContract.CurrencyData.COLUMN_CURRENT_PRICE));
                    currentTotal = currentPrice*quantityTotal;
                    variation = currentTotal - buyTotal;
                }
            }

            if (symbol.equalsIgnoreCase("BTC") || symbol.equalsIgnoreCase("LTC")){
                Intent mServiceIntent = new Intent(mContext, CryptoIntentService
                        .class);
                mServiceIntent.putExtra(CryptoIntentService.ADD_SYMBOL, symbol);
                getActivity().startService(mServiceIntent);
            } else {
                Intent mServiceIntent = new Intent(mContext, CurrencyIntentService
                        .class);
                mServiceIntent.putExtra(CurrencyIntentService.ADD_SYMBOL, symbol);
                getActivity().startService(mServiceIntent);
            }

            currencyDataCV.put(PortfolioContract.CurrencyData.COLUMN_QUANTITY_TOTAL, quantityTotal);
            currencyDataCV.put(PortfolioContract.CurrencyData.COLUMN_BUY_VALUE_TOTAL, buyValue);
            if ((type == Constants.Type.DELETE_TRANSACION || type == Constants.Type.BONIFICATION) && queryDataCursor.getCount() > 0){
                currencyDataCV.put(PortfolioContract.CurrencyData.COLUMN_CURRENT_TOTAL, currentTotal);
                currencyDataCV.put(PortfolioContract.CurrencyData.COLUMN_VARIATION, variation);
            }
            currencyDataCV.put(PortfolioContract.CurrencyData.COLUMN_MEDIUM_PRICE, mediumPrice);

            if(quantityTotal > 0){
                // Set Currency as active
                currencyDataCV.put(PortfolioContract.CurrencyData.COLUMN_STATUS, Constants.Status.ACTIVE);
            } else {
                // Set Currency as sold
                currencyDataCV.put(PortfolioContract.CurrencyData.COLUMN_STATUS, Constants.Status.SOLD);
            }
            // Searches for existing CurrencyData to update value.
            // If dosent exists, creates new one
            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.CurrencyData.URI,
                    null, selection, selectionArguments, null);

            queryCursor.moveToFirst();

            String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.CurrencyData._ID)));

            // Update
            // Prepare query to update Currency data
            String updateSelection = PortfolioContract.CurrencyData._ID + " = ?";
            String[] updatedSelectionArguments = {_id};

            // Update value on Currency data
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.CurrencyData.URI,
                    currencyDataCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                // Update Currency Portfolio
                // Send broadcast so CurrencyReceiver can update the rest
                updateSoldCurrencyData(symbol, soldBuyValue);
                return true;
            } else {
                return false;
            }
        } else{
            return false;
        }
    }

    // Reads the CurrencyTransaction entries and calculates value for CurrencyData table for this symbol
    public boolean updateSoldCurrencyData(String symbol, double soldBuyValue){

        String selection = PortfolioContract.CurrencyTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.CurrencyTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.CurrencyTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in CurrencyData
            double quantityTotal = 0;
            double soldTotal = 0;
            double sellMediumPrice = 0;
            int currentType;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_TYPE));
                double price = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_PRICE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.SELL:
                        quantityTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY));
                        soldTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.CurrencyTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                        sellMediumPrice = soldTotal/quantityTotal;
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());

            // If there is any sold Currency
            if (quantityTotal > 0) {
                ContentValues currencyDataCV = new ContentValues();

                currencyDataCV.put(PortfolioContract.SoldCurrencyData.COLUMN_SYMBOL, symbol);

                selection = PortfolioContract.SoldCurrencyData.COLUMN_SYMBOL + " = ? ";

                // Searches for existing CurrencyData to update value.
                // If dosent exists, creates new one
                Cursor queryDataCursor = mContext.getContentResolver().query(
                        PortfolioContract.SoldCurrencyData.URI,
                        null, selection, selectionArguments, null);

                // Create new CurrencyData for this symbol
                if (queryDataCursor.getCount() == 0) {
                    // Adds data to the database
                    Uri insertedCurrencyDataUri = mContext.getContentResolver().insert(PortfolioContract.SoldCurrencyData.URI,

                            currencyDataCV);

                    // If error occurs to add, shows error message
                    if (insertedCurrencyDataUri != null) {
                        // Update Currency Portfolio
                    } else {
                        return false;
                    }
                }

                double sellGain = soldTotal - soldBuyValue;
                currencyDataCV.put(PortfolioContract.SoldCurrencyData.COLUMN_QUANTITY_TOTAL, quantityTotal);
                currencyDataCV.put(PortfolioContract.SoldCurrencyData.COLUMN_BUY_VALUE_TOTAL, soldBuyValue);
                currencyDataCV.put(PortfolioContract.SoldCurrencyData.COLUMN_SELL_MEDIUM_PRICE, sellMediumPrice);
                currencyDataCV.put(PortfolioContract.SoldCurrencyData.COLUMN_SELL_TOTAL, soldTotal);
                currencyDataCV.put(PortfolioContract.SoldCurrencyData.COLUMN_SELL_GAIN, sellGain);

                // Searches for existing CurrencyData to update value.
                // If dosent exists, creates new one
                Cursor queryCursor = mContext.getContentResolver().query(
                        PortfolioContract.SoldCurrencyData.URI,
                        null, selection, selectionArguments, null);

                queryCursor.moveToFirst();

                String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.SoldCurrencyData._ID)));

                // Update
                // Prepare query to update Currency data
                String updateSelection = PortfolioContract.SoldCurrencyData._ID + " = ?";
                String[] updatedSelectionArguments = {_id};

                // Update value on Currency data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.SoldCurrencyData.URI,
                        currencyDataCV, updateSelection, updatedSelectionArguments);
                // Log update success/fail result
                if (updatedRows > 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        } else{
            return false;
        }
    }

    // Reads the FiiTransaction entries and calculates value for FiiData table for this symbol
    public boolean updateFiiData(String symbol, int type){

        String selection = PortfolioContract.FiiTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.FiiTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in FiiData
            double quantityTotal = 0;
            double buyValue = 0;
            // Buy quantity and total is to calculate correct medium buy price
            // Medium price is only for buys
            double buyQuantity = 0;
            double buyTotal = 0;
            double receiveIncome = 0;
            double taxIncome = 0;
            double mediumPrice = 0;
            int currentType;
            // Used for brokerage calculation for buy and sell
            // See file calculo brokerage.txt for math
            double buyBrokerage = 0;
            double buyQuantityBrokerage = 0;
            double sellBrokerage = 0;
            // At the time of the sell, need to calculate the Medium price and total bought of that time
            // by using mediumPrice afterwards, will result in calculation error
            // Ex: In timestamp sequence, Buy 100 at 20,00, Sell 100 at 21,00, Buy 100 at 30,00
            // Ex: By that, medium price will be 25,00 and the sell by 21,00 will show as money loss, which is wrong
            // By using 20,00 at that time, sell at 21,00 will result in profit, which is correct
            double soldBuyValue = 0;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        buyQuantity += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        buyTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                        quantityTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        buyValue += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                        mediumPrice = buyValue/quantityTotal;
                        // Brokerage
                        buyBrokerage += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_BROKERAGE));
                        buyQuantityBrokerage += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        buyValue -= STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY))*mediumPrice;
                        // Add the value sold times the current medium buy price
                        soldBuyValue += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY))*mediumPrice;
                        // Brokerage
                        double partialSell = 0;
                        partialSell = buyBrokerage/buyQuantityBrokerage * STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        buyBrokerage -=partialSell;
                        sellBrokerage += partialSell + STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_BROKERAGE));
                        buyQuantityBrokerage -= STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SPLIT:
                        buyQuantity = buyQuantity*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        quantityTotal = quantityTotal*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        mediumPrice = mediumPrice/STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.GROUPING:
                        buyQuantity = buyQuantity/STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        quantityTotal = quantityTotal/STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        mediumPrice = mediumPrice*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());
            ContentValues fiiDataCV = new ContentValues();

            fiiDataCV.put(PortfolioContract.FiiData.COLUMN_SYMBOL, symbol);

            selection = PortfolioContract.FiiData.COLUMN_SYMBOL + " = ? ";

            // Searches for existing FiiData to update value.
            // If dosent exists, creates new one
            Cursor queryDataCursor = mContext.getContentResolver().query(
                    PortfolioContract.FiiData.URI,
                    null, selection, selectionArguments, null);

            double currentTotal = 0;
            double variation = 0;
            // Create new FiiData for this symbol
            if (queryDataCursor.getCount() == 0){
                // Adds data to the database
                Uri insertedFiiDataUri = mContext.getContentResolver().insert(PortfolioContract.FiiData.URI,
                        fiiDataCV);

                // If error occurs to add, shows error message
                if (insertedFiiDataUri != null) {
                    // Update Fii Portfolio
                } else {
                    return false;
                }
            } else {
                // Needs to update current total and total gain with latest current price
                // If not, FiiDetailsOverview will not update current total and total gain, unless refreshing the View
                if (type == Constants.Type.DELETE_TRANSACION || type == Constants.Type.SELL){
                    queryDataCursor.moveToFirst();
                    double currentPrice = queryDataCursor.getDouble(queryDataCursor.getColumnIndex(PortfolioContract.FiiData.COLUMN_CURRENT_PRICE));
                    currentTotal = currentPrice*quantityTotal;
                    variation = currentTotal - buyTotal;
                }
            }

            Intent mServiceIntent = new Intent(mContext, FiiIntentService
                    .class);
            mServiceIntent.putExtra(FiiIntentService.ADD_SYMBOL, symbol);
            getActivity().startService(mServiceIntent);

            // Query Income table to get total of this fii income
            String[] affectedColumn = {"sum("+ PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID+")",
                    "sum("+ PortfolioContract.FiiIncome.COLUMN_TAX+")"};
            selection = PortfolioContract.FiiIncome.COLUMN_SYMBOL + " = ?";

            Cursor incomeQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.FiiIncome.URI,
                    affectedColumn, selection, selectionArguments, null);

            if (incomeQueryCursor.getCount() > 0){
                incomeQueryCursor.moveToFirst();
                receiveIncome = incomeQueryCursor.getDouble(0);
                taxIncome = incomeQueryCursor.getDouble(1);
            } else {
                receiveIncome = 0;
            }

            fiiDataCV.put(PortfolioContract.FiiData.COLUMN_QUANTITY_TOTAL, quantityTotal);
            fiiDataCV.put(PortfolioContract.FiiData.COLUMN_BUY_VALUE_TOTAL, buyValue);
            if ((type == Constants.Type.DELETE_TRANSACION || type == Constants.Type.SELL) && queryDataCursor.getCount() > 0){
                fiiDataCV.put(PortfolioContract.FiiData.COLUMN_CURRENT_TOTAL, currentTotal);
                fiiDataCV.put(PortfolioContract.FiiData.COLUMN_VARIATION, variation);
            }
            fiiDataCV.put(PortfolioContract.FiiData.COLUMN_INCOME, receiveIncome);
            fiiDataCV.put(PortfolioContract.FiiData.COLUMN_INCOME_TAX, taxIncome);
            fiiDataCV.put(PortfolioContract.FiiData.COLUMN_MEDIUM_PRICE, mediumPrice);
            fiiDataCV.put(PortfolioContract.FiiData.COLUMN_BROKERAGE, buyBrokerage);

            if(quantityTotal > 0){
                // Set fii as active
                fiiDataCV.put(PortfolioContract.FiiData.COLUMN_STATUS, Constants.Status.ACTIVE);
            } else {
                // Set fii as sold
                fiiDataCV.put(PortfolioContract.FiiData.COLUMN_STATUS, Constants.Status.SOLD);
            }
            // Searches for existing FiiData to update value.
            // If dosent exists, creates new one
            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.FiiData.URI,
                    null, selection, selectionArguments, null);

            queryCursor.moveToFirst();

            String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FiiData._ID)));

            // Update
            // Prepare query to update fii data
            String updateSelection = PortfolioContract.FiiData._ID + " = ?";
            String[] updatedSelectionArguments = {_id};

            // Update value on fii data
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.FiiData.URI,
                    fiiDataCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                // Update Fii Portfolio
                // Send broadcast so FiiReceiver can update the rest
                updateSoldFiiData(symbol, soldBuyValue, sellBrokerage);
                return true;
            } else {
                return false;
            }
        } else{
            return false;
        }
    }

    // Reads the FiiTransaction entries and calculates value for SoldFiiData table for this symbol
    public boolean updateSoldFiiData(String symbol, double soldBuyValue, double sellBrokerage){

        String selection = PortfolioContract.FiiTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.FiiTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in FiiData
            int quantityTotal = 0;
            double soldTotal = 0;
            double sellMediumPrice = 0;
            int currentType;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_TYPE));
                double price = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.SELL:
                        quantityTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        soldTotal += STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                        sellMediumPrice = soldTotal/quantityTotal;
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());

            // If there is any sold fii
            if (quantityTotal > 0) {
                ContentValues fiiDataCV = new ContentValues();

                fiiDataCV.put(PortfolioContract.SoldFiiData.COLUMN_SYMBOL, symbol);

                selection = PortfolioContract.SoldFiiData.COLUMN_SYMBOL + " = ? ";

                // Searches for existing FiiData to update value.
                // If dosent exists, creates new one
                Cursor queryDataCursor = mContext.getContentResolver().query(
                        PortfolioContract.SoldFiiData.URI,
                        null, selection, selectionArguments, null);

                // Create new FiiData for this symbol
                if (queryDataCursor.getCount() == 0) {
                    // Adds data to the database
                    Uri insertedFiiDataUri = mContext.getContentResolver().insert(PortfolioContract.SoldFiiData.URI,

                            fiiDataCV);

                    // If error occurs to add, shows error message
                    if (insertedFiiDataUri != null) {
                        // Update Fii Portfolio
                    } else {
                        return false;
                    }
                }

                double sellGain = soldTotal - soldBuyValue - sellBrokerage;
                fiiDataCV.put(PortfolioContract.SoldFiiData.COLUMN_QUANTITY_TOTAL, quantityTotal);
                fiiDataCV.put(PortfolioContract.SoldFiiData.COLUMN_BUY_VALUE_TOTAL, soldBuyValue);
                fiiDataCV.put(PortfolioContract.SoldFiiData.COLUMN_SELL_MEDIUM_PRICE, sellMediumPrice);
                fiiDataCV.put(PortfolioContract.SoldFiiData.COLUMN_SELL_TOTAL, soldTotal);
                fiiDataCV.put(PortfolioContract.SoldFiiData.COLUMN_SELL_GAIN, sellGain);
                fiiDataCV.put(PortfolioContract.SoldFiiData.COLUMN_BROKERAGE, sellBrokerage);

                // Searches for existing FiiData to update value.
                // If dosent exists, creates new one
                Cursor queryCursor = mContext.getContentResolver().query(
                        PortfolioContract.SoldFiiData.URI,
                        null, selection, selectionArguments, null);

                queryCursor.moveToFirst();

                String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.SoldFiiData._ID)));

                // Update
                // Prepare query to update fii data
                String updateSelection = PortfolioContract.SoldFiiData._ID + " = ?";
                String[] updatedSelectionArguments = {_id};

                // Update value on fii data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.SoldFiiData.URI,
                        fiiDataCV, updateSelection, updatedSelectionArguments);
                // Log update success/fail result
                if (updatedRows > 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        } else{
            return false;
        }
    }

    // By using the timestamp of bought/sold fii, function will check if any added income
    // is affected by this buy/sell fii.
    // If any income is affected, it will update income line with new value by using
    // getFiiQuantity function for each affected line
    public void updateFiiIncomes(String symbol, long timestamp){
        // Prepare query for checking affected incomes
        String selection = PortfolioContract.FiiIncome.COLUMN_SYMBOL + " = ? AND " + PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " > ?";
        String[] selectionArguments = {symbol, String.valueOf(timestamp)};

        // Check if any income is affected by fii buy/sell
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FiiIncome.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            // Sum that will be returned and updated on FiiData table by updateFiiData()
            double sumReceiveTotal = 0;
            do{
                String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FiiIncome._ID)));
                long incomeTimestamp = queryCursor.getLong(queryCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                double quantity = getFiiQuantity(symbol, incomeTimestamp);
                double perFii = queryCursor.getDouble((queryCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_PER_FII)));
                int incomeType = queryCursor.getInt((queryCursor.getColumnIndex(PortfolioContract.FiiIncome.COLUMN_TYPE)));
                double receiveTotal = quantity * perFii;

                // Prepare query to update fii quantity applied for that dividend
                // and the total income received
                String updateSelection = PortfolioContract.FiiIncome._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                ContentValues incomeCV = new ContentValues();

                incomeCV.put(PortfolioContract.FiiIncome.COLUMN_AFFECTED_QUANTITY, quantity);
                incomeCV.put(PortfolioContract.FiiIncome.COLUMN_RECEIVE_TOTAL, receiveTotal);
                incomeCV.put(PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID, receiveTotal);
                double tax = 0;
                double receiveLiquid = receiveTotal;
                incomeCV.put(PortfolioContract.FiiIncome.COLUMN_TAX, tax);
                incomeCV.put(PortfolioContract.FiiIncome.COLUMN_RECEIVE_LIQUID, receiveLiquid);

                // Update value on incomes table
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.FiiIncome.URI,
                        incomeCV, updateSelection, updatedSelectionArguments);
                // Log update success/fail result
                if (updatedRows > 0){
                } else {
                }
            } while (queryCursor.moveToNext());
        } else {
        }
    }

    // Get fii quantity that will receive the dividend per fii
    // symbol is to query by specific symbol only
    // income timestamp is to query only the quantity of fiis transactions before the timestamp
    public double getFiiQuantity(String symbol, Long incomeTimestamp){
        // Return column should be only quantity of fii
        String selection = PortfolioContract.FiiTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP + " < ?";
        String[] selectionArguments = {symbol,String.valueOf(incomeTimestamp)};
        String sortOrder = PortfolioContract.FiiTransaction.COLUMN_TIMESTAMP + " ASC";

        // Check if the symbol exists in the db
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.FiiTransaction.URI,
                null, selection, selectionArguments, sortOrder);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            double quantityTotal = 0;
            int currentType = 0;
            do {
                currentType = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        quantityTotal += queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SPLIT:
                        quantityTotal = quantityTotal*queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.GROUPING:
                        quantityTotal = quantityTotal/queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_QUANTITY));
                        break;
                    default:
                }
            } while (queryCursor.moveToNext());
            return quantityTotal;
        } else{
            return 0;
        }
    }

    // Reads the FixedTransaction entries and calculates value for FixedData table for this symbol
    public boolean updateFixedData(String symbol, int type){

        String selection = PortfolioContract.FixedTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.FixedTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.FixedTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in FixedData
            // Buy quantity and total is to calculate correct medium buy price
            // Medium price is only for buys
            double buyTotal = 0;
            double lastSell = 0;
            int currentType;
            // At the time of the sell, need to calculate the Medium price and total bought of that time
            // by using mediumPrice afterwards, will result in calculation error
            // Ex: In timestamp sequence, Buy 100 at 20,00, Sell 100 at 21,00, Buy 100 at 30,00
            // Ex: By that, medium price will be 25,00 and the sell by 21,00 will show as money loss, which is wrong
            // By using 20,00 at that time, sell at 21,00 will result in profit, which is correct
            double soldTotal = 0;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        buyTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TOTAL));
                        break;
                    case Constants.Type.SELL:
                        // Add the value sold times the current medium buy price
                        soldTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TOTAL));
                        lastSell = STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FixedTransaction.COLUMN_TOTAL));
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());
            ContentValues fixedDataCV = new ContentValues();

            fixedDataCV.put(PortfolioContract.FixedData.COLUMN_SYMBOL, symbol);

            selection = PortfolioContract.FixedData.COLUMN_SYMBOL + " = ? ";

            // Searches for existing FixedData to update value.
            // If dosent exists, creates new one
            Cursor queryDataCursor = mContext.getContentResolver().query(
                    PortfolioContract.FixedData.URI,
                    null, selection, selectionArguments, null);

            double currentTotal = 0;
            // Create new FixedData for this symbol
            if (queryDataCursor.getCount() == 0){
                // Current total will be the same as buyTotal at first
                currentTotal = buyTotal;
                fixedDataCV.put(PortfolioContract.FixedData.COLUMN_CURRENT_TOTAL, buyTotal);
                // Adds data to the database
                Uri insertedFixedDataUri = mContext.getContentResolver().insert(PortfolioContract.FixedData.URI,
                        fixedDataCV);

                // If error occurs to add, shows error message
                if (insertedFixedDataUri != null) {
                    // Update Fixed income Portfolio
                } else {
                    return false;
                }
            } else {
                // Needs to update current total and total gain with latest current price
                // If not, FixedDetailsOverview will not update current total and total gain, unless refreshing the View
                queryDataCursor.moveToFirst();
                currentTotal = queryDataCursor.getDouble(queryDataCursor.getColumnIndex(PortfolioContract.FixedData.COLUMN_CURRENT_TOTAL));
            }

            // Subtract sold value from currentTotal if is selling fixed income
            if (type == Constants.Type.SELL){
                currentTotal -= lastSell;
            }

            double totalGain = currentTotal + soldTotal - buyTotal;

            fixedDataCV.put(PortfolioContract.FixedData.COLUMN_BUY_VALUE_TOTAL, buyTotal);
            fixedDataCV.put(PortfolioContract.FixedData.COLUMN_SELL_VALUE_TOTAL, soldTotal);
            fixedDataCV.put(PortfolioContract.FixedData.COLUMN_TOTAL_GAIN, totalGain);
            if ((type == Constants.Type.SELL) && queryDataCursor.getCount() > 0){
                fixedDataCV.put(PortfolioContract.FixedData.COLUMN_CURRENT_TOTAL, currentTotal);
            }


            // Set fixed income as active
            fixedDataCV.put(PortfolioContract.FixedData.COLUMN_STATUS, Constants.Status.ACTIVE);

            Intent mServiceIntent = new Intent(mContext, FixedIntentService
                    .class);
            mServiceIntent.putExtra(FixedIntentService.ADD_SYMBOL, symbol);
            getActivity().startService(mServiceIntent);

            // Searches for existing FixedData to update value.
            // If dosent exists, creates new one
            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.FixedData.URI,
                    null, selection, selectionArguments, null);

            queryCursor.moveToFirst();

            String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FixedData._ID)));

            // Update
            // Prepare query to update fixed income data
            String updateSelection = PortfolioContract.FixedData._ID + " = ?";
            String[] updatedSelectionArguments = {_id};

            // Update value on fixed income data
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.FixedData.URI,
                    fixedDataCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                return true;
            } else {
                return false;
            }
        } else{
            return false;
        }
    }

    // Reads the FundTransaction entries and calculates value for FundData table for this symbol
    public boolean updateFundData(String symbol, int type){

        String selection = PortfolioContract.FundTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.FundTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.FundTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in FundData
            // Buy quantity and total is to calculate correct medium buy price
            // Medium price is only for buys
            double buyTotal = 0;
            double lastSell = 0;
            int currentType;
            // At the time of the sell, need to calculate the Medium price and total bought of that time
            // by using mediumPrice afterwards, will result in calculation error
            // Ex: In timestamp sequence, Buy 100 at 20,00, Sell 100 at 21,00, Buy 100 at 30,00
            // Ex: By that, medium price will be 25,00 and the sell by 21,00 will show as money loss, which is wrong
            // By using 20,00 at that time, sell at 21,00 will result in profit, which is correct
            double soldTotal = 0;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.FundTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        buyTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FundTransaction.COLUMN_TOTAL));
                        break;
                    case Constants.Type.SELL:
                        // Add the value sold times the current medium buy price
                        soldTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FundTransaction.COLUMN_TOTAL));
                        lastSell = STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FundTransaction.COLUMN_TOTAL));
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());
            ContentValues fundDataCV = new ContentValues();

            fundDataCV.put(PortfolioContract.FundData.COLUMN_SYMBOL, symbol);

            selection = PortfolioContract.FundData.COLUMN_SYMBOL + " = ? ";

            // Searches for existing FundData to update value.
            // If dosent exists, creates new one
            Cursor queryDataCursor = mContext.getContentResolver().query(
                    PortfolioContract.FundData.URI,
                    null, selection, selectionArguments, null);

            double currentTotal = 0;
            // Create new FundData for this symbol
            if (queryDataCursor.getCount() == 0){
                // Current total will be the same as buyTotal at first
                currentTotal = buyTotal;
                fundDataCV.put(PortfolioContract.FundData.COLUMN_CURRENT_TOTAL, buyTotal);
                // Adds data to the database
                Uri insertedFundDataUri = mContext.getContentResolver().insert(PortfolioContract.FundData.URI,
                        fundDataCV);

                // If error occurs to add, shows error message
                if (insertedFundDataUri != null) {
                    // Update Fund income Portfolio
                } else {
                    return false;
                }
            } else {
                // Needs to update current total and total gain with latest current price
                // If not, FundDetailsOverview will not update current total and total gain, unless refreshing the View
                queryDataCursor.moveToFirst();
                currentTotal = queryDataCursor.getDouble(queryDataCursor.getColumnIndex(PortfolioContract.FundData.COLUMN_CURRENT_TOTAL));
            }

            // Subtract sold value from currentTotal if is selling fund income
            if (type == Constants.Type.SELL){
                currentTotal -= lastSell;
            }

            double totalGain = currentTotal + soldTotal - buyTotal;

            fundDataCV.put(PortfolioContract.FundData.COLUMN_BUY_VALUE_TOTAL, buyTotal);
            fundDataCV.put(PortfolioContract.FundData.COLUMN_SELL_VALUE_TOTAL, soldTotal);
            fundDataCV.put(PortfolioContract.FundData.COLUMN_TOTAL_GAIN, totalGain);
            if ((type == Constants.Type.SELL) && queryDataCursor.getCount() > 0){
                fundDataCV.put(PortfolioContract.FundData.COLUMN_CURRENT_TOTAL, currentTotal);
            }


            // Set fund income as active
            fundDataCV.put(PortfolioContract.FundData.COLUMN_STATUS, Constants.Status.ACTIVE);

            /*Intent mServiceIntent = new Intent(mContext, FundIntentService
                    .class);
            mServiceIntent.putExtra(FundIntentService.ADD_SYMBOL, symbol);
            getActivity().startService(mServiceIntent);*/

            // Searches for existing FundData to update value.
            // If dosent exists, creates new one
            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.FundData.URI,
                    null, selection, selectionArguments, null);

            queryCursor.moveToFirst();

            String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.FundData._ID)));

            // Update
            // Prepare query to update fund income data
            String updateSelection = PortfolioContract.FundData._ID + " = ?";
            String[] updatedSelectionArguments = {_id};

            // Update value on fund income data
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.FundData.URI,
                    fundDataCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                return true;
            } else {
                return false;
            }
        } else{
            return false;
        }
    }

    // Reads the TreasuryTransaction entries and calculates value for TreasuryData table for this symbol
    public boolean updateTreasuryData(String symbol, int type){

        String selection = PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.TreasuryTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in TreasuryData
            double quantityTotal = 0;
            double buyValue = 0;
            // Buy quantity and total is to calculate correct medium buy price
            // Medium price is only for buys
            double buyQuantity = 0;
            double buyTotal = 0;
            double receiveIncome = 0;
            double taxIncome = 0;
            double mediumPrice = 0;
            int currentType;
            // At the time of the sell, need to calculate the Medium price and total bought of that time
            // by using mediumPrice afterwards, will result in calculation error
            // Ex: In timestamp sequence, Buy 100 at 20,00, Sell 100 at 21,00, Buy 100 at 30,00
            // Ex: By that, medium price will be 25,00 and the sell by 21,00 will show as money loss, which is wrong
            // By using 20,00 at that time, sell at 21,00 will result in profit, which is correct
            double soldBuyValue = 0;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        buyQuantity += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY));
                        buyTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                        quantityTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY));
                        buyValue += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                        mediumPrice = buyValue/quantityTotal;
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY));
                        buyValue -= STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY))*mediumPrice;
                        // Add the value sold times the current medium buy price
                        soldBuyValue += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY))*mediumPrice;
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());
            ContentValues treasuryDataCV = new ContentValues();

            treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_SYMBOL, symbol);

            selection = PortfolioContract.TreasuryData.COLUMN_SYMBOL + " = ? ";

            // Searches for existing TreasuryData to update value.
            // If dosent exists, creates new one
            Cursor queryDataCursor = mContext.getContentResolver().query(
                    PortfolioContract.TreasuryData.URI,
                    null, selection, selectionArguments, null);

            double currentTotal = 0;
            double variation = 0;
            double currentPrice = 0;
            // Create new TreasuryData for this symbol
            if (queryDataCursor.getCount() == 0){
                //TODO temporary solution until Treasury is ready for IntentService
                treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_CURRENT_PRICE, mediumPrice);
                currentPrice = mediumPrice;
                // Adds data to the database
                Uri insertedTreasuryDataUri = mContext.getContentResolver().insert(PortfolioContract.TreasuryData.URI,
                        treasuryDataCV);

                // If error occurs to add, shows error message
                if (insertedTreasuryDataUri != null) {
                    // Update Treasury Portfolio
                } else {
                    return false;
                }
            } else {
                // Needs to update current total and total gain with latest current price
                // If not, TreasuryDetailsOverview will not update current total and total gain, unless refreshing the View
                queryDataCursor.moveToFirst();
                currentPrice = queryDataCursor.getDouble(queryDataCursor.getColumnIndex(PortfolioContract.TreasuryData.COLUMN_CURRENT_PRICE));
                if (type == Constants.Type.DELETE_TRANSACION){
                    currentTotal = currentPrice*quantityTotal;
                    variation = currentTotal - buyTotal;
                }
            }

            Intent mServiceIntent = new Intent(mContext, TreasuryIntentService
                    .class);
            mServiceIntent.putExtra(TreasuryIntentService.ADD_SYMBOL, symbol);
            getActivity().startService(mServiceIntent);

            // Query Income table to get total of this treasury income
            String[] affectedColumn = {"sum("+ PortfolioContract.TreasuryIncome.COLUMN_RECEIVE_LIQUID+")",
                    "sum("+ PortfolioContract.TreasuryIncome.COLUMN_TAX+")"};
            selection = PortfolioContract.TreasuryIncome.COLUMN_SYMBOL + " = ?";

            Cursor incomeQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.TreasuryIncome.URI,
                    affectedColumn, selection, selectionArguments, null);

            if (incomeQueryCursor.getCount() > 0){
                incomeQueryCursor.moveToFirst();
                receiveIncome = incomeQueryCursor.getDouble(0);
                taxIncome = incomeQueryCursor.getDouble(1);
            } else {
                receiveIncome = 0;
            }

            treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_QUANTITY_TOTAL, quantityTotal);
            treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_BUY_VALUE_TOTAL, buyValue);
            if ((type == Constants.Type.DELETE_TRANSACION) && queryDataCursor.getCount() > 0){
                treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_CURRENT_TOTAL, currentTotal);
                treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_VARIATION, variation);
            }
            treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_INCOME, receiveIncome);
            treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_INCOME_TAX, taxIncome);
            treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_MEDIUM_PRICE, mediumPrice);

            if(quantityTotal > 0){
                // Set treasury as active
                treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_STATUS, Constants.Status.ACTIVE);
            } else {
                // Set treasury as sold
                treasuryDataCV.put(PortfolioContract.TreasuryData.COLUMN_STATUS, Constants.Status.SOLD);
            }
            // Searches for existing TreasuryData to update value.
            // If dosent exists, creates new one
            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.TreasuryData.URI,
                    null, selection, selectionArguments, null);

            queryCursor.moveToFirst();

            String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.TreasuryData._ID)));

            // Update
            // Prepare query to update treasury data
            String updateSelection = PortfolioContract.TreasuryData._ID + " = ?";
            String[] updatedSelectionArguments = {_id};

            // Update value on treasury data
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.TreasuryData.URI,
                    treasuryDataCV, updateSelection, updatedSelectionArguments);
            //TODO temporary solution until Treasury is ready for IntentService
            ContentValues treasuryBulkCV = new ContentValues();
            treasuryBulkCV.put(symbol, currentPrice);
            int updatedRows2 = mContext.getContentResolver().update(
                    PortfolioContract.TreasuryData.BULK_UPDATE_URI,
                    treasuryBulkCV, null, null);
            // Log update success/fail result
            if (updatedRows > 0 && updatedRows2 > 0){
                // Update Treasury Portfolio
                // Send broadcast so TreasuryReceiver can update the rest
                // Send to update Treasury Income Portfolio and show overview
                updateSoldTreasuryData(symbol, soldBuyValue);
                return true;
            } else {
                return false;
            }
        } else{
            return false;
        }
    }

    // Reads the TreasuryTransaction entries and calculates value for SoldTreasuryData table for this symbol
    public boolean updateSoldTreasuryData(String symbol, double soldBuyValue){

        String selection = PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.TreasuryTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in TreasuryData
            double quantityTotal = 0;
            double soldTotal = 0;
            double sellMediumPrice = 0;
            int currentType;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_TYPE));
                double price = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_PRICE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.SELL:
                        quantityTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY));
                        soldTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY))*STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.FiiTransaction.COLUMN_PRICE));
                        sellMediumPrice = soldTotal/quantityTotal;
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());

            // If there is any sold treasury
            if (quantityTotal > 0) {
                ContentValues treasuryDataCV = new ContentValues();

                treasuryDataCV.put(PortfolioContract.SoldTreasuryData.COLUMN_SYMBOL, symbol);

                selection = PortfolioContract.SoldTreasuryData.COLUMN_SYMBOL + " = ? ";

                // Searches for existing TreasuryData to update value.
                // If dosent exists, creates new one
                Cursor queryDataCursor = mContext.getContentResolver().query(
                        PortfolioContract.SoldTreasuryData.URI,
                        null, selection, selectionArguments, null);

                // Create new TreasuryData for this symbol
                if (queryDataCursor.getCount() == 0) {
                    // Adds data to the database
                    Uri insertedTreasuryDataUri = mContext.getContentResolver().insert(PortfolioContract.SoldTreasuryData.URI,

                            treasuryDataCV);

                    // If error occurs to add, shows error message
                    if (insertedTreasuryDataUri != null) {
                        // Update Treasury Portfolio
                    } else {
                        return false;
                    }
                }

                double sellGain = soldTotal - soldBuyValue;
                treasuryDataCV.put(PortfolioContract.SoldTreasuryData.COLUMN_QUANTITY_TOTAL, quantityTotal);
                treasuryDataCV.put(PortfolioContract.SoldTreasuryData.COLUMN_BUY_VALUE_TOTAL, soldBuyValue);
                treasuryDataCV.put(PortfolioContract.SoldTreasuryData.COLUMN_SELL_MEDIUM_PRICE, sellMediumPrice);
                treasuryDataCV.put(PortfolioContract.SoldTreasuryData.COLUMN_SELL_TOTAL, soldTotal);
                treasuryDataCV.put(PortfolioContract.SoldTreasuryData.COLUMN_SELL_GAIN, sellGain);

                // Searches for existing TreasuryData to update value.
                // If dosent exists, creates new one
                Cursor queryCursor = mContext.getContentResolver().query(
                        PortfolioContract.SoldTreasuryData.URI,
                        null, selection, selectionArguments, null);

                queryCursor.moveToFirst();

                String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.SoldTreasuryData._ID)));

                // Update
                // Prepare query to update treasury data
                String updateSelection = PortfolioContract.SoldTreasuryData._ID + " = ?";
                String[] updatedSelectionArguments = {_id};

                // Update value on treasury data
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.SoldTreasuryData.URI,
                        treasuryDataCV, updateSelection, updatedSelectionArguments);
                // Log update success/fail result
                if (updatedRows > 0) {
                    return true;
                } else {
                    return false;
                }
            }
            return true;
        } else{
            return false;
        }
    }

    // By using the timestamp of bought/sold treasury, function will check if any added income
    // is affected by this buy/sell treasury.
    // If any income is affected, it will update income line with new value by using
    // getTreasuryQuantity function for each affected line
    public void updateTreasuryIncomes(String symbol, long timestamp){
        // Prepare query for checking affected incomes
        String selection = PortfolioContract.TreasuryIncome.COLUMN_SYMBOL + " = ? AND " + PortfolioContract.TreasuryIncome.COLUMN_EXDIVIDEND_TIMESTAMP + " > ?";
        String[] selectionArguments = {symbol, String.valueOf(timestamp)};

        // Check if any income is affected by treasury buy/sell
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.TreasuryIncome.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            // Sum that will be returned and updated on TreasuryData table by updateTreasuryData()
            double sumReceiveTotal = 0;
            do{
                String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.TreasuryIncome._ID)));
                long incomeTimestamp = queryCursor.getLong(queryCursor.getColumnIndex(PortfolioContract.TreasuryIncome.COLUMN_EXDIVIDEND_TIMESTAMP));
                double receiveTotal = queryCursor.getDouble((queryCursor.getColumnIndex(PortfolioContract.TreasuryIncome.COLUMN_RECEIVE_TOTAL)));

                // Prepare query to update treasury quantity applied for that dividend
                // and the total income received
                String updateSelection = PortfolioContract.TreasuryIncome._ID + " = ?";
                String[] updatedSelectionArguments = {_id};
                ContentValues incomeCV = new ContentValues();

                incomeCV.put(PortfolioContract.TreasuryIncome.COLUMN_RECEIVE_TOTAL, receiveTotal);
                double tax = receiveTotal*0.15;
                double receiveLiquid = receiveTotal - tax;
                incomeCV.put(PortfolioContract.TreasuryIncome.COLUMN_TAX, tax);
                incomeCV.put(PortfolioContract.TreasuryIncome.COLUMN_RECEIVE_LIQUID, receiveLiquid);

                // Update value on incomes table
                int updatedRows = mContext.getContentResolver().update(
                        PortfolioContract.TreasuryIncome.URI,
                        incomeCV, updateSelection, updatedSelectionArguments);
                // Log update success/fail result
                if (updatedRows > 0){
                } else {
                }
            } while (queryCursor.moveToNext());
        } else {
        }
    }

    // Get treasury quantity that will receive the dividend per treasury
    // symbol is to query by specific symbol only
    // income timestamp is to query only the quantity of treasury transactions before the timestamp
    public double getTreasuryQuantity(String symbol, Long incomeTimestamp){
        // Return column should be only quantity of treasury
        String selection = PortfolioContract.TreasuryTransaction.COLUMN_SYMBOL + " = ? AND "
                + PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP + " < ?";
        String[] selectionArguments = {symbol,String.valueOf(incomeTimestamp)};
        String sortOrder = PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP + " ASC";

        // Check if the symbol exists in the db
        Cursor queryCursor = mContext.getContentResolver().query(
                PortfolioContract.TreasuryTransaction.URI,
                null, selection, selectionArguments, sortOrder);
        if(queryCursor.getCount() > 0) {
            queryCursor.moveToFirst();
            double quantityTotal = 0;
            int currentType = 0;
            do {
                currentType = queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        quantityTotal += queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY));
                        break;
                    case Constants.Type.SELL:
                        quantityTotal -= queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.TreasuryTransaction.COLUMN_QUANTITY));
                        break;
                    default:
                }
            } while (queryCursor.moveToNext());
            return quantityTotal;
        } else{
            return 0;
        }
    }

    // Reads the OthersTransaction entries and calculates value for OthersData table for this symbol
    public boolean updateOthersData(String symbol, int type){

        String selection = PortfolioContract.OthersTransaction.COLUMN_SYMBOL + " = ? ";
        String[] selectionArguments = {symbol};
        String sortOrder = PortfolioContract.OthersTransaction.COLUMN_TIMESTAMP + " ASC";

        Cursor STQueryCursor = mContext.getContentResolver().query(
                PortfolioContract.OthersTransaction.URI,
                null, selection, selectionArguments, sortOrder);

        if(STQueryCursor.getCount() > 0){
            STQueryCursor.moveToFirst();
            // Final values to be inserted in OthersData
            // Buy quantity and total is to calculate correct medium buy price
            // Medium price is only for buys
            double buyTotal = 0;
            double lastSell = 0;
            int currentType;
            double receiveIncome = 0;
            double taxIncome = 0;
            // At the time of the sell, need to calculate the Medium price and total bought of that time
            // by using mediumPrice afterwards, will result in calculation error
            // Ex: In timestamp sequence, Buy 100 at 20,00, Sell 100 at 21,00, Buy 100 at 30,00
            // Ex: By that, medium price will be 25,00 and the sell by 21,00 will show as money loss, which is wrong
            // By using 20,00 at that time, sell at 21,00 will result in profit, which is correct
            double soldTotal = 0;

            do {
                currentType = STQueryCursor.getInt(STQueryCursor.getColumnIndex(PortfolioContract.OthersTransaction.COLUMN_TYPE));
                // Does correct operation to values depending on Transaction type
                switch (currentType){
                    case Constants.Type.BUY:
                        buyTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.OthersTransaction.COLUMN_TOTAL));
                        break;
                    case Constants.Type.SELL:
                        // Add the value sold times the current medium buy price
                        soldTotal += STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.OthersTransaction.COLUMN_TOTAL));
                        lastSell = STQueryCursor.getDouble(STQueryCursor.getColumnIndex(PortfolioContract.OthersTransaction.COLUMN_TOTAL));
                        break;
                    default:
                }
            } while (STQueryCursor.moveToNext());
            ContentValues othersDataCV = new ContentValues();

            othersDataCV.put(PortfolioContract.OthersData.COLUMN_SYMBOL, symbol);

            selection = PortfolioContract.OthersData.COLUMN_SYMBOL + " = ? ";

            // Searches for existing OthersData to update value.
            // If dosent exists, creates new one
            Cursor queryDataCursor = mContext.getContentResolver().query(
                    PortfolioContract.OthersData.URI,
                    null, selection, selectionArguments, null);

            double currentTotal = 0;
            // Create new OthersData for this symbol
            if (queryDataCursor.getCount() == 0){
                // Current total will be the same as buyTotal at first
                currentTotal = buyTotal;
                othersDataCV.put(PortfolioContract.OthersData.COLUMN_CURRENT_TOTAL, buyTotal);
                // Adds data to the database
                Uri insertedOthersDataUri = mContext.getContentResolver().insert(PortfolioContract.OthersData.URI,
                        othersDataCV);

                // If error occurs to add, shows error message
                if (insertedOthersDataUri != null) {
                    // Update others income Portfolio
                } else {
                    return false;
                }
            } else {
                // Needs to update current total and total gain with latest current price
                // If not, OthersDetailsOverview will not update current total and total gain, unless refreshing the View
                queryDataCursor.moveToFirst();
                currentTotal = queryDataCursor.getDouble(queryDataCursor.getColumnIndex(PortfolioContract.OthersData.COLUMN_CURRENT_TOTAL));
            }

            // Query Income table to get total of this others income
            String[] affectedColumn = {"sum("+ PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL+")",
                    "sum("+ PortfolioContract.OthersIncome.COLUMN_TAX+")"};
            selection = PortfolioContract.OthersIncome.COLUMN_SYMBOL + " = ?";

            Cursor incomeQueryCursor = mContext.getContentResolver().query(
                    PortfolioContract.OthersIncome.URI,
                    affectedColumn, selection, selectionArguments, null);

            if (incomeQueryCursor.getCount() > 0){
                incomeQueryCursor.moveToFirst();
                receiveIncome = incomeQueryCursor.getDouble(0);
                taxIncome = incomeQueryCursor.getDouble(1);
                receiveIncome = receiveIncome - taxIncome;
            } else {
                receiveIncome = 0;
            }

            // Subtract sold value from currentTotal if is selling others income
            if (type == Constants.Type.SELL){
                currentTotal -= lastSell;
            }

            double variation = currentTotal + soldTotal - buyTotal;
            double totalGain = currentTotal + soldTotal + receiveIncome - buyTotal;

            othersDataCV.put(PortfolioContract.OthersData.COLUMN_BUY_VALUE_TOTAL, buyTotal);
            othersDataCV.put(PortfolioContract.OthersData.COLUMN_SELL_VALUE_TOTAL, soldTotal);
            othersDataCV.put(PortfolioContract.OthersData.COLUMN_VARIATION, variation);
            othersDataCV.put(PortfolioContract.OthersData.COLUMN_TOTAL_GAIN, totalGain);
            if ((type == Constants.Type.SELL) && queryDataCursor.getCount() > 0){
                othersDataCV.put(PortfolioContract.OthersData.COLUMN_CURRENT_TOTAL, currentTotal);
            }
            othersDataCV.put(PortfolioContract.OthersData.COLUMN_INCOME, receiveIncome);
            othersDataCV.put(PortfolioContract.OthersData.COLUMN_INCOME_TAX, taxIncome);

            // Set others income as active
            othersDataCV.put(PortfolioContract.OthersData.COLUMN_STATUS, Constants.Status.ACTIVE);

            // Searches for existing OthersData to update value.
            // If dosent exists, creates new one
            Cursor queryCursor = mContext.getContentResolver().query(
                    PortfolioContract.OthersData.URI,
                    null, selection, selectionArguments, null);

            queryCursor.moveToFirst();

            String _id = String.valueOf(queryCursor.getInt(queryCursor.getColumnIndex(PortfolioContract.OthersData._ID)));

            // Update
            // Prepare query to update others income data
            String updateSelection = PortfolioContract.OthersData._ID + " = ?";
            String[] updatedSelectionArguments = {_id};

            // Update value on others income data
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.OthersData.URI,
                    othersDataCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                // Send broadcast so OthersReceiver can update the rest
                // Send to update others Income Portfolio and show overview
                OthersReceiver othersReceiver = new OthersReceiver(mContext);
                othersReceiver.updateOthersPortfolio();
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.OTHERS));

                PortfolioReceiver portfolioReceiver = new PortfolioReceiver(mContext);
                portfolioReceiver.updatePortfolio();
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.Receiver.PORTFOLIO));
                return true;
            } else {
                return false;
            }
        } else{
            return false;
        }
    }
}
