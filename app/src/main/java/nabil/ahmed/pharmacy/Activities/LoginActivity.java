package nabil.ahmed.pharmacy.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import nabil.ahmed.pharmacy.Helpers.StaticVariables;
import nabil.ahmed.pharmacy.MainActivity;
import nabil.ahmed.pharmacy.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button mSigninBtn;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mSendToRegister;
    private String uid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mSigninBtn = findViewById(R.id.login_signin_btn);
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        mSendToRegister = findViewById(R.id.login_send_to_register_btn);

//        if(StaticVariables.currentUserType.equals(StaticVariables.USER)){
//            mEmail.setHint("Email");
//        }

        mSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Email and password can't be blank",
                            Toast.LENGTH_SHORT).show();
                }

                else {
                    signInUser(email, password);
                }

            }
        });

        mSendToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToRegister();
            }
        });

    }

    private void signInUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login In Success.",
                                    Toast.LENGTH_SHORT).show();

                            uid = mAuth.getCurrentUser().getUid();

                            checkIfUserOrPharmacyThenAct();


                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkIfUserOrPharmacyThenAct() {
        db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        StaticVariables.currentUserType = StaticVariables.USER;
                        sendToUserSearch();
                        return;
                    }
                }
            }
        });

        db.collection("pharmacies").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        StaticVariables.currentUserType = StaticVariables.PHARMACY;
                        sendToMain();
                        return;
                    }
                }
            }
        });
    }

    private void sendToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToUserSearch() {
        Intent intent = new Intent(LoginActivity.this, UserSearchActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        //intent.putExtra(StaticVariables.USER_OR_PHARMACY, userOrPharmacy);
        startActivity(intent);
        finish();
    }
}
