package br.com.carteira.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.com.carteira.R;
import br.com.carteira.adapter.StockDividendAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.listener.IncomeDetailsListener;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StockIncomesFragment extends BaseFragment implements
        StockDividendAdapter.StockAdapterOnClickHandler, LoaderManager.
        LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = StockIncomesFragment.class.getSimpleName();

    private IncomeDetailsListener mIncomeDetailsListener;

    @BindView(R.id.incomesRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private StockDividendAdapter mStockDividendAdapter;

    private String id;
    // Loader IDs
    private static final int INCOME_LOADER = 1;

    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IncomeDetailsListener) {
            mIncomeDetailsListener = (IncomeDetailsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " Parent Activity must implements IncomeDetailsListener");
        }
    }

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

        // Gets symbol received from Intent of MainActivity and puts on Bundle for initLoader
        Intent mainActivityIntent = getActivity().getIntent();
        String symbol = mainActivityIntent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        // TODO: Need to change adapter to accept all incomes type and not only Dividend
        // Probably change to StockIncomeAdapter
        mStockDividendAdapter = new StockDividendAdapter(mContext, this);
        mRecyclerView.setAdapter(mStockDividendAdapter);
        getActivity().getSupportLoaderManager().initLoader(INCOME_LOADER, bundle, this);

        return view;
    }

    @Override
    public void onClick(String id) {
        Log.d(LOG_TAG, "ID: " + id);
        mIncomeDetailsListener.onIncomeDetails(Constants.IncomeType.DIVIDEND, id);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, String id) {
        MenuInflater inflater = getActivity().getMenuInflater();
        this.id = id;
        inflater.inflate(R.menu.income_item_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Receives symbol to make query of incomes for specific symbol
        String symbol = args.getString(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        // TODO Change sortOrder from COLUMN_SYMBOL to Date. The Order of incomes will be shown from oldest to most recent income.
        CursorLoader Loader = new CursorLoader(mContext,
                PortfolioContract.StockIncome
                        .makeUriForStockIncome(symbol),
                PortfolioContract.StockIncome.STOCK_INCOME_COLUMNS,
                null, null, PortfolioContract.StockIncome.COLUMN_SYMBOL);
        return Loader;
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
