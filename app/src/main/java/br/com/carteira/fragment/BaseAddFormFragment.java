package br.com.carteira.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.EditText;

import br.com.carteira.R;

public abstract class BaseAddFormFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enables the menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Common menu for add forms
        inflater.inflate(R.menu.add_form_menu, menu);
    }

    // Function that validate if an EditText field is empty
    protected boolean isEditTextEmpty(EditText symbol) {
        Editable editable = symbol.getText();
        if (editable != null && TextUtils.isEmpty(editable.toString())) {
            return true;
        } else {
            return false;
        }
    }
}
