package edu.gwu.androidtweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivityJava extends AppCompatActivity {

    private EditText username;

    private EditText password;

    private Button login;

    private ProgressBar progressBar;

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) { }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String inputtedUsername = username.getText().toString().trim();
            String inputtedPassword = password.getText().toString().trim();
            Boolean enableButton = inputtedUsername.length() > 0 && inputtedPassword.length() > 0;

            login.setEnabled(enableButton);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);

        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                Intent intent = new Intent(MainActivityJava.this, TweetsActivity.class);
                intent.putExtra("location", "Washington D.C.");
                startActivity(intent);
            }
        });
    }
}
