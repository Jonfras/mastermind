package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView txtView;

    List<Character> code;
    Mastermind mastermind;

    int counter;

    private final String MESSAGE_SEPARATOR = ",";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtView = findViewById(R.id.inputText);
        mastermind = new Mastermind(getAssets(), "config.txt");
        txtView.setHint("Guess " + mastermind.getCodeLength() + " of these: " + mastermind.getAlphabet().toString());
        code = mastermind.createCode();

        System.out.println("CODE:");;
        code.forEach(System.out::println);

        findViewById(R.id.btnSubmit).setOnClickListener(view -> {
            if (isValidGuess(txtView.getText().toString())) {
                counter++;
                String rating = onSubmitClicked();
                System.out.println(rating);
            } else {
                System.err.println("INVALID GUESS");
            }
        });


    }

    private String onSubmitClicked() {
        StringBuilder rating = new StringBuilder();
        char tempHint;
        List<String> guess = List.of(txtView.getText().toString().split(MESSAGE_SEPARATOR));
        System.out.println("GUESS:");
        guess.forEach(System.out::println);
        for (String element : guess) {
            tempHint = ' ';

            if (code.contains(element)) {
                tempHint = mastermind.getCorrectCodeElementSign();

                if (guess.indexOf(element) == code.indexOf(element)) {
                    tempHint = mastermind.getCorrectPositionSign();
                }
            }

            rating.append(tempHint);
        }
        if (rating.toString().isEmpty()) {
            return "incorrect";
        }
        return rating.toString();
    }

    private boolean isValidGuess(String text) {
        List<String> alphabet = mastermind.getAlphabet();
        List<String> guess = List.of(text.split(MESSAGE_SEPARATOR));

        //check for doubledElements
        if (mastermind.isDoubleAllowed()) {
            for (String element : guess) {
                if (guess.indexOf(element) != guess.lastIndexOf(element)) {
                    return false;
                }
            }
        }

        if (alphabet.containsAll(guess) && guess.size() == mastermind.getCodeLength()) {
            return true;
        } else return false;
    }
}