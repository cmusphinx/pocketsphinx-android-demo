package edu.cmu.pocketsphinx.demo;

public interface RecognitionListener {

    public void onPartialResult(SpeechResult result);
    
    public void onResult(SpeechResult result);
}
