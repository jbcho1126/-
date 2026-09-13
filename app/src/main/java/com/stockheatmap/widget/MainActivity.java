package com.stockheatmap.widget;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 30, 16, 16);
        root.setBackgroundColor(Color.rgb(20, 24, 30));

        TextView title = new TextView(this);
        title.setText("MY STOCK HEATMAP");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 15, 0, 25);
        root.addView(title);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);

        addStock(grid, "신세계 I&C", "+2.35%", true);
        addStock(grid, "현대제철", "+1.72%", true);
        addStock(grid, "금호석유", "-0.84%", false);
        addStock(grid, "현대모비스", "+0.65%", true);
        addStock(grid, "LG CNS", "-1.20%", false);
        addStock(grid, "삼성전자", "+1.05%", true);

        root.addView(grid);
        setContentView(root);
    }

    private void addStock(GridLayout grid, String name,
                          String rate, boolean up) {

        TextView stock = new TextView(this);

        stock.setText(name + "\n" + rate);
        stock.setTextColor(Color.WHITE);
        stock.setTextSize(19);
        stock.setGravity(Gravity.CENTER);
        stock.setPadding(10, 30, 10, 30);

        if (up) {
            stock.setBackgroundColor(Color.rgb(20, 145, 70));
        } else {
            stock.setBackgroundColor(Color.rgb(185, 55, 55));
        }

        GridLayout.LayoutParams params =
                new GridLayout.LayoutParams();

        params.width = 0;
        params.height = 190;
        params.columnSpec = GridLayout.spec(
                GridLayout.UNDEFINED, 1f);
        params.setMargins(5, 5, 5, 5);

        stock.setLayoutParams(params);
        grid.addView(stock);
    }
}
