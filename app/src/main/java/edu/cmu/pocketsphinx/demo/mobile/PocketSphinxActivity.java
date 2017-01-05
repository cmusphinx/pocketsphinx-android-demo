/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package edu.cmu.pocketsphinx.demo.mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import edu.cmu.pocketsphinx.demo.BaseActivity;

public class PocketSphinxActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(edu.cmu.pocketsphinx.demo.mobile.R.layout.main);
        ((TextView) findViewById(edu.cmu.pocketsphinx.demo.mobile.R.id.caption_text))
                .setText("Preparing the recognizer");

        runRecognizerSetup();
    }

    @Override
    protected void setPartialResultText(String text) {
        ((TextView) findViewById(edu.cmu.pocketsphinx.demo.mobile.R.id.result_text)).setText(text);
    }

    @Override
    protected void setResultText() {
        ((TextView) findViewById(edu.cmu.pocketsphinx.demo.mobile.R.id.result_text)).setText("");
    }

    @Override
    protected void setErrorText(Exception error) {
        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    protected void setPostExecuteText(Exception result) {
        ((TextView) findViewById(R.id.caption_text)).setText("Failed to init recognizer " + result);
    }

    @Override
    protected void setSearchText(String caption) {
        ((TextView) findViewById(R.id.caption_text)).setText(caption);
    }
}
