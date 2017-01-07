package br.com.carteira.domain;

import java.io.Serializable;

/**
 * Created by thipr on 12/30/2016.
 * This is a Class that contain the information of each stock(Acao)
 * The objects created from this class will be the stocks the user has on his portfolio
 */

public class Acao implements Serializable{
    // Unique id of each Object created
    public long id;

    // Owned stock Ticker (Ex: PETR4)
    public String ticker;

    // Quantity of Stocks of that company that is owned by user. User sets this value when
    // adding a new stock or buying more of already owned stock (Ex: 100 stocks)
    public int stockQuantity;

    // Price paid for each stock quantity (Ex: R$32,45/Stock)
    public double boughtPrice;

    // (Compra) Total paid for buying the current owned Stock. The same as acoesQuantity*boughtValue. (EX:R$3245,00)
    public double boughtTotal;

    // Current price of the stock, provided by the webservice to check the stock price of the time checked.
    // (Ex: R$35,50/Stock)
    public double currentPrice;

    // (Atual) Total money value of the user stock portfolio. If user has 100 stocks of PETR4 at currentPrice of R$35,50,
    // he has a currentTotal of R$3550,00.
    public double currentTotal;

    // (Valorização) It is the total gained from the stock difference of buy total and current total. It can be negative if
    // the stock current price lost value compared to its bought price.
    public double stockAppreciation;

    // (Set by system according to current stock price) The percentual value owned by the user of that stock compared to his whole stock portfolio
    // If the user has a total of R$10.000 in stocks, and R$2000,00 of PETR4, on PETR4 card view will show this as 20% (R$2000/R$10000)
    // Note that this values changes in time depending on the currentPrice of the stock.
    public double currentPercent;

    // [Set by user] The target percentual value that the user wants to have of this stock compared to his whole stock portfolio
    // If the user has a total of R$10.000 of stocks and sets his target as 10% for PETR4, and after a high gain value of the stock, he now has R$1500,00 on PETR4
    // It will show that he has 15% of the currentPercentValue, but his targetValue is 10%, so the user need to rebalance his portfolio by buyin another that is
    // below the targetValue
    public double targetPercent;

    // (Rendimentos) Total income from the current stock, dividends and other incomes. (Ex: R$1,00/Stock paid on 01/12/2016 per stock of PETR4)
    public double totalIncome;

    // (Ganho) Total gain or lost from investing in that stock. The total gain is the sum of the stock appreciation and total income of that stock.
    public double totalGain;
}
