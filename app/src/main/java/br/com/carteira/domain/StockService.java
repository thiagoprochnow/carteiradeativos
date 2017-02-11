package br.com.carteira.domain;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class StockService {
    // TODO: Translate the comments here
    // Função inicial que retorna uma lista virtual de ações da petrobras para preencher a tabela
    public static List<Stock> getStocks(Context context) {
        List<Stock> stocks = new ArrayList<Stock>();
        // Por vinte vezes ele cria uma ação hipotetica para compor a tabela
        // É adicionado o nome, symbol, preço de compra e preço atual da ação
        for (int i = 0; i < 1; i++) {
            // For more information on each stock variable, check the Stock.java class
            Stock stock = new Stock();
            stock.setSymbol("PETR4");
            stock.setStockQuantity(100);
            stock.setBoughtPrice(32.45);
            stock.setBoughtTotal(stock.getStockQuantity() * stock.getBoughtPrice());
            stock.setCurrentPrice(35.50);
            stock.setCurrentTotal(stock.getStockQuantity() * stock.getCurrentPrice());
            stock.setStockAppreciation(stock.getCurrentTotal() - stock.getBoughtTotal());
            stock.setObjectivePercent(10.00);
            stock.setCurrentPercent(20.00);
            stock.setTotalIncome(150.00);
            stock.setTotalGain(stock.getStockAppreciation() + stock.getTotalIncome());
            stocks.add(stock);
        }
        return stocks;
    }
}
