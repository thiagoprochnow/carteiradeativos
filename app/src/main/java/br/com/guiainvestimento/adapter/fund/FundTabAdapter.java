package br.com.guiainvestimento.adapter.fund;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.fragment.fundincome.FundDataFragment;
import br.com.guiainvestimento.fragment.fundincome.FundOverviewFragment;


public class FundTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = FundTabAdapter.class.getSimpleName();
    final private Context mContext;

    public FundTabAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }
    @Override
    public int getCount(){
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position){
        if(position == 0){
            return mContext.getString(R.string.fund_overview);
        }

        return mContext.getString(R.string.fund_portfolio);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            return new FundOverviewFragment();
        }
        else {
            return new FundDataFragment();
        }
    }
}
