package br.com.carteira.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import br.com.carteira.R;

public abstract class BaseAddFormFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enables the menu
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Common menu for add forms
        inflater.inflate(R.menu.add_form_menu, menu);
    }

    // Validate if an EditText field is empty
    protected boolean isEditTextEmpty(EditText text) {
        Editable editable = text.getText();
        if (editable != null && TextUtils.isEmpty(editable.toString())) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was inputed with valid Stock Symbol
    protected boolean isValidStockSymbol(EditText symbol) {
        Editable editable = symbol.getText();
        // Regex Pattern for Stock (EX: PETR4) or ETF (EX: BOVA11 or SMAL11)
        Pattern pattern = Pattern.compile("^[A-Z]{4}([0-9]|[0-9][0-9])$");
        if (editable != null && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was inputed with valid int
    protected boolean isValidInt(EditText symbol) {
        Editable editable = symbol.getText();
        // Check if it is digit only
        boolean isDigitOnly = TextUtils.isDigitsOnly(editable.toString());
        if (editable != null && isDigitOnly && !isEditTextEmpty(symbol)) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was inputed with valid double value
    protected boolean isValidDouble(EditText symbol) {
        Editable editable = symbol.getText();
        // Check if it is double input
        Pattern pattern = Pattern.compile("^[0-9]+\\.?[0-9]*$");
        if (editable != null && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was inputed with valid double value
    protected boolean isValidPercent(EditText symbol) {
        Editable editable = symbol.getText();
        // Check if it is percent input
        Pattern pattern = Pattern.compile("^[0-9]+\\.?[0-9]*$");
        // Check inputted value is lower or equal to 100%, no sense being bigger
        boolean isPercent = (Double.parseDouble(editable.toString())<=100);
        if (editable != null && pattern.matcher(editable.toString()).matches() && isPercent) {
            return true;
        } else {
            return false;
        }
    }

    // Validate if an EditText was inputed with valid date value
    protected boolean isValidDate(EditText symbol) {
        Editable editable = symbol.getText();
        // Check if it is date input
        Pattern pattern = Pattern.compile("^[0-9][0-9]\\/[0-9][0-9]\\/[0-9][0-9][0-9][0-9]$");
        if (editable != null && pattern.matcher(editable.toString()).matches()) {
            return true;
        } else {
            return false;
        }
    }

    // Sets DatePicker and return the OnClickListener
    public View.OnClickListener setDatePicker(final EditText inputDateView){
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the inputBuyDate field
                final Calendar mCalendar = Calendar.getInstance();
                final String mDateFormat = "dd/MM/yyyy";
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mDateFormat, Locale.getDefault());
                // To show current date or inputted date on datepicker
                if(inputDateView.getText().length() > 0){
                    String mDate = inputDateView.getText().toString();
                    try {
                        Date inputtedDate = simpleDateFormat.parse(mDate);
                        // Sets the current date to the previously inputted date
                        mCalendar.setTime(inputtedDate);
                    } catch (ParseException e){
                        e.printStackTrace();
                    }
                }

                int mYear = mCalendar.get(Calendar.YEAR);
                int mMonth = mCalendar.get(Calendar.MONTH);
                int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    // When the date is selected and clicked "OK" on the Spinner
                    public void onDateSet(DatePicker datepicker, int year, int month, int day) {
                        // Sets the date on the EditText field value
                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month);
                        mCalendar.set(Calendar.DAY_OF_MONTH, day);
                        inputDateView.setText(simpleDateFormat.format(mCalendar.getTime()));
                    }
                },mYear, mMonth, mDay);
                mDatePicker.show();
            }
        };
        return onClickListener;
    }
}
