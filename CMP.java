
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.lang.Math;

/**
 * The {@code CMP} class implements a Cryptarithmetic Puzzle Solver.
 * Cryptarithmetic puzzles are mathematical puzzles in which the digits are
 * replaced by letters of the alphabet
 * or other symbols. The task is to find the original digits. This solver
 * focuses on puzzles involving multiplication.
 * 
 * This class handles the entire process of solving the puzzle, from accepting
 * input equations from the user,
 * setting up the puzzle constraints, to attempting to solve the puzzle and
 * displaying the solution.
 * 
 * @version 0.0.1
 * @author Ahmed Alonazi
 */
public class CMP {

    /** Counter for the number of backtracks during the puzzle solving process. */
    int count = 0;
    /**
     * Index of the letter that appears as the last character in the product of the
     * equation.
     */
    int lastLetterIndex = -1;
    /** Array to store the unique words (operands and the result) of the puzzle. */
    private String words[];
    /**
     * Array of Letter objects representing the unique letters found in the puzzle
     * and their associated constraints.
     */
    private Letter letters[];
    /**
     * Domain array representing the available digits (0-9) for assignment to
     * letters. True indicates available.
     */
    private boolean domain[] = { true, true, true, true, true, true, true, true, true, true };
    /** Scanner object for reading the user's input. */
    private Scanner input = new Scanner(System.in);
    /**
     * Flag to indicate if any letter is repeating in the last position of operands
     * and the result.
     */
    private boolean letterRepating = false;

    /**
     * Starts the puzzle solver interaction with the user. It welcomes users and
     * explains the puzzle rules.
     * Continuously prompts the user for puzzles to solve until the user decides to
     * exit by entering -1.
     */
    public void start() {
        System.out.println("-------------------------------------------------");
        System.out.println("| Welcome to the Cryptarithmetic Puzzle Solver! |");
        System.out.println("/------------------------------------------------\n");
        System.out.println(
                "In this puzzle, each letter stands for a unique digit. Your task is to assign digits to the letters\nso that the given arithmetic operation holds true."
                        + "For a multiplication puzzle, you will input two\noperands and a product resulting from their multiplication.");

        int n = 0;
        while (true) {
            n = setupPuzzle();
            if (n == -1)
                break;
            else if (n == 0) {
                System.out.print("\nAttempting to solve the puzzle... Please wait.");
                long startTime = System.nanoTime();
                if (solvePuzzle(0)) {
                    long endTime = System.nanoTime();
                    long duration = endTime - startTime;
                    double durationInSeconds = duration / 1_000_000_000.0;
                    System.out.println(" (" + durationInSeconds + " sec) (" + count + " backtrack)\n");

                    System.out.println("------------------------------------------------------");
                    System.out.println("|  Puzzle solved successfully! Here's the solution:  |");
                    System.out.println("/-----------------------------------------------------");

                    printEquationNumbers();

                    System.out.println();
                    System.out
                            .println(
                                    "\n---------------------------------------------------------------------------------");
                    System.out.println(
                            "|  The table for the letters with their assigned digits based on the solution:  |");
                    System.out.println(
                            "/--------------------------------------------------------------------------------");
                    printTable();

                    words = null;
                    letters = null;
                    lastLetterIndex = -1;
                    for (int i = 0; i < domain.length; i++) {
                        domain[i] = true;
                    }
                    count = 0;
                    continue;
                }
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                double durationInSeconds = duration / 1_000_000_000.0;
                System.out.println(" (" + durationInSeconds + " sec) (" + count + " backtrack)\n");
                System.out.println("----------------------------------------------------------");
                System.out.println("|  Unable to solve the puzzle with the provided inputs.  |");
                System.out.println("/---------------------------------------------------------\n");
            }
            count = 0;
            words = null;
            letters = null;
            lastLetterIndex = -1;
            for (int i = 0; i < domain.length; i++) {
                domain[i] = true;
            }
        }
    }

