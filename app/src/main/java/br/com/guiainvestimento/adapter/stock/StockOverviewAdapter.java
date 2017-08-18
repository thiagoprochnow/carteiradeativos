package br.com.guiainvestimento.adapter.stock;

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


public class StockOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = StockOverviewAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;

    public StockOverviewAdapter(Context context) {
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
            // If it is the first view, return viewholder for StockPortfolio overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_stock_overview, parent, false);
                return new StockOverviewViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_piechart, parent, false);
                return new StockPieChartViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                StockOverviewViewHolder viewHolder = (StockOverviewViewHolder) holder;
                mCursor.moveToPosition(position);

                double totalAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.StockPortfolio.COLUMN_VARIATION_TOTAL));
                double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL));
                double totalGain = totalAppreciation + totalIncome;
                Locale locale = new Locale("pt", "BR");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

                // Set text colors according to positive or negative values
                if (totalAppreciation >= 0){
                    viewHolder.stockAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.stockAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.stockAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.stockAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }

                if (totalIncome >= 0){
                    viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.totalIncome.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.totalIncomePercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }

                if (totalGain >= 0){
                    viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.totalGain.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.totalGainPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                }
                double buyTotal =  mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_BUY_TOTAL));
                double stockAppreciationPercent = 0;
                double totalGainPercent = 0;
                double incomePercent = 0;
                if (buyTotal != 0){
                    stockAppreciationPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalAppreciation / buyTotal * 100)));
                    incomePercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalIncome / buyTotal * 100)));
                    totalGainPercent = stockAppreciationPercent + incomePercent;
                }
                viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
                viewHolder.soldTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_SOLD_TOTAL)))));
                viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_CURRENT_TOTAL)))));
                viewHolder.stockAppreciation.setText(String.format(formatter.format(totalAppreciation)));
                viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
                viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
                viewHolder.stockAppreciationPercent.setText("(" + String.format("%.2f", stockAppreciationPercent) + "%)");
                viewHolder.totalIncomePercent.setText("(" + String.format("%.2f", incomePercent) + "%)");
                viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
                break;
            default:
                StockPieChartViewHolder chartHolder = (StockPieChartViewHolder) holder;

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
                        // Do not show sold stocks
                        if (currentPercent > 0) {
                            if (dataCursor.getPosition() < 8) {
                                entries.add(new PieEntry(currentPercent, symbol));
                            } else {
                                // Check if is last stock data
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

                    Log.d(LOG_TAG, "Pie Chart Drawn");
                } else {
                    Log.d(LOG_TAG, "No StockData found for pieChart");
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

    class StockOverviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.soldTotal)
        TextView soldTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.stockAppreciation)
        TextView stockAppreciation;

        @BindView(R.id.totalIncome)
        TextView totalIncome;

        @BindView(R.id.totalGain)
        TextView totalGain;

        @BindView(R.id.stockAppreciationPercent)
        TextView stockAppreciationPercent;

        @BindView(R.id.totalIncomePercent)
        TextView totalIncomePercent;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        public StockOverviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class StockPieChartViewHolder extends RecyclerView.ViewHolder implements OnChartValueSelectedListener {

        @BindView(R.id.chart)
        PieChart pieChart;

        public StockPieChartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {

            if (e == null)
                return;
            PieEntry pe = (PieEntry) e;
            Log.d("VAL SELECTED",
                    "Value: " + e.getY() + ", Label: " + pe.getLabel());
            pieChart.setCenterText(generateCenterSpannableText(pe.getLabel()));
        }

        @Override
        public void onNothingSelected() {
            Log.d("PieChart", "nothing selected");
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
        String[] affectedColumn = {PortfolioContract.StockData.COLUMN_CURRENT_PERCENT,
            PortfolioContract.StockData.COLUMN_SYMBOL};
        String sortOrder = PortfolioContract.StockData.COLUMN_CURRENT_PERCENT + " DESC";

        // Searches for existing StockData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                affectedColumn, null, null, sortOrder);
    }
}
