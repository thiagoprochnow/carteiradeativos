package br.com.carteira.fragment.treasury;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.carteira.R;
import br.com.carteira.activity.MainActivity;
import br.com.carteira.adapter.fii.FiiDetailsTabAdapter;
import br.com.carteira.adapter.fii.FiiTabAdapter;
import br.com.carteira.adapter.treasury.TreasuryDetailsTabAdapter;
import br.com.carteira.adapter.treasury.TreasuryTabAdapter;
import br.com.carteira.fragment.BaseFragment;

public class TreasuryTabFragment extends BaseFragment {

    protected Context mContext;

    private static final String LOG_TAG = TreasuryTabFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view;
        ViewPager viewPager;
        String activityName = getActivity().getClass().getSimpleName();
        // Check if it is a Tab for TreasuryData tabs or TreasuryDetails tabs
        if (activityName.equals(MainActivity.class.getSimpleName())){
            view = inflater.inflate(R.layout.fragment_treasury_tab, container, false);
            viewPager = (ViewPager) view.findViewById(R.id.treasuryViewPager);
            Log.d(LOG_TAG, "TreasuryTabAdapter");
            viewPager.setAdapter(new TreasuryTabAdapter(mContext, getChildFragmentManager()));
        } else {
            view = inflater.inflate(R.layout.fragment_treasury_details_tab, container, false);
            viewPager = (ViewPager) view.findViewById(R.id.treasuryViewPager);
            Log.d(LOG_TAG, "TreasuryDetailsTabAdapter");
            viewPager.setAdapter(new TreasuryDetailsTabAdapter(mContext, getChildFragmentManager()));
        }

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.treasuryTabLayout);
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
