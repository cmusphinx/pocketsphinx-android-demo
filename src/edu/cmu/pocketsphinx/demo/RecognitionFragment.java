package edu.cmu.pocketsphinx.demo;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import edu.cmu.pocketsphinx.demo.RecognitionService.LocalBinder;

public abstract class RecognitionFragment extends Fragment implements
        OnCheckedChangeListener, RecognitionListener {

    protected Context context;
    protected RecognitionService recognitionService;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            recognitionService = binder.getService();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(context, RecognitionService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRecognition();
        context.unbindService(connection);
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        if (checked)
            startRecognition();
        else
            stopRecognition();
    }

    protected void startRecognition() {
        recognitionService.startListening(this);
    }

    protected void stopRecognition() {
        recognitionService.stopListening();
    }
}