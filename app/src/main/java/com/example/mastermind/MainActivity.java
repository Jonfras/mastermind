package com.example.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    TextView txtView;

    List<Character> code;
    Mastermind mastermind;

    int counter;
    int MAX_ROUNDS;
    ListView board;
    ArrayAdapter<String> mAdapter;
    List<String> guessList = new ArrayList<>();
    Button submit;

    private final String MESSAGE_SEPARATOR = ",";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submit = findViewById(R.id.btnSubmit);


        mAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, guessList);
        board = findViewById(R.id.listView);
        board.setAdapter(mAdapter);
        txtView = findViewById(R.id.inputText);

        mastermind = new Mastermind(getAssets(), "config.txt");
        MAX_ROUNDS = mastermind.getGuessRounds();
        txtView.setHint("Guess " + mastermind.getCodeLength() + " of these: " + mastermind.getAlphabet().toString());
        code = mastermind.createCode();


        /**
         * Game Logic
         * creates a code and lets the user input his guesses and responds with rating
         */
        System.out.println("CODE:");
        code.forEach(System.out::println);

        findViewById(R.id.btnSubmit).setOnClickListener(view -> {
            onSubmitListener();
            txtView.setText("");
        });


        /**
         *  SettingsClickListener
         *  stops current game and shows settings
         */
        findViewById(R.id.btnSettings).setOnClickListener(view -> {
            onSettingsClicked();
        });
    }

    private void onSubmitListener() {
        mAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, guessList);
        board.setAdapter(mAdapter);
        if (isValidGuess(txtView.getText().toString())) {
            counter++;

            String rating = onSubmitClicked(txtView.getText().toString());
            guessList.add(txtView.getText() + " | " + rating);
            mAdapter.notifyDataSetChanged();
            System.out.println("--------------------");
            System.out.println("RESPONSE:");
            System.out.println(rating);
            if (rating.equals("++++")) {
                Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_LONG).show();
            } else {
                if (MAX_ROUNDS - counter == 0) {
                    Toast.makeText(getApplicationContext(), "You lost!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You have " + (MAX_ROUNDS - counter) + " rounds left", Toast.LENGTH_LONG).show();
                }
            }
            System.out.println("--------------------");
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Guess!", Toast.LENGTH_LONG).show();
            System.err.println("INVALID GUESS");
        }
    }



    private String onSubmitClicked(String text) {
        StringBuilder rating = new StringBuilder();
        char tempHint;

        char[] chars = text.toCharArray();
        List<Character> guess = convertArrayToList(chars);

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
            if (tempHint != ' ') {
                rating.append(tempHint);
            }
        }
        if (rating.toString().isEmpty()) {
            return "";
        }
        return rating.toString();
    }


    private void onSettingsClicked() {
        submit.setClickable(false);
        guessList.clear();
        txtView.setText("");
        txtView.setClickable(false);

        List<String> settingsList = mastermind.getSettings();
        settingsList.add("START NEW GAME");
        mAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, settingsList);
        board.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        board.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos == settingsList.size() - 1) {
                    mAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, guessList);
                    board.setAdapter(mAdapter);
                    submit.setClickable(true);
                    txtView.setClickable(true);
                }
            }
        });

    }


    private boolean isValidGuess(String text) {
        //habe sie in Chars konvertiert da es mit Strings nicht funktioniert hat...
        List<Character> alphabet = mastermind.getAlphabet().stream()
                .map(string -> string.charAt(0))
                .collect(Collectors.toList());

        char[] chars = text.toCharArray();
        List<Character> guess = convertArrayToList(chars);

        //Arrays.stream(text.split(MESSAGE_SEPARATOR))
        //                .map(string -> string.charAt(0))
        //                .collect(Collectors.toList());
        if (guess.size() != 4) {
            return false;
        }
        for (char element : guess) {
            if (!alphabet.contains(element)) {      //alphabet.contains(element) verstehe nicht warum der Vergleich mit Strings nicht funktioniert
                return false;
            }
            if (!mastermind.isDoubleAllowed() && guess.indexOf(element) != guess.lastIndexOf(element)) {
                return false;
            }
        }
        return true;
    }

    private List<Character> convertArrayToList(char[] chars) {
        List<Character> charList = new ArrayList<>();
        for (char x :
                chars) {
            charList.add(x);
        }
        return charList;
    }


}