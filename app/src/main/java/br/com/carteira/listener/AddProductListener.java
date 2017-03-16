package br.com.carteira.listener;

public interface AddProductListener {
    void onBuyProduct(int productType, String symbol);
    void onSellProduct(int productType, String symbol);
}