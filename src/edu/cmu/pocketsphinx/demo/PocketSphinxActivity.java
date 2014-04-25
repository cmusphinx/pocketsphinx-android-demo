package edu.cmu.pocketsphinx.demo;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import edu.cmu.pocketsphinx.*;


public class PocketSphinxActivity extends Activity implements
        RecognitionListener, AssetsTaskCallback {

    private static final String KWS_SEARCH_NAME = "wakeup";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String MENU_SEARCH = "menu";
    private static final String KEYPHRASE = "oh mighty computer";

    private SpeechRecognizer recognizer;
    private final Map<String, Integer> captions;
    private ProgressDialog dialog;

    public PocketSphinxActivity() {
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH_NAME, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
        captions.put(FORECAST_SEARCH, R.string.forecast_caption);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.main);
        dialog = new ProgressDialog(this);
        new AssetsTask(this, this).execute();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE))
            switchSearch(MENU_SEARCH);
        else if (text.equals(DIGITS_SEARCH))
            switchSearch(DIGITS_SEARCH);
        else if (text.equals(FORECAST_SEARCH))
            switchSearch(FORECAST_SEARCH);
        else
            ((TextView) findViewById(R.id.result_text)).setText(text);
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.result_text)).setText("");
        String text = hypothesis.getHypstr();
        makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        recognizer.startListening(searchName);

        String caption = getResources().getString(captions.get(searchName));
        ((TextView) findViewById(R.id.caption_text)).setText(caption);
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        if (DIGITS_SEARCH.equals(recognizer.getSearchName())
                || FORECAST_SEARCH.equals(recognizer.getSearchName()))
            switchSearch(KWS_SEARCH_NAME);
    }

    @Override
    public void onTaskCancelled() {
    }

    @Override
    public void onTaskComplete(File assetsDir) {
        File modelsDir = new File(assetsDir, "models");
        recognizer = defaultSetup()
                .setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
                .setDictionary(new File(modelsDir, "lm/cmu07a.dic"))
                .setRawLogDir(assetsDir)
                .setKeywordThreshold(1e-5f)
                .getRecognizer();

        recognizer.addListener(this);
        // Create keyword-activation search.
        recognizer.addKeywordSearch(KWS_SEARCH_NAME, KEYPHRASE);
        // Create grammar-based searches.
        File menuGrammar = new File(modelsDir, "grammar/menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
        File digitsGrammar = new File(modelsDir, "grammar/digits.gram");
        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
        // Create language model search.
        File languageModel = new File(modelsDir, "lm/weather.dmp");
        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);

        switchSearch(KWS_SEARCH_NAME);
        dialog.dismiss();
    }

    @Override
    public void onTaskError(Throwable e) {
        if (dialog.isShowing())
            dialog.dismiss();
        ((TextView) findViewById(R.id.caption_text)).setText(e.getMessage());
    }

    @Override
    public void onTaskProgress(File file) {
        dialog.setMessage(file.getName());
        dialog.incrementProgressBy(1);
    }

    @Override
    public void onTaskStart(int size) {
        dialog.setTitle("Copying model files...");
        dialog.setMax(size);
        dialog.show();
    }
}
