package br.com.guiainvestimento.fragment.fund;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.adapter.fundincome.FundTabAdapter;
import br.com.guiainvestimento.fragment.BaseFragment;

public class FundTabFragment extends BaseFragment {

    protected Context mContext;

    private static final String LOG_TAG = FundTabFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view;
        ViewPager viewPager;
        String activityName = getActivity().getClass().getSimpleName();

        view = inflater.inflate(R.layout.fragment_fund_tab, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.fundViewPager);
        viewPager.setAdapter(new FundTabAdapter(mContext, getChildFragmentManager()));


        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.fundTabLayout);
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
