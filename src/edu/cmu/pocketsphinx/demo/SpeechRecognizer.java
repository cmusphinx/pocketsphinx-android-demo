package edu.cmu.pocketsphinx.demo;

import java.util.Collection;
import java.util.Vector;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;

public class SpeechRecognizer {

    private static final int MSG_START = 1;
    private static final int MSG_NEXT = 2;
    private static final int MSG_STOP = 3;

    private final AudioRecord recorder;
    private final Decoder decoder;

    private final Handler handler;
    private final HandlerThread handlerThread;

    private final Handler mainLoopHandler = new Handler(Looper.getMainLooper());
    private Collection<RecognitionListener> listeners = new Vector<RecognitionListener>();

    private final short[] buffer = new short[1024];

    public SpeechRecognizer(Config config) {
        decoder = new Decoder(config);
        recorder = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
                                   (int) config.getFloat("-samprate"),
                                   AudioFormat.CHANNEL_IN_MONO,
                                   AudioFormat.ENCODING_PCM_16BIT,
                                   8192);

        handlerThread = new HandlerThread(getClass().getSimpleName());
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper(), new Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                return SpeechRecognizer.this.handleMessage(msg);
            }
        });
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void addListener(RecognitionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(RecognitionListener listener) {
        listeners.remove(listener);
    }

    public void startListening() {
        sendMessage(MSG_START);
    }

    public void stopListening() {
        sendMessage(MSG_STOP);
    }

    public boolean isActive() {
        int state = recorder.getRecordingState();
        return AudioRecord.RECORDSTATE_RECORDING == state;
    }

    private void sendMessage(int what) {
        handler.sendMessage(handler.obtainMessage(what));
    }

    private boolean handleMessage(Message msg) {
        switch (msg.what) {
            default:
                return false;
            case MSG_STOP:
                if (isActive())
                    endUtterance();
                break;
            case MSG_START:
                if (!isActive())
                    startUtterance();
            case MSG_NEXT:
                if (isActive())
                    continueUtterance();
        }

        return true;
    }

    private void startUtterance() {
        decoder.startUtt(null);
        recorder.startRecording();
    }

    private void continueUtterance() {
        int nread = recorder.read(buffer, 0, buffer.length);

        if (-1 == nread) {
            sendMessage(MSG_STOP);
            return;
        } else if (nread > 0) {
            decoder.processRaw(buffer, nread, false, false);
            final Hypothesis hypothesis = decoder.hyp();
            if (null != hypothesis)
                mainLoopHandler.post(new PartialResultCallback(hypothesis));
        }

        sendMessage(MSG_NEXT);
    }

    private void endUtterance() {
        recorder.stop();
        int nread = recorder.read(buffer, 0, buffer.length);
        Log.d(getClass().getSimpleName(), "recorder.read returned " + nread);
        if (nread > 0)
            decoder.processRaw(buffer, nread, false, false);

        decoder.endUtt();
        final Hypothesis hypothesis = decoder.hyp();
        if (null != hypothesis)
            mainLoopHandler.post(new ResultCallback(hypothesis));
    }

    private class ResultCallback implements Runnable {

        protected final Hypothesis hypothesis;

        public ResultCallback(Hypothesis hypothesis) {
            this.hypothesis = hypothesis;
        }

        @Override
        public void run() {
            for (RecognitionListener l : listeners)
                l.onResult(hypothesis);
        }
    }

    private class PartialResultCallback extends ResultCallback {

        public PartialResultCallback(Hypothesis hypothesis) {
            super(hypothesis);
        }

        @Override
        public void run() {
            for (RecognitionListener l : listeners)
                l.onPartialResult(hypothesis);
        }
    }
}