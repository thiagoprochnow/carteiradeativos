package br.com.carteira.api.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Domain class responsible for mapping the attributes that a stock will contain
 */
@SuppressWarnings("unused")
public class Stock {

    @SerializedName("symbol")
    private String mSymbol;

    @SerializedName("Name")
    private String mName;

    @SerializedName("Bid")
    private String mBid;

    @SerializedName("Change")
    private String mChange;

    @SerializedName("ChangeinPercent")
    private String mChangeInPercent;

    @SerializedName("DaysLow")
    private String mDaysLow;

    @SerializedName("DaysHigh")
    private String mDaysHigh;

    public String getSymbol() {
        return mSymbol;
    }

    public String getName() {
        return mName;
    }

    public String getBid() {
        return mBid;
    }

    public String getChange() {
        return mChange;
    }

    public String getChangeInPercent() {
        return mChangeInPercent;
    }

    public String getDaysLow() {
        return mDaysLow;
    }

    public String getDaysHigh() {
        return mChange;
    }

}
