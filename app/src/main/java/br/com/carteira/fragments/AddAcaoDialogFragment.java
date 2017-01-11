package br.com.carteira.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

import br.com.carteira.R;

/**
 * Dialog fragment to add a new or already owned stock in the portfolio.
 */
public class AddAcaoDialogFragment extends DialogFragment {

    public String username;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflator = getActivity().getLayoutInflater();
        // Set the layout for the Dialog Fragment
        builder.setView(inflator.inflate(R.layout.dialog_add_acao, null))
        // Add action buttons
        .setPositiveButton(R.string.buy_acao_button, new DialogInterface.OnClickListener() {
            @Override
            // Add the new stock in the porfolio or make a quantity sum for a already existing stock
            public void onClick(DialogInterface dialog, int id) {
                Dialog dialogView = getDialog();
                // Get the value inputed on the Dialog Fragment field
                EditText inputTicker = (EditText) dialogView.findViewById(R.id.inputTicker);
                EditText inputQuantity = (EditText) dialogView.findViewById(R.id.inputQuantity);
                EditText inputBuyPrice = (EditText) dialogView.findViewById(R.id.inputBuyPrice);
                EditText inputObjective = (EditText) dialogView.findViewById(R.id.inputObjective);

                // Put the values in a intent to send back to the calling fragment
                Intent intent = new Intent();
                intent.putExtra("inputTicker", inputTicker.getText().toString());
                intent.putExtra("inputQuantity", inputQuantity.getText().toString());
                intent.putExtra("inputBuyPrice", inputBuyPrice.getText().toString());
                intent.putExtra("inputObjective", inputObjective.getText().toString());
                // Send result back to fragment
                sendResult(0, intent);
            }
        })
                .setNegativeButton(R.string.cancel_acao_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        AddAcaoDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    // Sends the information back to the Fragment that called the Dialog and is expecting a result
    private void sendResult(int REQUEST_CODE, Intent intent) {
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), REQUEST_CODE, intent);
    }
}
