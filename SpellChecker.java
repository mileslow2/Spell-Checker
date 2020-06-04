import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;

public class SpellChecker {

    // Originally I had a <String, String> hashtable but I had to 
    // use the redundant MisspelledWord class for the project
    Hashtable<String, MisspelledWord> misspellingDictionary;
    HashSet<String> dictionary;
    String dictionaryFileName, misspellingsFileName;

    public static void main(String[] args) {
        new SpellChecker(args[0], args[1], 60000).spellCheck(args[2]);
    }

    public SpellChecker(String dictionaryFileName, String misspellingsFileName, int tableSize) {
        dictionary = new HashSet<>(tableSize);
        misspellingDictionary = new Hashtable<>(tableSize * 10);
        this.dictionaryFileName = dictionaryFileName;
        this.misspellingsFileName = misspellingsFileName;
        populateDictionary(dictionaryFileName);
        populateMisspellings(misspellingsFileName);
    }

    /*
     * Loads all of the misspelled words into the misspellingDictionary
     * 
     * @param the name of the misspelled dictionary file
     */
    private void populateMisspellings(String misspellingsFileName) {
        File misspellingsFile = new File(misspellingsFileName);
        try {
            Scanner scanner = new Scanner(misspellingsFile);
            while (scanner.hasNextLine()) {
                String[] words = scanner.nextLine().split("->");
                MisspelledWord m = new MisspelledWord(words[0], words[1]);
                misspellingDictionary.put(words[0], m);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * Loads all of the misspelled words into the dictionary of words
     * 
     * @param the name of the dictionary file
     */
    private void populateDictionary(String dictionaryFileName) {
        File dictionaryFile = new File(dictionaryFileName);
        try {
            Scanner scanner = new Scanner(dictionaryFile);
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();
                dictionary.add(word.toLowerCase());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*
     * Iterates through every word in a given file and explains t
     * 
     * @param the name of file for the document to be spellchecked
     */
    public void spellCheck(String documentFileName) {
        File documentFile = new File(documentFileName);
        try {
            Scanner scanner = new Scanner(documentFile);
            while (scanner.hasNext()) {
                // removes all of the weird stuff in the word (not my finest code)
                String word = scanner.next().replaceAll(" ", "").replace("'", "").replace(".", "").replace(",", "")
                        .replace("/", "").replace(";", "").replace(":", "").replace("“", "").replace("”", "")
                        .replace("?", "").replace("(", "").replace(")", "").replace("!", "").replace("{", " ")
                        .replace("}", " ").toLowerCase();
                if (word.length() != 0 && !dictionaryContains(word))
                    if (misspellingDictionary.containsKey(word))
                        System.out.println("Misspelled word:\t" + word + "\tDid you mean?:\t"
                                + misspellingDictionary.get(word).getCorrectWord());
                    else
                        handleUknownWord(word);
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Asks user for help with words that aren't located in the dictionaries
     * 
     * @param word program doesn't recognize
     */
    private void handleUknownWord(String word) {
        System.out.println("We're sorry, we could not identify this word:" + word
                + "\nIs this word spelled correctly? Please input Y/N below.");
        Scanner scanner = new Scanner(System.in);
        if (scanner.nextLine().toLowerCase().equals("y"))
            updateDictionary(word);
        else {
            System.out.println("please input what you beleive the correct spelling to be.");
            updateMisspellings(word, scanner.nextLine());
        }
        System.out.println("Thanks to you, we have expanded our dictionary!");
        // scanner.close();
    }

    /*
     * the following update methods have code within this stackoverflow post
     * https://stackoverflow.com/questions/1625234/how-to-append-text-to-an-existing
     * -file-in-java
     */

    /*
     * appends the correct spelling of a misspelled word to the misspelled
     * dictionary
     * 
     * @param word the misspelled word
     * 
     * @param newWord the correctly spelled word
     */
    private void updateMisspellings(String word, String newWord) {
        updateDictionary(newWord);
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(misspellingsFileName), true)));
            out.println(word + "->" + newWord);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * appends a word to the dictionary
     * 
     * @param word the correctly spelled word to be added
     */
    private void updateDictionary(String word) {
        dictionary.add(word);
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(dictionaryFileName), true)));
            out.println(word);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * finds root word of given word and check it against the dictionary
     * 
     * @param word the word to be checked
     */
    private boolean dictionaryContains(String word) {
        if (dictionary.contains(word))
            return true;
        int l = word.length();
        String tempWord;

        if (l > 3) {
            String last3Chars = word.substring(l - 3, l);
            tempWord = word.substring(0, l - 3);
            if (last3Chars.equals("ing")) {
                if (dictionary.contains(tempWord) || dictionary.contains(tempWord + "e"))
                    return true;
            }
            if (last3Chars.equals("ies"))
                if (dictionary.contains(tempWord) || dictionary.contains(tempWord + "y"))
                    return true;
        } else if (l > 2) {
            String last2Chars = word.substring(l - 2, l);
            if (last2Chars.equals("es") || last2Chars.equals("ed")) {
                tempWord = word.substring(0, l - 1);
                if (dictionary.contains(tempWord) || dictionary.contains(tempWord.substring(0, l - 1)))
                    return true;
            }
            if (last2Chars.equals("ly"))
                if (dictionary.contains(word.substring(l - 2, l)))
                    return true;
        } else if (l > 1 && word.substring(l - 1, l).equals("s")) {
            tempWord = word.substring(l - 1, l);
            if (dictionary.contains(tempWord))
                return true;
        }

        return false;
    }

    private class MisspelledWord {

        String misspelledWord, correctWord;

        public MisspelledWord(String misspelledWord, String correctWord) {
            this.misspelledWord = misspelledWord;
            this.correctWord = correctWord;
        }

        @Override
        public boolean equals(Object obj) {
            return misspelledWord.equals(obj);
        }

        @Override
        public int hashCode() {
            return misspelledWord.hashCode();
        }

        public String getCorrectWord() {
            return correctWord;
        }

    }
}