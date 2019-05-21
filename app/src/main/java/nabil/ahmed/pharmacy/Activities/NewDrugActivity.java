package nabil.ahmed.pharmacy.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import nabil.ahmed.pharmacy.DatabaseModels.Drug;
import nabil.ahmed.pharmacy.MainActivity;
import nabil.ahmed.pharmacy.R;

public class NewDrugActivity extends AppCompatActivity {

    private TextInputLayout mDrugName;
    private TextInputLayout mDrugPrice;
    private TextInputLayout mDrugQuantity;
    private Button mSaveBtn;
    private TextView mWelcome;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_drug);

        final String drugId = getIntent().getStringExtra("drug_id");
        String edit = getIntent().getStringExtra("edit");

        mDrugName = findViewById(R.id.new_drug_name);
        mDrugPrice = findViewById(R.id.new_drug_price);
        mDrugQuantity = findViewById(R.id.new_drug_quantity);
        mSaveBtn = findViewById(R.id.new_drug_save_btn);
        mWelcome = findViewById(R.id.new_drug_welcome);

        if(edit != null){
            mWelcome.setText(edit);
            getDrugData(drugId);
        }


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name  = mDrugName.getEditText().getText().toString();
                String price = mDrugPrice.getEditText().getText().toString();
                String quantity = mDrugQuantity.getEditText().getText().toString();

                if(TextUtils.isEmpty(name) || TextUtils.isEmpty("" + price) || TextUtils.isEmpty("" + quantity)){
                    Toast.makeText(NewDrugActivity.this, "Please Fill fields above.", Toast.LENGTH_SHORT).show();
                }
                else{

                    Drug drug = new Drug();
                    drug.name = name;
                    drug.price = price;
                    drug.quantity = quantity;

                    addNewDrug(drugId, drug);
                }
            }
        });

    }

    private void addNewDrug(String drugId, Drug drug) {
        db.collection("pharmacies").document(uid).collection("drugs").document(drugId).set(drug)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(NewDrugActivity.this, "Added Successfully.", Toast.LENGTH_SHORT).show();
                    sendToMain();
                }
                else {
                    Toast.makeText(NewDrugActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDrugData(String drugId){
        db.collection("pharmacies").document(uid).collection("drugs").document(drugId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Drug drug = task.getResult().toObject(Drug.class);
                            mDrugName.getEditText().setText(drug.name);
                            mDrugPrice.getEditText().setText(drug.price);
                            mDrugQuantity.getEditText().setText(drug.quantity);
                        }
                    }
                });
    }

    private void sendToMain() {
        Intent intent = new Intent(NewDrugActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
