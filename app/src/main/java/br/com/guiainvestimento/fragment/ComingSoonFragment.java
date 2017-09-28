package br.com.guiainvestimento.fragment;


import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.guiainvestimento.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ComingSoonFragment extends BaseFragment{
    private static final String LOG_TAG = ComingSoonFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        ButterKnife.bind(this, view);

        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        return view;
    }
}
