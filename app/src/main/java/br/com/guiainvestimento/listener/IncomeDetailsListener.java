package br.com.guiainvestimento.listener;

public interface IncomeDetailsListener {
    void onIncomeDetails(int incomeType, String itemId);
    void onIncomeEdit(int incomeType, String itemId);
}