    /**
     * Sets up the cryptarithmetic puzzle by parsing the user's input equation and
     * preparing the environment for the solver.
     * This method prompts the user to input an equation following the specific
     * format (a * b * .. * n = z) representing
     * the multiplication cryptarithmetic puzzle. It handles input parsing,
     * validation, and preparation for solving.
     * 
     * The setup process includes:
     * - Reading and sanitizing the user's input equation.
     * - Splitting the equation into its left and right components.
     * - Identifying unique letters that need digit assignments.
     * - Checking the feasibility of solving the puzzle (i.e., not more than 10
     * unique letters).
     * - Initializing {@code Letter} objects for each unique letter with initial
     * constraints (leading letter constraints).
     * - Prioritizing letters based on their positions to optimize the solving
     * process.
     * - Sorting letters based on their priority to further optimize solving.
     * 
     * Additionally, the method prepares a domain for each letter indicating
     * possible digit assignments and checks for the
     * special case of repeating letters in specific positions of the operands and
     * result to apply further optimizations.
     * 
     * @return int This method returns -1 if the user wishes to exit, 1 if the
     *         puzzle is impossible to solve due to the
     *         number of unique letters exceeding the available digits (0-9), or 0
     *         if the setup is successful and the puzzle is
     *         ready to be solved.
     */
    private int setupPuzzle() {
        // Print instructions for the user on how to enter the equation and provide an
        // option to exit.
        System.out.println("\n-----------------------------------------------------------\n");
        System.out.println("Please write your equation in this form: a * b * .. * n = z");
        System.out.println("Or write -1 to exit the program.");
        System.out.print("Equation: ");

        // Read the user's input, convert it to uppercase and remove spaces for
        // standardization.
        String equation = input.nextLine().toUpperCase().replaceAll(" ", "");

        // Check if the user requested to exit the program.
        if (equation.equalsIgnoreCase("-1"))
            return -1;

        // Split the equation into left-hand side (LHS) operands and right-hand side
        // (RHS) result.
        String leftHandSide[] = equation.split("=")[0].split("\\*");
        String rightHandSide = equation.split("=")[1];

        // Initialize the words array to hold all parts of the equation, including
        // operands and the result.
        words = new String[leftHandSide.length + 1];
        for (int i = 0; i < leftHandSide.length; i++) {
            words[i] = leftHandSide[i];
        }

        words[words.length - 1] = rightHandSide;

        // Concatenate all words to form a string containing all letters used in the
        // puzzle.
        String allLetters = "";
        for (int i = 0; i < words.length; i++) {
            allLetters += words[i];
        }

        // Create an array of Letter objects to represent each unique letter found in
        // the puzzle.
        letters = new Letter[countUniqueLetters(allLetters)];

        // If there are more than 10 unique letters, the puzzle is unsolvable with
        // digits 0-9.
        if (letters.length > 10) {
            System.out.println("\n--------------------------------------------------------------------------");
            System.out.println("|  This puzzle is impossible to solve with more than 10 unique letters,  |");
            System.out.println("|  since there are only 10 digits available (0-9).                       |");
            System.out.println("/-------------------------------------------------------------------------\n");
            return 1;
        }

        // Iterate over each word and letter in the equation to initialize Letter
        // objects with constraints.
        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < words[i].length(); j++) {
                // Logic for identifying unique letters and setting their leading status.
                for (int k = 0; k < letters.length; k++) {
                    if ((letters[k] != null) && (letters[k].getCharacter() == words[i].charAt(j))) {
                        break;
                    }
                    if (letters[k] == null) {
                        if ((j == 0 && words[i].length() > 1)
                                || (words[i].length() == 1 && words[words.length - 1].length() > 1))
                            letters[k] = new Letter(words[i].charAt(j), true);
                        else
                            letters[k] = new Letter(words[i].charAt(j), false);
                        break;
                    }
                }
            }
        }

        // Increment priority for letters based on their position and usage in the
        // equation to optimize solving.
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                for (Letter letter : letters) {
                    if (letter.getCharacter() == word.charAt(i)) {
                        // Increase the priority of letters that appear more frequently or in
                        // significant positions.
                        letter.incrementPriority();
                        break;
                    }
                }
            }
        }

        // Increment priority for letters based on their position and usage in the
        // equation to optimize solving.
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                for (Letter letter : letters) {
                    // Increase the priority of letters that appear more frequently or in
                    // significant positions.
                    if (letter.getCharacter() == word.charAt(0)) {
                        letter.incrementPriority();
                        break;
                    }
                }
            }
        }

        // Sort the letters array based on the priority of letters to optimize the
        // solving order.
        Arrays.sort(letters, new Comparator<Letter>() {
            @Override
            public int compare(Letter l1, Letter l2) {
                return l2.getPriority() - l1.getPriority(); // Descending order sort by priority.
            }
        });

        // searching for the last letter index that appear in the opreand in the
        // "letters" array.
        for (int i = 0; i < letters.length; i++) {
            if (letters[i].getCharacter() == words[words.length - 1].charAt(words[words.length - 1].length() - 1)) {
                lastLetterIndex = i;
                break;
            }
        }

        // Determine if the last letter of the result repeats in any operand to apply
        // optimization.
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].charAt(words[i].length() - 1) == words[words.length - 1]
                    .charAt(words[words.length - 1].length() - 1)) {
                letterRepating = true;
                break;

            }
        }

        // Set up domain constraints for each letter based on preliminary calculations. Too see if the current assignment will lead to a dead end.
        for (Letter letter : letters) {
            for (int i = letter.isLeading() ? 1 : 0; i <= 9; i++) {
                letter.setDigit(i);
                for (int j = 0; j < letters.length; j++) {
                    if (letters[j] != letter)
                        letters[j].setDigit(9);

                }

                if (leftHandSideValue() >= Math.pow(10, words[words.length - 1].length() - 1))
                    letter.setDomainAt(i, true);
                else if (leftHandSideValue() == 0 && Math.pow(10, words[words.length - 1].length() - 1) == 1)
                    letter.setDomainAt(i, true);
                else
                    letter.setDomainAt(i, false);
            }

            for (int i = 0; i < letters.length; i++) {
                letters[i].resetDigit();
            }

        }
    }

    /**
     * Attempts to solve the cryptarithmetic puzzle using a recursive backtracking
     * algorithm.
     * Assigns digits to letters while adhering to the constraints and checks if a
     * valid solution is found.
     * 
     * @param index The current index in the letters array being processed.
     * @return {@code true} if the puzzle is solved successfully; {@code false}
     *         otherwise.
     */
    private boolean solvePuzzle(int index) {

        if (index == letters.length) {
            return checkSolution();
        }

        if (!letterRepating && index == lastLetterIndex)
            return solvePuzzle(index + 1);

        for (int digit = letters[index].isLeading() ? 1 : 0; digit <= 9; digit++) {

            if (domain[digit] && letters[index].domainAt(digit)) {

                letters[index].setDigit(digit);
                domain[digit] = false;
                if (solvePuzzle(index + 1))
                    return true;
                count++;
                letters[index].resetDigit();
                domain[digit] = true;
            }

        }

        return false;
    }

    /**
     * Checks if the current assignment of digits to letters satisfies the puzzle
     * equation.
     * 
     * @return {@code true} if the current assignment solves the puzzle;
     *         {@code false} otherwise.
     */
    private boolean checkSolution() {
        int leftSide = 0, rightSide = 0, currentValue = 0, lastDigitValue = 1;

        if (!letterRepating) {
            for (int i = 0; i < words.length - 1; i++) {
                for (int j = 0; j < letters.length; j++) {
                    if (letters[j].getCharacter() == words[i].charAt(words[i].length() - 1)) {
                        lastDigitValue *= letters[j].getDigit();
                        break;
                    }
                }
            }

            if (lastDigitValue >= 0 && domain[lastDigitValue % 10])
                letters[lastLetterIndex].setDigit(lastDigitValue % 10);
            else
                return false;
            if (letters[lastLetterIndex].isLeading() && letters[lastLetterIndex].getDigit() == 0)
                return false;
        }
        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < words[i].length(); j++) {
                for (int k = 0; k < letters.length; k++) {
                    if (letters[k].getCharacter() == words[i].charAt(j)) {
                        currentValue += letters[k].getDigit() * Math.pow(10, words[i].length() - j - 1);
                        break;
                    }
                }
            }
            if (i == 0 && leftSide == 0 && currentValue > 0) {
                leftSide = currentValue;
            } else if (i < words.length - 1) {
                leftSide *= currentValue;
            } else if (i == words.length - 1) {
                rightSide = currentValue;
            }
            currentValue = 0;
        }
        return leftSide == rightSide;
    }

    /**
     * Calculates the value of the left-hand side of the equation based on the
     * current digit assignments.
     * 
     * @return The numerical value of the left-hand side of the equation.
     */
    private int leftHandSideValue() {
        int leftSide = 0, rightSide = 0, currentValue = 0, lastDigitValue = 1;

        for (int i = 0; i < words.length - 1; i++) {
            for (int j = 0; j < words[i].length(); j++) {
                for (int k = 0; k < letters.length; k++) {
                    if (letters[k].getCharacter() == words[i].charAt(j)) {
                        currentValue += letters[k].getDigit() * Math.pow(10, words[i].length() - j - 1);
                        break;
                    }
                }
            }
            if (i == 0 && leftSide == 0 && currentValue > 0) {
                leftSide = currentValue;
            } else if (i < words.length - 1) {
                leftSide *= currentValue;
            } else if (i == words.length - 1) {
                rightSide = currentValue;
            }
            currentValue = 0;
        }
        return leftSide;
    }

    /**
     * Counts the unique letters in a given string.
     * 
     * @param string The string to analyze.
     * @return The count of unique letters in the string.
     */
    private int countUniqueLetters(String string) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            for (int j = 0; j <= i; j++) {
                if (i == 0) {
                    count++;
                    break;
                }
                if (string.charAt(i) == string.charAt(j) && j != i)
                    break;
                else if (string.charAt(i) == string.charAt(j) && j == i)
                    count++;
            }
        }
        return count;
    }

    /**
     * Prints the equation with letters instead of digits to give an overview of the
     * puzzle's structure.
     */
    private void printEquationLetters() {
        for (int i = 0; i < words.length - 1; i++) {
            if (i < words.length - 2) {
                System.out.println("    " + words[i]);
            } else {
                System.out.println("x   " + words[i]);
            }
        }
        System.out.println("------------");
        System.out.println("   " + words[words.length - 1]);
    }

    /**
     * Prints the equation with the assigned digits, showing a possible solution to
     * the puzzle.
     */
    private void printEquationNumbers() {
        for (int i = 0; i < words.length - 1; i++) {
            if (i < words.length - 2) {
                System.out.print("    ");
            } else {
                System.out.print("x   ");
            }
            for (int j = 0; j < words[i].length(); j++) {
                for (int k = 0; k < letters.length; k++) {
                    if (letters[k].getCharacter() == words[i].charAt(j)) {
                        System.out.print(letters[k].getDigit());
                        break;
                    }
                }
            }
            System.out.println();

        }
        System.out.println("------------");
        System.out.print("   ");
        for (int i = 0; i < words[words.length - 1].length(); i++) {
            for (int j = 0; j < letters.length; j++) {
                if (letters[j].getCharacter() == words[words.length - 1].charAt(i)) {
                    System.out.print(letters[j].getDigit());
                    break;
                }
            }
        }
        System.out.println();
    }

    /**
     * Prints a table showing each letter and its assigned digit in the solution.
     */
    private void printTable() {
        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                for (int j = 0; j < letters.length * 6; j++) {
                    System.out.print("-");
                }
                System.out.println("-");
            }

            else if (i == 1) {
                System.out.print("|");
                for (int j = 0; j < letters.length - 1; j++) {
                    System.out.print("  " + letters[j].getCharacter() + "  |");
                }
                System.out.println("  " + letters[letters.length - 1].getCharacter() + "  |");
            }

            else if (i == 2) {
                for (int j = 0; j < letters.length * 6; j++) {
                    System.out.print("-");
                }
                System.out.println("-");
            }

            else if (i == 3) {
                System.out.print("|");
                for (int j = 0; j < letters.length - 1; j++) {
                    System.out.print("  " + letters[j].getDigit() + "  |");
                }
                System.out.println("  " + letters[letters.length - 1].getDigit() + "  |");
            }

            else if (i == 4) {
                for (int j = 0; j < letters.length * 6; j++) {
                    System.out.print("-");
                }
                System.out.println("-");
            }
        }
    }
}
