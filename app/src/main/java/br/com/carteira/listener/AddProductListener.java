package br.com.carteira.listener;

public interface AddProductListener {
    void onBuyProduct(int productType);
    void onSellProduct(int productType, String symbol);
}