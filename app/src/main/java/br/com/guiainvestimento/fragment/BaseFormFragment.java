package br.com.guiainvestimento.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import br.com.guiainvestimento.R;

public abstract class BaseFormFragment extends BaseFragment {

    private static final String LOG_TAG = BaseFormFragment.class.getSimpleName();

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
}
