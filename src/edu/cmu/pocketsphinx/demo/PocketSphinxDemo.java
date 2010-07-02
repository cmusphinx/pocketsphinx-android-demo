package edu.cmu.pocketsphinx.demo;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.pocketsphinx;

class RecognizerTask implements Runnable {
	Decoder ps;
	AudioRecord rec;
	
	public RecognizerTask() {
		this.createDecoder();
		this.createAudio();
	}

	void createDecoder() {
        pocketsphinx.setLogfile("/sdcard/Android/data/edu.cmu.pocketsphinx/pocketsphinx.log");
		Config c = new Config();
		/* In 2.2 and above we can use getExternalFilesDir() or whatever it's called */
		c.setString("-hmm", "/sdcard/Android/data/edu.cmu.pocketsphinx/hmm/en_US/hub4wsj_sc_8k");
		c.setString("-dict", "/sdcard/Android/data/edu.cmu.pocketsphinx/lm/en_US/hub4.5000.dic");
		c.setString("-lm", "/sdcard/Android/data/edu.cmu.pocketsphinx/lm/en_US/hub4.5000.DMP");
		/* Necessary because binary data is always big-endian in Java. */
		c.setString("-input_endian", "big");
		this.ps = new Decoder(c);
	}
	
	void createAudio() {
		this.rec = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
					8000, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,
					8192);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}

public class PocketSphinxDemo extends Activity {
	static {
		System.loadLibrary("pocketsphinx_jni");
	}
	RecognizerTask rec;
	Thread rec_thread;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.rec = new RecognizerTask();
        this.rec_thread = new Thread(this.rec);
        HoldButton b = (HoldButton) findViewById(R.id.Button01);
    }
}