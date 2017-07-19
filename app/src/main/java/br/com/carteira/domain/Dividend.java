package br.com.carteira.domain;

import com.google.gson.annotations.SerializedName;

/**
 * This is a Class that contain the information of the Dividend
 */

public class Dividend {

    // Unique id of each Object created
    private long id;

    // Owned stock Ticker (Ex: PETR4)
    @SerializedName("symbol")
    private String mSymbol;

    // Date of dividend share
    @SerializedName("Date")
    private String mDate;

    // Dividend value
    @SerializedName("Dividends")
    private String mDividends;

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

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getDividends() {
        return mDividends;
    }

    public void setDividends(String mDividends) {
        this.mDividends = mDividends;
    }
}
