package br.com.carteira.fragment.currency;


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
import br.com.carteira.adapter.currency.CurrencyDataAdapter;
import br.com.carteira.adapter.fii.FiiDataAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFragment;
import br.com.carteira.listener.ProductListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main fragment screen of Currency of portfolio, accessed by selecting "Currency" in navigation menu.
 */
public class CurrencyDataFragment extends BaseFragment implements
        CurrencyDataAdapter.CurrencyAdapterOnClickHandler, LoaderManager
        .LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = CurrencyDataFragment.class.getSimpleName();

    @BindView(R.id.currencyRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private CurrencyDataAdapter mCurrencyDataAdapter;
    private ProductListener mFormProductListener;

    private String symbol;

    // TODO: Precisamos ver uma maneira de otimizar o onAttach. Não vamos colocar em todos XMainFragment
    // Tem que ter um jeito de ficar apenas no BaseFragment
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
        // Set fragment title
        getActivity().setTitle(R.string.title_currency);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency_data, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        // Floating Action Button setup
        view.findViewById(R.id.fabCurrencys).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onBuyProduct(Constants.ProductType.CURRENCY, "");
            }
        });
        mCurrencyDataAdapter = new CurrencyDataAdapter(mContext, this);
        mRecyclerView.setAdapter(mCurrencyDataAdapter);
        registerForContextMenu(mRecyclerView);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.CURRENCY_DATA, null, this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(String symbol) {
        // Launch details activity for clicked currency
        Log.d(LOG_TAG, ": "+symbol);
        mFormProductListener.onProductDetails(Constants.ProductType.CURRENCY, symbol);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                             ContextMenu.ContextMenuInfo menuInfo, String symbol){
        MenuInflater inflater = getActivity().getMenuInflater();
        this.symbol = symbol;
        inflater.inflate(R.menu.currency_item_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_item_buy:
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onBuyProduct(Constants.ProductType.CURRENCY, symbol);
                break;

            case R.id.menu_item_edit:
                mFormProductListener.onEditProduct(Constants.ProductType.CURRENCY, symbol);
                break;

            case R.id.menu_item_sell:
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onSellProduct(Constants.ProductType.CURRENCY, symbol);
                break;

            case R.id.menu_item_delete:
                // Show Dialog for user confirmation to delete currency from database
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_currency_title);

                builder.setMessage(R.string.delete_currency_dialog)
                        .setPositiveButton(R.string.delete_confirm, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteCurrency(symbol);
                            }
                        })
                        .setNegativeButton(R.string.delete_cancel, new DialogInterface
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
        // Will use the table of currency symbols as cursor. CurrencyTransaction values will be handled at CurrencyDataAdapter.
        String selection = PortfolioContract.CurrencyData.COLUMN_STATUS + " = ?";
        String[] selectionArgs = {String.valueOf(Constants.Status.ACTIVE)};
        // CURRENCY_SOLD_LOADER for sold currency tab
        return new CursorLoader(mContext,
                PortfolioContract.CurrencyData.URI,
                PortfolioContract.CurrencyData.CURRENCY_DATA_COLUMNS,
                selection, selectionArgs, PortfolioContract.CurrencyData.COLUMN_SYMBOL);
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

        mCurrencyDataAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCurrencyDataAdapter.setCursor(null);
    }
}
