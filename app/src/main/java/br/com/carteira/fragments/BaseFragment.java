package br.com.carteira.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by thipr on 11/9/2016.
 */

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Keep the fragment alive on screen rotation or other changes
        setRetainInstance(true);
    }
}
