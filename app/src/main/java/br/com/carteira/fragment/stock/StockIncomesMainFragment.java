package br.com.carteira.fragment.stock;


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
import br.com.carteira.adapter.stock.StockIncomeMainAdapter;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFragment;
import br.com.carteira.listener.IncomeDetailsListener;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StockIncomesMainFragment extends BaseFragment implements
        StockIncomeMainAdapter.StockAdapterOnClickHandler, LoaderManager.
        LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = StockIncomesMainFragment.class.getSimpleName();

    private IncomeDetailsListener mIncomeDetailsListener;

    @BindView(R.id.incomesRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    private StockIncomeMainAdapter mStockIncomeMainAdapter;

    private String id;

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
                mStockIncomeMainAdapter.notifyDataSetChanged();
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, new IntentFilter(Constants.Receiver.STOCK));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_incomes, container, false);

        ButterKnife.bind(this, view);

        setUserVisibleHint(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mStockIncomeMainAdapter = new StockIncomeMainAdapter(mContext, this);
        mRecyclerView.setAdapter(mStockIncomeMainAdapter);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.STOCK_INCOME, null, this);

        return view;
    }

    @Override
    public void onClick(String id, int type) {
        Log.d(LOG_TAG, "ID: " + id);
        mIncomeDetailsListener.onIncomeDetails(type, id);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, String id, int type) {
        MenuInflater inflater = getActivity().getMenuInflater();
        this.id = id;
        inflater.inflate(R.menu.income_item_menu, menu);
        if (type == Constants.IncomeType.JCP){
            menu.findItem(R.id.menu_item_change_jcp).setVisible(false);
            menu.findItem(R.id.menu_item_change_dividend).setVisible(true);
        } else {
            menu.findItem(R.id.menu_item_change_dividend).setVisible(false);
            menu.findItem(R.id.menu_item_change_jcp).setVisible(true);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_delete_income:
                // Show Dialog for user confirmation to delete Stock Income from database
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.delete_stock_income_title);

                builder.setMessage(R.string.delete_stock_income_dialog)
                        .setPositiveButton(R.string.delete_confirm, new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int onClickId) {
                                deleteStockIncome(id, null);
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
            case R.id.menu_item_change_jcp:
                changeIncomeType(id, Constants.IncomeType.JCP);
                break;
            case R.id.menu_item_change_dividend:
                changeIncomeType(id, Constants.IncomeType.DIVIDEND);
                break;
            default:
                Log.d(LOG_TAG, "Wrong menu Id");
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " ASC";
        CursorLoader Loader = new CursorLoader(mContext,
                PortfolioContract.StockIncome.URI,
                PortfolioContract.StockIncome.STOCK_INCOME_COLUMNS,
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

        mStockIncomeMainAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStockIncomeMainAdapter.setCursor(null);
    }

    // Change the income of "id" from dividend to JCP or JCP to dividend
    public void changeIncomeType(String id, int type){
        // Get symbol for that id
        String symbol = "";
        String selectionData = PortfolioContract.StockIncome._ID + " = ? ";
        String[] selectionDataArguments = {id};
        String[] affectedColumn = {PortfolioContract.StockIncome.COLUMN_SYMBOL};
        Cursor cursor = mContext.getContentResolver().query(
                PortfolioContract.StockIncome.URI,
                affectedColumn, selectionData, selectionDataArguments, null);

        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            symbol = cursor.getString(0);
        } else {
            Log.d(LOG_TAG, "No symbol for for that income");
        }

        String selection = PortfolioContract.StockIncome._ID + " = ?";
        String[] selectionArguments = {id};

        Cursor queryCursor = mContext.getContentResolver().query(PortfolioContract.StockIncome.URI,
                null, selection, selectionArguments, null);
        if(queryCursor.getCount() > 0){
            queryCursor.moveToFirst();
            double perStock = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_PER_STOCK));
            double quantity = queryCursor.getDouble(queryCursor.getColumnIndex(PortfolioContract.StockIncome.COLUMN_AFFECTED_QUANTITY));
            double grossIncome = perStock*quantity;
            double tax = 0;
            double netIncome = 0;
            if (type == Constants.IncomeType.JCP){
                // Change to JCP
                tax = grossIncome*0.15;
                netIncome = grossIncome-tax;
            } else {
                // Change to Dividend
                tax = 0;
                netIncome = grossIncome;
            }

            String updateSelection = PortfolioContract.StockIncome._ID + " = ?";
            String[] updatedSelectionArguments = {id};

            ContentValues incomeCV = new ContentValues();
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_TOTAL, grossIncome);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_TAX, tax);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_RECEIVE_LIQUID, netIncome);
            incomeCV.put(PortfolioContract.StockIncome.COLUMN_TYPE, type);

            // Update value on incomes table
            int updatedRows = mContext.getContentResolver().update(
                    PortfolioContract.StockIncome.URI,
                    incomeCV, updateSelection, updatedSelectionArguments);
            // Log update success/fail result
            if (updatedRows > 0){
                updateStockData(symbol, -1, -1);
                Log.d(LOG_TAG, "updateStockIncomes successfully updated");
            } else {
                Log.d(LOG_TAG, "updateStockIncomes failed update");
            }
        } else {
            Log.d(LOG_TAG, "No income found for this ID and Symbol");
        }
    }
}
