package br.com.guiainvestimento.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.fragment.currency.BuyCurrencyFormFragment;
import br.com.guiainvestimento.fragment.currency.SellCurrencyFormFragment;
import br.com.guiainvestimento.fragment.fii.BuyFiiFormFragment;
import br.com.guiainvestimento.fragment.currency.EditCurrencyTransactionFormFragment;
import br.com.guiainvestimento.fragment.fii.EditFiiFormFragment;
import br.com.guiainvestimento.fragment.fii.EditFiiIncomeFormFragment;
import br.com.guiainvestimento.fragment.fii.EditFiiTransactionFormFragment;
import br.com.guiainvestimento.fragment.fii.FiiGroupingFormFragment;
import br.com.guiainvestimento.fragment.fii.FiiIncomeFormFragment;
import br.com.guiainvestimento.fragment.fii.FiiSplitFormFragment;
import br.com.guiainvestimento.fragment.fii.SellFiiFormFragment;
import br.com.guiainvestimento.fragment.fixedincome.BuyFixedFormFragment;
import br.com.guiainvestimento.fragment.fixedincome.EditFixedFormFragment;
import br.com.guiainvestimento.fragment.fixedincome.EditFixedTransactionFormFragment;
import br.com.guiainvestimento.fragment.fixedincome.SellFixedFormFragment;
import br.com.guiainvestimento.fragment.others.BuyOthersFormFragment;
import br.com.guiainvestimento.fragment.others.EditOthersFormFragment;
import br.com.guiainvestimento.fragment.others.EditOthersIncomeFormFragment;
import br.com.guiainvestimento.fragment.others.EditOthersTransactionFormFragment;
import br.com.guiainvestimento.fragment.others.OthersIncomeFormFragment;
import br.com.guiainvestimento.fragment.others.SellOthersFormFragment;
import br.com.guiainvestimento.fragment.stock.BonificationFormFragment;
import br.com.guiainvestimento.fragment.stock.BuyStockFormFragment;
import br.com.guiainvestimento.fragment.stock.EditJCPDividendFormFragment;
import br.com.guiainvestimento.fragment.stock.EditStockFormFragment;
import br.com.guiainvestimento.fragment.stock.EditStockTransactionFormFragment;
import br.com.guiainvestimento.fragment.stock.GroupingFormFragment;
import br.com.guiainvestimento.fragment.stock.JCPDividendFormFragment;
import br.com.guiainvestimento.fragment.stock.SellStockFormFragment;
import br.com.guiainvestimento.fragment.stock.SplitFormFragment;
import br.com.guiainvestimento.fragment.treasury.BuyTreasuryFormFragment;
import br.com.guiainvestimento.fragment.treasury.EditTreasuryFormFragment;
import br.com.guiainvestimento.fragment.treasury.EditTreasuryIncomeFormFragment;
import br.com.guiainvestimento.fragment.treasury.EditTreasuryTransactionFormFragment;
import br.com.guiainvestimento.fragment.treasury.SellTreasuryFormFragment;
import br.com.guiainvestimento.fragment.treasury.TreasuryIncomeFormFragment;


/* This is the Activity that will hold all form fragment.
The correct fragment will be selected based on the EXTRA passed
 */
public class FormActivity extends AppCompatActivity {

