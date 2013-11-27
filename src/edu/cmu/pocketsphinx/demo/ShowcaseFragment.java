package edu.cmu.pocketsphinx.demo;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public abstract class ShowcaseFragment extends Fragment implements
        OnCheckedChangeListener, RecognitionListener {

    protected Context context;
    protected SpeechRecognizer recognizer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        recognizer = ((PocketSphinxActivity) context).getRecognizer();
        recognizer.addListener(this);
        recognizer.getDecoder().setSearch(getClass().getSimpleName());
    }

    @Override
    public void onStop() {
        super.onStop();
        recognizer.stopListening();
        recognizer.removeListener(this);
        recognizer = null;
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        if (checked)
            recognizer.startListening();
        else
            recognizer.stopListening();
    }
}