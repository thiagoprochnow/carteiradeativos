package br.com.carteira.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.fragment.stock.IncomeDetailsFragment;
import br.com.carteira.listener.ProductListener;

public class IncomeDetailsActivity extends AppCompatActivity {

    private ProductListener mFormProductListener;

    private static final String LOG_TAG = IncomeDetailsActivity.class.getSimpleName();

    Intent comingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_details);
        comingIntent = getIntent();
        if (comingIntent != null && comingIntent.hasExtra(Constants.Extra.EXTRA_INCOME_TYPE)) {

            int productType = comingIntent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE,
                    Constants.ProductType.INVALID);
            switch (productType) {
                case Constants.IncomeType.DIVIDEND:
                    replaceFragment(new IncomeDetailsFragment());
                    break;
                case Constants.IncomeType.JCP:
                    replaceFragment(new IncomeDetailsFragment());
                    break;
                default:
                    Log.d(LOG_TAG, "Could not find EXTRA_INCOME_TYPE. Finishing activity...");
                    finish();
                    break;
            }
        } else{
            Log.d(LOG_TAG, "Could not find EXTRA_INCOME_TYPE. Finishing activity...");
        }
    }

    // Sets the fragment on the container according to the selected item in menu
    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.details_container, frag)
                .commit();
    }
}
