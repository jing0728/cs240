package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {
  Set<String> possibleWords;
  SortedSet<Character> guessedLetters;
  String currGamePattern;
  int letterOccurrences;


  public EvilHangmanGame() {
    possibleWords = null;
    guessedLetters = null;
    currGamePattern = null;
    letterOccurrences = 0;
  }

  @Override
  public void startGame(File dictionary, int wordLength) throws IOException, EmptyDictionaryException {
    if (dictionary.length() == 0) {
      throw new EmptyDictionaryException("The file " + dictionary.getName() + " is empty");
    }

    if (possibleWords != null) {
      possibleWords.clear();
    }
    if (guessedLetters != null) {
      guessedLetters.clear();
    }

    possibleWords = new HashSet<>();
    guessedLetters = new TreeSet<>();
    currGamePattern = "_".repeat(wordLength);

    Scanner scanner = new Scanner(dictionary);
    while (scanner.hasNext()) {
      possibleWords.add(scanner.next());
    }
    scanner.close();

    possibleWords.removeIf(word -> word.length() != wordLength);

    if (possibleWords.isEmpty()) {
      throw new EmptyDictionaryException("There are no words of length " + wordLength);
    }
  }

  @Override
  public Set<String> makeGuess(char guess) throws GuessAlreadyMadeException {
    checkAndAddGuess(guess);

    Map<String, Set<String>> partitions = new HashMap<>();
    int wordLength = getWordLength();
    int maxSize = 0;
    int minOccurrences = wordLength;

    for (String word : possibleWords) {

      int currOccurrences = 0;
      StringBuilder pattern = new StringBuilder();

      for (int i = 0; i < word.length(); i++) {
        if (word.charAt(i) == guess) {
          pattern.append(guess);
          currOccurrences++;
        } else {
          pattern.append(currGamePattern.charAt(i));
        }
      }
      String key = pattern.toString();

      if (!partitions.containsKey(key)) {
        Set<String> keySet = new HashSet<>();
        keySet.add(word);
        partitions.put(key, keySet);
      } else {
        partitions.get(key).add(word);
      }


      if (partitions.get(key).size() > maxSize) {
        maxSize = partitions.get(key).size();
      }
      if (currOccurrences < minOccurrences) {
        minOccurrences = currOccurrences;
      }
    }

    prioritizeLargestSet(partitions, maxSize);

    if (partitions.size() > 1) {
      for (String pattern : partitions.keySet()) {
        if (!pattern.contains(Character.toString(guess))) {
          letterOccurrences = 0;
          possibleWords = partitions.get(pattern);
          return partitions.get(pattern);
        }
      }

      prioritizeFewestOccurrences(partitions, minOccurrences, guess);

      if (partitions.size() > 1) {
        prioritizeRightMost(partitions, wordLength - 1, guess);
      }
    }
    
    String patternFinal = "";
    int occurrences = 0;
    for (String pattern : partitions.keySet()) {
      for (int i = 0; i < pattern.length(); i++) {
        if (pattern.charAt(i) == guess) {
          occurrences++;
        }
      }
      patternFinal = pattern;
    }

    currGamePattern = patternFinal;
    letterOccurrences = occurrences;
    possibleWords = partitions.get(patternFinal);
    return partitions.get(patternFinal);
  }

  private void checkAndAddGuess(char guess) throws GuessAlreadyMadeException {
    guess = Character.toLowerCase(guess);
    if (guessedLetters.contains(guess)) {
      throw new GuessAlreadyMadeException(guess + " has already been guessed.");
    } else {
      guessedLetters.add(guess);
    }
  }

  private void prioritizeLargestSet(Map<String, Set<String>> partitions, int maxSize) {
    partitions.values().removeIf(set -> set.size() < maxSize);
  }

  private void prioritizeFewestOccurrences(Map<String, Set<String>> partitions, int minOccurrences, char guess) {
    for (Iterator<Map.Entry<String, Set<String>>> itr = partitions.entrySet().iterator(); itr.hasNext();) {
      int occurrences = 0;
      String pattern = itr.next().getKey();
      for (int i = 0; i < pattern.length(); i++) {
        if (pattern.charAt(i) == guess) {
          occurrences++;
        }
      }
      if (occurrences > minOccurrences) {
        itr.remove();
      }
    }
  }

  private void prioritizeRightMost(Map<String, Set<String>> partitions, int startIndex, char guess) {
    int ind = 0;

    for (String pattern : partitions.keySet()) {
      int i = startIndex;
      while (i > ind) {
        if (pattern.charAt(i) == guess) {
          ind = i;
        }
        i--;
      }
    }

    int rightmostInd = ind;
    partitions.keySet().removeIf(key -> key.charAt(rightmostInd) != guess);

    if (partitions.size() > 1) {
      prioritizeRightMost(partitions, rightmostInd - 1, guess);
    }
  }

  private int getWordLength() {
    return currGamePattern.length();
  }

  public String getWordPattern() {
    return currGamePattern;
  }

  public int getNumLetterOccurrences() {
    return letterOccurrences;
  }

  public String getFirstWordInSet() {
    String firstWordInSet = null;
    for (String word : possibleWords) {
      firstWordInSet = word;
      break;
    }
    return firstWordInSet;
  }

  @Override
  public SortedSet<Character> getGuessedLetters() {
    return guessedLetters;
  }
}