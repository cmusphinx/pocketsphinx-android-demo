package edu.cmu.pocketsphinx.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class WeatherForecastFragment extends ShowcaseFragment {

    private TextView resultText;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weather_forecast, container, false);
        resultText = (TextView) v.findViewById(R.id.result_text);

        ToggleButton b = (ToggleButton) v.findViewById(R.id.start_button);
        b.setOnCheckedChangeListener(this);

        return v;
    }

    @Override
    public void onPartialResult(SpeechResult result) {
        resultText.setText(result.getBestHypothesis());
    }

    @Override
    public void onResult(SpeechResult result) {
        resultText.setText(result.getBestHypothesis());
    }

    @Override
    protected void createRecognizer() {
        recognizer = SpeechRecognizer.createNGramRecognizer(context);
    }
}
