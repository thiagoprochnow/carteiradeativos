package br.com.carteira.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.carteira.R;
import br.com.carteira.domain.Fii;

public class FiiAdapter extends RecyclerView.Adapter<FiiAdapter.FiisViewHolder> {

    private static final String LOG_TAG = FiiAdapter.class.getSimpleName();

    private final List<Fii> mFiis;
    private final Context mContext;
    private FiiOnClickListener mFiiOnClickListener;

    public FiiAdapter(Context context, List<Fii> fiis, FiiOnClickListener fiiOnClickListener) {
        this.mContext = context;
        this.mFiis = fiis;
        this.mFiiOnClickListener = fiiOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.mFiis != null ? this.mFiis.size() : 0;
    }

    @Override
    public FiisViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_fii, viewGroup, false);
        FiisViewHolder holder = new FiisViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FiisViewHolder holder, final int position) {
        // Updates the view with the values of the elements on CardView
        Fii fii = mFiis.get(position);
        // To know more of each fii variable, check Fii.java for coments
        holder.symbol.setText(fii.getSymbol());
        holder.fiiQuantity.setText(String.valueOf(fii.getFiiQuantity()));
        holder.boughtTotal.setText(String.format("R$%.2f", fii.getBoughtTotal()));
        holder.currentTotal.setText(Html.fromHtml("<u>" + String.format("R$%.2f", fii
                .getCurrentTotal()) + "</u>"));
        holder.fiiAppreciation.setText(String.format("R$%.2f", fii.getFiiAppreciation()));
        holder.currentPercent.setText(String.valueOf(fii.getCurrentPercent()) + "%");
        holder.objectivePercent.setText(String.valueOf(fii.getObjectivePercent()) + "%");
        holder.totalIncome.setText(String.format("R$%.2f", fii.getTotalIncome()));
        holder.totalGain.setText(String.format("R$%.2f", fii.getTotalGain()));
        // Click on the CardView of the fii
        if (mFiiOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFiiOnClickListener.onClickFii(holder.itemView, position);
                }
            });
        }
    }

    public interface FiiOnClickListener {
        public void onClickFii(View view, int idx);
    }

    public static class FiisViewHolder extends RecyclerView.ViewHolder {
        public TextView symbol, fiiQuantity, boughtTotal, currentTotal, fiiAppreciation,
                currentPercent, objectivePercent, totalIncome, totalGain;

        public FiisViewHolder(View view) {
            super(view);
            // Create the view to save on the ViewHolder
            symbol = (TextView) view.findViewById(R.id.symbol);
            fiiQuantity = (TextView) view.findViewById(R.id.fiiQuantity);
            boughtTotal = (TextView) view.findViewById(R.id.boughtTotal);
            currentTotal = (TextView) view.findViewById(R.id.currentTotal);
            fiiAppreciation = (TextView) view.findViewById(R.id.fiiAppreciation);
            currentPercent = (TextView) view.findViewById(R.id.currentPercent);
            objectivePercent = (TextView) view.findViewById(R.id.objectivePercent);
            totalIncome = (TextView) view.findViewById(R.id.totalIncome);
            totalGain = (TextView) view.findViewById(R.id.totalGain);
        }
    }
}
