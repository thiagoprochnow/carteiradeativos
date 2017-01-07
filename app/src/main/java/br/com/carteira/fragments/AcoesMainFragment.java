package br.com.carteira.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
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
import br.com.carteira.adapter.AcaoAdapter;
import br.com.carteira.domain.Acao;
import br.com.carteira.domain.AcaoService;

/**
 * A simple {@link Fragment} subclass.
 */
public class AcoesMainFragment extends BaseFragment {
    List<Acao> acoes = new ArrayList<Acao>();
    protected RecyclerView recyclerView;

    public AcoesMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acoes_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.acaoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        // Floating Action Button setup
        view.findViewById(R.id.fabAcoes).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // For more information on each acao variable, check the Acao.java class
                Acao acao = new Acao();
                acao.ticker = "PETR4";
                acao.stockQuantity = 100;
                acao.boughtPrice = 23.45;
                acao.boughtTotal = acao.stockQuantity * acao.boughtPrice;
                acao.currentPrice = 35.50;
                acao.currentTotal = acao.stockQuantity * acao.currentPrice;
                acao.stockAppreciation = acao.currentTotal - acao.boughtTotal;
                acao.targetPercent = 10.00;
                acao.currentPercent = 20.00;
                acao.totalIncome = 150.00;
                acao.totalGain = acao.stockAppreciation + acao.totalIncome;
                acoes.add(acao);
                taskAcoes();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        taskAcoes();
    }

    private void taskAcoes(){
        //Busca a lista de acoes
        // If acoes list is empty, it will load from AcaoService, else it will add when clicked on FAB button
        if(acoes.size() == 0) {
            this.acoes = AcaoService.getAcoes(getContext());
        }
        recyclerView.setAdapter(new AcaoAdapter(getContext(), acoes, onClickAcao()));
    }

    private AcaoAdapter.AcaoOnClickListener onClickAcao(){
        return new AcaoAdapter.AcaoOnClickListener(){
            @Override
            public void onClickAcao(View view, int idx){
                Acao acao = acoes.get(idx);
                Toast.makeText(getContext(), "Acao: " + acao.ticker, Toast.LENGTH_SHORT).show();
            }
        };
    }
}
