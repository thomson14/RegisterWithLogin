package com.example.registerwithlogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    EditText username , email, passowrd;
    Button signup;
    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressBar signUpProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);



        username = findViewById(R.id.username);
        passowrd = findViewById(R.id.password);
        email = findViewById(R.id.emailid);
        signup = findViewById(R.id.register);
        signUpProgress = findViewById(R.id.loginProgress);
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpProgress.setVisibility(View.VISIBLE);
                String txt_username = username.getText().toString();
                String txt_email =  email.getText().toString();
                String txt_password = passowrd.getText().toString();

                if(TextUtils.isEmpty(txt_username)|| TextUtils.isEmpty(txt_email)|| TextUtils.isEmpty(txt_password)  ){
                    Toast.makeText(SignUp.this,"All fields are required",Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.length()<6){
                    Toast.makeText(SignUp.this,"Password must be of atleast 6 characters",Toast.LENGTH_SHORT).show();
                }
                else {
                    register(txt_username,txt_email,txt_password);
                }
            }
        });


    }
    private void register(final String username, String email, String password){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            signUpProgress.setVisibility(View.INVISIBLE);
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userID   = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                            HashMap<String, String > hashMap = new HashMap<>();
                            hashMap.put("id",userID);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                       // Log.d("Intent","intent is not executed");
                                        Intent intent = new Intent(SignUp.this,Login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(SignUp.this,"You can't register with this email or password ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
