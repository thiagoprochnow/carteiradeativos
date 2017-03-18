package br.com.carteira.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.fragment.StockIncomesFragment;
import br.com.carteira.listener.AddProductListener;

public class IncomeDetailsActivity extends AppCompatActivity {

    private AddProductListener mFormProductListener;

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
                    // TODO: Make tabs to switch between incomes and details.
                    Toast.makeText(this, "Dividend", Toast.LENGTH_LONG).show();
                    break;
                case Constants.IncomeType.JCP:
                    // TODO: replaceFragment(new FiiIncomesFragment());
                    Toast.makeText(this, "JCP", Toast.LENGTH_LONG).show();
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