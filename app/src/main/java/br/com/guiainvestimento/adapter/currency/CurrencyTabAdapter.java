package br.com.guiainvestimento.adapter.currency;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.fragment.currency.CurrencyDataFragment;
import br.com.guiainvestimento.fragment.currency.CurrencyOverviewFragment;
import br.com.guiainvestimento.fragment.currency.SoldCurrencyDataFragment;


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
