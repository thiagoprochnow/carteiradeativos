package br.com.carteira.fragment;


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
import br.com.carteira.activity.AddStockForm;
import br.com.carteira.adapter.StockQuoteAdapter;
import br.com.carteira.data.PortfolioContract;

/**
 * A simple {@link Fragment} subclass.
 * Main fragment screen of Stocks of portfolio, accessed by selecting "Ações" in navigation menu.
 */
public class StockMainFragment extends BaseFragment implements
        StockQuoteAdapter.StockAdapterOnClickHandler, LoaderManager
        .LoaderCallbacks<Cursor> {
    protected RecyclerView mRecyclerView;
    private Context mContext;
    private StockQuoteAdapter mStockQuoteAdapter;

    // Loader IDs
    private static final int STOCK_LOADER = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.stockRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        // Floating Action Button setup
        view.findViewById(R.id.fabStocks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On click FAB, requests the new stock form with result of inputed values
                Intent intent = new Intent(getContext(),AddStockForm.class);
                startActivity(intent);
            }
        });
        mStockQuoteAdapter = new StockQuoteAdapter(mContext, this);
        mRecyclerView.setAdapter(mStockQuoteAdapter);
        getActivity().getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onClick(String ticker) {
        Toast.makeText(mContext, "Stock: " + ticker, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                PortfolioContract.StockQuote.URI,
                PortfolioContract.StockQuote.STOCK_QUOTE_COLUMNS,
                null, null, PortfolioContract.StockQuote.COLUMN_SYMBOL);
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
