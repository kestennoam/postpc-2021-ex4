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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class SuccessActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        // check if extras is null;
        if (extras == null){
            return;
        }

        // set data members
        TextView elapsedTimeView = findViewById(R.id.elapsedTime);
        TextView originalNumberView = findViewById(R.id.originalNumber);
        TextView resultView = findViewById(R.id.result);

        // set time and original number
        originalNumberView.setText(String.format(getString(R.string.original_number), extras.getLong("original_number")));
        elapsedTimeView.setText(String.format(getString(R.string.text_elapsed_time), extras.getLong("elapsed_time")));


        // check if prime
        if (extras.getBoolean("is_prime")){
            resultView.setText(R.string.text_is_prime);
        }
        else{
            resultView.setText(String.format(getString(R.string.text_result), extras.getLong("root1"), extras.getLong("root2")));
        }
    }


}



