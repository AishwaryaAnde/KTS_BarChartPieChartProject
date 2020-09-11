package com.kts_barchartpiechartproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kts_barchartpiechartproject.Constants;
import com.kts_barchartpiechartproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS;

public class PieChart_Activity extends AppCompatActivity
        implements OnChartValueSelectedListener {

    TextView rent, grocery, transport, current, school_fees, savings;

    PieChart pieChart;
    private String valSavings;
    private String valRent;
    private String valGrocery;
    private String valTransport;
    private String valCurrent;
    private String valSchoolFees;
    private TableLayout mTableLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.expenses));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pieChart = findViewById(R.id.piechart);
        rent = findViewById(R.id.rent);
        grocery = findViewById(R.id.grocery);
        transport = findViewById(R.id.transport);
        current = findViewById(R.id.current);
        school_fees = findViewById(R.id.fee);
        savings = findViewById(R.id.savings);

        fetchData();
    }

    private void fetchData() {

        StringRequest request = new StringRequest(Request.Method.GET, Constants.URL_EXPENSES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);

                            JSONArray array = jsonObject.getJSONArray("results");

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.getJSONObject(i);

                                valRent = object1.getString("rent");
                                valGrocery = object1.getString("grocery");
                                valTransport = object1.getString("transport");
                                valCurrent = object1.getString("current");
                                valSchoolFees = object1.getString("school_fees");
                                valSavings = object1.getString("savings");

                                rent.setText(valRent);
                                grocery.setText(valGrocery);
                                transport.setText(valTransport);
                                current.setText(valCurrent);
                                school_fees.setText(valSchoolFees);
                                savings.setText(valSavings);

                                String[] entries = new String[6];
                                entries[0] = "" + valRent;
                                entries[1] = "" + valGrocery;
                                entries[2] = "" + valTransport;
                                entries[3] = "" + valCurrent;
                                entries[4] = "" + valSchoolFees;
                                entries[5] = "" + valSavings;
                                localPieChartData();
                                setDataToPieChart(entries);
                            }

                        } catch (JSONException e) {
                            Log.d(LOG_TAG, "onErrorResponse->error : " + e.getMessage());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG, "onErrorResponse->error : " + error.getMessage());
                Toast.makeText(PieChart_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private static final String LOG_TAG = PieChart_Activity.class.getSimpleName();

    private void localPieChartData() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setExtraOffsets(45.f, 0.f, 30.f, 0.f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(1f);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(1f);
        pieChart.setTransparentCircleAlpha(80);

        pieChart.setDrawCenterText(false);
        pieChart.setRotationAngle(270);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);

        // add a selection listener
        pieChart.setOnChartValueSelectedListener(this);

        pieChart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setWordWrapEnabled(true);
        l.setDrawInside(false);
        l.setYOffset(5f);
        l.setEnabled(true);

    }

    private void setDataToPieChart(String[] valueList) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        String[] parties = new String[]
                { getResources().getString(R.string.rent),
                getResources().getString(R.string.grocery),
                getResources().getString(R.string.transport),
                getResources().getString(R.string.current),
                getResources().getString(R.string.school_fee),
                getResources().getString(R.string.savings) }
                ;
        for (int i = 0; i < valueList.length; i++) {
            entries.add(new PieEntry(Float.parseFloat((valueList[i])),
                    parties[i % parties.length]));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        int[] colors = new int[10];
        int counter = 0;
        for (int color : ColorTemplate.JOYFUL_COLORS
        ) {
            colors[counter] = color;
            counter++;
        }

        for (int color : ColorTemplate.MATERIAL_COLORS
        ) {
            colors[counter] = color;
            counter++;
        }
        dataSet.setColors(colors);
        dataSet.setValueLinePart1OffsetPercentage(30.f);
        dataSet.setValueLinePart1Length(1.1f);
        dataSet.setValueLinePart2Length(.2f);
        dataSet.setValueTextColor(android.R.color.secondary_text_light);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieChart.setEntryLabelColor(Color.BLACK);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.d(LOG_TAG, "Entry selected " + e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.d(LOG_TAG, "Nothing selected");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
