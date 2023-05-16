package spell;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;


public class SpellCorrector implements ISpellCorrector {


  private final ITrie dictionary;
  private final Set<String> editOneDistanceWords;
  private final Set<String> editTwoDistanceWords;
  private final TreeSet<String> suggestionWords;
  private int highestFrequency;


  public SpellCorrector() {
    dictionary = new Trie();
    editOneDistanceWords = new HashSet<>();
    editTwoDistanceWords = new HashSet<>();
    suggestionWords = new TreeSet<>();
    highestFrequency = 0;
  }


  @Override
  public void useDictionary(String dictionaryFileName) throws IOException {
    File file = new File(dictionaryFileName);
    Scanner scanner = new Scanner(file);


    while (scanner.hasNext()) {
      dictionary.add(scanner.next());
    }


    scanner.close();
  }


  @Override
  public String suggestSimilarWord(String inputWord) {
    inputWord = inputWord.toLowerCase();
    if (dictionary.find(inputWord) != null) {
      clearSuggestions();
      return inputWord;
    }
    calculateDistances(inputWord, editOneDistanceWords);
    if (suggestionWords.isEmpty()) {
      for (String word : editOneDistanceWords) {
        calculateDistances(word, editTwoDistanceWords);

      }
    }
    if (suggestionWords.isEmpty()) {
      clearSuggestions();
      return null;
    } else {
      String word = suggestionWords.first();
      clearSuggestions();
      return word;
    }
  }


  private void calculateDistances(String word, Set<String> editedWords) {
    for (int i = 0; i < word.length(); i++) {
      // Deletion Distance
      StringBuilder tmp = new StringBuilder(word);
      String deletionWord = tmp.deleteCharAt(i).toString();
      editedWords.add(deletionWord);
      calcHighestFreq(deletionWord);

      // Transposition Distance
      if (i < word.length() - 1) {
        tmp = new StringBuilder(word);
        StringBuilder transposedChars = new StringBuilder(word.substring(i, i + 2)).reverse();
        tmp.replace(i, i + 2, transposedChars.toString());
        String transposedWord = tmp.toString();
        editedWords.add(transposedWord);
        calcHighestFreq(transposedWord);
      }

      // Alteration Distance
      for (char letter = 'a'; letter <= 'z'; letter++) {
        if (letter != word.charAt(i)) {
          char[] tmpChars = word.toCharArray();
          tmpChars[i] = letter;
          String alteredWord = new String(tmpChars);
          editedWords.add(alteredWord);
          calcHighestFreq(alteredWord);
        }
      }
    }

    // Insertion Distance
    for (int i = 0; i <= word.length(); i++) {
      for (char letter = 'a'; letter <= 'z'; letter++) {
        String insertedWord = word.substring(0, i) + letter + word.substring(i);
        editedWords.add(insertedWord);
        calcHighestFreq(insertedWord);
      }
    }
  }


  private void clearSuggestions() {
    suggestionWords.clear();
    highestFrequency = 0;
  }


  private void calcHighestFreq(String word) {
    INode found = dictionary.find(word);
    if (found != null) {
      if (found.getValue() > highestFrequency) {
        suggestionWords.clear();
        highestFrequency = found.getValue();
        suggestionWords.add(word);
      } else if (found.getValue() == highestFrequency) {
        suggestionWords.add(word);
      }
    }
  }


}
