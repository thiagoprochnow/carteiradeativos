package br.com.carteira.fragment.stock;


import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import br.com.carteira.R;
import br.com.carteira.adapter.StockDetailAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StockDetailsFragment extends BaseFragment implements
        StockDetailAdapter.StockAdapterOnClickHandler, LoaderManager.
        LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = StockDetailsFragment.class.getSimpleName();

    private View mView;

    @BindView(R.id.detailsRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private String id;
    private String mSymbol;

    private StockDetailAdapter mStockDetailAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_stock_details, container, false);

        ButterKnife.bind(this, mView);

        // Gets symbol received from Intent of MainActivity and puts on Bundle for initLoader
        Intent mainActivityIntent = getActivity().getIntent();
        mSymbol = mainActivityIntent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Extra.EXTRA_PRODUCT_SYMBOL, mSymbol);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mStockDetailAdapter = new StockDetailAdapter(mContext, this);
        mRecyclerView.setAdapter(mStockDetailAdapter);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.STOCK_DETAILS, bundle, this);

        return mView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, String id, int type) {
        MenuInflater inflater = getActivity().getMenuInflater();
        this.id = id;
        inflater.inflate(R.menu.detail_item_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_delete_detail:
                // Show Dialog for user confirmation to delete Stock Operation from database
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_stock_detail_title);

                builder.setMessage(R.string.delete_stock_detail_dialog)
                        .setPositiveButton(R.string.delete_confirm, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int onClickId) {
                                deleteStockTransaction(id, mSymbol);
                            }
                        })
                        .setNegativeButton(R.string.delete_cancel, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int onClickId) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
            default:
                Log.d(LOG_TAG, "Wrong menu Id");
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Receives symbol to make query of detail for specific symbol
        String symbol = args.getString(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        String sortOrder = PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " ASC";
        CursorLoader Loader = new CursorLoader(mContext,
                PortfolioContract.StockTransaction
                        .makeUriForStockTransaction(symbol),
                PortfolioContract.StockTransaction.STOCK_TRANSACTION_COLUMNS,
                null, null, sortOrder);
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

        mStockDetailAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStockDetailAdapter.setCursor(null);
    }
}
