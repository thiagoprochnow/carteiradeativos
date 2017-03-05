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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.com.carteira.R;
import br.com.carteira.adapter.StockQuoteAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.listener.AddProductListener;
import br.com.carteira.listener.DetailsProductListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main fragment screen of Stocks of portfolio, accessed by selecting "Stocks" in navigation menu.
 */
public class StockMainFragment extends BaseFragment implements
        StockQuoteAdapter.StockAdapterOnClickHandler, LoaderManager
        .LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = StockMainFragment.class.getSimpleName();

    @BindView(R.id.stockRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private StockQuoteAdapter mStockQuoteAdapter;
    private AddProductListener mFormProductListener;
    private DetailsProductListener mDetailsProductListener;

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

        if (context instanceof DetailsProductListener) {
            mDetailsProductListener = (DetailsProductListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " Parent Activity must implements DetailsProductListener");
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
                mFormProductListener.onAddProduct(Constants.ProductType.STOCK);
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
        // Launch details activity for clicked stock
        Log.d(LOG_TAG, ": "+symbol);
        mDetailsProductListener.onDetailsProduct(Constants.ProductType.STOCK, symbol);
    }

    @Override
    public void onLongClick(final String symbol) {
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
    }

    // Delete stock and all its information from database
    // This is different then selling a stock, that will maintain some information
    private boolean removeStock(String symbol) {
        int deletedQuote = getActivity().getContentResolver().delete(PortfolioContract.StockQuote
                .makeUriForStockQuote(symbol), null, null);
        int deletedSymbol = getActivity().getContentResolver().delete(PortfolioContract.StockSymbol
                .makeUriForStockSymbol(symbol), null, null);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Will use the table of stock symbols as cursor. StockQuote values will be handled at StockQuoteAdapter.
        return new CursorLoader(mContext,
                PortfolioContract.StockSymbol.URI,
                PortfolioContract.StockSymbol.STOCK_QUOTE_COLUMNS,
                null, null, PortfolioContract.StockSymbol.COLUMN_SYMBOL);
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

        mStockQuoteAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStockQuoteAdapter.setCursor(null);
    }
}
