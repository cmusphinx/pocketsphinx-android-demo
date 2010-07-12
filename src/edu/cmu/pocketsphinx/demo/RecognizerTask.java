package edu.cmu.pocketsphinx.demo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
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
		c.setFloat("-samprate", 8000.0);
		c.setInt("-maxhmmpf", 2000);
		c.setInt("-maxwpf", 10);
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
				16384);
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