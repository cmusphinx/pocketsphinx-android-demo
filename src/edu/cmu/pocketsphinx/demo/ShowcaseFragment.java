package edu.cmu.pocketsphinx.demo;

import static edu.cmu.pocketsphinx.SphinxUtil.syncAssets;

import java.io.IOException;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public abstract class ShowcaseFragment extends Fragment implements
        OnCheckedChangeListener, RecognitionListener {

    protected Context context;
    protected SpeechRecognizer recognizer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity();

        try {
            recognizer = new SpeechRecognizer(syncAssets(context, "models"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        recognizer.addListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        recognizer.stopListening();
        recognizer.removeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        if (checked)
            recognizer.startListening();
        else
            recognizer.stopListening();
    }
}