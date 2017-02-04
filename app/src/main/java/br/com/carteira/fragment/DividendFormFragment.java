package br.com.carteira.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.carteira.R;
import br.com.carteira.common.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class DividendFormFragment extends BaseFormFragment {
    private View mView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_done:
                if (addDividend()) {
                    getActivity().finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_dividend_form, container, false);
        EditText inputDateView = (EditText) mView.findViewById(R.id.inputDividendReceiveDate);
        // Adding current date to Buy Date field
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        inputDateView.setText(simpleDateFormat.format(new Date()));
        // Configure to show Spinner when clicked on the Date EditText field
        inputDateView.setOnClickListener(setDatePicker(inputDateView));
        return mView;
    }

    // Validate inputted values and add the dividend to the stock portfolio
    private boolean addDividend() {
        // TODO: Create addDividend to add on database
        Intent intent = getActivity().getIntent();
        String symbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        Toast.makeText(getContext(),"Dividendo adicionado para: "+ symbol, Toast.LENGTH_LONG).show();
        return true;
    }

}
