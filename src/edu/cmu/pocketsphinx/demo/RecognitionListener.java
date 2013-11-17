package edu.cmu.pocketsphinx.demo;

import edu.cmu.pocketsphinx.Hypothesis;

public interface RecognitionListener {

    public void onPartialResult(Hypothesis hypothesis);
    
    public void onResult(Hypothesis hypothesis);
}
