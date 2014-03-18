package edu.cmu.pocketsphinx.demo;

import static edu.cmu.pocketsphinx.Assets.syncAssets;
import static edu.cmu.pocketsphinx.Decoder.defaultConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.cmu.pocketsphinx.*;


public class PocketSphinxActivity extends Activity implements
        RecognitionListener {

    private static String KWS_SEARCH_NAME = "wakeup";
    private static String KEYPHRASE = "oh mighty computer";

    static {
        System.loadLibrary("pocketsphinx_jni");
    }

    private File assetDir;
    private SpeechRecognizer recognizer;
    private final Map<String, Integer> captions = new HashMap<String, Integer>();

    public PocketSphinxActivity() {
        captions.put(KWS_SEARCH_NAME, R.string.kws_caption);
        captions.put("menu", R.string.menu_caption);
        captions.put("digits", R.string.digits_caption);
        captions.put("forecast", R.string.forecast_caption);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        try {
            assetDir = syncAssets(getApplicationContext());
        } catch (IOException e) {
            throw new RuntimeException("Failed to synchronize assets", e);
        }

        Config config = defaultConfig();
        config.setString("-dict", joinPath(assetDir, "models/lm/cmu07a.dic"));
        config.setString("-hmm", joinPath(assetDir, "models/hmm/en-us-semi"));
        config.setString("-rawlogdir", assetDir.getPath());
        config.setInt("-maxhmmpf", 10000);
        config.setBoolean("-fwdflat", false);
        config.setBoolean("-bestpath", false);
        config.setFloat("-kws_threshold", 1e-5);

        recognizer = new SpeechRecognizer(config);
        recognizer.addListener(this);

        // Create keyword-activation search.
        recognizer.setKws(KWS_SEARCH_NAME, KEYPHRASE);
        // Create grammar-based searches.
        int lw = config.getInt("-lw");
        addSearch("menu", "menu.gram", "<menu.item>", lw);
        addSearch("digits", "digits.gram", "<digits.digits>", lw);
        // Create language model search.
        String path = joinPath(assetDir, "models/lm/weather.dmp");
        NGramModel lm = new NGramModel(config, recognizer.getLogmath(), path);
        recognizer.setLm("forecast", lm);

        setContentView(R.layout.main);
        recognizer.addListener(this);
        switchSearch(KWS_SEARCH_NAME);
    }

    public void onStartStop(View view) {
        if (((ToggleButton) view).isChecked())
            switchSearch("menu");
        else
            switchSearch(KWS_SEARCH_NAME);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();

        if (KEYPHRASE.equals(text)) {
            switchSearch("menu");
            ((ToggleButton) findViewById(R.id.start_button)).setChecked(true);
        } else if ("digits".equals(text)) {
            switchSearch("digits");
        } else if ("forecast".equals(text)) {
            switchSearch("forecast");
        } else {
            ((TextView) findViewById(R.id.result_text)).setText(text);
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.result_text)).setText("");
    }

    private void addSearch(String name, String path, String ruleName, int lw) {
        File grammarParent = new File(joinPath(assetDir, "grammar"));
        Jsgf jsgf = new Jsgf(joinPath(grammarParent, path));
        JsgfRule rule = jsgf.getRule(ruleName);
        FsgModel fsg = jsgf.buildFsg(rule, recognizer.getLogmath(), lw);
        recognizer.setFsg(name, fsg);
    }

    private void switchSearch(String searchName) {
        recognizer.stopListening();
        recognizer.setSearch(searchName);
        recognizer.startListening();
        
        if ("menu".equals(searchName))
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);

        String caption = getResources().getString(captions.get(searchName));
        ((TextView) findViewById(R.id.caption_text)).setText(caption);
    }

    private static String joinPath(File parent, String path) {
        return new File(parent, path).getPath();
    }
}