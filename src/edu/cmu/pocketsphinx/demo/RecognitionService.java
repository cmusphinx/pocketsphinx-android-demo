package edu.cmu.pocketsphinx.demo;

import static edu.cmu.pocketsphinx.SphinxUtil.syncAssets;
import static edu.cmu.pocketsphinx.sphinxbase.setLogFile;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognitionListener;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;

public class RecognitionService extends Service {

    private static final int SAMPLE_RATE = 8000;

    private static final int MSG_START = 1;
    private static final int MSG_CONTINUE = 2;
    private static final int MSG_STOP = 3;

    static {
        System.loadLibrary("pocketsphinx_jni");
    }

    private final IBinder binder = new LocalBinder();

    private volatile Looper serviceLooper;
    private volatile ServiceHandler serviceHandler;

    private AudioRecord recorder;
    private Decoder decoder;

    private RecognitionListener listener;
    private final Handler listenerHandler = new Handler(Looper.getMainLooper());

    public class LocalBinder extends Binder {
        RecognitionService getService() {
            return RecognitionService.this;
        }
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            short[] buf = new short[1024];

            switch (msg.what) {
                case MSG_START:
                    decoder.startUtt(null);
                    recorder.startRecording();

                    listenerHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            listener.onBeginningOfSpeech();
                        }
                    });
                case MSG_CONTINUE:
                    int nread = recorder.read(buf, 0, buf.length);
                    decoder.processRaw(buf, nread, false, false);

                    Hypothesis hyp = decoder.hyp();
                    if (null != hyp) {
                        final Bundle bundle = new Bundle();
                        bundle.putString("hypothesis", hyp.getHypstr());
                        listenerHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                listener.onPartialResults(bundle);
                            }
                        });
                    }

                    Message newMsg = obtainMessage();
                    newMsg.what = MSG_CONTINUE;
                    sendMessage(newMsg);
                    break;
                case MSG_STOP:
                    recorder.stop();
                    nread = recorder.read(buf, 0, buf.length);
                    decoder.processRaw(buf, nread, false, false);
                    decoder.endUtt();

                    hyp = decoder.hyp();
                    if (null != hyp) {
                        final Bundle bundle = new Bundle();
                        bundle.putString("hypothesis", hyp.getHypstr());
                        listenerHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                listener.onResults(bundle);
                            }
                        });
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                                   SAMPLE_RATE,
                                   AudioFormat.CHANNEL_IN_MONO,
                                   AudioFormat.ENCODING_PCM_16BIT,
                                   8192);

        Config config = Decoder.defaultConfig();

        try {
            File m = syncAssets(getApplicationContext(), "models");
            File root = m.getParentFile();
            setLogFile(new File(root, "pocketsphinx.log").getPath());

            config.setString("-jsgf", new File(m, "dialog.gram").getPath());
            config.setString("-lm", new File(m, "lm/weather.lm").getPath());
            config.setString("-hmm", new File(m, "hmm/hub4wsj_sc_8k").getPath());
            config.setString("-dict", new File(m, "lm/cmu07a.dic").getPath());
            config.setString("-rawlogdir", root.getPath());
            config.setFloat("-samprate", SAMPLE_RATE);
            config.setInt("-maxhmmpf", 10000);
            config.setBoolean("-backtrace", true);
            config.setBoolean("-bestpath", false);
            config.setBoolean("-remove_noise", false);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        decoder = new Decoder(config);

        HandlerThread thread = new HandlerThread(getClass().getSimpleName());
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public void onDestroy() {
        serviceLooper.quit();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    public void startListening(RecognitionListener listener) {
        this.listener = listener;
        Message msg = serviceHandler.obtainMessage();
        msg.what = MSG_START;
        serviceHandler.sendMessage(msg);
    }

    public void stopListening() {
        Message msg = serviceHandler.obtainMessage();
        msg.what = MSG_STOP;
        serviceHandler.sendMessage(msg);
    }
}
