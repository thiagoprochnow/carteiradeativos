package br.com.guiainvestimento.adapter.stock;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.fragment.stock.StockDetailsFragment;
import br.com.guiainvestimento.fragment.stock.StockIncomesFragment;


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
            return new StockDetailsFragment();
        } else {
            return new StockIncomesFragment();
        }
    }
}
