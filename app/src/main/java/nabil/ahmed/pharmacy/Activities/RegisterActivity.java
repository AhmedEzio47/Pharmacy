package nabil.ahmed.pharmacy.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import nabil.ahmed.pharmacy.Helpers.StaticVariables;
import nabil.ahmed.pharmacy.R;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout mConfirmPassword;
    private Button mSignUpBtn;
    private Switch mRegisterAsPharmacySwitch;
    //private String userOrPharmacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //userOrPharmacy = getIntent().getStringExtra(StaticVariables.USER_OR_PHARMACY);

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.register_email);
        mPassword = findViewById(R.id.register_password);
        mConfirmPassword = findViewById(R.id.register_confirm_password);
        mSignUpBtn = findViewById(R.id.register_signup_btn);
        mRegisterAsPharmacySwitch = findViewById(R.id.register_as_pharmacy_switch);

//        if(StaticVariables.currentUserType.equals(StaticVariables.USER)){
//            mEmail.setHint("Email");
//        }

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                String confirmPassword = mConfirmPassword.getEditText().getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                        || TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Email and password can't be blank",
                            Toast.LENGTH_SHORT).show();
                }

                else if(!password.equals(confirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Passwords mismatch.",
                            Toast.LENGTH_SHORT).show();
                }

                else{
                    signUpUser(email, password);
                }
            }
        });

    }

    private void signUpUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Sign up success.",
                                    Toast.LENGTH_SHORT).show();
                            sendToSetup();

                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void sendToSetup() {

        Intent intent;

        if(mRegisterAsPharmacySwitch.isChecked()){
            StaticVariables.currentUserType = StaticVariables.PHARMACY;
            intent = new Intent(RegisterActivity.this, Setup_SettingsActivity.class);
        }
        else {
            StaticVariables.currentUserType = StaticVariables.USER;
            intent = new Intent(RegisterActivity.this, UserSetup_SettingsActivity.class);
        }

        startActivity(intent);
        finish();
    }

}
