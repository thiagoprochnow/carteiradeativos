package br.com.carteira.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.carteira.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarteiraMainFragment extends BaseFragment {
    private static final String TAG = "CarteiraMainFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "Fragment carregado");
        View view = inflater.inflate(R.layout.fragment_carteira_main, container, false);
        return view;
    }

}
