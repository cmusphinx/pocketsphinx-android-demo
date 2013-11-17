package edu.cmu.pocketsphinx.demo;

import static edu.cmu.pocketsphinx.SphinxUtil.syncAssets;
import static edu.cmu.pocketsphinx.sphinxbase.setLogFile;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;

public class SpeechRecognizer {

    private static final int MSG_START = 1;
    private static final int MSG_STOP = 2;
    private static final int MSG_CANCEL = 3;
    private static final int MSG_NEXT = 4;

    private static final int SAMPLE_RATE = 8000;

    private static SpeechRecognizer instance;
    
    static {
        System.loadLibrary("pocketsphinx_jni");
    }
    
    public static SpeechRecognizer createGrammarRecognizer(Context context) {
        ensureCreatedInstance(context);
        instance.decoder.updateFsgset();
        
        return instance;
    }
    
    public static SpeechRecognizer createNGramRecognizer(Context context) {
        ensureCreatedInstance(context);
        instance.decoder.updateLmset(null);
        
        return instance;
    }
    
    private static void ensureCreatedInstance(Context context) {
        if (null == instance) {
            synchronized (SpeechRecognizer.class) {
                if (null == instance)
                    instance = new SpeechRecognizer(context);
            }
        }
    }

    private static final String joinPath(File dir, String path) {
        return new File(dir, path).getPath();
    }

    private final short[] buffer = new short[1024];

    private final AudioRecord recorder;
    private final Decoder decoder;

    private final Handler handler;
    private final HandlerThread handlerThread;

    private final Handler listenerHandler = new Handler(Looper.getMainLooper());
    private RecognitionListener listener;

    private SpeechRecognizer(Context context) {
        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                                   SAMPLE_RATE,
                                   AudioFormat.CHANNEL_IN_MONO,
                                   AudioFormat.ENCODING_PCM_16BIT,
                                   8192);

        Config config = Decoder.defaultConfig();

        try {
            File modelsDir = syncAssets(context, "models");
            File root = modelsDir.getParentFile();
            setLogFile(new File(root, "pocketsphinx.log").getPath());

            config.setString("-lm", joinPath(modelsDir, "lm/weather.dmp"));
            config.setString("-jsgf", joinPath(modelsDir, "dialog.gram"));
            config.setString("-dict", joinPath(modelsDir, "lm/cmu07a.dic"));
            config.setString("-hmm", joinPath(modelsDir, "hmm/hub4wsj_sc_8k"));

            config.setString("-rawlogdir", root.getPath());
            config.setFloat("-samprate", SAMPLE_RATE);
            config.setInt("-maxhmmpf", 10000);
            config.setBoolean("-backtrace", true);
            config.setBoolean("-bestpath", false);
            config.setBoolean("-remove_noise", false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        decoder = new Decoder(config);

        handlerThread = new HandlerThread(getClass().getSimpleName());
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), new Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                return SpeechRecognizer.this.handleMessage(msg);
            }
        });
    }

    public void startRecognition(RecognitionListener listener) {
        if (isActive())
            throw new IllegalStateException("already started");

        this.listener = listener;
        handler.sendMessage(handler.obtainMessage(MSG_START));
    }

    public void stop() {
        if (!isActive())
            throw new IllegalStateException("not started");
        handler.sendMessage(handler.obtainMessage(MSG_STOP));
    }

    public void cancel() {
        if (!isActive())
            throw new IllegalStateException("not started");
        handler.sendMessage(handler.obtainMessage(MSG_CANCEL));
    }

    public boolean isActive() {
        int state = recorder.getRecordingState();
        return AudioRecord.RECORDSTATE_RECORDING == state;
    }

    private boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_START:
                startUtterance();
            case MSG_NEXT:
                if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
                    continueUtterance();
                break;
            case MSG_CANCEL:
            case MSG_STOP:
                endUtterance(msg.what == MSG_CANCEL);
                break;
            default:
                return false;
        }

        return true;
    }

    private void startUtterance() {
        decoder.startUtt(null);
        recorder.startRecording();
    }

    private void continueUtterance() {
        int nread;

        if ((nread = recorder.read(buffer, 0, buffer.length)) > 0) {
            decoder.processRaw(buffer, nread, false, false);
            final Hypothesis hyp = decoder.hyp();

            if (null != hyp) {
                listenerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onPartialResult(new SpeechResult(decoder));
                    }
                });
            }

            handler.sendMessage(handler.obtainMessage(MSG_NEXT));
        } else if (nread == -1) {
            handler.sendMessage(handler.obtainMessage(MSG_STOP));
        }
    }

    private void endUtterance(boolean cancel) {
        recorder.stop();
        
        if (cancel) {
            decoder.endUtt();
            return;
        }

        int nread = recorder.read(buffer, 0, buffer.length);
        if (nread > 0)
            decoder.processRaw(buffer, nread, false, false);

        decoder.endUtt();
        final Hypothesis hyp = decoder.hyp();

        if (null != hyp) {
            listenerHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onResult(new SpeechResult(decoder));
                }
            });
        }
    }
}