package br.com.carteira.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.com.carteira.R;

/**
 * Dialog fragment to add a new or already owned fii in the portfolio.
 */
public class AddFiiDialogFragment extends BaseDialogFragment {

    public String mUsername;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        // Set the layout for the Dialog Fragment
        builder.setView(inflator.inflate(R.layout.dialog_add_fii, null));
        // Add action buttons
        builder.setPositiveButton(R.string.buy_fii_button, new DialogInterface.OnClickListener() {
            @Override
            // Add the new fii in the porfolio or make a quantity sum for a already existing fii
            public void onClick(DialogInterface dialog, int id) {
                //Do nothing here because we override this button later to change the close
                // behaviour.
                //However, we still need this because on older versions of Android unless we
                //pass a handler the button doesn't get instantiated
            }
        });
        builder.setNegativeButton(R.string.cancel_fii_button, new DialogInterface.OnClickListener
                () {
            public void onClick(DialogInterface dialog, int id) {
                // Send the negative button event back to the host activity
                AddFiiDialogFragment.this.getDialog().cancel();
            }
        });
        final AlertDialog dialog = builder.create();

        dialog.show();
        // Implement the positive button here. If it is all correct, the Dialog will be dismissed
        // and the fii added.
        // If the field validation fails, the Dialog will not be dismissed and error will show to
        // fix fields inputs.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener
                () {
            @Override
            public void onClick(View v) {
                Dialog dialogView = getDialog();
                // Get the value inputed on the Dialog Fragment field
                EditText inputTicker = (EditText) dialogView.findViewById(R.id.inputTicker);
                EditText inputQuantity = (EditText) dialogView.findViewById(R.id.inputQuantity);
                EditText inputBuyPrice = (EditText) dialogView.findViewById(R.id.inputBuyPrice);
                EditText inputObjective = (EditText) dialogView.findViewById(R.id.inputObjective);
                EditText inputBuyDate = (EditText) dialogView.findViewById(R.id.inputBuyDate);

                // Validate for each inputted value
                Boolean validateTicker = validateFiiTicker(inputTicker);
                Boolean validateQuantity = validateNotEmpty(inputQuantity);
                Boolean validateBuyPrice = validateNotEmpty(inputBuyPrice);
                Boolean validateObjective = validateNotEmpty(inputObjective);
                Boolean validateBuyDate = validateBuyDate(inputBuyDate);

                // If validations are ok, dialog will be dismissed and fii added, otherwise error
                // will be shown to correct input fields.
                if (validateTicker && validateQuantity && validateBuyPrice && validateObjective
                        && validateBuyDate) {
                    // Put the values in a intent to send back to the calling fragment
                    Intent intent = new Intent();
                    intent.putExtra("inputTicker", inputTicker.getText().toString());
                    intent.putExtra("inputQuantity", inputQuantity.getText().toString());
                    intent.putExtra("inputBuyPrice", inputBuyPrice.getText().toString());
                    intent.putExtra("inputObjective", inputObjective.getText().toString());
                    intent.putExtra("inputDate", inputBuyDate.getText().toString());
                    // Send result back to fragment
                    sendResult(0, intent);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), R.string.wrong_inputs, Toast.LENGTH_LONG).show();
                }
            }
        });
        return dialog;
    }
}
