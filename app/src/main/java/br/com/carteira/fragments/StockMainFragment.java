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
import br.com.carteira.adapter.StockAdapter;
import br.com.carteira.domain.Stock;
import br.com.carteira.domain.StockService;

/**
 * A simple {@link Fragment} subclass.
 * Main fragment screen of Stocks of portfolio, accessed by selecting "Ações" in navigation menu.
 */
public class StockMainFragment extends BaseFragment{
    List<Stock> stocks = new ArrayList<Stock>();
    protected RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stocks_main, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.stockRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        // Floating Action Button setup
        view.findViewById(R.id.fabStocks).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showAddStockDialog();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        taskStock();
    }

    private void taskStock(){
        // Search for the stock list List<Stock>
        // If stocks list is empty, it will load from StockService, else it will add when clicked on FAB button
        if(stocks.size() == 0) {
            this.stocks = StockService.getStocks(getContext());
        }
        recyclerView.setAdapter(new StockAdapter(getContext(), stocks, onClickStock()));
    }

    private StockAdapter.StockOnClickListener onClickStock(){
        return new StockAdapter.StockOnClickListener(){
            // Implement the onClickStock function from the interface of StockAdapter onClickListener
            @Override
            public void onClickStock(View view, int idx){
                Stock stock = stocks.get(idx);
                Toast.makeText(getContext(), "Stock: " + stock.getTicker(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    // Function to show the add stock dialog fragment
    public void showAddStockDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new AddStockDialogFragment();
        // Asks the new DialogFragment for a result with a Request_code = 0
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "AddStockDialogFragment");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // Request code 0 is the return of the Dialog Fragment after filling the EditText field and pressing positive buttons
        if(requestCode == 0){
            // Add as a new stock to the portfolio or sums to already existing one.
            if(addStock(intent)){
                Toast.makeText(getContext(), R.string.add_stock_success, Toast.LENGTH_SHORT);
            } else {
                Toast.makeText(getContext(), R.string.add_stock_fail, Toast.LENGTH_SHORT);
            }
        }
    }

    public boolean addStock(Intent intent) {
        // Parse the information of the intent sent from the DialogFragment to add the stock
        String inputTicker = intent.getStringExtra("inputTicker");
        int inputQuantity = Integer.parseInt(intent.getStringExtra("inputQuantity"));
        double inputBuyPrice = Double.parseDouble(intent.getStringExtra("inputBuyPrice"));
        double inputObjective = Double.parseDouble(intent.getStringExtra("inputObjective"));

        // For more information on each stock variable, check the Stock.java class
        Stock stock = new Stock();
        stock.setTicker(inputTicker);
        stock.setStockQuantity(inputQuantity);
        stock.setBoughtPrice(inputBuyPrice);
        stock.setBoughtTotal(stock.getStockQuantity() * stock.getBoughtPrice());
        stock.setCurrentPrice(35.50);
        stock.setCurrentTotal(stock.getStockQuantity() * stock.getCurrentPrice());
        stock.setStockAppreciation(stock.getCurrentTotal() - stock.getBoughtTotal());
        stock.setObjectivePercent(inputObjective);
        stock.setCurrentPercent(20.00);
        stock.setTotalIncome(150.00);
        stock.setTotalGain(stock.getStockAppreciation() + stock.getTotalIncome());
        stocks.add(stock);
        taskStock();
        return true;
    }
}
