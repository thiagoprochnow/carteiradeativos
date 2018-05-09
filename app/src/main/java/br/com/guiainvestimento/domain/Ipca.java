package br.com.guiainvestimento.domain;

import com.google.gson.annotations.SerializedName;

/**
 * This is a Class that contain the information of each stock(Stock)
 * The objects created from this class will be the stocks the user has on his portfolio
 */

public class Ipca {
    // Owned stock Ticker (Ex: PETR4)
    @SerializedName("id")
    private String mId;

    @SerializedName("ano")
    private String mAno;

    @SerializedName("mes")
    private String mMes;

    @SerializedName("valor")
    private String mValor;

    @SerializedName("atualizado")
    private String mAtualizado;

    // Getters
    public String getId() {
        return this.mId;
    }

    public String getAno() {
        return mAno;
    }

    public String getMes() {
        return mMes;
    }

    public String getValor() {
        return mValor;
    }

    public String getAtualizado() {
        return mAtualizado;
    }
}
