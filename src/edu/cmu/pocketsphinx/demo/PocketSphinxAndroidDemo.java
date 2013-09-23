package edu.cmu.pocketsphinx.demo;

import android.app.Activity;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;

import android.widget.TextView;
import android.widget.ToggleButton;

import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;

import static edu.cmu.pocketsphinx.sphinxbase.setLogFile;


public class PocketSphinxAndroidDemo extends Activity {

    private class RecognitionTask
            extends AsyncTask<AudioRecord, Void, Hypothesis> {

        private final Decoder decoder;

        public RecognitionTask() {
            Config config = Decoder.defaultConfig();
            config.setString("-hmm", DATA_PATH + "hmm/hub4wsj_sc_8k");
            config.setString("-dict", DATA_PATH + "lm/hub4.5000.dic");
            config.setString("-lm", DATA_PATH + "lm/hub4.5000.DMP");
            config.setString("-rawlogdir", DATA_PATH);
            config.setFloat("-samprate", SAMPLE_RATE);
            config.setInt("-maxhmmpf", 10000);
            config.setBoolean("-backtrace", true);
            config.setBoolean("-bestpath", false);
            config.setBoolean("-remove_noise", false);
            decoder = new Decoder(config);
        }

        protected Hypothesis doInBackground(AudioRecord... recorder) {
            int nread;
            short[] buf = new short[1024];
            decoder.startUtt(null);

            while ((nread = recorder[0].read(buf, 0, buf.length)) > 0)
                decoder.processRaw(buf, nread, false, false);

            decoder.endUtt();
            return decoder.hyp();
        }

        protected void onPostExecute(Hypothesis hypothesis) {
            if (null != hypothesis)
                speechResult.append("\n" + hypothesis.getHypstr());
            else
                throw new IllegalStateException("no hypothesis");
        }
    }

    private static final String DATA_PATH =
        "/sdcard/Android/data/edu.cmu.pocketsphinx/";

    private static final int SAMPLE_RATE = 8000;

    static {
        System.loadLibrary("pocketsphinx_jni");
        setLogFile(DATA_PATH + "pocketsphinx.log");
    }

    private TextView speechResult;
    private AudioRecord recorder;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        speechResult = (TextView) findViewById(R.id.SpeechResult);

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                                   SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                                   AudioFormat.ENCODING_PCM_16BIT, 8192);
    }

    public void onToggleRecognition(View view) {
        if (!(view instanceof ToggleButton))
            return;

        if (((ToggleButton) view).isChecked()) {
            recorder.startRecording();
            new RecognitionTask().execute(recorder);
        } else {
            recorder.stop();
        }
    }
}
