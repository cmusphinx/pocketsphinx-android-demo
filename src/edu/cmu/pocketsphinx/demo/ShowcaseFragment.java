package edu.cmu.pocketsphinx.demo;

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
        context = getActivity();
        createRecognizer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recognizer.isActive())
            recognizer.cancel();
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        if (checked)
            recognizer.startRecognition(this);
        else
            recognizer.stop();
    }
    
    protected abstract void createRecognizer();
}