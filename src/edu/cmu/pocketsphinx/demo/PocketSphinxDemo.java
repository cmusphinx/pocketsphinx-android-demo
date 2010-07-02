package edu.cmu.pocketsphinx.demo;

import android.app.Activity;
import android.os.Bundle;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.pocketsphinx;

public class PocketSphinxDemo extends Activity {
	Decoder ps;
	static {
		System.loadLibrary("pocketsphinx_jni");
	}
	public void createDecoder() {
		Config c = new Config();
		/* In 2.2 and above we can use getExternalFilesDir() or whatever it's called */
		c.setString("-hmm", "/sdcard/Android/data/edu.cmu.pocketsphinx/hmm/en_US/hub4wsj_sc_8k");
		c.setString("-dict", "/sdcard/Android/data/edu.cmu.pocketsphinx/lm/en_US/hub4.5000.dic");
		c.setString("-lm", "/sdcard/Android/data/edu.cmu.pocketsphinx/lm/en_US/hub4.5000.DMP");
		this.ps = new Decoder(c);
	}
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pocketsphinx.setLogfile("/sdcard/Android/data/edu.cmu.pocketsphinx/pocketsphinx.log");
        createDecoder();
        setContentView(R.layout.main);
    }
}