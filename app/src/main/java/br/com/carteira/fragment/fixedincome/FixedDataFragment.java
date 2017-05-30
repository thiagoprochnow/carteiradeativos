package br.com.carteira.fragment.fixedincome;


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
import br.com.carteira.adapter.fixedincome.FixedDataAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFragment;
import br.com.carteira.listener.ProductListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main fragment screen of Fixed incomes of portfolio, accessed by selecting "Fixed Income" in navigation menu.
 */
public class FixedDataFragment extends BaseFragment implements
        FixedDataAdapter.FixedAdapterOnClickHandler, LoaderManager
        .LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = FixedDataFragment.class.getSimpleName();

    @BindView(R.id.fixedRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private FixedDataAdapter mFixedDataAdapter;
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
        getActivity().setTitle(R.string.title_fixed);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Clears old adapter and recreates it
        // This is important because of issue that bottom margin of last item was not cleared
        // when a new item was inserted, then we had last view and the one before with altered bottom margin
        mFixedDataAdapter = new FixedDataAdapter(mContext, this);
        mRecyclerView.setAdapter(mFixedDataAdapter);
        getActivity().getSupportLoaderManager().restartLoader(Constants.Loaders.FIXED_DATA, null, this);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.FIXED_DATA, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fixed_data, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        // Floating Action Button setup
        getActivity().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onBuyProduct(Constants.ProductType.FIXED, "");
            }
        });
        mFixedDataAdapter = new FixedDataAdapter(mContext, this);
        mRecyclerView.setAdapter(mFixedDataAdapter);
        registerForContextMenu(mRecyclerView);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.FIXED_DATA, null, this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(String symbol) {
        // Launch details activity for clicked fixed income
        Log.d(LOG_TAG, ": "+symbol);
        mFormProductListener.onProductDetails(Constants.ProductType.FIXED, symbol);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                             ContextMenu.ContextMenuInfo menuInfo, String symbol){
        MenuInflater inflater = getActivity().getMenuInflater();
        this.symbol = symbol;
        inflater.inflate(R.menu.fixed_item_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_item_buy:
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onBuyProduct(Constants.ProductType.FIXED, symbol);
                break;

            case R.id.menu_item_edit:
                mFormProductListener.onEditProduct(Constants.ProductType.FIXED, symbol);
                break;

            case R.id.menu_item_sell:
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onSellProduct(Constants.ProductType.FIXED, symbol);
                break;

            case R.id.menu_item_delete:
                // Show Dialog for user confirmation to delete Fixed incom from database
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_fii_title);

                builder.setMessage(R.string.delete_fixed_dialog)
                        .setPositiveButton(R.string.delete_confirm, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteFixed(symbol);
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
        // Will use the table of fixed income symbols as cursor. FixedTransaction values will be handled at FixedDataAdapter.
        String selection = PortfolioContract.FixedData.COLUMN_STATUS + " = ?";
        String[] selectionArgs = {String.valueOf(Constants.Status.ACTIVE)};
        // FIXED_SOLD_LOADER for sold fixed income tab
        return new CursorLoader(mContext,
                PortfolioContract.FixedData.URI,
                PortfolioContract.FixedData.FIXED_DATA_COLUMNS,
                selection, selectionArgs, PortfolioContract.FixedData.COLUMN_SYMBOL);
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

        mFixedDataAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFixedDataAdapter.setCursor(null);
    }
}
