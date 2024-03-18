import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.lang.Math;

public class CMP {
    int count = 0, lastLetterIndex = -1;
    private String words[];
    private Letter letters[];
    private boolean domain[] = { true, true, true, true, true, true, true, true, true, true };
    private Scanner input = new Scanner(System.in);
    private boolean letterRepating = false;

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

    private int setupPuzzle() {
        System.out.println("\n-----------------------------------------------------------\n");
        System.out.println("Please write your equation in this form: a * b * .. * n = z");
        System.out.println("Or write -1 to exit the program.");
        System.out.print("Equation: ");
        String equation = input.nextLine().toUpperCase().replaceAll(" ", "");
        if (equation.equalsIgnoreCase("-1"))
            return -1;
        String leftHandSide[] = equation.split("=")[0].split("\\*");
        String rightHandSide = equation.split("=")[1];

        words = new String[leftHandSide.length + 1];

        for (int i = 0; i < leftHandSide.length; i++) {
            words[i] = leftHandSide[i];
        }

        words[words.length - 1] = rightHandSide;

        String allLetters = "";
        for (int i = 0; i < words.length; i++) {
            allLetters += words[i];
        }

        letters = new Letter[countUniqueLetters(allLetters)];
        if (letters.length > 10) {
            System.out.println("\n--------------------------------------------------------------------------");
            System.out.println("|  This puzzle is impossible to solve with more than 10 unique letters,  |");
            System.out.println("|  since there are only 10 digits available (0-9).                       |");
            System.out.println("/-------------------------------------------------------------------------\n");
            return 1;
        }

        for (int i = 0; i < words.length; i++) {
            for (int j = 0; j < words[i].length(); j++) {
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

        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                char c = word.charAt(i);
                for (Letter letter : letters) {
                    if (letter.getCharacter() == c) {
                        letter.incrementPriority();
                        break;
                    }
                }
            }
        }

        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                for (Letter letter : letters) {
                    if (letter.getCharacter() == word.charAt(0)) {
                        letter.incrementPriority();
                        break;
                    }
                }
            }
        }

        // Sort the letters array based on frequency or priority
        Arrays.sort(letters, new Comparator<Letter>() {
            @Override
            public int compare(Letter l1, Letter l2) {
                // Replace getPriority() with getFrequency() if sorting by frequency
                return l2.getPriority() - l1.getPriority(); // For descending order
            }
        });

        for (int i = 0; i < letters.length; i++) {
            if (letters[i].getCharacter() == words[words.length - 1].charAt(words[words.length - 1].length() - 1)) {
                lastLetterIndex = i;
                break;
            }
        }

        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].charAt(words[i].length() - 1) == words[words.length - 1]
                    .charAt(words[words.length - 1].length() - 1)) {
                letterRepating = true;
                break;

            }
        }

        for (Letter letter : letters) {
            for (int i = letter.isLeading() ? 1 : 0; i <= 9; i++) {
                letter.setDigit(i);
                for (int j = 0; j < letters.length; j++) {
                    if (letters[j] != letter)
                        letters[j].setDigit(9);
                    if (leftHandSideValue() >= Math.pow(10, words[words.length - 1].length() - 1))
                        letter.setDomainAt(i, true);
                    else
                        letter.setDomainAt(i, false);
                }
            }

            for (int i = 0; i < letters.length; i++) {
                letters[i].resetDigit();
            }
        }

        System.out.println();
        printEquationLetters();
        System.out.println();
        return 0;
    }

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
        /*
         * System.out.print("    ");
         * for (int i = 0; i < multiplier.length(); i++) {
         * for (int j = 0; j < letters.length; j++) {
         * if (letters[j].getCharacter() == multiplier.charAt(i)) {
         * System.out.print(letters[j].getDigit());
         * break;
         * }
         * }
         * }
         * System.out.println();
         * 
         * System.out.print("x   ");
         * for (int i = 0; i < multiplicand.length(); i++) {
         * for (int j = 0; j < letters.length; j++) {
         * if (letters[j].getCharacter() == multiplicand.charAt(i)) {
         * System.out.print(letters[j].getDigit());
         * break;
         * }
         * }
         * }
         * System.out.println();
         * 
         * System.out.println("------------");
         * System.out.print("   ");
         * for (int i = 0; i < product.length(); i++) {
         * for (int j = 0; j < letters.length; j++) {
         * if (letters[j].getCharacter() == product.charAt(i)) {
         * System.out.print(letters[j].getDigit());
         * break;
         * }
         * }
         * }
         */
    }

    private boolean assignableDigit(int digit) {
        for (int i = 0; i < letters.length; i++) {
            if (letters[i].getDigit() == digit)
                return false;
        }
        return true;
    }

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
