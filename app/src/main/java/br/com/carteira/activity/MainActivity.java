package br.com.carteira.activity;

import android.os.Bundle;

import br.com.carteira.R;
import br.com.carteira.fragment.PortfolioMainFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setupNavDrawer();
        replaceFragment(new PortfolioMainFragment());
    }
}
