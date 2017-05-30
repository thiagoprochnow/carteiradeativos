package br.com.carteira.fragment.fixedincome;

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
import br.com.carteira.adapter.fixedincome.FixedDetailsTabAdapter;
import br.com.carteira.adapter.fixedincome.FixedTabAdapter;
import br.com.carteira.fragment.BaseFragment;

public class FixedTabFragment extends BaseFragment {

    protected Context mContext;

    private static final String LOG_TAG = FixedTabFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view;
        ViewPager viewPager;
        String activityName = getActivity().getClass().getSimpleName();
        // Check if it is a Tab for FixedData tabs or FixedDetails tabs
        if (activityName.equals(MainActivity.class.getSimpleName())){
            view = inflater.inflate(R.layout.fragment_fixed_tab, container, false);
            viewPager = (ViewPager) view.findViewById(R.id.fixedViewPager);
            Log.d(LOG_TAG, "FixedTabAdapter");
            viewPager.setAdapter(new FixedTabAdapter(mContext, getChildFragmentManager()));
        } else {
            view = inflater.inflate(R.layout.fragment_fixed_details_tab, container, false);
            viewPager = (ViewPager) view.findViewById(R.id.fixedViewPager);
            Log.d(LOG_TAG, "FixedDetailsTabAdapter");
            viewPager.setAdapter(new FixedDetailsTabAdapter(mContext, getChildFragmentManager()));
        }

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.fixedTabLayout);
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
