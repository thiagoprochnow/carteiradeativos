package br.com.guiainvestimento.adapter.stock;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.adapter.treasury.TreasuryOverviewAdapter;
import br.com.guiainvestimento.common.Constants;
import br.com.guiainvestimento.data.PortfolioContract;
import br.com.guiainvestimento.utils.MyPercentFormatter;
import butterknife.BindView;
import butterknife.ButterKnife;


public class StockOverviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = StockOverviewAdapter.class.getSimpleName();
    final private Context mContext;
    private Cursor mCursor;
    private TableLayout mChartTable;

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
            case 1:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_piechart, parent, false);
                return new StockPieChartViewHolder(item);
            default:
                item = LayoutInflater.from(mContext).inflate(R.layout.adapter_table, parent, false);
                return new StockChartViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Locale locale = new Locale("pt", "BR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        switch (holder.getItemViewType()) {
            case 0:
                StockOverviewViewHolder viewHolder = (StockOverviewViewHolder) holder;
                mCursor.moveToPosition(position);

                double totalAppreciation = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.StockPortfolio.COLUMN_VARIATION_TOTAL));
                double totalIncome = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.StockPortfolio.COLUMN_INCOME_TOTAL));
                double totalBrokerage = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.StockPortfolio.COLUMN_BROKERAGE));
                double totalGain = mCursor.getDouble(mCursor.getColumnIndex
                        (PortfolioContract.StockPortfolio.COLUMN_TOTAL_GAIN));

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

                viewHolder.totalBrokerage.setTextColor(ContextCompat.getColor(mContext,R.color.red));
                viewHolder.totalBrokeragePercent.setTextColor(ContextCompat.getColor(mContext,R.color.red));

                double buyTotal =  mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_BUY_TOTAL));
                double stockAppreciationPercent = 0;
                double totalGainPercent = 0;
                double incomePercent = 0;
                double brokeragePercent = 0;
                if (buyTotal != 0){
                    stockAppreciationPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalAppreciation / buyTotal * 100)));
                    incomePercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalIncome / buyTotal * 100)));
                    brokeragePercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalBrokerage / buyTotal * 100)));
                    totalGainPercent = Double.parseDouble(String.format(java.util.Locale.US,"%.2f",(totalGain / buyTotal * 100)));
                }
                viewHolder.boughtTotal.setText(String.format(formatter.format(buyTotal)));
                viewHolder.soldTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_SOLD_TOTAL)))));
                viewHolder.currentTotal.setText(String.format(formatter.format(mCursor.getDouble(
                        mCursor.getColumnIndex(PortfolioContract.StockPortfolio.COLUMN_CURRENT_TOTAL)))));
                viewHolder.stockAppreciation.setText(String.format(formatter.format(totalAppreciation)));
                viewHolder.totalIncome.setText(String.format(formatter.format(totalIncome)));
                viewHolder.totalBrokerage.setText(String.format(formatter.format(totalBrokerage)));
                viewHolder.totalGain.setText(String.format(formatter.format(totalGain)));
                viewHolder.stockAppreciationPercent.setText("(" + String.format("%.2f", stockAppreciationPercent) + "%)");
                viewHolder.totalIncomePercent.setText("(" + String.format("%.2f", incomePercent) + "%)");
                viewHolder.totalBrokeragePercent.setText("(" + String.format("%.2f", brokeragePercent) + "%)");
                viewHolder.totalGainPercent.setText("(" + String.format("%.2f", totalGainPercent) + "%)");
                break;
            case 1:
                StockPieChartViewHolder piechartHolder = (StockPieChartViewHolder) holder;

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
                                int positiosn = dataCursor.getPosition();
                                int countt = dataCursor.getCount();
                                if (dataCursor.getPosition() == dataCursor.getCount() - 1) {
                                    entries.add(new PieEntry(otherPercent, mContext.getResources().getString(R.string.portfolio_other_label)));
                                }
                            }
                        }
                    } while (dataCursor.moveToNext());

                    // Animation on show
                    piechartHolder.pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                    piechartHolder.pieChart.setDrawHoleEnabled(true);
                    piechartHolder.pieChart.setHoleColor(mContext.getResources().getColor(R.color.white));
                    PieDataSet dataSet = new PieDataSet(entries, "");
                    piechartHolder.pieChart.setHoleRadius(58f);
                    piechartHolder.pieChart.setTransparentCircleRadius(61f);
                    piechartHolder.pieChart.setOnChartValueSelectedListener(piechartHolder);

                    dataSet.setColors(new int[]{R.color.green2, R.color.blue, R.color.red,
                            R.color.yellow, R.color.darkBlue, R.color.lightGray, R.color.wine, R.color.darkGreen, R.color.darkGray}, mContext);

                    Legend l = piechartHolder.pieChart.getLegend();
                    l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
                    l.setXEntrySpace(7f);
                    l.setYEntrySpace(5f);

                    PieData data = new PieData(dataSet);
                    piechartHolder.pieChart.setData(data);
                    data.setValueTextSize(10f);
                    data.setValueTextColor(Color.BLACK);
                    data.setDrawValues(false);
                    //Hides labels
                    piechartHolder.pieChart.setDrawEntryLabels(false);
                    // Hide Description
                    piechartHolder.pieChart.setDescription(null);
                    piechartHolder.pieChart.invalidate(); // refresh
                    piechartHolder.pieChart.setVisibility(View.VISIBLE);
                    piechartHolder.piechartCardview.setVisibility(View.VISIBLE);
                    piechartHolder.piechartHeader.setVisibility(View.VISIBLE);
                } else {
                    piechartHolder.pieChart.setVisibility(View.GONE);
                    piechartHolder.piechartCardview.setVisibility(View.GONE);
                    piechartHolder.piechartHeader.setVisibility(View.GONE);
                }
                break;
            default:
                mCursor.moveToPosition(0);
                final StockChartViewHolder chartHolder = (StockChartViewHolder) holder;
                mChartTable = (TableLayout) chartHolder.tableChart;
                mChartTable.removeAllViews();

                Cursor growthCursor = getGrowthCursor();
                Cursor growthLookahead = getGrowthCursor();

                ArrayList<TableRow> rowList = new ArrayList<>();

                if (growthCursor != null && growthCursor.getCount() > 0) {

                    growthCursor.moveToFirst();
                    growthLookahead.moveToFirst();
                    double previousTotal = 0;
                    do {
                        boolean first = false;
                        TableRow row = new TableRow(mContext);
                        TableLayout.LayoutParams rowParams =
                                new TableLayout.LayoutParams
                                        (TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                        rowParams.setMargins(5, 30, 5, 30);
                        row.setLayoutParams(rowParams);

                        long month = growthCursor.getLong(growthCursor.getColumnIndex(PortfolioContract.PortfolioGrowth.MONTH));
                        long year = growthCursor.getLong(growthCursor.getColumnIndex(PortfolioContract.PortfolioGrowth.YEAR));
                        long timestamp = growthCursor.getLong(growthCursor.getColumnIndex(PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP));

                        final String[] months = mContext.getResources().getStringArray(R.array.chart_months_abv);
                        int imonth = (int) month;
                        String monthAbv = months[imonth];
                        String mesAbv = monthAbv+"/"+String.valueOf(year);

                        TextView mes = new TextView(mContext);
                        mes.setText(mesAbv);
                        mes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f);
                        mes.setGravity(Gravity.CENTER);
                        mes.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        mes.setWidth(0);

                        double valueTotal = growthCursor.getDouble(growthCursor.getColumnIndex(PortfolioContract.PortfolioGrowth.COLUMN_TOTAL));

                        TextView total = new TextView(mContext);
                        total.setText(String.format(formatter.format(valueTotal)));
                        total.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f);
                        total.setGravity(Gravity.CENTER);
                        total.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        total.setWidth(0);

                        if (growthCursor.isFirst()){
                            previousTotal = valueTotal;
                            first = true;
                        }

                        double buyGain = 0;
                        if (growthLookahead.moveToNext()){
                            String currentMonth = "";
                            String lookaheadMonth = "";

                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(timestamp);
                            currentMonth = DateFormat.format("MM-yyyy", cal).toString();

                            long timestampLookahead = growthLookahead.getLong(growthLookahead.getColumnIndex(PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP));
                            cal.setTimeInMillis(timestampLookahead);
                            lookaheadMonth = DateFormat.format("MM-yyyy", cal).toString();
                            buyGain = getBuyGain(currentMonth, lookaheadMonth);
                        } else {
                            String currentMonth = "";
                            String lookaheadMonth = "";

                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(timestamp);
                            currentMonth = DateFormat.format("MM-yyyy", cal).toString();

                            buyGain = getBuyGain(currentMonth, "0");
                        }

                        TextView compra = new TextView(mContext);
                        compra.setText(String.format(formatter.format(buyGain)));
                        compra.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f);
                        compra.setGravity(Gravity.CENTER);
                        compra.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        compra.setWidth(0);

                        double gain = 0;

                        if (previousTotal != 0 && !first) {
                            gain = valueTotal - previousTotal - buyGain;
                        }
                        TextView ganho = new TextView(mContext);
                        ganho.setText(String.format(formatter.format(gain)));
                        ganho.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f);
                        ganho.setGravity(Gravity.CENTER);
                        ganho.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        ganho.setWidth(0);

                        double gainPercent = 0;

                        if (gain != 0 && previousTotal > 0) {
                            gainPercent = gain / previousTotal * 100;
                        }
                        TextView ganhoPer = new TextView(mContext);
                        ganhoPer.setText(String.format("%.2f", gainPercent) + "%");
                        ganhoPer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10f);
                        ganhoPer.setGravity(Gravity.CENTER);
                        ganhoPer.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        ganhoPer.setWidth(0);

                        row.addView(mes);
                        row.addView(total);
                        row.addView(compra);
                        row.addView(ganho);
                        row.addView(ganhoPer);

                        // Sets for next iteration
                        previousTotal = valueTotal;

                        if (valueTotal > 0) {
                            rowList.add(row);
                        }
                    } while (growthCursor.moveToNext());

                    // Invert Table
                    int size = rowList.size();
                    for (int i = size-1; i >=0; i--){
                        TableRow row = rowList.get(i);
                        mChartTable.addView(row);

                        if (i != 0) {
                            View line = new View(mContext);
                            line.setBackgroundColor(0xFF4E4E4E);
                            line.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));

                            mChartTable.addView(line);
                        }
                    }
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

        @BindView(R.id.totalBrokerage)
        TextView totalBrokerage;

        @BindView(R.id.totalGain)
        TextView totalGain;

        @BindView(R.id.stockAppreciationPercent)
        TextView stockAppreciationPercent;

        @BindView(R.id.totalIncomePercent)
        TextView totalIncomePercent;

        @BindView(R.id.totalBrokeragePercent)
        TextView totalBrokeragePercent;

        @BindView(R.id.totalGainPercent)
        TextView totalGainPercent;

        public StockOverviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class StockPieChartViewHolder extends RecyclerView.ViewHolder implements OnChartValueSelectedListener {

        @BindView(R.id.piechart)
        PieChart pieChart;

        @BindView(R.id.piechart_cardview)
        CardView piechartCardview;

        @BindView(R.id.piechart_header_label)
        TextView piechartHeader;

        public StockPieChartViewHolder(View itemView) {
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

    class StockChartViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tableChart)
        TableLayout tableChart;

        public StockChartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private Cursor getDataCursor(){
        String[] affectedColumn = {PortfolioContract.StockData.COLUMN_CURRENT_PERCENT,
            PortfolioContract.StockData.COLUMN_SYMBOL};
        String sortOrder = PortfolioContract.StockData.COLUMN_CURRENT_PERCENT + " DESC";
        String selection = PortfolioContract.StockData.COLUMN_STATUS + " = ?";
        String[] selectionArguments = {String.valueOf(Constants.Status.ACTIVE)};

        // Searches for existing StockData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.StockData.URI,
                affectedColumn, selection, selectionArguments, sortOrder);
    }

    private Cursor getYears(){
        String sortOrder = PortfolioContract.PortfolioGrowth.YEAR + " ASC";
        String[] affectedRows = {"DISTINCT " + PortfolioContract.PortfolioGrowth.YEAR};
        String selection = PortfolioContract.PortfolioGrowth.COLUMN_TYPE + " = ?";
        String[] selectionArguments = {String.valueOf(Constants.ProductType.STOCK)};

        // Searches for existing StockData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.PortfolioGrowth.URI,
                affectedRows, selection, selectionArguments, sortOrder);
    }

    private Cursor getGrowthCursor(){
        String sortOrder = PortfolioContract.PortfolioGrowth.COLUMN_TIMESTAMP + " ASC";

        String selection = PortfolioContract.PortfolioGrowth.COLUMN_TYPE + " = ?";
        String[] selectionArguments = {String.valueOf(Constants.ProductType.STOCK)};

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
        String[] selectionArguments = {String.valueOf(Constants.ProductType.STOCK), year};

        // Searches for existing StockData to update value.
        // If dosent exists, creates new one
        return mContext.getContentResolver().query(
                PortfolioContract.BuyGrowth.URI,
                null, selection, selectionArguments, sortOrder);
    }

    private double getBuyGain(String currentMonth, String lookaheadMonth){
        double buyGain = 0;

        long timestamp = 0;
        long timestampAhead = 0;
        String currentDate="01-"+currentMonth;
        String lookaheadDate = "01-"+lookaheadMonth;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = formatter.parse(currentDate);
            timestamp = date.getTime();
            if (!lookaheadMonth.equals("0")) {
                Date date2 = formatter.parse(lookaheadDate);
                timestampAhead = date2.getTime();
            } else {
                timestampAhead = System.currentTimeMillis();
            }
        } catch (ParseException e){
            Log.d(LOG_TAG, e.toString());
        }

        //Stock
        String selection = PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " >= ? AND " + PortfolioContract.StockTransaction.COLUMN_TIMESTAMP + " < ? AND "
                + PortfolioContract.StockTransaction.COLUMN_TYPE + " = ?";
        String[] selectionArguments = {String.valueOf(timestamp), String.valueOf(timestampAhead), String.valueOf(Constants.Type.BUY)};
        String[] selectionArguments2 = {String.valueOf(timestamp), String.valueOf(timestampAhead), String.valueOf(Constants.Type.SELL)};

        Cursor stockBuysCursor =  mContext.getContentResolver().query(PortfolioContract.StockTransaction.URI,
                null, selection, selectionArguments, null);


        Cursor stockSellsCursor =  mContext.getContentResolver().query(PortfolioContract.StockTransaction.URI,
                null, selection, selectionArguments2, null);

        if(stockBuysCursor.moveToFirst()){
            do{
                buyGain += stockBuysCursor.getInt(stockBuysCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*stockBuysCursor.getDouble(stockBuysCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
            } while (stockBuysCursor.moveToNext());
        }

        if(stockSellsCursor.moveToFirst()){
            do{
                buyGain -= stockSellsCursor.getInt(stockSellsCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_QUANTITY))*stockSellsCursor.getDouble(stockSellsCursor.getColumnIndex(PortfolioContract.StockTransaction.COLUMN_PRICE));
            } while (stockSellsCursor.moveToNext());
        }

        return buyGain;
    }
}
