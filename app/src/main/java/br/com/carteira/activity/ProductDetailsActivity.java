package br.com.carteira.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.fragment.StockIncomesFragment;
import br.com.carteira.listener.AddProductListener;

public class ProductDetailsActivity extends AppCompatActivity {

    private AddProductListener mFormProductListener;

    private static final String LOG_TAG = ProductDetailsActivity.class.getSimpleName();

    Intent comingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        comingIntent = getIntent();
        if (comingIntent != null && comingIntent.hasExtra(Constants.Extra.EXTRA_PRODUCT_TYPE)) {

            int productType = comingIntent.getIntExtra(Constants.Extra.EXTRA_PRODUCT_TYPE,
                    Constants.ProductType.INVALID);
            switch (productType) {
                case Constants.ProductType.STOCK:
                    // TODO: Make tabs to switch between incomes and details.
                    replaceFragment(new StockIncomesFragment());
                    break;
                case Constants.ProductType.FII:
                    // TODO: replaceFragment(new FiiIncomesFragment());
                    break;
                default:
                    Log.d(LOG_TAG, "Could not find EXTRA_PRODUCT_TYPE. Finishing activity...");
                    finish();
                    break;
            }
        } else {
            Log.d(LOG_TAG, "Could not find EXTRA_PRODUCT_TYPE. Finishing activity...");
        }
    }

    // Sets the fragment on the container according to the selected item in menu
    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.details_container, frag)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: Need to change to show menu depending on product (Stock, FII, etc)
        // Common menu for details
        getMenuInflater().inflate(R.menu.stock_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, FormActivity.class);
        String symbol = comingIntent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        // Open correct income form
        switch (item.getItemId()) {
            case R.id.menu_item_dividends:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.DIVIDEND);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case R.id.menu_item_jcp:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.JCP);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
