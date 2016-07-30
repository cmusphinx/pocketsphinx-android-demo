package edu.pdx.anb2;

import android.app.Activity;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class SpeechRecognizerSingleton {

    public static final String ANIMALS_GRAMMAR_FILE = "animals.gram";
    public static final String ANIMALS_GRAMMAR = "animals";
    public static SpeechRecognizer instance;

    public static SpeechRecognizer getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Call startInstance() before attempting to retrieve the instance.");
        }
        return instance;
    }

    public static void startInstance(Activity activity) throws IOException {
        instance = newInstance(activity);
    }

    public static void stopInstance(){
        instance.cancel();
        instance.shutdown();
        instance = null;
    }

    static SpeechRecognizer newInstance(final Activity activity) throws IOException {
        File assetsDir = getAssetsDirectory(activity);

        SpeechRecognizer recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setKeywordThreshold(1e-45f) // Threshold to tune for keyphrase to balance between false alarms and misses
                .setBoolean("-allphone_ci", true)  // Use context-independent phonetic search, context-dependent is too slow for mobile
                .getRecognizer();

        // create grammar-based search for digit recognition
        File animalsGrammar = new File(assetsDir, ANIMALS_GRAMMAR_FILE);
        recognizer.addGrammarSearch(ANIMALS_GRAMMAR, animalsGrammar);

        /*
        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // Create grammar-based search for selection between demos
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        // Create language model search
        File languageModel = new File(assetsDir, "weather.dmp");
        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);

        // Phonetic search
        File phoneticModel = new File(assetsDir, "en-phone.dmp");
        recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);
        */

        return recognizer;
    }

    private static File getAssetsDirectory(Activity activity) throws IOException {
        Assets assets = new Assets(activity);
        return assets.syncAssets();
    }
}
