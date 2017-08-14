package br.com.guiainvestimento.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.fragment.fii.FiiIncomeDetailsFragment;
import br.com.guiainvestimento.fragment.others.OthersIncomeDetailsFragment;
import br.com.guiainvestimento.fragment.stock.StockIncomeDetailsFragment;
import br.com.guiainvestimento.fragment.treasury.TreasuryIncomeDetailsFragment;
import br.com.guiainvestimento.listener.ProductListener;

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
                    setTitle(R.string.dividend_income_type);
                    replaceFragment(new StockIncomeDetailsFragment());
                    break;
                case Constants.IncomeType.JCP:
                    setTitle(R.string.jcp_income_type);
                    replaceFragment(new StockIncomeDetailsFragment());
                    break;
                case Constants.IncomeType.FII:
                    setTitle(R.string.fii_income_type);
                    replaceFragment(new FiiIncomeDetailsFragment());
                    break;
                case Constants.IncomeType.TREASURY:
                    setTitle(R.string.treasury_income_type);
                    replaceFragment(new TreasuryIncomeDetailsFragment());
                    break;
                case Constants.IncomeType.OTHERS:
                    setTitle(R.string.others_income_type);
                    replaceFragment(new OthersIncomeDetailsFragment());
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
