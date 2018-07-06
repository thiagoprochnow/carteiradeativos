package br.com.guiainvestimento.fragment;


import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.guiainvestimento.BuildConfig;
import br.com.guiainvestimento.R;

public class FaqFragment extends BaseFragment{
    private static final String LOG_TAG = FaqFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        return view;
    }
}
