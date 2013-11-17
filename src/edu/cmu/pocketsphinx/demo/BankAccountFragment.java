package edu.cmu.pocketsphinx.demo;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.cmu.pocketsphinx.Hypothesis;

public class BankAccountFragment extends ShowcaseFragment {

    private static final String BALANCE = "balance";
    private static final String RESULT = "result";

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
        String number = "";
        for (int i = 1; i < words.length; ++i)
            number += DIGITS.get(words[i]);

        return Float.parseFloat(number);
    }

    private TextView resultText;

    private float balance;
    private ToggleButton toggleButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState
                && savedInstanceState.containsKey(BALANCE))
            balance = savedInstanceState.getFloat(BALANCE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bank_account, container, false);
        toggleButton = (ToggleButton) v.findViewById(R.id.start_button);
        resultText = (TextView) v.findViewById(R.id.result_text);

        if (null != savedInstanceState)
            resultText.setText(savedInstanceState.getCharSequence(RESULT));
        else
            setBalance(balance);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        toggleButton.setChecked(false);
        toggleButton.setOnCheckedChangeListener(this);
        // TODO: switch to grammar
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(BALANCE, balance);
        outState.putCharSequence(RESULT, resultText.getText());
    }

    private void setBalance(float balance) {
        this.balance = balance;
        resultText.setText(context.getString(R.string.balance_fmt, balance));
    }

    private void deposit(float amount) {
        setBalance(balance + amount);
        notify(R.string.deposit_fmt, amount);
    }

    private void withdraw(float amount) {
        setBalance(balance - amount);
        notify(R.string.withdraw_fmt, amount);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        resultText.setText(hypothesis.getHypstr());
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        String command = hypothesis.getHypstr();

        if (command.endsWith("balance"))
            notify(R.string.balance_fmt, balance);
        else if (command.startsWith("deposit"))
            deposit(parseAmount(command));
        else if (command.startsWith("withdraw"))
            withdraw(parseAmount(command));
    }

    private void notify(int resId, Object... args) {
        String text = context.getString(resId, args);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}