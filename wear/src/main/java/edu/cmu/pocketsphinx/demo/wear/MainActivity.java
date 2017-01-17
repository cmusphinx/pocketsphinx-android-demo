package edu.cmu.pocketsphinx.demo.wear;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import edu.cmu.pocketsphinx.demo.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(edu.cmu.pocketsphinx.demo.wear.R.layout.activity_main);
        ((TextView) findViewById(edu.cmu.pocketsphinx.demo.wear.R.id.caption_text))
                .setText("Preparing the recognizer");

        // Check if user has given permission to record audio
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        runRecognizerSetup();
    }

    @Override
    protected void setPartialResultText(String text) {
        ((TextView) findViewById(edu.cmu.pocketsphinx.demo.wear.R.id.result_text)).setText(text);
    }

    @Override
    protected void setResultText() {
        ((TextView) findViewById(edu.cmu.pocketsphinx.demo.wear.R.id.result_text)).setText("");
    }

    @Override
    protected void setErrorText(Exception error) {
        ((TextView) findViewById(edu.cmu.pocketsphinx.demo.wear.R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    protected void setPostExecuteText(Exception result) {
        ((TextView) findViewById(edu.cmu.pocketsphinx.demo.wear.R.id.caption_text)).setText("Failed to init recognizer " + result);
    }

    @Override
    protected void setSearchText(String caption) {
        ((TextView) findViewById(edu.cmu.pocketsphinx.demo.wear.R.id.caption_text)).setText(caption);
    }
}
