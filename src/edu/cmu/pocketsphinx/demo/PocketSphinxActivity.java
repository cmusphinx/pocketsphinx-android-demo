package edu.cmu.pocketsphinx.demo;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class PocketSphinxActivity extends Activity {

    private ActionBar bar;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab t = bar.newTab();
        t.setText("Bank Account");
        t.setTabListener(newTabListener(BankAccountFragment.class, state));
        bar.addTab(t);

        t = bar.newTab();
        t.setText("Weather Forecast");
        t.setTabListener(newTabListener(WeatherForecastFragment.class, state));
        bar.addTab(t);

        if (null != state)
            bar.setSelectedNavigationItem(state.getInt("tab", 0));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tab", bar.getSelectedNavigationIndex());
        super.onSaveInstanceState(outState);
    }

    <T extends Fragment> TabListener newTabListener(Class<T> c, Bundle state) {
        return new TabFragmentListener<T>(this, c.getSimpleName(), c, state);
    }
}
