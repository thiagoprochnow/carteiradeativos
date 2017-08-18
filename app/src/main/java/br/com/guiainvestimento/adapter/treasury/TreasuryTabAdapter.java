package br.com.guiainvestimento.adapter.treasury;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.fragment.treasury.SoldTreasuryDataFragment;
import br.com.guiainvestimento.fragment.treasury.TreasuryDataFragment;
import br.com.guiainvestimento.fragment.treasury.TreasuryIncomesMainFragment;
import br.com.guiainvestimento.fragment.treasury.TreasuryOverviewFragment;


public class TreasuryTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = TreasuryTabAdapter.class.getSimpleName();
    final private Context mContext;

    public TreasuryTabAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }
    @Override
    public int getCount(){
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position){
        if(position == 0){
            return mContext.getString(R.string.treasury_overview);
        }
         else if(position == 1){
            return mContext.getString(R.string.treasury_portfolio);
        } else if(position == 2){
            return mContext.getString(R.string.treasury_history);
        }
        return mContext.getString(R.string.treasury_incomes);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            return new TreasuryOverviewFragment();
        }
        else if (position == 1){
            return new TreasuryDataFragment();
        } else if (position == 2) {
            return new SoldTreasuryDataFragment();
        } else {
            return new TreasuryIncomesMainFragment();
        }
    }
}
