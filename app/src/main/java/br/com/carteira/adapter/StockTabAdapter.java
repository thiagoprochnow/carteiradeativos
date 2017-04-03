package br.com.carteira.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.carteira.R;
import br.com.carteira.common.Constants;
import br.com.carteira.data.PortfolioContract;
import br.com.carteira.fragment.stock.StockDetailsFragment;
import br.com.carteira.fragment.stock.StockIncomesFragment;
import butterknife.BindView;
import butterknife.ButterKnife;


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
