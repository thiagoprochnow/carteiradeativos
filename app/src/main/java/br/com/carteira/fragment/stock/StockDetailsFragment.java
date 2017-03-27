package br.com.carteira.fragment.stock;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFragment;


public class StockDetailsFragment extends BaseFragment {
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_stock_details, container, false);
        // TODO: Only for testing, will change later, sets text as symbol passed by intent
        TextView textSymbol = (TextView) mView.findViewById(R.id.textSymbol);
        Intent intent = getActivity().getIntent();
        String symbol = intent.getStringExtra(Constants.Extra.EXTRA_PRODUCT_SYMBOL);
        textSymbol.setText(symbol);
        return mView;
    }
}
