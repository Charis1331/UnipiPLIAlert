package com.example.xoulis.xaris.unipiplialert;

import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class Slide3 implements TextWatcher, View.OnClickListener {

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

    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";

    private AppCompatActivity activity;

    public Slide3(AppCompatActivity activity) {
        this.activity = activity;
    }

    void configureSlide3() {
        // Initialise the views
        initViews();

        // Add TextChangeListeners to the editTexts
        addTextChangeListeners();

        // Handle cursor movement in Contacts EditTexts
        handleCursorMovement();

        // Set default text for contacts editTexts
        setDefaultTextForContactsFields();

        // Add click listener to the saveButton
        saveButton.setOnClickListener(this);
    }

    private void initViews() {
        usernameTextInputLayout = activity.findViewById(R.id.textInputLayoutUserName);
        passwordTextInputLayout = activity.findViewById(R.id.textInputLayoutPassword);
        contact1TextInputLayout = activity.findViewById(R.id.textInputLayoutContact1);
        contact2TextInputLayout = activity.findViewById(R.id.textInputLayoutContact2);

        usernameEditText = activity.findViewById(R.id.usernameEditText);
        passwordEditText = activity.findViewById(R.id.passwordEditText);
        contact1EditText = activity.findViewById(R.id.contact1EditText);
        contact2EditText = activity.findViewById(R.id.contact2EditText);
        saveButton = activity.findViewById(R.id.saveButton);
    }

    private void addTextChangeListeners() {
        usernameEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        contact1EditText.addTextChangedListener(this);
        contact2EditText.addTextChangedListener(this);
    }

    private void handleCursorMovement() {
        contact1EditText.setOnClickListener(this);
        contact2EditText.setOnClickListener(this);
    }

    private void setDefaultTextForContactsFields() {
        String countryCallCode = GetCountryCodeNumber.getCallCode(activity);
        String defaultText = "+" + countryCallCode;
        contact1EditText.setText(defaultText);
        contact2EditText.setText(defaultText);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.contact1EditText) {
            if (contact1EditText.getSelectionStart() == 0) {
                contact1EditText.setSelection(1);
            }
        } else if (viewId == R.id.contact2EditText) {
            if (contact1EditText.getSelectionStart() == 0) {
                contact1EditText.setSelection(1);
            }
        } else if (viewId == R.id.saveButton) {
            // Don't display welcome intro again
            SettingsPreferences.setFirstTimeStart(activity);

            // Save input
            saveUserInfo();
        }
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
                usernameTextInputLayout.setError(activity.getString(R.string.username_text_input_layout_error_message));
                usernameIsValid = false;
            } else {
                usernameTextInputLayout.setErrorEnabled(false);
                usernameIsValid = true;
            }
        } else {
            if (passwordEditText.getText().length() < 4) {
                passwordTextInputLayout.setError(activity.getString(R.string.password_text_input_layout_error_message));
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
                contact1TextInputLayout.setError(activity.getString(R.string.contact_without_country_code_error_message));
                contact1IsValid = false;
            } else {
                contact1TextInputLayout.setErrorEnabled(false);
                contact1IsValid = true;
            }
        } else {
            if (contact2EditText.getText().length() < 2) {
                contact2TextInputLayout.setError(activity.getString(R.string.contact_without_country_code_error_message));
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

    private void saveUserInfo() {
        // Get all the inputs
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String contact1 = contact1EditText.getText().toString();
        String contact2 = contact2EditText.getText().toString();

        // Save them using SharedPreferences
        SettingsPreferences.setUsername(activity, username);
        SettingsPreferences.setPassword(activity, password);
        SettingsPreferences.setContact1(activity, contact1);
        SettingsPreferences.setContact2(activity, contact2);

        // Disable UI, while toast is being shown
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // Show successful save Toast
        Toast.makeText(activity, "Info successfully saved!", Toast.LENGTH_SHORT).show();

        // Exit Welcome Intro
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.finish();
                activity.overridePendingTransition(0, android.R.anim.fade_out);
            }
        }, 2000);
    }
}