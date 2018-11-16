package br.com.guiainvestimento.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.activity.MainActivity;
import br.com.guiainvestimento.adapter.PortfolioAdapter;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PortfolioMainFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = PortfolioMainFragment.class.getSimpleName();

    @BindView(R.id.portfolioRecyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.empty_list_text)
    protected TextView mEmptyListTextView;

    @BindView(R.id.premium_cardview)
    protected CardView mPremiumCardView;

    @BindView(R.id.premium_sign_button)
    protected LinearLayout mSignButton;

    private PortfolioAdapter mPortfolioAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPortfolioAdapter = new PortfolioAdapter(mContext);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // After new current price is get, reload Overview view
                mPortfolioAdapter.notifyDataSetChanged();
            }
        };
        if (mContext != null && mPortfolioAdapter != null) {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(receiver, new IntentFilter(Constants.Receiver.PORTFOLIO));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_portfolio_main, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mPremiumCardView.setVisibility(CardView.VISIBLE);
        mSignButton.setOnClickListener(mSignButtonOnClick);

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        mRecyclerView.setAdapter(mPortfolioAdapter);
        getActivity().getSupportLoaderManager().initLoader(Constants.Loaders.PORTFOLIO, null, this);

        if (isPremium()){
            hidePremium();
        } else {
            showPremium();
        }

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                PortfolioContract.Portfolio.URI,
                PortfolioContract.Portfolio.PORTFOLIO_COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && mEmptyListTextView != null) {
            if (data.getCount() != 0) {
                mEmptyListTextView.setVisibility(View.GONE);
                if (!isPremium()) {
                    showPremium();
                }
            } else {
                mEmptyListTextView.setVisibility(View.VISIBLE);
                hidePremium();
            }
        }

        mPortfolioAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPortfolioAdapter.setCursor(null);
    }

    View.OnClickListener mSignButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            replacePremiumFragment();
        }
    };

    public boolean isPremium(){
        MainActivity main = ((MainActivity)getActivity());
        if(main != null) {
            return main.isPremium();
        } else {
            return true;
        }
    }

    public void hidePremium(){
        mPremiumCardView.setVisibility(CardView.GONE);
    }

    public void showPremium(){
        mPremiumCardView.setVisibility(CardView.VISIBLE);
    }

    private void replacePremiumFragment(){
        getActivity().setTitle(R.string.title_premium_edition);
        ((MainActivity)getActivity()).replaceFragment(new PremiumEditionFragment());
    }
}
