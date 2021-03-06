package br.com.guiainvestimento.adapter.stock;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.fragment.stock.SoldStockDataFragment;
import br.com.guiainvestimento.fragment.stock.StockDataFragment;
import br.com.guiainvestimento.fragment.stock.StockIncomesMainFragment;
import br.com.guiainvestimento.fragment.stock.StockOverviewFragment;


public class StockTabAdapter extends FragmentPagerAdapter {
    private static final String LOG_TAG = StockTabAdapter.class.getSimpleName();
    final private Context mContext;

    public StockTabAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }
    @Override
    public int getCount(){
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position){
        if(position == 0){
            return mContext.getString(R.string.stocks_overview);
        }
         else if(position == 1){
            return mContext.getString(R.string.stocks_portfolio);
        } else if(position == 2){
            return mContext.getString(R.string.stocks_history);
        }
        return mContext.getString(R.string.stocks_incomes);
    }

    @Override
    public Fragment getItem(int position){
        if (position == 0){
            return new StockOverviewFragment();
        }
        else if (position == 1){
            return new StockDataFragment();
        } else if (position == 2) {
            return new SoldStockDataFragment();
        } else {
            return new StockIncomesMainFragment();
        }
    }
}
