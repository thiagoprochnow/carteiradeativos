package br.com.carteira.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import br.com.carteira.adapter.FiiAdapter;
import br.com.carteira.domain.Fii;
import br.com.carteira.domain.FiiService;

/**
 * A simple {@link Fragment} subclass.
 * Main fragment screen of Fiis of portfolio, accessed by selecting "Ações" in navigation menu.
 */
public class FiiMainFragment extends BaseFragment{
    List<Fii> fiis = new ArrayList<Fii>();
    protected RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fii_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fiiRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        // Floating Action Button setup
        view.findViewById(R.id.fabFiis).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showAddFiiDialog();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        taskFii();
    }

    private void taskFii(){
        // Search for the fii list List<Fii>
        // If fiis list is empty, it will load from FiiService, else it will add when clicked on FAB button
        if(fiis.size() == 0) {
            this.fiis = FiiService.getFiis(getContext());
        }
        recyclerView.setAdapter(new FiiAdapter(getContext(), fiis, onClickFii()));
    }

    private FiiAdapter.FiiOnClickListener onClickFii(){
        return new FiiAdapter.FiiOnClickListener(){
            // Implement the onClickFii function from the interface of FiiAdapter onClickListener
            @Override
            public void onClickFii(View view, int idx){
                Fii fii = fiis.get(idx);
                Toast.makeText(getContext(), "Fii: " + fii.getTicker(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    // Function to show the add fii dialog fragment
    public void showAddFiiDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddFiiDialogFragment();
        // Asks the new DialogFragment for a result with a Request_code = 0
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "AddFiiDialogFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // Request code 0 is the return of the Dialog Fragment after filling the EditText field and pressing positive buttons
        if(requestCode == 0){
            // Add as a new fii to the portfolio or sums to already existing one.
            if(addFii(intent)){
                Toast.makeText(getContext(), R.string.add_fii_success, Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(getContext(), R.string.add_fii_fail, Toast.LENGTH_SHORT);
            }
        }
    }

    public boolean addFii(Intent intent) {
        // Parse the information of the intent sent from the DialogFragment to add the fii
        String inputTicker = intent.getStringExtra("inputTicker");
        int inputQuantity = Integer.parseInt(intent.getStringExtra("inputQuantity"));
        double inputBuyPrice = Double.parseDouble(intent.getStringExtra("inputBuyPrice"));
        double inputObjective = Double.parseDouble(intent.getStringExtra("inputObjective"));

        // For more information on each fii variable, check the Fii.java class
        Fii fii = new Fii();
        fii.setTicker(inputTicker);
        fii.setFiiQuantity(inputQuantity);
        fii.setBoughtPrice(inputBuyPrice);
        fii.setBoughtTotal(fii.getFiiQuantity() * fii.getBoughtPrice());
        fii.setCurrentPrice(35.50);
        fii.setCurrentTotal(fii.getFiiQuantity() * fii.getCurrentPrice());
        fii.setFiiAppreciation(fii.getCurrentTotal() - fii.getBoughtTotal());
        fii.setObjectivePercent(inputObjective);
        fii.setCurrentPercent(20.00);
        fii.setTotalIncome(150.00);
        fii.setTotalGain(fii.getFiiAppreciation() + fii.getTotalIncome());
        fiis.add(fii);
        taskFii();
        return true;
    }
}
