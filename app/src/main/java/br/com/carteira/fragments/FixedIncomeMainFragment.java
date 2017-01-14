package br.com.carteira.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.carteira.R;
import br.com.carteira.api.service.StockIntentService;

/**
 * A simple {@link Fragment} subclass.
 */
public class FixedIncomeMainFragment extends Fragment {


    public FixedIncomeMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Run the initialize task service so that some stocks appear upon an empty database
        Intent mServiceIntent = new Intent(getActivity(), StockIntentService.class);
        mServiceIntent.putExtra(StockIntentService.ADD_SYMBOL, "YHOO");

        getActivity().startService(mServiceIntent);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fixed_income_main, container, false);
    }

}
