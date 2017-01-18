package br.com.carteira.domain;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class FiiService {
    // TODO: Translate comments here
    // Função inicial que retorna uma lista virtual de ações da petrobras para preencher a tabela
    public static List<Fii> getFiis(Context context) {
        List<Fii> fiis = new ArrayList<Fii>();
        // Por vinte vezes ele cria uma ação hipotetica para compor a tabela
        // É adicionado o nome, ticker, preço de compra e preço atual da ação
        for (int i = 0; i < 1; i++) {
            // For more information on each stock variable, check the Stock.java class
            Fii fii = new Fii();
            fii.setTicker("HTMX11B");
            fii.setFiiQuantity(100);
            fii.setBoughtPrice(32.45);
            fii.setBoughtTotal(fii.getFiiQuantity() * fii.getBoughtPrice());
            fii.setCurrentPrice(35.50);
            fii.setCurrentTotal(fii.getFiiQuantity() * fii.getCurrentPrice());
            fii.setFiiAppreciation(fii.getCurrentTotal() - fii.getBoughtTotal());
            fii.setObjectivePercent(10.00);
            fii.setCurrentPercent(20.00);
            fii.setTotalIncome(150.00);
            fii.setTotalGain(fii.getFiiAppreciation() + fii.getTotalIncome());
            fiis.add(fii);
        }
        return fiis;
    }
}
