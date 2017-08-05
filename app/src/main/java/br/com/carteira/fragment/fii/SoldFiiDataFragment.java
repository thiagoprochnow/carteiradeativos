package br.com.carteira.fragment.fii;


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
import br.com.carteira.adapter.fii.SoldFiiDataAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFragment;
import br.com.carteira.listener.ProductListener;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Main fragment screen of Fiis of portfolio, accessed by selecting "Fiis" in navigation menu.
 */
public class SoldFiiDataFragment extends BaseFragment implements
        SoldFiiDataAdapter.FiiAdapterOnClickHandler, LoaderManager
        .LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = SoldFiiDataFragment.class.getSimpleName();

    @BindView(R.id.fiiRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private SoldFiiDataAdapter mSoldFiiAdapter;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        // Clears old adapter and recreates it
        // This is important because of issue that bottom margin of last item was not cleared
        // when a new item was inserted, then we had last view and the one before with altered bottom margin
        mSoldFiiAdapter = new SoldFiiDataAdapter(mContext, this);
        mRecyclerView.setAdapter(mSoldFiiAdapter);
        getActivity().getSupportLoaderManager().restartLoader(Constants.Loaders.SOLD_FII_DATA, null, this);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.SOLD_FII_DATA, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fii_data, container, false);

        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        // Floating Action Button setup
        getActivity().findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onBuyProduct(Constants.ProductType.FII, "");
            }
        });
        mSoldFiiAdapter = new SoldFiiDataAdapter(mContext, this);
        mRecyclerView.setAdapter(mSoldFiiAdapter);
        registerForContextMenu(mRecyclerView);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.SOLD_FII_DATA, null, this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(final String symbol, int id) {
        switch (id){
            case Constants.AdapterClickable.MAIN:
                // Launch details activity for clicked fii
                mFormProductListener.onProductDetails(Constants.ProductType.FII, symbol);
                break;
            case Constants.AdapterClickable.ADD:
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onBuyProduct(Constants.ProductType.FII, symbol);
                break;
            case Constants.AdapterClickable.EDIT:
                mFormProductListener.onEditProduct(Constants.ProductType.FII, symbol);
                break;
            case Constants.AdapterClickable.SELL:
                // This will call the FormActivity with the correct form fragment
                mFormProductListener.onSellProduct(Constants.ProductType.FII, symbol);
                break;
            case Constants.AdapterClickable.DELETE:
                // Show Dialog for user confirmation to delete fii from database
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_fii_title);

                builder.setMessage(R.string.delete_fii_dialog)
                        .setPositiveButton(R.string.delete_confirm, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteFii(symbol);
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
            default:
                Log.d(LOG_TAG, "Invalid id for onClick");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Will use the table of FII symbols as cursor. FiiTransaction values will be handled at FiiDataAdapter.
        // FII_SOLD_LOADER for sold fiis tab
        return new CursorLoader(mContext,
                PortfolioContract.SoldFiiData.URI,
                PortfolioContract.SoldFiiData.SOLD_FII_DATA_COLUMNS,
                null, null, PortfolioContract.SoldFiiData.COLUMN_SYMBOL);
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

        mSoldFiiAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSoldFiiAdapter.setCursor(null);
    }
}
