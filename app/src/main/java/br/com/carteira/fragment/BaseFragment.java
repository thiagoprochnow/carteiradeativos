package br.com.carteira.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.carteira.R;
import br.com.carteira.data.PortfolioContract;

public abstract class BaseFragment extends Fragment {

    protected Context mContext;

    private static final String LOG_TAG = BaseFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    // Delete stock and all its information from database
    // This is different then selling a stock, that will maintain some information
    public boolean removeStock(String symbol) {
        int deletedTransaction = getActivity().getContentResolver().delete(PortfolioContract
                .StockTransaction
                .makeUriForStockTransaction(symbol), null, null);
        int deletedData = getActivity().getContentResolver().delete(PortfolioContract.StockData
                .makeUriForStockData(symbol), null, null);
        // Cannot check if deletedIncome > 0, because stock may not have any income to delete
        // Which is not an error
        int deletedIncome = getActivity().getContentResolver().delete(PortfolioContract.StockIncome
                .makeUriForStockIncome(symbol), null, null);
        Log.d(LOG_TAG, "DeletedTransaction: " + deletedTransaction + " DeletedData: " + deletedData + " DeletedIncome: " + deletedIncome);
        if (deletedTransaction > 0 && deletedData > 0) {

            Toast.makeText(mContext, getString(R.string.toast_stock_successfully_removed, symbol)
                    , Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.toast_stock_not_removed, symbol), Toast
                    .LENGTH_SHORT).show();
            return false;
        }
    }

    // Transform a date value of dd/MM/yyyy into a timestamp value
    public Long DateToTimestamp(String inputDate){
        Log.d(LOG_TAG, "InputDate String: " + inputDate);
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
}
