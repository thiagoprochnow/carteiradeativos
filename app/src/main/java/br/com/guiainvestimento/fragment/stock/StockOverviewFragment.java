package br.com.guiainvestimento.fragment.stock;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.adapter.stock.StockOverviewAdapter;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFragment;
import br.com.guiainvestimento.listener.ProductListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main fragment screen of Stocks of portfolio, accessed by selecting "Stocks" in navigation menu.
 */
public class StockOverviewFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = StockOverviewFragment.class.getSimpleName();

    @BindView(R.id.stockRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private StockOverviewAdapter mStockOverviewAdapter;
    private ProductListener mFormProductListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProductListener) {
            mFormProductListener = (ProductListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " Parent Activity must implements ProductListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStockOverviewAdapter = new StockOverviewAdapter(mContext);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // After new current price is get, reload Overview view
                mStockOverviewAdapter.notifyDataSetChanged();
            }
        };
        if(mContext != null && mStockOverviewAdapter != null) {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, new IntentFilter(Constants.Receiver.STOCK));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_overview, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        getActivity().findViewById(R.id.fab).setVisibility(View.VISIBLE);
        // Floating Action Button setup
        getActivity().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onBuyProduct(Constants.ProductType.STOCK, "");
            }
        });
        mRecyclerView.setAdapter(mStockOverviewAdapter);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.STOCK_OVERVIEW, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                PortfolioContract.StockPortfolio.URI,
                PortfolioContract.StockPortfolio.STOCK_PORTFOLIO_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        if (data != null && mEmptyListTextView != null) {
            if (data.getCount() != 0 && data.getDouble(data.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_BUY_TOTAL)) > 0) {
                mEmptyListTextView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            } else {
                mEmptyListTextView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }
        }

        mStockOverviewAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStockOverviewAdapter.setCursor(null);
    }
}
