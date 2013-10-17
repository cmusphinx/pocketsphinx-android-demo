package edu.cmu.pocketsphinx.demo;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class BankAccountFragment extends RecognitionFragment {

    private final static Map<String, String> DIGITS = new HashMap<String, String>();

    static {
        DIGITS.put("point", ".");
        DIGITS.put("oh", "0");
        DIGITS.put("zero", "0");
        DIGITS.put("one", "1");
        DIGITS.put("two", "2");
        DIGITS.put("three", "3");
        DIGITS.put("four", "4");
        DIGITS.put("five", "5");
        DIGITS.put("six", "6");
        DIGITS.put("seven", "7");
        DIGITS.put("eight", "8");
        DIGITS.put("nine", "9");
    }

    private static float parseAmount(String command) {
        String[] words = command.split("\\s");
        Log.d("TEST", words.toString());
        String number = "";
        for (int i = 1; i < words.length; ++i)
            number += DIGITS.get(words[i]);

        return Float.parseFloat(number);
    }

    private TextView resultText;

    private float balance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState
                && savedInstanceState.containsKey("balance"))
            balance = savedInstanceState.getFloat("balance");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bank_account, container, false);

        resultText = (TextView) v.findViewById(R.id.result_text);
        setBalance(balance);

        ToggleButton button = (ToggleButton) v.findViewById(R.id.start_button);
        button.setOnCheckedChangeListener(this);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("balance", balance);
    }

    private void setBalance(float balance) {
        this.balance = balance;
        resultText.setText(context.getString(R.string.balance_fmt, balance));
    }

    private void deposit(float amount) {
        setBalance(balance + amount);
        Toast.makeText(context,
                       context.getString(R.string.deposit_fmt, amount),
                       Toast.LENGTH_SHORT).show();
    }

    private void withdraw(float amount) {
        setBalance(balance - amount);
        Toast.makeText(context,
                       context.getString(R.string.withdraw_fmt, amount),
                       Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
    }

    @Override
    public void onError(int error) {
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        resultText.setText(partialResults.getString("hypothesis"));
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
    }

    @Override
    public void onResults(Bundle bundle) {
        if (!bundle.containsKey("hypothesis"))
            return;

        String command = bundle.getString("hypothesis");

        if (command.endsWith("balance"))
            Toast.makeText(context,
                           context.getString(R.string.balance_fmt, balance),
                           Toast.LENGTH_SHORT).show();
        else if (command.startsWith("deposit"))
            deposit(parseAmount(command));
        else if (command.startsWith("withdraw"))
            withdraw(parseAmount(command));
    }

    @Override
    public void onRmsChanged(float rmsdB) {
    }
}
