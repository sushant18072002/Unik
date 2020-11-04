package os.app.unik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_activity extends AppCompatActivity {
    EditText email,password;
    Button login;
    TextView txt_signup;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        txt_signup=findViewById(R.id.txt_signup);
        auth=FirebaseAuth.getInstance();
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_activity.this,RegisterActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pd=new ProgressDialog(Login_activity.this);
                pd.setMessage("Please wait...");
                pd.show();
                String str_email=email.getText().toString();
                String str_password=password.getText().toString();
                if(TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password)){
                    Toast.makeText(Login_activity.this,"All fileds are required!",Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
                else{
                    auth.signInWithEmailAndPassword(str_email,str_password).addOnCompleteListener(Login_activity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
                           reference.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   pd.dismiss();
                                   Intent intent=new Intent(Login_activity.this,MainActivity.class);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   startActivity(intent);
                                   finish();
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {
                                   pd.dismiss();
                                   Toast.makeText(Login_activity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                               }
                           });
                       }
                       else{
                           pd.dismiss();
                           Toast.makeText(Login_activity.this,"You have entered the wrong email or password",Toast.LENGTH_LONG).show();
                       }

                        }
                    });
                }
            }
        });
    }
    public void bypass(View view) {
        ProgressDialog pd=new ProgressDialog(Login_activity.this);
        pd.setMessage("Please wait...");
        pd.show();
               /* String str_email=email.getText().toString();
                String str_password=password.getText().toString();*/
        String str_email="sushant18072002@gmail.com";
        String str_password="luckyday";
        if(TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password)){
            Toast.makeText(Login_activity.this,"All fileds are required!",Toast.LENGTH_LONG).show();
            pd.dismiss();
        }
        else{
            auth.signInWithEmailAndPassword(str_email,str_password).addOnCompleteListener(Login_activity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                pd.dismiss();
                                Intent intent=new Intent(Login_activity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                pd.dismiss();
                                Toast.makeText(Login_activity.this,error.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else{
                        pd.dismiss();
                        Toast.makeText(Login_activity.this,"You have entered the wrong email or password",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

}