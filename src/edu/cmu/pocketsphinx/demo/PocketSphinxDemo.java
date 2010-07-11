package edu.cmu.pocketsphinx.demo;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.pocketsphinx;

/**
 * Speech recognition task, which runs in a worker thread.
 * 
 * This class implements speech recognition for this demo application.
 * 
 * @author dhuggins
 */
class RecognizerTask implements Runnable {
	/**
	 * PocketSphinx native decoder object.
	 */
	Decoder ps;
	/**
	 * Audio input object.
	 */
	AudioRecord rec;
	/**
	 * Flag which indicates whether recording/recognition is currently underway.
	 */
	Boolean recording;

	public RecognizerTask() {
		this.createDecoder();
		this.createAudio();
		this.recording = false;
	}

	/**
	 * Initialize the speech recognizer.
	 */
	void createDecoder() {
		pocketsphinx
				.setLogfile("/sdcard/Android/data/edu.cmu.pocketsphinx/pocketsphinx.log");
		Config c = new Config();
		/*
		 * In 2.2 and above we can use getExternalFilesDir() or whatever it's
		 * called
		 */
		c.setString("-hmm",
				"/sdcard/Android/data/edu.cmu.pocketsphinx/hmm/en_US/hub4wsj_sc_8k");
		c.setString("-dict",
				"/sdcard/Android/data/edu.cmu.pocketsphinx/lm/en_US/hub4.5000.dic");
		c.setString("-lm",
				"/sdcard/Android/data/edu.cmu.pocketsphinx/lm/en_US/hub4.5000.DMP");
		c.setString("-rawlogdir", "/sdcard/Android/data/edu.cmu.pocketsphinx");
		c.setInt("-samprate", 8000);
		c.setInt("-pl_window", 2);
		c.setBoolean("-backtrace", true);
		c.setBoolean("-bestpath", false);
		this.ps = new Decoder(c);
	}

	/**
	 * Initialize audio recording.
	 */
	void createAudio() {
		this.rec = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 8000,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
				32768);
	}

	public void run() {
		while (true) {
			try {
				synchronized (this.recording) {
					this.recording.wait();
				}
				Log.d(getClass().getName(), (this.recording ? "" : "not ")
						+ "recording!");
				if (!this.recording)
					continue;
			} catch (InterruptedException e) {
				Log.d(getClass().getName(), "interrupted!");
				continue;
			}
			this.rec.startRecording();
			this.ps.startUtt();
			short[] buf = new short[512];
			String hypstr = null;
			while (this.recording) {
				int nshorts = this.rec.read(buf, 0, buf.length);
				Log.d(getClass().getName(), "Read " + nshorts + " values");
				if (nshorts <= 0)
					break;
				this.ps.processRaw(buf, false, false);
				Hypothesis hyp = this.ps.getHyp();
				if (hyp != null && hyp.getHypstr() != hypstr) {
					hypstr = hyp.getHypstr();
					Log.d(getClass().getName(), "partial hyp: " + hypstr);
				}
			}
			Log.d(getClass().getName(), "end of utterance");
			this.rec.stop();
			this.ps.endUtt();
			Hypothesis hyp = this.ps.getHyp();
			Log.d(getClass().getName(), "hyp: " + hyp.getHypstr());
		}
	}

	public void start() {
		Log.d(getClass().getName(), "start");
		synchronized (this.recording) {
			this.recording.notifyAll();
			this.recording = true;
		}
	}

	public void stop() {
		Log.d(getClass().getName(), "stop");
		this.recording = false;
	}
}

public class PocketSphinxDemo extends Activity implements OnTouchListener {
	static {
		System.loadLibrary("pocketsphinx_jni");
	}
	/**
	 * Recognizer task, which runs in a worker thread.
	 */
	RecognizerTask rec;
	/**
	 * Worker thread in which the recognizer task runs.
	 */
	Thread rec_thread;

	/**
	 * Respond to touch events on the Speak button.
	 * 
	 * This allows the Speak button to function as a "push and hold" button, by
	 * triggering the start of recognition when it is first pushed, and the end
	 * of recognition when it is released.
	 * 
	 * @param v
	 *            View on which this event is called
	 * @param event
	 *            Event that was triggered.
	 */
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			this.rec.start();
			break;
		case MotionEvent.ACTION_UP:
			this.rec.stop();
			break;
		default:
			;
		}
		/* Let the button handle its own state */
		return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.rec = new RecognizerTask();
		this.rec_thread = new Thread(this.rec);
		Button b = (Button) findViewById(R.id.Button01);
		b.setOnTouchListener(this);
		this.rec_thread.start();
	}
}