package br.com.guiainvestimento.domain;

import com.google.gson.annotations.SerializedName;

public class Fund {

    @SerializedName("id")
    private long id;

    @SerializedName("cnpj")
    private String cnpj;

    @SerializedName("nome")
    private String nome;

    @SerializedName("tipo")
    private String tipo;

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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }
}