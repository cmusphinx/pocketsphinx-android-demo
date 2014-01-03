package edu.cmu.pocketsphinx.demo;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public abstract class ShowcaseFragment extends Fragment implements
        OnCheckedChangeListener, RecognitionListener {

    protected Context context;
    protected SpeechRecognizer recognizer;
    
    private Vibrator vibrator;
    private boolean sleeping;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onStart() {
        super.onStart();
        recognizer = ((PocketSphinxActivity) context).getRecognizer();
        recognizer.addListener(this);
        recognizer.setSearch(PocketSphinxActivity.KWS_SRCH_NAME);
        sleeping = true;
        recognizer.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        recognizer.stopListening();
        recognizer.removeListener(this);
        recognizer = null;
    }
    
    private void switchToReconigtion() {
        recognizer.stopListening();
        recognizer.setSearch(getClass().getSimpleName());
        sleeping = false;
        vibrator.vibrate(300);
        recognizer.startListening();
    }
    
    private void switchToSpotting() {
        recognizer.stopListening();
        recognizer.setSearch(PocketSphinxActivity.KWS_SRCH_NAME);
        sleeping = true;
        recognizer.startListening();
    }
    
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (sleeping && hypothesis.getHypstr().equals(PocketSphinxActivity.KEYPHRASE))
            //keyphrase detected. equivalent to toggle button pressed
            setButtonPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        if (checked)
            switchToReconigtion();
        else
            switchToSpotting();
    }
    
    protected abstract void setButtonPressed();
}