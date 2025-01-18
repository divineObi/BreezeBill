package io.kamzy.breezebill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Passcode extends AppCompatActivity {
    private StringBuilder passcode = new StringBuilder();
    private View[] dots;
    Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passcode);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ctx = this;

        // Initialize dots
        dots = new View[]{
                findViewById(R.id.dot1),
                findViewById(R.id.dot2),
                findViewById(R.id.dot3),
                findViewById(R.id.dot4)
        };

        // Set up keypad buttons
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7,
                R.id.btn8, R.id.btn9
        };

        View.OnClickListener numberClickListener = v -> {
            if (passcode.length() < 4) {
                passcode.append(((Button) v).getText().toString());
                updateDots();
            }
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(numberClickListener);
        }

        // Backspace button
        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            if (passcode.length() > 0) {
                passcode.deleteCharAt(passcode.length() - 1);
                updateDots();
            }
        });
    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundResource(i < passcode.length() ? R.drawable.dot_filled : R.drawable.dot_empty);
        }

        if (passcode.length() == 4) {
            validatePasscode();
        }
    }

    private void validatePasscode() {
        // Add passcode validation logic here
        String enteredPasscode = passcode.toString();
        // Example: Validate with a hardcoded passcode "1234"
        if (enteredPasscode.equals("1234")) {
            Toast.makeText(ctx, "Passcode is correct", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ctx, Dashboard.class);
            startActivity(intent);
            // Success: Proceed to the next screen
        } else {
            // Failure: Reset and show error
            passcode.setLength(0);
            updateDots();
        }
    }
}