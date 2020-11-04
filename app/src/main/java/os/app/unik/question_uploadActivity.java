package os.app.unik;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.HashMap;

import os.app.unik.Model.User;

import static java.security.AccessController.getContext;

public class question_uploadActivity extends AppCompatActivity {
    ImageView image_profile,question_image;
    ImageButton cancel;
    LinearLayout add_question_photo,answer_layout;
    RelativeLayout question_image_layout;
    TextView fullname;
    Uri mImageUri;
    EditText question;
    Button post;

    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_upload);

        MaterialToolbar toolbar=findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("Question post");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });



    image_profile=findViewById(R.id.image_profile);
    fullname=findViewById(R.id.fullname);
    question=findViewById(R.id.question);
    question_image=findViewById(R.id.question_image);
    add_question_photo=findViewById(R.id.add_question_photo);
    question_image_layout=findViewById(R.id.question_image_layout);
    answer_layout=findViewById(R.id.answer_layout);
    post=findViewById(R.id.question_button);
    cancel=findViewById(R.id.cancel);

    storageRef = FirebaseStorage.getInstance().getReference("questions");

    add_question_photo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CropImage.activity().start(question_uploadActivity.this);
        }
    });
    cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mImageUri = null;
            question_image.setImageResource(R.drawable.placeholder);
            question_image_layout.setVisibility(View.GONE);
        }
    });
    post.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            uploadquestion();
        }
    });
    answer_layout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(question_uploadActivity.this, "Answer is not ready yet", Toast.LENGTH_SHORT).show();
        }
    });
    userInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            question_image.setImageURI(mImageUri);
            question_image_layout.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfo(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext()==null){
                    return;
                }
                User user=snapshot.getValue(User.class);
                Glide.with(question_uploadActivity.this).load(user.getImageurl()).into(image_profile);
                fullname.setText(user.getFullname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadquestion(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting");
        pd.show();
        if (mImageUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progresspercent=(100.00 * snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    pd.setMessage("Percentage: "+(int)progresspercent+"%");
                }
            });
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions");
                        String postid = reference.push().getKey();
                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("postid", postid);
                        hashMap.put("postimage", miUrlOk);
                        hashMap.put("question", question.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        reference.child(postid).setValue(hashMap);
                        pd.dismiss();
                        finish();

                    } else {
                        Toast.makeText(question_uploadActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(question_uploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if(!question.getText().toString().equals("")){
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("questions");
                String postid = reference.push().getKey();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("postid", postid);
                hashMap.put("postimage","none");
                hashMap.put("question", question.getText().toString());
                hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(postid).setValue(hashMap);
                pd.dismiss();
                finish();
            }
            else
            Toast.makeText(question_uploadActivity.this, "Please enter the question", Toast.LENGTH_SHORT).show();
        }
    }

}
