package br.com.guiainvestimento.adapter.others;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.fragment.others.OthersDetailsFragment;
import br.com.guiainvestimento.fragment.others.OthersIncomesFragment;

public class OthersDetailsTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = OthersDetailsTabAdapter.class.getSimpleName();
    final private Context mContext;

    public OthersDetailsTabAdapter(Context context, FragmentManager fm) {
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
            return mContext.getString(R.string.operations);
        }
        return mContext.getString(R.string.others_incomes);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            Log.d(LOG_TAG, "Loading OthersDetailsFragment()");
            return new OthersDetailsFragment();
        } else {
            Log.d(LOG_TAG, "Loading OthersIncomesFragment()");
            return new OthersIncomesFragment();
        }
    }
}
