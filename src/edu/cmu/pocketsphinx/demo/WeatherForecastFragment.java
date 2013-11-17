package edu.cmu.pocketsphinx.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.cmu.pocketsphinx.Hypothesis;

public class WeatherForecastFragment extends ShowcaseFragment {

    private TextView resultText;

    private ToggleButton toggleButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weather_forecast, container, false);
        toggleButton = (ToggleButton) v.findViewById(R.id.start_button);
        resultText = (TextView) v.findViewById(R.id.result_text);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        toggleButton.setChecked(false);
        toggleButton.setOnCheckedChangeListener(this);
        // TODO: switch to LM
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        resultText.setText(hypothesis.getHypstr());
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        resultText.setText(hypothesis.getHypstr());
    }
}
