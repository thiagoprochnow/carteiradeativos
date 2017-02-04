package br.com.carteira.fragment;

import android.os.Bundle;


import br.com.carteira.listener.AddProductListener;

public abstract class BaseDetailsFragment extends BaseFragment {

    private static final String LOG_TAG = BaseDetailsFragment.class.getSimpleName();

    private AddProductListener mFormProductListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enables the menu
        setHasOptionsMenu(true);
    }
}
