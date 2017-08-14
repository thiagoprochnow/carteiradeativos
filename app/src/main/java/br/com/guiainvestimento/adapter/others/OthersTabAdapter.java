package br.com.guiainvestimento.adapter.others;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.fragment.others.OthersDataFragment;
import br.com.guiainvestimento.fragment.others.OthersIncomesMainFragment;
import br.com.guiainvestimento.fragment.others.OthersOverviewFragment;


public class OthersTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = OthersTabAdapter.class.getSimpleName();
    final private Context mContext;

    public OthersTabAdapter(Context context, FragmentManager fm) {
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
            return mContext.getString(R.string.others_overview);
        } else if (position == 1){
            return mContext.getString(R.string.others_portfolio);
        }

        return mContext.getString(R.string.others_incomes);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            Log.d(LOG_TAG, "Loading OthersOverviewFragment()");
            return new OthersOverviewFragment();
        }
        else if (position == 1){
            Log.d(LOG_TAG, "Loading OthersDataFragment()");
            return new OthersDataFragment();
        } else{
            Log.d(LOG_TAG, "Loading OthersDataFragment()");
            return new OthersIncomesMainFragment();
        }
    }
}
