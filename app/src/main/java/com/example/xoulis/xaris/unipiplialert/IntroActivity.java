package com.example.xoulis.xaris.unipiplialert;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.paolorotolo.appintro.AppIntro;

public class IntroActivity extends AppIntro implements TextWatcher {

    private TextInputLayout usernameTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private TextInputLayout contact1TextInputLayout;
    private TextInputLayout contact2TextInputLayout;

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText contact1EditText;
    private TextInputEditText contact2EditText;
    private Button saveButton;

    private boolean usernameIsValid = false;
    private boolean passIsValid = false;
    private boolean contact1IsValid = false;
    private boolean contact2IsValid = false;

    private Boolean addDefaultTextToContacts = true;

    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(SampleSlide.newInstance(R.layout.slide1));
        addSlide(SampleSlide.newInstance(R.layout.slide2));
        addSlide(SampleSlide.newInstance(R.layout.slide3));

        showSkipButton(false);
        setProgressButtonEnabled(false);
        setSeparatorColor(Color.TRANSPARENT);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    @Override
    protected void onPageSelected(int position) {
        super.onPageSelected(position);

        if (position == 2) {
            // Initialise the views
            initViews();

            // Set listeners to all the views
            addTextChangeListeners();

            // Add default text to Contact fields, ONLY the FIRST time this class is called
            if (addDefaultTextToContacts) {
                // Set default text for contacts editTexts
                setDefaultTextForContactsFields();
                addDefaultTextToContacts = false;
            }
        }

    }

    private void initViews() {
        usernameTextInputLayout = findViewById(R.id.textInputLayoutUserName);
        passwordTextInputLayout = findViewById(R.id.textInputLayoutPassword);
        contact1TextInputLayout = findViewById(R.id.textInputLayoutContact1);
        contact2TextInputLayout = findViewById(R.id.textInputLayoutContact2);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        contact1EditText = findViewById(R.id.contact1EditText);
        contact2EditText = findViewById(R.id.contact2EditText);
        saveButton = findViewById(R.id.saveButton);
    }

    private void addTextChangeListeners() {
        usernameEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        contact1EditText.addTextChangedListener(this);
        contact2EditText.addTextChangedListener(this);
    }

    private void setDefaultTextForContactsFields() {
        String defaultText = "+" + GetCountryCodeNumber.getCallCode(this);
        contact1EditText.setText(defaultText);
        contact2EditText.setText(defaultText);

        // Move the cursor
        contact1EditText.setSelection(2);
        contact2EditText.setSelection(2);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() == 1) {
            contact1EditText.setSelection(1);
            contact2EditText.setSelection(1);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        checkValidityOfFields(editable);

        // Enable or Disable the button, based on the validity of the fields
        enableOrDisableSaveButton();
    }

    private void checkValidityOfFields(Editable editable) {
        if (editable == usernameEditText.getEditableText()) {
            // Check if input is at least of length 4
            inputOfLengthFour(USERNAME_FIELD);

        } else if (editable == passwordEditText.getEditableText()) {
            // Check if input is at least of length 2
            inputOfLengthFour(PASSWORD_FIELD);

        } else if (editable == contact1EditText.getEditableText()) {
            // First check for existence of + symbol
            symbolExistence(1, editable);

            // Then check for input validity [must be at least 2 char (including country code)]
            inputOfLengthTwo(1);
        } else if (editable == contact2EditText.getEditableText()) {
            // First check for existence of + symbol
            symbolExistence(2, editable);

            // Then check for input validity [must be at least 2 char (including country code)]
            inputOfLengthTwo(2);
        }
    }

    private void inputOfLengthFour(String field) {
        if (field.equals(USERNAME_FIELD)) {
            if (usernameEditText.getText().length() < 4) {
                usernameTextInputLayout.setError(getString(R.string.username_text_input_layout_error_message));
                usernameIsValid = false;
            } else {
                usernameTextInputLayout.setErrorEnabled(false);
                usernameIsValid = true;
            }
        } else {
            if (passwordEditText.getText().length() < 4) {
                passwordTextInputLayout.setError(getString(R.string.password_text_input_layout_error_message));
                passIsValid = false;
            } else {
                passwordTextInputLayout.setErrorEnabled(false);
                passIsValid = true;
            }
        }
    }

    private void symbolExistence(int contactNo, Editable editable) {
        String temp;
        String newText;

        if (contactNo == 1) {
            if (!editable.toString().startsWith("+")) {
                temp = contact1EditText.getText().toString();
                newText = "+" + temp;
                contact1EditText.setText(newText);
            }
        } else {
            if (!editable.toString().startsWith("+")) {
                temp = contact2EditText.getText().toString();
                newText = "+" + temp;
                contact2EditText.setText(newText);
            }
        }
    }

    private void inputOfLengthTwo(int contactNo) {
        if (contactNo == 1) {
            if (contact1EditText.getText().length() < 2) {
                contact1TextInputLayout.setError(getString(R.string.contact_without_country_code_error_message));
                contact1IsValid = false;
            } else {
                contact1TextInputLayout.setErrorEnabled(false);
                contact1IsValid = true;
            }
        } else {
            if (contact2EditText.getText().length() < 2) {
                contact2TextInputLayout.setError(getString(R.string.contact_without_country_code_error_message));
                contact2IsValid = false;
            } else {
                contact2TextInputLayout.setErrorEnabled(false);
                contact2IsValid = true;
            }
        }
    }

    private void enableOrDisableSaveButton() {
        boolean enableButton = usernameIsValid && passIsValid
                && contact1IsValid && contact2IsValid;
        saveButton.setEnabled(enableButton);
        if (enableButton) saveButton.setAlpha(1f);
    }
}
