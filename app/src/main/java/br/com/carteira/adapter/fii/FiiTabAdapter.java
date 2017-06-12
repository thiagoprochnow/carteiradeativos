package br.com.carteira.adapter.fii;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.carteira.R;
import br.com.carteira.fragment.fii.FiiDataFragment;
import br.com.carteira.fragment.fii.FiiIncomesMainFragment;
import br.com.carteira.fragment.fii.FiiOverviewFragment;
import br.com.carteira.fragment.fii.SoldFiiDataFragment;


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
            Log.d(LOG_TAG, "Loading FiiOverviewFragment()");
            return new FiiOverviewFragment();
        }
        else if (position == 1){
            Log.d(LOG_TAG, "Loading FiiDataFragment()");
            return new FiiDataFragment();
        } else if (position == 2) {
            Log.d(LOG_TAG, "Loading SoldFiiDataFragment()");
            return new SoldFiiDataFragment();
        } else {
            Log.d(LOG_TAG, "Loading FiiIncomesMainFragment()");
            return new FiiIncomesMainFragment();
        }
    }
}