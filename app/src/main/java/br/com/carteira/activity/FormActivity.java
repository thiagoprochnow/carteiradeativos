package br.com.carteira.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.fragment.AddStockFormFragment;
import br.com.carteira.fragment.DividendFormFragment;


/* This is the Activity that will hold all form fragment.
The correct fragment will be selected based on the EXTRA passed
 */
public class FormActivity extends AppCompatActivity {

    private static final String LOG_TAG = FormActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Intent intent = getIntent();
        // If it has EXTRA_PRODUCT_TYPE, it is adding a product
        if (intent != null && intent.hasExtra(Constants.Extra.EXTRA_PRODUCT_TYPE)) {

            int productType = intent.getIntExtra(Constants.Extra.EXTRA_PRODUCT_TYPE,
                    Constants.ProductType.INVALID);
            switch (productType) {
                case Constants.ProductType.STOCK:
                    replaceFragment(new AddStockFormFragment());
                    break;
                case Constants.ProductType.FII:
                    // TODO: replaceFragment(new AddFiiFormFragment());
                    break;
                default:
                    Log.d(LOG_TAG, "Could not find EXTRA_PRODUCT_TYPE. Finishing activity...");
                    finish();
                    break;
            }
        }

        // If it has EXTRA_INCOME_TYPE, it is registering income
        if (intent != null && intent.hasExtra(Constants.Extra.EXTRA_INCOME_TYPE)) {

            int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE,
                    Constants.IncomeType.INVALID);
            switch (incomeType) {
                case Constants.IncomeType.DIVIDEND:
                    replaceFragment(new DividendFormFragment());
                    break;
                default:
                    Log.d(LOG_TAG, "Could not find EXTRA_PRODUCT_TYPE. Finishing activity...");
                    finish();
                    break;
            }
        }
    }

    // Sets the fragment on the container according to the selected item in menu
    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.form_container, frag)
                .commit();
    }

}
