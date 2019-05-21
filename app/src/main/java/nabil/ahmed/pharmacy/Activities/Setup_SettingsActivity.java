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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import nabil.ahmed.pharmacy.DatabaseModels.Pharmacy;
import nabil.ahmed.pharmacy.MainActivity;
import nabil.ahmed.pharmacy.R;

public class Setup_SettingsActivity extends AppCompatActivity {

    private TextInputLayout mPharmacyName;
    private TextInputLayout mPharmacyAddress;
    private Switch mDeliverySwitch;
    private Button mSaveBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mPharmacyName = findViewById(R.id.setup_pharmacy_name);
        mPharmacyAddress = findViewById(R.id.setup_pharmacy_address);
        mDeliverySwitch = findViewById(R.id.setup_has_delivery_switch);

        mSaveBtn = findViewById(R.id.setup_save_btn);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pharmacyName = mPharmacyName.getEditText().getText().toString();
                String pharmacyAddress = mPharmacyAddress.getEditText().getText().toString();
                boolean hasDeliveryService = mDeliverySwitch.isChecked();
                //GeoPoint pharmacyLocation =

                if(TextUtils.isEmpty(pharmacyName)){
                    Toast.makeText(Setup_SettingsActivity.this, "Please choose a name.", Toast.LENGTH_SHORT).show();
                }

                else{
                    Pharmacy pharmacy = new Pharmacy();
                    pharmacy.name = pharmacyName;
                    pharmacy.address = pharmacyAddress;
                    pharmacy.hasDeliveryService = hasDeliveryService;

                    addPharmacy(pharmacy);

                }

            }
        });

    }

    private void sendToMain() {
        Intent intent = new Intent(Setup_SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addPharmacy(Pharmacy pharmacy){
        db.collection("pharmacies").document(uid).set(pharmacy).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Setup_SettingsActivity.this, "Saved Successfully.",
                            Toast.LENGTH_SHORT).show();
                    sendToMain();
                }

                else {
                    Toast.makeText(Setup_SettingsActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
