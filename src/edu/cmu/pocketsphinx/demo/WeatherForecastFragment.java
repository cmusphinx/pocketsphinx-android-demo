package edu.cmu.pocketsphinx.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class WeatherForecastFragment extends RecognitionFragment {

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
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onBufferReceived(byte[] arg0) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int arg0) {
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        resultText.setText(bundle.getString("hypothesis"));
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
    }

    @Override
    public void onResults(Bundle bundle) {
        resultText.setText(bundle.getString("hypothesis"));
    }

    @Override
    public void onRmsChanged(float rms) {
    }
}
