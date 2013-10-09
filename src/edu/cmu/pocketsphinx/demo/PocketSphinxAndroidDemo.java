package edu.cmu.pocketsphinx.demo;

import java.io.File;
import java.io.IOException;

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

import static edu.cmu.pocketsphinx.SphinxUtil.syncAssets;
import static edu.cmu.pocketsphinx.sphinxbase.setLogFile;


public class PocketSphinxAndroidDemo extends Activity {

    private class RecognitionTask
            extends AsyncTask<AudioRecord, Void, Hypothesis> {

        private final Decoder decoder;

        public RecognitionTask() {
            File root = null;

            try {
                root = syncAssets(getApplicationContext(), "models");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            File rootLog = new File(root.getParentFile(), "pocketsphinx.log");
            setLogFile(rootLog.getPath());

            Config config = Decoder.defaultConfig();
            config.setString("-lm",
                    new File(root, "lm/hub4.5000.DMP").getPath());
            config.setString("-hmm",
                    new File(root, "hmm/hub4wsj_sc_8k").getPath());
            config.setString("-dict",
                    new File(root, "lm/hub4.5000.dic").getPath());
            config.setString("-rawlogdir", root.getPath());
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
                speechResult.append("\n<no speech>");
        }
    }

    private static final int SAMPLE_RATE = 8000;

    static {
        System.loadLibrary("pocketsphinx_jni");
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
