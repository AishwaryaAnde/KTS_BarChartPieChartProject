package com.kts_barchartpiechartproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btn_BarChart, btn_PieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_BarChart = findViewById(R.id.btn_BarChart);
        btn_PieChart = findViewById(R.id.btn_PieChart);

        btn_BarChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,BarChart_Activity.class);
                startActivity(intent);

            }
        });

        btn_PieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, PieChart_Activity.class);
                startActivity(intent);

            }
        });
    }
}
