package br.com.guiainvestimento.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.fragment.currency.CurrencyDetailsFragment;
import br.com.guiainvestimento.fragment.fii.FiiTabFragment;
import br.com.guiainvestimento.fragment.fixedincome.FixedDetailsFragment;
import br.com.guiainvestimento.fragment.others.OthersTabFragment;
import br.com.guiainvestimento.fragment.stock.StockTabFragment;
import br.com.guiainvestimento.fragment.treasury.TreasuryTabFragment;
import br.com.guiainvestimento.listener.ProductListener;
import br.com.guiainvestimento.listener.IncomeDetailsListener;
import br.com.guiainvestimento.listener.TransactionListener;

public class ProductDetailsActivity extends AppCompatActivity implements IncomeDetailsListener, TransactionListener {

    private ProductListener mFormProductListener;

    private static final String LOG_TAG = ProductDetailsActivity.class.getSimpleName();

    Intent comingIntent;

    int mProductType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        comingIntent = getIntent();
        if (comingIntent != null && comingIntent.hasExtra(Constants.Extra.EXTRA_PRODUCT_TYPE)) {

            mProductType = comingIntent.getIntExtra(Constants.Extra.EXTRA_PRODUCT_TYPE,
                    Constants.ProductType.INVALID);
            switch (mProductType) {
                case Constants.ProductType.STOCK:
                    // TODO: Make tabs to switch between incomes and details.
                    replaceFragment(new StockTabFragment());
                    break;
                case Constants.ProductType.FII:
                    replaceFragment(new FiiTabFragment());
                    break;
                case Constants.ProductType.FIXED:
                    replaceFragment(new FixedDetailsFragment());
                    break;
                case Constants.ProductType.CURRENCY:
                    replaceFragment(new CurrencyDetailsFragment());
                    break;
                case Constants.ProductType.TREASURY:
                    replaceFragment(new TreasuryTabFragment());
                    break;
                case Constants.ProductType.OTHERS:
                    replaceFragment(new OthersTabFragment());
                    break;
                default:
                    finish();
                    break;
            }
        } else {
        }
    }

    // Sets the fragment on the container according to the selected item in menu
    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.details_container, frag)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Common menu for details
        switch (mProductType) {
            case Constants.ProductType.STOCK:
                getMenuInflater().inflate(R.menu.stock_details_menu, menu);
                return true;
            case Constants.ProductType.FII:
                getMenuInflater().inflate(R.menu.fii_details_menu, menu);
                return true;
            case Constants.ProductType.TREASURY:
                getMenuInflater().inflate(R.menu.treasury_details_menu, menu);
                return true;
            case Constants.ProductType.OTHERS:
                getMenuInflater().inflate(R.menu.others_details_menu, menu);
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, FormActivity.class);
        String symbol = comingIntent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
        // Open correct income form
        switch (item.getItemId()) {
            case R.id.menu_item_dividends:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.DIVIDEND);
                startActivity(intent);
                break;
            case R.id.menu_item_jcp:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.JCP);
                startActivity(intent);
                break;
            case R.id.menu_item_bonification:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.BONIFICATION);
                startActivity(intent);
                break;
            case R.id.menu_item_split:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.SPLIT);
                startActivity(intent);
                break;
            case R.id.menu_item_grouping:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.GROUPING);
                startActivity(intent);
                break;
            case R.id.menu_item_income:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.FII);
                startActivity(intent);
                break;
            case R.id.menu_item_treasury_income:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.TREASURY);
                startActivity(intent);
                break;
            case R.id.menu_item_others_income:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.OTHERS);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onIncomeEdit(int incomeType, String id){
        Intent intent = new Intent(this, FormActivity.class);
        switch (incomeType){
            case Constants.IncomeType.DIVIDEND:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.DIVIDEND);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_INCOME);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            case Constants.IncomeType.JCP:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.JCP);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_INCOME);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            case Constants.IncomeType.FII:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.FII);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_INCOME);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            case Constants.IncomeType.FIXED:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.FIXED);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_INCOME);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            case Constants.IncomeType.TREASURY:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.TREASURY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_INCOME);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            case Constants.IncomeType.OTHERS:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.OTHERS);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_INCOME);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onIncomeDetails(int incomeType, String id){
        Intent intent = new Intent(this, IncomeDetailsActivity.class);
        switch (incomeType) {
            case Constants.IncomeType.DIVIDEND:
                // Sends id of clicked income to income details acitivity
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.DIVIDEND);
                intent.putExtra(Constants.Extra.EXTRA_INCOME_ID, id);
                startActivity(intent);
                break;
            case Constants.IncomeType.JCP:
                // Sends id of clicked income to income details acitivity
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.JCP);
                intent.putExtra(Constants.Extra.EXTRA_INCOME_ID, id);
                startActivity(intent);
                break;
            case Constants.IncomeType.FII:
                // Sends id of clicked income to income details acitivity
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.FII);
                intent.putExtra(Constants.Extra.EXTRA_INCOME_ID, id);
                startActivity(intent);
                break;
            case Constants.IncomeType.TREASURY:
                // Sends id of clicked income to income details acitivity
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.TREASURY);
                intent.putExtra(Constants.Extra.EXTRA_INCOME_ID, id);
                startActivity(intent);
                break;
            case Constants.IncomeType.OTHERS:
                // Sends id of clicked income to income details acitivity
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.OTHERS);
                intent.putExtra(Constants.Extra.EXTRA_INCOME_ID, id);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onEditTransaction(int mProductType, String id){
        Intent intent = new Intent(this, FormActivity.class);
        switch (mProductType) {
            case Constants.ProductType.TREASURY:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.TREASURY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_TRANSACION);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            case Constants.ProductType.FIXED:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FIXED);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_TRANSACION);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            case Constants.ProductType.STOCK:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.STOCK);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_TRANSACION);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            case Constants.ProductType.FII:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FII);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_TRANSACION);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            case Constants.ProductType.CURRENCY:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.CURRENCY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_TRANSACION);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            case Constants.ProductType.OTHERS:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.OTHERS);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT_TRANSACION);
                intent.putExtra(Constants.Extra.EXTRA_TRANSACTION_ID, id);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
