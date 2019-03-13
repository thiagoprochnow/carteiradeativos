package br.com.guiainvestimento.domain;

import com.google.gson.annotations.SerializedName;

/**
 * This is a Class that contain the information of each stock(Stock)
 * The objects created from this class will be the stocks the user has on his portfolio
 */

public class StockQuote {
    // Owned stock Ticker (Ex: PETR4)
    @SerializedName("symbol")
    private String mSymbol;

    @SerializedName("high")
    private String mHigh;

    @SerializedName("low")
    private String mLow;

    @SerializedName("open")
    private String mOpen;

    @SerializedName("previous")
    private String mPrevious;

    @SerializedName("lastTrade")
    private String mLast;

    @SerializedName("messageError")
    private String mError;

    // Getters
    public String getSymbol() {
        return this.mSymbol;
    }

    public String getHigh() {
        return this.mHigh;
    }

    public String getLow() {
        return this.mLow;
    }

    public String getOpen() {
        return this.mOpen;
    }

    public String getPrevious() {
        return this.mPrevious;
    }

    public String getLast() {
        return this.mLast;
    }

    public String getError() {
        return this.mError;
    }

    public void setmSymbol(String mSymbol) {
        this.mSymbol = mSymbol;
    }

    public void setmHigh(String mHigh) {
        this.mHigh = mHigh;
    }

    public void setmLow(String mLow) {
        this.mLow = mLow;
    }

    public void setmOpen(String mOpen) {
        this.mOpen = mOpen;
    }

    public void setmPrevious(String mPrevious) {
        this.mPrevious = mPrevious;
    }

    public void setmLast(String mLast) {
        this.mLast = mLast;
    }

    public void setmError(String mError) {
        this.mError = mError;
    }
}
