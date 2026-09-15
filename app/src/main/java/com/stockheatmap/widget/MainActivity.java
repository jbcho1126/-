package com.stockheatmap.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private GridLayout grid;
    private final ArrayList<Stock> stocks = new ArrayList<>();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("stock_data_v2", MODE_PRIVATE);

        loadStocks();
        createScreen();
    }

    private void createScreen() {

        ScrollView scrollView = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 30, 16, 30);
        root.setBackgroundColor(Color.rgb(20, 24, 30));

        TextView title = new TextView(this);
        title.setText("주식 추세추종 히트맵");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 15, 0, 20);
        root.addView(title);

        Button addButton = new Button(this);
        addButton.setText("+ 관심종목 검색");
        addButton.setTextSize(17);

        addButton.setOnClickListener(v ->
                showStockDialog(-1));

        root.addView(addButton);

        TextView guide = new TextView(this);
        guide.setText(
                "종목명으로 관심종목을 추가하세요.\n" +
                "종목 카드를 길게 누르면 수정 또는 삭제할 수 있습니다."
        );
        guide.setTextColor(Color.LTGRAY);
        guide.setTextSize(13);
        guide.setPadding(5, 15, 5, 15);

        root.addView(guide);

        grid = new GridLayout(this);
        grid.setColumnCount(2);

        root.addView(grid);

        scrollView.addView(root);

        setContentView(scrollView);

        refreshGrid();
    }

    private void refreshGrid() {

        grid.removeAllViews();

        for (int i = 0; i < stocks.size(); i++) {

            final int position = i;
            Stock item = stocks.get(i);

            TextView stockView = new TextView(this);

            stockView.setText(
                    item.name +
                    "\n\n데이터 연결 대기"
            );

            stockView.setTextColor(Color.WHITE);
            stockView.setTextSize(18);
            stockView.setGravity(Gravity.CENTER);
            stockView.setPadding(8, 25, 8, 25);

            stockView.setBackgroundColor(
                    Color.rgb(70, 75, 82)
            );

            GridLayout.LayoutParams params =
                    new GridLayout.LayoutParams();

            params.width = 0;
            params.height = 210;

            params.columnSpec =
                    GridLayout.spec(
                            GridLayout.UNDEFINED,
                            1f
                    );

            params.setMargins(5, 5, 5, 5);

            stockView.setLayoutParams(params);

            stockView.setOnLongClickListener(v -> {

                showEditDelete(position);

                return true;
            });

            grid.addView(stockView);
        }
    }

    private void showStockDialog(int position) {

        LinearLayout box =
                new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL);

        box.setPadding(
                40, 10, 40, 0);

        EditText nameInput =
                new EditText(this);

        nameInput.setHint(
                "종목명 예: 현대제철");

        box.addView(nameInput);

        if (position >= 0) {

            Stock stock =
                    stocks.get(position);

            nameInput.setText(
                    stock.name);
        }

        AlertDialog dialog =
                new AlertDialog.Builder(this)

                .setTitle(
                        position >= 0
                                ? "종목명 수정"
                                : "관심종목 검색")

                .setView(box)

                .setPositiveButton(
                        position >= 0
                                ? "저장"
                                : "추가",
                        null)

                .setNegativeButton(
                        "취소",
                        null)

                .create();

        dialog.setOnShowListener(d -> {

            Button saveButton =
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE);

            saveButton.setOnClickListener(v -> {

                String name =
                        nameInput
                                .getText()
                                .toString()
                                .trim();

                if (name.isEmpty()) {

                    Toast.makeText(
                            this,
                            "종목명을 입력해주세요.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                if (position >= 0) {

                    stocks.set(
                            position,
                            new Stock(name));

                } else {

                    if (containsStock(name)) {

                        Toast.makeText(
                                this,
                                "이미 추가된 종목입니다.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    stocks.add(
                            new Stock(name));
                }

                saveStocks();

                refreshGrid();

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private boolean containsStock(
            String name) {

        for (Stock stock : stocks) {

            if (stock.name.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    private void showEditDelete(
            int position) {

        String[] menu = {
                "수정",
                "삭제"
        };

        new AlertDialog.Builder(this)

                .setTitle(
                        stocks
                                .get(position)
                                .name)

                .setItems(
                        menu,
                        (dialog, which) -> {

                            if (which == 0) {

                                showStockDialog(
                                        position);

                            } else {

                                confirmDelete(
                                        position);
                            }
                        })

                .show();
    }

    private void confirmDelete(
            int position) {

        new AlertDialog.Builder(this)

                .setTitle("종목 삭제")

                .setMessage(
                        stocks
                                .get(position)
                                .name
                                + " 종목을 삭제할까요?"
                )

                .setPositiveButton(
                        "삭제",
                        (dialog, which) -> {

                            stocks.remove(
                                    position);

                            saveStocks();

                            refreshGrid();
                        })

                .setNegativeButton(
                        "취소",
                        null)

                .show();
    }

    private void saveStocks() {

        try {

            JSONArray array =
                    new JSONArray();

            for (Stock stock : stocks) {

                JSONObject object =
                        new JSONObject();

                object.put(
                        "name",
                        stock.name);

                array.put(object);
            }

            prefs.edit()
                    .putString(
                            "stocks",
                            array.toString())
                    .apply();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void loadStocks() {

        stocks.clear();

        String saved =
                prefs.getString(
                        "stocks",
                        "");

        if (saved.isEmpty()) {

            stocks.add(
                    new Stock(
                            "현대제철"));

            stocks.add(
                    new Stock(
                            "신세계 I&C"));

            return;
        }

        try {

            JSONArray array =
                    new JSONArray(saved);

            for (int i = 0;
                 i < array.length();
                 i++) {

                JSONObject object =
                        array.getJSONObject(i);

                stocks.add(
                        new Stock(
                                object.getString(
                                        "name")
                        )
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static class Stock {

        String name;

        Stock(String name) {

            this.name = name;
        }
    }
    }
