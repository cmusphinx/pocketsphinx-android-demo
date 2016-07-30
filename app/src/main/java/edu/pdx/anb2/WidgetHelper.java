package edu.pdx.anb2;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

public class WidgetHelper {

    static Button linkTo(final Activity from, final Class<? extends Activity> to, @IdRes int button) {
        Button b = (Button) from.findViewById(button);
        assert b != null;
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goTo(from, to);
            }
        });
        return b;
    }

    static void goTo(final Activity from, final Class<? extends Activity> to) {
        Intent intent = new Intent(from.getApplicationContext(), to);
        from.startActivity(intent);
    }

    static <T> Spinner populateSpinner(final Activity activity, @IdRes int spinnerResource, T[] items) {
        ArrayAdapter<T> spinnerArrayAdapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, items);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) activity.findViewById(spinnerResource);
        assert spinner != null;
        spinner.setAdapter(spinnerArrayAdapter);
        return spinner;
    }

    static <T> void populateCheckboxes(final Activity activity, @IdRes int tableResource, T[] items) {
        TableLayout tableLayout = (TableLayout) activity.findViewById(tableResource);
        assert tableLayout != null;

        TableRow row = null;
        for (int i = 0; i < items.length; i++) {
            if (i % 3 == 0) {
                row = new TableRow(activity);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                tableLayout.addView(row);
            }
            assert row != null;

            CheckBox ch = new CheckBox(activity);
            ch.setText(items[i].toString());
            row.addView(ch);
        }
    }

    static Integer[] range(int min, int max) {
        assert max > min;
        Integer[] list = new Integer[max - min + 1];
        for (int i = 0; i + min <= max; i++) {
            list[i] = i + min;
        }
        return list;
    }
}
