package br.com.carteira.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

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
        int deletedQuote = getActivity().getContentResolver().delete(PortfolioContract.StockQuote
                .makeUriForStockQuote(symbol), null, null);
        int deletedSymbol = getActivity().getContentResolver().delete(PortfolioContract.StockPortfolio
                .makeUriForStockPortfolio(symbol), null, null);
        // Cannot check if deletedIncome > 0, because stock may not have any income to delete
        // Which is not an error
        int deletedIncome = getActivity().getContentResolver().delete(PortfolioContract.StockIncome
                .makeUriForStockIncome(symbol), null, null);
        Log.d(LOG_TAG, "DeletedQuote: " + deletedQuote + " DeletedSymbol: " + deletedSymbol + " DeletedIncome: " + deletedIncome);
        if (deletedQuote > 0 && deletedSymbol > 0) {

            Toast.makeText(mContext, getString(R.string.toast_stock_successfully_removed, symbol)
                    , Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(mContext, getString(R.string.toast_stock_not_removed, symbol), Toast
                    .LENGTH_SHORT).show();
            return false;
        }
    }
}
