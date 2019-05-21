package nabil.ahmed.pharmacy.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import nabil.ahmed.pharmacy.R;

public class StartActivity extends AppCompatActivity {

    private Button mEnterSearch;
    private Button mSendToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mEnterSearch = findViewById(R.id.start_enter_search);
        mSendToLogin = findViewById(R.id.start_send_to_login);

        mEnterSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToSearch();
            }
        });

        mSendToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLogin();
            }
        });


    }

    private void sendToLogin(){
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToSearch(){
        Intent intent = new Intent(StartActivity.this, UserSearchActivity.class);
        startActivity(intent);
        finish();
    }
}
