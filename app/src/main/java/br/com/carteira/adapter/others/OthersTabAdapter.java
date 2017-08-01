package br.com.carteira.adapter.others;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.carteira.R;
import br.com.carteira.fragment.fixedincome.FixedDataFragment;
import br.com.carteira.fragment.fixedincome.FixedOverviewFragment;
import br.com.carteira.fragment.others.OthersDataFragment;
import br.com.carteira.fragment.others.OthersOverviewFragment;


public class OthersTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = OthersTabAdapter.class.getSimpleName();
    final private Context mContext;

    public OthersTabAdapter(Context context, FragmentManager fm) {
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
            return mContext.getString(R.string.others_overview);
        }

        return mContext.getString(R.string.others_portfolio);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            Log.d(LOG_TAG, "Loading OthersOverviewFragment()");
            return new OthersOverviewFragment();
        }
        else {
            Log.d(LOG_TAG, "Loading OthersDataFragment()");
            return new OthersDataFragment();
        }
    }
}
