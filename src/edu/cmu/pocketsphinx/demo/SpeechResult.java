package edu.cmu.pocketsphinx.demo;

import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;

public class SpeechResult {

    private String hypothesis;
    private int score;
    
    public SpeechResult(Decoder decoder) {
        Hypothesis hyp = decoder.hyp();
        if (null == hyp)
            throw new NullPointerException("no hypothesis");
        
        hypothesis = hyp.getHypstr();
        score = hyp.getBestScore();
    }
    
    public String getBestHypothesis() {
        return hypothesis;
    }
    
    public int getBestScore() {
        return score;
    }
}
