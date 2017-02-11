package br.com.carteira.fragment;


import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.carteira.R;
import br.com.carteira.adapter.StockDividendAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StockIncomesFragment extends BaseFragment implements
        StockDividendAdapter.StockAdapterOnClickHandler, LoaderManager.
        LoaderCallbacks<Cursor>{

    @BindView(R.id.incomesRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private StockDividendAdapter mStockDividendAdapter;

    // Loader IDs
    private static final int INCOME_LOADER = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fragment title
        getActivity().setTitle(R.string.title_incomes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_incomes, container, false);

        ButterKnife.bind(this, view);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        // TODO: Need to change adapter to accept all incomes type and not only Dividend
        // Probably change to StockIncomeAdapter
        mStockDividendAdapter = new StockDividendAdapter(mContext, this);
        mRecyclerView.setAdapter(mStockDividendAdapter);
        getActivity().getSupportLoaderManager().initLoader(INCOME_LOADER, null, this);

        return view;
    }

    @Override
    public void onClick(String symbol) {

    }

    @Override
    public void onLongClick(final String symbol) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                PortfolioContract.StockIncome.URI,
                PortfolioContract.StockIncome.STOCK_INCOME_COLUMNS,
                null, null, PortfolioContract.StockIncome.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && mEmptyListTextView != null) {
            if (data.getCount() != 0) {
                mEmptyListTextView.setVisibility(View.GONE);
            } else {
                mEmptyListTextView.setVisibility(View.VISIBLE);
            }
        }

        mStockDividendAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStockDividendAdapter.setCursor(null);
    }
}
