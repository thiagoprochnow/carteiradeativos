package br.com.carteira.util;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Input Filter responsible for check if there is only two decimal numbers and if the number is
 * in the range selected.
 */
public class InputFilterDecimal implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
                               int dend) {
        // Check if the insert was made before the dot
        if(dstart <= dest.toString().indexOf(".")){
            return null;
        }

        // Check if there are two decimal at max. In case of failure, invalidate the input
        if (Util.hasTwoDecimal(dest.toString() + source.toString())) {
            return null;
        } else {
            return "";
        }
    }
}