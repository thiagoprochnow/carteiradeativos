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
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        } else if (symbol.equals("BTC")){
            return myArrayList.get(2);
        } else if (symbol.equals("LTC")){
            return myArrayList.get(3);
        } else if (symbol.equals("ETH")){
            return myArrayList.get(4);
        } else if (symbol.equals("BCH")){
            return myArrayList.get(5);
        } else {
            return myArrayList.get(6);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
