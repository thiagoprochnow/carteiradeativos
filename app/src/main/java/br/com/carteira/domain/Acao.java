package br.com.carteira.domain;

import java.io.Serializable;

/**
 * Created by thipr on 12/30/2016.
 * This is a Class that contain the information of each stock(Acao)
 * The objects created from this class will be the stocks the user has on his portfolio
 */

public class Acao implements Serializable{
    public long id;
    public String ticker;
    public int acoesQuantity;
    public double boughtValue;
    public double currentValue;
    public double objectiveValue;
    public double percentualValue;
    public double totalValue;
}
