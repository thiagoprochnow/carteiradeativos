package br.com.guiainvestimento.fragment.currency;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.adapter.currency.CurrencyDetailAdapter;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.BaseFragment;
import br.com.guiainvestimento.listener.TransactionListener;
import br.com.guiainvestimento.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;


public class CurrencyDetailsFragment extends BaseFragment implements
        CurrencyDetailAdapter.CurrencyAdapterOnClickHandler, LoaderManager.
        LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = CurrencyDetailsFragment.class.getSimpleName();

    private View mView;

    @BindView(R.id.detailsRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private String id;
    private String mSymbol;

    private CurrencyDetailAdapter mCurrencyDetailAdapter;
    private TransactionListener mFormTransactionListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TransactionListener) {
            mFormTransactionListener = (TransactionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " Parent Activity must implements ProductListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrencyDetailAdapter = new CurrencyDetailAdapter(mContext, this);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // After new current price is get, reload Overview view
                mCurrencyDetailAdapter.notifyDataSetChanged();
            }
        };
        if (mContext != null && mCurrencyDetailAdapter != null) {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, new IntentFilter(Constants.Receiver.CURRENCY));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_currency_details, container, false);

        ButterKnife.bind(this, mView);

        // Gets symbol received from Intent of MainActivity and puts on Bundle for initLoader
        Intent mainActivityIntent = getActivity().getIntent();
        mSymbol = mainActivityIntent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Extra.EXTRA_PRODUCT_SYMBOL, mSymbol);
        String title = Util.convertCurrencySymbol(mContext, mSymbol);
        getActivity().setTitle(title);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mCurrencyDetailAdapter);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.CURRENCY_DETAILS, bundle, this);

        return mView;
    }

    @Override
    public void onClick(final String id, int type) {
        switch (type){
            case Constants.AdapterClickable.DELETE:
                // Show Dialog for user confirmation to delete Currency Operation from database
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_detail_title);

                builder.setMessage(R.string.delete_detail_dialog)
                        .setPositiveButton(R.string.delete_confirm, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int onClickId) {
                                deleteCurrencyTransaction(id, mSymbol);
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
            case Constants.AdapterClickable.EDIT:
                mFormTransactionListener.onEditTransaction(Constants.ProductType.CURRENCY, id);
                break;
            default:
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Receives symbol to make query of detail for specific symbol
        String symbol = args.getString(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        String sortOrder = PortfolioContract.CurrencyTransaction.COLUMN_TIMESTAMP + " ASC";
        CursorLoader Loader = new CursorLoader(mContext,
                PortfolioContract.CurrencyTransaction
                        .makeUriForCurrencyTransaction(symbol),
                PortfolioContract.CurrencyTransaction.CURRENCY_TRANSACTION_COLUMNS,
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

        mCurrencyDetailAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCurrencyDetailAdapter.setCursor(null);
    }
}
