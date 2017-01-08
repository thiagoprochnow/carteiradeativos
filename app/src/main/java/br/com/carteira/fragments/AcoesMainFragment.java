package br.com.carteira.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
public class AcoesMainFragment extends BaseFragment{
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
                showAddAcaoDialog();
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
        // Search for the stock list List<Acao>
        // If acoes list is empty, it will load from AcaoService, else it will add when clicked on FAB button
        if(acoes.size() == 0) {
            this.acoes = AcaoService.getAcoes(getContext());
        }
        recyclerView.setAdapter(new AcaoAdapter(getContext(), acoes, onClickAcao()));
    }

    private AcaoAdapter.AcaoOnClickListener onClickAcao(){
        return new AcaoAdapter.AcaoOnClickListener(){
            // Implement the onClickAcao function from the interface of AcaoAdapter onClickListener
            @Override
            public void onClickAcao(View view, int idx){
                Acao acao = acoes.get(idx);
                Toast.makeText(getContext(), "Acao: " + acao.ticker, Toast.LENGTH_SHORT).show();
            }
        };
    }

    // Function to show the add stock dialog fragment
    public void showAddAcaoDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddAcaoDialogFragment();
        // Asks the new DialogFragment for a result with a Request_code = 0
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "AddAcaoDialogFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // Request code 0 is the return of the Dialog Fragment after filling the EditText field and pressing positive buttons
        if(requestCode == 0){
            String teste = intent.getStringExtra("codigoAcao");
            // Add as a new stock to the portfolio or sums to already existing one.
            boolean success = addStock(intent);
            if(success){
                Toast.makeText(getContext(), R.string.add_acao_success, Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(getContext(), R.string.add_acao_fail, Toast.LENGTH_SHORT);
            }
        }
    }

    public boolean addStock(Intent intent) {
        // Parse the information of the intent sent from the DialogFragment to add the stock
        String inputTicker = intent.getStringExtra("inputTicker");
        int inputQuantity = Integer.parseInt(intent.getStringExtra("inputQuantity"));
        double inputBuyPrice = Double.parseDouble(intent.getStringExtra("inputBuyPrice"));
        double inputObjective = Double.parseDouble(intent.getStringExtra("inputObjective"));

        // For more information on each acao variable, check the Acao.java class
        Acao acao = new Acao();
        acao.ticker = inputTicker;
        acao.stockQuantity = inputQuantity;
        acao.boughtPrice = inputBuyPrice;
        acao.boughtTotal = acao.stockQuantity * acao.boughtPrice;
        acao.currentPrice = 35.50;
        acao.currentTotal = acao.stockQuantity * acao.currentPrice;
        acao.stockAppreciation = acao.currentTotal - acao.boughtTotal;
        acao.targetPercent = inputObjective;
        acao.currentPercent = 20.00;
        acao.totalIncome = 150.00;
        acao.totalGain = acao.stockAppreciation + acao.totalIncome;
        acoes.add(acao);
        taskAcoes();
        return true;
    }
}
