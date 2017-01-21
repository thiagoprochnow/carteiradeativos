package br.com.carteira.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import br.com.carteira.R;
import br.com.carteira.fragment.AddStockFormFragment;

/**
 * Created by thipr on 1/20/2017.
 */

public class AddStockForm extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_stock_form);
        replaceFragment(new AddStockFormFragment());
    }

    // Sets the fragment on the container according to the selected item in menu
    protected void replaceFragment(Fragment frag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.add_stock_form_container, frag,
                "TAG").commit();
    }
}
