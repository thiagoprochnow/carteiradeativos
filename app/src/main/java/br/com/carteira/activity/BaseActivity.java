package br.com.carteira.activity;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import br.com.carteira.R;
import br.com.carteira.fragments.StockMainFragment;
import br.com.carteira.fragments.PortfolioMainFragment;
import br.com.carteira.fragments.StatisticMainFragment;
import br.com.carteira.fragments.FiiMainFragment;
import br.com.carteira.fragments.CurrencyMainFragment;
import br.com.carteira.fragments.FixedIncomeMainFragment;
import livroandroid.lib.utils.NavDrawerUtil;

/**
 * Created by thipr on 11/9/2016.
 */

// Class basica da qual todos ou a maioria das activities vão herdar varias funções
public class BaseActivity extends livroandroid.lib.activity.BaseActivity {
    protected DrawerLayout drawerLayout;

    // Configura a Toolbar
    protected void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    // Configura o Nav Drawer
    protected void setupNavDrawer() {
        // Drawer Layout
        final ActionBar actionBar = getSupportActionBar();
        // Ícone do menu do nav drawer
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null && drawerLayout != null) {
            // Atualiza a imagem e textos do header
            NavDrawerUtil.setHeaderValues(navigationView, R.id.containerNavDrawerListViewHeader, R.drawable.nav_drawer_header, R.drawable.ic_logo_user, R.string.nav_drawer_username, R.string.nav_drawer_email);
            // Trata o evento de clique no menu.
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            // Seleciona a linha
                            menuItem.setChecked(true);
                            // Fecha o menu
                            drawerLayout.closeDrawers();
                            // Trata o evento do menu
                            onNavDrawerItemSelected(menuItem);
                            return true;
                        }
                    });
        }
    }

    // Trata o evento do menu lateral
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
                // Trata o clique no botão que abre o menu.
                if (drawerLayout != null) {
                    openDrawer();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    // Adiciona o fragment no centro da tela
    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag, "TAG").commit();
    }

    // Abre o menu lateral
    protected void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    // Fecha o menu lateral
    protected void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
}
