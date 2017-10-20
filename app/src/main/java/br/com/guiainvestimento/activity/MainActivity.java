package br.com.guiainvestimento.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.api.service.CurrencyIntentService;
import br.com.guiainvestimento.api.service.FiiIntentService;
import br.com.guiainvestimento.api.service.StockIntentService;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.fragment.AboutFragment;
import br.com.guiainvestimento.fragment.BackupRestoreFragment;
import br.com.guiainvestimento.fragment.ComingSoonFragment;
import br.com.guiainvestimento.fragment.currency.CurrencyTabFragment;
import br.com.guiainvestimento.fragment.fii.FiiTabFragment;
import br.com.guiainvestimento.fragment.PortfolioMainFragment;
import br.com.guiainvestimento.fragment.fixedincome.FixedTabFragment;
import br.com.guiainvestimento.fragment.others.OthersTabFragment;
import br.com.guiainvestimento.fragment.stock.StockTabFragment;
import br.com.guiainvestimento.fragment.treasury.TreasuryTabFragment;
import br.com.guiainvestimento.listener.IncomeDetailsListener;
import br.com.guiainvestimento.listener.ProductListener;

// Main app Activity
public class MainActivity extends AppCompatActivity implements ProductListener, IncomeDetailsListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    protected DrawerLayout mDrawerLayout;

    private FirebaseAnalytics mFirebaseAnalytics;

    boolean mStockReceiver = false;
    boolean mFiiReceiver = false;
    boolean mCurrencyReceiver = false;

    GoogleApiClient mGoogleApiClient;

    private Menu mMenu;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setupNavDrawer();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Checks if savedInstanceState is null so it will not load portfolio fragment on screen
        // rotation
        // and hard keyboard opening
        if (savedInstanceState == null) {
            replaceFragment(new PortfolioMainFragment());
        }

        BroadcastReceiver receiverStock = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mCurrencyReceiver && mFiiReceiver) {
                    // Ends progress bar on menu when portfolio is updated
                    mMenu.findItem(R.id.menu_refresh).setActionView(null);
                    // Reset receiver flags
                    mCurrencyReceiver = false;
                    mFiiReceiver = false;
                    mStockReceiver = false;
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
                if (mCurrencyReceiver && mStockReceiver) {
                    // Ends progress bar on menu when portfolio is updated
                    mMenu.findItem(R.id.menu_refresh).setActionView(null);
                    // Reset receiver flags
                    mCurrencyReceiver = false;
                    mFiiReceiver = false;
                    mStockReceiver = false;
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
                if (mFiiReceiver && mStockReceiver) {
                    // Ends progress bar on menu when portfolio is updated
                    mMenu.findItem(R.id.menu_refresh).setActionView(null);
                    // Reset receiver flags
                    mCurrencyReceiver = false;
                    mFiiReceiver = false;
                    mStockReceiver = false;
                } else {
                    // Sets StockReceiver flag
                    mCurrencyReceiver = true;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverCurrency, new IntentFilter(Constants.Receiver.CURRENCY));
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
            // Sets the image of the header of the navigation view
            setHeaderValues(navigationView, R.id.containerNavDrawerListViewHeader,
                    R.drawable.nav_drawer_header);
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
                replaceFragment(new PortfolioMainFragment());
                break;
            case R.id.nav_item_treasury:
                setTitle(R.string.title_treasury);
                replaceFragment(new TreasuryTabFragment());
                break;
            case R.id.nav_item_fixed_income:
                setTitle(R.string.title_fixed);
                replaceFragment(new FixedTabFragment());
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
            case R.id.nav_item_about:
                setTitle(R.string.title_about);
                replaceFragment(new AboutFragment());
                break;
            case R.id.nav_item_coming_soon:
                setTitle(R.string.title_coming_soon);
                replaceFragment(new ComingSoonFragment());
                break;
            case R.id.nav_item_backup_restore:
                setTitle(R.string.title_backup_restore);
                replaceFragment(new BackupRestoreFragment());
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
    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag,
                "TAG").commit();
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

    // Set Drawer header values and images
    public static void setHeaderValues(View navDrawerView, int listViewContainerId, int
            imgNavDrawerHeaderId) {
        View view = navDrawerView.findViewById(listViewContainerId);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            ImageView imgUserBackground = (ImageView) view.findViewById(R.id.imgUserBackground);
            if (imgUserBackground != null) {
                imgUserBackground.setImageResource(imgNavDrawerHeaderId);
            }
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
        Intent mStockServiceIntent = new Intent(this, StockIntentService
                .class);

        String[] affectedColumn = {PortfolioContract.StockData.COLUMN_SYMBOL};

        Cursor queryCursor = this.getContentResolver().query(
                PortfolioContract.StockData.URI, affectedColumn,
                null, null, null);

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
            mStockServiceIntent.putExtra(StockIntentService.ADD_SYMBOL, symbol);
            startService(mStockServiceIntent);
        } else{
            // Clear menu progressbar so it is not set indefinitely
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.STOCK));
        }

        //Fii Refresh
        Intent mFiiServiceIntent = new Intent(this, FiiIntentService
                .class);

        String[] affectedColumn2 = {PortfolioContract.FiiData.COLUMN_SYMBOL};

        queryCursor = this.getContentResolver().query(
                PortfolioContract.FiiData.URI, affectedColumn2,
                null, null, null);

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
            mFiiServiceIntent.putExtra(FiiIntentService.ADD_SYMBOL, symbol);
            startService(mFiiServiceIntent);
        } else{
            // Clear menu progressbar so it is not set indefinitely
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.FII));
        }

        //Currency Refresh
        Intent mCurrencyServiceIntent = new Intent(this, CurrencyIntentService
                .class);

        String[] affectedColumn3 = {PortfolioContract.CurrencyData.COLUMN_SYMBOL};

        queryCursor = this.getContentResolver().query(
                PortfolioContract.CurrencyData.URI, affectedColumn3,
                null, null, null);

        // For each symbol found on CurrencyData, add to service make webservice query and update
        if (queryCursor.getCount() > 0) {
            String symbol = "";
            queryCursor.moveToFirst();
            do {
                if (!queryCursor.isLast()){
                    symbol += queryCursor.getString(queryCursor.getColumnIndex
                            (PortfolioContract.CurrencyData.COLUMN_SYMBOL))+",";
                } else{
                    symbol += queryCursor.getString(queryCursor.getColumnIndex
                            (PortfolioContract.CurrencyData.COLUMN_SYMBOL));
                }
            } while (queryCursor.moveToNext());
            mCurrencyServiceIntent.putExtra(CurrencyIntentService.ADD_SYMBOL, symbol);
            startService(mCurrencyServiceIntent);
        } else{
            // Clear menu progressbar so it is not set indefinitely
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constants.Receiver.CURRENCY));
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
}
