package edu.cmu.pocketsphinx.demo;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.os.Bundle;

public class PocketSphinxActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab bankTab = bar.newTab();
        bankTab.setText("Bank Account");
        bankTab.setTabListener(new TabFragmentListener<BankAccountFragment>(this,
                                                                            "bank account",
                                                                            BankAccountFragment.class,
                                                                            null));
        Tab weatherTab = bar.newTab();
        weatherTab.setText("Weather Forecast");
        weatherTab
                .setTabListener(new TabFragmentListener<WeatherForecastFragment>(this,
                                                                                 "weather forecast",
                                                                                 WeatherForecastFragment.class,
                                                                                 null));
        bar.addTab(bankTab);
        bar.addTab(weatherTab);

        if (null != savedInstanceState)
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
    }
}
