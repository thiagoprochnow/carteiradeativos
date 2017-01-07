package br.com.carteira.domain;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thipr on 12/30/2016.
 */

public class AcaoService {
    // Função inicial que retorna uma lista virtual de ações da petrobras para preencher a tabela
    public static List<Acao> getAcoes(Context context){
        List<Acao> acoes = new ArrayList<Acao>();
        // Por vinte vezes ele cria uma ação hipotetica para compor a tabela
        // É adicionado o nome, ticker, preço de compra e preço atual da ação
        for (int i = 0; i < 1; i++){
            // For more information on each acao variable, check the Acao.java class
            Acao acao = new Acao();
            acao.ticker = "PETR4";
            acao.stockQuantity = 100;
            acao.boughtPrice = 32.45;
            acao.boughtTotal = acao.stockQuantity * acao.boughtPrice;
            acao.currentPrice = 35.50;
            acao.currentTotal = acao.stockQuantity * acao.currentPrice;
            acao.stockAppreciation = acao.currentTotal - acao.boughtTotal;
            acao.targetPercent = 10.00;
            acao.currentPercent = 20.00;
            acao.totalIncome = 150.00;
            acao.totalGain = acao.stockAppreciation + acao.totalIncome;
            acoes.add(acao);
        }
        return acoes;
    }
}
