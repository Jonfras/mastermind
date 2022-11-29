package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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


        /**
         * Game Logic
         * creates a code and lets the user input his guesses and responds with rating
         */
        System.out.println("CODE:");
        code.forEach(System.out::println);

        findViewById(R.id.btnSubmit).setOnClickListener(view -> {
            if (isValidGuess(txtView.getText().toString())) {
                counter++;
                String rating = onSubmitClicked(txtView.getText().toString());
                System.out.println("RESPONSE:");
                System.out.println(rating);
                System.out.println("--------------------");
            } else {
                System.err.println("INVALID GUESS");
            }
        });




    }

    private String onSubmitClicked(String text) {
        StringBuilder rating = new StringBuilder();
        char tempHint;
        List<Character> guess = Arrays.stream(text.split(MESSAGE_SEPARATOR))
                .map(string -> string.charAt(0))
                .collect(Collectors.toList());

        System.out.println("GUESS:");
        guess.forEach(System.out::println);
        for (char element : guess) {
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

        //habe sie in Chars konvertiert da es mit Strings nicht funktioniert hat...
        List<Character> alphabet = mastermind.getAlphabet().stream()
                .map(string -> string.charAt(0))
                .collect(Collectors.toList());

        List<Character> guess = Arrays.stream(text.split(MESSAGE_SEPARATOR))
                .map(string -> string.charAt(0))
                .collect(Collectors.toList());


        for (char element : guess) {
            if (!alphabet.contains(element)){      //alphabet.contains(element) verstehe nicht warum der Vergleich mit Strings nicht funktioniert
                return false;
            }
            if (!mastermind.isDoubleAllowed() && guess.indexOf(element) != guess.lastIndexOf(element)) {
                return false;
            }
        }
        return true;
    }
}