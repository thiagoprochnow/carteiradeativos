package br.com.carteira.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.carteira.R;
import br.com.carteira.fragment.stock.StockDetailsFragment;
import br.com.carteira.fragment.stock.StockIncomesFragment;


public class StockDetailsTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = StockDetailsTabAdapter.class.getSimpleName();
    final private Context mContext;

    public StockDetailsTabAdapter(Context context, FragmentManager fm) {
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
        return mContext.getString(R.string.incomes);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            Log.d(LOG_TAG, "Loading StockDetailsFragment()");
            return new StockDetailsFragment();
        } else {
            Log.d(LOG_TAG, "Loading StockIncomesFragment()");
            return new StockIncomesFragment();
        }
    }
}
