package br.com.carteira.domain;

import com.google.gson.annotations.SerializedName;

/**
 * This is a Class that contain the information of the Currency
 */

public class Currency {

    // Unique id of each Object created
    private String id;

    // Currency symbol (Ex: USD)
    @SerializedName("Name")
    private String mSymbol;

    // Date of the rate
    @SerializedName("Date")
    private String mDate;

    // Rate value
    @SerializedName("Rate")
    private String mRate;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getRate() {
        return mRate;
    }

    public void setRate(String mRate) {
        this.mRate = mRate;
    }
}
