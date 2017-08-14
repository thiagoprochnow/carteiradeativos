package br.com.guiainvestimento.domain;

import com.google.gson.annotations.SerializedName;

/**
 * This is a Class that contain the information of each fii(Fii)
 * The objects created from this class will be the fiis the user has on his portfolio
 */

public class Fii {
    // Unique id of each Object created
    private long id;

    // Owned fii Ticker (Ex: PETR4)
    @SerializedName("symbol")
    private String mSymbol;

    // Full Name of the fii ticker
    @SerializedName("Name")
    private String mName;

    // Last value of the quotation of the day
    @SerializedName("LastTradePriceOnly")
    private String mLastTradePriceOnly;

    // Quantity of Fiis of that company that is owned by user. User sets this value when
    // adding a new fii or buying more of already owned fii (Ex: 100 fiis)
    private int mFiiQuantity;

    // Price paid for each fii quantity (Ex: R$32,45/Fii)
    private double mBoughtPrice;

    // (Compra) Total paid for buying the current owned Fii. The same as
    // fiisQuantity*boughtValue. (EX:R$3245,00)
    private double mBoughtTotal;

    // Current price of the fii, provided by the webservice to check the fii price of the
    // time checked.
    // (Ex: R$35,50/Fii)
    private double mCurrentPrice;

    // (Atual) Total money value of the user fii. If user has 100 fiis of PETR4 at
    // currentPrice of R$35,50,
    // he has a currentTotal of R$3550,00.
    private double mCurrentTotal;

    // (Valorização) It is the total gained from the fii difference of buy total and current
    // total. It can be negative if
    // the fii current price lost value compared to its bought price.
    private double mFiiAppreciation;

    // (Set by system according to current fii price) The percentual value owned by the user of
    // that fii compared to his whole fii
    // If the user has a total of R$10.000 in fiis, and R$2000,00 of PETR4, on PETR4 card view
    // will show this as 20% (R$2000/R$10000)
    // Note that this values changes in time depending on the currentPrice of the fii.
    private double mCurrentPercent;

    // [Set by user] The objective percentual value that the user wants to have of this fii
    // compared to his whole fii portfolio
    // If the user has a total of R$10.000 of fiis and sets his objective as 10% for PETR4, and
    // after a high gain value of the fii, he now has R$1500,00 on PETR4
    // It will show that he has 15% of the currentPercentValue, but his objectivePercent is 10%,
    // so the user need to rebalance his portfolio by buyin another that is
    // below the objectivePercent
    private double mObjectivePercent;

    // (Rendimentos) Total income from the current fii, dividends and other incomes. (Ex: R$1,
    // 00/Fii paid on 01/12/2016 per fii of PETR4)
    private double mTotalIncome;

    // (Ganho) Total gain or lost from investing in that fii. The total gain is the sum of the
    // fii appreciation and total income of that fii.
    private double mTotalGain;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String mSymbol) {
        this.mSymbol = mSymbol;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getLastTradePriceOnly() {
        return mLastTradePriceOnly;
    }

    public void setLastTradePriceOnly(String mLastTradePriceOnly) {
        this.mLastTradePriceOnly = mLastTradePriceOnly;
    }

    public int getFiiQuantity() {
        return mFiiQuantity;
    }

    public void setFiiQuantity(int fiiQuantity) {
        this.mFiiQuantity = fiiQuantity;
    }

    public double getBoughtPrice() {
        return mBoughtPrice;
    }

    public void setBoughtPrice(double boughtPrice) {
        this.mBoughtPrice = boughtPrice;
    }

    public double getBoughtTotal() {
        return mBoughtTotal;
    }

    public void setBoughtTotal(double boughtTotal) {
        this.mBoughtTotal = boughtTotal;
    }

    public double getCurrentPrice() {
        return mCurrentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.mCurrentPrice = currentPrice;
    }

    public double getCurrentTotal() {
        return mCurrentTotal;
    }

    public void setCurrentTotal(double currentTotal) {
        this.mCurrentTotal = currentTotal;
    }

    public double getFiiAppreciation() {
        return mFiiAppreciation;
    }

    public void setFiiAppreciation(double fiiAppreciation) {
        this.mFiiAppreciation = fiiAppreciation;
    }

    public double getCurrentPercent() {
        return mCurrentPercent;
    }

    public void setCurrentPercent(double currentPercent) {
        this.mCurrentPercent = currentPercent;
    }

    public double getObjectivePercent() {
        return mObjectivePercent;
    }

    public void setObjectivePercent(double objectivePercent) {
        this.mObjectivePercent = objectivePercent;
    }

    public double getTotalIncome() {
        return mTotalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.mTotalIncome = totalIncome;
    }

    public double getTotalGain() {
        return mTotalGain;
    }

    public void setTotalGain(double totalGain) {
        this.mTotalGain = totalGain;
    }
}
