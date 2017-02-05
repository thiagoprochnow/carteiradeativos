package br.com.carteira.util;

/**
 * Util class
 */
public class Util {

    /**
     * Method responsible for check if the input has two decimal at max
     * @param input - input number to be checked
     * @return true for valid input
     */
    public static boolean hasTwoDecimal(String input) {
        String[] splitInput = input.split("\\.");
        if (splitInput.length > 1) {
            if (splitInput[1].length() > 2) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method responsible for check if the input is in the range selected
     * @param min - minimal value of the range
     * @param max - maximal value of the range
     * @param input - input number to be checked
     * @return true for valid format
     */
    public static boolean isInRange(int min, int max, float input) {
        return max > min ? input >= min && input <= max : input >= max && input <= min;
    }
}
