package com.kts_barchartpiechartproject;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class BarChart_Activity extends AppCompatActivity {

    private BarChart barChart;
    private String valState;
    private String valMaleCount;
    private String valFemaleCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        barChart = findViewById(R.id.barchart);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.population));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fetchData();
    }

    private void setBarChart(int statesCount)
    {
        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        Description description = new Description();
        description.setTextAlign(Paint.Align.LEFT);
        description.setPosition(30, 30);
        description.setXOffset(0f);
        description.setYOffset(10f);
        description.setText(getResources().getString(R.string.population_in_crores));
        barChart.setDescription(description);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);

        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

    }

    private void loadBarChart(int count, float[] maleValueList, float[] femaleValueList, String[] xAxisList) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < maleValueList.length; i++) {
            yVals1.add(new BarEntry(i, new float[]{maleValueList[i], femaleValueList[i]}));
        }

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisList));
        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");
            set1.setValueTextColor(Color.BLACK);
            set1.setDrawIcons(false);
            // add a lot of colors
            set1.setColors(getResources().getColor(R.color.colorMaleBar, getTheme()), getResources().getColor(R.color.colorFemaleBar, getTheme()));
            set1.setStackLabels(new String[]{getResources().getString(R.string.male), getResources().getString(R.string.female)});
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueFormatter(new LargeValueFormatter());
            data.setValueTextColor(Color.WHITE);

            barChart.setData(data);
        }

        barChart.setFitBars(true);
        float barWidth = 0.5f; // x4 DataSet
        // specify the width each bar should have
        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinimum(barChart.getBarData().getXMin() - .5f);
        barChart.getXAxis().setAxisMaximum(barChart.getBarData().getXMax() + .5f);
        barChart.getXAxis().setLabelCount(count);
        barChart.getXAxis().setCenterAxisLabels(false);
        barChart.getXAxis().setLabelCount(count);
        barChart.invalidate();
    }

    private void fetchData() {

        StringRequest request = new StringRequest(Request.Method.GET, Constants.URL_POPULATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray array = jsonObject.getJSONArray("results");
                            float[] maleEntries = new float[array.length()];
                            float[] femaleEntries = new float[array.length()];
                            String[] states = new String[array.length()];
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);
                                valState = object1.getString("state");
                                valMaleCount = object1.getString("male_count");
                                valFemaleCount = object1.getString("female_count");

                                if (!TextUtils.isEmpty(valState))
                                    states[i] = valState;

                                if (!TextUtils.isEmpty(valMaleCount))
                                    maleEntries[i] = Float.parseFloat(valMaleCount);

                                if (!TextUtils.isEmpty(valFemaleCount))
                                    femaleEntries[i] = Float.parseFloat(valFemaleCount);
                            }
                            setBarChart(states.length);
                            loadBarChart(states.length, maleEntries, femaleEntries, states);


                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "onErrorResponse->error : " + e.getMessage());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "onErrorResponse->error : " + error.getMessage());
                Toast.makeText(BarChart_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private static final String LOG_TAG = BarChart_Activity.class.getSimpleName();

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
