package edu.cmu.pocketsphinx.demo;

import java.io.File;
import java.io.IOException;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.FsgModel;
import edu.cmu.pocketsphinx.Jsgf;
import edu.cmu.pocketsphinx.JsgfRule;
import edu.cmu.pocketsphinx.NGramModel;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public class PocketSphinxActivity extends Activity {

    protected static String KWS_SRCH_NAME = "wakeup_search";
    protected static String KEYPHRASE = "oh mighty computer";

    static {
        System.loadLibrary("pocketsphinx_jni");
    }
    
    private static String joinPath(File dir, String path) {
        return new File(dir, path).getPath();
    }
    
    private ActionBar tabBar;
    private SpeechRecognizer recognizer;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        
        File appDir;
        try {
            appDir = Assets.syncAssets(getApplicationContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        Config config = Decoder.defaultConfig();
        config.setString("-dict", joinPath(appDir, "models/lm/cmu07a.dic"));
        config.setString("-hmm", joinPath(appDir, "models/hmm/en-us-semi"));
        config.setString("-rawlogdir", appDir.getPath());
        config.setInt("-maxhmmpf", 10000);
        config.setBoolean("-fwdflat", false);
        config.setBoolean("-bestpath", false);
        config.setFloat("-kws_threshold", 1e-5);
        recognizer = new SpeechRecognizer(config);
        
        recognizer.setKws(KWS_SRCH_NAME, KEYPHRASE);
        Jsgf jsgf = new Jsgf(joinPath(appDir, "models/dialog.gram"));
        JsgfRule rule = jsgf.getRule("<dialog.command>");
        int lw = config.getInt("-lw");
        FsgModel fsg = jsgf.buildFsg(rule, recognizer.getLogmath(), lw);
        recognizer.setFsg(BankAccountFragment.class.getSimpleName(), fsg);
        recognizer.setSearch(BankAccountFragment.class.getSimpleName());
        NGramModel lm = new NGramModel(config, recognizer.getLogmath(), joinPath(appDir, "models/lm/weather.dmp"));
        recognizer.setLm(WeatherForecastFragment.class.getSimpleName(), lm);
        
        tabBar = getActionBar();
        tabBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab t = tabBar.newTab();
        t.setText("Bank Account");
        t.setTabListener(newTabListener(BankAccountFragment.class, state));
        tabBar.addTab(t);

        t = tabBar.newTab();
        t.setText("Weather Forecast");
        t.setTabListener(newTabListener(WeatherForecastFragment.class, state));
        tabBar.addTab(t);

        if (null != state)
            tabBar.setSelectedNavigationItem(state.getInt("tab", 0));
    }

    public SpeechRecognizer getRecognizer() {
        return recognizer;
    }
        
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tab", tabBar.getSelectedNavigationIndex());
        super.onSaveInstanceState(outState);
    }

    <T extends Fragment> TabListener newTabListener(Class<T> c, Bundle state) {
        return new TabFragmentListener<T>(this, c.getSimpleName(), c, state);
    }
}
