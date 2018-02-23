package br.com.guiainvestimento.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.activity.MainActivity;
import br.com.guiainvestimento.api.service.CryptoIntentService;
import br.com.guiainvestimento.api.service.CurrencyIntentService;
import br.com.guiainvestimento.api.service.StockIntentService;
import br.com.guiainvestimento.common.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PremiumEditionFragment extends BaseFragment{
    private static final String LOG_TAG = PremiumEditionFragment.class.getSimpleName();

    @BindView(R.id.premium_sign_button)
    LinearLayout mSignButton;

    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_premium_edition, container, false);
        ButterKnife.bind(this, mView);
        mSignButton.setOnClickListener(signButtonOnClick);
        // Sets autocomplete for stock symbol
        return mView;
    }

    public View.OnClickListener signButtonOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).signPremium();
        }
    };
}
