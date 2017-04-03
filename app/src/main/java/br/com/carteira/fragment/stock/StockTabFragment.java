package br.com.carteira.fragment.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.carteira.R;
import br.com.carteira.adapter.StockTabAdapter;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.BaseFragment;

public class StockTabFragment extends BaseFragment {

    protected Context mContext;

    private static final String LOG_TAG = StockTabFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_stock_tab, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.stockViewPager);
        viewPager.setAdapter(new StockTabAdapter(mContext, getChildFragmentManager()));

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.stockTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        int color = ContextCompat.getColor(mContext, R.color.white);
        tabLayout.setTabTextColors(color, color);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
