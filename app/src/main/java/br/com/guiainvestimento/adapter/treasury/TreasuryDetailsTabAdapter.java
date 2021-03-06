package br.com.guiainvestimento.adapter.treasury;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.fragment.treasury.TreasuryDetailsFragment;
import br.com.guiainvestimento.fragment.treasury.TreasuryIncomesFragment;

public class TreasuryDetailsTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = TreasuryDetailsTabAdapter.class.getSimpleName();
    final private Context mContext;

    public TreasuryDetailsTabAdapter(Context context, FragmentManager fm) {
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
        return mContext.getString(R.string.treasury_incomes);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            return new TreasuryDetailsFragment();
        } else {
            return new TreasuryIncomesFragment();
        }
    }
}
