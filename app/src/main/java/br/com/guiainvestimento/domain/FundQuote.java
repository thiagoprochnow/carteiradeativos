package br.com.guiainvestimento.domain;

import com.google.gson.annotations.SerializedName;

public class FundQuote {

    @SerializedName("id")
    private long id;

    @SerializedName("cnpj")
    private String cnpj;

    @SerializedName("data")
    private String data;

    @SerializedName("quote")
    private String quote;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj){
        this.cnpj = cnpj;
    }

    public String getData() {
        return data;
    }

    public void setData(String data){
        this.data = data;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote){
        this.quote = quote;
    }
}