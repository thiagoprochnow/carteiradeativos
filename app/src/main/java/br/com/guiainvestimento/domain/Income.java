package br.com.guiainvestimento.domain;

import com.google.gson.annotations.SerializedName;

/**
 * This is a Class that contain the information of each stock(Stock)
 * The objects created from this class will be the stocks the user has on his portfolio
 */

public class Income {
    // Owned stock Ticker (Ex: PETR4)
    @SerializedName("id")
    private String mId;

    @SerializedName("codigo")
    private String mCodigo;

    @SerializedName("data")
    private String mData;

    @SerializedName("timestamp")
    private String mTimestamp;

    @SerializedName("valor")
    private String mValor;

    @SerializedName("tipo")
    private String mTipo;

    @SerializedName("atualizado")
    private String mAtualizado;

    // Getters
    public String getId() {
        return this.mId;
    }

    public String getCodigo() {
        return this.mCodigo;
    }

    public String getData() {
        return this.mData;
    }

    public String getTimestamp() {
        return this.mTimestamp;
    }

    public String getValor() {
        return this.mValor;
    }

    public String getTipo() {
        return this.mTipo;
    }

    public String getAtualizado() {
        return this.mAtualizado;
    }

}
