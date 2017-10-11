package br.com.guiainvestimento.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import br.com.guiainvestimento.R;
import br.com.guiainvestimento.utils.FileUtils;

import static android.content.Context.DOWNLOAD_SERVICE;

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

    // Transform from currency symbol to currency label USD -> Dolar
    public static String convertCurrencySymbol(Context context, String symbol){
        List<String> myArrayList = Arrays.asList(context.getResources().getStringArray(R.array.currency_array));
        if (symbol.equals("USD")){
            return myArrayList.get(0);
        } else if (symbol.equals("EUR")){
            return myArrayList.get(1);
        } else{
            return myArrayList.get(2);
        }
    }
}
