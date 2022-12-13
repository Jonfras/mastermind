package com.example.mastermind;

import static java.lang.System.in;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
    String XMLSpliterator = ",";
    final String FILENAME = "SaveState.xml";


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
            if (counter < MAX_ROUNDS) {
                onSubmitClicked();
            } else {
                Toast.makeText(getApplicationContext(), "Start a new Game via the SHOW SETTINGS button", Toast.LENGTH_SHORT).show();
            }
        });


        /**
         *  SettingsClickListener
         *  stops current game and shows settings
         */
        findViewById(R.id.btnSettings).setOnClickListener(view -> {
            onSettingsClicked();
        });

        /**
         * SaveClickListener
         * Saves the game State if it is not empty or finished.
         */
        findViewById(R.id.btnSave).setOnClickListener(view -> {
            if (counter <= 0 || counter >= 12) {
                Toast.makeText(getApplicationContext(), "Wasn't able to Save! \nGame is either empty or lost", Toast.LENGTH_SHORT).show();
            } else {
                onSaveClicked();
            }
        });

        /**
         * LoadClickListener
         * Loads a previous game state that has been saved.
         */
        findViewById(R.id.btnLoad).setOnClickListener(view -> {
            counter = mastermind.getGuessRounds();
            guessList = onLoadClicked();
            mAdapter.notifyDataSetChanged();
        });
    }


    private void onSubmitClicked() {
        mAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, guessList);
        board.setAdapter(mAdapter);
        if (isValidGuess(txtView.getText().toString())) {
            counter++;

            String rating = getRating(txtView.getText().toString());
            guessList.add(txtView.getText() + " | " + rating);
            mAdapter.notifyDataSetChanged();
            System.out.println("--------------------");
            System.out.println("RESPONSE:");
            System.out.println(rating);
            if (rating.equals("++++")) {
                Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_SHORT).show();
            } else {
                if (counter == MAX_ROUNDS) {
                    AtomicReference<String> temp = new AtomicReference<>("");
                    code.forEach(s -> temp.updateAndGet(v -> v + s));
                    Toast.makeText(getApplicationContext(), "You lost!\nThe Code was: " + temp, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You have " + (MAX_ROUNDS - counter) + " rounds left", Toast.LENGTH_SHORT).show();
                }
            }
            System.out.println("--------------------");
        } else {
            Toast.makeText(getApplicationContext(), "Invalid Guess!", Toast.LENGTH_SHORT).show();
            System.err.println("INVALID GUESS");
        }
    }


    private String getRating(String text) {
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
                    counter = 0;
                    code = mastermind.createCode();
                    System.out.println("CODE:");
                    for (char x : code) {
                        System.out.println(x);
                    }
                }
            }
        });
    }

    private void onSaveClicked() {
        System.out.println("SAVING...");
        try {
            saveGamestateToXML(openFileOutput(FILENAME, MODE_PRIVATE), guessList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

    private List<String> onLoadClicked() {
        List<String> gameCourse = new ArrayList<>();
        try {
            XmlPullParserFactory xmlPull = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlPull.newPullParser();
            xmlPullParser.setInput(
                    new StringReader(
                            new BufferedReader(
                                    new InputStreamReader(openFileInput(FILENAME))).readLine()));

            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xmlPullParser.getName().equals("code")) {
                        xmlPullParser.next();
                        String codeString = xmlPullParser.getText();
                        for (int i = 0; i < codeString.length(); i++) {

                            if (mastermind.getAlphabet().contains(codeString.charAt(i))) {
                                code.set(i, codeString.charAt(i));
                            }

                        }
                        System.out.println("found code: " + codeString);
                    } else if (xmlPullParser.getName().contains("guess")) {
                        counter--;
                        xmlPullParser.next();
                        xmlPullParser.next();
                        StringBuilder guess = new StringBuilder();
                        for (String s : xmlPullParser.getText().split(XMLSpliterator)) {
                            guess.append(s);
                            guess.append(" ");
                        }
                        xmlPullParser.next();
                        xmlPullParser.next();
                        xmlPullParser.next();
                        StringBuilder rating = new StringBuilder();
                        for (String s : xmlPullParser.getText().split(XMLSpliterator)) {
                            rating.append(s);
                            rating.append(" ");
                        }
                        gameCourse.add(guess + " | " + rating);
                    }
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return gameCourse;
    }


    public void saveGamestateToXML(FileOutputStream out, List<String> guesses) {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startTag("", "saveState");
            serializer.startTag("", "code");
            serializer.text(code.toString());
            serializer.endTag("", "code");
            for (int i = 0; i < guesses.size(); i++) {
                String[] guessesValueSplitted = guesses.get(i).split("\\|");
                serializer.startTag("", "guess" + (i + 1));
                serializer.startTag("", "userInput");
                StringBuilder guessInXMLFormat = new StringBuilder();
                for (int j = 0; j < guessesValueSplitted[0].length(); j++) {
                    char character = guessesValueSplitted[0].charAt(j);
                    if (character != ' ')
                        guessInXMLFormat.append(character).append(XMLSpliterator);
                }
                serializer.text(guessInXMLFormat.toString());
                serializer.endTag("", "userInput");
                serializer.startTag("", "result");
                StringBuilder ratingInXMLFormat = new StringBuilder();
                for (int j = 0; j < guessesValueSplitted[1].length(); j++) {
                    char character = guessesValueSplitted[1].charAt(j);
                    if (character != ' ') {
                        ratingInXMLFormat.append(character);
                        ratingInXMLFormat.append(XMLSpliterator);
                    }

                }
                serializer.text(ratingInXMLFormat.toString());
                serializer.endTag("", "result");
                serializer.endTag("", "guess" + (i + 1));
            }
            serializer.endTag("", "saveState");
            serializer.endDocument();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(out), true);
            pw.println(writer);
            System.out.println("saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidGuess(String text) {

        List<Character> alphabet = mastermind.getAlphabet().stream()
                .map(string -> string.charAt(0))
                .collect(Collectors.toList());

        char[] chars = text.toCharArray();
        List<Character> guess = convertArrayToList(chars);

        if (guess.size() != 4) {
            return false;
        }
        for (char element : guess) {
            if (!alphabet.contains(element)) {
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