package br.com.carteira.listener;

public interface ProductListener {
    void onBuyProduct(int productType, String symbol);
    void onSellProduct(int productType, String symbol);
    void onEditProduct(int productType, String symbol);
    void onProductDetails(int productType, String itemId);
}