package br.com.carteira.fragments;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import br.com.carteira.R;
import br.com.carteira.adapter.StockQuoteAdapter;
import br.com.carteira.data.WalletContract;

/**
 * A simple {@link Fragment} subclass.
 * Main fragment screen of Stocks of portfolio, accessed by selecting "Ações" in navigation menu.
 */
public class StockMainFragment extends BaseFragment implements
        StockQuoteAdapter.StockAdapterOnClickHandler, LoaderManager
        .LoaderCallbacks<Cursor> {
    protected RecyclerView recyclerView;
    private Context mContext;
    private StockQuoteAdapter mStockQuoteAdapter;

    // Loader IDs
    private static final int STOCK_LOADER = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.stockRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        // Floating Action Button setup
        view.findViewById(R.id.fabStocks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddStockDialog();
            }
        });
        mStockQuoteAdapter = new StockQuoteAdapter(mContext, this);
        recyclerView.setAdapter(mStockQuoteAdapter);
        getActivity().getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*if (context instanceof OnStockListFragmentListener) {
            mOnStockListFragmentListener = (OnStockListFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStockListFragmentListener");
        }*/

        mContext = context;
    }

    // Function to show the add stock dialog fragment
    public void showAddStockDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddStockDialogFragment();
        // Asks the new DialogFragment for a result with a Request_code = 0
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "AddStockDialogFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // Request code 0 is the return of the Dialog Fragment after filling the EditText field
        // and pressing positive buttons
        if (requestCode == 0) {
            // Add as a new stock to the portfolio or sums to already existing one.
            if (addStock(intent)) {
                Toast.makeText(mContext, R.string.add_stock_success, Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(mContext, R.string.add_stock_fail, Toast.LENGTH_SHORT);
            }
        }
    }

    public boolean addStock(Intent intent) {

        // Parse the information of the intent sent from the DialogFragment to add the stock
        String inputTicker = intent.getStringExtra("inputTicker");
        int inputQuantity = Integer.parseInt(intent.getStringExtra("inputQuantity"));
        double inputBuyPrice = Double.parseDouble(intent.getStringExtra("inputBuyPrice"));
        double inputObjective = Double.parseDouble(intent.getStringExtra("inputObjective"));

        // For more information on each stock variable, check the Stock.java class
        /*Stock stock = new Stock();
        stock.setTicker(inputTicker);
        stock.setStockQuantity(inputQuantity);
        stock.setBoughtPrice(inputBuyPrice);
        stock.setBoughtTotal(stock.getStockQuantity() * stock.getBoughtPrice());
        stock.setObjectivePercent(inputObjective);*/


        ContentValues stockCV = new ContentValues();
        stockCV.put(WalletContract.StockQuote.COLUMN_SYMBOL, inputTicker);
        stockCV.put(WalletContract.StockQuote.COLUMN_QUANTITY, inputQuantity);
        stockCV.put(WalletContract.StockQuote.COLUMN_BOUGHT_TOTAL, inputBuyPrice);
        stockCV.put(WalletContract.StockQuote.COLUMN_OBJECTIVE_PERCENT, inputObjective);
        Uri insertedUri = mContext.getContentResolver().insert(WalletContract.StockQuote.URI,
                stockCV);

        /*
        stock.setCurrentPrice(35.50);
        stock.setCurrentTotal(stock.getStockQuantity() * stock.getCurrentPrice());
        stock.setStockAppreciation(stock.getCurrentTotal() - stock.getBoughtTotal());

        stock.setCurrentPercent(20.00);
        stock.setTotalGain(stock.getStockAppreciation() + stock.getTotalIncome());*/

        if (insertedUri != null) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void onClick(String ticker) {
        Toast.makeText(mContext, "Stock: " + ticker, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                WalletContract.StockQuote.URI,
                WalletContract.StockQuote.STOCK_QUOTE_COLUMNS,
                null, null, WalletContract.StockQuote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mStockQuoteAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStockQuoteAdapter.setCursor(null);
    }
}
