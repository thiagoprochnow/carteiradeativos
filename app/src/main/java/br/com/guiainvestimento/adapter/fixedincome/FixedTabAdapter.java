package br.com.guiainvestimento.adapter.fixedincome;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.fragment.fixedincome.FixedDataFragment;
import br.com.guiainvestimento.fragment.fixedincome.FixedOverviewFragment;


public class FixedTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = FixedTabAdapter.class.getSimpleName();
    final private Context mContext;

    public FixedTabAdapter(Context context, FragmentManager fm) {
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
            return mContext.getString(R.string.fixed_overview);
        }

        return mContext.getString(R.string.fixed_portfolio);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            return new FixedOverviewFragment();
        }
        else {
            return new FixedDataFragment();
        }
    }
}
