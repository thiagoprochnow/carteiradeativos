package br.com.guiainvestimento.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.api.service.FixedIntentService;
import br.com.guiainvestimento.api.service.CryptoIntentService;
import br.com.guiainvestimento.api.service.CurrencyIntentService;
import br.com.guiainvestimento.api.service.FiiIncomeIntentService;
import br.com.guiainvestimento.api.service.StockIncomeIntentService;
import br.com.guiainvestimento.api.service.TreasuryIntentService;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.AboutFragment;
import br.com.guiainvestimento.fragment.BackupRestoreFragment;
import br.com.guiainvestimento.fragment.ConsultQuotesFragment;
import br.com.guiainvestimento.fragment.FaqFragment;
import br.com.guiainvestimento.fragment.PremiumEditionFragment;
import br.com.guiainvestimento.fragment.currency.CurrencyTabFragment;
import br.com.guiainvestimento.fragment.fii.FiiTabFragment;
import br.com.guiainvestimento.fragment.PortfolioMainFragment;
import br.com.guiainvestimento.fragment.fixedincome.FixedTabFragment;
import br.com.guiainvestimento.fragment.fund.FundTabFragment;
import br.com.guiainvestimento.fragment.others.OthersTabFragment;
import br.com.guiainvestimento.fragment.stock.StockTabFragment;
import br.com.guiainvestimento.fragment.treasury.TreasuryTabFragment;
import br.com.guiainvestimento.listener.IncomeDetailsListener;
import br.com.guiainvestimento.listener.ProductListener;
import br.com.guiainvestimento.purchaseutil.IabHelper;
import br.com.guiainvestimento.purchaseutil.IabResult;
import br.com.guiainvestimento.purchaseutil.Inventory;
import br.com.guiainvestimento.purchaseutil.Purchase;
import br.com.guiainvestimento.receiver.CurrencyReceiver;
import br.com.guiainvestimento.receiver.FiiReceiver;
import br.com.guiainvestimento.receiver.FixedReceiver;
import br.com.guiainvestimento.receiver.FundReceiver;
import br.com.guiainvestimento.receiver.OthersReceiver;
import br.com.guiainvestimento.receiver.PortfolioReceiver;
import br.com.guiainvestimento.receiver.StockReceiver;
import br.com.guiainvestimento.receiver.TreasuryReceiver;

