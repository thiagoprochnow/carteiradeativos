package br.com.carteira.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.carteira.R;
import br.com.carteira.fragment.stock.SoldStockMainFragment;
import br.com.carteira.fragment.stock.StockDetailsFragment;
import br.com.carteira.fragment.stock.StockIncomesFragment;
import br.com.carteira.fragment.stock.StockMainFragment;


public class StockTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = StockTabAdapter.class.getSimpleName();
    final private Context mContext;

    public StockTabAdapter(Context context, FragmentManager fm) {
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
            return mContext.getString(R.string.stocks_portfolio);
        }
        return mContext.getString(R.string.stocks_history);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            Log.d(LOG_TAG, "Loading StockMainFragment()");
            return new StockMainFragment();
        } else {
            Log.d(LOG_TAG, "Loading SoldStockMainFragment()");
            return new SoldStockMainFragment();
        }
    }
}
