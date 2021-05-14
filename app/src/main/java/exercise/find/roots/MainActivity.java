package exercise.find.roots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity {


    private BroadcastReceiver broadcastReceiverForSuccess = null;
    private BroadcastReceiver broadcastReceiverForFailure = null;

    private ProgressBar progressBar;
    private EditText editTextUserInput;
    private Button buttonCalculateRoots;


    static class MainActivityState implements Serializable {
        String editTextVal;
        boolean isEditTextEnabled;
        boolean isButtonEnabled;
        int progressVisibility;

        public MainActivityState(String editTextVal, boolean isEditTextEnabled, boolean isButtonEnabled, int progressVisibility) {
            this.editTextVal = editTextVal;
            this.isEditTextEnabled = isEditTextEnabled;
            this.isButtonEnabled = isButtonEnabled;
            this.progressVisibility = progressVisibility;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        editTextUserInput = findViewById(R.id.editTextInputNumber);
        buttonCalculateRoots = findViewById(R.id.buttonCalculateRoots);

        // set initial UI:
        resetUI();

        // set listener on the input written by the keyboard to the edit-text
        editTextUserInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                // text did change
                String newText = editTextUserInput.getText().toString();
                buttonCalculateRoots.setEnabled(MainActivity.isNumeric(newText));
            }
        });

        // set click-listener to the button
        buttonCalculateRoots.setOnClickListener(v -> {
            Intent intentToOpenService = new Intent(MainActivity.this, CalculateRootsService.class);
            String userInputString = editTextUserInput.getText().toString();
            // check that `userInputString` is a number. handle bad input. convert `userInputString` to long
            long userInputLong;
            if (MainActivity.isNumeric(userInputString)) {
                userInputLong = Long.parseLong(userInputString);
            } else {
                buttonCalculateRoots.setEnabled(false);
                return;
            }
            intentToOpenService.putExtra("number_for_service", userInputLong);
            startService(intentToOpenService);
            buttonCalculateRoots.setEnabled(false);
            editTextUserInput.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        });


        // register a broadcast-receiver to handle action "found_roots"
        broadcastReceiverForSuccess = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent incomingIntent) {
                if (incomingIntent == null || !incomingIntent.getAction().equals(CalculateRootsService.ACTION_SUCCESS_ROOTS))
                    return;
                // success finding roots!
                incomingIntent.setClass(MainActivity.this, SuccessActivity.class);
                resetUI();
                startActivity(incomingIntent);


            }
        };
        registerReceiver(broadcastReceiverForSuccess, new IntentFilter(CalculateRootsService.ACTION_SUCCESS_ROOTS));

        broadcastReceiverForFailure = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || !intent.getAction().equals(CalculateRootsService.ACTION_FAILURE_ROOTS)) {
                    return;
                }
                // failure finding roots
                Toast.makeText(MainActivity.this, "Aborted after 20 sec", Toast.LENGTH_SHORT).show();
                resetUI();
            }
        };
        registerReceiver(broadcastReceiverForFailure, new IntentFilter(CalculateRootsService.ACTION_FAILURE_ROOTS));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverForSuccess);
        unregisterReceiver(broadcastReceiverForFailure);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        MainActivityState mainActivityState = new MainActivityState(
                editTextUserInput.getText().toString(),
                editTextUserInput.isEnabled(),
                buttonCalculateRoots.isEnabled(),
                progressBar.getVisibility()
        );

        outState.putSerializable("main_activity_state", mainActivityState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MainActivityState mainActivityState = (MainActivityState) savedInstanceState.getSerializable("main_activity_state");
        this.editTextUserInput.setText(mainActivityState.editTextVal);
        this.editTextUserInput.setEnabled(mainActivityState.isEditTextEnabled);
        this.buttonCalculateRoots.setEnabled(mainActivityState.isButtonEnabled);
        this.progressBar.setVisibility(mainActivityState.progressVisibility);
    }

    private void resetUI() {
        progressBar.setVisibility(View.GONE); // hide progress
        editTextUserInput.setText(""); // cleanup text in edit-text
        editTextUserInput.setEnabled(true); // set edit-text as enabled (user can input text)
        buttonCalculateRoots.setEnabled(false); // set button as disabled (user can't click)
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

}

