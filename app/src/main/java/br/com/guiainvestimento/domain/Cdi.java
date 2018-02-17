package br.com.guiainvestimento.domain;

import com.google.gson.annotations.SerializedName;

/**
 * This is a Class that contain the information of each stock(Stock)
 * The objects created from this class will be the stocks the user has on his portfolio
 */

public class Cdi {
    // Owned stock Ticker (Ex: PETR4)
    @SerializedName("id")
    private String mId;

    @SerializedName("valor")
    private String mValor;

    @SerializedName("data")
    private String mData;

    @SerializedName("data_string")
    private String mDataString;

    @SerializedName("atualizado")
    private String mAtualizado;

    // Getters
    public String getId() {
        return this.mId;
    }

    public String getValor() {
        return this.mValor;
    }

    public String getData() {
        return this.mData;
    }

    public String getDataString() {
        return this.mDataString;
    }

    public String getAtualizado() {
        return this.mAtualizado;
    }

}
