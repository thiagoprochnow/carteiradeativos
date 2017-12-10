package br.com.guiainvestimento.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.utils.MyPercentFormatter;
import butterknife.BindView;
import butterknife.ButterKnife;


public class PortfolioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = PortfolioAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private TextView mChartLabel;

    public PortfolioAdapter(Context context) {
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
            // If it is the first view, return viewholder for Portfolio overview
            case 0:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_portfolio, parent, false);
                return new PortfolioViewHolder(item);
            case 1:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_piechart, parent, false);
                return new PortfolioPieChartViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_chart, parent, false);
                return new PortfolioChartViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                PortfolioViewHolder viewHolder = (PortfolioViewHolder) holder;
                mCursor.moveToPosition(position);

                double totalAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.Portfolio.COLUMN_VARIATION_TOTAL));
                double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.Portfolio.COLUMN_INCOME_TOTAL));
                double totalGain = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.Portfolio.COLUMN_TOTAL_GAIN));
                double brokerage = mCursor.getDouble(mCursor.getColumnIndex
                    (PortfolioContract.Portfolio.COLUMN_BROKERAGE));
                Locale locale = new Locale("pt", "BR");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

                // Set text colors according to positive or negative values
                if (totalAppreciation >= 0){
                    viewHolder.portfolioAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                    viewHolder.portfolioAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.green));
                } else {
                    viewHolder.portfolioAppreciation.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                    viewHolder.portfolioAppreciationPercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));
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

                viewHolder.totalBrokerage.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                viewHolder.totalBrokeragePercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));

                double buyTotal =  mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.Portfolio.COLUMN_BUY_TOTAL));
                double portfolioAppreciationPercent = 0;
                double totalGainPercent = 0;
                double incomePercent = 0;
                double brokeragePercent = 0;
                if(buyTotal != 0) {
                    portfolioAppreciationPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalAppreciation / buyTotal * 100)));
                    incomePercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalIncome / buyTotal * 100)));
                    brokeragePercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(brokerage / buyTotal * 100)));
                    totalGainPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalGain / buyTotal * 100)));
                }
                viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
                viewHolder.soldTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.Portfolio.COLUMN_SOLD_TOTAL)))));
                viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.Portfolio.COLUMN_CURRENT_TOTAL)))));
                viewHolder.portfolioAppreciation.setText(String.format(formatter.format(totalAppreciation)));
                viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
                viewHolder.totalBrokerage.setText(String.format(formatter.format(brokerage)));
                viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
                viewHolder.portfolioAppreciationPercent.setText("(" + String.format("%.2f", portfolioAppreciationPercent) + "%)");
                viewHolder.totalIncomePercent.setText("(" + String.format("%.2f", incomePercent) + "%)");
                viewHolder.totalBrokeragePercent.setText("(" + String.format("%.2f", brokeragePercent) + "%)");
                viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
                break;
            case 1:
                mCursor.moveToPosition(0);
                PortfolioPieChartViewHolder piechartHolder = (PortfolioPieChartViewHolder) holder;

                List<PieEntry> entries = new ArrayList<>();

                float treasuryEntry = mCursor.getFloat(
                        mCursor.getColumnIndex(PortfolioContract.Portfolio.COLUMN_TREASURY_PERCENT));
                float fixedEntry = mCursor.getFloat(
                        mCursor.getColumnIndex(PortfolioContract.Portfolio.COLUMN_FIXED_PERCENT));
                float stockEntry = mCursor.getFloat(
                        mCursor.getColumnIndex(PortfolioContract.Portfolio.COLUMN_STOCK_PERCENT));
                float fiiEntry = mCursor.getFloat(
                        mCursor.getColumnIndex(PortfolioContract.Portfolio.COLUMN_FII_PERCENT));
                float currencyEntry = mCursor.getFloat(
                        mCursor.getColumnIndex(PortfolioContract.Portfolio.COLUMN_CURRENCY_PERCENT));
                float othersEntry = mCursor.getFloat(
                        mCursor.getColumnIndex(PortfolioContract.Portfolio.COLUMN_OTHERS_PERCENT));

                if (treasuryEntry > 0) {
                    entries.add(new PieEntry(treasuryEntry, mContext.getResources().getString(R.string.title_treasury)));
                }

                if (fixedEntry > 0) {
                    entries.add(new PieEntry(fixedEntry, mContext.getResources().getString(R.string.title_fixed)));
                }

                if (stockEntry > 0) {
                    entries.add(new PieEntry(stockEntry, mContext.getResources().getString(R.string.title_stocks)));
                }

                if (fiiEntry > 0) {
                    entries.add(new PieEntry(fiiEntry, mContext.getResources().getString(R.string.title_fii)));
                }

                if (currencyEntry > 0) {
                    entries.add(new PieEntry(currencyEntry, mContext.getResources().getString(R.string.title_currency)));
                }

                if (othersEntry > 0) {
                    entries.add(new PieEntry(othersEntry, mContext.getResources().getString(R.string.title_others)));
                }

                if(treasuryEntry == 0 && fixedEntry == 0 && stockEntry == 0 && fiiEntry == 0 && currencyEntry == 0 && othersEntry == 0){
                    piechartHolder.pieChart.setVisibility(View.GONE);
                    piechartHolder.piechartCardview.setVisibility(View.GONE);
                    piechartHolder.piechartHeader.setVisibility(View.GONE);
                } else {
                    piechartHolder.pieChart.setVisibility(View.VISIBLE);
                    piechartHolder.piechartCardview.setVisibility(View.VISIBLE);
                    piechartHolder.piechartHeader.setVisibility(View.VISIBLE);

                }

                // Animation on show
                piechartHolder.pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                piechartHolder.pieChart.setDrawHoleEnabled(true);
                piechartHolder.pieChart.setHoleColor(mContext.getResources().getColor(R.color.white));
                PieDataSet dataSet = new PieDataSet(entries, "");
                piechartHolder.pieChart.setHoleRadius(58f);
                piechartHolder.pieChart.setTransparentCircleRadius(61f);
                piechartHolder.pieChart.setOnChartValueSelectedListener(piechartHolder);

                dataSet.setColors(new int[]{R.color.green2, R.color.blue, R.color.red,
                        R.color.yellow, R.color.darkGreen, R.color.darkGray}, mContext);

                Legend l = piechartHolder.pieChart.getLegend();
                l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
                l.setXEntrySpace(7f);
                l.setYEntrySpace(5f);

                PieData data = new PieData(dataSet);
                piechartHolder.pieChart.setData(data);

                data.setValueTextSize(10f);

                data.setValueTextColor(Color.BLACK);
                // Set as Percent
                data.setDrawValues(false);
                //Hides labels
                piechartHolder.pieChart.setDrawEntryLabels(false);
                // Hide Description
                piechartHolder.pieChart.setDescription(null);
                piechartHolder.pieChart.invalidate(); // refresh
                break;
            default:
                mCursor.moveToPosition(0);
                PortfolioChartViewHolder chartHolder = (PortfolioChartViewHolder) holder;
                mChartLabel = (TextView) chartHolder.chartLabel;

                List<Entry> growthEntries = new ArrayList<Entry>();
                List<Entry> buyEntries = new ArrayList<Entry>();
                //TODO: Change so year can be selected or inputed by user
                String queryYear = "2017";

                Cursor growthCursor = null;
                Cursor buyCursor = null;
                try {
                    growthCursor = getGrowthCursor(queryYear);
                    buyCursor = getBuyGrowthCursor(queryYear);
                } catch (SQLException e){
                    Log.e(LOG_TAG, e.toString());
                }

                if(growthCursor != null && growthCursor.getCount() > 0 && buyCursor != null && buyCursor.getCount() > 0){

                    // Make growth entries
                    growthCursor.moveToFirst();
                    do{
                        long timestamp = growthCursor.getLong(growthCursor.getColumnIndex(PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP));
                        long month = growthCursor.getLong(growthCursor.getColumnIndex(PortfolioContract.PortfolioGrowth.MONTH));
                        long year = growthCursor.getLong(growthCursor.getColumnIndex(PortfolioContract.PortfolioGrowth.YEAR));
                        float value = growthCursor.getFloat(growthCursor.getColumnIndex(PortfolioContract.PortfolioGrowth.COLUMN_TOTAL));
                        // Check if already reach others field
                        // Show each pie data order in asc form
                        // Do not show sold stocks
                        growthEntries.add(new Entry(new Long(month).floatValue(), value, String.valueOf(year)));

                    } while (growthCursor.moveToNext());

                    // Make buy entries
                    buyCursor.moveToFirst();
                    do{
                        long timestamp = buyCursor.getLong(buyCursor.getColumnIndex(PortfolioContract.BuyGrowth.COLUMN_TIMESTAMP));
                        long month = buyCursor.getLong(buyCursor.getColumnIndex(PortfolioContract.BuyGrowth.MONTH));
                        long year = buyCursor.getLong(buyCursor.getColumnIndex(PortfolioContract.BuyGrowth.YEAR));
                        float value = buyCursor.getFloat(buyCursor.getColumnIndex(PortfolioContract.BuyGrowth.COLUMN_TOTAL));
                        // Check if already reach others field
                        // Show each pie data order in asc form
                        // Do not show sold stocks
                        buyEntries.add(new Entry(new Long(month).floatValue(), value, String.valueOf(year)));

                    } while (buyCursor.moveToNext());

                    Description desc = chartHolder.chart.getDescription();
                    desc.setEnabled(false);

                    // Disable zoom
                    chartHolder.chart.setDoubleTapToZoomEnabled(false);
                    chartHolder.chart.setPinchZoom(false);
                    chartHolder.chart.setScaleEnabled(false);

                    chartHolder.chart.setOnChartValueSelectedListener(valueSelectedListener());

                    Legend legend = chartHolder.chart.getLegend();
                    legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                    legend.setXEntrySpace(20);

                    LineDataSet growthDS = new LineDataSet(growthEntries, "Atual"); // add entries to dataset
                    growthDS.setHighLightColor(Color.BLACK);
                    growthDS.setLineWidth(3);
                    growthDS.setColor(mContext.getResources().getColor(R.color.green));
                    growthDS.setCircleRadius(7f);
                    growthDS.setCircleHoleRadius(5f);
                    growthDS.setValueTextSize(10f);
                    growthDS.setDrawValues(false);

                    LineDataSet buyDS = new LineDataSet(buyEntries, "Comprado");
                    buyDS.setHighLightColor(Color.BLACK);
                    buyDS.setLineWidth(3);
                    buyDS.setCircleRadius(7f);
                    buyDS.setCircleHoleRadius(5f);
                    buyDS.setValueTextSize(10f);
                    buyDS.setDrawValues(false);

                    final String[] months = mContext.getResources().getStringArray(R.array.chart_months_abv);

                    XAxis xAxis = chartHolder.chart.getXAxis();
                    xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                    xAxis.setDrawAxisLine(false);
                    xAxis.setDrawGridLines(false);
                    xAxis.setValueFormatter(axisValueFormatter());
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setLabelCount(8);
                    xAxis.setXOffset(20f);
                    xAxis.setAxisMinValue(0);
                    xAxis.setAxisMaxValue(11);
                    xAxis.setTextSize(8f);

                    YAxis yAxisLeft = chartHolder.chart.getAxisLeft();
                    yAxisLeft.setDrawAxisLine(false);
                    //yAxisLeft.setDrawGridLines(false);
                    yAxisLeft.setGranularity(0.1f);
                    yAxisLeft.setTextSize(10f);
                    yAxisLeft.setLabelCount(5);
                    yAxisLeft.setXOffset(20f);
                    yAxisLeft.setTextSize(8f);

                    YAxis yAxisRight = chartHolder.chart.getAxisRight();
                    yAxisRight.setDrawGridLines(false);
                    yAxisRight.setDrawAxisLine(false);
                    yAxisRight.setDrawLabels(false);
                    yAxisRight.setXOffset(20f);

                    // use the interface ILineDataSet
                    List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(growthDS);
                    dataSets.add(buyDS);

                    LineData lineData = new LineData(dataSets);
                    chartHolder.chart.setData(lineData);
                    chartHolder.chart.invalidate();

                    chartHolder.chart_cardview.setVisibility(View.VISIBLE);
                    chartHolder.chart.setVisibility(View.VISIBLE);
                } else {
                    chartHolder.chart.setVisibility(View.GONE);
                    chartHolder.chart_cardview.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mCursor != null && mCursor.getCount() > 0) {
            count = mCursor.getCount();
            count++;
            count++;
        }
        return count;
    }

    class PortfolioViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.boughtTotal)
        TextView boughtTotal;

        @BindView(R.id.soldTotal)
        TextView soldTotal;

        @BindView(R.id.currentTotal)
        TextView currentTotal;

        @BindView(R.id.portfolioAppreciation)
        TextView portfolioAppreciation;

        @BindView(R.id.totalIncome)
        TextView totalIncome;

        @BindView(R.id.totalGain)
        TextView totalGain;

        @BindView(R.id.totalBrokerage)
        TextView totalBrokerage;

        @BindView(R.id.portfolioAppreciationPercent)
        TextView portfolioAppreciationPercent;

        @BindView(R.id.totalIncomePercent)
        TextView totalIncomePercent;

        @BindView(R.id.totalBrokeragePercent)
        TextView totalBrokeragePercent;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        public PortfolioViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class PortfolioPieChartViewHolder extends RecyclerView.ViewHolder implements OnChartValueSelectedListener {

        @BindView(R.id.piechart)
        PieChart pieChart;

        @BindView(R.id.piechart_cardview)
        CardView piechartCardview;

        @BindView(R.id.piechart_header_label)
        TextView piechartHeader;

        public PortfolioPieChartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onValueSelected(Entry e, Highlight h) {

            if (e == null)
                return;
            PieEntry pe = (PieEntry) e;
            pieChart.setCenterText(generateCenterSpannableText(pe.getLabel(),pe.getValue()));
        }

        @Override
        public void onNothingSelected() {

        }

        private SpannableString generateCenterSpannableText(String text, float value) {
            String valueS = String.format("%.2f", value) + "%";
            SpannableString s = new SpannableString(text+"\n"+valueS);
            s.setSpan(new StyleSpan(Typeface.NORMAL), 0, s.length(), 0);
            s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, s.length(), 0);
            s.setSpan(new RelativeSizeSpan(1.4f), 0, s.length(), 0);
            s.setSpan(new StyleSpan(Typeface.ITALIC), s.length(), s.length(), 0);
            s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length(), s.length(), 0);
            return s;
        }
    }

    class PortfolioChartViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.chart_cardview)
        CardView chart_cardview;

        @BindView(R.id.chart)
        LineChart chart;

        @BindView(R.id.chart_label)
        TextView chartLabel;

        public PortfolioChartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private Cursor getGrowthCursor(String year){
        String sortOrder = PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP + " ASC Limit 12";

        String selection = PortfolioContract.PortfolioGrowth.COLUMN_TYPE + " = ? AND "
                + PortfolioContract.PortfolioGrowth.YEAR + " = ?";
        String[] selectionArguments = {String.valueOf(Constants.ProductType.PORTFOLIO), year};

        // Searches for existing StockData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.PortfolioGrowth.URI,
                null, selection, selectionArguments, sortOrder);
    }

    private Cursor getBuyGrowthCursor(String year){
        String sortOrder = PortfolioContract.BuyGrowth.COLUMN_TIMESTAMP + " ASC Limit 12";
        String selection = PortfolioContract.BuyGrowth.COLUMN_TYPE + " = ? AND "
                + PortfolioContract.BuyGrowth.YEAR + " = ?";
        String[] selectionArguments = {String.valueOf(Constants.ProductType.PORTFOLIO), year};

        // Searches for existing StockData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.BuyGrowth.URI,
                null, selection, selectionArguments, sortOrder);
    }

    private OnChartValueSelectedListener valueSelectedListener(){
        OnChartValueSelectedListener valueSelectedListener = new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int month = (int) e.getX();
                float value = e.getY();
                String year = e.getData().toString();
                int chart = h.getDataSetIndex();

                final String[] months = mContext.getResources().getStringArray(R.array.chart_months);

                Locale locale = new Locale("pt", "BR");
                NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

                String tipo;
                if (chart == 1){
                    tipo = mContext.getResources().getString(R.string.portfolio_bought_label) + ": ";
                } else {
                    tipo = mContext.getResources().getString(R.string.portfolio_current_label) + ": ";
                }
                mChartLabel.setText(tipo + String.valueOf(months[month] + ", " + year + " - " + formatter.format(e.getY())));
            }

            @Override
            public void onNothingSelected() {
                mChartLabel.setText("");
            }
        };

        return valueSelectedListener;
    }

    private IAxisValueFormatter axisValueFormatter() {
        IAxisValueFormatter axisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                final String[] months = mContext.getResources().getStringArray(R.array.chart_months_abv);
                int month = (int) value;
                return months[month];
            }
        };
        return axisValueFormatter;
    };
}
