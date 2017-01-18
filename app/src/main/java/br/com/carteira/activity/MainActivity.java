package br.com.carteira.activity;

import android.os.Bundle;

import br.com.carteira.R;
import br.com.carteira.fragment.PortfolioMainFragment;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar();
        setupNavDrawer();
        // Checks if savedInstanceState is null so it will not load portfolio fragment on screen rotation
        // and fisical keyboard opening
        if (savedInstanceState == null) {
            // Inicializa o aplicativo com a Fragment da carteira geral
            replaceFragment(new PortfolioMainFragment());
        }
    }
}
