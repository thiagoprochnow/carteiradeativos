package br.com.carteira.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.carteira.R;
import br.com.carteira.activity.AddFiiForm;
import br.com.carteira.adapter.FiiAdapter;
import br.com.carteira.domain.Fii;

/**
 * A simple {@link Fragment} subclass.
 * Main fragment screen of Fiis of portfolio, accessed by selecting "Ações" in navigation menu.
 */
public class FiiMainFragment extends BaseFragment {
    List<Fii> mFiis = new ArrayList<Fii>();
    protected RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fii_main, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fiiRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        // Floating Action Button setup
        view.findViewById(R.id.fabFiis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // On click FAB, requests the new fii form with result of inputed values
                Intent intent = new Intent(getContext(),AddFiiForm.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private FiiAdapter.FiiOnClickListener onClickFii() {
        return new FiiAdapter.FiiOnClickListener() {
            // Implement the onClickFii function from the interface of FiiAdapter onClickListener
            @Override
            public void onClickFii(View view, int idx) {
                Fii fii = mFiis.get(idx);
                Toast.makeText(getContext(), "Fii: " + fii.getSymbol(), Toast.LENGTH_SHORT).show();
            }
        };
    }
}
