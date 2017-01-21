package br.com.carteira.domain;

import java.io.Serializable;


public class Fii implements Serializable {

    //TODO: Lets use java beans standard in this class by adding a 'm' before
    // instance class variables. For example: mId, mSymbol, mFiiQuantity, etc.

    // Unique id of each Object created
    private long id;

    // Owned fii Symbol (Ex: KNRI11)
    private String symbol;

    // Quantity of fii of that company that is owned by user. User sets this value when
    // adding a new fii or buying more of already owned fii (Ex: 40 fii)
    private int fiiQuantity;

    // Price paid for each fii quantity (Ex: R$32,45/Fii)
    private double boughtPrice;

    // (Compra) Total paid for buying the current owned fii. The same as fiiQuantity*boughtValue.
    // (EX:R$3245,00)
    private double boughtTotal;

    // Current price of the fii, provided by the webservice to check the fii price of the time
    // checked.
    // (Ex: R$35,50/Fii)
    private double currentPrice;

    // (Atual) Total money value of the user fii portfolio. If user has 100 fii of PETR4 at
    // currentPrice of R$35,50,
    // he has a currentTotal of R$3550,00.
    private double currentTotal;

    // (Valorização) It is the total gained from the fii difference of buy total and current
    // total. It can be negative if
    // the fii current price lost value compared to its bought price.
    private double fiiAppreciation;

    // (Set by system according to current fii price) The percentual value owned by the user of
    // that fii compared to his whole fii portfolio
    // If the user has a total of R$10.000 in fii, and R$2000,00 of PETR4, on PETR4 card view
    // will show this as 20% (R$2000/R$10000)
    // Note that this values changes in time depending on the currentPrice of the fii.
    private double currentPercent;

    // [Set by user] The objective percentual value that the user wants to have of this fii
    // compared to his whole fii portfolio
    // If the user has a total of R$10.000 of fii and sets his objective as 10% for PETR4, and
    // after a high gain value of the fii, he now has R$1500,00 on PETR4
    // It will show that he has 15% of the currentPercentValue, but his objectivePercent is 10%,
    // so the user need to rebalance his portfolio by buyin another that is
    // below the objectivePercent
    private double objectivePercent;

    // (Rendimentos) Total income from the current fii, dividends and other incomes. (Ex: R$1,
    // 00/Fii paid on 01/12/2016 per fii of PETR4)
    private double totalIncome;

    // (Ganho) Total gain or lost from investing in that fii. The total gain is the sum of the
    // fii appreciation and total income of that fii.
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

    public int getFiiQuantity() {
        return fiiQuantity;
    }

    public void setFiiQuantity(int fiiQuantity) {
        this.fiiQuantity = fiiQuantity;
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

    public double getFiiAppreciation() {
        return fiiAppreciation;
    }

    public void setFiiAppreciation(double fiiAppreciation) {
        this.fiiAppreciation = fiiAppreciation;
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
