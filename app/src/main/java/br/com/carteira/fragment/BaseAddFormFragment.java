package br.com.carteira.fragment;


import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.EditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseAddFormFragment extends Fragment {
    // Function that validate the inputted value on the stock ticker field
    protected Boolean validateStockTicker(EditText ticker) {
        // To Do by REGEX
        if (ticker.getText().toString().length() > 0)
            return true;
        else
            return false;
    }

    // Function that validate the inputted value on the fii ticker field
    protected Boolean validateFiiTicker(EditText ticker) {
        // To Do by REGEX
        if (ticker.getText().toString().length() > 0)
            return true;
        else
            return false;
    }

    // Function that validate the field is not empty on confirm button pressed
    protected Boolean validateNotEmpty(EditText text) {
        if (text.getText().toString().length() > 0)
            return true;
        else
            return false;
    }

    // Function that validate the date field if the value was inputted correctly
    protected Boolean validateBuyDate(EditText buyDate) {
        // To Do by REGEX
        if (buyDate.getText().toString().length() > 0)
            return true;
        else
            return false;
    }

    // Sends the information back to the Fragment that called the Dialog and is expecting a result
    protected void sendResult(int REQUEST_CODE, Intent intent) {
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), REQUEST_CODE, intent);
    }
}
