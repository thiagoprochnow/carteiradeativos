package br.com.carteira.domain;

import java.io.Serializable;

/**
 * This is a Class that contain the information of each stock(Stock)
 * The objects created from this class will be the stocks the user has on his portfolio
 */

public class Stock implements Serializable {
    // Unique id of each Object created
    private long id;

    // Owned stock Symbol (Ex: PETR4)
    private String symbol;

    // Quantity of Stocks of that company that is owned by user. User sets this value when
    // adding a new stock or buying more of already owned stock (Ex: 100 stocks)
    private int stockQuantity;

    // Price paid for each stock quantity (Ex: R$32,45/Stock)
    private double boughtPrice;

    // (Compra) Total paid for buying the current owned Stock. The same as
    // stocksQuantity*boughtValue. (EX:R$3245,00)
    private double boughtTotal;

    // Current price of the stock, provided by the webservice to check the stock price of the
    // time checked.
    // (Ex: R$35,50/Stock)
    private double currentPrice;

    // (Atual) Total money value of the user stock portfolio. If user has 100 stocks of PETR4 at
    // currentPrice of R$35,50,
    // he has a currentTotal of R$3550,00.
    private double currentTotal;

    // (Valorização) It is the total gained from the stock difference of buy total and current
    // total. It can be negative if
    // the stock current price lost value compared to its bought price.
    private double stockAppreciation;

    // (Set by system according to current stock price) The percentual value owned by the user of
    // that stock compared to his whole stock portfolio
    // If the user has a total of R$10.000 in stocks, and R$2000,00 of PETR4, on PETR4 card view
    // will show this as 20% (R$2000/R$10000)
    // Note that this values changes in time depending on the currentPrice of the stock.
    private double currentPercent;

    // [Set by user] The objective percentual value that the user wants to have of this stock
    // compared to his whole stock portfolio
    // If the user has a total of R$10.000 of stocks and sets his objective as 10% for PETR4, and
    // after a high gain value of the stock, he now has R$1500,00 on PETR4
    // It will show that he has 15% of the currentPercentValue, but his objectivePercent is 10%,
    // so the user need to rebalance his portfolio by buyin another that is
    // below the objectivePercent
    private double objectivePercent;

    // (Rendimentos) Total income from the current stock, dividends and other incomes. (Ex: R$1,
    // 00/Stock paid on 01/12/2016 per stock of PETR4)
    private double totalIncome;

    // (Ganho) Total gain or lost from investing in that stock. The total gain is the sum of the
    // stock appreciation and total income of that stock.
    private double totalGain;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public double getBoughtPrice() {
        return boughtPrice;
    }

    public void setBoughtPrice(double boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    public double getBoughtTotal() {
        return boughtTotal;
    }

    public void setBoughtTotal(double boughtTotal) {
        this.boughtTotal = boughtTotal;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getCurrentTotal() {
        return currentTotal;
    }

    public void setCurrentTotal(double currentTotal) {
        this.currentTotal = currentTotal;
    }

    public double getStockAppreciation() {
        return stockAppreciation;
    }

    public void setStockAppreciation(double stockAppreciation) {
        this.stockAppreciation = stockAppreciation;
    }

    public double getCurrentPercent() {
        return currentPercent;
    }

    public void setCurrentPercent(double currentPercent) {
        this.currentPercent = currentPercent;
    }

    public double getObjectivePercent() {
        return objectivePercent;
    }

    public void setObjectivePercent(double objectivePercent) {
        this.objectivePercent = objectivePercent;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalGain() {
        return totalGain;
    }

    public void setTotalGain(double totalGain) {
        this.totalGain = totalGain;
    }
}
