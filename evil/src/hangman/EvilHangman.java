/*package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class EvilHangman {

    public static void main(String[] args) {

        EvilHangmanGame game = new EvilHangmanGame();

        try {
            File dictionary = new File(args[0]);
            int wordLength = Integer.parseInt(args[1]);
            game.startGame(dictionary, wordLength);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (EmptyDictionaryException ex) {
            System.out.println(ex);
            System.exit(1);
        }
 
        Scanner scanner = new Scanner(System.in);
        int numGuesses = Integer.parseInt(args[2]);

        while (numGuesses > 0) {
            try {
                System.out.println("You have " + numGuesses + " guesses left");

                System.out.print("Used letters:");
                for (Character letter : game.getGuessedLetters()) {
                    System.out.print(" " + letter);
                }
                System.out.println();

                System.out.println("Word: " + game.getWordPattern());

                System.out.print("Enter guess: ");

                String guess = scanner.next();

                if (guess.isBlank()) {
                    throw new IllegalArgumentException("You must enter a letter as a guess");
                }
                if (guess.length() > 1) {
                    throw new IllegalArgumentException("Guess cannot be longer than 1 character.");
                }
                if (!Character.isLetter(guess.charAt(0))) {
                    throw new IllegalArgumentException(guess.charAt(0) + " is not valid input. Please guess a letter.");
                }
                game.makeGuess(guess.charAt(0));

                int remainingGuesses = game.getNumLetterOccurrences();
                if (remainingGuesses == 0) {
                    System.out.print("Sorry, there are no " + guess.charAt(0) + "'s");
                    numGuesses--;
                } else {
                    System.out.print("Yes, there are " + remainingGuesses + " " + guess);
                }
            } catch (GuessAlreadyMadeException | IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }
            System.out.println("\n");

            if (!game.getWordPattern().contains("_")) {
                numGuesses = 0;
            }
        }
        scanner.close();

        if (game.getWordPattern().contains("_")) {
            System.out.println("You lose!");
        } else {
            System.out.println("You win!");
        }
        System.out.print("The word was: " + game.getFirstWordInSet());
    }
}*/
package hangman;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class EvilHangman {
    public static void main(String[] args) {
        EvilHangmanGame game = new EvilHangmanGame();
        initializeGame(args, game);
        playGame(args, game);
        displayGameResult(game);
    }

    private static void initializeGame(String[] args, EvilHangmanGame game) {
        try {
            File dictionary = new File(args[0]);
            int wordLength = Integer.parseInt(args[1]);
            game.startGame(dictionary, wordLength);
        } catch (IOException | EmptyDictionaryException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    private static void playGame(String[] args, EvilHangmanGame game) {
        Scanner scanner = new Scanner(System.in);
        int numGuesses = Integer.parseInt(args[2]);

        while (numGuesses > 0) {
            displayGameStatus(game, numGuesses);

            char guessedLetter = getGuessedLetter(scanner);
            try {
                game.makeGuess(guessedLetter);
                numGuesses = updateGuesses(game, numGuesses, guessedLetter);
            } catch (GuessAlreadyMadeException ex) {
                System.out.println(ex.getMessage());
            }

            System.out.println("\n");
            if (!game.getWordPattern().contains("_")) {
                numGuesses = 0;
            }
        }
        scanner.close();
    }

    private static void displayGameStatus(EvilHangmanGame game, int numGuesses) {
        System.out.println("You have " + numGuesses + " guesses left");
        System.out.print("Used letters:");
        for (Character letter : game.getGuessedLetters()) {
            System.out.print(" " + letter);
        }
        System.out.println();
        System.out.println("Word: " + game.getWordPattern());
    }

    private static char getGuessedLetter(Scanner scanner) {
        System.out.print("Enter guess: ");
        String guess = scanner.next();
        validateGuess(guess);
        return guess.charAt(0);
    }

    private static void validateGuess(String guess) {
        if (guess.isBlank()) {
            throw new IllegalArgumentException("You must enter a letter as a guess");
        }
        if (guess.length() > 1) {
            throw new IllegalArgumentException("Guess cannot be longer than 1 character.");
        }
        if (!Character.isLetter(guess.charAt(0))) {
            throw new IllegalArgumentException(guess.charAt(0) + " is not valid input. Please guess a letter.");
        }
    }

    private static int updateGuesses(EvilHangmanGame game, int numGuesses, char guessedLetter) {
        int remainingGuesses = game.getNumLetterOccurrences();
        if (remainingGuesses == 0) {
            System.out.print("Sorry, there are no " + guessedLetter + "'s");
            numGuesses--;
        } else {
            System.out.print("Yes, there are " + remainingGuesses + " " + guessedLetter);
        }
        return numGuesses;
    }

    private static void displayGameResult(EvilHangmanGame game) {
        if (game.getWordPattern().contains("_")) {
            System.out.println("You lose!");
        } else {
            System.out.println("You win!");
        }
        System.out.println("The word was: " + game.getFirstWordInSet());
    }
}
