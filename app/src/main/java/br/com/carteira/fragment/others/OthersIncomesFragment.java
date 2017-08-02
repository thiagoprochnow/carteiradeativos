package br.com.carteira.fragment.others;


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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.carteira.R;
import br.com.carteira.adapter.others.OthersIncomeAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFragment;
import br.com.carteira.listener.IncomeDetailsListener;
import butterknife.BindView;
import butterknife.ButterKnife;


public class OthersIncomesFragment extends BaseFragment implements
        OthersIncomeAdapter.OthersAdapterOnClickHandler, LoaderManager.
        LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = OthersIncomesFragment.class.getSimpleName();

    private IncomeDetailsListener mIncomeDetailsListener;

    @BindView(R.id.incomesRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private OthersIncomeAdapter mOthersIncomeAdapter;

    private String mSymbol;

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
                mOthersIncomeAdapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, new IntentFilter(Constants.Receiver.OTHERS));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_others_incomes, container, false);

        ButterKnife.bind(this, view);

        // Gets symbol received from Intent of MainActivity and puts on Bundle for initLoader
        Intent mainActivityIntent = getActivity().getIntent();
        mSymbol = mainActivityIntent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.Extra.EXTRA_PRODUCT_SYMBOL, mSymbol);
        setUserVisibleHint(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mOthersIncomeAdapter = new OthersIncomeAdapter(mContext, this);
        mRecyclerView.setAdapter(mOthersIncomeAdapter);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.OTHERS_INCOME, bundle, this);

        return view;
    }

    @Override
    public void onClick(final String id, int type, int operation) {
        switch (operation) {
            case Constants.AdapterClickable.DELETE:
                // Show Dialog for user confirmation to delete Others Income from database
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_income_title);

                builder.setMessage(R.string.delete_income_dialog)
                        .setPositiveButton(R.string.delete_confirm, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int onClickId) {
                                deleteOthersIncome(id, mSymbol);
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
        // Receives symbol to make query of incomes for specific symbol
        String symbol = args.getString(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        String sortOrder = PortfolioContract.OthersTransaction.COLUMN_TIMESTAMP + " ASC";
        CursorLoader Loader = new CursorLoader(mContext,
                PortfolioContract.OthersIncome
                        .makeUriForOthersIncome(symbol),
                PortfolioContract.OthersIncome.OTHERS_INCOME_COLUMNS,
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

        mOthersIncomeAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mOthersIncomeAdapter.setCursor(null);
    }

    // Change the income of "id" from dividend to JCP or JCP to dividend
    public void changeIncomeType(String id, String symbol, int type){
        String selection = PortfolioContract.OthersIncome._ID + " = ? AND "
                + PortfolioContract.OthersIncome.COLUMN_SYMBOL + " = ?";
        String[] selectionArguments = {id, symbol};

        Cursor queryCursor = mContext.getContentResolver().query(PortfolioContract.OthersIncome.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            double grossIncome = queryCursor.getDouble((queryCursor.getColumnIndex(PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL)));
            double tax = grossIncome*0.15;
            double netIncome = grossIncome-tax;

            String updateSelection = PortfolioContract.OthersIncome._ID + " = ?";
            String[] updatedSelectionArguments = {id};

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_RECEIVE_TOTAL, grossIncome);
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_TAX, tax);
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_RECEIVE_LIQUID, netIncome);
            incomeCV.put(PortfolioContract.OthersIncome.COLUMN_TYPE, type);

            // Update value on incomes table
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.OthersIncome.URI,
                    incomeCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                updateOthersData(mSymbol, -1);
                Log.d(LOG_TAG, "updateOthersIncomes successfully updated");
            } else {
                Log.d(LOG_TAG, "updateOthersIncomes failed update");
            }
        } else {
            Log.d(LOG_TAG, "No income found for this ID and Symbol");
        }
    }
}
