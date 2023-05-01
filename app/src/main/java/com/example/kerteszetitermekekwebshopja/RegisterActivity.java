package com.example.kerteszetitermekekwebshopja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;

    EditText userNameEditText;
    EditText userEmailEditText;
    EditText userPasswordText;
    EditText userPasswordAgainText;

    EditText phoneEditText;

    EditText addressEditText;
    RadioGroup accountTypeGroup;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        Bundle bundle = getIntent().getExtras();
//        int secret_key = bundle.getInt("SECRET_KEY");
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);

        if(secret_key != 99) {
            finish();
        }

        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        userPasswordText = findViewById(R.id.passwordEditText);
        userPasswordAgainText = findViewById(R.id.passwordAgainEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        accountTypeGroup = findViewById(R.id.accountType);
        accountTypeGroup.check(R.id.buyerRadioButton);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        userNameEditText.setText(userName);
        userPasswordText.setText(password);
        userPasswordAgainText.setText(password);

        mAuth = FirebaseAuth.getInstance();

    }

    public void register(View view) {
        String userName = userNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String password = userPasswordText.getText().toString();
        String passwordAgain = userPasswordAgainText.getText().toString();


        if(!password.equals(passwordAgain)) {
            Log.e(LOG_TAG, "Nem egyenlő a két jelszó!");
            return;
        }

        String phoneNumber = phoneEditText.getText().toString();
        String address = addressEditText.getText().toString();

        if( userName.trim().equals("") || email.trim().equals("") || password.trim().equals("") || passwordAgain.trim().equals("") || phoneNumber.trim().equals("") || address.trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "Nincs kitöltve valamelyik mező", Toast.LENGTH_LONG).show();
            return;
        }

        int checkedId = accountTypeGroup.getCheckedRadioButtonId();
        RadioButton radioButton = accountTypeGroup.findViewById(checkedId);
        String accountType = radioButton.getText().toString();

        Log.i(LOG_TAG, "*********************************");
        Log.i(LOG_TAG, "Regisztrált: " + userName);
        Log.i(LOG_TAG, "E-mail: "  + email);
        Log.i(LOG_TAG, "Jelszó: "  + password);
        Log.i(LOG_TAG, "Telefonszám: "  + phoneNumber);
        Log.i(LOG_TAG, "Jelszó: "  + password);
        Log.i(LOG_TAG, "Lakcím: " + address);
        Log.i(LOG_TAG, "Fiók: " + accountType);
        Log.i(LOG_TAG, "*********************************");

        //shopping();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "A Felhasználó sikeresen beregisztált");
                    shopping();
                } else {
                    Log.d(LOG_TAG, "Nem sikerült a regisztráció");
                    Toast.makeText(RegisterActivity.this, "Nem sikerült a regisztráció" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void shopping() {
        Intent intent = new Intent(this, ShopListActivity.class);
//        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }
}