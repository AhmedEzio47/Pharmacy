package nabil.ahmed.photoblog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import nabil.ahmed.photoblog.DatabaseModels.User;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView mSetupImage;
    private EditText mSetupName;
    private Button mSaveBtn;
    private ProgressBar mSetupProgressBar;
    private Uri mImageUri;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseFirestore mFirebaseFirestore;
    private String mUserId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Settings");

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseFirestore = FirebaseFirestore.getInstance();

        mSetupImage = findViewById(R.id.setup_image);
        mSetupName = findViewById(R.id.setup_name);
        mSaveBtn = findViewById(R.id.setup_save_btn);
        mSetupProgressBar = findViewById(R.id.setup_progress);

        mSetupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }

                    else{
                        imagePicker();
                    }

                }

                else{
                    imagePicker();
                }
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mSetupName.getText().toString();

                if(TextUtils.isEmpty(name) || mImageUri == null){
                    Toast.makeText(SetupActivity.this, "Please pick a photo and a name", Toast.LENGTH_SHORT).show();
                }

                else{
                    mSetupProgressBar.setVisibility(View.VISIBLE);
                    final String uid = mAuth.getCurrentUser().getUid();
                    StorageReference image_path = mStorageRef.child("profile_images").child(uid + ".jpg");
                    image_path.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){

                                task.getResult().getMetadata().getReference()
                                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadUri = uri;
                                        User user = new User();
                                        user.name = name;
                                        user.image = downloadUri.toString();
                                        mFirebaseFirestore.collection("users").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    sendToMain();
                                                }
                                                else {
                                                    String error = task.getException().getMessag