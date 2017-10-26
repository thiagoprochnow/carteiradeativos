package br.com.guiainvestimento.domain;

import com.google.gson.annotations.SerializedName;

/**
 * This is a Class that contain the information of the Currency
 */

public class Crypto {

    // Unique id of each Object created
    private String id;

    // Rate value
    @SerializedName("last")
    private String mQuote;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuote() {
        return mQuote;
    }

    public void setQuote(String mQuote) {
        this.mQuote = mQuote;
    }
}
