package br.com.carteira.fragment.treasury;


import android.content.BroadcastReceiver;
import android.content.ContentValues;
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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.carteira.R;
import br.com.carteira.adapter.treasury.TreasuryIncomeMainAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFragment;
import br.com.carteira.listener.IncomeDetailsListener;
import butterknife.BindView;
import butterknife.ButterKnife;


public class TreasuryIncomesMainFragment extends BaseFragment implements
        TreasuryIncomeMainAdapter.TreasuryAdapterOnClickHandler, LoaderManager.
        LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = TreasuryIncomesMainFragment.class.getSimpleName();

    private IncomeDetailsListener mIncomeDetailsListener;

    @BindView(R.id.incomesRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private TreasuryIncomeMainAdapter mTreasuryIncomeMainAdapter;

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
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // After new current price is get, reload Overview view
                mTreasuryIncomeMainAdapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, new IntentFilter(Constants.Receiver.TREASURY));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Clears old adapter and recreates it
        // This is important because of issue that bottom margin of last item was not cleared
        // when a new item was inserted, then we had last view and the one before with altered bottom margin
        mTreasuryIncomeMainAdapter = new TreasuryIncomeMainAdapter(mContext, this);
        mRecyclerView.setAdapter(mTreasuryIncomeMainAdapter);
        getActivity().getSupportLoaderManager().restartLoader(Constants.Loaders.TREASURY_INCOME, null, this);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.TREASURY_INCOME, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_treasury_incomes, container, false);

        ButterKnife.bind(this, view);

        setUserVisibleHint(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mTreasuryIncomeMainAdapter = new TreasuryIncomeMainAdapter(mContext, this);
        mRecyclerView.setAdapter(mTreasuryIncomeMainAdapter);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.TREASURY_INCOME, null, this);

        return view;
    }

    @Override
    public void onClick(final String id, int type, int operation) {
        switch (operation) {
            case Constants.AdapterClickable.DELETE:
                // Show Dialog for user confirmation to delete Treasury Income from database
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_income_title);

                builder.setMessage(R.string.delete_income_dialog)
                        .setPositiveButton(R.string.delete_confirm, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int onClickId) {
                                deleteTreasuryIncome(id, null);
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
            case Constants.AdapterClickable.MAIN:
                Log.d(LOG_TAG, "ID: " + id);
                mIncomeDetailsListener.onIncomeDetails(type, id);
                break;
            default:
                Log.d(LOG_TAG, "Invalid onClick");
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = PortfolioContract.TreasuryTransaction.COLUMN_TIMESTAMP + " ASC";
        CursorLoader Loader = new CursorLoader(mContext,
                PortfolioContract.TreasuryIncome.URI,
                PortfolioContract.TreasuryIncome.TREASURY_INCOME_COLUMNS,
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

        mTreasuryIncomeMainAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTreasuryIncomeMainAdapter.setCursor(null);
    }
}
