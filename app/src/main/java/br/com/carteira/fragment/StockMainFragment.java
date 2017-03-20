package br.com.carteira.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
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

import br.com.carteira.R;
import br.com.carteira.adapter.StockPortfolioAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.listener.AddProductListener;
import br.com.carteira.listener.ProductDetailsListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main fragment screen of Stocks of portfolio, accessed by selecting "Stocks" in navigation menu.
 */
public class StockMainFragment extends BaseFragment implements
        StockPortfolioAdapter.StockAdapterOnClickHandler, LoaderManager
        .LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = StockMainFragment.class.getSimpleName();

    @BindView(R.id.stockRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private StockPortfolioAdapter mStockPortfolioAdapter;
    private AddProductListener mFormProductListener;
    private ProductDetailsListener mProductDetailsListener;

    private String symbol;

    // Loader IDs
    private static final int STOCK_LOADER = 0;

    // TODO: Precisamos ver uma maneira de otimizar o onAttach. Não vamos colocar em todos XMainFragment
    // Tem que ter um jeito de ficar apenas no BaseFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddProductListener) {
            mFormProductListener = (AddProductListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " Parent Activity must implements AddProductListener");
        }

        if (context instanceof ProductDetailsListener) {
            mProductDetailsListener = (ProductDetailsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " Parent Activity must implements ProductDetailsListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set fragment title
        getActivity().setTitle(R.string.title_stocks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks_main, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        // Floating Action Button setup
        view.findViewById(R.id.fabStocks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onBuyProduct(Constants.ProductType.STOCK, "");
            }
        });
        mStockPortfolioAdapter = new StockPortfolioAdapter(mContext, this);
        mRecyclerView.setAdapter(mStockPortfolioAdapter);
        registerForContextMenu(mRecyclerView);
        getActivity().getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(String symbol) {
        // Launch details activity for clicked stock
        Log.d(LOG_TAG, ": "+symbol);
        mProductDetailsListener.onProductDetails(Constants.ProductType.STOCK, symbol);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                             ContextMenu.ContextMenuInfo menuInfo, String symbol){
        MenuInflater inflater = getActivity().getMenuInflater();
        this.symbol = symbol;
        inflater.inflate(R.menu.stock_item_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_item_buy:
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onBuyProduct(Constants.ProductType.STOCK, symbol);
                break;

            case R.id.menu_item_sell:
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onSellProduct(Constants.ProductType.STOCK, symbol);
                break;

            case R.id.menu_item_delete:
                // Show Dialog for user confirmation to delete Stock from database
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_stock_title);

                builder.setMessage(R.string.delete_stock_dialog)
                        .setPositiveButton(R.string.delete_stock_confirm, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeStock(symbol);
                            }
                        })
                        .setNegativeButton(R.string.delete_stock_cancel, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Will use the table of stock symbols as cursor. StockTransaction values will be handled at StockPortfolioAdapter.
        return new CursorLoader(mContext,
                PortfolioContract.StockData.URI,
                PortfolioContract.StockData.STOCK_DATA_COLUMNS,
                null, null, PortfolioContract.StockData.COLUMN_SYMBOL);
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

        mStockPortfolioAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStockPortfolioAdapter.setCursor(null);
    }
}
