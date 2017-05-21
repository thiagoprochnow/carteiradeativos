package br.com.carteira.adapter.currency;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.carteira.R;
import br.com.carteira.fragment.currency.CurrencyDataFragment;
import br.com.carteira.fragment.currency.CurrencyOverviewFragment;
import br.com.carteira.fragment.currency.SoldCurrencyDataFragment;


public class CurrencyTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = CurrencyTabAdapter.class.getSimpleName();
    final private Context mContext;

    public CurrencyTabAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }
    @Override
    public int getCount(){
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position){
        if(position == 0){
            return mContext.getString(R.string.currency_overview);
        }
         else if(position == 1){
            return mContext.getString(R.string.currency_portfolio);
        } else {
            return mContext.getString(R.string.currency_history);
        }
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            Log.d(LOG_TAG, "Loading CurrencyOverviewFragment()");
            return new CurrencyOverviewFragment();
        }
        else if (position == 1){
            Log.d(LOG_TAG, "Loading CurrencyDataFragment()");
            return new CurrencyDataFragment();
        } else {
            Log.d(LOG_TAG, "Loading SoldFiiDataFragment()");
            return new SoldCurrencyDataFragment();
        }
    }
}
