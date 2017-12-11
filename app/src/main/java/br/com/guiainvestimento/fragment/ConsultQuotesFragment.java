package br.com.guiainvestimento.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import br.com.guiainvestimento.api.service.StockIntentService;
import br.com.guiainvestimento.common.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsultQuotesFragment extends BaseFragment{
    private static final String LOG_TAG = ConsultQuotesFragment.class.getSimpleName();

    @BindView(R.id.inputConsultQuote)
    AutoCompleteTextView mInputSymbolView;

    @BindView(R.id.consultQuoteLabel)
    TextView mResultLabelView;

    @BindView(R.id.consultQuoteResult)
    TextView mResultQuoteView;

    @BindView(R.id.consultQuoteButton)
    LinearLayout mConsultButtonView;

    @BindView(R.id.consultProgressBar)
    ProgressBar mConsultProgressBar;

    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_consult_quotes, container, false);
        ButterKnife.bind(this, mView);
        // Sets autocomplete for stock symbol
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_dropdown_item_1line, Constants.Symbols.ALL);
        mInputSymbolView.setAdapter(adapter);
        mConsultButtonView.setOnClickListener(consultQuoteOnClick());

        BroadcastReceiver receiverQuote = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("quote")){
                    mResultLabelView.setVisibility(View.VISIBLE);
                    mResultQuoteView.setVisibility(View.VISIBLE);
                    mConsultProgressBar.setVisibility(View.GONE);
                    Locale locale = new Locale("pt", "BR");
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
                    String quote = intent.getStringExtra("quote");
                    if (quote != "" && quote != "error") {
                        double doubleQuote = Double.parseDouble(quote);
                        mResultQuoteView.setText(formatter.format(doubleQuote));
                    } else {
                        mResultQuoteView.setText(mContext.getResources().getString(R.string.consult_quotes_error));
                    }
                }
            }
        };
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiverQuote, new IntentFilter(Constants.Receiver.CONSULT_QUOTE));


        getActivity().findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        return mView;
    }

    private View.OnClickListener consultQuoteOnClick() {
        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate for each inputted value
                boolean isValidSymbol = isValidStockSymbol(mInputSymbolView);
                if (isValidSymbol) {
                    String symbol = mInputSymbolView.getText().toString();

                    mResultQuoteView.setVisibility(View.GONE);
                    mConsultProgressBar.setVisibility(View.VISIBLE);

                    if (symbol.equalsIgnoreCase("Dolar") || symbol.equalsIgnoreCase("Euro")){

                    } else if (symbol.equalsIgnoreCase("Bitcoin") || symbol.equalsIgnoreCase("Litecoin")){

                    } else {

                        Intent mStockServiceIntent = new Intent(mContext, StockIntentService
                                .class);
                        mStockServiceIntent.putExtra(StockIntentService.CONSULT_SYMBOL, symbol);
                        mContext.startService(mStockServiceIntent);
                    }
                } else{
                    mInputSymbolView.setError(mContext.getString(R.string.wrong_code));
                }
            }
        };

        return onclick;
    }
}
