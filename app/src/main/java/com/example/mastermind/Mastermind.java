package com.example.mastermind;

import android.content.res.AssetManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Mastermind {
    private List<String> alphabet;
    private int codeLength;
    private boolean doubleAllowed;
    private int guessRounds;
    private String correctPositionSign;
    private String correctCodeElementSign;
    private List<String> log = new ArrayList<>();

    private AssetManager assetManager;
    private final String filename;

    private final String MESSAGE_SEPARATOR = " ";

    Predicate<String> notNull = s -> !Objects.equals(s, "");

    public Mastermind(AssetManager assetManager, String filename) {
        this.assetManager = assetManager;
        this.filename = filename;
        readAsset();

    }

    public void readAsset() {
        try {
            int i;
            Scanner s = new Scanner(getInputStreamForAsset(filename));
            for (i = 0; s.hasNext(); i++) {
                String line = s.nextLine().trim();
                System.out.println(line.length());
                String[] parts = line.split(MESSAGE_SEPARATOR);

                //remove all Null parts
                List<String> tempList = Arrays.stream(parts).collect(Collectors.toList());
                tempList = tempList.stream().filter(notNull).collect(Collectors.toList());
                parts = tempList.toArray(new String[tempList.size()]);

                //looking if there is no space after the '='
                if (!parts[1].trim().equals("=")) {
                    String temp = parts[1].substring(1);
                    tempList.set(1, "=");
                    tempList.add(2, temp);
                    parts = tempList.toArray(new String[tempList.size()]);
                }
                switch (parts[0]) {
                    case "alphabet":
                        try {
                            alphabet = Arrays.stream(parts)
                                    .skip(2)
                                    .map(n -> n.replace(",", MESSAGE_SEPARATOR))
                                    .collect(Collectors.toList());
                        } catch (Exception e) {
                            logReadingError("Alphabet");
                        }

                        break;

                    case "codeLength":
                        try {
                            codeLength = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            logReadingError("CodeLength");
                        }

                        break;

                    case "doubleAllowed":
                        if (parts[2].equals("true")) {
                            doubleAllowed = true;
                        } else if (parts[2].equals("false")) {
                            doubleAllowed = false;
                        } else {
                            logReadingError("DoubleAllowed");
                        }

                        break;

                    case "guessRounds":
                        try {
                            Arrays.stream(parts).forEach(System.out::println);
                            guessRounds = Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            logReadingError("GuessRounds");
                        }

                        break;

                    case "correctPositionSign":
                        try {
                            correctPositionSign = parts[2];
                        } catch (Exception e) {
                            logReadingError("CorrectPositionSign");
                        }

                        break;

                    case "correctCodeElementSign":
                        try {
                            correctCodeElementSign = parts[2];
                        } catch (Exception e) {
                            logReadingError("CorrectCodeElementSign");
                        }

                    default:
                }
            }
        } catch (
                Exception e) {
            System.out.println("!!File was not found");
        }
    }

    private InputStream getInputStreamForAsset(String filename) throws Exception {
        try {
            return assetManager.open(filename);
        } catch (IOException e) {
            System.out.println("!!Asset couldn't be accessed...");
            throw new Exception();
        }
    }

    private void logReadingError(String field) {
        String message = "!!" + System.currentTimeMillis() + " " + field + " was not entered correctly...";
        System.out.println(message);
        log.add(message);
    }

    public List<Character> createCode(){
        ArrayList<Character> code = new ArrayList<>();
        for (int i = 0; i < codeLength; i++) {
            int idx = (int) (Math.random() * alphabet.size());
            if (doubleAllowed){
                code.add(alphabet.get(idx).charAt(0));
            }
            else{
                if (!code.contains(alphabet.get(idx)))
                    code.add(alphabet.get(idx).charAt(0));
            }
        }
        return code;
    }

    public List<String> getAlphabet(){
        return alphabet;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public boolean isDoubleAllowed() {
        return doubleAllowed;
    }

    public char getCorrectPositionSign() {
        return correctPositionSign.charAt(0);
    }

    public char getCorrectCodeElementSign() {
        return correctCodeElementSign.charAt(0);
    }
}
