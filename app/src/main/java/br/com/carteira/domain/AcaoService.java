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
            Acao acao = new Acao();
            acao.ticker = "PETR4";
            acao.acoesQuantity = 100;
            acao.currentValue = 3.15;
            acao.objectiveValue = 20.00;
            acao.percentualValue = 15.36;
            acao.totalValue = 315.00;
            acoes.add(acao);
        }
        return acoes;
    }
}
