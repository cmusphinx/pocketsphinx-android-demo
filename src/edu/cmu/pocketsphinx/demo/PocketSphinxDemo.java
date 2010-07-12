package edu.cmu.pocketsphinx.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

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