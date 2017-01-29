package br.com.carteira.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.carteira.R;

public class PortfolioMainFragment extends BaseFragment {
    private static final String LOG_TAG = PortfolioMainFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set fragment title
        getActivity().setTitle(R.string.title_complete_portfolio);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(LOG_TAG, "Fragment loaded");
        View view = inflater.inflate(R.layout.fragment_portfolio_main, container, false);
        return view;
    }

}
