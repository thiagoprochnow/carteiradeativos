package br.com.guiainvestimento.adapter.others;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.utils.MyPercentFormatter;
import butterknife.BindView;
import butterknife.ButterKnife;


public class OthersOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = OthersOverviewAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;

    public OthersOverviewAdapter(Context context) {
        this.mContext = context;
    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item;
        switch (viewType){
            // If it is the first view, return viewholder for OthersPortfolio overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_others_overview, parent, false);
                return new OthersOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_piechart, parent, false);
                return new OthersPieChartViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                OthersOverviewViewHolder viewHolder = (OthersOverviewViewHolder) holder;
                mCursor.moveToPosition(position);

                double totalAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.OthersPortfolio.COLUMN_VARIATION_TOTAL));
                double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.OthersPortfolio.COLUMN_INCOME_TOTAL));
                double totalGain = totalAppreciation + totalIncome;
                Locale locale = new Locale("pt", "BR");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

                // Set text colors according to positive or negative values

                if (totalGain >= 0){
                    viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }

                if (totalIncome >= 0){
                    viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }

                if (totalAppreciation >= 0){
                    viewHolder.othersAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.othersAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.othersAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.othersAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }

                double buyTotal =  mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.OthersPortfolio.COLUMN_BUY_TOTAL));
                double totalGainPercent = 0;
                double totalAppreciationPercent = 0;
                double totalIncomePercent = 0;
                if (buyTotal != 0 ) {
                    totalAppreciationPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalAppreciation / buyTotal * 100)));
                    totalIncomePercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalIncome / buyTotal * 100)));
                    totalGainPercent = totalAppreciationPercent + totalIncomePercent;
                }
                viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
                viewHolder.soldTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.OthersPortfolio.COLUMN_SOLD_TOTAL)))));
                viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.OthersPortfolio.COLUMN_CURRENT_TOTAL)))));
                viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
                viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
                viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
                viewHolder.totalIncomePercent.setText("(" + String.format("%.2f", totalIncomePercent) + "%)");
                viewHolder.othersAppreciation.setText(String.format(formatter.format(totalAppreciation)));
                viewHolder.othersAppreciationPercent.setText("(" + String.format("%.2f", totalAppreciationPercent) + "%)");
                break;
            default:
                OthersOverviewAdapter.OthersPieChartViewHolder chartHolder = (OthersOverviewAdapter.OthersPieChartViewHolder) holder;

                List<PieEntry> entries = new ArrayList<>();
                Cursor dataCursor = getDataCursor();

                if(dataCursor.getCount() > 0) {
                    dataCursor.moveToFirst();
                    float otherPercent = 0;
                    do{
                        float currentPercent = dataCursor.getFloat(0);
                        String symbol = dataCursor.getString(1);
                        // Check if already reach others field
                        // Show each pie data order in asc form
                        // Do not show sold others
                        if (currentPercent > 0) {
                            if (dataCursor.getPosition() < 8) {
                                entries.add(new PieEntry(currentPercent, symbol));
                            } else {
                                // Check if is last others data
                                otherPercent += currentPercent;
                                if (dataCursor.getPosition() == dataCursor.getCount() - 1) {
                                    entries.add(new PieEntry(otherPercent, mContext.getResources().getString(R.string.portfolio_other_label)));
                                }
                            }
                        }
                    } while (dataCursor.moveToNext());

                    // Animation on show
                    chartHolder.pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                    chartHolder.pieChart.setDrawHoleEnabled(true);
                    chartHolder.pieChart.setHoleColor(mContext.getResources().getColor(R.color.activity_main_background));
                    PieDataSet dataSet = new PieDataSet(entries, "");
                    chartHolder.pieChart.setHoleRadius(58f);
                    chartHolder.pieChart.setTransparentCircleRadius(61f);
                    chartHolder.pieChart.setOnChartValueSelectedListener(chartHolder);

                    dataSet.setColors(new int[]{R.color.green2, R.color.blue, R.color.red,
                            R.color.yellow, R.color.darkBlue, R.color.lightGray, R.color.wine, R.color.darkGreen, R.color.darkGray}, mContext);

                    Legend l = chartHolder.pieChart.getLegend();
                    l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
                    l.setXEntrySpace(7f);
                    l.setYEntrySpace(5f);

                    PieData data = new PieData(dataSet);
                    chartHolder.pieChart.setData(data);
                    data.setValueTextSize(12f);
                    data.setValueTextColor(Color.BLACK);
                    // Set as Percent
                    data.setValueFormatter(new MyPercentFormatter());
                    //Hides labels
                    chartHolder.pieChart.setDrawEntryLabels(false);
                    // Hide Description
                    chartHolder.pieChart.setDescription(null);
                    chartHolder.pieChart.invalidate(); // refresh
                    chartHolder.pieChart.setVisibility(View.VISIBLE);
                } else {
                    chartHolder.pieChart.setVisibility(View.GONE);
                }
        }

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null && mCursor.getCount() > 0) {
            count = mCursor.getCount();
            count++;
        }
        return count;
    }

    class OthersOverviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.soldTotal)
        TextView soldTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.totalGain)
        TextView totalGain;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        @BindView(R.id.othersAppreciation)
        TextView othersAppreciation;

        @BindView(R.id.othersAppreciationPercent)
        TextView othersAppreciationPercent;

        @BindView(R.id.totalIncome)
        TextView totalIncome;

        @BindView(R.id.totalIncomePercent)
        TextView totalIncomePercent;

        public OthersOverviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class OthersPieChartViewHolder extends RecyclerView.ViewHolder implements OnChartValueSelectedListener {

        @BindView(R.id.chart)
        PieChart pieChart;

        public OthersPieChartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {

            if (e == null)
                return;
            PieEntry pe = (PieEntry) e;
            pieChart.setCenterText(generateCenterSpannableText(pe.getLabel()));
        }

        @Override
        public void onNothingSelected() {

        }

        private SpannableString generateCenterSpannableText(String text) {

            SpannableString s = new SpannableString(text);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
            s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, s.length(), 0);
            s.setSpan(new RelativeSizeSpan(2f), 0, s.length(), 0);
            s.setSpan(new StyleSpan(Typeface.ITALIC), s.length(), s.length(), 0);
            s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length(), s.length(), 0);
            return s;
        }
    }

    private Cursor getDataCursor(){
        String[] affectedColumn = {PortfolioContract.OthersData.COLUMN_CURRENT_PERCENT,
                PortfolioContract.OthersData.COLUMN_SYMBOL};
        String sortOrder = PortfolioContract.OthersData.COLUMN_CURRENT_PERCENT + " DESC";

        // Searches for existing OthersData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.OthersData.URI,
                affectedColumn, null, null, sortOrder);
    }
}
