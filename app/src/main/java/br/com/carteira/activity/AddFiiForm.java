package br.com.carteira.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import br.com.carteira.R;
import br.com.carteira.fragment.AddFiiFormFragment;
import br.com.carteira.fragment.AddStockFormFragment;

/**
 * Created by thipr on 1/20/2017.
 */

public class AddFiiForm extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_fii_form);
        replaceFragment(new AddFiiFormFragment());
    }

    // Sets the fragment on the container according to the selected item in menu
    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.add_fii_form_container, frag,
                "TAG").commit();
    }
}