    private static final String LOG_TAG = FormActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        // If it has EXTRA_PRODUCT_TYPE, it is adding a product
        if (intent != null && intent.hasExtra(Constants.Extra.EXTRA_PRODUCT_TYPE)) {

            int productType = intent.getIntExtra(Constants.Extra.EXTRA_PRODUCT_TYPE,
                    Constants.ProductType.INVALID);
            int productStatus = intent.getIntExtra(Constants.Extra.EXTRA_PRODUCT_STATUS,
                    Constants.Type.INVALID);
            if(productStatus == Constants.Type.BUY) {
                switch (productType) {
                    case Constants.ProductType.STOCK:
                        replaceFragment(new BuyStockFormFragment());
                        break;
                    case Constants.ProductType.FII:
                        replaceFragment(new BuyFiiFormFragment());
                        break;
                    case Constants.ProductType.CURRENCY:
                        replaceFragment(new BuyCurrencyFormFragment());
                        break;
                    case Constants.ProductType.FIXED:
                        replaceFragment(new BuyFixedFormFragment());
                        break;
                    case Constants.ProductType.TREASURY:
                        replaceFragment(new BuyTreasuryFormFragment());
                        break;
                    case Constants.ProductType.OTHERS:
                        replaceFragment(new BuyOthersFormFragment());
                        break;
                    default:
                        finish();
                        break;
                }
            } else if(productStatus == Constants.Type.SELL) {
                switch (productType) {
                    case Constants.ProductType.STOCK:
                        replaceFragment(new SellStockFormFragment());
                        break;
                    case Constants.ProductType.FII:
                        replaceFragment(new SellFiiFormFragment());
                        break;
                    case Constants.ProductType.CURRENCY:
                        replaceFragment(new SellCurrencyFormFragment());
                        break;
                    case Constants.ProductType.FIXED:
                        replaceFragment(new SellFixedFormFragment());
                        break;
                    case Constants.ProductType.TREASURY:
                        replaceFragment(new SellTreasuryFormFragment());
                        break;
                    case Constants.ProductType.OTHERS:
                        replaceFragment(new SellOthersFormFragment());
                        break;
                    default:
                        finish();
                        break;
                }
            } else if (productStatus == Constants.Type.EDIT){
                switch (productType) {
                    case Constants.ProductType.STOCK:
                        replaceFragment(new EditStockFormFragment());
                        break;
                    case Constants.ProductType.FII:
                        replaceFragment(new EditFiiFormFragment());
                        break;
                    /*case Constants.ProductType.CURRENCY:
                        replaceFragment(new EditCurrencyFormFragment());
                        break;*/
                    case Constants.ProductType.FIXED:
                        replaceFragment(new EditFixedFormFragment());
                        break;
                    case Constants.ProductType.TREASURY:
                        replaceFragment(new EditTreasuryFormFragment());
                        break;
                    case Constants.ProductType.OTHERS:
                        replaceFragment(new EditOthersFormFragment());
                        break;
                    default:
                        finish();
                        break;
                }
            } else if (productStatus == Constants.Type.EDIT_TRANSACION){
                switch (productType) {
                    case Constants.ProductType.STOCK:
                        replaceFragment(new EditStockTransactionFormFragment());
                        break;
                    case Constants.ProductType.FII:
                        replaceFragment(new EditFiiTransactionFormFragment());
                        break;
                    case Constants.ProductType.CURRENCY:
                        replaceFragment(new EditCurrencyTransactionFormFragment());
                        break;
                    case Constants.ProductType.FIXED:
                        replaceFragment(new EditFixedTransactionFormFragment());
                        break;
                    case Constants.ProductType.TREASURY:
                        replaceFragment(new EditTreasuryTransactionFormFragment());
                        break;
                    case Constants.ProductType.OTHERS:
                        replaceFragment(new EditOthersTransactionFormFragment());
                        break;
                    default:
                        finish();
                        break;
                }
            }
        }

        // If it has EXTRA_INCOME_TYPE, it is registering income
        if (intent != null && intent.hasExtra(Constants.Extra.EXTRA_INCOME_TYPE)) {
            int productStatus = intent.getIntExtra(Constants.Extra.EXTRA_PRODUCT_STATUS,
                    Constants.Type.INVALID);
            int incomeType = intent.getIntExtra(Constants.Extra.EXTRA_INCOME_TYPE,
                    Constants.IncomeType.INVALID);
            int productType = Constants.ProductType.INVALID;
            if(intent.hasExtra(Constants.Extra.EXTRA_PRODUCT_TYPE)) {
                productType = intent.getIntExtra(Constants.Extra.EXTRA_PRODUCT_TYPE,
                        Constants.ProductType.INVALID);
            }
            if (productStatus == Constants.Type.EDIT_INCOME){
                switch (incomeType) {
                    case Constants.IncomeType.DIVIDEND:
                        replaceFragment(new EditJCPDividendFormFragment());
                        break;
                    case Constants.IncomeType.JCP:
                        replaceFragment(new EditJCPDividendFormFragment());
                        break;
                    case Constants.IncomeType.FII:
                        replaceFragment(new EditFiiIncomeFormFragment());
                        break;
                    case Constants.IncomeType.TREASURY:
                        replaceFragment(new EditTreasuryIncomeFormFragment());
                        break;
                    case Constants.IncomeType.OTHERS:
                        replaceFragment(new EditOthersIncomeFormFragment());
                        break;
                    default:
                        finish();
                        break;
                }
            } else {
                switch (incomeType) {
                    case Constants.IncomeType.DIVIDEND:
                        replaceFragment(new JCPDividendFormFragment());
                        break;
                    case Constants.IncomeType.JCP:
                        replaceFragment(new JCPDividendFormFragment());
                        break;
                    case Constants.IncomeType.BONIFICATION:
                        replaceFragment(new BonificationFormFragment());
                        break;
                    case Constants.IncomeType.SPLIT:
                        if(productType == Constants.ProductType.FII){
                            replaceFragment(new FiiSplitFormFragment());
                        } else {
                            replaceFragment(new SplitFormFragment());
                        }
                        break;
                    case Constants.IncomeType.GROUPING:
                        if(productType == Constants.ProductType.FII){
                            replaceFragment(new FiiGroupingFormFragment());
                        } else {
                            replaceFragment(new GroupingFormFragment());
                        }
                        break;
                    case Constants.IncomeType.FII:
                        replaceFragment(new FiiIncomeFormFragment());
                        break;
                    case Constants.IncomeType.TREASURY:
                        replaceFragment(new TreasuryIncomeFormFragment());
                        break;
                    case Constants.IncomeType.OTHERS:
                        replaceFragment(new OthersIncomeFormFragment());
                        break;
                    case Constants.IncomeType.FII_SPLIT:
                        replaceFragment(new FiiSplitFormFragment());
                        break;
                    default:
                        finish();
                        break;
                }
            }
        }
    }

    // Sets the fragment on the container according to the selected item in menu
    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.form_container, frag)
                .commit();
    }

}
