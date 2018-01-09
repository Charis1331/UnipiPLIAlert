package com.example.xoulis.xaris.unipiplialert;

import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangeContactsActivity extends AppCompatActivity implements TextWatcher {

    private boolean contactHasChanged = false;
    private boolean isContact1Valid;
    private boolean isContact2Valid;

    private EditText changeContact1EditText;
    private EditText changeContact2EditText;
    private Button saveChangesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_contacts);

        // Find the views
        changeContact1EditText = findViewById(R.id.changeContact1EditText);
        changeContact2EditText = findViewById(R.id.changeContact2EditText);
        saveChangesButton = findViewById(R.id.saveChangesButton);

        // Display the current contacts' tel number
        changeContact1EditText.setText(SettingsPreferences.getContact1(this));
        changeContact2EditText.setText(SettingsPreferences.getContact2(this));

        // Set touch listener to detect any changes
        changeContact1EditText.setOnTouchListener(touchListener);
        changeContact2EditText.setOnTouchListener(touchListener);

        // Add Listener to check input validity
        changeContact1EditText.addTextChangedListener(this);
        changeContact2EditText.addTextChangedListener(this);

        // Handle the button click
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String changedContact1 = changeContact1EditText.getText().toString();
                String changedContact2 = changeContact2EditText.getText().toString();

                SettingsPreferences.setContact1(ChangeContactsActivity.this, changedContact1);
                SettingsPreferences.setContact2(ChangeContactsActivity.this, changedContact2);

            }
        });
    }

    @Override
    public void onBackPressed() {
        // Check if no changes have been made
        if (!contactHasChanged) {
            super.onBackPressed();
            return;
        }

        // Create the appropriate listener, dialog
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                // Check if no changes have been made
                if (!contactHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Else, if changes have been made show a dialog
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(ChangeContactsActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            contactHasChanged = true;
            return false;
        }
    };

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() == 1) {
            changeContact1EditText.setSelection(1);
            changeContact2EditText.setSelection(1);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String temp;
        String newText;

        // Tel number check
        if (editable == changeContact1EditText.getEditableText()) {
            if (changeContact1EditText.getText().length() < 2) {
                changeContact1EditText.setError(getString(R.string.contact_without_country_code_error_message));
                isContact1Valid = false;
            } else {
                changeContact1EditText.setError(null);
                isContact1Valid = true;
            }

            if (!editable.toString().startsWith("+")) {
                temp = changeContact1EditText.getText().toString();
                newText = "+" + temp;
                changeContact1EditText.setText(newText);
            }
        } else if (editable == changeContact2EditText.getEditableText()) {
            if (changeContact2EditText.getText().length() < 2) {
                changeContact2EditText.setError(getString(R.string.contact_without_country_code_error_message));
                isContact2Valid = false;
            } else {
                changeContact2EditText.setError(null);
                isContact2Valid = true;
            }

            if (!editable.toString().startsWith("+")) {
                temp = changeContact2EditText.getText().toString();
                newText = "+" + temp;
                changeContact2EditText.setText(newText);
            }
        }

        enableButton();
    }

    private void enableButton() {
        if (isContact1Valid && isContact2Valid) {
            saveChangesButton.setEnabled(true);
        }
    }


}
