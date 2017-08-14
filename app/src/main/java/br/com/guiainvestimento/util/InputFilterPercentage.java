package br.com.guiainvestimento.util;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Input Filter responsible for check if there is only two decimal numbers and if the number is
 * in the range selected.
 */
public class InputFilterPercentage implements InputFilter {

    private int min, max;

    /**
     * Constructor for Integer parameters
     * @param min - minimal value of the range
     * @param max - maximal value of the range
     */
    public InputFilterPercentage(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Constructor for String parameters
     * @param min - minimal value of the range
     * @param max - maximal value of the range
     */
    public InputFilterPercentage(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
                               int dend) {
        try {

            // Parse the input according to where it character was inserted
            String input = dest.toString().substring(0,dstart) + source.toString();
            if(dend < dest.length()){
                 input += dest.toString().substring(dstart, dest.toString().length() -1);
            }

            // Check if the insert was made before the dot and if it is inside the range
            if(dstart <= dest.toString().indexOf(".") && Util.isInRange(min, max, Float.parseFloat(input))){
                return null;
            }

            // Check if the input is in the range and if there are two decimal at max. In case of
            // failure, invalidate the input
            if (Util.isInRange(min, max, Float.parseFloat(input)) && Util.hasTwoDecimal(dest.toString() + source.toString()))
                return null;
        } catch (NumberFormatException nfe) {
        }
        return "";
    }
}