// Main app Activity
public class MainActivity extends AppCompatActivity implements ProductListener, IncomeDetailsListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    protected DrawerLayout mDrawerLayout;

    private FirebaseAnalytics mFirebaseAnalytics;

    private final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlkgmReCxrtpx7ZEU1oTxzwlEedEFKy41W+J9KMSVB74mrzxFCBq+BNnA72RKEcOAhBkYPzDF6Ku8LErZK+/1JkszJ6EgBjF7AXyf6Auav9uDsn1PvBzt6kUNa5blJtmsEJ+WTFW82uVJ9O+1QL3nMzdUPx+cpDf6Vx7gSzy2DSu2JVjHZVTW98flsSeYHieWiL1+OFQwv68PpFbQ8QS4hwEzQsPIbqdKCw9IT061OgAIcBhh37kBWmfbc5PfxHkUupv0eiHk2Df9lrNpMcWiZQH8m6wiennbSLYNj+qOSngoy0xaeYIOti0JiuLluiNswOour6CFzcEbuQ//MrkXPQIDAQAB";

    IabHelper mHelper;

    boolean mStockReceiver = false;
    boolean mFiiReceiver = false;
    boolean mCurrencyReceiver = false;
    boolean mTreasuryReceiver = false;
    boolean mFixedReceiver = false;
    boolean mFundReceiver = false;
    private boolean mIsPremium = true;
    private String mPremiumType = "";

    private Context context;

    GoogleApiClient mGoogleApiClient;

    private Menu mMenu = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setupNavDrawer();
        context = this;

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Connect to Billing service
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // There was a problem.
                    Log.d(LOG_TAG, "Problem setting up In-app Billing: " + result);
                } else {
                    try{
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e){
                        Log.e(LOG_TAG, e.toString());
                    }
                }
            }
        });

        // Checks if savedInstanceState is null so it will not load portfolio fragment on screen
        // rotation
        // and hard keyboard opening
        if (savedInstanceState == null) {
            replaceFragment(new PortfolioMainFragment(), "PortfolioMainFragment");
        }

        BroadcastReceiver receiverStock = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mCurrencyReceiver && mFiiReceiver && mTreasuryReceiver && mFixedReceiver && mFundReceiver) {
                    // Ends progress bar on menu when portfolio is updated
                    mMenu.findItem(R.id.menu_refresh).setActionView(null);
                    // Reset receiver flags
                    mCurrencyReceiver = false;
                    mFiiReceiver = false;
                    mStockReceiver = false;
                    mTreasuryReceiver = false;
                    mFixedReceiver = false;
                    mFundReceiver = false;
                    updatePortfolios();
                } else {
                    // Sets StockReceiver flag
                    mStockReceiver = true;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverStock, new IntentFilter(Constants.Receiver.STOCK));

        BroadcastReceiver receiverFii = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mCurrencyReceiver && mStockReceiver && mTreasuryReceiver && mFixedReceiver && mFundReceiver) {
                    // Ends progress bar on menu when portfolio is updated
                    mMenu.findItem(R.id.menu_refresh).setActionView(null);
                    // Reset receiver flags
                    mCurrencyReceiver = false;
                    mFiiReceiver = false;
                    mStockReceiver = false;
                    mTreasuryReceiver = false;
                    mFixedReceiver = false;
                    mFundReceiver = false;
                    updatePortfolios();
                } else {
                    // Sets StockReceiver flag
                    mFiiReceiver = true;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverFii, new IntentFilter(Constants.Receiver.FII));

        BroadcastReceiver receiverCurrency = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mFiiReceiver && mStockReceiver && mTreasuryReceiver && mFixedReceiver && mFundReceiver) {
                    // Ends progress bar on menu when portfolio is updated
                    mMenu.findItem(R.id.menu_refresh).setActionView(null);
                    // Reset receiver flags
                    mCurrencyReceiver = false;
                    mFiiReceiver = false;
                    mStockReceiver = false;
                    mTreasuryReceiver = false;
                    mFixedReceiver = false;
                    mFundReceiver = false;
                    updatePortfolios();
                } else {
                    // Sets StockReceiver flag
                    mCurrencyReceiver = true;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverCurrency, new IntentFilter(Constants.Receiver.CURRENCY));

        BroadcastReceiver receiverTreasury = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mCurrencyReceiver && mStockReceiver && mFiiReceiver && mFixedReceiver && mFundReceiver) {
                    // Ends progress bar on menu when portfolio is updated
                    mMenu.findItem(R.id.menu_refresh).setActionView(null);
                    // Reset receiver flags
                    mCurrencyReceiver = false;
                    mFiiReceiver = false;
                    mStockReceiver = false;
                    mTreasuryReceiver = false;
                    mFixedReceiver = false;
                    mFundReceiver = false;
                    updatePortfolios();
                } else {
                    // Sets StockReceiver flag
                    mTreasuryReceiver = true;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverTreasury, new IntentFilter(Constants.Receiver.TREASURY));

        BroadcastReceiver receiverFixed = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mCurrencyReceiver && mStockReceiver && mFiiReceiver && mTreasuryReceiver) {
                    // Ends progress bar on menu when portfolio is updated
                    mMenu.findItem(R.id.menu_refresh).setActionView(null);
                    // Reset receiver flags
                    mCurrencyReceiver = false;
                    mFiiReceiver = false;
                    mStockReceiver = false;
                    mTreasuryReceiver = false;
                    mFixedReceiver = false;
                    mFundReceiver = false;
                    updatePortfolios();
                } else {
                    // Sets StockReceiver flag
                    mFixedReceiver = true;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverFixed, new IntentFilter(Constants.Receiver.FIXED));

        BroadcastReceiver receiverFund = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mCurrencyReceiver && mStockReceiver && mFiiReceiver && mTreasuryReceiver && mFixedReceiver) {
                    // Ends progress bar on menu when portfolio is updated
                    mMenu.findItem(R.id.menu_refresh).setActionView(null);
                    // Reset receiver flags
                    mCurrencyReceiver = false;
                    mFiiReceiver = false;
                    mStockReceiver = false;
                    mTreasuryReceiver = false;
                    mFixedReceiver = false;
                    mFundReceiver = false;
                    updatePortfolios();
                } else {
                    // Sets StockReceiver flag
                    mFundReceiver = true;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverFund, new IntentFilter(Constants.Receiver.FUND));
    }

    protected void updatePortfolios(){
        CurrencyReceiver currencyReceiver = new CurrencyReceiver(context);
        currencyReceiver.updateCurrencyPortfolio();

        FiiReceiver fiiReceiver = new FiiReceiver(context);
        fiiReceiver.updateFiiPortfolio();

        FixedReceiver fixedReceiver = new FixedReceiver(context);
        fixedReceiver.updateFixedPortfolio();

        FundReceiver fundReceiver = new FundReceiver(context);
        fundReceiver.updateFundPortfolio();

        OthersReceiver othersReceiver = new OthersReceiver(context);
        othersReceiver.updateOthersPortfolio();

        StockReceiver stockReceiver = new StockReceiver(context);
        stockReceiver.updateStockPortfolio();

        TreasuryReceiver treasuryReceiver = new TreasuryReceiver(context);
        treasuryReceiver.updateTreasuryPortfolio();

        PortfolioReceiver portfolioReceiver = new PortfolioReceiver(context);
        portfolioReceiver.updatePortfolio();
    }

    // Send result for Fragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (requestCode == Constants.Intent.DRIVE_CONNECTION_RESOLUTION) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == Constants.Intent.GET_DRIVE_FILE){
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == Constants.Intent.PURCHASE_SUBSCRIPTION){
            mHelper.flagEndAsync();
            mHelper.handleActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try{
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e){
                Log.e(LOG_TAG, "Billing Error: " + e.toString());
            }
        };
        mHelper = null;
    }

    // Configure the toolbar
    protected void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    // Configure and set the Drawer when called in the main activity
    protected void setupNavDrawer() {
        final ActionBar actionBar = getSupportActionBar();
        // Icon to call upon the Drawer to come out
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Drawer layout view of the activity layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Navigation view of the activity layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null && mDrawerLayout != null) {
            // Configures the event when a item is selected from the menu
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            // Sets that clicked item as selected
                            menuItem.setChecked(true);
                            // Closes the menu to show back the content
                            mDrawerLayout.closeDrawers();
                            // Calls upon the function that will treat the selected item.
                            onNavDrawerItemSelected(menuItem);
                            return true;
                        }
                    });
        }
    }

    // Will treat the selected item and attach the correct fragment.
    private void onNavDrawerItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_item_complete_portfolio:
                setTitle(R.string.title_complete_portfolio);
                replaceFragment(new PortfolioMainFragment(), "PortfolioMainFragment");
                break;
            case R.id.nav_item_treasury:
                setTitle(R.string.title_treasury);
                replaceFragment(new TreasuryTabFragment());
                break;
            case R.id.nav_item_fixed_income:
                setTitle(R.string.title_fixed);
                replaceFragment(new FixedTabFragment());
                break;
            case R.id.nav_item_fund:
                if(isPremium()) {
                    setTitle(R.string.title_fund);
                    replaceFragment(new FundTabFragment());
                } else {
                    setTitle(R.string.title_premium_edition);
                    replaceFragment(new PremiumEditionFragment());
                }
                break;
            case R.id.nav_item_stocks:
                setTitle(R.string.title_stocks);
                replaceFragment(new StockTabFragment());
                break;
            case R.id.nav_item_fii:
                setTitle(R.string.title_fii);
                replaceFragment(new FiiTabFragment());
                break;
            case R.id.nav_item_currency:
                setTitle(R.string.title_currency);
                replaceFragment(new CurrencyTabFragment());
                break;
            case R.id.nav_item_others:
                setTitle(R.string.title_others);
                replaceFragment(new OthersTabFragment());
                break;
            case R.id.nav_item_premium_edition:
                setTitle(R.string.title_premium_edition);
                replaceFragment(new PremiumEditionFragment());
                break;
            case R.id.nav_item_consult_quotes:
                setTitle(R.string.title_consult_quotes);
                replaceFragment(new ConsultQuotesFragment());
                break;
            case R.id.nav_item_about:
                setTitle(R.string.title_about);
                replaceFragment(new AboutFragment());
                break;
            case R.id.nav_item_backup_restore:
                setTitle(R.string.title_backup_restore);
                replaceFragment(new BackupRestoreFragment());
                break;
            case R.id.nav_item_faq:
                setTitle(R.string.title_faq);
                replaceFragment(new FaqFragment());
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout != null) {
                    openDrawer();
                    return true;
                }
            case R.id.menu_refresh:
                ProgressBar spinner = new ProgressBar(this);
                spinner.getIndeterminateDrawable().setColorFilter(
                        ContextCompat.getColor(this,R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);

                item.setActionView(spinner);
                refreshPortfolio();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Sets the fragment on the container according to the selected item in menu
    public void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag,
                "TAG").commit();
    }

    public void replaceFragment(Fragment frag, String tag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag,
                tag).commit();
    }

    // Open the Drawer
    protected void openDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    // Closes the Drawer
    protected void closeDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onBuyProduct(int productType, String symbol) {
        Intent intent = new Intent(this, FormActivity.class);
        switch (productType) {
            case Constants.ProductType.STOCK:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.STOCK);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.BUY);
                if(symbol != null && !symbol.isEmpty()){
                    intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                }
                startActivity(intent);
                break;
            case Constants.ProductType.FII:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FII);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.BUY);
                if(symbol != null && !symbol.isEmpty()){
                    intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                }
                startActivity(intent);
                break;
            case Constants.ProductType.CURRENCY:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.CURRENCY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.BUY);
                if(symbol != null && !symbol.isEmpty()){
                    intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                }
                startActivity(intent);
                break;
            case Constants.ProductType.FIXED:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FIXED);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.BUY);
                if(symbol != null && !symbol.isEmpty()){
                    intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                }
                startActivity(intent);
                break;
            case Constants.ProductType.FUND:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FUND);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.BUY);
                if(symbol != null && !symbol.isEmpty()){
                    intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                }
                startActivity(intent);
                break;
            case Constants.ProductType.TREASURY:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.TREASURY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.BUY);
                if(symbol != null && !symbol.isEmpty()){
                    intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                }
                startActivity(intent);
                break;
            case Constants.ProductType.OTHERS:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.OTHERS);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.BUY);
                if(symbol != null && !symbol.isEmpty()){
                    intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                }
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onSellProduct(int productType, String symbol) {
        Intent intent = new Intent(this, FormActivity.class);
        switch (productType) {
            case Constants.ProductType.STOCK:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.STOCK);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.SELL);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.FII:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FII);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.SELL);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.CURRENCY:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.CURRENCY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.SELL);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.FIXED:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FIXED);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.SELL);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.FUND:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FUND);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.SELL);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.TREASURY:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.TREASURY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.SELL);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.OTHERS:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.OTHERS);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.SELL);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onProductDetails(int productType, String itemId){
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        switch (productType) {
            case Constants.ProductType.STOCK:
                // Sends symbol of clicked stock to details acitivity
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.STOCK);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, itemId);
                startActivity(intent);
                break;
            case Constants.ProductType.FII:
                // Sends symbol of clicked stock to details acitivity
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FII);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, itemId);
                startActivity(intent);
                break;
            case Constants.ProductType.CURRENCY:
                // Sends symbol of clicked stock to details acitivity
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.CURRENCY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, itemId);
                startActivity(intent);
                break;
            case Constants.ProductType.FIXED:
                // Sends symbol of clicked stock to details acitivity
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FIXED);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, itemId);
                startActivity(intent);
                break;
            case Constants.ProductType.FUND:
                // Sends symbol of clicked stock to details acitivity
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FUND);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, itemId);
                startActivity(intent);
                break;
            case Constants.ProductType.TREASURY:
                // Sends symbol of clicked stock to details acitivity
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.TREASURY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, itemId);
                startActivity(intent);
                break;
            case Constants.ProductType.OTHERS:
                // Sends symbol of clicked stock to details acitivity
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.OTHERS);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, itemId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onEditProduct(int productType, String symbol){
        Intent intent = new Intent(this, FormActivity.class);
        switch (productType) {
            case Constants.ProductType.STOCK:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.STOCK);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.FII:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FII);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.CURRENCY:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.CURRENCY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.FIXED:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FIXED);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.FUND:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.FUND);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.TREASURY:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.TREASURY);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            case Constants.ProductType.OTHERS:
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.OTHERS);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_STATUS, Constants.Type.EDIT);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL, symbol);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    // Refresh the portfolio by getting the values from their respective services and updating on the tables
    public void refreshPortfolio(){
        //Stock Refresh
        Intent mStockIncomeService = new Intent(this, StockIncomeIntentService.class);

        String[] affectedColumn = {PortfolioContract.StockData.COLUMN_SYMBOL};
        String selection = PortfolioContract.StockData.COLUMN_STATUS + " = ?";
        String[] selectionArguments = {String.valueOf(Constants.Status.ACTIVE)};

        Cursor queryCursor = this.getContentResolver().query(
                PortfolioContract.StockData.URI, affectedColumn,
                selection, selectionArguments, null);

        // For each symbol found on StockData, add to service make webservice query and update
        if (queryCursor.getCount() > 0) {
            String symbol = "";
            queryCursor.moveToFirst();
            do {
                if (!queryCursor.isLast()){
                    symbol += queryCursor.getString(queryCursor.getColumnIndex
                            (PortfolioContract.StockData.COLUMN_SYMBOL))+",";
                } else{
                    symbol += queryCursor.getString(queryCursor.getColumnIndex
                            (PortfolioContract.StockData.COLUMN_SYMBOL));
                }
            } while (queryCursor.moveToNext());
            // Stock quotes called inside StockIncomeIntentService

            //Stock Incomes
            mStockIncomeService.putExtra(StockIncomeIntentService.ADD_SYMBOL, symbol);
            mStockIncomeService.putExtra(StockIncomeIntentService.PREMIUM, isPremium());
            startService(mStockIncomeService);
        } else{
            // Clear menu progressbar so it is not set indefinitely
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.STOCK));
        }

        // Treasury Refresh
        Intent mTreasuryServiceIntent = new Intent(this, TreasuryIntentService
                .class);

        String[] affectedColumn4 = {PortfolioContract.TreasuryData.COLUMN_SYMBOL};
        String selection4 = PortfolioContract.TreasuryData.COLUMN_STATUS + " = ?";
        String[] selectionArguments4 = {String.valueOf(Constants.Status.ACTIVE)};

        queryCursor = this.getContentResolver().query(
                PortfolioContract.TreasuryData.URI, affectedColumn4,
                selection4, selectionArguments4, null);

        // For each symbol found on StockData, add to service make webservice query and update
        if (queryCursor.getCount() > 0) {
            String symbol = "";
            queryCursor.moveToFirst();
            do {
                if (!queryCursor.isLast()){
                    symbol += queryCursor.getString(queryCursor.getColumnIndex
                            (PortfolioContract.TreasuryData.COLUMN_SYMBOL))+",";
                } else{
                    symbol += queryCursor.getString(queryCursor.getColumnIndex
                            (PortfolioContract.TreasuryData.COLUMN_SYMBOL));
                }
            } while (queryCursor.moveToNext());
            mTreasuryServiceIntent.putExtra(TreasuryIntentService.ADD_SYMBOL, symbol);
            mTreasuryServiceIntent.putExtra(TreasuryIntentService.PREMIUM, isPremium());
            startService(mTreasuryServiceIntent);
        } else{
            // Clear menu progressbar so it is not set indefinitely
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.TREASURY));
        }

        // Fixed Income Refresh
        Intent mCdiServiceIntent = new Intent(this, FixedIntentService
                .class);

        String[] affectedColumn5 = {PortfolioContract.FixedData.COLUMN_SYMBOL};
        String selection5 = PortfolioContract.FixedData.COLUMN_STATUS + " = ?";
        String[] selectionArguments5 = {String.valueOf(Constants.Status.ACTIVE)};

        queryCursor = this.getContentResolver().query(
                PortfolioContract.FixedData.URI, affectedColumn5,
                selection5, selectionArguments5, null);

        // For each symbol found on StockData, add to service make webservice query and update
        if (queryCursor.getCount() > 0) {
            String symbol = "";
            queryCursor.moveToFirst();
            do {
                if (!queryCursor.isLast()){
                    symbol += queryCursor.getString(queryCursor.getColumnIndex
                            (PortfolioContract.FixedData.COLUMN_SYMBOL))+",";
                } else{
                    symbol += queryCursor.getString(queryCursor.getColumnIndex
                            (PortfolioContract.FixedData.COLUMN_SYMBOL));
                }
            } while (queryCursor.moveToNext());
            mCdiServiceIntent.putExtra(FixedIntentService.ADD_SYMBOL, symbol);
            startService(mCdiServiceIntent);
        } else{
            // Clear menu progressbar so it is not set indefinitely
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.FIXED));
        }

        // Fund Refresh

        String[] affectedColumn6 = {PortfolioContract.FixedData.COLUMN_SYMBOL};
        String selection6 = PortfolioContract.FixedData.COLUMN_STATUS + " = ?";
        String[] selectionArguments6 = {String.valueOf(Constants.Status.ACTIVE)};

        queryCursor = this.getContentResolver().query(
                PortfolioContract.FundData.URI, affectedColumn6,
                selection6, selectionArguments6, null);

        // For each symbol found on StockData, add to service make webservice query and update
        if (queryCursor.getCount() > 0) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.FUND));
        } else{
            // Clear menu progressbar so it is not set indefinitely
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.FUND));
        }

        //Currency Refresh

        String[] affectedColumn3 = {PortfolioContract.CurrencyData.COLUMN_SYMBOL};

        queryCursor = this.getContentResolver().query(
                PortfolioContract.CurrencyData.URI, affectedColumn3,
                null, null, null);

        // For each symbol found on CurrencyData, add to service make webservice query and update
        if (queryCursor.getCount() > 0) {
            String symbol = "";
            String currencySymbol = "";
            String cryptoSymbol = "";
            queryCursor.moveToFirst();
            do {
                // Prepare symbols of crypto and normal currency to send on Intent Services
                symbol = queryCursor.getString(queryCursor.getColumnIndex
                        (PortfolioContract.CurrencyData.COLUMN_SYMBOL));
                if (symbol.equalsIgnoreCase("BTC") || symbol.equalsIgnoreCase("LTC")){
                    // Crypto currency
                    if (cryptoSymbol == "") {
                        cryptoSymbol += symbol;
                    } else {
                        cryptoSymbol += "," + symbol;
                    }
                } else {
                    // Normal currency
                    if (currencySymbol == "") {
                        currencySymbol += symbol;
                    } else {
                        currencySymbol += "," + symbol;
                    }
                }
            } while (queryCursor.moveToNext());

            // Start Intent Service to update currency
            if (currencySymbol != "") {
                Intent mCurrencyServiceIntent = new Intent(this, CurrencyIntentService
                        .class);
                mCurrencyServiceIntent.putExtra(CurrencyIntentService.ADD_SYMBOL, currencySymbol);
                startService(mCurrencyServiceIntent);
            }

            // Start Intent Service to update crypto currency
            if (cryptoSymbol != ""){
                Intent mCryptoServiceIntent = new Intent(this, CryptoIntentService
                        .class);
                mCryptoServiceIntent.putExtra(CurrencyIntentService.ADD_SYMBOL, cryptoSymbol);
                startService(mCryptoServiceIntent);
            }
        } else{
            // Clear menu progressbar so it is not set indefinitely
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.CURRENCY));
        }

        //Fii Refresh
        Intent mFiiIncomeService = new Intent(this, FiiIncomeIntentService.class);

        String[] affectedColumn2 = {PortfolioContract.FiiData.COLUMN_SYMBOL};
        String selection2 = PortfolioContract.FiiData.COLUMN_STATUS + " = ?";
        String[] selectionArguments2 = {String.valueOf(Constants.Status.ACTIVE)};

        queryCursor = this.getContentResolver().query(
                PortfolioContract.FiiData.URI, affectedColumn2,
                selection2, selectionArguments2, null);

        // For each symbol found on FiiData, add to service make webservice query and update
        if (queryCursor.getCount() > 0) {
            String symbol = "";
            queryCursor.moveToFirst();
            do {
                if (!queryCursor.isLast()){
                    symbol += queryCursor.getString(queryCursor.getColumnIndex
                            (PortfolioContract.FiiData.COLUMN_SYMBOL))+",";
                } else{
                    symbol += queryCursor.getString(queryCursor.getColumnIndex
                            (PortfolioContract.FiiData.COLUMN_SYMBOL));
                }
            } while (queryCursor.moveToNext());
            // Fii quotes called inside StockIncomeIntentService

            //Fii Incomes
            mFiiIncomeService.putExtra(FiiIncomeIntentService.ADD_SYMBOL, symbol);
            mFiiIncomeService.putExtra(FiiIncomeIntentService.PREMIUM, isPremium());
            startService(mFiiIncomeService);
        } else{
            // Clear menu progressbar so it is not set indefinitely
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.FII));
        }
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
            case Constants.IncomeType.FUND:
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.FUND);
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
            case Constants.IncomeType.FIXED:
                // Sends id of clicked income to income details acitivity
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.FIXED);
                intent.putExtra(Constants.Extra.EXTRA_INCOME_ID, id);
                startActivity(intent);
                break;
            case Constants.IncomeType.FUND:
                // Sends id of clicked income to income details acitivity
                intent.putExtra(Constants.Extra.EXTRA_INCOME_TYPE, Constants.IncomeType.FUND);
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

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            if (result.isFailure()) {
                // handle error here
            }
            else {
                // does the user have the premium upgrade?
                mIsPremium = (inventory.hasPurchase("mensal") || inventory.hasPurchase("semestral"));

                if (inventory.hasPurchase("mensal")){
                    mPremiumType = "mensal";
                } else if(inventory.hasPurchase("semestral")){
                    mPremiumType = "semestral";
                }

                // update UI accordingly
                if (!mIsPremium) {
                    // Show premium board on Portfolio Fragment
                    FragmentManager supportFragmentManager = getSupportFragmentManager();
                    Fragment fragment = supportFragmentManager.findFragmentByTag("PortfolioMainFragment");
                    if (fragment != null) {
                        ((PortfolioMainFragment) fragment).showPremium();
                    }
                }
            }
        }
    };

    public void signPremium(String type){
        try {
            if(mIsPremium){
                List<String> oldSkus = new ArrayList<String>();
                oldSkus.add(mPremiumType);
                mHelper.launchPurchaseFlow(this,type,"subs",oldSkus,Constants.Intent.PURCHASE_SUBSCRIPTION,
                        mPurchaseFinishedListener,"");
            } else {
                mHelper.launchSubscriptionPurchaseFlow(this, type, Constants.Intent.PURCHASE_SUBSCRIPTION,
                        mPurchaseFinishedListener, "");
            }
        } catch (IabHelper.IabAsyncInProgressException e){
            Log.e(LOG_TAG, e.toString());
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener(){
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            mHelper.flagEndAsync();
            if (result.isFailure()) {
                Log.d(LOG_TAG, "Error purchasing: " + result);
                return;
            }
            else if (purchase.getSku().equals("mensal") || purchase.getSku().equals("semestral")) {
                // give user access to premium content and update the UI
                finish();
                startActivity(getIntent());
                System.exit(0);
            }
        }
    };

    public boolean isPremium(){
        return mIsPremium;
    }
}
