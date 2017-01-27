package br.com.carteira.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
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
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.listener.AddProductListener;

/**
 * Main fragment screen of Stocks of portfolio, accessed by selecting "Stocks" in navigation menu.
 */
public class StockMainFragment extends BaseFragment implements
        StockQuoteAdapter.StockAdapterOnClickHandler, LoaderManager
        .LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private StockQuoteAdapter mStockQuoteAdapter;
    private AddProductListener mAddProductListener;

    // Loader IDs
    private static final int STOCK_LOADER = 0;

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddProductListener) {
            mAddProductListener = (AddProductListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " Parent Activity must implements AddProductListener");
        }
    }

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
                // This will call the AddFormActivity with the correct form fragment
                mAddProductListener.onAddProduct(Constants.ProductType.STOCK);
            }
        });
        mStockQuoteAdapter = new StockQuoteAdapter(mContext, this);
        mRecyclerView.setAdapter(mStockQuoteAdapter);
        getActivity().getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(String symbol) {
        Toast.makeText(mContext, "Stock: " + symbol, Toast.LENGTH_SHORT).show();
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