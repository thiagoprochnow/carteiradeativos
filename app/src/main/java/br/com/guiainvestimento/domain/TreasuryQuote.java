package br.com.guiainvestimento.domain;

import com.google.gson.annotations.SerializedName;

/**
 * This is a Class that contain the information of each stock(Stock)
 * The objects created from this class will be the stocks the user has on his portfolio
 */

public class TreasuryQuote {
    // Owned stock Ticker (Ex: PETR4)
    @SerializedName("id")
    private String mId;

    @SerializedName("nome")
    private String mNome;

    @SerializedName("valor")
    private String mValor;

    @SerializedName("data")
    private String mData;

    @SerializedName("tipo")
    private String mTipo;

    @SerializedName("atualizado")
    private String mAtualizado;

    @SerializedName("error")
    private String mError;

    // Getters
    public String getId() {
        return this.mId;
    }

    public String getNome() {
        return this.mNome;
    }

    public String getValor() {
        return this.mValor;
    }

    public String getData() {
        return this.mData;
    }

    public String getTipo() {
        return this.mTipo;
    }

    public String getAtualizado() {
        return this.mAtualizado;
    }

    public String getError() {
        return this.mError;
    }

}
