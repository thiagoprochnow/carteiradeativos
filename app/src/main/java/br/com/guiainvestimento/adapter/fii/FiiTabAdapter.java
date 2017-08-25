package br.com.guiainvestimento.adapter.fii;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.fragment.fii.FiiDataFragment;
import br.com.guiainvestimento.fragment.fii.FiiIncomesMainFragment;
import br.com.guiainvestimento.fragment.fii.FiiOverviewFragment;
import br.com.guiainvestimento.fragment.fii.SoldFiiDataFragment;


public class FiiTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = FiiTabAdapter.class.getSimpleName();
    final private Context mContext;

    public FiiTabAdapter(Context context, FragmentManager fm) {
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
            return mContext.getString(R.string.fii_overview);
        }
         else if(position == 1){
            return mContext.getString(R.string.fii_portfolio);
        } else if(position == 2){
            return mContext.getString(R.string.fii_history);
        }
        return mContext.getString(R.string.fii_incomes);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            return new FiiOverviewFragment();
        }
        else if (position == 1){
            return new FiiDataFragment();
        } else if (position == 2) {
            return new SoldFiiDataFragment();
        } else {
            return new FiiIncomesMainFragment();
        }
    }
}
