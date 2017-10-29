package br.com.guiainvestimento.listener;

public interface TransactionListener {
    void onEditTransaction(int productType, String itemId);

}