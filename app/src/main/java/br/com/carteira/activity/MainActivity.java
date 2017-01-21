package br.com.carteira.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.fragment.CurrencyMainFragment;
import br.com.carteira.fragment.FiiMainFragment;
import br.com.carteira.fragment.FixedIncomeMainFragment;
import br.com.carteira.fragment.PortfolioMainFragment;
import br.com.carteira.fragment.StatisticMainFragment;
import br.com.carteira.fragment.StockMainFragment;
import br.com.carteira.listener.AddProductListener;

// Main app Activity
public class MainActivity extends AppCompatActivity implements AddProductListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    protected DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setupNavDrawer();
        // Checks if savedInstanceState is null so it will not load portfolio fragment on screen
        // rotation
        // and hard keyboard opening
        if (savedInstanceState == null) {
            replaceFragment(new PortfolioMainFragment());
        }
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
                replaceFragment(new PortfolioMainFragment());
                break;
            case R.id.nav_item_fixed_income:
                replaceFragment(new FixedIncomeMainFragment());
                break;
            case R.id.nav_item_stocks:
                replaceFragment(new StockMainFragment());
                break;
            case R.id.nav_item_fii:
                replaceFragment(new FiiMainFragment());
                break;
            case R.id.currency:
                replaceFragment(new CurrencyMainFragment());
                break;
            case R.id.nav_item_statistic:
                replaceFragment(new StatisticMainFragment());
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
    public void onAddProduct(int productType) {
        switch (productType) {
            case Constants.ProductType.STOCK:
                Intent intent = new Intent(this, AddFormActivity.class);
                intent.putExtra(Constants.Extra.EXTRA_PRODUCT_TYPE, Constants.ProductType.STOCK);
                startActivity(intent);
                break;
            default:
                Log.d(LOG_TAG, "Could not launch the AddFormActivity.");
                break;
        }
    }
}
